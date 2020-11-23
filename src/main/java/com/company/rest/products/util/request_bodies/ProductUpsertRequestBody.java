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
	@JsonProperty("id") public String clientProductId;          // Not asserted @NonNull, since PUT and PATCH give them separately in URI.
	@JsonProperty("name")  public String name;
	@JsonProperty("product_type") public String productType;
	@JsonProperty("cost_in_cents") public Long costInCents;
	@JsonProperty("description")  public String description;
	@JsonProperty("label_color") public String labelColor;
	@JsonProperty("sku") public String sku;
	@JsonProperty("upc") public String upc;
	public String squareProductId;   // Useful for Square Service - level queries. Never given by the user.
	public Long version;                // A field used by Square for UPDATEs. Will never be given by user.

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
		                                                    .productName(name)
		                                                    .productType(productType)
		                                                    .clientProductId(clientProductId)
		                                                    .costInCents(costInCents)
		                                                    .version(DEFAULT_VERSION_FOR_TESTS)
		                                                    .squareItemId("SOME_RANDOM_SQUARE_ID")
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
		                                                    .productName(name)
		                                                    .productType(productType)
		                                                    .clientProductId(clientProductId)
		                                                    .costInCents(costInCents)
		                                                    .version(DEFAULT_VERSION_FOR_TESTS)
		                                                    .squareItemId("SOME_RANDOM_SQUARE_ID")
		                                                    .build())
		                            .build();
	}
}



