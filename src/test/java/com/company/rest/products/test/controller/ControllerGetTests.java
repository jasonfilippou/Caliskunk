package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.test.model.square.SquareServiceGetTests;
import com.company.rest.products.test.requests_responses.get.GoodGetRequests;
import com.company.rest.products.test.requests_responses.get.MockedBackendServiceGetResponses;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.post.MockedBackendServicePostResponses;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.util.TestUtil.checkEntityStatusAndFetchResponse;
import static com.company.rest.products.test.util.TestUtil.makeAPost;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Mocked GET request tests for {@link ProductController}.
 *
 * This unit test suite is actually subsumed by {@link ControllerPostTests}.
 *
 * @see ProductController
 * @see BackendServiceGetTests
 * @see SquareServiceGetTests
 * @see com.company.rest.products.test.end_to_end.EndToEndGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerGetTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private ProductController controller; // The class we are testing

	@Mock
	private BackendService backendService;     // The class that will be mocked


	private boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                           @NonNull ProductResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOneGet()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_GET_ID";
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId, backendService, controller);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding GET, and ensure it works. Mock the backend call.
		final ProductGetRequestBody request = new ProductGetRequestBody(productId);
		when(backendService.getProduct(productId)).thenReturn(BackendServiceResponseBody.fromProductResponseBody(postResponse));

		final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(productId);
		final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);
		assertTrue("Request did not match response", responseMatchesGetRequest(request, getResponse));
	}



	@Test
	public void testManyGets()
	{

		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////

			// Mock the backend POST call.
			when(backendService.postProduct(GoodPostRequests.REQUESTS[i]))
				.thenReturn(MockedBackendServicePostResponses.RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

			// Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
//			assertTrue("Request did not match response", responseMatchesPostRequest(GoodPostRequests.REQUESTS[i],
//			                                                                        postResponse));

			///////////////////////////////////
			// And now we check the GET call.//
			///////////////////////////////////

			// Mock the backend GET call.
			when(backendService.getProduct(GoodGetRequests.REQUESTS[i].getClientProductId()))
						.thenReturn(MockedBackendServiceGetResponses.RESPONSES[i]);    // You will still be getting the data from POST!

			// Call controller
			final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(GoodGetRequests.REQUESTS[i].getClientProductId());
			final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesGetRequest(GoodGetRequests.REQUESTS[i], getResponse));

		}
	}

}
