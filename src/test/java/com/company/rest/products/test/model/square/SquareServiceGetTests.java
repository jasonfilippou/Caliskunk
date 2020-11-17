package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEMS;
import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEM_VARIATIONS;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.responseMatchesGetRequest;
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


    private UpsertCatalogObjectResponse buildItemResponseOutOfUpsertRequest(final UpsertCatalogObjectRequest request)
    {
    	final CatalogItem item = Optional.of(request.getObject().getItemData())
	                                     .orElseThrow(() -> new AssertionError("Upsert request not for CatalogItem"));
    	final CatalogObject itemWrapper = new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, "RANDOM_ITEM_ID")
		                                                        .itemData(item)
		                                                    .build();
    	return new UpsertCatalogObjectResponse.Builder()
			                                        .catalogObject(itemWrapper)
			                                    .build();
    }

    private UpsertCatalogObjectResponse buildItemVariationResponseOutOfUpsertRequest(final UpsertCatalogObjectRequest request)
    {
		final CatalogItemVariation itemVariation = Optional.of(request.getObject().getItemVariationData())
		                                                   .orElseThrow(() -> new AssertionError("Upsert request not for CatalogItemVariation"));
    	final CatalogObject itemVariationWrapper = new CatalogObject.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS, "RANDOM_ITEM_VAR_ID")
		                                                                 .itemVariationData(itemVariation)
	                                                                 .build();
    	return new UpsertCatalogObjectResponse.Builder()
			                                        .catalogObject(itemVariationWrapper)
			                                    .build();
    }

	@Before
    public void setUp() throws ExecutionException, InterruptedException
	{

		/////////////////////////////////////////////
		// Prepare CatalogWrapper mocked responses //
		/////////////////////////////////////////////

		// Response for an Upsert (POST, PUT) request
        when(catalogWrapper.upsertObject(any(UpsertCatalogObjectRequest.class)))
            .then(
		        invocation -> {
		            final UpsertCatalogObjectRequest request = invocation.getArgument(0);
		            if(request.getObject().getType().equals(CODE_FOR_CATALOG_ITEMS))
			        {
			            return buildItemResponseOutOfUpsertRequest(request);
			        }
		            else if(request.getObject().getType().equals(CODE_FOR_CATALOG_ITEM_VARIATIONS))
			        {
			            return buildItemVariationResponseOutOfUpsertRequest(request);
			        }
			        else
			        {
			            throw new AssertionError("Bad upsert request: type was " +
				                                 request.getObject().getType());
			        }

		        }
	        );

        // Response for a Batch Retrieve (GET, GET ALL) request
        when(catalogWrapper.retrieveObject(any(BatchRetrieveCatalogObjectsRequest.class)))
            .then(
		        invocation ->
		        {
		            final BatchRetrieveCatalogObjectsRequest request = invocation.getArgument(0);
		            return buildResponseOutOfRetrieveRequest(request);
		        }
	        );
    }

    private BatchRetrieveCatalogObjectsResponse buildResponseOutOfRetrieveRequest(final BatchRetrieveCatalogObjectsRequest request)
    {
    	return new BatchRetrieveCatalogObjectsResponse.Builder()
			                                                .objects(buildObjectsForRetrieveResponse(request))
			                                            .build();
    }

    private List<CatalogObject> buildObjectsForRetrieveResponse(final BatchRetrieveCatalogObjectsRequest request)
    {
    	// By construction of the list referred to by objectIDs, the first ID
		// represents the CatalogItem, and the second one the CatalogItemVariation.
		// The actual contents of the objects do not matter, since this code serves
		// a mocked call; we don't touch data stored on the Square API.
		final List<String> objIds = request.getObjectIds();
		assert objIds.size() == 2 : " Bad retrieve request; object IDs were: " + objIds + ".";
		final List<CatalogObject> retVal  = new ArrayList<>();
		retVal.add(new CatalogObject
							.Builder(CODE_FOR_CATALOG_ITEMS, objIds.get(0))
								.itemData(new CatalogItem.Builder()
															.name("RANDOM_ITEM_NAME")
										                 .build())      // Yo dawg I heard you like builders :(
							.build());
		retVal.add(new CatalogObject
							.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS, objIds.get(1))
								.itemVariationData(new CatalogItemVariation.Builder()
								                                                .name("RANDOM_ITEM_VAR_NAME")
								                                                .priceMoney(new Money(10000L, "USD"))
								                                            .build())
							.build());
		return retVal;
	 }

	@Test
	public void testOneGet()
	{

		// Prepare request
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
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

		// Make the POST, and optionally assess response (given that we already have a POST testing suite)
		final SquareServiceResponseBody postResponse = squareService.upsertProduct(request, request.getClientProductId());
		// assertTrue("Request did not match response", responseMatchesPostRequest(postResponse, request));

		// Make the GET call and test it.
		final SquareServiceResponseBody getResponse = squareService.getProduct(LiteProduct.buildLiteProductFromSquareResponse(postResponse));
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
			final SquareServiceResponseBody postResponse = squareService.upsertProduct(goodPost, goodPost.getClientProductId());
			//	assertTrue("Mismatch in response #" + i + ".", responseMatchesPostRequest(postResponse, GOOD_POSTS[i]));
			final SquareServiceResponseBody getResponse = squareService.getProduct(LiteProduct.buildLiteProductFromSquareResponse(postResponse));
			assertTrue("Bad GET response from Square layer", responseMatchesGetRequest(new ProductGetRequestBody(goodPost.getClientProductId()), getResponse));
		}
	}

}
