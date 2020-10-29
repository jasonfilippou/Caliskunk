package com.company.rest.products.sample_requests.post;

import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;

public class MockedBackendServicePostResponses
{
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.POST_REQUESTS[i]);
		}
		return retVal;
	}

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

