package com.company.rest.products.test.end_to_end;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests.GOOD_DELETES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static org.junit.Assert.assertTrue;

/**
 * DELETE end-to-end tests.
 *
 * @see EndToEndPostTests
 * @see EndToEndGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndDeleteTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@Autowired
	private ProductController controller;

	@Autowired
	private LiteProductRepository repository;

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
	public void testOneDel()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_DEL_ID";
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId, controller);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding DELETE, and ensure it works. Mock the backend DELETE call.
		final ProductDeleteRequestBody deleteRequest = new ProductDeleteRequestBody(productId);
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
								"Mismatch between #resources to be posteed and #resources to be deleted.";

		final int numRequests = GOOD_DELETES.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GOOD_POSTS[i]);

			// Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// final ProductResponseBody postResponse = checkEntityStatusAndGetResponse(postResponseEntity, HttpStatus.OK);
			// assertTrue("Request did not match response", responseMatchesPostRequest(GOOD_POSTS[i], postResponse));

			////////////////////////////////////
			// And now we check the DEL call. //
			////////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> delResponseEntity = controller.deleteProduct(GOOD_DELETES[i].getClientProductId());
			final ProductResponseBody delResponse = checkEntityStatusAndFetchResponse(delResponseEntity, HttpStatus.OK);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesDeleteRequest(GOOD_DELETES[i], delResponse));
		}
	}

}
