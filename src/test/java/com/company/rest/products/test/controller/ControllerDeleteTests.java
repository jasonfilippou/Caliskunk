package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.BackendServiceDeleteTests;
import com.company.rest.products.test.model.square.SquareServiceDeleteTests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.controller.MockedBackendServiceDeleteResponses.MOCKED_BACKEND_DELETE_RESPONSES;
import static com.company.rest.products.test.controller.MockedBackendServicePostResponses.MOCKED_BACKEND_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests.GOOD_DELETES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.checkEntityStatusAndFetchResponse;
import static com.company.rest.products.test.util.TestUtil.makeAPost;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static com.company.rest.products.test.util.TestUtil.*;

/**
 * Mocked DELETE request tests for {@link ProductController}.
 *
 * @see ProductController
 * @see BackendServiceDeleteTests
 * @see SquareServiceDeleteTests
 * @see com.company.rest.products.test.end_to_end.EndToEndDeleteTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerDeleteTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private ProductController controller; // The class we are testing

	@Mock
	private BackendService backendService;     // The class that will be mocked


	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */


	@Test
	public void testOneDel()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_DEL_ID";
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId, backendService, controller);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding DELETE, and ensure it works. Mock the backend DELETE call.
		final ProductDeleteRequestBody deleteRequest = new ProductDeleteRequestBody(productId);
		when(backendService.deleteProduct(deleteRequest)).thenReturn(BackendServiceResponseBody.fromProductResponseBody(postResponse));
		final ResponseEntity<ResponseMessage> delResponseEntity = controller.deleteProduct(productId);
		final ProductResponseBody delResponseBody = checkEntityStatusAndFetchResponse(delResponseEntity, HttpStatus.OK);
		assertTrue("Request did not match response", responseMatchesDeleteRequest(deleteRequest, delResponseBody));
	}



	@Test
	public void testManyDels()
	{
		//////////////////////////////////////////////////////////////
		//  Use already prepared request and mocked response bodies //
		//////////////////////////////////////////////////////////////
		assert GOOD_DELETES.length == GOOD_POSTS.length :
								"Mismatch between #resources to be posted and #resources to be deleted.";

		final int numRequests = GOOD_DELETES.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////

			// Mock the backend POST call.
			when(backendService.postProduct(GOOD_POSTS[i]))
											.thenReturn(MOCKED_BACKEND_POST_RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GOOD_POSTS[i]);

			// Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// final ProductResponseBody postResponse = checkEntityStatusAndGetResponse(postResponseEntity, HttpStatus.OK);
			// assertTrue("Request did not match response", responseMatchesPostRequest(GOOD_POSTS[i], postResponse));

			////////////////////////////////////
			// And now we check the DEL call. //
			////////////////////////////////////

			// Mock the backend DEL call.
			when(backendService.deleteProduct(GOOD_DELETES[i]))
						.thenReturn(MOCKED_BACKEND_DELETE_RESPONSES[i]);    // You will still be getting the data from POST!

			// Call controller
			final ResponseEntity<ResponseMessage> delResponseEntity = controller.deleteProduct(GOOD_DELETES[i].getClientProductId());
			final ProductResponseBody delResponse = checkEntityStatusAndFetchResponse(delResponseEntity, HttpStatus.OK);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesDeleteRequest(GOOD_DELETES[i], delResponse));

		}
	}

}
