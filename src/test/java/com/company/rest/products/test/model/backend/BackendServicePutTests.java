package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
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
import static com.company.rest.products.test.model.backend.MockedSquareServicePutResponses.MOCKED_SQUARE_PUT_RESPONSES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static com.company.rest.products.test.util.TestUtil.flushRepo;
import static com.company.rest.products.test.util.TestUtil.responseMatchesUpsertRequest;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Mocked PUT request tests for {@link BackendService}.
 *
 * @see BackendServicePostTests
 * @see BackendServiceGetTests
 * @see BackendServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class BackendServicePutTests
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
	public void testOnePut()
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

		final SquareServiceResponseBody mockedSquarePostResponse = SquareServiceResponseBody
				.builder()
					.name(postRequest.getName())
					.clientProductId(postRequest.getClientProductId())
					.productType(postRequest.getProductType())
					.squareItemId("#RANDOM_ITEM_ID")
					.squareItemVariationId("#RANDOM_ITEM_VAR_ID")
					.costInCents(postRequest.getCostInCents())
					.isDeleted(false)
					.availableOnline(postRequest.getAvailableOnline())
					.availableElectronically(postRequest.getAvailableElectronically())
					.availableForPickup(postRequest.getAvailableForPickup())
				.build();

		// In this putRequest, we will change some fields and keep some others. Since PUT does NOT
		// require the ID to be in the request body but instead provides it in the URL, we will not
		// have an ID field to ensure this functionality works as asserted.
		final ProductUpsertRequestBody putRequest = ProductUpsertRequestBody.
				builder()
					.name("Culeothesis Necrosis")
					.productType("Vaporizer")
					.costInCents(15000L)
					.description("Will eat your face.")
					.labelColor("25AAAC")
					.upc("RANDOM_UPC")
					.sku("RANDOM_SKU")
					.availableOnline(true)
					.availableElectronically(false)
					.availableForPickup(false)
				.build();

		final SquareServiceResponseBody mockedSquarePutResponse = SquareServiceResponseBody
				.builder()
					.name(putRequest.getName())
					.clientProductId(postRequest.getClientProductId())
					.productType(postRequest.getProductType())
					.squareItemId("#RANDOM_ITEM_ID")
					.squareItemVariationId("#RANDOM_ITEM_VAR_ID")
					.costInCents(postRequest.getCostInCents())
					.isDeleted(false)
					.availableForPickup(putRequest.getAvailableForPickup())
					.availableOnline(putRequest.getAvailableOnline())
					.availableElectronically(putRequest.getAvailableElectronically())
					.availableOnline(putRequest.getAvailableOnline())
				.build();

		///////////
		// Mocks //
		///////////
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(mockedSquarePostResponse);
		when(repository.findByClientProductId(postRequest.getClientProductId())).thenReturn(Optional.empty());
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		when(squareService.upsertProduct(postRequest, postRequest.getClientProductId())).thenReturn(mockedSquarePostResponse);
		when(squareService.upsertProduct(putRequest, postRequest.getClientProductId())).thenReturn(mockedSquarePutResponse); // Notice how we pull the ID from the POST request...
		doNothing().when(repository).deleteByClientProductId(postRequest.getClientProductId());

		///////////////
		// Make POST //
		///////////////
		final BackendServiceResponseBody postResponse = backendService.postProduct(postRequest);
		// Optionally assess it, given that we presumably already have assessed POST in the relevant test suite.
		// assertTrue("Request did not match response", responseMatchesPostRequest(request, response));

		//////////////
		// PUT Call //
		//////////////

		// Have to re-mock LiteProductRepository.findByClientProductId() because *this* time we *want* it to return our cached product.
		when(repository.findByClientProductId(postRequest.getClientProductId())).thenReturn(Optional.of(cachedMiniProduct));
		// Now, make the call and test it.
		final BackendServiceResponseBody putResponse = backendService.putProduct(putRequest, postRequest.getClientProductId());
		assertTrue("Request did not match response", responseMatchesUpsertRequest(putRequest, putResponse, PUT));
	}


	@Test
	public void testManyPuts()
	{
		//////////////////////////////////////////////////////////////
		//  Use already prepared request and mocked response bodies //
		//////////////////////////////////////////////////////////////
		assert GOOD_PUTS.length == GOOD_POSTS.length : "Mismatch between #resources to be posted and #resources to be deleted.";
		final int numRequests = GOOD_PUTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			/////////////////////////////////////////////////////////////
			// Prepare LiteProduct to be returned by some mocked calls //
			/////////////////////////////////////////////////////////////
			final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(MOCKED_SQUARE_PUT_RESPONSES[i]);

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

			/////////////////////////////////////////////
			// PUT request meant to edit previous POST //
			/////////////////////////////////////////////

			// Mock the Square service upsert call and the repo SAVE and DEL calls.
			when(squareService.upsertProduct(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId())).thenReturn(MOCKED_SQUARE_DELETE_RESPONSES[i]);
			doNothing().when(repository).deleteByClientProductId(any(String.class));
			when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);

			// Perform and check the backend PUT call.
			final BackendServiceResponseBody putResponse = backendService.putProduct(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId());
			assertTrue("Request did not match response", responseMatchesUpsertRequest(GOOD_PUTS[i], putResponse, PUT));
		}
	}
}
