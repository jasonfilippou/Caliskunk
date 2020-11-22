package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.test.model.square.SquareServiceGetTests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

import static com.company.rest.products.test.controller.MockedBackendServicePostResponses.MOCKED_BACKEND_POST_RESPONSES;
import static com.company.rest.products.test.controller.MockedBackendServicePutResponses.MOCKED_BACKEND_PUT_RESPONSES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
public class ControllerPutTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private ProductController controller;       // The class we are testing

	@Mock
	private BackendService backendService;     // The class that will be mocked

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOnePut()
	{
		// Prepare request bodies.
		final String id = "#RANDOM_ID";
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
													.builder()
													.name("Culeothesis Necrosis")
													.productType("Flower")
													.clientProductId(id)
													.costInCents(10000L) // 'L for long literal
													.description("Will eat your face.")
													.labelColor("7FFFD4")
													.upc("RANDOM_UPC")
													.sku("RANDOM_SKU")
													.build();

		final ProductUpsertRequestBody putRequest = ProductUpsertRequestBody // No client ID, since we give that separately
													.builder()
													.name("Geez Louise OG")
													.productType("Vaporizer")
													.costInCents(15000L) // 'L for long literal
													.description("Will also eat your face.")
													.labelColor("8AFA94")
													.upc("NEW_RANDOM_UPC")
													.version(DEFAULT_VERSION_FOR_TESTS)
													.sku("NEW_RANDOM_SKU")
													.build();

		// Prepare mocked backend layer responses.
		final BackendServiceResponseBody preparedPostResponse = BackendServiceResponseBody
														.builder()
                                                        .name(postRequest.getName().strip().toUpperCase())
                                                        .clientProductId(id)
														.squareItemId("#RANDOM_SQUARE_ITEM_ID")
                                                        .productType(postRequest.getProductType())
                                                        .costInCents(postRequest.getCostInCents())
														.isDeleted(false)
														.sku(postRequest.getSku())
														.upc(postRequest.getUpc())
														.updatedAt(DEFAULT_UPDATED_AT_STRING)
														.description(postRequest.getDescription())
														.labelColor(postRequest.getLabelColor())
														.version(DEFAULT_VERSION_FOR_TESTS)
                                                        .build();

		final BackendServiceResponseBody preparedPutResponse = BackendServiceResponseBody
														.builder()
                                                        .name(putRequest.getName().strip().toUpperCase())
                                                        .clientProductId(id)
														.squareItemId("#RANDOM_SQUARE_ITEM_ID")
                                                        .productType(putRequest.getProductType())
                                                        .costInCents(putRequest.getCostInCents())
														.isDeleted(false)
														.sku(putRequest.getSku())
														.updatedAt(DEFAULT_UPDATED_AT_STRING)
														.upc(putRequest.getUpc())
														.description(putRequest.getDescription())
														.labelColor(putRequest.getLabelColor())
														.version(DEFAULT_VERSION_FOR_TESTS)
                                                        .build();

		// Mock backend layer calls appropriately.
		when(backendService.postProduct(postRequest)).thenReturn(preparedPostResponse);
		when(backendService.putProduct(putRequest)).thenReturn(preparedPutResponse);

		// Perform POST
		final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(postRequest);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		final ResponseEntity<ResponseMessage> putResponseEntity = controller.putProduct(putRequest, id);
		final ProductResponseBody putResponse = checkEntityStatusAndFetchResponse(putResponseEntity, HttpStatus.OK);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(putRequest, putResponse, PUT));
	}

	@Test
	public void testManyPuts()
	{
		// Length assertion
		assertEquals("Mismatch between lengths of input arrays.", 1,
		             Stream.of(GOOD_POSTS.length, GOOD_PUTS.length, MOCKED_BACKEND_POST_RESPONSES.length,
		                       MOCKED_BACKEND_PUT_RESPONSES.length).distinct().count());
		// Main loop
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock backend layer POST and PUT calls.
			when(backendService.postProduct(GOOD_POSTS[i])).thenReturn(MOCKED_BACKEND_POST_RESPONSES[i]);
			when(backendService.putProduct(any(ProductUpsertRequestBody.class))).thenReturn(MOCKED_BACKEND_PUT_RESPONSES[i]);

			// Send controller a POST, optionally assessing it (we have already covered POST tests).
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GOOD_POSTS[i]);
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

			// Send controller a PUT, assess its results.
			final ResponseEntity<ResponseMessage> putResponseEntity = controller.putProduct(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId());
			final ProductResponseBody putResponse = checkEntityStatusAndFetchResponse(putResponseEntity, HttpStatus.OK);
			assertTrue("Mismatch in response #" + i + " (0-indexed).",  responseMatchesUpsertRequest(GOOD_PUTS[i], putResponse, PUT));
		}
	}

}
