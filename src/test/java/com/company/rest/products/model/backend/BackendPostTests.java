package com.company.rest.products.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.requests_responses.post.MockedSquareServicePostResponses;
import com.company.rest.products.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class BackendPostTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private BackendService backendService; // The class we are testing

	@Mock
	private SquareService squareService;     // One class that will be mocked

	@Mock
	private LiteProductRepository repository;     // Another class that will be mocked


	private boolean responseMatchesPostRequest(@NonNull final ProductPostRequestBody postRequestBody,
	                                           @NonNull final BackendServiceResponseBody responseBody)
	{
		return
				// Basic data that will always be provided:
				postRequestBody.getName().equals(responseBody.getName()) &&
				postRequestBody.getProductType().equals(responseBody.getProductType()) &&
				postRequestBody.getCostInCents().equals(responseBody.getCostInCents()) &&
				postRequestBody.getClientProductId().equals(responseBody.getClientProductId()) &&

				// Subsequent fields that may or may not have been provided, so we
				// use an Optional to protect ourselves against NPEs:
				ofNullable(postRequestBody.getAvailableElectronically()).equals(ofNullable(responseBody.getAvailableElectronically()) ) &&
				ofNullable(postRequestBody.getAvailableForPickup()).equals(ofNullable(responseBody.getAvailableForPickup()) ) &&
				ofNullable(postRequestBody.getAvailableOnline()).equals(ofNullable(responseBody.getAvailableOnline()) ) &&
				ofNullable(postRequestBody.getLabelColor()).equals(ofNullable(responseBody.getLabelColor()) ) &&
				ofNullable(postRequestBody.getDescription()).equals(ofNullable(responseBody.getDescription()) ) &&
				ofNullable(postRequestBody.getSku()).equals(ofNullable(responseBody.getSku()) ) &&
				ofNullable(postRequestBody.getUpc()).equals(ofNullable(responseBody.getUpc())) &&

				// Let us also ensure that the POST didn't trip the object's deletion flag:
				(responseBody.getIsDeleted() == null) || (!responseBody.getIsDeleted());

	}

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOnePost()
	{
		final ProductPostRequestBody request = ProductPostRequestBody
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

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																	.builder()
						                                                  .name(request.getName())
						                                                  .squareItemId("#RANDOM_ITEM_ID")
						                                                  .squareItemVariationId("RANDOM_ITEM_VAR_ID")
						                                                  .costInCents(request.getCostInCents())
						                                                  .isDeleted(false)
		                                                             .build();

		when(squareService.postProduct(any(ProductPostRequestBody.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(preparedResponse, request.getClientProductId(),
		                                                                                     request.getProductType());
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody response = backendService.postProduct(request);
		assertTrue("Request did not match response", responseMatchesPostRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(squareService.postProduct(any(ProductPostRequestBody.class)))
					.thenReturn(MockedSquareServicePostResponses.RESPONSES[i]);

			// Call backend service
			final BackendServiceResponseBody response = backendService.postProduct(GoodPostRequests.REQUESTS[i]);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesPostRequest(GoodPostRequests.REQUESTS[i], response));
		}
	}
}
