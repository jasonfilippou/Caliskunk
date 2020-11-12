package com.company.rest.products.util.request_bodies;

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
@NoArgsConstructor
public class ProductGetRequestBody implements Serializable
{
	@JsonProperty @NonNull	private String clientProductId;
}
