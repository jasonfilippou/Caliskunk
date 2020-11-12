package com.company.rest.products.util.exceptions;

public class InconsistentRequestException extends Exception
{
	public InconsistentRequestException(String inconsistency)
	{
		super("An inconsistency was detected with the request: " + inconsistency);
	}
}
