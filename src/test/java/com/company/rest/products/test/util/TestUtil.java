package com.company.rest.products.test.util;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.model.backend.BackendServiceDeleteTests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.*;
import com.squareup.square.models.*;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static com.company.rest.products.model.SquareService.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static com.company.rest.products.util.Util.nullOrFalse;
import static com.company.rest.products.util.Util.stringsMatch;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * A class of utilities for unit tests. We leverage {@code jUnit4} assertions.
 * @see ResponseEntity
 * @see ResponseMessage
 * @see org.junit.Assert
 * @see AssertionError
 */
public class TestUtil
{
	/**
	 * An enum to assist with our testing of specific upsert requests.
	 */
	public enum UpsertType
	{
		POST, PUT, PATCH
	}
	/**
	 * A test-wide constant to assist with mocked tests that need to see a version field in various responses.
	 * @see CatalogObject#getVersion()
	 */
	public static final Long DEFAULT_VERSION_FOR_TESTS = 1000000L;

	/**
	 * A test-wide constant to assist with mocked tests that need to see a stringified date field in various responses.
	 * @see CatalogObject#getUpdatedAt()
	 */
	public static final String DEFAULT_UPDATED_AT_STRING = "01/01/1970 00:00:01";

	/**
	 * A default pricing model for our tests.
	 */
	public static final String PRICE_MODEL = "FIXED_PRICING";

	/**
	 * A default cost, in cents, for our tests.
	 */
	public static final Long DEFAULT_COST_IN_CENTS = 10000L;

	/**
	 * A default "type" field for {@link CatalogObject} instances. Required by Square.
	 */
	public static final String DEFAULT_CATALOG_OBJECT_TYPE = "REGULAR";

	/**
	 * A default product name for our tests.
	 */
	public static final String DEFAULT_PRODUCT_NAME = "Culeothesis Necrosis";
	/**
	 *  A {@link String} that {@link CatalogObject} requires to signify that the {@link CatalogObject} is
	 *  actually a wrapper around a {@link CatalogItem} instance.
	 * @see #CODE_FOR_CATALOG_ITEM_VARIATIONS
	 */
	public static final String CODE_FOR_CATALOG_ITEMS = "ITEM";

	/**
	 *  A {@link String} that {@link CatalogObject} requires to signify that the {@link CatalogObject} is
	 *  actually a wrapper around a {@link CatalogItemVariation} instance.
	 * @see #CODE_FOR_CATALOG_ITEMS
	 */
	public static final String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";

	/**
	 * Ensure that a provided {@link ResponseEntity} has the provided {@link HttpStatus}.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @param status The {@link HttpStatus} that we want to check against.
	 */
	public static void checkEntityStatus(final ResponseEntity<ResponseMessage> responseEntity, final HttpStatus status)
	{
		assertEquals(status, responseEntity.getStatusCode());
	}


	/**
	 * Ensure that the provided {@link ResponseEntity} has the appropriate {@link HttpStatus} and return the query data.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @param status The {@link HttpStatus} to check against.
	 * @see #checkEntityStatus(ResponseEntity, HttpStatus)
	 * @return The information contained within {@code responseEntity}.
	 */
	public static ProductResponseBody checkEntityStatusAndFetchResponse(final ResponseEntity<ResponseMessage> responseEntity,
	                                                                    final HttpStatus status)
	{
		checkEntityStatus(responseEntity, status);
		return getResponseData(responseEntity);
	}


	/**
	 * Retrieve the query data contained in the {@link ResponseEntity} argument.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @return The information contained within {@code responseEntity}.
	 */
	public static ProductResponseBody getResponseData(final ResponseEntity<ResponseMessage> responseEntity)
	{
		return (ProductResponseBody) Objects.requireNonNull(responseEntity.getBody()).getData();
	}


	/**
	 * Delete all the instances of the {@link LiteProductRepository} argument. Useful for testing when using
	 * a persistent local database, such as MySQL, PostGres, Mongo, etc
	 * @param repo The - usually {@code @Autowired} {@link com.company.rest.products.model.liteproduct.LiteProductRepository}
	 *             instance that we want to flush.
	 * @see JpaRepository#deleteAll()
	 */
	public static void flushRepo(LiteProductRepository repo)
	{
		repo.deleteAll();
	}

