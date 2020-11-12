package com.company.rest.products.test.end_to_end;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
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
import static java.util.Optional.ofNullable;
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

	private boolean responseMatchesDeleteRequest(final ProductDeleteRequestBody delRequestBody,
	                                             final ProductResponseBody responseBody)
	{
		return	delRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}



	private boolean responseMatchesPostRequest(@NonNull ProductUpsertRequestBody postRequestBody,
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
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding DELETE, and ensure it works. Mock the backend DELETE call.
		final ProductDeleteRequestBody deleteRequest = new ProductDeleteRequestBody(productId);
		final ResponseEntity<ResponseMessage> delResponseEntity = controller.deleteProduct(productId);
		final ProductResponseBody delResponseBody = checkEntityStatusAndFetchResponse(delResponseEntity, HttpStatus.OK);
		assertTrue("Request did not match response", responseMatchesDeleteRequest(deleteRequest, delResponseBody));
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
	public void testManyDels()
	{
		//////////////////////////////////////////////////////////////
		//  Use already prepared request and mocked response bodies //
		//////////////////////////////////////////////////////////////
		assert GoodDeleteRequests.REQUESTS.length == GoodPostRequests.REQUESTS.length :
								"Mismatch between #resources to be posteed and #resources to be deleted.";

		final int numRequests = GoodDeleteRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);

			// Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// final ProductResponseBody postResponse = checkEntityStatusAndGetResponse(postResponseEntity, HttpStatus.OK);
			// assertTrue("Request did not match response", responseMatchesPostRequest(GoodPostRequests.REQUESTS[i], postResponse));

			////////////////////////////////////
			// And now we check the DEL call. //
			////////////////////////////////////

			// Call controller
			final ResponseEntity<ResponseMessage> delResponseEntity = controller.deleteProduct(GoodDeleteRequests.REQUESTS[i].getClientProductId());
			final ProductResponseBody delResponse = checkEntityStatusAndFetchResponse(delResponseEntity, HttpStatus.OK);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesDeleteRequest(GoodDeleteRequests.REQUESTS[i], delResponse));
		}
	}

}
