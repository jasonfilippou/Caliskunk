package com.company.rest.products.model;

import com.company.rest.products.controller.ProductRequestBody;
import com.company.rest.products.controller.ProductResponseBody;
import com.company.rest.products.model.exceptions.ResourceAlreadyCreatedException;
import com.company.rest.products.util.UnimplementedMethodPlaceholder;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
public class BackendService
{

	// Default values for several CatalogObject attributes:
	private final static String PRICE_MODEL = "FIXED_PRICING";
	private final static String CURRENCY = "USD";
	private final static String CODE_FOR_CATALOG_ITEMS = "ITEM";
	private final static String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";
	private final static String DEFAULT_CATALOG_ITEM_PREFIX = "PROD_";
	private final static String DEFAULT_CATALOG_ITEM_VARIATION_PREFIX = "VAR_";
	private final static int ABBRV_CHARS = 3;

	// Necessary objects to connect to API
	private static final SquareClient client = new SquareClient.Builder()
			.environment(Environment.SANDBOX)
			.accessToken(System.getenv("SQUARE_SANDBOX_ACCESS_TOKEN"))
			.build();
	private static final CatalogApi catalogApi = client.getCatalogApi();


	/**
	 * Handle a POST request to Square's API.
	 *
	 * @param request A {@link ProductRequestBody} instance containing details of the request.
	 * @throws ResourceAlreadyCreatedException if the resource already exists.
	 */
	public static void postProduct(ProductRequestBody request)
	{

		//  Create a CatalogItem and a CatalogItemVariation registered to that item.
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it..
		sendCatalogItemUpsertRequest(request);
		sendCatalogItemVariationUpsertRequest(request);
	}

	/* CatalogItem upsert request helpers */

	private static void sendCatalogItemUpsertRequest(ProductRequestBody request)
	{
		final UpsertCatalogObjectRequest catalogItemUpsertRequest =
				createCatalogItemUpsertRequest(request);
		catalogApi.upsertCatalogObjectAsync(catalogItemUpsertRequest)
					.thenAccept(result-> log.info("New CatalogItem created on Square, with name "
					                              + request.getName() + "."))
					.exceptionally(exception ->
			         {
			         	logException(exception);
			         	throw new RuntimeException(exception.getMessage());
			         });
	}


	private static void logException(Throwable thrown)

	{
		log.error("An instance of " + thrown.getClass().getName() +
		          " was thrown, with message: " + thrown.getMessage() + ".");
			}

