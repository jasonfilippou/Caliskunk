package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.post.MockedSquareServicePostResponses;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import org.aspectj.lang.annotation.Aspect;
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

import static com.company.rest.products.test.util.TestUtil.flushRepo;
import static com.company.rest.products.test.util.TestUtil.responseMatchesUpsertRequest;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * Mocked tests for {@link BackendService}.
 *
 * @see BackendServiceGetTests
 * @see BackendServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
@Aspect
public class BackendServicePostTests
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
	public void testOnePost()
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

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																	.builder()
						                                                  .name(request.getName())
						                                                  .squareItemId("#RANDOM_ITEM_ID")
						                                                  .squareItemVariationId("RANDOM_ITEM_VAR_ID")
						                                                  .costInCents(request.getCostInCents())
						                                                  .isDeleted(false)
		                                                             .build();

		when(squareService.upsertProduct(any(ProductUpsertRequestBody.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.buildLiteProductFromSquareResponse(preparedResponse, request.getClientProductId(),
		                                                                                     request.getProductType());
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody response = backendService.postProduct(request);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(request, response));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(squareService.upsertProduct(any(ProductUpsertRequestBody.class)))
					.thenReturn(MockedSquareServicePostResponses.RESPONSES[i]);

			// Call backend service
			final BackendServiceResponseBody response = backendService.postProduct(GoodPostRequests.REQUESTS[i]);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesUpsertRequest(GoodPostRequests.REQUESTS[i], response));
		}
	}
}
