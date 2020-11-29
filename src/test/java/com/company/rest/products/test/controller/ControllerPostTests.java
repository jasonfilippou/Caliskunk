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

import static com.company.rest.products.test.controller.MockedBackendServicePostResponses.MOCKED_BACKEND_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static org.junit.Assert.assertTrue;
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
public class ControllerPostTests
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
	public void testOnePost()
	{
		final ProductUpsertRequestBody postRequest  = ProductUpsertRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("Flower")
														.clientProductId("#RANDOM_ID")
														.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
													.build();

		final BackendServiceResponseBody preparedResponse = BackendServiceResponseBody
														.builder()
                                                        .name(postRequest.getName())
                                                        .clientProductId(postRequest.getClientProductId())
														.squareItemId("#RANDOM_SQUARE_ITEM_ID")
                                                        .productType(postRequest.getProductType())
                                                        .costInCents(postRequest.getCostInCents())
														.isDeleted(false)
														.updatedAt(DEFAULT_UPDATED_AT_STRING)
														.sku(postRequest.getSku())
														.upc(postRequest.getUpc())
														.description(postRequest.getDescription())
														.labelColor(postRequest.getLabelColor())
														.version(DEFAULT_VERSION_FOR_TESTS)    // Random Long
														.build();

		when(backendService.postProduct(postRequest)).thenReturn(preparedResponse);
		final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(postRequest);
		final ProductResponseBody response = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(postRequest, response, POST));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(backendService.postProduct(GOOD_POSTS[i])).thenReturn(MOCKED_BACKEND_POST_RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GOOD_POSTS[i]);
			final ProductResponseBody response = checkEntityStatusAndFetchResponse(responseEntity, HttpStatus.CREATED);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).", responseMatchesUpsertRequest(GOOD_POSTS[i], response, POST));
		}
	}

}
