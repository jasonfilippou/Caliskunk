package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.SquareService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * A response that {@link BackendService} provides {@link com.company.rest.products.controller.ProductController}.
 * @see SquareServiceResponseBody
 * @see ProductResponseBody
 * @see BackendService
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class BackendServiceResponseBody implements Serializable
{
	// For now this class is almost identical to SquareServiceResponseBody, in order for us
	// to give a breadth of information to the user. However, this can change,
	// so separate logic can be useful when refactoring.
	@JsonProperty("product_id") @NonNull private String clientProductId;    // Provided by client.
	@JsonProperty("product_backend_id") @NonNull private String squareItemId;    // Provided by Square.
	@JsonProperty("product_variation_backend_id") @NonNull private String squareItemVariationId;    // Provided by Square.
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost")  @NonNull private Long costInCents;
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
	@JsonProperty("updated_at") private String updatedAt;

	/**
	 * Create a response to feed to {@link com.company.rest.products.controller.ProductController} based on information
	 * mined from both {@link SquareService} <b>and</b> {@link com.company.rest.products.controller.ProductController}.
	 *
	 * @param squareResponse The response provided by {@link SquareService} after we called it.
	 *
	 * @return An instance of {@link BackendServiceResponseBody} that contains information about the run of {@link BackendService}.
	 */
	public static BackendServiceResponseBody buildBackendResponseBody(@NonNull final SquareServiceResponseBody squareResponse)
	{
		return builder()
					.clientProductId(squareResponse.getClientProductId())
					.squareItemId(squareResponse.getSquareItemId())
					.squareItemVariationId(squareResponse.getSquareItemVariationId())
					.name(squareResponse.getName())
					.productType(squareResponse.getProductType())
					.costInCents(squareResponse.getCostInCents())
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

	/**
	 * Use a {@link LiteProduct} instance to populate data to feed to {@link com.company.rest.products.controller.ProductController}.
	 * @param product The {@link LiteProduct} that we will use to build our response from.
	 * @return An instance of {@link BackendServiceResponseBody} which contains the data about what happened in the current
	 * 	               run of {@link BackendService}.
	 */
	public static BackendServiceResponseBody fromLiteProduct(@NonNull final LiteProduct product)
	{
		// Right now all it does is copy data over, but this can change in the future.
		return builder()
					.name(product.getProductName())
					.costInCents(product.getCostInCents())
					.clientProductId(product.getClientProductId())
					.squareItemId(product.getSquareItemId())
					.squareItemVariationId(product.getSquareItemVariationId())
					.productType(product.getProductType())
				.build();
	}

	/**
	 * A method useful for mocked tests, where we want the backend service to return data based on a
	 * request that has already been served.
	 *
	 * @param response The {@link ProductResponseBody} that contains information that will allow us to provide
	 *                  information about what happened in the {@link BackendService} run.
	 *
	 * @return An instance of {@link BackendServiceResponseBody} which contains the data about what happened in the current
	 * 	               run of {@link BackendService}.
	 */
	public static BackendServiceResponseBody fromProductResponseBody(@NonNull final ProductResponseBody response)
	{
		return BackendServiceResponseBody.builder()
		                                    .clientProductId(response.getClientProductId())
		                                    .availableElectronically(response.getAvailableElectronically())
		                                    .availableForPickup(response.getAvailableForPickup())
		                                    .availableOnline(response.getAvailableOnline())
		                                    .name(response.getName())
		                                    .costInCents(response.getCostInCents())
		                                    .description(response.getDescription())
		                                    .isDeleted(response.getIsDeleted())
		                                    .labelColor(response.getLabelColor())
		                                    .presentAtAllLocations(response.getPresentAtAllLocations())
		                                    .productType(response.getProductType())
		                                    .sku(response.getSku())
		                                    .upc(response.getUpc())
		                                    .taxIDs(response.getTaxIDs())
		                                    // The following are alright, since this is a class used only in mocks anyhow
		                                    .squareItemId("#RANDOM_SQUARE_ITEM_ID")
		                                    .squareItemVariationId("RANDOM_SQUARE_ITEM_VAR_ID")
		                                    .updatedAt(response.getUpdatedAt())
		                                    .version(response.getVersion())
		                                 .build();
	}
}
