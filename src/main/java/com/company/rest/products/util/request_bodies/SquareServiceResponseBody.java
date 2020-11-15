package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogItemVariation;
import com.squareup.square.models.CatalogObject;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Information about the run of the {@link SquareService} routine that was called.
 *
 * @see SquareService
 * @see com.company.rest.products.model.BackendService
 * @see BackendServiceResponseBody
 * @see ProductResponseBody
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class SquareServiceResponseBody implements Serializable
{
	@JsonProperty("product_client_id") @NonNull private String clientProductId;
	@JsonProperty("name")  private String name;
	@JsonProperty("cost")   private Long costInCents;
	@JsonProperty("product_type") private String productType;
	@JsonProperty("product_backend_id") @NonNull private String squareItemId;    // Provided by Square.
	@JsonProperty("product_variation_backend_id") @NonNull private String squareItemVariationId;    // Provided by Square.
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
	@JsonProperty("updated_at") private String updatedAt;


	/**
	 * Populate data to feed to {@link BackendService} based on information mined by the Square API.
	 *
	 * @param itemObject The {@link CatalogObject} that wraps a {@link CatalogItem}, sent to us by Square.
	 * @param itemVarObject The {@link CatalogObject} that wraps a {@link CatalogItemVariation}, sent to us by Square.
	 * @return An instance of {@link SquareServiceResponseBody} which describes the information related to the current call.
	 */
	public static SquareServiceResponseBody fromSquareData(@NonNull final CatalogObject itemObject,
	                                                       @NonNull final CatalogObject itemVarObject,
	                                                       @NonNull final LiteProduct liteProduct)
	{
		final CatalogItem item = itemObject.getItemData();
		final CatalogItemVariation variation = itemVarObject.getItemVariationData();

		return SquareServiceResponseBody
						.builder()
							// Product client ID and type.
							.clientProductId(liteProduct.getClientProductId())
							.productType(liteProduct.getProductType())


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
