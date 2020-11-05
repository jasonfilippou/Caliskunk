package com.company.rest.products.requests_responses.post;

import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;

public class MockedSquareServicePostResponses
{
	public static final SquareServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static SquareServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.REQUESTS[i]);
		}
		return retVal;
	}

	private static SquareServiceResponseBody mockedResponse(ProductPostRequestBody request)
	{
		return SquareServiceResponseBody.builder()
		                            .name(request.getName())
	                                .squareItemId("RANDOM_ITEM_ID")
		                            .squareItemVariationId("random_var_id")
		                            .isDeleted(false)
		                            .costInCents(request.getCostInCents())
		                            .labelColor(request.getLabelColor())
		                            .presentAtAllLocations(true)
		                            .availableElectronically(request.getAvailableElectronically())
		                            .availableForPickup(request.getAvailableForPickup())
		                            .availableOnline(request.getAvailableOnline())
		                            .description(request.getDescription())
		                            .sku(request.getSku())
		                            .upc(request.getUpc())
		                          .build();
	}
}
