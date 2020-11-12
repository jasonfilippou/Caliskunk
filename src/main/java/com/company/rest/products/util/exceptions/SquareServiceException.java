package com.company.rest.products.util.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SquareServiceException extends RuntimeException
{
	private HttpStatus status;

	public SquareServiceException(Throwable t, HttpStatus status)
	{
		super("An exception in the Square service layer occurred: " + t.getClass().getName()
		      + " with message: " + t.getMessage());
		this.status = status;
	}
}
