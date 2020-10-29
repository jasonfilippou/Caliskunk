package com.company.rest.products.model;

import com.company.rest.products.util.exceptions.SquareServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.company.rest.products.util.Util.*;

/**
 * Square service class. Makes calls to {@link CatalogApi} and returns results to {@link BackendService} callers.
 *
 * @see BackendService
 * @see CatalogApi
 * @see CatalogItem
 * @see CatalogItemVariation
 */
@Slf4j
@Component
public class SquareService
{
	public static final String PRICE_MODEL = "FIXED_PRICING";
	public static final String CURRENCY = "USD";
	public static final String CODE_FOR_CATALOG_ITEMS = "ITEM";
	public static final String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";
	public static final Integer ABBRV_CHARS = 3;
	public static final Integer SECONDS_TO_WAIT = 10;
	public static final TimeUnit TIME_UNIT_USED = TimeUnit.SECONDS;
	public static final String DEFAULT_SQUARE_CATALOG_ITEM_TYPE = "REGULAR";

	private final CatalogWrapper catalogWrapper;

	/**
	 * Constructor takes an autowired {@link CatalogWrapper} instance  as a parameter.
	 */
	@Autowired
	public SquareService(final CatalogWrapper catalogWrapper)
	{
		this.catalogWrapper = catalogWrapper;
	}


	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity does <i>not</i> already exist in the repo by checking cached instances.
	 *
	 * @param request A {@link ProductPostRequestBody} instance containing details of the request.
	 * @throws SquareServiceException if any Exception is sent to us by Square.
	 */
	public SquareServiceResponseBody postProduct(@NonNull final ProductPostRequestBody request) throws SquareServiceException
	{

		//  Create a CatalogItem and a CatalogItemVariation registered to that item.
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it.

		final UpsertCatalogObjectResponse itemResponse;
		final UpsertCatalogObjectResponse itemVariationResponse;

		try
		{
			itemResponse = sendCatalogItemUpsertRequest(request);
			itemVariationResponse = sendCatalogItemVariationUpsertRequest(request, itemResponse.getCatalogObject().getId());
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::postProduct");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return combine(itemResponse, itemVariationResponse);
	}

	private SquareServiceResponseBody combine(@NonNull UpsertCatalogObjectResponse itemResponse,
	                                          @NonNull UpsertCatalogObjectResponse itemVariationResponse)
	{
		final CatalogObject itemObject = itemResponse.getCatalogObject();
		final CatalogObject itemVarObject = itemVariationResponse.getCatalogObject();
		final CatalogItem itemData = itemResponse.getCatalogObject().getItemData();
		final CatalogItemVariation itemVariationData = itemVariationResponse.getCatalogObject().getItemVariationData();

		return SquareServiceResponseBody.builder()

			                                // Pull some data from the CatalogObjects
			                                .squareItemId(itemObject.getId())
			                                .squareItemVariationId(itemVarObject.getId())
			                                .isDeleted(itemObject.getIsDeleted())
			                                .presentAtAllLocations(itemObject.getPresentAtAllLocations())
			                                .version(itemObject.getVersion())
			                                .updatedAt(itemObject.getUpdatedAt())

			                                // Now from the CatalogItem
			                                .name(itemData.getName())
			                                .availableElectronically(itemData.getAvailableElectronically())
			                                .availableForPickup(itemData.getAvailableForPickup())
			                                .availableOnline(itemData.getAvailableOnline())
			                                .description(itemData.getDescription())
			                                .labelColor(itemData.getLabelColor())
		                                    .taxIDs(itemData.getTaxIds())

			                                // And from the CatalogItemVariation.
			                                .costInCents(itemVariationData.getPriceMoney().getAmount())
		                                    .sku(itemVariationData.getSku())
		                                    .upc(itemVariationData.getUpc())

		                                .build();
	}

	/* ****************************** CatalogItem upsert request helpers  ****************************** */

	private  UpsertCatalogObjectResponse sendCatalogItemUpsertRequest(@NonNull final ProductPostRequestBody request)
																		throws  ExecutionException, InterruptedException
	{
		final UpsertCatalogObjectRequest catalogItemUpsertRequest =  createCatalogItemUpsertRequest(request);
		final UpsertCatalogObjectResponse response = catalogWrapper.upsertObject(catalogItemUpsertRequest);
		log.info("New CatalogItem created on Square, with ID " + response.getCatalogObject().getId() + ".");
		return response;
	}


	private  UpsertCatalogObjectRequest createCatalogItemUpsertRequest(@NonNull final ProductPostRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForCatalogItemUpsertRequest(request));
	}

