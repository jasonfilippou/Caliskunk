package com.company.rest.products.model.exceptions;

public class ResourceAlreadyCreatedException extends RuntimeException
{
	public ResourceAlreadyCreatedException()
	{
		super("The resource already exists.");
	}
}
