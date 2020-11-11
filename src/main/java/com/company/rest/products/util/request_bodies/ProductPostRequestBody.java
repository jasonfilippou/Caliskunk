package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * A JSON POST request.
 *
 * @see ProductGetRequestBody
 * @see ProductDeleteRequestBody
 * @see ProductUpdateRequestBody
 * @see ProductResponseBody
 */
@Builder(access = AccessLevel.PUBLIC)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPostRequestBody implements Serializable
{
	@JsonProperty("name")  @NonNull 	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost_in_cents")  @NonNull private Long costInCents;
	@JsonProperty("product_id") @NonNull private String clientProductId;
	@JsonProperty("description")  private String description;
	@JsonProperty("available_online") private Boolean availableOnline;
	@JsonProperty("available_for_pickup") private Boolean availableForPickup;
	@JsonProperty("available_electronically") private Boolean availableElectronically;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
}
