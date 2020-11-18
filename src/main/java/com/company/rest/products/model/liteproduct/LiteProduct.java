package com.company.rest.products.model.liteproduct;
import com.company.rest.products.util.Util;
import com.company.rest.products.util.exceptions.InvalidProductTypeException;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor // Required for Entities
public class LiteProduct
{
	@Id
	private String clientProductId;                  // Provided by user
	private String squareItemId;	                // Provided by Square, guaranteed unique across all codes.
	private String squareItemVariationId;	        // Unique on Square
	private String productName;
	private String productType;
	private Long costInCents;

	 // Model the product types as a Hash Set in case we end up with several
	 // and need fast retrieval. The types are uppercased by convention.
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("FLOWER", "TOPICAL", "VAPORIZER", "EDIBLE", "PREROLL", "CONCENTRATE",
			                "TINCTURE", "PET", "ACCESSORY", "OTHER"));


	public LiteProduct(@NonNull final String clientProductId, final String squareItemId,
	                   final String squareItemVariationId, @NonNull final String productName,
	                   @NonNull final String productType, @NonNull final Long costInCents)
	{
		if(!PRODUCT_TYPES.contains(productType.strip().toUpperCase()))
		{
			final InvalidProductTypeException exc = new InvalidProductTypeException(productType);
			Util.logException(exc, this.getClass().getName() + "::LiteProduct");
			throw new InvalidProductTypeException(productType);
		}
		else
		{
			this.clientProductId = clientProductId;
			this.productName = productName.strip().toUpperCase();
			this.squareItemId = squareItemId;
			this.squareItemVariationId = squareItemVariationId;
			this.costInCents = costInCents;
			this.productType = productType;
		}
	}

	/* ******************************
	/* Some static methods to create LiteProducts on the fly from various layer responses. */

	/**
	 * Build a {@link LiteProduct} instance out of a {@link SquareServiceResponseBody} instance. This factory method
	 * can also supply {@code this} with information mined from Square and contained in the argument.
	 *
	 * @param response A {@link SquareServiceResponseBody} containing at least the necessary fields for creation
	 *                 of an instance of {@code this}.
	 * @return An instance of {@code this}.
	 */
	public static LiteProduct buildLiteProductFromSquareResponse(@NonNull final SquareServiceResponseBody response)
	{
			return LiteProduct.builder()
			                    .clientProductId(response.getClientProductId())
								.squareItemId(response.getSquareItemId())
			                    .squareItemVariationId(response.getSquareItemVariationId())
								.productName(response.getName().trim().toUpperCase()) // Uppercasing name to make it case-insensitive
								.productType(response.getProductType().trim().toUpperCase()) // Similar approach
								.costInCents(response.getCostInCents())
                              .build();
	}

	/**
	 * Build a {@link LiteProduct} instance out of two {@link ProductUpsertRequestBody} instances, one for a PUT
	 * request and another for its originating POST. Since UPDATE requests (PUT, PATCH) are allowed to change as many fields
	 * as they can, some of the crucial fields for the creation of a {@link LiteProduct} might be {@literal null}.
	 * So we need both requests to build a {@link LiteProduct} instance.
	 *
	 * @param putRequest A {@link ProductUpsertRequestBody} instance corresponding to a PUT request.
	 * @param postRequest A {@link ProductUpsertRequestBody} instance corresponding to the POST request that {@code putRequest}
	 *                    comes after.
	 * @return An instance of {@code this}.
	 */
	public static LiteProduct buildLiteProductFromPostAndPutRequests(@NonNull final ProductUpsertRequestBody putRequest,
	                                                                 @NonNull final ProductUpsertRequestBody postRequest)
	{
		return LiteProduct.builder()
		                  .clientProductId(postRequest.getClientProductId())
		                  .productName(Optional.of(putRequest.getName()).orElse(postRequest.getName()))
		                  .productType(Optional.of(putRequest.getProductType()).orElse(postRequest.getProductType()))
		                  .costInCents(Optional.of(putRequest.getCostInCents()).orElse(postRequest.getCostInCents()))
		                  .build();
	}
}