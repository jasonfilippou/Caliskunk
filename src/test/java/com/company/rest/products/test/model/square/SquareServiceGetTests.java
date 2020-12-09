package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * GET tests for {@link SquareService}.
 *
 * @see SquareServicePostTests
 * @see SquareServiceDeleteTests
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class SquareServiceGetTests
{

	@InjectMocks
	private SquareService squareService; // The class we are testing

	@Mock
	private CatalogWrapper catalogWrapper;     // Class that will be mocked


	@Before
    public void setUp() throws ExecutionException, InterruptedException
	{

		/////////////////////////////////////////////
		// Prepare CatalogWrapper mocked responses //
		/////////////////////////////////////////////

		// Response for a POST request
		when(catalogWrapper.postObject(any(UpsertCatalogObjectRequest.class)))
				.then(invocation ->
				      {
					      final UpsertCatalogObjectRequest request = invocation.getArgument(0);
					      final Long version = request.getObject().getVersion() != null ?
					                           request.getObject().getVersion() : DEFAULT_VERSION;
					      return buildItemResponseOutOfRequest(request, version, UpsertType.POST);
				      });

        // Response for a Batch Retrieve (GET, GET ALL) request
        when(catalogWrapper.retrieveObject(any(ProductGetRequestBody.class)))
            .then(
		        invocation ->
		        {
		            final ProductGetRequestBody getRequest = invocation.getArgument(0);
		            return buildResponseOutOfRetrieveRequest(getRequest);
		        }
	        );
    }



	@Test
	public void testOneGet()
	{
		// Prepare request
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
													.productName(DEFAULT_PRODUCT_NAME)
													.productType("Flower")
													.clientProductId("#RANDOM_ITEM_ID")
													.costInCents(DEFAULT_COST_IN_CENTS) // 'L for long literal
													.description("Will eat your face.")
													.labelColor("7FFFD4")
													.upc("RANDOM_UPC")
													.sku("RANDOM_SKU")
													.build();

		// Make the POST
		final SquareServiceResponseBody postResponse = squareService.postProduct(request);

		// Make the GET call and test it.
		final SquareServiceResponseBody getResponse = squareService.getProduct(request.toProductGetRequestBody());
		assertTrue("Bad GET response from Square layer", responseMatchesGetRequest(new ProductGetRequestBody(request.getClientProductId()), getResponse));
	}

	@Test
	public void testManyGets()
	{
		// Requests already prepared in GoodPostRequests
		final int numRequests = GOOD_POSTS.length;
		for (ProductUpsertRequestBody goodPost : GOOD_POSTS)
		{
			// Make Square Service POST call and retrieve response
			final SquareServiceResponseBody postResponse = squareService.postProduct(goodPost);

			// Make Square Service GET call, retrieve and  assess response
			final SquareServiceResponseBody getResponse = squareService.getProduct(goodPost.toProductGetRequestBody());
			assertTrue("Bad GET response from Square layer", responseMatchesGetRequest(goodPost.toProductGetRequestBody(), getResponse));
		}
	}

}
