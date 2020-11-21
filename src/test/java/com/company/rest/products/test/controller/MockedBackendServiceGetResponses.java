package com.company.rest.products.test.controller;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.DEFAULT_VERSION_ID_FOR_MOCKS;
/**
 * A class that contains prepared responses of {@link BackendService} towards
 * {@link ProductController}. These responses are useful for mocked unit tests.
 *
 * @see MockedBackendServiceDeleteResponses
 * @see MockedBackendServicePostResponses
 * @see com.company.rest.products.test.controller.ControllerGetTests
 */
public class MockedBackendServiceGetResponses
{
	/**
	 * An array of prepared {@link BackendServiceResponseBody} instances.
	 */
	public static final BackendServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static BackendServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GOOD_POSTS.length;
		final BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GOOD_POSTS[i]);       // *Not* a mistake! See the comment below.
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
	private static BackendServiceResponseBody mockedResponse(final ProductUpsertRequestBody request)
	{
		return BackendServiceResponseBody.builder()
			                                 .name(request.getName())
			                                 .clientProductId(request.getClientProductId())
			                                 .squareItemId("random_item_id")
			                                 .isDeleted(false)
			                                 .costInCents(request.getCostInCents())
			                                 .labelColor(request.getLabelColor())
			                                 .availableElectronically(request.getAvailableElectronically())
			                                 .availableForPickup(request.getAvailableForPickup())
			                                 .availableOnline(request.getAvailableOnline())
			                                 .description(request.getDescription())
			                                 .productType(request.getProductType())
			                                 .sku(request.getSku())
			                                 .upc(request.getUpc())
			                                 .version(DEFAULT_VERSION_ID_FOR_MOCKS)
		                                 .build();
	}

	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final BackendServiceResponseBody[] MOCKED_BACKEND_GET_RESPONSES = RESPONSES;
}

