package com.company.rest.products.test.requests_responses.post;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

/**
 * A class that contains prepared responses of {@link BackendService} towards
 * {@link ProductController}. These responses are useful for mocked unit tests.
 *
 * @see com.company.rest.products.test.requests_responses.get.MockedBackendServiceGetResponses
 * @see com.company.rest.products.test.requests_responses.delete.MockedSquareServiceDeleteResponses
 * @see com.company.rest.products.test.controller.ControllerPostTests
 */
public class MockedBackendServicePostResponses
{
	/** An array of prepared {@link BackendServiceResponseBody} instances.
	 */
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.REQUESTS[i]);
		}
		return retVal;
	}

	private static BackendServiceResponseBody mockedResponse(ProductUpsertRequestBody request)
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

