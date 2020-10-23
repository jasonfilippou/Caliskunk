package com.company.rest.products.model;

import com.company.rest.products.util.exceptions.SquareServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.SquareServiceResponseBody;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
	public final static String PRICE_MODEL = "FIXED_PRICING";
	public final static String CURRENCY = "USD";
	public final static String CODE_FOR_CATALOG_ITEMS = "ITEM";
	public final static String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";
	public final static Integer ABBRV_CHARS = 3;
	public final static Integer SECONDS_TO_WAIT = 10;
	public final static TimeUnit TIME_UNIT_USED = TimeUnit.SECONDS;

	// Necessary objects to connect to API
	private static final SquareClient client = new SquareClient.Builder()
			.environment(Environment.SANDBOX)
			.accessToken("123abe")
			.build();
	private static final CatalogApi catalogApi = client.getCatalogApi();


	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity does <i>not</i> already exist in the repo by checking cached instances.
	 *
	 * @param request A {@link ProductPostRequestBody} instance containing details of the request.
	 * @throws SquareServiceException if any Exception is sent to us by Square.
	 */
	public SquareServiceResponseBody postProduct(ProductPostRequestBody request) throws SquareServiceException
	{

		//  Create a CatalogItem and a CatalogItemVariation registered to that item.
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it.

		final UpsertCatalogObjectResponse itemResponse;
		final UpsertCatalogObjectResponse itemVariationResponse;

		try
		{
			itemResponse = sendCatalogItemUpsertRequest(request);
			itemVariationResponse = sendCatalogItemVariationUpsertRequest(request);
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getEnclosingMethod().getName());
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return combine(itemResponse, itemVariationResponse);
	}

	private SquareServiceResponseBody combine(UpsertCatalogObjectResponse itemResponse,
	                                          UpsertCatalogObjectResponse itemVariationResponse)
	{
		final CatalogObject itemObject = itemResponse.getCatalogObject();
		final CatalogObject itemVarObject = itemVariationResponse.getCatalogObject();
		final CatalogItem itemData = itemResponse.getCatalogObject().getItemData();
		final CatalogItemVariation itemVariationData = itemVariationResponse.getCatalogObject().getItemVariationData();

		return SquareServiceResponseBody.builder()
			                                // Pull some data from the CatalogObjects
			                                .itemId(itemObject.getId())
			                                .itemVariationId(itemVarObject.getId())
			                                .isDeleted(itemObject.getIsDeleted())
			                                .presentAtAllLocations(itemObject.getPresentAtAllLocations())
			                                .version(itemObject.getVersion())
			                                .updatedAt(itemObject.getUpdatedAt())

			                                // Now from the CatalogItem
			                                .name(itemData.getName())
			                                .availableElectronically(itemData.getAvailableElectronically())
			                                .availableForPickup(itemData.getAvailableForPickup())
			                                .availableOnline(itemData.getAvailableOnline())
			                                .categoryId(itemData.getCategoryId())
			                                .description(itemData.getDescription())
			                                .labelColor(itemData.getLabelColor())
			                                .productType(itemData.getProductType())

			                                // And from the CatalogItemVariation data.
			                                .costInCents(itemVariationData.getPriceMoney().getAmount())
		                                .build();
	}

	/* CatalogItem upsert request helpers */

	// To avoid the warning about changing the lambda to a method reference.
	private  UpsertCatalogObjectResponse sendCatalogItemUpsertRequest(ProductPostRequestBody request)
													throws InterruptedException, ExecutionException, TimeoutException
	{
		final UpsertCatalogObjectRequest catalogItemUpsertRequest =  createCatalogItemUpsertRequest(request);
		final UpsertCatalogObjectResponse response;
		response = catalogApi.upsertCatalogObjectAsync(catalogItemUpsertRequest).get(SECONDS_TO_WAIT, TIME_UNIT_USED);
		log.info("New CatalogItem created on Square, with ID " + response.getCatalogObject().getId() + ".");
		return response;
	}


	private  UpsertCatalogObjectRequest createCatalogItemUpsertRequest(ProductPostRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForCatalogItemUpsertRequest(request));
	}

	private  CatalogObject createObjectFieldForCatalogItemUpsertRequest(ProductPostRequestBody request)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, request.getName())
									.itemData(createCatalogItem(request))
								.build();
	}

	private CatalogItem createCatalogItem(ProductPostRequestBody request)
	{
		return new CatalogItem
				.Builder()
					.name(request.getName())
					.abbreviation(abbreviate(request.getName(), ABBRV_CHARS))
					.categoryId(request.getCategoryId())
					.productType(request.getProductType())
					.description(request.getDescription())
					.labelColor(request.getLabelColor())
					.availableElectronically(request.getAvailableElectronically())
					.availableForPickup(request.getAvailableForPickup())
					.availableOnline(request.getAvailableOnline())
				.build();
	}



	/*  CatalogItemVariation upsert request helpers */

	private  UpsertCatalogObjectResponse sendCatalogItemVariationUpsertRequest(ProductPostRequestBody request)
											throws InterruptedException, ExecutionException, TimeoutException
	{
		final UpsertCatalogObjectRequest catalogItemVariationUpsertRequest =
				createCatalogItemVariationUpsertRequest(request);
		return catalogApi.upsertCatalogObjectAsync(catalogItemVariationUpsertRequest)
		                                                 .get(SECONDS_TO_WAIT, TIME_UNIT_USED);
	}

	private  UpsertCatalogObjectRequest createCatalogItemVariationUpsertRequest(ProductPostRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForItemVariationUpsertRequest(request));
	}

	private  CatalogObject createObjectFieldForItemVariationUpsertRequest(ProductPostRequestBody request)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS,
		                  request.getName())
					.itemVariationData(createCatalogItemVariation(request))
				.build();
	}

	private  CatalogItemVariation createCatalogItemVariation(ProductPostRequestBody request)
	{
		return new CatalogItemVariation
				.Builder()
					.itemId(request.getName())
					.name(request.getName())
					.sku(request.getSku())
					.upc(request.getUpc())
					.priceMoney(new Money(request.getCostInCents(), CURRENCY))
					.pricingType(PRICE_MODEL)
				.build();
	}

	/**
	 * Send a GET request for a specific product.
	 * @param itemId The relevant {@link CatalogItem}'s unique ID on Square.
	 * @param itemVarId The relevant {@link CatalogItemVariation}'s unique ID on Square.
	 * @return A {@link ProductPostRequestBody} instance with the entire client-facing product data.
	 * @throws SquareServiceException if Square sends an Exception.
	 */
	public SquareServiceResponseBody getProduct(String itemId, String itemVarId) throws SquareServiceException
	{
		final Boolean INCLUDE_RELATED_OBJECTS = true;     // We will make use of the additional info returned later.
		final BatchRetrieveCatalogObjectsRequest request =
					new BatchRetrieveCatalogObjectsRequest
						.Builder(Arrays.asList(itemId, itemVarId))
							.includeRelatedObjects(INCLUDE_RELATED_OBJECTS)
						.build();
		try
		{
			final BatchRetrieveCatalogObjectsResponse response =
					catalogApi.batchRetrieveCatalogObjectsAsync(request).get(SECONDS_TO_WAIT, TIME_UNIT_USED);
			validateBatchRetrievalResponse(response);
			CatalogObject[] itemAndVar = fetchItemAndVar(response.getObjects());
			return SquareServiceResponseBody.fromSquareData(itemAndVar[0], itemAndVar[1]);
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getEnclosingMethod().getName());
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private  void validateBatchRetrievalResponse(BatchRetrieveCatalogObjectsResponse response)
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
		                                            ) //</allMatch>
		                          ), //</response.getObjects()...>
                    "Bad batch retrieval response received: " + response);
	}

//
	private CatalogObject[] fetchItemAndVar(List<CatalogObject> objects)
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
