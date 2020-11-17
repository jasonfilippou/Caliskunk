package com.company.rest.products.test.controller;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.test.model.backend.MockedSquareServiceDeleteResponses;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
/**
 * A class that contains prepared responses of {@link BackendService} towards
 * {@link ProductController}. These responses are useful for mocked unit tests.
 *
 * @see MockedBackendServiceGetResponses
 * @see MockedSquareServiceDeleteResponses
 * @see com.company.rest.products.test.controller.ControllerPostTests
 */
public class MockedBackendServicePostResponses
{
	/** An array of prepared {@link BackendServiceResponseBody} instances.
	 */
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GOOD_POSTS.length;
		final BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GOOD_POSTS[i]);
		}
		return retVal;
	}

	private static BackendServiceResponseBody mockedResponse(final ProductUpsertRequestBody request)
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

	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final BackendServiceResponseBody[] MOCKED_BACKEND_POST_RESPONSES = RESPONSES;
}

