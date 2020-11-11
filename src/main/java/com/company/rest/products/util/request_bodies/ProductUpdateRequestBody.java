package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * A JSON UPDATE (PUT / PATCH) request. This class is essentially a {@link ProductPostRequestBody} without any non-null
 * assertions on fields.
 *
 * @see ProductPostRequestBody
 * @see ProductGetRequestBody
 * @see ProductDeleteRequestBody
 * @see ProductResponseBody
 */
@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateRequestBody implements Serializable
{
	@JsonProperty("name")  private String name;
	@JsonProperty("product_id") private String clientProductId;
	@JsonProperty("product_type") private String productType;
	@JsonProperty("cost_in_cents") private Long costInCents;
	@JsonProperty("description")  private String description;
	@JsonProperty("available_online") private Boolean availableOnline;
	@JsonProperty("available_for_pickup") private Boolean availableForPickup;
	@JsonProperty("available_electronically") private Boolean availableElectronically;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
}
