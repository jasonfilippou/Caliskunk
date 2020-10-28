package com.company.rest.products.model.sample_jsons.post;

import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.SquareServiceResponseBody;

public class ExpectedSquareServicePostResponses
{
	public static final SquareServiceResponseBody[] RESPONSES = buildExpectedResponses();

	private static SquareServiceResponseBody[] buildExpectedResponses()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		SquareServiceResponseBody[] retVal = new SquareServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = expectedResponse(GoodPostRequests.POST_REQUESTS[i]);
		}
		return retVal;
	}

	private static SquareServiceResponseBody expectedResponse(ProductPostRequestBody request)
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
		                            .categoryId(request.getCategoryId())
		                            .sku(request.getSku())
		                            .upc(request.getUpc())
		                          .build();
	}
}
