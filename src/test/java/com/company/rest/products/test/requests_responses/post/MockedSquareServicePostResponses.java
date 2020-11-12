package com.company.rest.products.test.requests_responses.post;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;

/**
 * A class that contains prepared responses of {@link SquareService} towards
 * {@link BackendService}. These responses are useful for mocked unit tests.
 *
 * @see com.company.rest.products.test.requests_responses.get.MockedBackendServiceGetResponses
 * @see com.company.rest.products.test.requests_responses.delete.MockedSquareServiceDeleteResponses
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
		final int numRequests = GoodPostRequests.REQUESTS.length;
		SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.REQUESTS[i]);
		}
		return retVal;
	}

	private static SquareServiceResponseBody mockedResponse(ProductUpsertRequestBody request)
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
