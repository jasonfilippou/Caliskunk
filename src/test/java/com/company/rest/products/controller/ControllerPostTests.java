package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.sample_jsons.post.ExpectedPostResponses;
import com.company.rest.products.model.sample_jsons.post.GoodPostRequests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.json_objects.BackendResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.ProductResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

	@Autowired
	private ProductController controller; // The class we are testing

	@MockBean
	private BackendService service;     // The class that will be mocked



	private boolean responseMatchesPostRequest(@NonNull ProductPostRequestBody postRequestBody,
	                                           @NonNull ProductResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				postRequestBody.getName().equals(responseBody.getName()) &&
				postRequestBody.getProductType().equals(responseBody.getProductType()) &&
				postRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&

				// Subsequent fields that may or may not have been provided, so we
				// use an Optional to protect ourselves against NPEs:
				ofNullable(postRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically()) ) &&
				ofNullable(postRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup()) ) &&
				ofNullable(postRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline()) ) &&
				ofNullable(postRequestBody.getCategoryId()).equals(ofNullable(responseBody.getCategoryId()) ) &&
				ofNullable(postRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor()) ) &&
				ofNullable(postRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription()) ) &&
				ofNullable(postRequestBody.getSku()).equals(ofNullable(responseBody.getSku()) ) &&
				ofNullable(postRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc()))

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				&&  ! responseBody.getIsDeleted();
	}


	private ProductResponseBody getResponseData(ResponseEntity<ResponseMessage> responseEntity)
	{
		return (ProductResponseBody) Objects.requireNonNull(responseEntity.getBody()).getData();
	}

	/* *********************************************************************************************************** */
	/* ************************************ Tests ************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testGetAll()
	{

	}


	@Test
	public void testGetOne()
	{

	}


	@Test
	public void testPost()
	{
		final ProductPostRequestBody request = ProductPostRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("Flower")
														.costInCents(600L) // 'L for long literal
													.build();

		BackendResponseBody expected = BackendResponseBody.builder()
									                         .name(request.getName())
		                                                     .itemId("RANDOM_ITEM_ID")
		                                                     .itemVarId("RANDOM_ITEM_VAR_ID")
									                         .productType(request.getProductType())
									                         .costInCents(request.getCostInCents())
									                         .isDeleted(false)
									                      .build();

		when(service.postProduct(request)).thenReturn(expected);

		ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		ProductResponseBody response = getResponseData(responseEntity);
		assertTrue("Request did not match response", responseMatchesPostRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		int numRequests = GoodPostRequests.POST_REQUESTS.length,
			numResponses = ExpectedPostResponses.RESPONSES.length;
		assertEquals("Mismatch between number of requests: " + numRequests
		             + " and number of expected responses: " + numResponses, numRequests, numResponses);
		for(int i = 0; i <  numRequests; i++)
		{
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesPostRequest(GoodPostRequests.POST_REQUESTS[i],  ExpectedPostResponses.RESPONSES[i]));
		}
	}


	@Test
	public void testPut()
	{

	}


	@Test
	public void testPatch()
	{

	}

}