	/**
	 * Performs a POST request by using the controller but mocking the backend service POST call.
	 *
	 * @param clientProductId The unique product ID provided by the client.
	 * @param backendService A usually {@code @Autowired} {@link BackendService} instance, whose relevant call will be mocked in this method
	 * @param controller A usually {@code @Autowired} {@link ProductController} instance, to which we will submit our query.
	 * @see #makeAPost(String, ProductController)
	 * @return An appropriate {@link ResponseEntity}.
	 */
	public static ResponseEntity<ResponseMessage> makeAPost(@NonNull final String clientProductId,
	                                                        @NonNull final BackendService backendService,
	                                                        @NonNull final ProductController controller)
	{
		// Build POST request body
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
														.name("Pengolin's Revenge")
														.productType("Vaporizer")
														.clientProductId(clientProductId)
														.costInCents(13000L) // 'L for long literal
														.description("We're done.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")



													.build();
		// Define mocked answer
		final BackendServiceResponseBody mockedResponse = BackendServiceResponseBody
														.builder()
                                                        .name(request.getName().strip().toUpperCase())
                                                        .clientProductId(request.getClientProductId())
														.squareItemId("#RANDOM_SQUARE_ITEM_ID")
														.updatedAt(DEFAULT_UPDATED_AT_STRING)
                                                        .productType(request.getProductType())
                                                        .costInCents(request.getCostInCents())
														.isDeleted(false)
														.sku(request.getSku())
														.upc(request.getUpc())
														.description(request.getDescription())
														.labelColor(request.getLabelColor())
														.version(DEFAULT_VERSION_FOR_TESTS)
                                                        .build();
		// Mock the call to the backend service
		when(backendService.postProduct(request)).thenReturn(mockedResponse);

		// Make the call to the controller
		return controller.postProduct(request);
	}

	/**
	 * Performs a POST request at the level of the controller, but without any mocked services.
	 * @param id  The unique product ID provided by the client.
	 * @param controller A usually {@code @Autowired} {@link ProductController} instance, instance, to which we will submit our query.
	 * @see #makeAPost(String, BackendService, ProductController)
	 * @return An appropriate {@link ResponseEntity}.
	 */
	public static ResponseEntity<ResponseMessage> makeAPost(@NonNull final String id, @NonNull final ProductController controller)
	{
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
													.name("Pengolin's Revenge")
													.productType("Vaporizer")
													.clientProductId(id)
													.costInCents(13000L) // 'L for long literal
													.description("We're done.")
													.labelColor("7FFFD4")
													.upc("RANDOM_UPC")
													.sku("RANDOM_SKU")



													.build();
		return controller.postProduct(request);
	}

	/* ********************************************************************************************************** */
	/*  Note: There's a lot of code repetition in the rest of this file, because of the fact that we don't have a */
	/*  common representation for all response classes. TODO: make one such representation (e.g GeneralResponseBody) */
	/* ********************************************************************************************************** */

