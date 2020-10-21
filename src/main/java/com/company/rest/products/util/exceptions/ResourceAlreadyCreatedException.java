package com.company.rest.products.util.exceptions;

public class ResourceAlreadyCreatedException extends RuntimeException
{
	public ResourceAlreadyCreatedException()
	{
		super("The resource already exists.");
	}
}
