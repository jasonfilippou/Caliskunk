package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * A JSON DELETE request.
 *
 * @see ProductPostRequestBody
 * @see ProductGetRequestBody
 * @see ProductUpdateRequestBody
 * @see ProductResponseBody
 *
 */
@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeleteRequestBody implements Serializable
{
	@JsonProperty @NonNull	private String clientProductId;
}
