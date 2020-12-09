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
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * PUT tests for {@link SquareService}.
 *
 * @see SquareServiceGetTests
 * @see SquareServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class SquareServicePutTests
{
	@InjectMocks
	private SquareService squareService; // The class we are testing

	@Mock
	private CatalogWrapper catalogWrapper;  // Class that will be mocked

	@Before
	public void setUp() throws ExecutionException, InterruptedException
	{

		// Prepare CatalogWrapper response for POST...
		when(catalogWrapper.postObject(any(UpsertCatalogObjectRequest.class)))
				.then(invocation ->
				      {
					      final UpsertCatalogObjectRequest request = invocation.getArgument(0);
					      return buildItemResponseOutOfRequest(request, DEFAULT_VERSION, POST);
				      });

		// ... and PUT.
		when(catalogWrapper.putObject(any(UpsertCatalogObjectRequest.class)))
				.then(invocation ->
				      {
					      final UpsertCatalogObjectRequest request = invocation.getArgument(0);
					      return buildItemResponseOutOfRequest(request, request.getObject().getVersion(), PUT);
				      });
	}


	@Test
	public void testOnePut()
	{
		// Prepare requests
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
				.builder()
				.productName("Culeothesis Necrosis")
				.productType("Flower")
				.clientProductId("#RANDOM_ITEM_ID")
				.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
				.description("Will eat your face.")
				.labelColor("7FFFD4")
				.upc("RANDOM_UPC")
				.sku("RANDOM_SKU")
				.build();

		// Make the POST
		final SquareServiceResponseBody postResponse = squareService.postProduct(postRequest);

		final ProductUpsertRequestBody putRequest = ProductUpsertRequestBody.builder()
		                                                                    .productName("Culeothesis Necrosis OG")
		                                                                    .clientProductId(postResponse.getClientProductId())
		                                                                    .labelColor("AB8235")
		                                                                    .upc("RANDOM_UPC")  // Not quite updating, but still valid to have set
		                                                                    .sku("RANDOM_UPDATED_SKU")
		                                                                    .squareItemId("SOME_RANDOM_ITEM_ID")    // Needed to be supplied here because Square service assumes it.
		                                                                    .squareItemVariationId("SOME_RANDOM_ITEM_VAR_ID")
		                                                                    .version(postResponse.getVersion())
		                                                                    .costInCents(2 * DEFAULT_COST_IN_CENTS)
		                                                                    .build();

		// Make the Square Service PUT call and test it.
		final SquareServiceResponseBody putResponse = squareService.putProduct(putRequest);
		assertTrue("Bad PUT response from Square layer", responseMatchesUpsertRequest(putRequest, putResponse, PUT));
	}

	@Test
	public void testManyPuts()
	{
		// Requests already prepared
		assert GOOD_POSTS.length == GOOD_PUTS.length : "Mismatch between number of POST and corresponding PUT requests";
		final int numRequests = GOOD_PUTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Make Square Service POST call and retrieve response
			final SquareServiceResponseBody postResponse = squareService.postProduct(GOOD_POSTS[i]);

			// Perform PUT and assess it.
			GOOD_PUTS[i].setClientProductId(postResponse.getClientProductId()); // For the test that follows
			GOOD_PUTS[i].setSquareItemId("RANDOM_SQUARE_ITEM_ID");
			GOOD_PUTS[i].setSquareItemVariationId("RANDOM_SQUARE_ITEM_VAR_ID");
			final SquareServiceResponseBody putResponse = squareService.putProduct(GOOD_PUTS[i]);
			assertTrue("Bad PUT response from Square service", responseMatchesUpsertRequest(GOOD_PUTS[i], putResponse, PUT));
		}
	}
}
