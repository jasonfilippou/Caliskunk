package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.SquareService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogItemVariation;
import com.squareup.square.models.CatalogObject;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * A projection of the response objects returned by Square on the fields that <i>might</i> interest our application.
 * @see SquareService
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class SquareServiceResponseBody implements Serializable
{
	@JsonProperty("product_backend_id") @NonNull private String squareItemId;    // Provided by Square.
	@JsonProperty("product_variation_backend_id") @NonNull private String squareItemVariationId;    // Provided by Square.
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("cost")  @NonNull private Long costInCents;
	@JsonProperty("description")  private String description;
	@JsonProperty("available_online") private Boolean availableOnline;
	@JsonProperty("available_for_pickup") private Boolean availableForPickup;
	@JsonProperty("available_electronically") private Boolean availableElectronically;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
	@JsonProperty("version") private Long version;
	@JsonProperty("is_deleted") private Boolean isDeleted;              // Boolean flag implementing soft deletion.
	@JsonProperty("present_at_all_locations") private Boolean presentAtAllLocations;
	@JsonProperty("tax_ids") private List<String> taxIDs;
	@JsonProperty("updatedAt") private String updatedAt;


	public static SquareServiceResponseBody fromSquareData(@NonNull final CatalogObject itemObject,
	                                                       @NonNull final CatalogObject itemVarObject)
	{
		@NonNull final CatalogItem item = itemObject.getItemData();
		@NonNull final CatalogItemVariation variation = itemVarObject.getItemVariationData();

		return SquareServiceResponseBody
						.builder()

							// Data contained in the CatalogObject instance
							.squareItemId(itemObject.getId())
							.updatedAt(itemObject.getUpdatedAt())
							.isDeleted(itemObject.getIsDeleted())
							.presentAtAllLocations(itemObject.getPresentAtAllLocations())
							.version(itemObject.getVersion())

							// Data pulled from the CatalogItem instance
							.name(item.getName())
							.description(item.getDescription())
							.availableElectronically(item.getAvailableElectronically())
							.availableForPickup(item.getAvailableForPickup())
							.availableOnline(item.getAvailableOnline())
							.labelColor(item.getLabelColor())


							// Data pulled from CatalogItemVariation instance
							.squareItemVariationId(itemVarObject.getId())
							.costInCents(variation.getPriceMoney().getAmount())
							.sku(variation.getSku())
							.upc(variation.getUpc())

						.build();
	}
}
