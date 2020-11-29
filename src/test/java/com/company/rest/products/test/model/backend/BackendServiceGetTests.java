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
import static com.company.rest.products.test.util.TestUtil.*;
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

		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
													.builder()
														.name("Culeothesis Necrosis")
														.productType("flower")
														.clientProductId("#RANDOM_ID")
														.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
													.build();

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																	.builder()
					                                                  .name(postRequest.getName())
																	  .clientProductId(postRequest.getClientProductId())
																	  .productType(postRequest.getProductType())
					                                                  .squareItemId("#RANDOM_ITEM_ID")
																	  .squareItemVariationId("#RANDOM_ITEM_VAR_ID")
					                                                  .costInCents(postRequest.getCostInCents())
																	  .version(DEFAULT_VERSION_FOR_TESTS)
					                                                  .isDeleted(false)
																	  .updatedAt(DEFAULT_UPDATED_AT_STRING)
																	  .upc(postRequest.getUpc())
																	  .sku(postRequest.getSku())
																	  .labelColor(postRequest.getLabelColor())
																	  .description(postRequest.getDescription())
		                                                              .build();

		/////////////////////////////////////////////////////////////////////////
		// Make the POST call that we will base the subsequent GET request on, //
		// with appropriate mocks along the way.                               //
		/////////////////////////////////////////////////////////////////////////

		when(squareService.postProduct(any(ProductUpsertRequestBody.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(preparedResponse);
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody postResponse = backendService.postProduct(postRequest);

		//////////////
		// GET Call //
		//////////////

		// Both the square service _and_ the repository call need to be mocked.
		when(squareService.getProduct(any(ProductGetRequestBody.class))).thenReturn(preparedResponse);
		when(repository.findByClientProductId(postRequest.getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

		// Now, make the call and test it.
		final ProductGetRequestBody getRequest  = new ProductGetRequestBody(postRequest.getClientProductId());
		final BackendServiceResponseBody getResponse = backendService.getProduct(new ProductGetRequestBody(postRequest.getClientProductId()));
		assertTrue("Request did not match response", responseMatchesGetRequest(new ProductGetRequestBody(postRequest.getClientProductId(), cachedMiniProduct),
		                                                                       getResponse));   // Need a new get request with the liteProduct component to compare with the square call.
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
			when(squareService.postProduct(any(ProductUpsertRequestBody.class))).thenReturn(MOCKED_SQUARE_POST_RESPONSES[i]);

			// Call backend service for POST
			final BackendServiceResponseBody postResponse = backendService.postProduct(GOOD_POSTS[i]);

			////////////////////////////////////////////
			// GET request based on the previous POST //
			////////////////////////////////////////////

			// Mock the square GET call
			when(squareService.getProduct(any(ProductGetRequestBody.class))).thenReturn(MOCKED_SQUARE_GET_RESPONSES[i]);

			// And the repo call
			final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(MOCKED_SQUARE_GET_RESPONSES[i]);
			when(repository.findByClientProductId(GOOD_POSTS[i].getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

			// Perform and check the GET
			final BackendServiceResponseBody getResponse = backendService.getProduct(GOOD_GETS[i]);
			assertTrue("Request did not match response", responseMatchesGetRequest(new ProductGetRequestBody
				                                                                            (GOOD_GETS[i].getClientProductId(), cachedMiniProduct),
		                                                                                        getResponse));  // Need a new get request with the liteProduct component to compare with the square call.
		}
	}
}
