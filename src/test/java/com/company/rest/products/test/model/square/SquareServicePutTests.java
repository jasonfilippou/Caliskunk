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

import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEMS;
import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEM_VARIATIONS;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.UpsertType.PUT;
import static com.company.rest.products.test.util.TestUtil.*;
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

		// Prepare CatalogWrapper response for POST (on which subsequent PUTs are based)
		when(catalogWrapper.upsertObject(any(UpsertCatalogObjectRequest.class)))
				.then(
						invocation -> {
							final UpsertCatalogObjectRequest request = invocation.getArgument(0);
							if(request.getObject().getType().equals(CODE_FOR_CATALOG_ITEMS))
							{
								return buildItemResponseOutOfRequest(request);
							}
							else if(request.getObject().getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS))
							{
								return buildItemVariationResponseOutOfRequest(request);
							}
							else
							{
								throw new AssertionError("Bad upsert request: type was " +
								                         request.getObject().getType());
							}

						}
				     );
	}


	@Test
	public void testOnePut()
	{
		// Prepare requests
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
				.builder()
				.name("Culeothesis Necrosis")
				.productType("Flower")
				.clientProductId("#RANDOM_ITEM_ID")
				.costInCents(10000L) // 'L for long literal
				.description("Will eat your face.")
				.labelColor("7FFFD4")
				.upc("RANDOM_UPC")
				.sku("RANDOM_SKU")
				.availableOnline(false)
				.availableElectronically(false)
				.availableForPickup(true)
				.build();

		final ProductUpsertRequestBody putRequest = ProductUpsertRequestBody    // Change some fields, keep others.
				.builder()
				.name("Culeothesis Necrosis OG")
				.costInCents(15000L)
				.labelColor("AB8235")
				.availableElectronically(false)
		        .availableForPickup(true)           // No change, but client should be able to re-specify no problem.
				.build();

		// Make the POST, and optionally test it, considering that we already have a POST test suite.
		final SquareServiceResponseBody postResponse = squareService.upsertProduct(postRequest, postRequest.getClientProductId());
		// assertTrue("BAD POST request from Square layer.", responseMatchesUpsertRequest(postRequest, postResponse, POST));

		// Make the Square Service PUT call and test it.
		final SquareServiceResponseBody putResponse = squareService.upsertProduct(putRequest, postRequest.getClientProductId());
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
			final SquareServiceResponseBody postResponse = squareService.upsertProduct(GOOD_POSTS[i], GOOD_POSTS[i].getClientProductId());

			// Optionally, assess POST response. Presumably, the POST test suite has covered that already.
			//	assertTrue("Mismatch in response #" + i + ".", responseMatchesUpsertRequest(GOOD_POSTS[i], postResponse, POST));

			// Perform PUT and assess it.
			final SquareServiceResponseBody putResponse = squareService.upsertProduct(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId());
			assertTrue("Bad PUT response from Square service", responseMatchesUpsertRequest(GOOD_PUTS[i], putResponse, PUT));
		}
	}
}
