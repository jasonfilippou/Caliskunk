package com.company.rest.products.controller;

import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.exceptions.BackendServiceException;
import com.company.rest.products.util.exceptions.InconsistentRequestException;
import com.company.rest.products.util.exceptions.ProductNotFoundException;
import com.company.rest.products.util.exceptions.ResourceAlreadyCreatedException;
import com.company.rest.products.util.request_bodies.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import static com.company.rest.products.util.Util.*;
import static java.util.Optional.ofNullable;
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
	public static final String SORT_BY = "costInCents";
	@Autowired
	public ProductController(final BackendService backendService)
	{
		this.backendService = backendService;
	}

	/* *************************************************************************************** */
	/* ********************   UTILITIES FOR INPUT VALIDATION  ******************************** */
	/* *************************************************************************************** */




	/* *************************************************************************************** */
	/* ********************   UTILITIES FOR RESPONDING TO CLIENT ***************************** */
	/* *************************************************************************************** */
	private ResponseEntity<ResponseMessage> response(final String requestStatus, final String message,
	                                                 final Object data, final HttpStatus httpStatus)
	{
		return new ResponseEntity<>(new ResponseMessage(requestStatus, message, data), httpStatus);
	}
	private ResponseEntity<ResponseMessage> failure(final String message, final HttpStatus httpStatus)
	{
		return response(ResponseMessage.FAILURE, message, null, httpStatus);
	}
	private ResponseEntity<ResponseMessage> failure(final Throwable thrown, final HttpStatus httpStatus)
	{
		return failure(thrown.getMessage(), httpStatus);
	}
	private ResponseEntity<ResponseMessage> success(final String message, final Object data,
	                                                final HttpStatus statusToReport)
	{
		return response(ResponseMessage.SUCCESS, message, data, statusToReport);
	}

	/* *************************************************************************************** */
	/* ************************** AGGREGATE ROOT GET AND POST ******************************** */
	/* *************************************************************************************** */
	/**
	 * Entry point for a GET ALL request.
	 *
	 * @param pageIdx     the current index of the page in the paginated response.
	 * @param itemsInPage the number of items in the current page.
	 * @param sortBy      the field of {@link LiteProduct} which we will use for sorting the products.
	 * @return an appropriate JSON response.
	 * @see BackendService#getAllProducts(Integer, Integer, String)
	 */
	@GetMapping(value = "/products")
	public ResponseEntity<ResponseMessage> getAll(@PathVariable(required = false, name = "page") Integer pageIdx,
	                                              @PathVariable(required = false, name = "items_in_page") Integer itemsInPage,
	                                              @PathVariable(required = false, name = "sort_by") String sortBy)
	{
		if (pageIdx == null) pageIdx = DEFAULT_PAGE_IDX;
		if (itemsInPage == null) itemsInPage = DEFAULT_PAGE_SIZE;
		if (sortBy == null) sortBy = "costInCents";
		if ((pageIdx < 0) || (itemsInPage < 1)) // Autoboxed
		{
			log.error("Received a bad GET ALL request (page=" + pageIdx + ", itemsInPage=" + itemsInPage);
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
				                             .collect(Collectors.toList()), HttpStatus.OK);
			}
			catch (BackendServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::getAll");
				return failure(exc, exc.getStatus());
			}
		}
	}
	/**
	 * Entry point for a POST request.
	 *
	 * @param postRequest The JSON body of the POST request.
	 * @return an appropriate JSON response.
	 * @see BackendService#postProduct(ProductUpsertRequestBody)
	 */
	@PostMapping(value = "/products")
	public ResponseEntity<ResponseMessage> postProduct(@RequestBody ProductUpsertRequestBody postRequest)
	{
		try
		{
			validatePostRequest(postRequest);
			final BackendServiceResponseBody backendResponse = backendService.postProduct(postRequest);
			validatePostResponse(backendResponse, postRequest);
			final ProductResponseBody productResponse = ProductResponseBody.fromBackendResponseBody(backendResponse); // TODO: change these static methods to class - specific ones.
			return success("Successfully posted product!", productResponse, HttpStatus.CREATED);
		}
		catch (InconsistentRequestException e)
		{
			logException(e, this.getClass().getName() + "::postProduct");
			return failure(e, HttpStatus.BAD_REQUEST);
		}
		catch (ResourceAlreadyCreatedException exc)
		{
			logException(exc, this.getClass().getName() + "::postProduct");
			return failure(exc, HttpStatus.CONFLICT);
		}
		catch (BackendServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::postProduct");
			return failure(exc, exc.getStatus());
		}
	}
	private void validatePostRequest(final ProductUpsertRequestBody postRequest) throws InconsistentRequestException
	{
		// Ensure that product ID, name, type, and cost values are appropriate.
		if (!crucialFieldsOk(postRequest))
		{
			throw new InconsistentRequestException("Bad POST request supplied.");
		}
		postRequest.setName(postRequest.getName().strip().toUpperCase());
	}
	private boolean crucialFieldsOk(final ProductUpsertRequestBody upsertRequest)
	{
		return idFieldPresent(upsertRequest) && nameCostAndProductTypeOk(upsertRequest);
	}
	private boolean idFieldPresent(final ProductUpsertRequestBody upsertRequest)
	{
		return upsertRequest.getClientProductId() != null && upsertRequest.getClientProductId().length() >= 1;
	}
	private boolean nameCostAndProductTypeOk(final ProductUpsertRequestBody upsertRequest)
	{
		// Ensure that name, type, and cost values are appropriate.
		return (upsertRequest.getCostInCents() != null && upsertRequest.getCostInCents() > 0L) &&
		       (upsertRequest.getProductType() != null && acceptedProductType(upsertRequest.getProductType())) &&
		       (upsertRequest.getName() != null && upsertRequest.getName().length() > 0);
	}
	private boolean acceptedProductType(final String productType)
	{
		return LiteProduct.PRODUCT_TYPES.contains(productType.trim().toUpperCase());
	}

	private void validatePostResponse(final BackendServiceResponseBody postResponse, final ProductUpsertRequestBody postRequest)
	{
		assertAndIfNotLogAndThrow(nullOrFalse(postResponse.getIsDeleted()) &&
		                          postResponse.getUpdatedAt() != null &&
		                          postResponse.getVersion() != null &&
		                          postResponse.getSquareItemId() != null &&
		                          postResponseMatchesRequest(postResponse, postRequest),
		                          "Upsert Request did not match response");
	}

	private boolean postResponseMatchesRequest(final BackendServiceResponseBody postResponse, final ProductUpsertRequestBody postRequest)
	{
		return 	postResponse.getClientProductId().equals(postRequest.getClientProductId()) &&
				optionalFieldsMatch(postResponse, postRequest);
	}

	private boolean optionalFieldsMatch(final BackendServiceResponseBody postResponse, final ProductUpsertRequestBody postRequest)
	{
		return ofNullable(postResponse.getName()).equals(ofNullable(postRequest.getName())) &&
		       ofNullable(postResponse.getProductType()).equals(ofNullable(postRequest.getProductType())) &&
		       ofNullable(postResponse.getCostInCents()).equals(ofNullable(postRequest.getCostInCents())) &&
		       ofNullable(postResponse.getDescription()).equals(ofNullable(postRequest.getDescription())) &&
		       ofNullable(postResponse.getLabelColor()).equals(ofNullable(postRequest.getLabelColor())) &&
		       ofNullable(postResponse.getSku()).equals(ofNullable(postRequest.getSku())) &&
		       ofNullable(postResponse.getUpc()).equals(ofNullable(postRequest.getUpc()));
	}

	/* *************************************************************************************** */
	/* ************************************ GET ONE ****************************************** */
	/* *************************************************************************************** */
	/**
	 * Entry point for a GET(id) request.
	 *
	 * @param id a non-{@code null} {@link String} representing the ID of the object we want to GET.
	 * @return an appropriate JSON response.
	 * @see BackendService#getProduct(ProductGetRequestBody)
	 */
	@GetMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> getProduct(@PathVariable("id") String id)
	{
		try
		{
			final ProductGetRequestBody getRequest = new ProductGetRequestBody(id);
			validateGetRequest(getRequest);
			final BackendServiceResponseBody backendResponse = backendService.getProduct(getRequest);
			validateGetResponse(backendResponse, getRequest);
			final ProductResponseBody productResponse = ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully got product with ID " + id + ".", productResponse, HttpStatus.FOUND);
		}
		catch (ProductNotFoundException e)
		{
			logException(e, this.getClass().getName() + "::getProduct");
			return failure(e, HttpStatus.NOT_FOUND);
		}
		catch (BackendServiceException e)
		{
			logException(e, this.getClass().getName() + "::getProduct");
			return failure(e, e.getStatus());
		}
	}

	private void validateGetRequest(final ProductGetRequestBody requestBody)
	{
		assertAndIfNotLogAndThrow(requestBody.getClientProductId() != null,
		                          "Bad GET request");        // As long as this is a path variable, we should be good.
	}

	private void validateGetResponse(final BackendServiceResponseBody getResponse, final ProductGetRequestBody getRequest)
	{
		assertAndIfNotLogAndThrow(getRequest.getClientProductId().equals(getResponse.getClientProductId()) , "Bad GET response");
	}
	/* *************************************************************************************** */
	/* *************************************** PUT ******************************************* */
	/* *************************************************************************************** */

	/**
	 * Entry point for a PUT(id) request.
	 * @param putRequest the new JSON data to replace the product with.
	 * @return an appropriate JSON response.
	 * @see BackendService#putProduct(ProductUpsertRequestBody)
	 */
	@PutMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> putProduct(@RequestBody ProductUpsertRequestBody putRequest,
	                                                  @PathVariable("id") String id)
	{
		try
		{
			validatePutRequest(putRequest);
			putRequest.setClientProductId(id); // And from now on the request has the ID in the body for the rest of its journey! :)
			final BackendServiceResponseBody backendResponse = backendService.putProduct(putRequest);
			validatePutResponse(backendResponse, putRequest);
			final ProductResponseBody productResponse = ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully updated product!", productResponse, HttpStatus.OK);
		}
		catch(InconsistentRequestException exc)
		{
			logException(exc, this.getClass().getName() + "::putProduct");
			return failure(exc, HttpStatus.BAD_REQUEST);
		}
		catch(BackendServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::putProduct");
			return failure(exc, exc.getStatus());
		}
	}

	private void validatePutRequest(final ProductUpsertRequestBody putRequest) throws InconsistentRequestException
	{
		if (putRequest.getVersion() == null)
		{
			throw new InconsistentRequestException("Request had no version ID.");
		}
		putRequest.setName(putRequest.getName().strip().toUpperCase());
	}

	private void validatePutResponse(final BackendServiceResponseBody putResponse, final ProductUpsertRequestBody putRequest)
	{
		assertAndIfNotLogAndThrow(nullOrFalse(putResponse.getIsDeleted()) &&
		                          putResponse.getUpdatedAt() != null &&
		                          putResponse.getVersion() != null &&
		                          putResponse.getSquareItemId() != null &&
		                          putResponseMatchesRequest(putResponse, putRequest),
		                          "Upsert Request did not match response");
	}


	private boolean putResponseMatchesRequest(final BackendServiceResponseBody putResponse, final ProductUpsertRequestBody putRequest)
	{
		return optionalFieldsMatch(putResponse, putRequest);
	}

	/* *************************************************************************************** */
	/* *************************************** PATCH ***************************************** */
	/* *************************************************************************************** */

	/**
	 * Entry point for a PATCH(id) request.
	 * @param request the JSON body with the new product data with which we want to update the product.
	 * @param id the ID of the existing product to rupdate.
	 * @see BackendService#patchProduct(ProductUpsertRequestBody, String)
	 * @return an appropriate JSON response.
	 */
	@PatchMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> patchProduct(@RequestBody ProductUpsertRequestBody request,
	                                                    @PathVariable("id") String id)
	{
		try
		{
			final BackendServiceResponseBody backendResponse = backendService.patchProduct(request, id);
			final ProductResponseBody productResponse = ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully updated product!", productResponse, HttpStatus.OK);
		}
		catch(BackendServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::patchProduct");
			return failure(exc, exc.getStatus());
		}
	}

	/* *************************************************************************************** */
	/* *************************************** DELETE ***************************************** */
	/* *************************************************************************************** */

	/**
	 * Entry point for a DELETE(id) request.
	 * @param id The ID of the object to delete.
	 * @see BackendService#deleteProduct(ProductDeleteRequestBody)
	 * @return an appropriate JSON response.
	 */
	@DeleteMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable("id") String id)
	{
		try
		{
			final ProductDeleteRequestBody deleteRequest = new ProductDeleteRequestBody(id);
			validateDeleteRequest(deleteRequest);
			final BackendServiceResponseBody backendResponse = backendService.deleteProduct(new ProductDeleteRequestBody(id));
			validateDeleteResponse(backendResponse, deleteRequest);
			final ProductResponseBody productResponse =  ProductResponseBody.fromBackendResponseBody(backendResponse);
			return success("Successfully deleted product with ID " + id + ".",
			               productResponse, HttpStatus.OK);
		}
		catch (BackendServiceException e)
		{
			logException(e, this.getClass().getName() + "::deleteProduct");
			return failure(e, e.getStatus());
		}
	}

	private void validateDeleteRequest(final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(deleteRequest.getClientProductId() != null, "Bad DELETE request");
	}

	private void validateDeleteResponse(final BackendServiceResponseBody deleteResponse, final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(deleteRequest.getClientProductId().equals(deleteResponse.getClientProductId()),
		                          "Bad DELETE response");
	}

}