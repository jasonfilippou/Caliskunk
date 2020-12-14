package com.company.rest.products.test.model.backend;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
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

import static com.company.rest.products.test.model.backend.MockedSquareServicePostResponses.MOCKED_SQUARE_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
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
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
														.builder()
														.productName("Culeothesis Necrosis")
														.productType("Flower")
														.clientProductId("#RANDOM_ID")
														.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
														.description("Will eat your face.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
														.build();

		final SquareServiceResponseBody preparedResponse = SquareServiceResponseBody
																.builder()
			                                                    .name(postRequest.getProductName())
														        .clientProductId(postRequest.getClientProductId())
															    .productType(postRequest.getProductType())
			                                                    .squareItemId("#RANDOM_ITEM_ID")
																.squareItemVariationId("#RANDOM_ITEM_VAR_ID")
			                                                    .costInCents(postRequest.getCostInCents())
			                                                    .isDeleted(false)
																.updatedAt(DEFAULT_UPDATED_AT_STRING)
																.description(postRequest.getDescription())
																.labelColor(postRequest.getLabelColor())
														        .version(DEFAULT_VERSION)
																.sku(postRequest.getSku())
																.upc(postRequest.getUpc())
                                                                .build();

		when(squareService.postProduct(any(ProductUpsertRequestBody.class))).thenReturn(preparedResponse);
		final LiteProduct cachedMiniProduct = LiteProduct.fromSquareResponse(preparedResponse);
		when(repository.save(any(LiteProduct.class))).thenReturn(cachedMiniProduct);
		final BackendServiceResponseBody response = backendService.postProduct(postRequest);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(postRequest, response, POST));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Mock
			when(squareService.postProduct(any(ProductUpsertRequestBody.class))).thenReturn(MOCKED_SQUARE_POST_RESPONSES[i]);
			when(repository.save(any(LiteProduct.class))).thenReturn(LiteProduct.fromSquareResponse(MOCKED_SQUARE_POST_RESPONSES[i]));

			// Call backend service
			final BackendServiceResponseBody response = backendService.postProduct(GOOD_POSTS[i]);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesUpsertRequest(GOOD_POSTS[i], response, POST));
		}
	}
}
