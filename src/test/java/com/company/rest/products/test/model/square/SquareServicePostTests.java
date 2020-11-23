package com.company.rest.products.test.model.square;
import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.UpsertCatalogObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * POST tests for {@link SquareService}.
 *
 * @see SquareServiceGetTests
 * @see SquareServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class SquareServicePostTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private SquareService squareService; // The class we are testing

	@Mock
	private CatalogWrapper catalogWrapper;     // Class that will be mocked

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Before
    public void setUp() throws ExecutionException, InterruptedException
	{
		//  I need a way to return different kinds of expected responses depending on the arguments
		// of the methods that are being mocked. The following trick owed to:
		// https://stackoverflow.com/questions/22338536/mockito-return-value-based-on-property-of-a-parameter
		when(catalogWrapper.upsertObject(any(UpsertCatalogObjectRequest.class)))
				.then(invocation ->
				      {
					      final UpsertCatalogObjectRequest request = invocation.getArgument(0);
					      final Long version = request.getObject().getVersion() != null ?
					                           request.getObject().getVersion() : DEFAULT_VERSION_FOR_TESTS;
					      return buildItemResponseOutOfRequest(request, version);
				      });
    }

	@Test
	public void testOnePost()
	{
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
													.builder()
													.name("Culeothesis Necrosis")
													.productType("Flower")
													.clientProductId("#RANDOM_ITEM_ID")
													.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
													.description("Will eat your face.")
													.labelColor("7FFFD4")
													.upc("RANDOM_UPC")
													.sku("RANDOM_SKU")
													.build();
		// catalog calls already mocked by setUp(); we can just call the method we want to debug.
		final SquareServiceResponseBody response = squareService.postProduct(postRequest);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(postRequest, response, POST));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Call backend service
			final SquareServiceResponseBody response = squareService.postProduct(GOOD_POSTS[i]);

			// Assess response
			assertTrue("Mismatch in response #" + i, responseMatchesUpsertRequest(GOOD_POSTS[i], response, POST));
		}
	}

}
