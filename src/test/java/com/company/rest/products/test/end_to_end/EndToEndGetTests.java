package com.company.rest.products.test.end_to_end;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.requests_responses.get.GoodGetRequests;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import lombok.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.util.TestUtil.checkEntityStatusAndFetchResponse;
import static com.company.rest.products.test.util.TestUtil.flushRepo;
import static org.junit.Assert.assertTrue;


/**
 * End-to-end GET tests.
 *
 * This unit test suite is actually subsumed by {@link EndToEndPostTests}.
 *
 * @see EndToEndPostTests
 * @see EndToEndDeleteTests
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

	@Autowired
	private LiteProductRepository repository;


	private boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                           @NonNull ProductResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}


	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Before
	public void setUp()
	{
		flushRepo(repository);
	}

	@After
	public void tearDown()
	{
		flushRepo(repository);
	}

	@Test
	public void testOneGet()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_GET_ID";
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding GET..
		final ProductGetRequestBody request = new ProductGetRequestBody(productId);

		final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(productId);
		final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);
		assertTrue("Request did not match response", responseMatchesGetRequest(request, getResponse));
	}

	private ResponseEntity<ResponseMessage> makeAPost(final String clientProductId)
	{
		// Make post request
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
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

			//Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// assertTrue("Request did not match response", responseMatchesPostRequest(GoodPostRequests.REQUESTS[i],  postResponse));

			///////////////////////////////////
			// And now we check the GET call.//
			///////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(GoodGetRequests.REQUESTS[i].getClientProductId());
			final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesGetRequest(GoodGetRequests.REQUESTS[i], getResponse));

		}
	}

}
