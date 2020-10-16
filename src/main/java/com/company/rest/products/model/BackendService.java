package com.company.rest.products.model;

import com.company.rest.products.controller.ProductRequestBody;
import com.company.rest.products.controller.ProductResponseBody;
import com.company.rest.products.model.exceptions.ResourceAlreadyCreatedException;
import com.company.rest.products.util.UnimplementedMethodPlaceholder;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.company.rest.products.util.Util.*;

/**
 * Service class. Builds unique ID of entity by combining given name and some pre-defined strings.
 *
 * @see CatalogItem
 * @see CatalogItemVariation
 */
@Slf4j
@Component
public class BackendService
{

	// Default values for several CatalogObject attributes:
	private final static String PRICE_MODEL = "FIXED_PRICING";
	private final static String CURRENCY = "USD";
	private final static String CODE_FOR_CATALOG_ITEMS = "ITEM";
	private final static String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";
	private final static String DEFAULT_CATALOG_ITEM_SUFFIX = "_PROD";
	private final static String DEFAULT_CATALOG_ITEM_VARIATION_SUFFIX = "_VAR";
	private final static int ABBRV_CHARS = 3;

	// Necessary objects to connect to API
	private static final SquareClient client = new SquareClient.Builder()
			.environment(Environment.SANDBOX)
			.accessToken(System.getenv("SQUARE_SANDBOX_ACCESS_TOKEN"))
			.build();
	private static final CatalogApi catalogApi = client.getCatalogApi();


	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity does <i>not</i> already exist in the repo by checking cached instances.
	 *
	 * @param request A {@link ProductRequestBody} instance containing details of the request.
	 * @throws ResourceAlreadyCreatedException if the resource already exists.
	 */
	public  void postProduct(ProductRequestBody request)
	{
		//  Create a CatalogItem and a CatalogItemVariation registered to that item.
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it..
		sendCatalogItemUpsertRequest(request);
		sendCatalogItemVariationUpsertRequest(request);
	}

	/* CatalogItem upsert request helpers */

	@SuppressWarnings("all") // To avoid the warning about changing the lambda to a method reference.
	private  void sendCatalogItemUpsertRequest(ProductRequestBody request)
	{
		final UpsertCatalogObjectRequest catalogItemUpsertRequest =
				createCatalogItemUpsertRequest(request);
		catalogApi.upsertCatalogObjectAsync(catalogItemUpsertRequest)
					.thenAccept(result-> log.info("New CatalogItem created on Square, with ID "
					                              + result.getCatalogObject().getId() + "."))
					.exceptionally(exception ->
			         {
                    	logException(exception);
						throw new RuntimeException(exception.getMessage());
			         });
	}


