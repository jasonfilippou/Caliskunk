package com.company.rest.products.end_to_end;

import com.company.rest.products.controller.ProductController;

import com.company.rest.products.model.backend.BackendGetTests;
import com.company.rest.products.model.backend.BackendPostTests;
import com.company.rest.products.requests_responses.get.GoodGetRequests;

import com.company.rest.products.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.util.TestUtil.checkEntityStatusAndFetchResponse;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;


/**
 * GET request tests for Controller.
 *
 * This unit test suite is actually subsumed by {@link BackendPostTests}.
 *
 * @see com.company.rest.products.controller.ControllerGetTests
 * @see com.company.rest.products.model.square.SquareGetTests
 * @see BackendGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndGetTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@Autowired
	private ProductController controller;


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
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.OK);

		// Now do the corresponding GET..
		final ProductGetRequestBody request = new ProductGetRequestBody(productId);

		final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(productId);
		final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.OK);
		assertTrue("Request did not match response", responseMatchesGetRequest(request, getResponse));
	}

	private ResponseEntity<ResponseMessage> makeAPost(final String clientProductId)
	{
		// Make post request
		final ProductPostRequestBody request = ProductPostRequestBody
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

		// Make the call to the controller
		return controller.postProduct(request);
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


			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.OK);

			//Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// assertTrue("Request did not match response", responseMatchesPostRequest(GoodPostRequests.REQUESTS[i],  postResponse));

			///////////////////////////////////
			// And now we check the GET call.//
			///////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(GoodGetRequests.REQUESTS[i].getClientProductId());
			final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.OK);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesGetRequest(GoodGetRequests.REQUESTS[i], getResponse));

		}
	}

}
