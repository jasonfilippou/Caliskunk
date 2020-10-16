package com.company.rest.products.controller;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

/**
 * A class simulating a response payload to the client.
 * @see ProductRequestBody
 */
@Data
@Builder(access = AccessLevel.PUBLIC)
public class ProductResponseBody
{
	private String name;
	private Long id;
	private String productType;
	private Long costInCents;
	private String categoryId;
	private String description;
	private Boolean availableOnline;
	private Boolean availableForPickup;
	private Boolean availableElectronically;
	private String labelColor;
	private String sku;
	private String upc;

	private ProductResponseBody(
			final String name,
			final Long id,
			final String productType,
			final Long costInCents,
			final String categoryId,
			final String description,
			final Boolean availableOnline,
			final Boolean availableForPickup,
			final Boolean availableElectronically,
			final String labelColor,
			final String sku,
			final String upc)
	{
		this.name = name;
		this.id = id;
		this.productType = productType;
		this.costInCents = costInCents;
		this.categoryId = categoryId;
		this.description = description;
		this.availableOnline = availableOnline;
		this.availableForPickup = availableForPickup;
		this.availableElectronically = availableElectronically;
		this.labelColor = labelColor;
		this.sku = sku;
		this.upc = upc;
	}

	// The following might not be required given Lombok's @Builder annotation
//	public static class Builder
//	{
//		private String name;
//		private Long id;
//		private String productType;
//		private Long costInCents;
//		private String categoryId;
//		private String description;
//		private Boolean availableOnline;
//		private Boolean availableForPickup;
//		private Boolean availableElectronically;
//		private String labelColor;
//		private String sku;
//		private String upc;
//
//
//		public ProductResponseBody build()
//		{
//			return new ProductResponseBody(name, id, productType, costInCents,  categoryId,
//							  description,  availableOnline, availableForPickup,  availableElectronically,
//							  labelColor, sku, upc);
//		}
//	}
	
	
}
