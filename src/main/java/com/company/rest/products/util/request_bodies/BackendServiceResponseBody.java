package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

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
	@JsonProperty("updated_at") private String updatedAt;

	/**
	 *
	 */
	public static BackendServiceResponseBody fromUpsertRequestAndResponse(@NonNull final ProductUpsertRequestBody upsertRequest,
	                                                                      @NonNull final SquareServiceResponseBody upsertResponse)
	{
		return builder()
				.name(upsertResponse.getName())
				.version(upsertResponse.getVersion())
				.squareItemId(upsertResponse.getSquareItemId())
				.availableElectronically(upsertResponse.getAvailableElectronically())
				.availableForPickup(upsertResponse.getAvailableForPickup())
				.availableOnline(upsertResponse.getAvailableOnline())
				.isDeleted(upsertResponse.getIsDeleted())
				.clientProductId(upsertResponse.getClientProductId())
				.costInCents(upsertResponse.getCostInCents())
				.labelColor(upsertResponse.getLabelColor())
				.upc(upsertResponse.getUpc())
				.sku(upsertResponse.getSku())
				.productType(upsertResponse.getProductType())
				.updatedAt(upsertResponse.getUpdatedAt())
				.description(upsertResponse.getDescription())
				.build();
	}

	public static BackendServiceResponseBody fromGetRequestAndResponse(@NonNull final ProductGetRequestBody getRequest,
	                                                                   @NonNull final SquareServiceResponseBody getResponse)
	{
		return builder()
				.name(getResponse.getName())
				.version(getResponse.getVersion())
				.squareItemId(getResponse.getSquareItemId())
				.availableElectronically(getResponse.getAvailableElectronically())
				.availableForPickup(getResponse.getAvailableForPickup())
				.availableOnline(getResponse.getAvailableOnline())
				.isDeleted(getResponse.getIsDeleted())
				.clientProductId(getResponse.getClientProductId())
				.costInCents(getResponse.getCostInCents())
				.labelColor(getResponse.getLabelColor())
				.upc(getResponse.getUpc())
				.sku(getResponse.getSku())
				.productType(getResponse.getProductType())
				.updatedAt(getResponse.getUpdatedAt())
				.description(getResponse.getDescription())
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
		return builder()
					.name(product.getProductName())
					.costInCents(product.getCostInCents())
					.clientProductId(product.getClientProductId())
					.squareItemId(product.getSquareItemId())
					.productType(product.getProductType())
					.version(product.getVersion())
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
		                                    .productType(response.getProductType())
		                                    .sku(response.getSku())
		                                    .upc(response.getUpc())
		                                    // The following are alright, since this is a class used only in mocks anyhow
		                                    .squareItemId("#RANDOM_SQUARE_ITEM_ID")
		                                    .updatedAt(response.getUpdatedAt())
		                                    .version(response.getVersion())
		                                 .build();
	}
}