	private static UpsertCatalogObjectRequest createCatalogItemUpsertRequest(ProductRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForCatalogItemUpsertRequest(request));
	}

	private static CatalogObject createObjectFieldForCatalogItemUpsertRequest(ProductRequestBody request)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEMS, DEFAULT_CATALOG_ITEM_PREFIX + request.getId())
				.itemData(createCatalogItem(request))
				.build();
	}

	private static CatalogItem createCatalogItem(ProductRequestBody request)
	{
		return new CatalogItem
				.Builder()
				.name(DEFAULT_CATALOG_ITEM_PREFIX + request.getName())
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

	private static String abbreviate(@NonNull String str, int idx)
	{
		assert str.length() > 0 && idx >= 1 : " Bad params: str = " + str + " and idx = " + idx + ".";
		return str.length() > idx ? str.substring(0, idx) : str;
	}

	/*  CatalogItemVariation upsert request helpers */

	private static void sendCatalogItemVariationUpsertRequest(ProductRequestBody request)
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

	private static UpsertCatalogObjectRequest createCatalogItemVariationUpsertRequest(ProductRequestBody request)
	{
		return new UpsertCatalogObjectRequest(UUID.randomUUID().toString(),
		                                      createObjectFieldForItemVariationUpsertRequest(request));
	}

	private static CatalogObject createObjectFieldForItemVariationUpsertRequest(ProductRequestBody request)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS,
		                 DEFAULT_CATALOG_ITEM_VARIATION_PREFIX + request.getId())
				.itemVariationData(createCatalogItemVariation(request))
				.build();
	}

	private static CatalogItemVariation createCatalogItemVariation(ProductRequestBody request)
	{
		return new CatalogItemVariation
				.Builder()
				.itemId(request.getName())
				.name(DEFAULT_CATALOG_ITEM_VARIATION_PREFIX + request.getName())
				.sku(request.getSku())
				.upc(request.getUpc())
				.priceMoney(new Money(request.getCostInCents(), CURRENCY))
				.pricingType(PRICE_MODEL)
				.build();
	}

	/**
	 * Send a GET request for a specific product.
	 * @param id The product's unique id.
	 * @return A {@link ProductRequestBody} instance with the entire client-facing product data.
	 */
	public static ProductResponseBody getProduct(Long id) throws InterruptedException, ExecutionException
	{
		final Boolean includeRelatedObjects = false;
		final String itemId = DEFAULT_CATALOG_ITEM_PREFIX + id,
					 itemVarId = DEFAULT_CATALOG_ITEM_VARIATION_PREFIX + id;
		final BatchRetrieveCatalogObjectsRequest request =
				new BatchRetrieveCatalogObjectsRequest
				.Builder(Arrays.asList(itemId, itemVarId))
				.includeRelatedObjects(includeRelatedObjects)
				.build();
		try
		{
			BatchRetrieveCatalogObjectsResponse response =
					catalogApi.batchRetrieveCatalogObjectsAsync(request).get();
			return buildResponseBodyFromSquareData(response);
		}
		catch (InterruptedException | ExecutionException e)
		{
			logException(e);
			throw(e);
		}
	}

	// A valid batch response contains exactly one CatalogItem and one CatalogItemVariation.
	private static void validateBatchRetrievalResponse(BatchRetrieveCatalogObjectsResponse response)
	{
		// TODO: see if you can instead look for non-null pointers for object.getItemData()
		//  or object.getItemVariationData(). It's a bit more efficient than comparing strings.
		assert response.getObjects().size() == 2 &&
		       (response.getObjects().stream()
		                .allMatch(obj-> ( obj.getType().equals(CODE_FOR_CATALOG_ITEMS)
				                          ||
				                          (obj.getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS))
		                                )
		                         )
		       );
	}

	private static ProductResponseBody buildResponseBodyFromSquareData(BatchRetrieveCatalogObjectsResponse response)
	{
		// First position will be the CatalogItem, second the registered CatalogItemVariation.
		final CatalogObject[] retrievedObjects = parseObjects(response.getObjects());
		return buildResponseBodyFromSquareData(retrievedObjects);
	}

	private static CatalogObject[] parseObjects(List<CatalogObject> retrievedObjects)
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

	private static ProductResponseBody buildResponseBodyFromSquareData(CatalogObject[] retrievedObjects)
	{
		// First element of the length 2 parameter array is a CatalogItem
		// and the second one a CatalogObject. This is guaranteed by caller.
		CatalogItem item = retrievedObjects[0].getItemData();
		CatalogItemVariation variation = retrievedObjects[1].getItemVariationData();
		assert item != null && variation != null : "Failed to retrieve appropriate handles to CatalogItem and " +
		                                           "CatalogItemVariation instances.";
		return	ProductResponseBody
						.builder()
						// Singular datum contained in the CatalogObject instance
						.id(unwrapCatalogItemId(retrievedObjects[0].getId()))

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

	// Unwrap an ID of type "PROD_ID" into "ID",
	private static Long unwrapCatalogItemId(String squareItemId)
	{
		return Long.parseLong(squareItemId.substring(DEFAULT_CATALOG_ITEM_PREFIX.length()));
	}

	/**
	 * Send a PUT request for a specific product.
	 * @param id The product's unique id, provided by the request.
	 * @param request The request body.
	 */
	public static void putProduct(Long id, ProductRequestBody request)
	{
		// TODO: DON'T FORGET THAT SQUARE IDS ARE STRINGS!!!
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param id The product's unique id.
	 */
	public static void patchProduct(Long id, ProductRequestBody request)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param id The product's unique id.
	 */
	public static void deleteProduct(Long id, ProductRequestBody newProduct)
	{
		throw new UnimplementedMethodPlaceholder();
	}
}
