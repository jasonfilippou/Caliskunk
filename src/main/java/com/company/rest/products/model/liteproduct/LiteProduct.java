package com.company.rest.products.model.liteproduct;

import com.company.rest.products.util.exceptions.InvalidProductTypeException;
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
	private String name;
	private Long costInCents;
	private String type;

	 // Model the product types as a Hash Set in case we end up with several
	 // and need fast retrieval. The types are uppercased by convention.
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("FLOWER", "TOPICAL", "VAPORIZER", "EDIBLE", "PREROLL", "CONCENTRATE",
			                "TINCTURE", "PET", "ACCESSORY", "OTHER"));


	public LiteProduct(@NonNull final String squareItemId, @NonNull final String squareItemVariationId,
	                   @NonNull final String name, @NonNull final Long costInCents, @NonNull final String type)
	{
		if(!PRODUCT_TYPES.contains(type))
		{
			InvalidProductTypeException exc = new InvalidProductTypeException(type);
			logException(exc, this.getClass().getEnclosingMethod().getName());
			throw new InvalidProductTypeException(type);
		}
		else
		{
			this.name = name;
			this.squareItemId = squareItemId;
			this.squareItemVariationId = squareItemVariationId;
			this.costInCents = costInCents;
		}
	}
}