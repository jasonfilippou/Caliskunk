package com.company.rest.products.model.exceptions;

public class InconsistentRequestException extends Exception
{
	public InconsistentRequestException(String inconsistency)
	{
		super("An inconsistency was detected with the request: " + inconsistency + ".");
	}
}
