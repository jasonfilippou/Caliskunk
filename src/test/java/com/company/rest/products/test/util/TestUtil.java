package com.company.rest.products.test.util;


import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

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
	 * Perforrms a POST request by using the controller but mocking the backend service.
	 *
	 * @see
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
	/* ********************************************************************************************************** */
	/*  Note: There's a lot of code repetition in the rest of this file, because of the fact that we don't have a */
	/*  common representation for all response classes. TODO: make one such representation (e.g GeneralResponseBody) */
	/* ********************************************************************************************************** */

	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final ProductResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getProductType().equals(responseBody.getProductType()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&
				upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody) &&

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
	}
	
	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final BackendServiceResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getProductType().equals(responseBody.getProductType()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&
				upsertRequestBody.getClientProductId().equals(responseBody.getClientProductId()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody) &&

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
	}
	
	public static boolean responseMatchesUpsertRequest(@NonNull final ProductUpsertRequestBody upsertRequestBody,
	                                                   @NonNull final SquareServiceResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				upsertRequestBody.getName().equals(responseBody.getName()) &&
				upsertRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&

				// Subsequent fields that may or may not have been provided:
				optionalFieldsMatch(upsertRequestBody, responseBody) &&


				// Let us also ensure that the POST didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
	}


	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final ProductResponseBody responseBody)
	{
		return  ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}

	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final BackendServiceResponseBody responseBody)
	{
		return  ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}


	private static boolean optionalFieldsMatch(final ProductUpsertRequestBody upsertRequestBody, final SquareServiceResponseBody responseBody)
	{
		return  ofNullable(upsertRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(upsertRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(upsertRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(upsertRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(upsertRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(upsertRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(upsertRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()));
	}
}
