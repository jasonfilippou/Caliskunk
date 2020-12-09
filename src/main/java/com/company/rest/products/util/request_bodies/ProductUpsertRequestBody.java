package com.company.rest.products.util.request_bodies;

import com.company.rest.products.model.liteproduct.LiteProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

import static com.company.rest.products.util.Util.DEFAULT_VERSION_FOR_TESTS;
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
	@JsonProperty("id") private String clientProductId;          // Not asserted @NonNull, since PUT and PATCH give them separately in URI.
	@JsonProperty("product_name")  private String productName;
	@JsonProperty("product_type") private String productType;
	@JsonProperty("cost_in_cents") private Long costInCents;
	@JsonProperty("description")  private String description;
	@JsonProperty("label_color") private String labelColor;
	@JsonProperty("sku") private String sku;
	@JsonProperty("upc") private String upc;
	private String squareItemId;                             // Useful for Square Service - level queries. Never given by the user.
	private String squareItemVariationId;                   // Same
	private Long version;                                   // Same

	/**
	 * Projects the fields of {@code this} that correspond to {@link ProductGetRequestBody} and returns such an instance.
	 * Useful in testing.
	 * @return An instance of {@link ProductGetRequestBody}.
	 */
	public ProductGetRequestBody toProductGetRequestBody()
	{
		return ProductGetRequestBody.builder()
		                            .clientProductId(clientProductId)
		                            .liteProduct(LiteProduct.builder()
		                                                    .productName(productName)
		                                                    .productType(productType)
		                                                    .clientProductId(clientProductId)
		                                                    .costInCents(costInCents)
		                                                    .version(DEFAULT_VERSION_FOR_TESTS)
		                                                    .squareItemId("SOME_RANDOM_ITEM_ID")
		                                                    .squareItemVariationId("SOME_RANDOM_ITEM_VAR_ID")
		                                                    .build())
									.build();
	}


	/**
	 * Projects the fields of {@code this} that correspond to {@link ProductDeleteRequestBody} and returns such an instance.
	 * Useful in testing.
	 * @return An instance of {@link ProductDeleteRequestBody}.
	 */
	public ProductDeleteRequestBody toProductDeleteRequestBody()
	{
		return ProductDeleteRequestBody.builder()
		                            .clientProductId(clientProductId)
		                            .liteProduct(LiteProduct.builder()
		                                                    .productName(productName)
		                                                    .productType(productType)
		                                                    .clientProductId(clientProductId)
		                                                    .costInCents(costInCents)
		                                                    .version(DEFAULT_VERSION_FOR_TESTS)
		                                                    .squareItemId("SOME_RANDOM_ITEM_ID")
		                                                    .squareItemVariationId("SOME_RANDOM_ITEM_VARIATION_ID")
		                                                    .build())
		                            .build();
	}
}



