package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
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

import static com.company.rest.products.test.model.backend.MockedSquareServiceDeleteResponses.MOCKED_SQUARE_DELETE_RESPONSES;
import static com.company.rest.products.test.model.backend.MockedSquareServicePostResponses.MOCKED_SQUARE_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests.GOOD_DELETES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.flushRepo;
import static com.company.rest.products.test.util.TestUtil.responseMatchesDeleteRequest;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Mocked DEL request tests for {@link BackendService}.
 *
 * @see BackendServicePostTests
 * @see BackendServiceGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class BackendServiceDeleteTests
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
	public void testOneDel()
	{
		//////////////////////////////////////////////////////
		// Prepare request and Square layer response bodies //
		//////////////////////////////////////////////////////
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
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
														.availableElectronically(false)
														.availableForPickup(false)
													.build();

		final SquareServiceResponseBody mockedSquaredResponse = SquareServiceResponseBody
																	.builder()
						                                                  .name(postRequest.getName())
																		  .clientProductId(postRequest.getClientProductId())
																		  .productType(postRequest.getProductType())
						                                                  .squareItemId("#RANDOM_ITEM_ID")
						                                                  .squareItemVariationId("#RANDOM_ITEM_VAR_ID")
						                                                  .costInCents(postRequest.getCostInCents())
						                                                  .isDeleted(false)
		                                                             .build();

		/////////////////////////////////////////////////////////////////////////
		// Make the POST call that we will base the subsequent DEL request on, //
		// with appropriate mocks along the way.                               //
		/////////////////////////////////////////////////////////////////////////

		when(squareService.upsertProduct(any(ProductUpsertRequestBody.class), any(String.class))).thenReturn(mockedSquaredResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(mockedSquaredResponse);
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody postResponse = backendService.postProduct(postRequest);

		// Optionally, assess POST response.  Must have already happened in POST tests.
		// assertTrue("Request did not match response", responseMatchesPostRequest(request, response));

		//////////////
		// DEL Call //
		//////////////

		// Both the square service _and_ the repository calls need to be mocked.
		when(squareService.deleteProduct(any(LiteProduct.class))).thenReturn(mockedSquaredResponse);
		doNothing().when(repository).deleteByClientProductId(postRequest.getClientProductId());
		when(repository.findByClientProductId(postRequest.getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

		// Now, make the call and test it.
		final BackendServiceResponseBody delResponse = backendService.deleteProduct(postRequest.getClientProductId());
		assertTrue("Request did not match response", responseMatchesDeleteRequest(new ProductDeleteRequestBody(postRequest.getClientProductId()),
		                                                                                                                delResponse));
	}


	@Test
	public void testManyDels()
	{
		//////////////////////////////////////////////////////////////
		//  Use already prepared request and mocked response bodies //
		//////////////////////////////////////////////////////////////
		assert GOOD_DELETES.length == GOOD_POSTS.length :
								"Mismatch between #resources to be posteed and #resources to be deleted.";
		final int numRequests = GOOD_DELETES.length;
		for(int i = 0; i <  numRequests; i++)
		{
			/////////////////////////////////////////////////////////////////
			// Prepare LiteProduct to be returned by mocked deletion calls //
			/////////////////////////////////////////////////////////////////
			final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(MOCKED_SQUARE_DELETE_RESPONSES[i]);  // Need the POST response for the product type

			////////////////////////
			// POST request first //
			////////////////////////

			// Mock square and JPA Repo calls involved in POST
			when(squareService.upsertProduct(any(ProductUpsertRequestBody.class), any(String.class))).thenReturn(MOCKED_SQUARE_POST_RESPONSES[i]);
			when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);


			// Call backend service for POST
			final BackendServiceResponseBody postResponse = backendService.postProduct(GOOD_POSTS[i]);

			// Optionally, assess POST response. Presumably this has already happened in our POST test suite.
			// assertTrue("Mismatch in response #" + i + " (0-indexed).", responseMatchesPostRequest(GOOD_POSTS[i], postResponse));

			////////////////////////////////////////////
			// DEL request based on the previous POST //
			////////////////////////////////////////////

			// Mock the square DEL call and the repo SEARCH and DEL calls.
			when(squareService.deleteProduct(any(LiteProduct.class))).thenReturn(MOCKED_SQUARE_DELETE_RESPONSES[i]);
			doNothing().when(repository).deleteByClientProductId(any(String.class));
			when(repository.findByClientProductId(GOOD_DELETES[i].getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));

			// Perform and check the backend DEL call.
			final BackendServiceResponseBody deleteResponse = backendService.deleteProduct(GOOD_DELETES[i].getClientProductId());
			assertTrue("Request did not match response", responseMatchesDeleteRequest(GOOD_DELETES[i], deleteResponse));
		}
	}
}
