package com.company.rest.products.model;

import com.company.rest.products.model.exceptions.InvalidProductTypeException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
// Lombok's @Builder annotation was giving me trouble
// with this class (maybe because of the other annotations?).
// So I went ahead and implemented my own Builder instead.
//@Builder(access = AccessLevel.PUBLIC)
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
	
	public static LiteProductBuilder builder()
	{
		return new LiteProductBuilder();
	}
	public static class LiteProductBuilder 
	{
		private Long id;
		private String name;
		private String type;
		private Long costInCents;
		
		public LiteProductBuilder id(Long id)
		{
			this.id = id;
			return this;
		}
		
		public LiteProductBuilder name(String name)
		{
			this.name = name;
			return this;
		}
				
		public LiteProductBuilder type(String type)
		{
			this.type = type;
			return this;
		}
				
		public LiteProductBuilder costInCents(Long costInCents)
		{
			this.costInCents = costInCents;
			return this;
		}

		public LiteProduct build()
		{
			return new LiteProduct(id, name, type, costInCents);
		}
	}

}