package com.company.rest.products.test.util;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.controller.ControllerGetTests;
import com.company.rest.products.test.end_to_end.EndToEndGetTests;
import com.company.rest.products.test.model.backend.BackendServiceDeleteTests;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.*;
import com.squareup.square.models.*;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static com.company.rest.products.model.SquareService.*;
import static com.company.rest.products.test.util.TestUtil.SortingOrder.ASC;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static com.company.rest.products.util.Util.*;
import static java.util.Optional.ofNullable;
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
	 * An enum encoding ascending or descending sorting order.
	 */
	public enum SortingOrder
	{
		DESC, ASC;
	}

	/**
	 * A test-wide constant to assist with mocked tests that need to see a version field in various responses.
	 * @see CatalogObject#getVersion()
	 */
	public static final Long DEFAULT_VERSION = 1000000L;

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
	 * A default product type to use in tests.
	 */
	public static final String DEFAULT_PRODUCT_TYPE = "FLOWER";

	/**
	 * A default {@link CatalogItem} id to use in tests.
	 */
	public static final String DEFAULT_SQUARE_ITEM_ID = "RANDOM_SQUARE_ITEM_ID";

	/**
	 * A default {@link CatalogItemVariation} to use in tests.
	 */
	public static final String DEFAULT_SQUARE_ITEM_VARIATION_ID = "RANDOM_SQUARE_ITEM_VAR_ID";

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
		assertAndIfNotLogAndThrow(status.equals(responseEntity.getStatusCode()), "Status was " +
		                                                                         responseEntity.getStatusCode() +
		                                                                         " instead of " + status.value());
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
													.productName("Pengolin's Revenge")
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
                                                        .name(request.getProductName().strip().toUpperCase())
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
														.version(DEFAULT_VERSION)
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
													.productName("Pengolin's Revenge")
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
	 * @param upsertRequestBody The {@code POST}, {@code PUT} or {@code PATCH} request information provided by the client.
	 * @param responseBody An instance of {@link ProductResponseBody} containing information from the {@link ProductController} layer's runtime.
	 * @param upsertType Can be one of {@link UpsertType#POST}, {@link UpsertType#PUT} or {@link UpsertType#PATCH}.
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
	 * @param upsertRequestBody The {@code POST}, {@code PUT} or {@code PATCH} request information provided by the client.
	 * @param responseBody An instance of {@link BackendServiceResponseBody} containing information from the {@link BackendService} layer's runtime.
	 * @param upsertType Can be one of {@link UpsertType#POST}, {@link UpsertType#PUT} or {@link UpsertType#PATCH}.
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
	 * @param upsertRequestBody The {@code POST}, {@code PUT} or {@code PATCH} request information provided by the client.
	 * @param responseBody An instance of {@link SquareServiceResponseBody} containing information from the {@link SquareService} layer's runtime.
	 * @param upsertType Can be one of {@link UpsertType#POST}, {@link UpsertType#PUT} or {@link UpsertType#PATCH}.
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
	 * @param version The &quot; version &quot; of the {@link CatalogItem} of interest. Essential for UPDATE queries.
	 * @param type An {@link UpsertType} meant to tell us if we need to include id mapping information in the returned
	 * {@link UpsertCatalogObjectResponse}. The field {@code id_mappings} of that response class is only set for POST requests.
	 * @return An instance of {@link UpsertCatalogObjectResponse}, usually appropriately formed based on the argument
	 * in order to successfully mock a {@link com.company.rest.products.model.CatalogWrapper} call in testing.
	 *
	 * @see com.company.rest.products.test.model.backend.BackendServicePutTests
	 * @see BackendServiceDeleteTests
	 */
	public static UpsertCatalogObjectResponse buildItemResponseOutOfRequest(@NonNull final UpsertCatalogObjectRequest request,
	                                                                       @NonNull final Long version, @NonNull UpsertType type)
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
				.idMappings(createCatalogMappings(request, itemWrapper, variationWrapper, type))
				.build();
	}

	private static List<CatalogIdMapping> createCatalogMappings(final UpsertCatalogObjectRequest request, final CatalogObject itemWrapper,
	                                                            final CatalogObject itemVariationWrapper, final UpsertType type)
	{
		return type == PUT ? null : Arrays.asList(new CatalogIdMapping.Builder()
												.clientObjectId(request.getObject().getId())
												.objectId("RANDOM_SQUARE_ID")
												.build(),
												new CatalogIdMapping.Builder()
												.clientObjectId(itemVariationWrapper.getId())
												.objectId("RANDOM_SQUARE_ID" + DEFAULT_ITEM_VARIATION_ID_SUFFIX)
												.build());
	}

	/** Prepare a mocked {@link RetrieveCatalogObjectResponse} instance given information from a {@link ProductGetRequestBody} instance.
	 *
	 * @param getRequest A {@link ProductGetRequestBody} instance.
	 *
	 * @return An instance of {@link RetrieveCatalogObjectResponse}.
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
	 * Prepare a mocked {@link DeleteCatalogObjectResponse} given information mined from a {@link ProductDeleteRequestBody}. Useful for tests.
	 * @param deleteRequest The {@link ProductDeleteRequestBody} instance provided by the user.
	 * @return An instance of {@link DeleteCatalogObjectResponse}.
	 */
	public static DeleteCatalogObjectResponse buildMockedDeleteResponseOutOfRequest(@NonNull final ProductDeleteRequestBody deleteRequest)
	{
		return new DeleteCatalogObjectResponse.Builder()
				.deletedObjectIds(Arrays.asList(deleteRequest.getLiteProduct().getSquareItemId(),
				                                deleteRequest.getLiteProduct().getSquareItemVariationId()))
				.deletedAt(LocalDateTime.now().toString())
				.build();
	}

	/* ************************************************************************ */
	/* ********************** UTILITIES FOR GET ALL TESTS ********************* */
	/* ************************************************************************ */
	/**
	 * Defines sorting strategies to test pagination.
	 * @see LiteProduct
	 * @see ControllerGetTests#testGetAll()
	 * @see BackendServiceGetTests#testGetAll()
	 * @see EndToEndGetTests#testGetAll()
	 * @return A {@link Map} that associates fields with appropriate sorting strategies for their type.
	 */
	public static Map<String, Comparator<LiteProduct>> createSortingStrategies()
	{
		return new HashMap<>()
		{
			{   // The following 4 fields are the only accepted ones for sorting reasons.
				put("clientProductId", Comparator.comparing(LiteProduct::getClientProductId));
				put("productName", Comparator.comparing(LiteProduct::getProductName));
				put("productType", Comparator.comparing(LiteProduct::getProductType));
				put("costInCents", Comparator.comparingLong(LiteProduct::getCostInCents));
			}
		};
	}
	/**
	 * Checks if the provided {@link Page}'s successor flag is the expected one.
	 * @param page An instance of {@link Page}.
	 * @param pageIdx The zero-based index of the provided {@link Page} in the collection of {@link Page}s that paginated the data.
	 * @param totalPages The total number of pages in the pagination.
	 * @return {@literal true} if the successor flag is of the expected state, {@literal false} otherwise. An &quot;expected&quot;
	 * state is one that is {@literal true} when there <b>should</b> be a successor flag, {@literal false} otherwise.
	 * @see EndToEndGetTests#testGetAll()
	 */
	public static boolean checkPageSuccessor(final int pageIdx, final int totalPages, @NonNull final Page<?> page)
	{
		return (pageIdx <= totalPages - 1) == page.hasNext();
	}

	/**
	 * Examines if the field provided is monotonically increasing or decreasing.
	 * @param page An instance of {@link Page} .
	 * @param sorterField The field of {@link LiteProduct} that will be used to sort the elements of {@code liteProducts}
	 * @param sortingOrder An instance of {@link SortingOrder} which will determine if the sorting based on {@code sorterField}
	 *      is in ascending or descending order.
	 * @return  {@literal true} if the {@link Page}'s elements match the provided array's elements 1 - by - 1, {@literal false} otherwise.
	 * @see ControllerGetTests#testGetAll()
	 * @see BackendServiceGetTests#testGetAll()
	 * @see EndToEndGetTests#testGetAll()
	 *
	 */
	public static boolean fieldMonotonic(final Page<LiteProduct> page, final String sorterField, final SortingOrder sortingOrder)
	{
		if(page.getNumberOfElements() >=  2)
		{
			final Iterator<LiteProduct> itBack = page.iterator(), itFront = page.iterator();
			itFront.next();     // The "front" iterator needs to advance to second position before the loop.
			do
			{
				final LiteProduct lpFront = itFront.next(), lpBack = itBack.next();
				if (!gtOrLt(lpFront, lpBack, sorterField, sortingOrder))
				{
					return false;
				}
			} while (itFront.hasNext());
		}
		return true;                        // Either exhausted the page, or it was too small to begin with.
	}

	private static boolean gtOrLt(final LiteProduct lpFront, final LiteProduct lpBack, final String sorterField, final SortingOrder sortingOrder)
	{
		// TODO: try to fix this with reflection and type inference
		switch (sorterField)
		{
			case "clientProductId":
			{
				return gtOrLt(lpFront.getClientProductId(), lpBack.getClientProductId(), sortingOrder);
			}
			case "productName":
			{
				return gtOrLt(lpFront.getProductName(), lpBack.getProductName(), sortingOrder);
			}
			case "productType":
			{
				return gtOrLt(lpFront.getProductType(), lpBack.getProductType(), sortingOrder);
			}
			case "costInCents":
			{
				return gtOrLt(lpFront.getCostInCents(), lpBack.getCostInCents(), sortingOrder);
			}
			default:
			{
				throw new IllegalArgumentException("Sorter Field " + sorterField + " either non-existant or not appropriate for client-side sorting.");
			}
		}
	}

	private static boolean gtOrLt(final String valFront, final String valBack, final SortingOrder sortingOrder)
	{
		return sortingOrder.equals(ASC) ? valFront.compareTo(valBack) >= 0 : valFront.compareTo(valBack) <= 0;
	}

	private static boolean gtOrLt(final Long valFront, final Long valBack, final SortingOrder sortingOrder)
	{
		return sortingOrder.equals(ASC) ? valFront.compareTo(valBack) >= 0 : valFront.compareTo(valBack) <= 0;
	}

	/**
	 * Returns the expected number of elements in the current page.
	 * @param pageIdx The zero-based index of the current page.
	 * @param totalPages The total number of pages in the paginated data.
	 * @param totalElements The total number of data elements.
	 * @return The expected number of elements in the current page.
	 */
	public static int getNumElementsInPage(final int pageIdx, final int totalPages, final long totalElements)
	{
		return (int) (pageIdx < totalPages - 1 ? totalElements / totalPages : totalElements % totalPages);
	}

	/**
	 * Mocks a {@link BackendService} or {@link LiteProductRepository} call to return a pre-defined {@link Page}.
	 *
	 * @param startElementIdx The index into the original collection of the element that should begin the {@link Page}.
	 * @param elementsInPage The elements that the returned {@link Page} should contain.
	 * @param liteProducts The requests to snatch a page from, transformed from instances of {@link ProductUpsertRequestBody}
	 *                     into {@link LiteProduct} instances.
	 *
	 * @return  A mocked {@link Page}.
	 */
	public static Page<LiteProduct> mockedPage(final int startElementIdx, final int elementsInPage, final List<LiteProduct> liteProducts)
	{
		return new PageImpl<>(liteProducts.subList(startElementIdx, startElementIdx + elementsInPage));
	}

	/**
	 * Transform a {@link ProductUpsertRequestBody} instance into a {@link LiteProduct} instance. Used extensively by
	 * GET ALL tests.
	 * @param request An instance of {@link ProductUpsertRequestBody}
	 * @return An instance of {@link LiteProduct}
	 */
	public static LiteProduct toyLiteProduct(final ProductUpsertRequestBody request)
	{
		return LiteProduct.builder()
		                  .clientProductId(Optional.ofNullable(request.getClientProductId()).orElseThrow(() -> new IllegalArgumentException("Request did not have client product ID")))
		                  .productName(Optional.ofNullable(request.getProductName()).orElse(DEFAULT_PRODUCT_NAME))
		                  .productType(Optional.ofNullable(request.getProductType()).orElse(DEFAULT_PRODUCT_TYPE))
		                  .costInCents(Optional.ofNullable(request.getCostInCents()).orElse(DEFAULT_COST_IN_CENTS))
		                  .squareItemId(Optional.ofNullable(request.getSquareItemId()).orElse(DEFAULT_SQUARE_ITEM_ID))
		                  .squareItemVariationId(Optional.ofNullable(request.getSquareItemVariationId()).orElse(DEFAULT_SQUARE_ITEM_VARIATION_ID))
		                  .version(Optional.ofNullable(request.getVersion()).orElse(DEFAULT_VERSION))
		                  .build();
	}
}
