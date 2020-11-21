package com.company.rest.products.util.request_bodies;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


/**
 * A JSON GET request.
 *
 * @see ProductUpsertRequestBody
 * @see ProductDeleteRequestBody
 * @see ProductUpsertRequestBody
 * @see ProductResponseBody
 */
@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
public class ProductGetRequestBody implements Serializable
{
	@JsonProperty("id") @NonNull private String clientProductId;
	private LiteProduct liteProduct;      // Will be populated later as we go down the API.

	public ProductGetRequestBody(final String id)
	{
		clientProductId = id;
	}
}
