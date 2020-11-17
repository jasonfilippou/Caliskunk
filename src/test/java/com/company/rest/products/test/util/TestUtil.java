package com.company.rest.products.test.util;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.*;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
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
	public static enum UpsertType
	{
		POST, PUT, PATCH
	}

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
	 * @see #makeAPut(String, ProductController)
	 * @see #makeAPut(String, BackendService, ProductController)
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
														.availableOnline(true)
														.availableElectronically(true)
														.availableForPickup(false)
													.build();
		// Define mocked answer
		final BackendServiceResponseBody mockedResponse = BackendServiceResponseBody
														.builder()
	                                                        .name(request.getName())
	                                                        .clientProductId(request.getClientProductId())
															.squareItemId("#RANDOM_SQUARE_ITEM_ID")
	                                                        .squareItemVariationId("#RANDOM_SQUARE_ITEM_VAR_ID")
	                                                        .productType(request.getProductType())
	                                                        .costInCents(request.getCostInCents())
	                                                        .availableElectronically(request.getAvailableElectronically())
															.availableForPickup(request.getAvailableForPickup())
															.availableOnline(request.getAvailableOnline())
															.isDeleted(false)
															.sku(request.getSku())
															.upc(request.getUpc())
															.description(request.getDescription())
															.labelColor(request.getLabelColor())
															.presentAtAllLocations(true)
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
	 * @see #makeAPut(String, ProductController)
	 * @see #makeAPut(String, BackendService, ProductController)
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
														.availableOnline(true)
														.availableElectronically(true)
														.availableForPickup(false)
													.build();
		return controller.postProduct(request);
	}

	/**
	 * Performs a PUT request at the level of the controller, but mocking the backend service call.
	 * @param clientProductId The unique product ID provided by the client.
	 * @param backendService A usually {@code @Autowired} {@link BackendService} instance, whose relevant call will be mocked in this method
	 * @param controller A usually {@code @Autowired} {@link ProductController} instance, to which we will submit our query.
	 * @see #makeAPost(String, BackendService, ProductController)
	 * @see #makeAPost(String, ProductController)
	 * @see #makeAPut(String, ProductController)
	 * @return An appropriate {@link ResponseEntity}.
	 */
	public static ResponseEntity<ResponseMessage> makeAPut(@NonNull final String clientProductId,
	                                                        @NonNull final BackendService backendService,
	                                                        @NonNull final ProductController controller)
	{
		// Build PUT request body
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
														.availableOnline(true)
														.availableElectronically(true)
														.availableForPickup(false)
													.build();
		// Define mocked answer of backend layer
		final BackendServiceResponseBody mockedResponse = BackendServiceResponseBody
														.builder()
	                                                        .name(request.getName())
	                                                        .clientProductId(request.getClientProductId())
															.squareItemId("#RANDOM_SQUARE_ITEM_ID")
	                                                        .squareItemVariationId("#RANDOM_SQUARE_ITEM_VAR_ID")
	                                                        .productType(request.getProductType())
	                                                        .costInCents(request.getCostInCents())
	                                                        .availableElectronically(request.getAvailableElectronically())
															.availableForPickup(request.getAvailableForPickup())
															.availableOnline(request.getAvailableOnline())
															.isDeleted(false)
															.sku(request.getSku())
															.upc(request.getUpc())
															.description(request.getDescription())
															.labelColor(request.getLabelColor())
															.presentAtAllLocations(true)
                                                          .build();
		// Mock the call to the backend service
		when(backendService.postProduct(request)).thenReturn(mockedResponse);

		// Make the call to the controller
		return controller.postProduct(request);
	}

	/**
	 * Performs a PUT request at the level of the controller, without any mocked calls.
	 * @param id  The unique product ID provided by the client.
	 * @param controller A usually {@code @Autowired} {@link ProductController} instance, instance, to which we will submit our query.
	 * @see #makeAPost(String, ProductController)
	 * @see #makeAPost(String, BackendService, ProductController)
	 * @see #makeAPut(String, BackendService, ProductController)
	 * @return An appropriate {@link ResponseEntity}.
	 */
	public static ResponseEntity<ResponseMessage> makeAPut(@NonNull final String id, @NonNull final ProductController controller)
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
														.availableOnline(true)
														.availableElectronically(true)
														.availableForPickup(false)
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
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getProductType().equals(responseBody.getProductType()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody, upsertType) &&

				// Let us also ensure that the upsert request didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
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
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getProductType().equals(responseBody.getProductType()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&
				upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody, upsertType) &&

				// Let us also ensure that the upsert request didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
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
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody, upsertType) &&


				// Let us also ensure that the upsert request didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
	}


	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final ProductResponseBody responseBody,
	                                                                                            final UpsertType upsertType)
	{
		return  ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
				ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}

	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final BackendServiceResponseBody responseBody,
											                                                    final UpsertType upsertType)
	{
		return  ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
				ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}


	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final SquareServiceResponseBody responseBody,
											                                                    final UpsertType upsertType)
	{
		return  ensureIdsMatch(upsertRequestBody, responseBody, upsertType) &&
			    ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
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
			return upsertRequestBody.getClientProductId() == null;
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
			return upsertRequestBody.getClientProductId() == null;
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
			return upsertRequestBody.getClientProductId() == null;
		}
	}

	/**
	 *  Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link ProductResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param responseBody The {@link ProductController}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                                @NonNull ProductResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}

	/**
	 *  Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param responseBody The {@link BackendService}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                                @NonNull BackendServiceResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}

	/**
	 * Ensures that the provided {@link ProductGetRequestBody} matches the provided {@link SquareServiceResponseBody}.
	 *
	 * @param getRequestBody A GET request provided by the client.
	 * @param responseBody The {@link SquareService}'s response to the GET request.
	 * @return {@literal true} if GET request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                                @NonNull SquareServiceResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}

	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param delRequestBody A DEL request provided by the client.
	 * @param responseBody The {@link ProductController}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody delRequestBody,
	                                         @NonNull final ProductResponseBody responseBody)
	{
		return delRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}
	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link BackendServiceResponseBody}.
	 *
	 * @param delRequestBody A DEL request provided by the client.
	 * @param responseBody The {@link BackendService}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody delRequestBody,
	                                          @NonNull final BackendServiceResponseBody responseBody)
	{
		return delRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}

	/**
	 * Ensures that the provided {@link ProductDeleteRequestBody} matches the provided {@link SquareServiceResponseBody}.
	 *
	 * @param delRequestBody A DEL request provided by the client.
	 * @param responseBody The {@link SquareService}'s response to the GET request.
	 * @return {@literal true} if DEL request matches controller response, {@literal false} otherwise.
	 */
	public static boolean responseMatchesDeleteRequest(@NonNull final ProductDeleteRequestBody delRequestBody,
	                                         @NonNull final SquareServiceResponseBody responseBody)
	{
		return delRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}


}