	private  CatalogObject createObjectFieldForCatalogItemUpsertRequest(@NonNull final ProductPostRequestBody request)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, request.getClientProductId())
									.itemData(createCatalogItem(request))
								.build();
	}

	private CatalogItem createCatalogItem(@NonNull final ProductPostRequestBody request)
	{
		return new CatalogItem
				.Builder()
					.name(request.getName())
					.abbreviation(abbreviate(request.getName(), ABBRV_CHARS))
					.productType(DEFAULT_SQUARE_CATALOG_ITEM_TYPE)
					.description(request.getDescription())
					.labelColor(request.getLabelColor())
					.availableElectronically(request.getAvailableElectronically())
					.availableForPickup(request.getAvailableForPickup())
					.availableOnline(request.getAvailableOnline())
				.build();
	}



	/* ************************* CatalogItemVariation upsert request helpers  ****************************** */

	private  UpsertCatalogObjectResponse sendCatalogItemVariationUpsertRequest(final ProductPostRequestBody request, String id)
															throws ExecutionException, InterruptedException
	{
		final UpsertCatalogObjectRequest catalogItemVariationUpsertRequest = createCatalogItemVariationUpsertRequest(request, id);
		final UpsertCatalogObjectResponse response = catalogWrapper.upsertObject(catalogItemVariationUpsertRequest);
		log.info("New CatalogItemVariation created on Square, with ID " + response.getCatalogObject().getId() + ".");
		return response;
	}

	private  UpsertCatalogObjectRequest createCatalogItemVariationUpsertRequest(final ProductPostRequestBody request, String id)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForItemVariationUpsertRequest(request, id));
	}

	private  CatalogObject createObjectFieldForItemVariationUpsertRequest(final ProductPostRequestBody request, String id)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS, "#RANDOM_ITEM_VAR_ID")
					.itemVariationData(createCatalogItemVariation(request, id))
				.build();
	}

	private  CatalogItemVariation createCatalogItemVariation(final ProductPostRequestBody request, String id)
	{
		return new CatalogItemVariation
				.Builder()
					.itemId(id)
					.name(request.getName())
					.sku(request.getSku())
					.upc(request.getUpc())
					.priceMoney(new Money(request.getCostInCents(), CURRENCY))
					.pricingType(PRICE_MODEL)
				.build();
	}

	/**
	 * Send a GET request for a specific product.
	 * @param squareItemId The relevant {@link CatalogItem}'s unique ID on Square.
	 * @param itemVarId The relevant {@link CatalogItemVariation}'s unique ID on Square.
	 * @return A {@link ProductPostRequestBody} instance with the entire client-facing product data.
	 * @throws SquareServiceException if Square sends an Exception.
	 */
	public SquareServiceResponseBody getProduct(@NonNull final String squareItemId, @NonNull final String itemVarId)
																				throws SquareServiceException
	{
		final Boolean INCLUDE_RELATED_OBJECTS = true;     // We will make use of the additional info returned later.
		final BatchRetrieveCatalogObjectsRequest request =
					new BatchRetrieveCatalogObjectsRequest
						.Builder(Arrays.asList(squareItemId, itemVarId))
							.includeRelatedObjects(INCLUDE_RELATED_OBJECTS)
						.build();
		try
		{
			final BatchRetrieveCatalogObjectsResponse response = catalogWrapper.batchRetrieveObjects(request);
			validateBatchRetrievalResponse(response);
			final CatalogObject[] itemAndVar = fetchItemAndVar(response.getObjects());
			return SquareServiceResponseBody.fromSquareData(itemAndVar[0], itemAndVar[1]);
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getEnclosingMethod().getName());
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private  void validateBatchRetrievalResponse(final BatchRetrieveCatalogObjectsResponse response)
	{
		//  TODO: see if you can instead look for non-null pointers for object.getItemData()
		//      or object.getItemVariationData(). It's a bit more efficient than comparing strings.
		assertAndIfNotLogAndThrow(response.getObjects().size() == 2 &&
		                          (response.getObjects()
	                                   .stream()
	                                   .allMatch(catalogObject ->
		                                             catalogObject.getType().equals(CODE_FOR_CATALOG_ITEMS)
                                                                ||
		                                             catalogObject.getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS)
	                                            )
		                          ),"Bad batch retrieval response received: " + response);
	}

//
	private CatalogObject[] fetchItemAndVar(final List<CatalogObject> objects)
	{
		final CatalogObject[] retVal = new CatalogObject[2];
		final String badDataMsg =  "Bad CatalogObject data retrieved: " + objects;
		if(objects.get(0).getType().equals(CODE_FOR_CATALOG_ITEMS))
		{
			assertAndIfNotLogAndThrow(objects.get(1).getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS), badDataMsg);
			retVal[0] = objects.get(0);
			retVal[1] = objects.get(1);
		}
		else if(objects.get(0).getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS))
		{
			assertAndIfNotLogAndThrow(objects.get(1).getType().equals(CODE_FOR_CATALOG_ITEMS), badDataMsg) ;
			retVal[0] = objects.get(1);
			retVal[1] = objects.get(0);
		}
		else
		{
			assertAndIfNotLogAndThrow(false, badDataMsg);
		}
		return retVal;
	}


	/**
	 * Send a PUT request for a specific product.
	 * @param id The product's unique id, provided by the request.
	 * @param request The request body.
	 * @throws SquareServiceException if Square sends an Exception.
	 */
	public SquareServiceResponseBody putProduct(String id, ProductPostRequestBody request) throws SquareServiceException
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param id The product's unique id.
	 * @throws SquareServiceException if Square sends an Exception.
	 */
	public SquareServiceResponseBody patchProduct(String id, ProductPostRequestBody request) throws SquareServiceException
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param id The product's unique id.
	 * @throws SquareServiceException if Square sends an Exception.
	 */
	public SquareServiceResponseBody deleteProduct(String id) throws SquareServiceException
	{
		throw new UnimplementedMethodPlaceholder();
	}
}
