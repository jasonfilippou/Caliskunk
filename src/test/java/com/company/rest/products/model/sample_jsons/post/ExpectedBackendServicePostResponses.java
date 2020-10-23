package com.company.rest.products.model.sample_jsons.post;

import com.company.rest.products.util.json_objects.BackendServiceResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;

import java.util.ArrayList;

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
		                                 .itemId("random_id")
		                                 .itemVariationId("random_var_id")
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
		                                 .taxIDs(new ArrayList<>())
		                                 .updatedAt("Whenever")
		                                 .version(0L)    // Random version
		                                 .build();
	}
}

