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
@AllArgsConstructor
public class ProductUpsertRequestBody implements Serializable
{
	@JsonProperty("id") public String clientProductId;          // Not asserted @NonNull, since PUT and PATCH give them separately in URI.
	@JsonProperty("name")  public String name;
	@JsonProperty("product_type") public String productType;
	@JsonProperty("cost_in_cents") public Long costInCents;
	@JsonProperty("description")  public String description;
	@JsonProperty("available_online") public Boolean availableOnline;
	@JsonProperty("available_for_pickup") public Boolean availableForPickup;
	@JsonProperty("available_electronically") public Boolean availableElectronically;
	@JsonProperty("label_color") public String labelColor;
	@JsonProperty("sku") public String sku;
	@JsonProperty("upc") public String upc;
	public String squareProductId;   // Useful for Square Service - level queries. Never given by the user.
	public Long version;                // A field used by Square for UPDATEs. Will never be given by user.
}
