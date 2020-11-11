package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.requests_responses.get.GoodGetRequests;
import com.company.rest.products.test.requests_responses.get.MockedSquareServiceGetResponses;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.post.MockedSquareServicePostResponses;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import lombok.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.company.rest.products.test.util.TestUtil.flushRepo;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * Mocked GET request tests for {@link BackendService}.
 *
 * This unit test suite is actually subsumed by {@link BackendServicePostTests}.
 *
 * @see BackendServicePostTests
 * @see BackendServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class BackendServiceGetTests
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

	private boolean responseMatchesGetRequest(@NonNull final ProductGetRequestBody postRequestBody,
	                                           @NonNull final BackendServiceResponseBody responseBody)
	{
		return	postRequestBody.getClientProductId().equals(responseBody.getClientProductId());
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
		//////////////////////////////////////////////////////
		// Prepare request and Square layer response bodies //
		//////////////////////////////////////////////////////

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
														.availableOnline(false)
														.availableElectronically(false) // Whatever that means
														.availableForPickup(false)
													.build();

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																	.builder()
						                                                  .name(request.getName())
						                                                  .squareItemId("#RANDOM_ITEM_ID")
						                                                  .squareItemVariationId("#RANDOM_ITEM_VAR_ID")
						                                                  .costInCents(request.getCostInCents())
						                                                  .isDeleted(false)
		                                                             .build();

		/////////////////////////////////////////////////////////////////////////
		// Make the POST call that we will base the subsequent GET request on, //
		// with appropriate mocks along the way.                               //
		/////////////////////////////////////////////////////////////////////////

		when(squareService.postProduct(any(ProductPostRequestBody.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(preparedResponse, request.getClientProductId(),
		                                                                                     request.getProductType());
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody postResponse = backendService.postProduct(request);

		// Optionally, run the following test, which actually subsumes the last one, but is slower.
		// assertTrue("Request did not match response", responseMatchesPostRequest(request, response));

		//////////////
		// GET Call //
		//////////////

		// Both the square service _and_ the repository call need to be mocked.
		when(squareService.getProduct(postResponse.getSquareItemId(),
		                              postResponse.getSquareItemVariationId())).thenReturn(preparedResponse);
		when(repository.findByClientProductId(request.getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

		// Now, make the call and test it.
		final BackendServiceResponseBody getResponse = backendService.getProduct(request.getClientProductId());
		assertTrue("Request did not match response", responseMatchesGetRequest(new ProductGetRequestBody
				                                                                            (request.getClientProductId()),
		                                                                                        getResponse));
	}

	@Test
	public void testManyGets()
	{
		/////////////////////////////////////////////////////////////////////////
		/////// Use already prepared request and mocked response bodies /////////
		/////////////////////////////////////////////////////////////////////////

		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{

			////////////////////////
			// POST request first //
			////////////////////////

			// Mock
			when(squareService.postProduct(any(ProductPostRequestBody.class)))
					.thenReturn(MockedSquareServicePostResponses.RESPONSES[i]);

			// Call backend service for POST
			final BackendServiceResponseBody postResponse = backendService.postProduct(GoodPostRequests.REQUESTS[i]);

			// Optionally, assess POST response (which actually subsumes the GET response that follows)
			// assertTrue("Mismatch in response #" + i + " (0-indexed).", responseMatchesPostRequest(GoodPostRequests.REQUESTS[i], postResponse));

			////////////////////////////////////////////
			// GET request based on the previous POST //
			////////////////////////////////////////////

			// Mock the square GET call
			// when(squareService.getProduct(postResponse.getSquareItemId(), postResponse.getSquareItemVariationId()))
			when(squareService.getProduct(any(String.class), any(String.class))).thenReturn(MockedSquareServiceGetResponses.RESPONSES[i]);
			// And the repo call
			final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(MockedSquareServiceGetResponses.RESPONSES[i],
			                                                                                     GoodPostRequests.REQUESTS[i].getClientProductId(),
			                                                                                     GoodPostRequests.REQUESTS[i].getProductType());
			when(repository.findByClientProductId(GoodPostRequests.REQUESTS[i].getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

			// Perform and check the GET
			final BackendServiceResponseBody getResponse = backendService.getProduct(GoodGetRequests.REQUESTS[i].getClientProductId());
			assertTrue("Request did not match response", responseMatchesGetRequest(new ProductGetRequestBody
				                                                                            (GoodGetRequests.REQUESTS[i].getClientProductId()),
		                                                                                        getResponse));
		}
	}
}
