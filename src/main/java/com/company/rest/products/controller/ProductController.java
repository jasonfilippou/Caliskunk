package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.exceptions.BackendServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.json_objects.BackendResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.ProductResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController // So no serving views of any kind
@Component
@Slf4j
public class ProductController
{
	private final BackendService backendService;

	@Autowired
	public ProductController(final BackendService backendService)
	{
		this.backendService = backendService;
	}

	/* *************************************************************************************** */
	/* ********************   UTILITIES FOR RESPONDING TO CLIENT ***************************** */
	/* *************************************************************************************** */

	private ResponseEntity<ResponseMessage> response(String requestStatus, String message,
	                                                        Object data, HttpStatus httpStatus)
	{
		return new ResponseEntity<>(new ResponseMessage(requestStatus,message,data), httpStatus);
	}

	private ResponseEntity<ResponseMessage> failure(String message, HttpStatus httpStatus)
	{
		return response(ResponseMessage.FAILURE, message,null, httpStatus);
	}

	private ResponseEntity<ResponseMessage> failure(Throwable thrown, HttpStatus httpStatus)
	{
		return failure(thrown.getMessage(), httpStatus);
	}

	private ResponseEntity<ResponseMessage> success(String message,
	                                                Object data)
	{
		return response(ResponseMessage.SUCCESS, message, data, HttpStatus.OK);
	}

	/* *************************************************************************************** */
	/* ************************** AGGREGATE ROOT GET AND POST ******************************** */
	/* *************************************************************************************** */

	@GetMapping(value = "/products")
	public ResponseEntity<ResponseMessage> getAll()
	{
		// For GET ALL, we will leverage the cached instances.
		// Then, if the client clicks on a specific product,
		// that will trigger a resource-specific GET, which we will
		// pull from the backend.
		throw new UnimplementedMethodPlaceholder();
	}

	@PostMapping(value = "/products")
	public ResponseEntity<ResponseMessage> postProduct(@RequestBody ProductPostRequestBody request)
	{
		// First, we check to see if the product is in our local cache. If it is,
		// we should notify client of conflict and do nothing else.
		// Otherwise, we make the backend POST request as is normal.
		if(!LiteProduct.PRODUCT_TYPES.contains(request.getProductType()))
		{
			return failure("Invalid product type provided: Valid categories are: " +
			               new ArrayList<>(LiteProduct.PRODUCT_TYPES) + ".",
			               HttpStatus.BAD_REQUEST);
		}
		else
		{
			try
			{
				BackendResponseBody backendResponse = backendService.postProduct(request);
				ProductResponseBody productResponse = ProductResponseBody.fromBackendResponseBody(backendResponse);
				return success("Successfully posted product!", productResponse);
			}
			catch (BackendServiceException exc)
			{
				return failure(exc, exc.getStatus());
			}
		}
	}

	/* *************************************************************************************** */
	/* ************************************ GET ONE ****************************************** */
	/* *************************************************************************************** */

	@GetMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> getProduct(@PathVariable String id)
	{
		try
		{
			BackendResponseBody backendResponse = backendService.getProduct(id);
			ProductResponseBody productResponse =  ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully got product with ID " + id + ".",
			               productResponse);
		}
		catch (BackendServiceException e)
		{
			return failure(e, e.getStatus());
		}
	}


	/* *************************************************************************************** */
	/* *************************************** PUT ******************************************* */
	/* *************************************************************************************** */


	@PutMapping("/products/{id}")
	public ResponseEntity<ResponseMessage> putProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable Long id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** PATCH ***************************************** */
	/* *************************************************************************************** */

	@PatchMapping("/products/{id}")
	public ResponseEntity<ResponseMessage> patchProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable Long id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** DELETE ***************************************** */
	/* *************************************************************************************** */

	@DeleteMapping("/products/{id}")
	public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable Long id)
	{
		throw new UnimplementedMethodPlaceholder();
	}
}