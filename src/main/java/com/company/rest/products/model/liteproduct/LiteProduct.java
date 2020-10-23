package com.company.rest.products.model.liteproduct;

import com.company.rest.products.util.exceptions.InvalidProductTypeException;
import com.company.rest.products.util.json_objects.SquareServiceResponseBody;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.company.rest.products.util.Util.logException;

@Data
@Entity
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor // Required for Entities
public class LiteProduct
{
	@Id
	private String squareItemId;	        // Unique on Square
	private String squareItemVariationId;	// Unique on Square
	private String productName;
	private Long costInCents;
	private String productType;

	 // Model the product types as a Hash Set in case we end up with several
	 // and need fast retrieval. The types are uppercased by convention.
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("FLOWER", "TOPICAL", "VAPORIZER", "EDIBLE", "PREROLL", "CONCENTRATE",
			                "TINCTURE", "PET", "ACCESSORY", "OTHER"));


	public LiteProduct(@NonNull final String squareItemId, @NonNull final String squareItemVariationId,
	                   @NonNull final String productName, @NonNull final Long costInCents, @NonNull final String productType)
	{
		if(!PRODUCT_TYPES.contains(productType))
		{
			InvalidProductTypeException exc = new InvalidProductTypeException(productType);
			logException(exc, this.getClass().getEnclosingMethod().getName());
			throw new InvalidProductTypeException(productType);
		}
		else
		{
			this.productName = productName;
			this.squareItemId = squareItemId;
			this.squareItemVariationId = squareItemVariationId;
			this.costInCents = costInCents;
		}
	}

	/* Some static methods to create LiteProducts on the fly from various layer responses. */

	public static LiteProduct fromSquareResponse(SquareServiceResponseBody response)
	{
			return LiteProduct.builder()
								.squareItemId(response.getItemId())
			                    .squareItemVariationId(response.getItemVariationId())
								.productName(response.getName().toUpperCase().trim()) // Uppercasing name to make it case-insensitive
								.productType(response.getProductType().toUpperCase().trim()) // Product types uppercased by convention
								.costInCents(response.getCostInCents())
                              .build();
	}
}