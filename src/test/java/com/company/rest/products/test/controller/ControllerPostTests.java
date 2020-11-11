package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.test.model.square.SquareServiceGetTests;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.post.MockedBackendServicePostResponses;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.util.TestUtil.checkHttpOkAndGet;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Mocked POST tests for {@link ProductController}.
 *
 * @see BackendServiceGetTests
 * @see SquareServiceGetTests
 * @see com.company.rest.products.test.end_to_end.EndToEndGetTests
 */
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
				ofNullable(postRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor())) &&
				ofNullable(postRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription())) &&
				ofNullable(postRequestBody.getSku()).equals(ofNullable(responseBody.getSku())) &&
				ofNullable(postRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc())) &&

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || !responseBody.getIsDeleted();
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
														.clientProductId("#RANDOM_ID")
														.costInCents(10000L) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.availableOnline(true)
														.availableElectronically(true) // Whatever that means
														.availableForPickup(true)
													.build();

		final BackendServiceResponseBody preparedResponse = BackendServiceResponseBody
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

		when(backendService.postProduct(request)).thenReturn(preparedResponse);

		final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		final ProductResponseBody response = checkHttpOkAndGet(responseEntity);
		assertTrue("Request did not match response", responseMatchesPostRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(backendService.postProduct(GoodPostRequests.REQUESTS[i]))
				.thenReturn(MockedBackendServicePostResponses.RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);
			final ProductResponseBody response = checkHttpOkAndGet(responseEntity);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesPostRequest(GoodPostRequests.REQUESTS[i], response));
		}
	}

}
