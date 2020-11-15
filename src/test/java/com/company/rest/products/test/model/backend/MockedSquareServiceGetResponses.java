package com.company.rest.products.test.model.backend;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.test.requests_responses.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;

/**
 * A class that contains prepared responses of {@link SquareService} towards
 * {@link BackendService}. These responses are useful for mocked unit tests.
 *
 * @see MockedSquareServicePostResponses
 * @see MockedSquareServiceDeleteResponses
 * @see com.company.rest.products.test.model.backend.BackendServiceGetTests
 */
public class MockedSquareServiceGetResponses
{
	/** An array of prepared {@link SquareServiceResponseBody} instances.
	 */
	public static final SquareServiceResponseBody[] RESPONSES = buildMockedResponses();

	private static SquareServiceResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		final SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
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
	private static SquareServiceResponseBody mockedResponse(ProductUpsertRequestBody request)
	{
		return SquareServiceResponseBody.builder()
			                                 .name(request.getName())
		                                     .clientProductId(request.getClientProductId())
			                                 .squareItemId("random_item_id")
			                                 .squareItemVariationId("random_item_var_id")
		                                     .productType(request.getProductType())
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
	/**
	 * A more readable alias for {@link #RESPONSES}.
	 */
	public static final SquareServiceResponseBody[] MOCKED_SQUARE_GET_RESPONSES = RESPONSES;
}

