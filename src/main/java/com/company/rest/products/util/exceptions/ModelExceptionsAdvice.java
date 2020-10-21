package com.company.rest.products.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ModelExceptionsAdvice
{
	@ResponseBody
	@ExceptionHandler(InvalidProductTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String invalidProductTypeHandler(InvalidProductTypeException ex)
	{
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(ResourceAlreadyCreatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String resourceAlreadyCreatedHandler(ResourceAlreadyCreatedException ex)
	{
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(InconsistentRequestException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String inconsistentRequestHandler(InconsistentRequestException ex)
	{
		return ex.getMessage();
	}
}
