package com.company.rest.products.test.model.backend;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_UPDATED_AT_STRING;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION;
/**
 * A class that contains prepared responses of {@link SquareService} towards
 * {@link BackendService}. These responses are useful for mocked unit tests of the DELETE verb.
 *
 * @see MockedSquareServiceGetResponses
 * @see MockedSquareServicePostResponses
 * @see com.company.rest.products.test.model.backend.BackendServiceDeleteTests
 */
public class MockedSquareServicePutResponses
{
	/**
	 * An array of prepared {@link SquareServiceResponseBody} instances.
	 */
	public static final SquareServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static SquareServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GOOD_PUTS.length;
		final SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId());
		}
		return retVal;
	}

	private static SquareServiceResponseBody mockedResponse(final ProductUpsertRequestBody request, final String relevantPostId)
	{
		return SquareServiceResponseBody.builder()
		                                .name(request.getProductName().strip().toUpperCase())
		                                .clientProductId(relevantPostId)
		                                .productType(request.getProductType().strip().toUpperCase())
		                                .squareItemId("#RANDOM_ITEM_ID")
		                                .squareItemVariationId("#RANDOM_ITEM_VAR_ID")
		                                .isDeleted(false)
		                                .costInCents(request.getCostInCents())
		                                .labelColor(request.getLabelColor())
		                                .description(request.getDescription())
		                                .sku(request.getSku())
		                                .updatedAt(DEFAULT_UPDATED_AT_STRING)
		                                .upc(request.getUpc())
		                                .version(DEFAULT_VERSION)
		                                .build();
	}

	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final SquareServiceResponseBody[] MOCKED_SQUARE_PUT_RESPONSES = RESPONSES;
}
