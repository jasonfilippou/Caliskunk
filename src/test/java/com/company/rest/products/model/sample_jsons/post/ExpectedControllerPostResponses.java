package com.company.rest.products.model.sample_jsons.post;

import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.ProductResponseBody;

import static com.company.rest.products.model.SquareService.DEFAULT_SQUARE_CATALOG_ITEM_TYPE;

/* Questionable whether this class is useful. */
public class ExpectedControllerPostResponses
{

	public static final ProductResponseBody[] RESPONSES = buildExpectedResponses();

	private static ProductResponseBody[] buildExpectedResponses()
	{
		final int numRequests = GoodPostRequests.POST_REQUESTS.length;
		ProductResponseBody[] retVal = new ProductResponseBody[numRequests];
		for(int i = 0; i < numRequests; i++)
		{
			retVal[i] = expectedResponse(GoodPostRequests.POST_REQUESTS[i]);
		}
		return retVal;
	}

	private static ProductResponseBody expectedResponse(ProductPostRequestBody request)
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
		                            .categoryId(request.getCategoryId())
		                            .productType(DEFAULT_SQUARE_CATALOG_ITEM_TYPE)
		                            .sku(request.getSku())
		                            .upc(request.getUpc())
		                          .build();
	}
}
