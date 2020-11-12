package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.test.model.square.SquareServiceGetTests;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.post.MockedBackendServicePostResponses;
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

import static com.company.rest.products.test.util.TestUtil.checkEntityStatusAndFetchResponse;
import static com.company.rest.products.test.util.TestUtil.responseMatchesUpsertRequest;
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
public class ControllerPutTests
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
	public void testOnePut()
	{
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("Flower")
														.clientProductId("#RANDOM_ID")
														.costInCents(10000L) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.availableOnline(true)
														.availableElectronically(true) // Whatever that means
														.availableForPickup(true)
													.build();

		final BackendServiceResponseBody preparedPostResponse = BackendServiceResponseBody
														.builder()
	                                                        .name(request.getName())
	                                                        .clientProductId(request.getClientProductId())
															.squareItemId("#RANDOM_SQUARE_ITEM_ID")
	                                                        .squareItemVariationId("#RANDOM_SQUARE_ITEM_VAR_ID")
	                                                        .productType(request.getProductType())
	                                                        .costInCents(request.getCostInCents())
	                                                        .availableElectronically(request.getAvailableElectronically())
															.availableForPickup(request.getAvailableForPickup())
															.availableOnline(request.getAvailableOnline())
															.isDeleted(false)
															.sku(request.getSku())
															.upc(request.getUpc())
															.description(request.getDescription())
															.labelColor(request.getLabelColor())
															.presentAtAllLocations(true)
                                                          .build();

		when(backendService.postProduct(request)).thenReturn(preparedPostResponse);
		final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		final ProductResponseBody response = checkEntityStatusAndFetchResponse(responseEntity, HttpStatus.CREATED);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(request, response));
	}

	@Test
	public void testManyPuts()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(backendService.postProduct(GoodPostRequests.REQUESTS[i]))
				.thenReturn(MockedBackendServicePostResponses.RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GoodPostRequests.REQUESTS[i]);
			final ProductResponseBody response = checkEntityStatusAndFetchResponse(responseEntity, HttpStatus.CREATED);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesUpsertRequest(GoodPostRequests.REQUESTS[i], response));
		}
	}

}
