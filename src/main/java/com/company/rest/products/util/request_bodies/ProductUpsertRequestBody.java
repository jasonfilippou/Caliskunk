package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

import static lombok.AccessLevel.PUBLIC;

/**
 * A class meant to provide the common interface of POST, PUT and PATCH requests.
 *
 * @see ProductUpsertRequestBody
 * @see ProductUpsertRequestBody
 * @see ProductUpsertRequestBody
 */
@Data
@Builder(access = PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpsertRequestBody implements Serializable
{
	@NonNull @JsonProperty("id") protected String clientProductId;
	@JsonProperty("name")  protected String name;
	@JsonProperty("product_type") protected String productType;
	@JsonProperty("cost_in_cents") protected Long costInCents;
	@JsonProperty("description")  protected String description;
	@JsonProperty("available_online") protected Boolean availableOnline;
	@JsonProperty("available_for_pickup") protected Boolean availableForPickup;
	@JsonProperty("available_electronically") protected Boolean availableElectronically;
	@JsonProperty("label_color") protected String labelColor;
	@JsonProperty("sku") protected String sku;
	@JsonProperty("upc") protected String upc;
}
