package com.company.rest.products.model.sample_jsons.post;

import com.company.rest.products.util.json_objects.BackendServiceResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;

public class ExpectedBackendServicePostResponses
{
	public static final BackendServiceResponseBody[] RESPONSES = buildExpectedResponses();

	private static BackendServiceResponseBody[] buildExpectedResponses()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		BackendServiceResponseBody[] retVal = new BackendServiceResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = expectedResponse(GoodPostRequests.POST_REQUESTS[i]);
		}
		return retVal;
	}

	private static BackendServiceResponseBody expectedResponse(ProductPostRequestBody request)
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
			                                 .categoryId(request.getCategoryId())
			                                 .productType(request.getProductType())
			                                 .sku(request.getSku())
			                                 .upc(request.getUpc())
		                                 .build();
	}
}

