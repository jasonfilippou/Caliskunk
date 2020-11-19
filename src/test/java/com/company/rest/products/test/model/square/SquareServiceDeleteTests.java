package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.DeleteCatalogObjectResponse;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEMS;
import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEM_VARIATIONS;
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

	private DeleteCatalogObjectResponse buildMockedDeleteResponse(final String squareItemId, final String squareItemVariationId)
	{
		return new DeleteCatalogObjectResponse.Builder()
													.deletedObjectIds(Arrays.asList(squareItemId, squareItemVariationId))
													.deletedAt(LocalDateTime.now().toString())
												.build();
	}

	@InjectMocks
	private SquareService squareService; // The class we are testing

	@Mock
	private CatalogWrapper catalogWrapper;     // Class that will be mocked

	@Before
	public void setUp() throws ExecutionException, InterruptedException
	{

		// Prepare CatalogWrapper respone for POST (on which subsequent DELETEs are based)
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

		// Prepare CatalogWrapper response for DELETE
		when(catalogWrapper.deleteObject(any(String.class)))
				.then(
						invocation ->
						{
							return buildMockedDeleteResponse(invocation.getArgument(0), "SOME_RANDOM_ITEM_VARIATION_ID"); // Square ID is the only real important argument here
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
				.costInCents(10000L) // 'L for long literal
				.description("Will eat your face.")
				.labelColor("7FFFD4")
				.upc("RANDOM_UPC")
				.sku("RANDOM_SKU")
				.availableOnline(false)
				.availableElectronically(false)
				.availableForPickup(true)
				.build();

		// Make the POST, and optionally test it, considering that we already have a POST test suite.
		final SquareServiceResponseBody postResponse = squareService.upsertProduct(postRequest, postRequest.getClientProductId());
		// assertTrue("Request did not match response", responseMatchesPostRequest(postResponse, request));

		// Make the Square Service DEL call and test it.
		final SquareServiceResponseBody delResponse = squareService.deleteProduct(LiteProduct.builder()
		                                                                                     .clientProductId(postRequest.getClientProductId())
		                                                                                     .productName(postRequest.getName())
		                                                                                     .productType(postRequest.getProductType())
		                                                                                     .costInCents(postRequest.getCostInCents())
		                                                                                     .squareItemId("A random Square Item ID")
		                                                                                     .squareItemVariationId("A random Square Item Variation ID")
		                                                                                     .build());
		// validateDeletionResponse(delResponse, postResponse.getSquareItemId(), postResponse.getSquareItemVariationId())); // Will be relevant in End-To-End tests.
		assertTrue("Bad DEL response from Square layer", responseMatchesDeleteRequest(new ProductDeleteRequestBody(postRequest.getClientProductId()),
		                                                                              delResponse));
	}

	@Test
	public void testManyDels()
	{
		// Requests already prepared
		final int numRequests = GOOD_DELETES.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Make Square Service POST call and retrieve response
			final SquareServiceResponseBody postResponse = squareService.upsertProduct(GOOD_POSTS[i], GOOD_POSTS[i].getClientProductId());

			// Optionally, assess POST response. Presumably, the POST test suite has covered that already.
			//	assertTrue("Mismatch in response #" + i + ".", responseMatchesPostRequest(postResponse, GOOD_POSTS[i]));

			final SquareServiceResponseBody delResponse = squareService.deleteProduct(LiteProduct.builder()
			                                                                                     .clientProductId(GOOD_DELETES[i].getClientProductId())
			                                                                                     .productType(GOOD_POSTS[i].getProductType())
			                                                                                     .productName(GOOD_POSTS[i].getName())
			                                                                                     .costInCents(GOOD_POSTS[i].getCostInCents())
			                                                                                     .squareItemId("A random Square Item ID")
			                                                                                     .squareItemVariationId("A random Square Item Variation ID")
			                                                                                     .build()) ;
			assertTrue("Bad DEL response from Square service",
			           responseMatchesDeleteRequest(GOOD_DELETES[i], delResponse));
		}
	}

}
