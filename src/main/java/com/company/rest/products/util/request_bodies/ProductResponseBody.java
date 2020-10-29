package com.company.rest.products.util.request_bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@Data
@Builder(access = AccessLevel.PUBLIC)
//@AllArgsConstructor
public class ProductResponseBody implements Serializable
{
	// For now this class is almost identical to BackendServiceResponseBody, in order for us
	// to give a breadth of information to the user. However, this can change,
	// so separate logic can be useful when refactoring.
	@JsonProperty("product_id") @NonNull private String clientProductId;    // Provided by client.
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost_in_cents")  @NonNull private Long costInCents;
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

	public static ProductResponseBody fromBackendResponseBody(final BackendServiceResponseBody backendResponse)
	{
		// Right now all it does is copy data over, but this can change in the future.

		return builder()
					.clientProductId(backendResponse.getClientProductId())
					.name(backendResponse.getName())
					.productType(backendResponse.getProductType())
					.costInCents(backendResponse.getCostInCents())
					.categoryId(backendResponse.getCategoryId())
					.description(backendResponse.getDescription())
					.isDeleted(backendResponse.getIsDeleted())
					.availableOnline(backendResponse.getAvailableOnline())
					.availableForPickup(backendResponse.getAvailableForPickup())
					.availableElectronically(backendResponse.getAvailableElectronically())
					.presentAtAllLocations(backendResponse.getPresentAtAllLocations())
					.labelColor(backendResponse.getLabelColor())
					.upc(backendResponse.getUpc())
					.sku(backendResponse.getSku())
					.version(backendResponse.getVersion())
					.taxIDs(backendResponse.getTaxIDs())
					.updatedAt(backendResponse.getUpdatedAt())
				.build();
	}
}
