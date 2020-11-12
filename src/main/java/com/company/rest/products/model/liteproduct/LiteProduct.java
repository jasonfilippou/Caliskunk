package com.company.rest.products.model.liteproduct;

import com.company.rest.products.util.exceptions.InvalidProductTypeException;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.company.rest.products.util.Util;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
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

	// TODO: Enable (maybe downscaled?) images in the cached product information.

	 // Model the product types as a Hash Set in case we end up with several
	 // and need fast retrieval. The types are uppercased by convention.
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("FLOWER", "TOPICAL", "VAPORIZER", "EDIBLE", "PREROLL", "CONCENTRATE",
			                "TINCTURE", "PET", "ACCESSORY", "OTHER"));


	public LiteProduct(@NonNull final String clientProductId, @NonNull final String squareItemId,
	                   @NonNull final String squareItemVariationId, @NonNull final String productName,
	                   @NonNull final String productType, @NonNull final Long costInCents)
	{
		if(!PRODUCT_TYPES.contains(productType))
		{
			final InvalidProductTypeException exc = new InvalidProductTypeException(productType);
			Util.logException(exc, this.getClass().getName() + "::LiteProduct");
			throw new InvalidProductTypeException(productType);
		}
		else
		{
			this.clientProductId = clientProductId;
			this.productName = productName;
			this.squareItemId = squareItemId;
			this.squareItemVariationId = squareItemVariationId;
			this.costInCents = costInCents;
			this.productType = productType;
		}
	}

	/* Some static methods to create LiteProducts on the fly from various layer responses. */

	public static LiteProduct buildLiteProductFromSquareResponse(@NonNull final SquareServiceResponseBody response, @NonNull final String clientProductId,
	                                                             @NonNull final String productType)
	{
			return LiteProduct.builder()
			                    .clientProductId(clientProductId)
								.squareItemId(response.getSquareItemId())
			                    .squareItemVariationId(response.getSquareItemVariationId())
								.productName(response.getName().trim().toUpperCase()) // Uppercasing name to make it case-insensitive
								.productType(productType.trim().toUpperCase()) // Product types uppercased by convention
								.costInCents(response.getCostInCents())
                              .build();
	}
}