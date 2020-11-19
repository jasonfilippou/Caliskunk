package com.company.rest.products.model;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.exceptions.SquareServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
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

import static com.company.rest.products.util.Util.*;

/**
 * Square service class. Uses {@link CatalogWrapper}'s calls to interact with Square API and returns response
 * to {@link BackendService}.
 *
 * @see BackendService
 * @see CatalogWrapper
 * @see SquareServiceException
 * @see SquareServiceResponseBody
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
	public static final String DEFAULT_SQUARE_CATALOG_ITEM_TYPE = "REGULAR";

	private final CatalogWrapper catalogWrapper;

	/**
	 * Constructor takes an autowired {@link CatalogWrapper} instance  as a parameter.
	 * @param catalogWrapper A - usually {@code @Autowired} - {@link CatalogWrapper} instance.
	 */
	@Autowired
	public SquareService(@NonNull final CatalogWrapper catalogWrapper)
	{
		this.catalogWrapper = catalogWrapper;
	}


	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity does <i>not</i> already exist in the repo by checking cached instances.
	 *
	 * @param request A {@link ProductUpsertRequestBody} instance containing details of the request.
	 *
	 * @throws SquareServiceException if any Exception is sent to us by Square.
	 * @return A serialized response containing the information of this layer.
	 */
	public SquareServiceResponseBody upsertProduct(@NonNull final ProductUpsertRequestBody request, @NonNull String id)
															throws SquareServiceException
	{
		//  Create a CatalogItem and a CatalogItemVariation registered to that item.
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it.
		final UpsertCatalogObjectResponse itemResponse;
		final UpsertCatalogObjectResponse itemVariationResponse;
		try
		{
			itemResponse = sendCatalogItemUpsertRequest(request, id);
			itemVariationResponse = sendCatalogItemVariationUpsertRequest(request, itemResponse.getCatalogObject().getId());
		}
		catch (InterruptedException | ExecutionException e)
		{
			logException(e, this.getClass().getName() + "::postProduct");
            throw new SquareServiceException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return combineResponses(itemResponse, itemVariationResponse, id, request);
	}

	/**
	 * Send a GET request for a specific product.
	 * @throws SquareServiceException if Square sends an Exception.
	 * @see BackendService#getProduct(ProductGetRequestBody)
	 * @see CatalogWrapper#retrieveObject(BatchRetrieveCatalogObjectsRequest)
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 */
	public SquareServiceResponseBody getProduct(@NonNull final LiteProduct cachedProduct) throws SquareServiceException
	{
		final Boolean INCLUDE_RELATED_OBJECTS = true;     // We will make use of the additional info returned later.
		final BatchRetrieveCatalogObjectsRequest request =
					new BatchRetrieveCatalogObjectsRequest.Builder(Arrays.asList(cachedProduct.getSquareItemId(), cachedProduct.getSquareItemVariationId()))
															.includeRelatedObjects(INCLUDE_RELATED_OBJECTS)
														   .build();
		try
		{
			final BatchRetrieveCatalogObjectsResponse response = catalogWrapper.retrieveObject(request);
			validateBatchRetrievalResponse(response);
			final CatalogObject[] itemAndVar = fetchItemAndVar(response.getObjects());
			return SquareServiceResponseBody.fromSquareData(itemAndVar[0], itemAndVar[1], cachedProduct);
		}
		catch (Throwable t)
		{
			logException(t, "::getProduct()");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateBatchRetrievalResponse(final BatchRetrieveCatalogObjectsResponse response)
	{

		assertAndIfNotLogAndThrow(response.getObjects().size() == 2 &&
		                          ((response.getErrors() == null) || response.getErrors().isEmpty()) &&
		                          (response.getObjects() != null) &&
		                          (response.getObjects()
	                                   .stream()
	                                   .allMatch(catalogObject ->
		                                             catalogObject.getType().equals(CODE_FOR_CATALOG_ITEMS)
                                                                ||
		                                             catalogObject.getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS)
	                                            )
		                          ),"Bad batch retrieval response received: " + response);
	}

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
	 * Send a PATCH request for a specific product.
	 * @param id The product's unique id.
	 * @param request The JSON request that contains the information we want to patch the product with.
	 * @throws SquareServiceException if Square sends an Exception.
	 * @see CatalogWrapper#upsertObject(UpsertCatalogObjectRequest)
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 */
	public SquareServiceResponseBody patchProduct(@NonNull final ProductUpsertRequestBody request, @NonNull final String id) throws SquareServiceException
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param cachedProduct A {@link LiteProduct} instance that contains information that we expect this layer to return to {@link BackendService} (and is useful for our tests, too)
	 * @throws SquareServiceException if Square sends an Exception of some kind.
	 * @see BackendService#deleteProduct(String)
	 * @see CatalogWrapper#deleteObject(String)
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 */
	public SquareServiceResponseBody deleteProduct(@NonNull final LiteProduct cachedProduct) throws SquareServiceException
	{
		// Just as a reminder, deletions on Square are cascading, so that deletion of a CatalogItem
		// will also yield the deletion of all of its CatalogItemVariations. Therefore, we don't need information
		// about the variation ID as an arg.
		try
		{
			final DeleteCatalogObjectResponse response = catalogWrapper.deleteObject(cachedProduct.getSquareItemId());
			validateDeletionResponse(response, cachedProduct.getSquareItemId());
			return SquareServiceResponseBody.builder()
			                                    .clientProductId(cachedProduct.getClientProductId())
			                                    .name(cachedProduct.getProductName())
			                                    .productType(cachedProduct.getProductType())
			                                    .costInCents(cachedProduct.getCostInCents())
			                                    .squareItemId(cachedProduct.getSquareItemId())
			                                    .squareItemVariationId(cachedProduct.getSquareItemVariationId())
			                                    .isDeleted(true)
			                                    .availableElectronically(false)
			                                    .availableForPickup(false)
			                                    .availableOnline(false)
			                                    .presentAtAllLocations(false)
			                                    .updatedAt(response.getDeletedAt())
			                                .build();
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::deleteProduct");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateDeletionResponse(final DeleteCatalogObjectResponse response, final String expectedDeletedItemId)
	{
		assert (response.getDeletedObjectIds() != null && response.getDeletedObjectIds().size() == 2)  && response.getDeletedObjectIds().contains(expectedDeletedItemId) // Appropriate response
													       &&
               ((response.getErrors() == null) || (response.getErrors().size() == 0)); // No errors
	}

	/* ************************************************************************************************* */
	/* ****************************** CatalogItem upsert request helpers  ****************************** */
	/* ************************************************************************************************* */

	private  UpsertCatalogObjectResponse sendCatalogItemUpsertRequest(final ProductUpsertRequestBody request, final String id)
																		throws  ExecutionException, InterruptedException
	{
		final UpsertCatalogObjectRequest catalogItemUpsertRequest = createCatalogItemUpsertRequest(request, id);
		final UpsertCatalogObjectResponse response = catalogWrapper.upsertObject(catalogItemUpsertRequest);
		log.info("New CatalogItem created on Square, with ID " + response.getCatalogObject().getId());
		return response;
	}


	private  UpsertCatalogObjectRequest createCatalogItemUpsertRequest(final ProductUpsertRequestBody request, final String id)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForCatalogItemUpsertRequest(request, id));
	}

	private  CatalogObject createObjectFieldForCatalogItemUpsertRequest(final ProductUpsertRequestBody request, final String id)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, ensureFirstCharIs(id, '#'))
									.itemData(createCatalogItem(request))
								.build();
	}

	private CatalogItem createCatalogItem(final ProductUpsertRequestBody request)
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

	/* ************************************************************************************************* */
	/* ************************* CatalogItemVariation upsert request helpers  ************************** */
	/* ************************************************************************************************* */

	private  UpsertCatalogObjectResponse sendCatalogItemVariationUpsertRequest(final ProductUpsertRequestBody request, final String id)
															throws ExecutionException, InterruptedException
	{
		final UpsertCatalogObjectRequest catalogItemVariationUpsertRequest = createCatalogItemVariationUpsertRequest(request, id);
		final UpsertCatalogObjectResponse response = catalogWrapper.upsertObject(catalogItemVariationUpsertRequest);
		log.info("New CatalogItemVariation created on Square, with ID " + response.getCatalogObject().getId());
		return response;
	}

	private  UpsertCatalogObjectRequest createCatalogItemVariationUpsertRequest(final ProductUpsertRequestBody request, final String id)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForItemVariationUpsertRequest(request, id));
	}

	private  CatalogObject createObjectFieldForItemVariationUpsertRequest(final ProductUpsertRequestBody request, final String id)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS, "#RANDOM_ITEM_VAR_ID")
					.itemVariationData(createCatalogItemVariation(request, id))
				.build();
	}

	private  CatalogItemVariation createCatalogItemVariation(final ProductUpsertRequestBody request, final String id)
	{
		return new CatalogItemVariation
				.Builder()
					.itemId(id)
					.name(request.getName())
					.sku(request.getSku())
					.upc(request.getUpc())
					.priceMoney(moneyOrNothing(request))
					.pricingType(PRICE_MODEL)
				.build();
	}

	private Money moneyOrNothing(final ProductUpsertRequestBody request)
	{
		return ( request.getCostInCents() != null ) ?  new Money(request.getCostInCents(), CURRENCY) : null;
	}

	/* **************************************************************************************** */
	/* ********************************** Combine function ************************************ */
	/* **************************************************************************************** */

	private SquareServiceResponseBody combineResponses(final UpsertCatalogObjectResponse itemResponse,
	                                                   final UpsertCatalogObjectResponse itemVariationResponse,
	                                                   final String clientProductId,
	                                                   final ProductUpsertRequestBody requestBody)
	{
		final CatalogObject itemObject = itemResponse.getCatalogObject();
		final CatalogObject itemVarObject = itemVariationResponse.getCatalogObject();
		final CatalogItem itemData = itemResponse.getCatalogObject().getItemData();
		final CatalogItemVariation itemVariationData = itemVariationResponse.getCatalogObject().getItemVariationData();

		return SquareServiceResponseBody.builder()

		                                    // Basic data
		                                    .clientProductId(clientProductId)
											.productType(requestBody.getProductType())

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
}
