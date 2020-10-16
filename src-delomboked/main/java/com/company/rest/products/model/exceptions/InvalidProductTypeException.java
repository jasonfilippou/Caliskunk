package com.company.rest.products.model.exceptions;

public class InvalidProductTypeException extends RuntimeException
{
	public InvalidProductTypeException(String categoryId)
	{
		super("Invalid category provided: " + categoryId + ".");
	}
}


