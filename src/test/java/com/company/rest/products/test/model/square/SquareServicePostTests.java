package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.*;
import lombok.NonNull;
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
import static java.util.Optional.ofNullable;
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

	private boolean responseMatchesRequest(@NonNull SquareServiceResponseBody response,
	                                       @NonNull ProductUpsertRequestBody request)
	{
		return	response.getName().equals(request.getName()) &&
		        response.getCostInCents().equals(request.getCostInCents()) &&

		       	ofNullable(request.getAvailableElectronically()).equals(ofNullable(response.getAvailableElectronically())) &&
				ofNullable(request.getAvailableForPickup()).equals(ofNullable(response.getAvailableForPickup())) &&
				ofNullable(request.getAvailableOnline()).equals(ofNullable(response.getAvailableOnline())) &&
				ofNullable(request.getLabelColor()).equals(ofNullable(response.getLabelColor())) &&
				ofNullable(request.getDescription()).equals(ofNullable(response.getDescription())) &&
				ofNullable(request.getSku()).equals(ofNullable(response.getSku())) &&
				ofNullable(request.getUpc()).equals(ofNullable(response.getUpc())) &&

		       // A POST request shouldn't trip a deletion flag.
		       (response.getIsDeleted() == null || !response.getIsDeleted());   
	}


	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Before
    public void setUp() throws ExecutionException, InterruptedException
	{

		// Uncomment the following if you want to mock the CatalogWrapper calls.

		//  I need a way to return different kinds of expected responses depending on the arguments
		// of the methods that are being mocked. The following trick owed to:
		// https://stackoverflow.com/questions/22338536/mockito-return-value-based-on-property-of-a-parameter
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

    private UpsertCatalogObjectResponse buildItemResponseOutOfRequest(final UpsertCatalogObjectRequest request)
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

    private UpsertCatalogObjectResponse buildItemVariationResponseOutOfRequest(final UpsertCatalogObjectRequest request)
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

	@Test
	public void testOnePost()
	{
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
														.availableOnline(true)
														.availableElectronically(true)
														.availableForPickup(true)
													.build();
		// catalog calls already mocked by setUp(); we can just call the method we want to debug.
		final SquareServiceResponseBody response = squareService.upsertProduct(request, request.getClientProductId());
		assertTrue("Request did not match response", responseMatchesRequest(response, request));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Call backend service
			final SquareServiceResponseBody response = squareService.upsertProduct(GOOD_POSTS[i], GOOD_POSTS[i].getClientProductId());

			// Assess response
			assertTrue("Mismatch in response #" + i, responseMatchesRequest(response, GOOD_POSTS[i]));
		}
	}

}
