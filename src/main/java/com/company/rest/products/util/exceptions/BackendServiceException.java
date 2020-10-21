package com.company.rest.products.util.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BackendServiceException extends RuntimeException
{
	private HttpStatus status;

	public BackendServiceException(Throwable t, HttpStatus status)
	{
		super("An exception in the backend service layer occurred: " +
		      t.getClass().getName() + " with message: " + t.getMessage() + ".");
		this.status = status;
	}
}
