package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
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
        when(catalogWrapper.retrieveObject(any(String.class)))
            .then(
		        invocation ->
		        {
		            final String id = invocation.getArgument(0);
		            return buildResponseOutOfRetrieveRequest(id);
		        }
	        );
    }

    private RetrieveCatalogObjectResponse buildResponseOutOfRetrieveRequest(final String id)
    {
    	return new RetrieveCatalogObjectResponse.Builder()
			                                        .object(buildObjectForRetrieveResponse(id))
			                                        .build();
    }

    private CatalogObject buildObjectForRetrieveResponse(final String id)
    {
        return new CatalogObject
					.Builder(CODE_FOR_CATALOG_ITEMS, id)
			        .itemData(new CatalogItem.Builder()
					                  .name("RANDOM_ITEM_NAME")
								      .build())
					.build();    // Yo dawg I heard you like builders :(
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
