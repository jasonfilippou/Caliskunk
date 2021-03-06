package com.company.rest.products.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

// This allows the server to serve a 404 page with the message of the thrown Exception.
// Without it, the response generated by the controller when an `ProductNotFoundException`instance is thrown
// appears like a 502 Internal Server Error on the client side.
@ControllerAdvice
class ProductNotFoundAdvice
{

	@ResponseBody // Needed explicitly since this is not a @RestController - annotated class
	@ExceptionHandler(ProductNotFoundException.class) // Tying the handler to the exception we want to "beautify"...
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String employeeNotFoundHandler(ProductNotFoundException ex)
	{
		return ex.getMessage();
	}
}