package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.sample_jsons.post.ExpectedBackendServicePostResponses;
import com.company.rest.products.model.sample_jsons.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.json_objects.BackendServiceResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerPostTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private ProductController controller; // The class we are testing

	@Mock
	private BackendService backendService;     // The class that will be mocked



	private boolean responseMatchesPostRequest(@NonNull ProductPostRequestBody postRequestBody,
	                                           @NonNull ProductResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				postRequestBody.getName().equals(responseBody.getName()) &&
				postRequestBody.getProductType().equals(responseBody.getProductType()) &&
				postRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&
				postRequestBody.getClientProductId().equals(responseBody.getClientProductId()) &&

				// Subsequent fields that may or may not have been provided, so we
				// use an Optional to protect ourselves against NPEs:
				ofNullable(postRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically())) &&
				ofNullable(postRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup())) &&
				ofNullable(postRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline())) &&
				ofNullable(postRequestBody.getCategoryId()).equals(ofNullable(responseBody.getCategoryId())) &&
				ofNullable(postRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(postRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(postRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(postRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc())) &&

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				responseBody.getIsDeleted() == null || !responseBody.getIsDeleted();
	}

	private ProductResponseBody checkAndGet(final ResponseEntity<ResponseMessage> responseEntity)
	{
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		return getResponseData(responseEntity);
	}


	private ProductResponseBody getResponseData(final ResponseEntity<ResponseMessage> responseEntity)
	{
		return (ProductResponseBody) Objects.requireNonNull(responseEntity.getBody()).getData();
	}

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOnePost()
	{
		final ProductPostRequestBody request = ProductPostRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("Flower")
														.clientProductId("RANDOM_ID")
														.costInCents(10000L) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.availableOnline(true)
														.availableElectronically(true) // Whatever that means
														.availableForPickup(true)
														.categoryId("ITEMS")
													.build();

		final BackendServiceResponseBody preparedResponse = BackendServiceResponseBody
														.builder()
	                                                        .name(request.getName())
	                                                        .clientProductId(request.getClientProductId())
															.squareItemId("RANDOM_SQUARE_ITEM_ID")
	                                                        .squareItemVariationId("RANDOM_SQUARE_ITEM_VAR_ID")
	                                                        .productType(request.getProductType())
	                                                        .costInCents(request.getCostInCents())
	                                                        .availableElectronically(request.getAvailableElectronically())
															.availableForPickup(request.getAvailableForPickup())
															.availableOnline(request.getAvailableOnline())
															.isDeleted(false)
															.sku(request.getSku())
															.upc(request.getUpc())
															.categoryId(request.getCategoryId())
															.description(request.getDescription())
															.labelColor(request.getLabelColor())
															.presentAtAllLocations(true)
                                                          .build();

		when(backendService.postProduct(request)).thenReturn(preparedResponse);

		final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		final ProductResponseBody response = checkAndGet(responseEntity);
		assertTrue("Request did not match response", responseMatchesPostRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(backendService.postProduct(GoodPostRequests.POST_REQUESTS[i]))
					.thenReturn(ExpectedBackendServicePostResponses.RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GoodPostRequests.POST_REQUESTS[i]);
			final ProductResponseBody response = checkAndGet(responseEntity);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesPostRequest(GoodPostRequests.POST_REQUESTS[i], response));
		}
	}

}
