package com.company.rest.products.util.exceptions;
import lombok.NonNull;
public class ResourceAlreadyCreatedException extends RuntimeException
{
	public ResourceAlreadyCreatedException(@NonNull final String id)
	{
		super("Resource with id " + id + " already exists.");
	}
}
