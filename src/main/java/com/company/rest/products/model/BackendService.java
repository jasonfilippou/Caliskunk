package com.company.rest.products.model;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.exceptions.*;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductPostRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpdateRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogItemVariation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	public BackendService(LiteProductRepository localRepo, SquareService squareService)
	{
		this.localRepo = localRepo;
		this.squareService = squareService;
	}


	/**
	 * Handle a POST request to Square's API.
	 *
	 * @param request A {@link ProductPostRequestBody} instance containing details of the request.
	 * @see LiteProduct
	 * @see LiteProductRepository
	 * @throws BackendServiceException if the resource is already there.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody postProduct(ProductPostRequestBody request) throws BackendServiceException
	{
		// First, make a local check to ensure that there's no name clash for
		// the product uploaded. This is one of the advantages of having a cache.
		if(localRepo.findByProductName(request.getName()).isPresent())
		{
			final ResourceAlreadyCreatedException exc = new ResourceAlreadyCreatedException();
            logException(exc, this.getClass().getName() + "::postProduct");
            throw new BackendServiceException(exc, HttpStatus.CONFLICT);
		}
		else
			// We first POST to Square and *then* store the cached version in our
			// local DB in order to grab the unique ID that Square provides us with.
		{
			try
			{
				final SquareServiceResponseBody response = squareService.postProduct(request);
				localRepo.save(LiteProduct. buildLiteProduct(response, request.getClientProductId(), request.getProductType()));
				return BackendServiceResponseBody.buildBackendResponseBody(response, request.getClientProductId(),
				                                                           request.getProductType());
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::postProduct");
                throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Send a GET request for a specific product.
	 * @param clientProductId The product's unique ID assigned by the client.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody getProduct(String clientProductId) throws BackendServiceException
	{
		// Cheap check first; if the product doesn't exist, why go to Square API with the request?
		final Optional<LiteProduct> cached = localRepo.findByClientProductId(clientProductId);
		if(cached.isEmpty())
		{
			final ProductNotFoundException exc = new ProductNotFoundException(clientProductId);
            logException(exc, this.getClass().getName() + "::getProduct");
            throw new BackendServiceException(exc, HttpStatus.NOT_FOUND);
		}
		else
		{
			final String squareItemId = cached.get().getSquareItemId();
			final String squareItemVariationId = cached.get().getSquareItemVariationId();
			final String productType = cached.get().getProductType();
			try
			{
				final SquareServiceResponseBody squareServiceResponse = squareService.getProduct(squareItemId,  squareItemVariationId);
				return BackendServiceResponseBody.buildBackendResponseBody(squareServiceResponse, clientProductId, productType);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::getProduct");
				throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Serve a GET ALL request.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public List<BackendServiceResponseBody> getAllProducts(@NonNull final Integer page, @NonNull final Integer itemsInPage,
	                                                       @NonNull final String sortBy)
	{
		// Paginated and sorted output whether it is on Square or the cache.
		try
		{
			return localRepo.findAll(PageRequest.of(page, itemsInPage, Sort.by(sortBy).ascending()))
			                .stream().parallel()
			                .map(BackendServiceResponseBody::fromLiteProduct)
			                .collect(Collectors.toList());
		}
		catch(Throwable t)
		{
			logException(t, this.getClass().getName() + "::getAllProducts");
            throw new BackendServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * Send a PUT request for a specific product.
	 * @param clientProductId The product's unique id, provided by the request.
	 * @param newProductRequest The request body.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody putProduct(@NonNull String clientProductId, @NonNull ProductPostRequestBody newProductRequest)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param clientProductId The product's unique id.
	 * @param patchProductRequest The fields to update.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody patchProduct(@NonNull String clientProductId, @NonNull ProductUpdateRequestBody patchProductRequest)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param clientProductId The product's unique id provided by the client.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody deleteProduct(@NonNull String clientProductId)
	{

		final Optional<LiteProduct> cached = localRepo.findByClientProductId(clientProductId);
		if(cached.isEmpty())
		{
			final ProductNotFoundException exc = new ProductNotFoundException(clientProductId);
            logException(exc, this.getClass().getName() + "::deleteProduct");
            throw new BackendServiceException(exc, HttpStatus.NOT_FOUND);
		}
		else
		{
			final String squareItemId = cached.get().getSquareItemId();
			// On Square, deletions are cascading, which means that all item variations of the item
			// that is deleted will also be deleted. Consequently, we do not need the item variation information.
			//	final String squareItemVarId = cached.get().getSquareItemVariationId();
			final String productType = cached.get().getProductType();
			try
			{
				final SquareServiceResponseBody squareServiceResponse = squareService.deleteProduct(squareItemId);
				localRepo.deleteByClientProductId(clientProductId); // delete(cached) will probably be slower
				return BackendServiceResponseBody.buildBackendResponseBody(squareServiceResponse, clientProductId, productType);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::deleteProduct");
				throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}
}
