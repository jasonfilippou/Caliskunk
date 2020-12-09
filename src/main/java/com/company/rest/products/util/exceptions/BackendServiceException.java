package com.company.rest.products.util.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


/**
 *
 */
@Getter
public class BackendServiceException extends RuntimeException
{
	private final HttpStatus status;

	/**
	 *
	 */
	public BackendServiceException(final Throwable t, final HttpStatus status)
	{
		super("An exception in the backend service layer occurred: " +
		      t.getClass().getName() + " with message: " + t.getMessage());
		this.status = status;
	}

	public BackendServiceException(final String msg, final HttpStatus status)
	{
		super("An exception in the backend service layer occurred: " + msg + ".");
		this.status = status;
	}

}
