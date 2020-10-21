package com.company.rest.products.util.json_objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * A class simulating a response payload to the client.
 * @see ProductPostRequestBody
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class BackendResponseBody implements Serializable
{
	// For now this class is almost identical to SquareServiceResponseBody, in order for us
	// to give a breadth of information to the user. However, this can change,
	// so separate logic can be useful when refactoring.
	@JsonProperty @NonNull private String itemId;
	@JsonProperty @NonNull private String itemVarId;
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
	@JsonProperty("version") private Long version;
	@JsonProperty("is_deleted") private Boolean isDeleted;
	@JsonProperty("present_at_all_locations") private Boolean presentAtAllLocations;
	@JsonProperty("tax_ids") private List<String> taxIDs;
	@JsonProperty("updatedAt") private String updatedAt;

	public static BackendResponseBody fromSquareResponseBody(SquareServiceResponseBody squareResponse)
	{
		// Right now all it does is copy data over, but this can change in the future.
		return builder()
					.itemId(squareResponse.getItemId())
					.itemVarId(squareResponse.getItemVariationId())
					.name(squareResponse.getName())
					.productType(squareResponse.getProductType())
					.costInCents(squareResponse.getCostInCents())
					.categoryId(squareResponse.getCategoryId())
					.description(squareResponse.getDescription())
					.isDeleted(squareResponse.getIsDeleted())
					.availableOnline(squareResponse.getAvailableOnline())
					.availableForPickup(squareResponse.getAvailableForPickup())
					.availableElectronically(squareResponse.getAvailableElectronically())
					.presentAtAllLocations(squareResponse.getPresentAtAllLocations())
					.labelColor(squareResponse.getLabelColor())
					.upc(squareResponse.getUpc())
					.sku(squareResponse.getSku())
					.version(squareResponse.getVersion())
					.taxIDs(squareResponse.getTaxIDs())
					.updatedAt(squareResponse.getUpdatedAt())
				.build();
	}
}
