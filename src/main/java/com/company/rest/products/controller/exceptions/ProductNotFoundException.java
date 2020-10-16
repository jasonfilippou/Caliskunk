package com.company.rest.products.controller.exceptions;

public class ProductNotFoundException extends RuntimeException
{
	public ProductNotFoundException(Long id)
	{
		super("Could not find product " + id);
	}
}
