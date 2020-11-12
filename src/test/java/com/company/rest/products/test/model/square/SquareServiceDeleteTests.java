package com.company.rest.products.test.model.square;

import com.company.rest.products.CaliSkunkApplication;
import com.company.rest.products.model.CatalogWrapper;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.test.requests_responses.delete.GoodDeleteRequests;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.exceptions.ApiException;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEMS;
import static com.company.rest.products.model.SquareService.CODE_FOR_CATALOG_ITEM_VARIATIONS;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * DELETE tests for {@link SquareService}.
 *
 * @see SquareServiceGetTests
 * @see SquareServiceDeleteTests
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

	private boolean responseMatchesDelRequest(@NonNull SquareServiceResponseBody response,
	                                          @NonNull String squareIdAssignedOnPost)
	{
		return	response.getSquareItemId().equals(squareIdAssignedOnPost);
	}

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
	public void testOneDel() throws IOException, ApiException, ExecutionException, InterruptedException
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
														.availableElectronically(false) // Whatever that means
														.availableForPickup(true)
													.build();

		// Make the POST, and optionally test it, considering that we already have a POST test suite.
		final SquareServiceResponseBody postResponse = squareService.upsertProduct(postRequest);
		// assertTrue("Request did not match response", responseMatchesPostRequest(postResponse, request));

		// Mock the CatalogWrapper call...
		when(catalogWrapper.deleteObject(postResponse.getSquareItemId())).thenReturn(buildMockedDeleteResponse
																								(postResponse.getSquareItemId(),
																                                postResponse.getSquareItemVariationId()));
		// Make the Square Service DEL call and test it.
		final SquareServiceResponseBody delResponse = squareService.deleteProduct(postResponse.getSquareItemId());
		// validateDeletionResponse(delResponse, postResponse.getSquareItemId(), postResponse.getSquareItemVariationId())); // Will be relevant in End-To-End tests.
		assertTrue("Bad GET response from Square layer", responseMatchesDelRequest(delResponse,
		                                                                           postResponse.getSquareItemId()));
	}

	private DeleteCatalogObjectResponse buildMockedDeleteResponse(final String squareItemId, final String squareItemVariationId)
	{
		return new DeleteCatalogObjectResponse.Builder()
													.deletedObjectIds(Arrays.asList(squareItemId, squareItemVariationId))
													.deletedAt(LocalDateTime.now().toString())
												.build();
	}

	@Test
	public void testManyDels() throws ExecutionException, InterruptedException
	{
		// Requests already prepared
		final int numRequests = GoodDeleteRequests.REQUESTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Make Square Service POST call and retrieve response
			final SquareServiceResponseBody postResponse = squareService.upsertProduct(GoodPostRequests.REQUESTS[i]);

			// Optionally, assess POST response. Presumably, the POST test suite has covered that already.
			//	assertTrue("Mismatch in response #" + i + ".", responseMatchesPostRequest(postResponse, GoodPostRequests.REQUESTS[i]));

			// Mock the CatalogWrapper call...
			when(catalogWrapper.deleteObject(postResponse.getSquareItemId())).thenReturn(buildMockedDeleteResponse
																								(postResponse.getSquareItemId(),
																                                postResponse.getSquareItemVariationId()));
			final SquareServiceResponseBody delResponse = squareService.deleteProduct(postResponse.getSquareItemId());
			assertTrue("Bad GET response from Square service",
			           responseMatchesDelRequest(delResponse, postResponse.getSquareItemId()));
		}
	}

}
