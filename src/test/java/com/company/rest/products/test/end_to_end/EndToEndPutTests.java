package com.company.rest.products.test.end_to_end;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
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
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static org.junit.Assert.assertTrue;
/**
 * PUT end-to-end tests.
 *
 * @see EndToEndDeleteTests
 * @see EndToEndGetTests
 * @see EndToEndPutTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndPutTests
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
	public void testOnePut()
	{
		// Build request bodies
		final String productId = "#TEST_ITEM_FOR_PUT_ID";
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
				.builder()
				.name("Pengolin's Revenge")
				.productType("Vaporizer")
				.clientProductId(productId)
				.costInCents(13000L) // 'L for long literal
				.description("We're done.")
				.labelColor("7FFFD4")
				.upc("RANDOM_UPC")
				.sku("RANDOM_SKU")
				.availableOnline(true)
				.availableElectronically(true)
				.availableForPickup(false)
				.build();

		final ProductUpsertRequestBody putRequest = ProductUpsertRequestBody    // Some fields different, but not all
				.builder()
				.name("Pengolin's Revenge II")
				.productType("Topical")
		        .description("Now in topical form!")
				.upc("RANDOM_UPC")          // Can very well define a field and input the same value if client wants.
				.sku("RANDOM_SKU")
				.sku("RANDOM_SKU_UPDATED")  // Builder should allow client to change their mind.
				.availableForPickup(true)
				.build();

		// Do the POST first, so that we can update it afterwards with the PUT.
		final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(postRequest);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding PUT, and ensure it works.
		final ResponseEntity<ResponseMessage> putResponseEntity = controller.putProduct(putRequest, productId);
		final ProductResponseBody putResponse = checkEntityStatusAndFetchResponse(putResponseEntity, HttpStatus.OK);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(putRequest, putResponse, PUT));
	}

	@Test
	public void testManyPuts()
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
