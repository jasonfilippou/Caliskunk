package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * A JSON DELETE request.
 *
 * @see ProductUpsertRequestBody
 * @see ProductGetRequestBody
 * @see ProductUpsertRequestBody
 * @see ProductResponseBody
 *
 */
@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
public class ProductDeleteRequestBody implements Serializable
{
	@JsonProperty("id") @NonNull	private String clientProductId;
	private LiteProduct liteProduct;
	/**
	 * ID-based constructor.
	 * @param clientProductId The client-provided ID, which should be unique in the application.
	 */
	public ProductDeleteRequestBody(final String clientProductId)
	{
		this.clientProductId = clientProductId;
	}
}
