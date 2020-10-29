package com.company.rest.products.util.exceptions;

public class InvalidProductTypeException extends RuntimeException
{
	public InvalidProductTypeException(String productType)
	{
		super("Invalid prroduct category provided: " + productType + ".");
	}
}


