package com.company.rest.products.model;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.exceptions.*;
import com.company.rest.products.util.json_objects.BackendServiceResponseBody;
import com.company.rest.products.util.json_objects.ProductPostRequestBody;
import com.company.rest.products.util.json_objects.SquareServiceResponseBody;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogItemVariation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.company.rest.products.util.Util.logException;

/**
 * Service class for {@link ProductController}.
 *
 * @see CatalogItem
 * @see CatalogItemVariation
 * @see SquareService
 * @see ProductController
 */

@Slf4j
@Component
public class BackendService
{
	private final SquareService squareService;
	private final LiteProductRepository localRepo;

	@Autowired
	public BackendService(LiteProductRepository localRepo)
	{
		this.localRepo = localRepo;
		this.squareService = new SquareService();
	}


	/**
	 * Handle a POST request to Square's API.
	 *
	 * @param request A {@link ProductPostRequestBody} instance containing details of the request.
	 * @see LiteProduct
	 * @see LiteProductRepository
	 * @throws BackendServiceException if the resource is already there.
	 */
	public BackendServiceResponseBody postProduct(ProductPostRequestBody request) throws BackendServiceException
	{
		// First, make a local check to ensure that there's no name clash for
		// the product uploaded. This is one of the advantages of having a cache.
		if(localRepo.findByName(request.getName()).isPresent())
		{
			final ResourceAlreadyCreatedException exc = new ResourceAlreadyCreatedException();
            logException(exc, this.getClass().getEnclosingMethod().getName());
            throw new BackendServiceException(exc, HttpStatus.CONFLICT);
		}
		else
			// We first POST to Square and *then* store the cached version in our
			// local DB in order to grab the unique ID that Square provides us with.
		{
			try
			{
				final SquareServiceResponseBody response = squareService.postProduct(request);
				localRepo.save(LiteProduct.fromSquareResponse(response));
				return BackendServiceResponseBody.fromSquareResponseBody(response);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getEnclosingMethod().getName());
                throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Send a GET request for a specific product.
	 * @param id The product's unique ID.
	 * @return A {@link ProductPostRequestBody} instance with the entire client-facing product data.
	 */
	public BackendServiceResponseBody getProduct(String id) throws BackendServiceException
	{
		// Cheap check first; if the product doesn't exist, why go to Square API with the request?
		Optional<LiteProduct> cached = localRepo.findBySquareItemId(id);
		if(cached.isEmpty())
		{
			ProductNotFoundException exc = new ProductNotFoundException(id);
            logException(exc, this.getClass().getEnclosingMethod().getName());
            throw new BackendServiceException(exc, HttpStatus.NOT_FOUND);
		}
		else
		{
			try
			{
				SquareServiceResponseBody response = squareService.getProduct(id, cached.get().getSquareItemVariationId());
				return BackendServiceResponseBody.fromSquareResponseBody(response);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getEnclosingMethod().getName());
				throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Serve a GET ALL request
	 * @return A {@link BackendServiceResponseBody} instance.
	 */
	public BackendServiceResponseBody getAllProducts()
	{
		// Paginated and sorted output whether it is on Square
		// or the cache.
		throw new UnimplementedMethodPlaceholder();
	}


	/**
	 * Send a PUT request for a specific product.
	 * @param id The product's unique id, provided by the request.
	 * @param request The request body.
	 */
	public BackendServiceResponseBody putProduct(String id, ProductPostRequestBody request)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param id The product's unique id.
	 */
	public BackendServiceResponseBody patchProduct(String id, ProductPostRequestBody request)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param id The product's unique id.
	 */
	public BackendServiceResponseBody deleteProduct(String id, ProductPostRequestBody newProduct)
	{
		throw new UnimplementedMethodPlaceholder();
	}
}