	/**
	 * Examines if the fields of the provided {@link ProductResponseBody} match those of the provided {@link ProductUpsertRequestBody}
	 *
	 * @return {@literal true} if the fields match, {@literal false} otherwise.
	 */
	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final ProductResponseBody responseBody,
	                                                   @NonNull final TestUtil.UpsertType upsertType)
	{
		return	!isDeleted(responseBody) &&
                ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
				optionalFieldsMatch(upsertRequestBody, responseBody, upsertType);
	}

	/**
	 * Examines if the fields of the provided {@link ProductResponseBody} match those of the provided {@link BackendServiceResponseBody}
	 *
	 * @return {@literal true} if the fields match, {@literal false} otherwise.
	 */
	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final BackendServiceResponseBody responseBody,
	                                                   @NonNull final TestUtil.UpsertType upsertType)
	{
		return	!isDeleted(responseBody) &&
	            ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
                optionalFieldsMatch(upsertRequestBody, responseBody, upsertType);
	}

	/**
	 * Examines if the fields of the provided {@link ProductResponseBody} match those of the provided {@link SquareServiceResponseBody}
	 *
	 * @return {@literal true} if the fields match, {@literal false} otherwise.
	 */
	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final SquareServiceResponseBody responseBody,
	                                                   @NonNull final TestUtil.UpsertType upsertType)
	{
		return	!isDeleted(responseBody) &&
	            ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
	            optionalFieldsMatch(upsertRequestBody, responseBody, upsertType);
	}

	private static boolean isDeleted(final ProductResponseBody responseBody)
	{
		return !nullOrFalse(responseBody.getIsDeleted());
	}

	private static boolean isDeleted(final BackendServiceResponseBody responseBody)
	{
		return !nullOrFalse(responseBody.getIsDeleted());
	}


	private static boolean isDeleted(final SquareServiceResponseBody responseBody)
	{
		return !nullOrFalse(responseBody.getIsDeleted());
	}

	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final ProductResponseBody responseBody,
	                                                                                            final UpsertType upsertType)
	{
		return  ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}

	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final BackendServiceResponseBody responseBody,
											                                                    final UpsertType upsertType)
	{
		return  ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}

	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final SquareServiceResponseBody responseBody,
											                                                    final UpsertType upsertType)
	{
		return  ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}

	private static boolean ensureIdsMatch(final ProductUpsertRequestBody upsertRequestBody, final ProductResponseBody responseBody,
	                                                                                    final UpsertType upsertType)
	{
		if(upsertType == POST)
		{
			return upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId());
		}
		else  // PUT, PATCH, where ID is not required, since it's provided separately at the URL
		{
			return responseBody.getClientProductId() != null; // The ProductResponseBody should *always* contain the ID.
		}
	}

	private static boolean ensureIdsMatch(final ProductUpsertRequestBody upsertRequestBody, final BackendServiceResponseBody responseBody,
	                                                                                    final UpsertType upsertType)
	{
		if(upsertType == POST)
		{
			return upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId());
		}
		else  // PUT, PATCH, where ID is not required, since it's provided separately at the URL
		{
			return responseBody.getClientProductId() != null; // The BackendServiceResponseBody should *always* contain the ID.
		}
	}

	private static boolean ensureIdsMatch(final ProductUpsertRequestBody upsertRequestBody, final SquareServiceResponseBody responseBody,
	                                                                                         final UpsertType upsertType)
	{
		if(upsertType == POST)
		{
			return upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId());
		}
		else  // PUT, PATCH, where ID is not required, since it's provided separately at the URL
		{
			return responseBody.getClientProductId() != null; // The SquareServiceResponseBody should *always* contain the ID.
		}
	}

	/**
	 *  Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link ProductResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param getResponseBody The {@link ProductController}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                                @NonNull ProductResponseBody getResponseBody)
	{
		return getRequestBody.getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(getRequestBody, getResponseBody);
	}

	/**
	 *  Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param getResponseBody The {@link BackendService}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                                @NonNull BackendServiceResponseBody getResponseBody)
	{
		return getRequestBody.getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(getRequestBody, getResponseBody);
	}


	/**
	 * Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link SquareServiceResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param getResponseBody The {@link SquareService}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull final ProductGetRequestBody getRequestBody,
	                                                @NonNull final SquareServiceResponseBody getResponseBody)
	{
		return getRequestBody.getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(getRequestBody, getResponseBody);
	}

	private static boolean liteProductFieldsMatchIfPresent(@NonNull final ProductGetRequestBody getRequestBody, @NonNull final ProductResponseBody getResponseBody)
	{
		return getRequestBody.getLiteProduct() == null ||
		       getRequestBody.getLiteProduct().getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductName(), getResponseBody.getName()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductType(), getResponseBody.getProductType()) &&
		       getRequestBody.getLiteProduct().getVersion().equals(getResponseBody.getVersion());
				// No Square ID in Product responses.
	}


	private static boolean liteProductFieldsMatchIfPresent(@NonNull final ProductGetRequestBody getRequestBody, @NonNull final BackendServiceResponseBody getResponseBody)
	{
		return getRequestBody.getLiteProduct() == null ||
		       getRequestBody.getLiteProduct().getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductName(), getResponseBody.getName()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductType(), getResponseBody.getProductType()) &&
		       getRequestBody.getLiteProduct().getSquareItemId().equals(getResponseBody.getSquareItemId()) &&
		       getRequestBody.getLiteProduct().getVersion().equals(getResponseBody.getVersion());
			   // Both square ID and version required here.
	}

	private static boolean liteProductFieldsMatchIfPresent(@NonNull final ProductGetRequestBody getRequestBody, @NonNull final SquareServiceResponseBody getResponseBody)
	{
		return getRequestBody.getLiteProduct() == null ||
		       getRequestBody.getLiteProduct().getClientProductId().equals(getResponseBody.getClientProductId()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductName(), getResponseBody.getName()) &&
		       stringsMatch(getRequestBody.getLiteProduct().getProductType(), getResponseBody.getProductType()) &&
		       getRequestBody.getLiteProduct().getSquareItemId().equals(getResponseBody.getSquareItemId()) &&
		       getRequestBody.getLiteProduct().getVersion().equals(getResponseBody.getVersion());
				// Both square ID and version required here.
	}


	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param deleteRequestBody A DEL request provided by the client.
	 * @param deleteResponseBody The {@link ProductController}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody deleteRequestBody,
	                                                   @NonNull final ProductResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(deleteRequestBody, deleteResponseBody);
	}
	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param deleteRequestBody A DEL request provided by the client.
	 * @param deleteResponseBody The {@link BackendService}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody deleteRequestBody,
	                                                   @NonNull final BackendServiceResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(deleteRequestBody, deleteResponseBody);
	}



	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link SquareServiceResponseBody}.
	 *
	 * @param deleteRequestBody A DEL request provided by the client.
	 * @param deleteResponseBody The {@link SquareService}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody deleteRequestBody,
	                                                   @NonNull final SquareServiceResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
		       liteProductFieldsMatchIfPresent(deleteRequestBody, deleteResponseBody);
	}

	private static boolean liteProductFieldsMatchIfPresent(@NonNull ProductDeleteRequestBody deleteRequestBody, @NonNull ProductResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getLiteProduct() == null ||
		       deleteRequestBody.getLiteProduct().getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
		       stringsMatch(deleteRequestBody.getLiteProduct().getProductName(), deleteResponseBody.getName()) &&
		       stringsMatch(deleteRequestBody.getLiteProduct().getProductType(), deleteResponseBody.getProductType());
				// No Square ID because it's a product response, and no version because it's a DELETE.
	}

	private static boolean liteProductFieldsMatchIfPresent(@NonNull ProductDeleteRequestBody deleteRequestBody, @NonNull BackendServiceResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getLiteProduct() == null ||
			   deleteRequestBody.getLiteProduct().getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
			   stringsMatch(deleteRequestBody.getLiteProduct().getProductName(), deleteResponseBody.getName()) &&
			   stringsMatch(deleteRequestBody.getLiteProduct().getProductType(), deleteResponseBody.getProductType()) &&
		       deleteRequestBody.getLiteProduct().getSquareItemId().equals(deleteResponseBody.getSquareItemId());
				// There's a square ID field because it's square service body, but no version field because it's a DELETE.
	}

	private static boolean liteProductFieldsMatchIfPresent(@NonNull ProductDeleteRequestBody deleteRequestBody, @NonNull SquareServiceResponseBody deleteResponseBody)
	{
		return deleteRequestBody.getLiteProduct() == null ||
		       deleteRequestBody.getLiteProduct().getClientProductId().equals(deleteResponseBody.getClientProductId()) &&
		       stringsMatch(deleteRequestBody.getLiteProduct().getProductName(), deleteResponseBody.getName()) &&
		       stringsMatch(deleteRequestBody.getLiteProduct().getProductType(), deleteResponseBody.getProductType()) &&
		       deleteRequestBody.getLiteProduct().getSquareItemId().equals(deleteResponseBody.getSquareItemId());
			  // There's a square ID field because it's square service body, but no version field because it's a DELETE.
	}


	/**
	 * A method useful for tests, where we want to mock {@link com.company.rest.products.model.CatalogWrapper}'s calls,
	 * which touch entities for {@link com.squareup.square.api.CatalogApi}. We receive an instance of {@link UpsertCatalogObjectRequest},
	 * and construct a {@link UpsertCatalogObjectResponse} that represents a successful call to the Square API.
	 *
	 *
	 * @param request An {@link UpsertCatalogObjectRequest} containing the information given to {@link com.company.rest.products.model.CatalogWrapper}.
	 *
	 * @return An instance of {@link UpsertCatalogObjectResponse}, usually appropriately formed based on the argument
	 * in order to successfully mock a {@link com.company.rest.products.model.CatalogWrapper} call in testing.
	 *
	 * @see com.company.rest.products.test.model.backend.BackendServicePutTests
	 * @see BackendServiceDeleteTests
	 */
	public static UpsertCatalogObjectResponse buildItemResponseOutOfRequest(@NonNull final UpsertCatalogObjectRequest request,
	                                                                       @NonNull final Long version)
	{
		final CatalogObject itemWrapper = new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, request.getObject().getId())
															.itemData(request.getObject().getItemData())
															.version(version)
															.isDeleted(false)
															.updatedAt(DEFAULT_UPDATED_AT_STRING)
															.build();
		final CatalogItem item = itemWrapper.getItemData();
		final CatalogObject variationWrapper = item.getVariations().get(0);
		return new UpsertCatalogObjectResponse.Builder()
				.catalogObject(itemWrapper)
				.idMappings(createCatalogMappings(request, itemWrapper, variationWrapper))
				.build();
	}

	private static List<CatalogIdMapping> createCatalogMappings(final UpsertCatalogObjectRequest request, final CatalogObject itemWrapper,
	                                                            final CatalogObject itemVariationWrapper)
	{
		return Arrays.asList(
				new CatalogIdMapping.Builder()
						.clientObjectId(request.getObject().getId())
						.objectId("RANDOM_SQUARE_ID")
						.build(),

				new CatalogIdMapping.Builder()
						.clientObjectId(itemVariationWrapper.getId())
						.objectId("RANDOM_SQUARE_ID" + DEFAULT_ITEM_VARIATION_ID_SUFFIX)
						.build()
		                    );
	}

	/**
	 *
	 */
	public static RetrieveCatalogObjectResponse buildResponseOutOfRetrieveRequest(final ProductGetRequestBody getRequest)
	{
		return new RetrieveCatalogObjectResponse.Builder()
				.object(buildObjectForRetrieveResponse(getRequest))
				.build();
	}

	private static CatalogObject buildObjectForRetrieveResponse(final ProductGetRequestBody getRequest)
	{
		return new CatalogObject
				.Builder(CODE_FOR_CATALOG_ITEMS, getRequest.getLiteProduct().getSquareItemId())
				.isDeleted(false)
				.updatedAt(DEFAULT_UPDATED_AT_STRING)
				.version(getRequest.getLiteProduct().getVersion())
				.itemData(new CatalogItem.Builder()
						          .name(getRequest.getLiteProduct().getProductName())
						          .variations(createVariationData(getRequest))
						          .build())
				.build();
	}

	private static List<CatalogObject> createVariationData(final ProductGetRequestBody getRequest)
	{
		final CatalogItemVariation variation = new CatalogItemVariation.Builder()
																		.name(getRequest.getLiteProduct().getProductName() + DEFAULT_ITEM_VARIATION_NAME_SUFFIX)
																		.itemId(getRequest.getLiteProduct().getSquareItemId())
																		.upc("SOME_UPC")
																		.sku("SOME_SKU")
																		.pricingType(PRICE_MODEL)
																		.priceMoney(new Money(getRequest.getLiteProduct().getCostInCents(),
																		                      CURRENCY))
																		.build();

		final CatalogObject variationWrapper = new CatalogObject.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS,
		                                                                 getRequest.getLiteProduct().getSquareItemId() + DEFAULT_ITEM_VARIATION_ID_SUFFIX)
																		.version(getRequest.getLiteProduct().getVersion())
																		.updatedAt(DEFAULT_UPDATED_AT_STRING)
																		.isDeleted(false)
																		.itemVariationData(variation)
																		.build();

		return Collections.singletonList(variationWrapper);
	}
	/**
	 *
	 */
	public static DeleteCatalogObjectResponse buildMockedDeleteResponseOutOfRequest(@NonNull final ProductDeleteRequestBody deleteRequest)
	{
		return new DeleteCatalogObjectResponse.Builder()
				.deletedObjectIds(Arrays.asList(deleteRequest.getLiteProduct().getSquareItemId(),
				                                deleteRequest.getLiteProduct().getSquareItemId() + DEFAULT_ITEM_VARIATION_ID_SUFFIX))
				.deletedAt(LocalDateTime.now().toString())
				.build();
	}
}
