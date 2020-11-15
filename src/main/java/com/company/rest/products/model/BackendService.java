package com.company.rest.products.model;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.exceptions.*;
import com.company.rest.products.util.request_bodies.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.company.rest.products.util.Util.logException;

/**
 * Service class for {@link ProductController}. Manages queries to our {@link LiteProductRepository} database
 * as well as communication with both {@link ProductController} and {@link SquareService}.
 *
 * @see ProductController
 * @see LiteProduct
 * @see LiteProductRepository
 * @see SquareService
 * @see BackendServiceResponseBody
 * @see BackendServiceException
 */
@Slf4j
@Component
public class BackendService
{
	private final SquareService squareService;
	private final LiteProductRepository localRepo;

	/**
	 * Standard full-arg constructor.
	 * @param localRepo A reference to our local DB. Usually {@code @Autowired}.
	 * @param squareService A reference to our {@link SquareService} instance. Usually {@code @Autowired}.
	 */
	@Autowired
	public BackendService(final LiteProductRepository localRepo, final SquareService squareService)
	{
		this.localRepo = localRepo;
		this.squareService = squareService;
	}


	/**
	 * Handles a POST request to Square's API. After the call to the API is made, ensures that a cached version
	 * of the product is maintained in our local DB before returning response to {@link ProductController}.
	 *
	 * @param request A {@link ProductUpsertRequestBody} instance containing details of the request.
	 * @see LiteProduct
	 * @see LiteProductRepository
	 * @see ProductController#postProduct(ProductUpsertRequestBody)
	 * @see SquareService#upsertProduct(ProductUpsertRequestBody, String)
	 * @throws ResourceAlreadyCreatedException if the resource is already there.
	 * @throws BackendServiceException if {@link SquareService} throws a {@link SquareServiceException} to us.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 */
	public BackendServiceResponseBody postProduct(final ProductUpsertRequestBody request) throws BackendServiceException, ResourceAlreadyCreatedException
	{
		// First, make a local check to ensure that there's no name clash for
		// the product uploaded. This is one of the advantages of having a cache.
		final String id = request.getClientProductId();
		if(localRepo.findByClientProductId(id).isPresent())
		{
			final ResourceAlreadyCreatedException exc = new ResourceAlreadyCreatedException(id);
            logException(exc, this.getClass().getName() + "::postProduct");
            throw exc;
		}
		else
		{
			// We first POST to Square and *then* store the cached version in our
			// local DB in order to grab the unique ID that Square provides us with.
			try
			{
				final SquareServiceResponseBody response = squareService.upsertProduct(request, id);
				localRepo.save(LiteProduct.buildLiteProductFromSquareResponse(response));
				return BackendServiceResponseBody.buildBackendResponseBody(response);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::postProduct");
                throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Sends a GET request for a specific product. Checks against local DB first to avoid a potentially
	 * costly {@link SquareService} API call.
	 *
	 * @param getRequest The GET request sent to us by {@link ProductController#deleteProduct(String)}
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @throws BackendServiceException if an instance of {@link SquareServiceException} is caught
	 *                              during the runtime of  {@link SquareService#getProduct(LiteProduct)}
   	 * @throws ProductNotFoundException if the resource is not available.
	 * @see SquareService#getProduct(LiteProduct)
	 * @see ProductController#getProduct(String)
	 */
	public BackendServiceResponseBody getProduct(final ProductGetRequestBody getRequest) throws BackendServiceException, ProductNotFoundException
	{
		// Cheap check first; if the product doesn't exist, why go to Square API with the request?
		final Optional<LiteProduct> cached = localRepo.findByClientProductId(getRequest.getClientProductId());
		if(cached.isEmpty())
		{

			final ProductNotFoundException exc = new ProductNotFoundException(getRequest.getClientProductId());
            logException(exc, this.getClass().getName() + "::getProduct");
            throw exc;
		}
		else
		{
			final String squareItemId = cached.get().getSquareItemId();
			final String squareItemVariationId = cached.get().getSquareItemVariationId();
			final String productType = cached.get().getProductType();
			try
			{
				final SquareServiceResponseBody squareServiceResponse = squareService.getProduct(cached.get());
				return BackendServiceResponseBody.buildBackendResponseBody(squareServiceResponse);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::getProduct");
				throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}

	/**
	 * Serve a GET ALL request. Paginates and sorts the output based on provided parameters. For speed, this method
	 * does not use {@link SquareService} functionality <i>at all</i> &#59; the entirety of the response comes from our
	 * {@link LiteProductRepository} and is paginated and sorted using {@link org.springframework.data.jpa.repository.JpaRepository}
	 * primitives.
	 *
	 * @param pageIdx the current index of the page in the paginated response.
	 * @param itemsInPage the number of items in the current page.
	 * @param sortBy the field of {@link LiteProduct} which we will use for sorting the products.
	 * @throws BackendServiceException if {@link SquareService} throws a {@link SquareServiceException}.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#getAll(Integer, Integer, String)
	 * @see LiteProductRepository#findAll()
	 * @see JpaRepository#findAll(Pageable)
	 */
	public List<BackendServiceResponseBody> getAllProducts(@NonNull final Integer pageIdx,
	                                                       @NonNull final Integer itemsInPage,
	                                                       @NonNull final String sortBy)
	{
		// Paginated and sorted output whether it is on Square or the cache.
		try
		{
			/* The way Chris usually does his pagination is by using something like this

				public Page<Employee> findByDept(String deptName, Pageable pageable);

				In our JPARepository type.

				Pageable pageable = PageRequest.of(0, 3, Sort.by("salary")); is one of the calls that he makes

				// Check the resource: https://www.logicbig.com/tutorials/spring-framework/spring-data/pagination-returning-page.html

			 */
			return localRepo.findAll(PageRequest.of(pageIdx, itemsInPage, Sort.by(sortBy).ascending()))
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
	 * @param request The request body.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#putProduct(ProductUpsertRequestBody, String)
	 * @see SquareService#upsertProduct(ProductUpsertRequestBody, String)
	 */
	public BackendServiceResponseBody putProduct(@NonNull final ProductUpsertRequestBody request, @NonNull String id)
													throws ProductNotFoundException, BackendServiceException
	{
		// First, ensure that the product is already POSTed, otherwise client done messed up and they need to POST.
		final Optional<LiteProduct> cachedProduct = localRepo.findByClientProductId(id);
		if(cachedProduct.isEmpty())
		{
			final ProductNotFoundException exc = new ProductNotFoundException(id);
            logException(exc, this.getClass().getName() + "::deleteProduct");
            throw exc;
		}
		else
		{
			final SquareServiceResponseBody response = squareService.upsertProduct(request, id);
			localRepo.deleteByClientProductId(id); // Since we PUT, we have to replace entirely. TODO: would it be faster to execute an HDL-assisted SQL UPDATE query for LiteProductRepository?
			localRepo.save(LiteProduct.buildLiteProductFromSquareResponse(response));
			return BackendServiceResponseBody.buildBackendResponseBody(response);
		}
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param request The fields to update.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#patchProduct(ProductUpsertRequestBody, String)
	 * @see SquareService#patchProduct(ProductUpsertRequestBody, String)
	 */
	public BackendServiceResponseBody patchProduct(@NonNull final ProductUpsertRequestBody request, @NonNull final String id)
	{
		throw new UnimplementedMethodPlaceholder();
	}

	/**
	 * Send a DELETE request for a specific product.
	 * @param clientProductId The product's unique id provided by the client.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#deleteProduct(String)
	 * @see SquareService#deleteProduct(LiteProduct)
	 */
	public BackendServiceResponseBody deleteProduct(@NonNull final String clientProductId) throws BackendServiceException, ProductNotFoundException
	{
		final Optional<LiteProduct> cached = localRepo.findByClientProductId(clientProductId);
		if(cached.isEmpty())
		{
			final ProductNotFoundException exc = new ProductNotFoundException(clientProductId);
            logException(exc, this.getClass().getName() + "::deleteProduct");
            throw exc;
		}
		else
		{
			// On Square, deletions are cascading, which means that all item variations of the item
			// that is deleted will also be deleted. Consequently, we do not need the item variation information,
			// but only the square item id.
			try
			{
				final SquareServiceResponseBody squareServiceResponse = squareService.deleteProduct(cached.get());
				localRepo.deleteByClientProductId(clientProductId); // delete(cached) will probably be slower
				return BackendServiceResponseBody.buildBackendResponseBody(squareServiceResponse);
			}
			catch(SquareServiceException exc)
			{
				logException(exc, this.getClass().getName() + "::deleteProduct");
				throw new BackendServiceException(exc, exc.getStatus());
			}
		}
	}
}
