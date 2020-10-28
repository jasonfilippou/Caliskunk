package com.company.rest.products.util.json_objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Builder(access = AccessLevel.PUBLIC)
@Data
public class ProductPostRequestBody implements Serializable
{
	@JsonProperty("name")  @NonNull 	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost")  @NonNull private Long costInCents;
	@JsonProperty("product_id") @NonNull private String clientProductId;
	@JsonProperty("category_id")  private String categoryId;
	@JsonProperty("description")  private String description;
	@JsonProperty("available_online") private Boolean availableOnline;
	@JsonProperty("available_for_pickup") private Boolean availableForPickup;
	@JsonProperty("available_electronically") private Boolean availableElectronically;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
}
