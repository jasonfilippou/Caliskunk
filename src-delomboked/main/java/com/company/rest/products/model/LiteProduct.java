package com.company.rest.products.model;

import com.company.rest.products.model.exceptions.InvalidProductTypeException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@Builder
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



	// Builder pattern to make life easier with construction
//	public static class Builder
//	{
//		private String name;
//		private Long id;
//		private String type;
//		private Long costInCents;
//
//		public Builder name(final String name)
//		{
//			this.name = name;
//			return this;
//		}
//
//
//		public Builder costInCents(final Long costInCents)
//		{
//			this.costInCents = costInCents;
//			return this;
//		}
//
//		public Builder type(final String type)
//		{
//			this.type = type;
//			return this;
//		}
//
//		public Builder id(Long id)
//		{
//			this.id = id;
//			return this;
//		}
//
//		public LiteProduct build()
//		{
//			return new LiteProduct(id, name, type, costInCents);
//		}
//	} // </Builder>

}