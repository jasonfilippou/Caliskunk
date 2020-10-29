package com.company.rest.products.sample_requests.post;

import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;

import static com.company.rest.products.model.SquareService.DEFAULT_SQUARE_CATALOG_ITEM_TYPE;

/* Questionable whether this class is useful. */
public class MockedControllerPostResponses
{

	public static final ProductResponseBody[] RESPONSES = buildMockedResponses();

	private static ProductResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		ProductResponseBody[] retVal = new ProductResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.POST_REQUESTS[i]);
		}
		return retVal;
	}

	private static ProductResponseBody mockedResponse(ProductPostRequestBody request)
	{
		return ProductResponseBody.builder()
		                            .name(request.getName())
		                            .clientProductId(request.getClientProductId())
		                            .isDeleted(false)
		                            .costInCents(request.getCostInCents())
		                            .labelColor(request.getLabelColor())
		                            .presentAtAllLocations(true)
		                            .availableElectronically(request.getAvailableElectronically())
		                            .availableForPickup(request.getAvailableForPickup())
		                            .availableOnline(request.getAvailableOnline())
		                            .description(request.getDescription())
		                            .productType(DEFAULT_SQUARE_CATALOG_ITEM_TYPE)
		                            .sku(request.getSku())
		                            .upc(request.getUpc())
		                          .build();
	}
}
