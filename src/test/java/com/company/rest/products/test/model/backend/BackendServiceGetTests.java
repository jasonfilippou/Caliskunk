package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
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

import static com.company.rest.products.test.model.backend.MockedSquareServiceGetResponses.MOCKED_SQUARE_GET_RESPONSES;
import static com.company.rest.products.test.model.backend.MockedSquareServicePostResponses.MOCKED_SQUARE_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.get.GoodGetRequests.GOOD_GETS;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION_ID_FOR_MOCKS;
import static com.company.rest.products.test.util.TestUtil.flushRepo;
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

		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("flower")
														.clientProductId("#RANDOM_ID")
														.costInCents(10000L) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.availableOnline(false)
														.availableElectronically(false)
														.availableForPickup(false)
													.build();

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																	.builder()
						                                                  .name(request.getName())
																		  .clientProductId(request.getClientProductId())
																		  .productType(request.getProductType())
						                                                  .squareItemId("#RANDOM_ITEM_ID")
						                                                  .costInCents(request.getCostInCents())
																		  .version(DEFAULT_VERSION_ID_FOR_MOCKS)
						                                                  .isDeleted(false)
		                                                             .build();

		/////////////////////////////////////////////////////////////////////////
		// Make the POST call that we will base the subsequent GET request on, //
		// with appropriate mocks along the way.                               //
		/////////////////////////////////////////////////////////////////////////

		when(squareService.upsertProduct(any(ProductUpsertRequestBody.class), any(String.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(preparedResponse);
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody postResponse = backendService.postProduct(request);

		// Optionally, run the following test, which actually subsumes the last one, but is slower.
		// assertTrue("Request did not match response", responseMatchesPostRequest(request, response));

		//////////////
		// GET Call //
		//////////////

		// Both the square service _and_ the repository call need to be mocked.
		when(squareService.getProduct(any(LiteProduct.class))).thenReturn(preparedResponse);
		when(repository.findByClientProductId(request.getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

		// Now, make the call and test it.
		final BackendServiceResponseBody getResponse = backendService.getProduct(new ProductGetRequestBody(request.getClientProductId()));
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

		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{

			////////////////////////
			// POST request first //
			////////////////////////

			// Mock
			when(squareService.upsertProduct(any(ProductUpsertRequestBody.class), any(String.class)))
					.thenReturn(MOCKED_SQUARE_POST_RESPONSES[i]);

			// Call backend service for POST
			final BackendServiceResponseBody postResponse = backendService.postProduct(GOOD_POSTS[i]);

			// Optionally, assess POST response (which actually subsumes the GET response that follows)
			// assertTrue("Mismatch in response #" + i + " (0-indexed).", responseMatchesPostRequest(GOOD_POSTS[i], postResponse));

			////////////////////////////////////////////
			// GET request based on the previous POST //
			////////////////////////////////////////////

			// Mock the square GET call
			// when(squareService.getProduct(postResponse.getSquareItemId(), postResponse.getSquareItemVariationId()))
			when(squareService.getProduct(any(LiteProduct.class))).thenReturn(MOCKED_SQUARE_GET_RESPONSES[i]);
			// And the repo call
			final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(MOCKED_SQUARE_GET_RESPONSES[i]);
			when(repository.findByClientProductId(GOOD_POSTS[i].getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

			// Perform and check the GET
			final BackendServiceResponseBody getResponse = backendService.getProduct(GOOD_GETS[i]);
			assertTrue("Request did not match response", responseMatchesGetRequest(new ProductGetRequestBody
				                                                                            (GOOD_GETS[i].getClientProductId()),
		                                                                                        getResponse));
		}
	}
}
