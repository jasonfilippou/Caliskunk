package com.company.rest.products.util.request_bodies;
import com.company.rest.products.model.SquareService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.square.models.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

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
	@JsonProperty("product_client_id")
	@NonNull
	private String clientProductId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("cost")
	private Long costInCents;
	@JsonProperty("product_type")
	private String productType;
	@JsonProperty("product_backend_id")
	@NonNull
	private String squareItemId;    // Provided by Square.
	@JsonProperty("description")
	private String description;
	@JsonProperty("available_online")
	private Boolean availableOnline;
	@JsonProperty("available_for_pickup")
	private Boolean availableForPickup;
	@JsonProperty("available_electronically")
	private Boolean availableElectronically;
	@JsonProperty("label_color")
	private String labelColor;
	@JsonProperty("sku")
	private String sku;
	@JsonProperty("upc")
	private String upc;
	@JsonProperty("version")
	private Long version;                       // Essential field for PUT requests.
	@JsonProperty("is_deleted")
	private Boolean isDeleted;              // Boolean flag implementing soft deletion.
	@JsonProperty("present_at_all_locations")
	private Boolean presentAtAllLocations;
	@JsonProperty("tax_ids")
	private List<String> taxIDs;
	@JsonProperty("updated_at")
	private String updatedAt;

	/* *************************************************************************************** */
	/* **************************** STATIC BUILDERS ****************************************** */
	/* *************************************************************************************** */
	/**
	 * Build a {@link SquareServiceResponseBody} out of a {@link ProductUpsertRequestBody} and an {@link UpsertCatalogObjectResponse}.
	 * @param clientUpsertRequest  An instance of {@link ProductUpsertRequestBody}.
	 * @param squareUpsertResponse An instance of {@link UpsertCatalogObjectResponse}.
	 * @return A new {@link SquareServiceResponseBody} with fields based on params.
	 */
	public static SquareServiceResponseBody fromUpsertRequestAndResponse(@NonNull final ProductUpsertRequestBody clientUpsertRequest,
	                                                                     @NonNull final UpsertCatalogObjectResponse squareUpsertResponse)
	{
		final CatalogObject catalogObject = squareUpsertResponse.getCatalogObject();
		final CatalogItem catalogItem = catalogObject.getItemData();
		final CatalogItemVariation catalogItemVariation = catalogItem.getVariations().get(0).getItemVariationData();
		return SquareServiceResponseBody.builder()
		                                .clientProductId(clientUpsertRequest.getClientProductId())
		                                .name(catalogItem.getName())
		                                .costInCents(catalogItemVariation.getPriceMoney().getAmount())
		                                .productType(clientUpsertRequest.getProductType())
		                                .squareItemId(catalogObject.getId())
		                                .description(catalogItem.getDescription())
		                                .availableOnline(catalogItem.getAvailableOnline())
		                                .availableForPickup(catalogItem.getAvailableForPickup())
		                                .availableElectronically(catalogItem.getAvailableElectronically())
		                                .labelColor(catalogItem.getLabelColor())
		                                .sku(catalogItemVariation.getSku())
		                                .upc(catalogItemVariation.getUpc())
		                                .version(catalogObject.getVersion())
		                                .isDeleted(catalogObject.getIsDeleted())
		                                .presentAtAllLocations(catalogObject.getPresentAtAllLocations())
		                                .taxIDs(catalogItem.getTaxIds())
		                                .updatedAt(catalogObject.getUpdatedAt())
		                                .build();
	}
	/**
	 * Build a {@link SquareServiceResponseBody} out of a {@link ProductGetRequestBody} and a {@link RetrieveCatalogObjectResponse}.
	 * @param clientGetRequest  An instance of {@link ProductGetRequestBody}.
	 * @param squareGetResponse An instance of {@link RetrieveCatalogObjectResponse}.
	 * @return A new {@link SquareServiceResponseBody} with fields based on params.
	 */
	public static SquareServiceResponseBody fromGetRequestAndResponse(final ProductGetRequestBody clientGetRequest,
	                                                                  final RetrieveCatalogObjectResponse squareGetResponse)
	{
		final CatalogObject catalogObject = squareGetResponse.getObject();
		final CatalogItem catalogItem = catalogObject.getItemData();
		final CatalogItemVariation catalogItemVariation = catalogItem.getVariations().get(0).getItemVariationData();
		return SquareServiceResponseBody.builder()
		                                .clientProductId(clientGetRequest.getClientProductId())
		                                .name(catalogItem.getName())
		                                .costInCents(catalogItemVariation.getPriceMoney().getAmount())
		                                .productType(clientGetRequest.getLiteProduct().getProductType())
		                                .squareItemId(catalogObject.getId())
		                                .description(catalogItem.getDescription())
		                                .availableOnline(catalogItem.getAvailableOnline())
		                                .availableForPickup(catalogItem.getAvailableForPickup())
		                                .availableElectronically(catalogItem.getAvailableElectronically())
		                                .labelColor(catalogItem.getLabelColor())
		                                .sku(catalogItemVariation.getSku())
		                                .upc(catalogItemVariation.getUpc())
		                                .version(catalogObject.getVersion())
		                                .isDeleted(catalogObject.getIsDeleted())
		                                .presentAtAllLocations(catalogObject.getPresentAtAllLocations())
		                                .taxIDs(catalogItem.getTaxIds())
		                                .updatedAt(catalogObject.getUpdatedAt())
		                                .build();
	}

	/**
	 * Build a {@link SquareServiceResponseBody} out of a {@link ProductDeleteRequestBody} and an {@link DeleteCatalogObjectResponse}.
	 * @param clientDeleteRequest  An instance of {@link ProductDeleteRequestBody}.
	 * @param squareDeleteResponse An instance of {@link UpsertCatalogObjectResponse}.
	 * @return A new {@link SquareServiceResponseBody} with fields based on params.
	 */
	public static SquareServiceResponseBody fromDeleteRequestAndResponse(final ProductDeleteRequestBody clientDeleteRequest,
	                                                                     final DeleteCatalogObjectResponse squareDeleteResponse)
	{

		return SquareServiceResponseBody.builder()
		                                .clientProductId(clientDeleteRequest.getClientProductId())
		                                .name(clientDeleteRequest.getLiteProduct().getProductName())
		                                .costInCents(clientDeleteRequest.getLiteProduct().getCostInCents())
		                                .productType(clientDeleteRequest.getLiteProduct().getProductType())
		                                .squareItemId(clientDeleteRequest.getLiteProduct().getSquareItemId())
		                                .isDeleted(true)
		                                .updatedAt(squareDeleteResponse.getDeletedAt())
		                                .build();
	}
}
