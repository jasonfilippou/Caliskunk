package com.company.rest.products.util.json_objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
@Builder(access = AccessLevel.PUBLIC)   // To allow access to base class fields from a `Builder` instance.
public class ProductGetRequestBody implements Serializable
{
	@JsonProperty @NonNull	private String id;
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost")  @NonNull private Long costInCents;
	@JsonProperty("category_id")  private String categoryId;
	@JsonProperty("description")  private String description;
	@JsonProperty("available_online") private Boolean availableOnline;
	@JsonProperty("available_for_pickup") private Boolean availableForPickup;
	@JsonProperty("available_electronically") private Boolean availableElectronically;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
}
