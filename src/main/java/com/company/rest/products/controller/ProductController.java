package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.exceptions.BackendServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.company.rest.products.util.Util.logException;

@RestController // So no serving views of any kind
@Component
@Slf4j
public class ProductController
{
	private final BackendService backendService;
	public static final Integer DEFAULT_PAGE_SIZE = 10;
	public static final Integer DEFAULT_PAGE_IDX = 0;
	public 	static final String SORT_BY = "costInCents";

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
	public ResponseEntity<ResponseMessage> getAll(@PathVariable(required = false, name = "page") Integer page,
	                                              @PathVariable(required = false, name="items_in_page") Integer itemsInPage,
	                                              @PathVariable(required = false, name = "sort_by") String sortBy)
	{
		if(page == null) page = DEFAULT_PAGE_IDX;
		if(itemsInPage == null)	itemsInPage = DEFAULT_PAGE_SIZE;
		if(sortBy == null) sortBy = "costInCents";
		if((page < 0) || (itemsInPage < 1)) // Autoboxed
		{
			log.error("Received a bad GET ALL request (page=" +page +", itemsInPage = " + itemsInPage + ".");
			return failure("Please provide non-negative page and items per page parameters", HttpStatus.BAD_REQUEST);
		}
		try
		{
			return success("Successfully retrieved all products!",
			               backendService.getAllProducts(page, itemsInPage, sortBy)
			                             .parallelStream()
			                             .map(ProductResponseBody::fromBackendResponseBody)
			                             .collect(Collectors.toList()));
		}
		catch(BackendServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::getAll");
			return failure(exc, exc.getStatus());
		}
	}

	@PostMapping(value = "/products")
	public ResponseEntity<ResponseMessage> postProduct(@RequestBody ProductPostRequestBody request)
	{
		if(!LiteProduct.PRODUCT_TYPES.contains(request.getProductType().trim().toUpperCase()))
		{
			log.error("Received a bad product type; accepted product types are: " +
			          String.join("", LiteProduct.PRODUCT_TYPES));
			return failure("Invalid product type provided: Valid categories are: " +
			               new ArrayList<>(LiteProduct.PRODUCT_TYPES) + ".",
			               HttpStatus.BAD_REQUEST);
		}
		else if(request.getCostInCents() < 0)
		{
			return failure("Negative cost provided: " + request.getCostInCents() +".",
			               HttpStatus.BAD_REQUEST);
		}
		else
		{
			try
			{
				final BackendServiceResponseBody backendResponse = backendService.postProduct(request);
				final ProductResponseBody productResponse = ProductResponseBody
																.fromBackendResponseBody(backendResponse);
				return success("Successfully posted product!", productResponse);
			}
			catch (BackendServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::postProduct");
				return failure(exc, exc.getStatus());
			}
		}
	}

	/* *************************************************************************************** */
	/* ************************************ GET ONE ****************************************** */
	/* *************************************************************************************** */

	@GetMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> getProduct(@PathVariable("id") String id)
	{
		try
		{
			final BackendServiceResponseBody backendResponse = backendService.getProduct(id);
			final ProductResponseBody productResponse =  ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully got product with ID " + id + ".",
			               productResponse);
		}
		catch (BackendServiceException e)
		{
			logException(e, "::getProduct");
			return failure(e, e.getStatus());
		}
	}


	/* *************************************************************************************** */
	/* *************************************** PUT ******************************************* */
	/* *************************************************************************************** */


	@PutMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> putProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable("id") String id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** PATCH ***************************************** */
	/* *************************************************************************************** */

	@PatchMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> patchProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable("id") String id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** DELETE ***************************************** */
	/* *************************************************************************************** */

	@DeleteMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable("id") String id)
	{

		try
		{
			final BackendServiceResponseBody backendResponse = backendService.deleteProduct(id);
			final ProductResponseBody productResponse =  ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully got product with ID " + id + ".",
			               productResponse);
		}
		catch (BackendServiceException e)
		{
			logException(e, "::deleteProduct");
			return failure(e, e.getStatus());
		}
	}
}