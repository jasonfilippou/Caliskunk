package com.company.rest.products.sample_requests.get;

import com.company.rest.products.sample_requests.post.GoodPostRequests;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;

import static com.company.rest.products.model.SquareService.DEFAULT_SQUARE_CATALOG_ITEM_TYPE;

/* Questionable whether this class is useful. */
public class MockedControllerGetResponses
{

	public static final ProductResponseBody[] RESPONSES = buildMockedResponses();

	private static ProductResponseBody[] buildMockedResponses()
	{
		final int numRequests = GoodPostRequests.REQUESTS.length;
		ProductResponseBody[] retVal = new ProductResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = mockedResponse(GoodPostRequests.REQUESTS[i]); // NOT a mistake: read the comment below.
		}
		return retVal;
	}

	/*
	 * Why do we return a response based on data from the POST request in a class that is supposed to
	 * provide mocked (prepared) GET responses? Because the correct GET responses actually correspond to the
	 * data sent by the POST request. The GET request just gives us an ID. What _we_ need to return, however,
	 * is effectively exactly the same data that is returned to us by a successful POST operation confirmation.
	 */

	private static ProductResponseBody mockedResponse(ProductPostRequestBody postData)
	{
		return ProductResponseBody.builder()
		                            .name(postData.getName())
		                            .clientProductId(postData.getClientProductId())
		                            .isDeleted(false)
		                            .costInCents(postData.getCostInCents())
		                            .labelColor(postData.getLabelColor())
		                            .presentAtAllLocations(true)
		                            .availableElectronically(postData.getAvailableElectronically())
		                            .availableForPickup(postData.getAvailableForPickup())
		                            .availableOnline(postData.getAvailableOnline())
		                            .description(postData.getDescription())
		                            .productType(DEFAULT_SQUARE_CATALOG_ITEM_TYPE)
		                            .sku(postData.getSku())
		                            .upc(postData.getUpc())
		                          .build();
	}
}
