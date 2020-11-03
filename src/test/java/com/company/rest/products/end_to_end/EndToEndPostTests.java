package com.company.rest.products.end_to_end;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.sample_requests.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.util.TestUtil.getAndCheckResponse;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndPostTests
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




	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOnePost()
	{
		final ProductPostRequestBody request = ProductPostRequestBody
													.builder()
														.name("Ramses V")
														.productType("topical")
														.clientProductId("#RANDOM_ID")
														.costInCents(10000L) // 'L for long literal
														.description("Will challenge his father for the throne")
														.labelColor("7FAAAD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.availableOnline(false)
														.availableElectronically(false) // Whatever that means
														.availableForPickup(true)
													.build();

		final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		final ProductResponseBody response = getAndCheckResponse(responseEntity);
		assertTrue("Request did not match response", responseMatchesPostRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);
			final ProductResponseBody response = getAndCheckResponse(responseEntity);
			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesPostRequest(GoodPostRequests.REQUESTS[i], response));
		}
	}

}
