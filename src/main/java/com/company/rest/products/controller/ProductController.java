package com.company.rest.products.controller;

import com.company.rest.products.controller.exceptions.ProductNotFoundException;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.LiteProduct;
import com.company.rest.products.model.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.UnimplementedMethodPlaceholder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController // So no serving views of any kind
@Component
@Slf4j
class ProductController
{
	private final LiteProductRepository repository;

	@Autowired
	public ProductController(final LiteProductRepository repository)
	{
		this.repository = repository;
	}

	/* *************************************************************************************** */
	/* ********************   UTILITIES FOR RESPONDING TO CLIENT ***************************** */
	/* *************************************************************************************** */
	private static ResponseEntity<ResponseMessage> response(String requestStatus, String message,
	                                                        Object data, HttpStatus httpStatus)
	{
		return new ResponseEntity<>(new ResponseMessage(requestStatus,message,data), httpStatus);
	}

	private static ResponseEntity<ResponseMessage> failure(String message, HttpStatus httpStatus)
	{
		return response(ResponseMessage.FAILURE,message,null, httpStatus);
	}

	private static ResponseEntity<ResponseMessage> failure(Throwable thrown, HttpStatus httpStatus)
	{
		return failure(thrown.getMessage(), httpStatus);
	}

	private static ResponseEntity<ResponseMessage> success(String message,
	                                                        Object data, HttpStatus httpStatus)
	{
		return response(ResponseMessage.SUCCESS, message, data, httpStatus);
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
		return success("Successfully hit GET ALL endpoint",
		               repository.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/products")
	public ResponseEntity<ResponseMessage> postProduct(@RequestBody ProductRequestBody request)
	{
		// First, we check to see if the product is in our local cache. If it is,
		// we should notify client of conflict and do nothing else.
		// Otherwise, we make the backend POST request as is normal.
		return repository.findById(request.getId())
			.map(product ->
		     {
		        log.warn("A POST request was made for an already existing resource: " + request + ".");
		        return failure("Product with ID " + product.getId() +
		                        " already exists!", HttpStatus.CONFLICT);
		     })
			.orElseGet(() ->
	         {
	         	final LiteProduct liteProduct = repository.save(createLiteProductOutOfRequest(request));
	            BackendService.postProduct(request);
	            return success("Successfully created product!",
	                           liteProduct, HttpStatus.CREATED);
	         });
	}

	private static LiteProduct createLiteProductOutOfRequest(ProductRequestBody request)
	{
		return LiteProduct
				.builder()
				.id(request.getId())
				.name(request.getName())
				.costInCents(request.getCostInCents())
				.type(request.getProductType())
				.build();
	}
	/* *************************************************************************************** */
	/* ************************************ GET ONE ****************************************** */
	/* *************************************************************************************** */

	@GetMapping("/product/{id}")
	public ResponseEntity<ResponseMessage> getProduct(@PathVariable Long id)
	{
		// First make a cheap check to ensure the product will be there.
		LiteProduct cachedProductInstance = repository.findById(id)
		                 .orElseThrow(() -> new ProductNotFoundException(id)); // Nicely formatted by registered advice class
		throw new UnimplementedMethodPlaceholder();
	}


	/* *************************************************************************************** */
	/* *************************************** PUT ******************************************* */
	/* *************************************************************************************** */


	@PutMapping("/products/{id}")
	public ResponseEntity<ResponseMessage> putProduct(@RequestBody ProductRequestBody request,
	                                         @PathVariable Long id)
	{
		if(!id.equals(request.getId()))
		{
			final String inconsistency = "Inconsistent PUT request: endpoint ID was " + id + " while item ID was " + request.getId() + ".";
			log.warn(inconsistency);
			return failure(inconsistency, HttpStatus.CONFLICT);
		}
		else
		{
			return repository
					.findById(request.getId())
			        .map(liteProduct ->
					{
					  updateLiteProduct(liteProduct, request);
					  BackendService.putProduct(id, request);
					  return success("Successfully updated product with id: "
					                 + id, liteProduct, HttpStatus.OK);
					})
                 .orElseGet(() ->
                    {
                        LiteProduct liteProduct = repository.save(createLiteProductOutOfRequest(request));
                        BackendService.putProduct(id, request);
                        return success("Successfully created product!", liteProduct,
                                       HttpStatus.CREATED);
                    });
		}
	}

	private void updateLiteProduct(LiteProduct product, ProductRequestBody request)
	{
		product.setName(request.getName());
		product.setId(request.getId());
		product.setCostInCents(request.getCostInCents());
	}

	/* *************************************************************************************** */
	/* *************************************** PATCH ***************************************** */
	/* *************************************************************************************** */

	@PatchMapping("/products/{id}")
	public ResponseEntity<ResponseMessage> patchProduct(@RequestBody ProductRequestBody request,
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