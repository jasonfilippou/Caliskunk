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
	@JsonProperty("id") @NonNull private String clientProductId;    // Provided by client.
	@JsonProperty("product_backend_id") @NonNull private String squareItemId;    // Provided by Square.
	@JsonProperty("name")  @NonNull	private String name;
	@JsonProperty("product_type") @NonNull private String productType;
	@JsonProperty("cost")  @NonNull private Long costInCents;
	@JsonProperty("description")  private String description;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
	@JsonProperty("version") private Long version;
	@JsonProperty("is_deleted") private Boolean isDeleted;
	@JsonProperty("updated_at") private String updatedAt;


	public static BackendServiceResponseBody fromSquareResponse(@NonNull final SquareServiceResponseBody squareServiceResponse)
	{
		return builder()
			.name(squareServiceResponse.getName().strip().toUpperCase())
			.version(squareServiceResponse.getVersion())
			.squareItemId(squareServiceResponse.getSquareItemId())
			.isDeleted(squareServiceResponse.getIsDeleted())
			.clientProductId(squareServiceResponse.getClientProductId())
			.costInCents(squareServiceResponse.getCostInCents())
			.labelColor(squareServiceResponse.getLabelColor())
			.upc(squareServiceResponse.getUpc())
			.sku(squareServiceResponse.getSku())
			.productType(squareServiceResponse.getProductType())
			.updatedAt(squareServiceResponse.getUpdatedAt())
			.description(squareServiceResponse.getDescription())
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
					.name(product.getProductName().strip().toUpperCase())
					.costInCents(product.getCostInCents())
					.clientProductId(product.getClientProductId())
					.squareItemId(product.getSquareItemId())
					.productType(product.getProductType().strip().toUpperCase())
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
		                                    .name(response.getName().strip().toUpperCase())
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
