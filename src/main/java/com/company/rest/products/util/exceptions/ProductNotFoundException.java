package com.company.rest.products.util.exceptions;

public class ProductNotFoundException extends RuntimeException
{
	public ProductNotFoundException(String name)
	{
		super("Could not find product " + name);
	}
}
