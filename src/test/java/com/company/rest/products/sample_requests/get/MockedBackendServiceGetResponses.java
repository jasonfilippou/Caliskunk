package com.company.rest.products.sample_requests.get;

import com.company.rest.products.sample_requests.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;

public class MockedBackendServiceGetResponses
{
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.REQUESTS[i]);       // *Not* a mistake! See the comment below.
		}
		return retVal;
	}

	/*
	 * Why are we using POST request information in a class whose methods are supposed to process
	 * GET requests? Because the GET request, which is based on the ID used in the POST, makes a request
	 * for the full data that was available to us in POST! The GET request itself only provides an ID,
	 * but the client expects everything they stored in the POST. Therefore, to appropriately mock
	 * the backend service, we need to provide a `BackendServiceResponseBody` which will have mined
	 * the information from the relevant `POST` request. Naturally, in production, this information will
	 * actually come from Square; this part of the codebase will not even run.
	 */

	private static BackendServiceResponseBody mockedResponse(ProductPostRequestBody request)
	{
		return BackendServiceResponseBody.builder()
			                                 .name(request.getName())
			                                 .clientProductId(request.getClientProductId())
			                                 .squareItemId("random_item_id")
			                                 .squareItemVariationId("random_item_var_id")
			                                 .isDeleted(false)
			                                 .costInCents(request.getCostInCents())
			                                 .labelColor(request.getLabelColor())
			                                 .presentAtAllLocations(true)
			                                 .availableElectronically(request.getAvailableElectronically())
			                                 .availableForPickup(request.getAvailableForPickup())
			                                 .availableOnline(request.getAvailableOnline())
			                                 .description(request.getDescription())
			                                 .productType(request.getProductType())
			                                 .sku(request.getSku())
			                                 .upc(request.getUpc())
		                                 .build();
	}
}

