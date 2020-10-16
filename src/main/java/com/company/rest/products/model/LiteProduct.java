package com.company.rest.products.model;

import com.company.rest.products.model.exceptions.InvalidProductTypeException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(access = AccessLevel.PUBLIC)
@Entity
public class LiteProduct
{
	// Minimal information required by our application.
	@Id
	private Long id;	// Unique
	private String name;
	private Long costInCents;
	private String type;

	 // Model the product types as a Hash Set in case we end up with several
	 // and need fast retrieval.
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("flower", "topical", "vaporizer", "edible", "pet"));

	// Have to allow creation of products without args for Entities.
	public LiteProduct()
	{

	}

	public LiteProduct(@NonNull final Long id, @NonNull final String name,
	                   @NonNull final String type, @NonNull final Long costInCents)
	{
		if(!PRODUCT_TYPES.contains(type))
		{
			throw new InvalidProductTypeException(type);
		}
		this.name = name;
		this.id = id;
		this.costInCents = costInCents;
	}

}