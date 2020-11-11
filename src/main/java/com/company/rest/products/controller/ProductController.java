package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.exceptions.BackendServiceException;
import com.company.rest.products.util.exceptions.InconsistentRequestException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.Util;
import com.company.rest.products.util.request_bodies.ProductUpdateRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Entry point to API. Decouples response and business logic by offloading all heavy lifting to {@link BackendService},
 * following an MVC pattern.
 *
 * @see BackendService
 * @see ResponseEntity
 * @see ResponseMessage
 */
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

	/**
	 * Entry point for a GET ALL request.
	 * @param pageIdx the current index of the page in the paginated response.
	 * @param itemsInPage the number of items in the current page.
	 * @param sortBy the field of {@link LiteProduct} which we will use for sorting the products.
	 * @see BackendService#getAllProducts(Integer, Integer, String)
	 * @return an appropriate JSON response.
	 */
	@GetMapping(value = "/products")
	public ResponseEntity<ResponseMessage> getAll(@PathVariable(required = false, name = "page") Integer pageIdx,
	                                              @PathVariable(required = false, name="items_in_page") Integer itemsInPage,
	                                              @PathVariable(required = false, name = "sort_by") String sortBy)
	{
		if(pageIdx == null) pageIdx = DEFAULT_PAGE_IDX;
		if(itemsInPage == null)	itemsInPage = DEFAULT_PAGE_SIZE;
		if(sortBy == null) sortBy = "costInCents";
		if((pageIdx < 0) || (itemsInPage < 1)) // Autoboxed
		{
			log.error("Received a bad GET ALL request (page=" +pageIdx +", itemsInPage = " + itemsInPage + ".");
			return failure("Please provide non-negative page and items per page parameters", HttpStatus.BAD_REQUEST);
		}
		else
		{
			try
			{
				return success("Successfully retrieved all products!",
				               backendService.getAllProducts(pageIdx, itemsInPage, sortBy)
				                             .parallelStream()
				                             .map(ProductResponseBody::fromBackendResponseBody)
				                             .collect(Collectors.toList()));
			}
			catch (BackendServiceException exc)
			{
				Util.logException(exc, this.getClass().getName() + "::getAll");
				return failure(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Entry point for a POST request.
	 * @see BackendService#postProduct(ProductPostRequestBody)
	 * @param request The JSON body of the POST request.
	 * @return an appropriate JSON response.
	 */
	@PostMapping(value = "/products")
	public ResponseEntity<ResponseMessage> postProduct(@RequestBody ProductPostRequestBody request)
	{
		try
		{
			validatePostRequest(request);
			final BackendServiceResponseBody backendResponse = backendService.postProduct(request);
			final ProductResponseBody productResponse = ProductResponseBody
															.fromBackendResponseBody(backendResponse);
			return success("Successfully posted product!", productResponse);
		}
		catch(InconsistentRequestException exc)
		{
			Util.logException(exc, this.getClass().getName() + "::postProduct");
			return failure(exc, HttpStatus.BAD_REQUEST);
		}
		catch (BackendServiceException exc)
		{
			Util.logException(exc, this.getClass().getName() + "::postProduct");
			return failure(exc, exc.getStatus());
		}
	}

	private void validatePostRequest(ProductPostRequestBody postRequest) throws InconsistentRequestException
	{
		// Ensure that product ID, name, type, and cost values are appropriate.
		if ( ! (
				postRequest.getClientProductId() != null && postRequest.getClientProductId().length() > 1

									        &&

                  postRequest.getCostInCents() != null && postRequest.getCostInCents() > 0L

                                            &&

                  postRequest.getProductType() != null && badProductType(postRequest.getProductType())

									        &&

                  postRequest.getName() != null && postRequest.getName().length() > 0

				)
			)
		{
			throw new InconsistentRequestException("Bad post request supplied.");
		}

	}

	private boolean badProductType(final String productType)
	{
		return LiteProduct.PRODUCT_TYPES.contains(productType.trim().toUpperCase());
	}

	/* *************************************************************************************** */
	/* ************************************ GET ONE ****************************************** */
	/* *************************************************************************************** */

	/**
	 * Entry point for a GET(id) request.
	 * @param id a non-{@code null} {@link String} representing the ID of the object we want to GET.
     * @see BackendService#getProduct(String)
	 * @return an appropriate JSON response.
	 */
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
			Util.logException(e, this.getClass().getName() + "::getProduct");
			return failure(e, e.getStatus());
		}
	}


	/* *************************************************************************************** */
	/* *************************************** PUT ******************************************* */
	/* *************************************************************************************** */


	/**
	 * Entry point for a PUT(id) request.
	 * @param request the new JSON data to replace the product with.
	 * @param id  the ID of the existing product to replace.
	 * @return an appropriate JSON response.
	 * @see BackendService#putProduct(String, ProductPostRequestBody)
	 */
	@PutMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> putProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable("id") String id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** PATCH ***************************************** */
	/* *************************************************************************************** */

	/**
	 * Entry point for a PATCH(id) request.
	 * @param request the JSON body with the new product data with which we want to update the product.
	 * @param id the ID of the existing product to rupdate.
	 * @see BackendService#patchProduct(String, ProductUpdateRequestBody)
	 * @return an appropriate JSON response.
	 */
	@PatchMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> patchProduct(@RequestBody ProductPostRequestBody request,
	                                         @PathVariable("id") String id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/* *************************************************************************************** */
	/* *************************************** DELETE ***************************************** */
	/* *************************************************************************************** */

	/**
	 * Entry point for a DELETE(id) request.
	 * @param id The ID of the object to delete.
	 * @see BackendService#deleteProduct(String)
	 * @return an appropriate JSON response.
	 */
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
			Util.logException(e, this.getClass().getName() + "::deleteProduct");
			return failure(e, e.getStatus());
		}
	}
}