	private  UpsertCatalogObjectRequest createCatalogItemUpsertRequest(ProductRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForCatalogItemUpsertRequest(request));
	}

	private  CatalogObject createObjectFieldForCatalogItemUpsertRequest(ProductRequestBody request)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEMS, request.getName() + DEFAULT_CATALOG_ITEM_SUFFIX)
				.itemData(createCatalogItem(request))
				.build();
	}

	private  CatalogItem createCatalogItem(ProductRequestBody request)
	{
		return new CatalogItem
				.Builder()
					.name(request.getName() + DEFAULT_CATALOG_ITEM_SUFFIX)
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

	private  void sendCatalogItemVariationUpsertRequest(ProductRequestBody request)
	{
		final UpsertCatalogObjectRequest catalogItemVariationUpsertRequest = createCatalogItemVariationUpsertRequest(request);

		catalogApi.upsertCatalogObjectAsync(catalogItemVariationUpsertRequest)
					.thenAccept(response-> log.info("New CatalogItemVariation created on Square, " +
					                              "with name " + request.getName() + "."))
					.exceptionally(exception ->
					         {
					         	logException(exception);
								throw new RuntimeException(exception.getMessage());
					         });
	}

	private  UpsertCatalogObjectRequest createCatalogItemVariationUpsertRequest(ProductRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForItemVariationUpsertRequest(request));
	}

	private  CatalogObject createObjectFieldForItemVariationUpsertRequest(ProductRequestBody request)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS,
		                  request.getName() + DEFAULT_CATALOG_ITEM_VARIATION_SUFFIX)
					.itemVariationData(createCatalogItemVariation(request))
				.build();
	}

	private  CatalogItemVariation createCatalogItemVariation(ProductRequestBody request)
	{
		return new CatalogItemVariation
				.Builder()
					.itemId(request.getName())
					.name(request.getName() + DEFAULT_CATALOG_ITEM_VARIATION_SUFFIX)
					.sku(request.getSku())
					.upc(request.getUpc())
					.priceMoney(new Money(request.getCostInCents(), CURRENCY))
					.pricingType(PRICE_MODEL)
				.build();
	}

	/**
	 * Send a GET request for a specific product.
	 * @param name The product's name.
	 * @return A {@link ProductRequestBody} instance with the entire client-facing product data.
	 */
	public  ProductResponseBody getProduct(String name) throws InterruptedException, ExecutionException
	{
		final Boolean includeRelatedObjects = false;
		final String itemId = name + DEFAULT_CATALOG_ITEM_SUFFIX,
					 itemVarId = name + DEFAULT_CATALOG_ITEM_VARIATION_SUFFIX;
		final BatchRetrieveCatalogObjectsRequest request =
					new BatchRetrieveCatalogObjectsRequest
						.Builder(Arrays.asList(itemId, itemVarId))
						.includeRelatedObjects(includeRelatedObjects)
						.build();
		try
		{
			BatchRetrieveCatalogObjectsResponse response =
					catalogApi.batchRetrieveCatalogObjectsAsync(request).get();
			validateBatchRetrievalResponse(response);
			return buildResponseBodyFromSquareData(response);
		}
		catch (InterruptedException | ExecutionException e)
		{
			logException(e);
			throw(e);
		}
	}

	// A valid batch response contains exactly one CatalogItem and one CatalogItemVariation.
	private  void validateBatchRetrievalResponse(BatchRetrieveCatalogObjectsResponse response)
	{
		//  TODO: see if you can instead look for non-null pointers for object.getItemData()
		//      or object.getItemVariationData(). It's a bit more efficient than comparing strings.
		assertAndIfNotLogAndThrow(response.getObjects().size() == 2 &&
		                          (response.getObjects().stream()
					             .allMatch(obj-> ( obj.getType().equals(CODE_FOR_CATALOG_ITEMS)
						                          ||
						                          (obj.getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS))
					                            )
					                      )
	                        ), "Bad batch retrieval response received: " + response);
	}

	private  ProductResponseBody buildResponseBodyFromSquareData(BatchRetrieveCatalogObjectsResponse response)
	{
		// First position will be the CatalogItem, second the registered CatalogItemVariation.
		final CatalogObject[] retrievedObjects = parseObjects(response.getObjects());
		return buildResponseBodyFromSquareData(retrievedObjects);
	}

	private  CatalogObject[] parseObjects(List<CatalogObject> retrievedObjects)
	{
		final CatalogObject[] retVal = new CatalogObject[2];
		for(CatalogObject object : retrievedObjects)
		{
			// TODO: see if you can instead look for non-null pointers for object.getItemData()
			//  or object.getItemVariationData(). It's a bit more efficient than comparing strings.
			if(object.getType().equals(CODE_FOR_CATALOG_ITEMS))
			{
				retVal[0] = object;
			}
			else    // Only one option in our application: CODE_FOR_CATALOG_ITEM_VARIATIONS
			{
				retVal[1] = object;
			}
		}
		return retVal;
	}

	@SuppressWarnings("all") // For some `getName()` calls that IntelliJ *thinks* can produce NPEs (I cover them in
							 // the assertAndThrow() method.
	private  ProductResponseBody buildResponseBodyFromSquareData(CatalogObject[] retrievedObjects)
	{
		// First element of the length 2 parameter array is a CatalogItem
		// and the second one a CatalogObject. This is guaranteed by caller.
		final CatalogItem item = retrievedObjects[0].getItemData();
		final CatalogItemVariation variation = retrievedObjects[1].getItemVariationData();
		assertAndIfNotLogAndThrow(item != null && variation != null,
		               "Failed to retrieve appropriate handles to CatalogItem and " +
		                                           "CatalogItemVariation instances.");
		return ProductResponseBody
						.builder()

							// Singular datum contained in the CatalogObject instance
							.id(unwrapCatalogItemName(retrievedObjects[0].getId()))

							// Most data pulled from the CatalogItem instance
							.name(item.getName())
							.description(item.getDescription())
							.availableElectronically(item.getAvailableElectronically())
							.availableForPickup(item.getAvailableForPickup())
							.availableOnline(item.getAvailableOnline())
							.categoryId(item.getCategoryId())
							.labelColor(item.getLabelColor())
							.productType(item.getProductType())

							// The rest of the data pulled from CatalogItemVariation instance
							.costInCents(variation.getPriceMoney().getAmount())
							.sku(variation.getSku())
							.upc(variation.getUpc())

						.build();
	}

	// Unwrap "NAME" from "NAME_{ITEM_SUFFIX}}" as a Long
	private  Long unwrapCatalogItemName(String squareItemId)
	{
		final int itemSuffixIdx = squareItemId.indexOf(DEFAULT_CATALOG_ITEM_SUFFIX);
		assertAndIfNotLogAndThrow(itemSuffixIdx <= 0, "Malformed CatalogItem id");
		return Long.parseLong(squareItemId.substring(itemSuffixIdx));
	}



	/**
	 * Send a PUT request for a specific product.
	 * @param id The product's unique id, provided by the request.
	 * @param request The request body.
	 */
	public  void putProduct(Long id, ProductRequestBody request)
	{
		// TODO: DON'T FORGET THAT SQUARE IDS ARE STRINGS!!!
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param id The product's unique id.
	 */
	public  void patchProduct(Long id, ProductRequestBody request)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param id The product's unique id.
	 */
	public  void deleteProduct(Long id, ProductRequestBody newProduct)
	{
		throw new UnimplementedMethodPlaceholder();
	}
}
