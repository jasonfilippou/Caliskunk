package com.company.rest.products.util.exceptions;

public class InvalidProductTypeException extends RuntimeException
{
	public InvalidProductTypeException(String categoryId)
	{
		super("Invalid prroduct category provided: " + categoryId + ".");
	}
}


