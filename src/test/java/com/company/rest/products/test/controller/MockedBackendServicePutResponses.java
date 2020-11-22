package com.company.rest.products.test.controller;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.requests_responses.put.GoodPutRequests.GOOD_PUTS;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_UPDATED_AT_STRING;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION_FOR_TESTS;
public class MockedBackendServicePutResponses
{
	/** An array of prepared {@link BackendServiceResponseBody} instances.
	 */
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GOOD_PUTS.length;
		final BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GOOD_PUTS[i], GOOD_POSTS[i].getClientProductId());
		}
		return retVal;
	}

	private static BackendServiceResponseBody mockedResponse(final ProductUpsertRequestBody putRequest, final String idFromPostRequest)
	{
		return BackendServiceResponseBody.builder()
		                                     .name(putRequest.getName().strip().toUpperCase())
			                                 .clientProductId(idFromPostRequest)
			                                 .squareItemId("random_item_id")
			                                 .isDeleted(false)
			                                 .costInCents(putRequest.getCostInCents())
			                                 .labelColor(putRequest.getLabelColor())
			                                 .description(putRequest.getDescription())
			                                 .productType(putRequest.getProductType())
			                                 .sku(putRequest.getSku())
			                                 .upc(putRequest.getUpc())
		                                     .updatedAt(DEFAULT_UPDATED_AT_STRING)
			                                 .version(DEFAULT_VERSION_FOR_TESTS)
		                                 .build();
	}
	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final BackendServiceResponseBody[] MOCKED_BACKEND_PUT_RESPONSES = RESPONSES;
}
