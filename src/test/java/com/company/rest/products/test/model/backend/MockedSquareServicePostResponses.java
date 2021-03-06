package com.company.rest.products.test.model.backend;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.test.controller.MockedBackendServiceGetResponses;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_UPDATED_AT_STRING;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION;
/**
 * A class that contains prepared responses of {@link SquareService} towards
 * {@link BackendService}. These responses are useful for mocked unit tests.
 *
 * @see MockedBackendServiceGetResponses
 * @see MockedSquareServiceDeleteResponses
 * @see com.company.rest.products.test.model.backend.BackendServicePostTests
 */
public class MockedSquareServicePostResponses
{
	/**
	 * An array of prepared {@link SquareServiceResponseBody} instances.
	 */
	public static final SquareServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static SquareServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GOOD_POSTS.length;
		final SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GOOD_POSTS[i]);
		}
		return retVal;
	}

	private static SquareServiceResponseBody mockedResponse(final ProductUpsertRequestBody request)
	{
		return SquareServiceResponseBody.builder()
		                            .name(request.getProductName().strip().toUpperCase())
	                                .clientProductId(request.getClientProductId())
	                                .productType(request.getProductType().strip().toUpperCase())
	                                .squareItemId("RANDOM_ITEM_ID")
	                                .squareItemVariationId("RANDOM_ITEM_VAR_ID")
		                            .isDeleted(false)
		                            .costInCents(request.getCostInCents())
		                            .labelColor(request.getLabelColor())
		                            .description(request.getDescription())
		                            .sku(request.getSku())
		                            .upc(request.getUpc())
	                                .version(DEFAULT_VERSION)
                                    .updatedAt(DEFAULT_UPDATED_AT_STRING)
		                          .build();
	}

	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final SquareServiceResponseBody[] MOCKED_SQUARE_POST_RESPONSES = RESPONSES;
}
