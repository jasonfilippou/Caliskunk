package com.company.rest.products.test.controller;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.test.requests_responses.put.GoodPutRequests;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

public class MockedBackendServicePutResponses
{
	/** An array of prepared {@link BackendServiceResponseBody} instances.
	 */
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPutRequests.REQUESTS.length;
		final BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPutRequests.REQUESTS[i], GoodPostRequests.REQUESTS[i].getClientProductId());
		}
		return retVal;
	}

	private static BackendServiceResponseBody mockedResponse(final ProductUpsertRequestBody putRequest, final String idFromPostRequest)
	{
		return BackendServiceResponseBody.builder()
			                                 .name(putRequest.getName())
			                                 .clientProductId(idFromPostRequest)
			                                 .squareItemId("random_item_id")
			                                 .squareItemVariationId("random_item_var_id")
			                                 .isDeleted(false)
			                                 .costInCents(putRequest.getCostInCents())
			                                 .labelColor(putRequest.getLabelColor())
			                                 .presentAtAllLocations(true)
			                                 .availableElectronically(putRequest.getAvailableElectronically())
			                                 .availableForPickup(putRequest.getAvailableForPickup())
			                                 .availableOnline(putRequest.getAvailableOnline())
			                                 .description(putRequest.getDescription())
			                                 .productType(putRequest.getProductType())
			                                 .sku(putRequest.getSku())
			                                 .upc(putRequest.getUpc())
		                                 .build();
	}
	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final BackendServiceResponseBody[] MOCKED_BACKEND_PUT_RESPONSES = RESPONSES;
}
