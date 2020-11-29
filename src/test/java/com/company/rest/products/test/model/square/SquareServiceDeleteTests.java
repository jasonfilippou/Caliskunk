package com.company.rest.products.test.model.square;
import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
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

import static com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests.GOOD_DELETES;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * DELETE tests for {@link SquareService}.
 *
 * @see SquareServiceGetTests
 * @see SquareServicePostTests
 * @see SquareServicePutTests
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@ContextConfiguration(classes = CaliSkunkApplication.class)
@ComponentScan(basePackages = {"com.company.rest.products"})
public class SquareServiceDeleteTests
{
	@InjectMocks
	private SquareService squareService; // The class we are testing

	@Mock
	private CatalogWrapper catalogWrapper;     // Class that will be mocked

	@Before
	public void setUp() throws ExecutionException, InterruptedException
	{

		// Prepare CatalogWrapper response for POST (on which subsequent DELETEs are based)
		when(catalogWrapper.postObject(any(UpsertCatalogObjectRequest.class)))
				.then(invocation ->
				      {
					      final UpsertCatalogObjectRequest request = invocation.getArgument(0);
					      return buildItemResponseOutOfRequest(request, DEFAULT_VERSION_FOR_TESTS, UpsertType.POST);
				      });

		// Prepare CatalogWrapper response for DELETE
		when(catalogWrapper.deleteObject(any(ProductDeleteRequestBody.class)))
				.then(invocation ->
						{
							return buildMockedDeleteResponseOutOfRequest(invocation.getArgument(0)); // Square ID is the only real important argument here
						}
				     );

	}

	@Test
	public void testOneDel()
	{
		// Prepare request
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

		// Make the POST
		final SquareServiceResponseBody postResponse = squareService.postProduct(postRequest);

		// Make the Square Service DEL call and test it.
		final SquareServiceResponseBody deleteResponse = squareService.deleteProduct(postRequest.toProductDeleteRequestBody());
		assertTrue("Bad DEL response from Square layer", responseMatchesDeleteRequest(postRequest.toProductDeleteRequestBody(),
		                                                                              deleteResponse));
	}

	@Test
	public void testManyDels()
	{
		// Requests already prepared
		final int numRequests = GOOD_DELETES.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Make Square Service POST call and retrieve response
			final SquareServiceResponseBody postResponse = squareService.postProduct(GOOD_POSTS[i]);

			final SquareServiceResponseBody delResponse = squareService.deleteProduct(GOOD_POSTS[i].toProductDeleteRequestBody()) ;
			assertTrue("Bad DEL response from Square service",  responseMatchesDeleteRequest(GOOD_POSTS[i].toProductDeleteRequestBody(),
			                                                                                 delResponse));
		}
	}

}
