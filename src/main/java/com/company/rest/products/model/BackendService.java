package com.company.rest.products.model;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.exceptions.*;
import com.company.rest.products.util.request_bodies.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.company.rest.products.util.Util.*;
import static java.util.Optional.ofNullable;
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
	 *
	 * @param localRepo     A reference to our local DB. Usually {@code @Autowired}.
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
	 * @param postRequest A {@link ProductUpsertRequestBody} instance containing details of the request.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @throws ResourceAlreadyCreatedException if the resource is already there.
	 * @throws BackendServiceException         if {@link SquareService} throws a {@link SquareServiceException} to us.
	 * @see LiteProduct
	 * @see LiteProductRepository
	 * @see ProductController#postProduct(ProductUpsertRequestBody)
	 * @see SquareService#postProduct(ProductUpsertRequestBody)
	 */
	public BackendServiceResponseBody postProduct(final ProductUpsertRequestBody postRequest) throws BackendServiceException, ResourceAlreadyCreatedException
	{
		// First, make a local check to ensure that there's no name clash for
		// the product uploaded. This is one of the advantages of having a cache.
		try
		{
			validatePostRequest(postRequest);
			final String id = postRequest.getClientProductId();
			if (localRepo.findByClientProductId(id).isPresent())
			{
				final ResourceAlreadyCreatedException exc = new ResourceAlreadyCreatedException(id);
				logException(exc, this.getClass().getName() + "::postProduct");
				throw exc;
			}
			else
			{
				// We first POST to Square and *then* store the cached version in our
				// local DB in order to grab the unique ID that Square provides us with.
				final SquareServiceResponseBody squareServiceResponse = squareService.postProduct(postRequest);
				validatePostResponse(squareServiceResponse, postRequest);
				localRepo.save(LiteProduct.fromSquareResponse(squareServiceResponse));
				return BackendServiceResponseBody.fromSquareResponse(squareServiceResponse);
			}
		}
		catch (SquareServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::postProduct");
			throw new BackendServiceException(exc, exc.getStatus());
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::postProduct");
			throw new BackendServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validatePostRequest(final ProductUpsertRequestBody postRequest)
	{
		assertAndIfNotLogAndThrow(postRequest.getClientProductId() != null,
		                          "Bad POST request.");
	}

	private void validatePostResponse(final SquareServiceResponseBody squareServiceResponse, final ProductUpsertRequestBody clientPostRequest)
	{
		assertAndIfNotLogAndThrow(nullOrFalse(squareServiceResponse.getIsDeleted()) &&
		                          stringsMatch(squareServiceResponse.getName(), clientPostRequest.getProductName()) &&
		                          stringsMatch(squareServiceResponse.getProductType(), clientPostRequest.getProductType()) &&
		                           squareServiceResponse.getUpdatedAt() != null &&
		                           squareServiceResponse.getVersion() != null &&
		                           squareServiceResponse.getSquareItemId() != null &&
		                          squareServiceResponse.getClientProductId().equals(clientPostRequest.getClientProductId()) &&
		                          optionalFieldsMatch(squareServiceResponse, clientPostRequest),
		                          "Bad Upsert Response from Square Service layer.");
	}

	private boolean optionalFieldsMatch(final SquareServiceResponseBody squareServiceResponse, final ProductUpsertRequestBody clientPostRequest)
	{
		return ofNullable(squareServiceResponse.getCostInCents()).equals(ofNullable(clientPostRequest.getCostInCents())) &&
		       ofNullable(squareServiceResponse.getDescription()).equals(ofNullable(clientPostRequest.getDescription())) &&
		       ofNullable(squareServiceResponse.getLabelColor()).equals(ofNullable(clientPostRequest.getLabelColor())) &&
		       ofNullable(squareServiceResponse.getSku()).equals(ofNullable(clientPostRequest.getSku())) &&
		       ofNullable(squareServiceResponse.getUpc()).equals(ofNullable(clientPostRequest.getUpc()));
	}

	/**
	 * Sends a GET request for a specific product. Checks against local DB first to avoid a potentially
	 * costly {@link SquareService} API call.
	 *
	 * @param getRequest The GET request sent to us by {@link ProductController#deleteProduct(String)}
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @throws BackendServiceException if an instance of {@link SquareServiceException} is caught
	 *                              during the runtime of  {@link SquareService#getProduct(ProductGetRequestBody)}
   	 * @throws ProductNotFoundException if the resource is not available.
	 *
	 * @see ProductController#getProduct(String)
	 * @see SquareService#getProduct(ProductGetRequestBody)
	 */
	public BackendServiceResponseBody getProduct(final ProductGetRequestBody getRequest) throws BackendServiceException, ProductNotFoundException
	{
		try
		{
			validateGetRequest(getRequest);
			// Cheap check first; if the product doesn't exist, why go to Square API with the request?
			final Optional<LiteProduct> cached = localRepo.findByClientProductId(getRequest.getClientProductId());
			if (cached.isEmpty())
			{
				throw new ProductNotFoundException(getRequest.getClientProductId());
			}
			else
			{
				expandGetRequest(getRequest, cached.get());
				final SquareServiceResponseBody squareServiceResponse = squareService.getProduct(getRequest);
				validateGetResponse(squareServiceResponse, getRequest);
				return BackendServiceResponseBody.fromSquareResponse(squareServiceResponse);
			}
		}
		catch(SquareServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::getProduct");
			throw new BackendServiceException(exc, exc.getStatus());
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::getProduct");
			throw new BackendServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateGetRequest(final ProductGetRequestBody getRequest)
	{
		assertAndIfNotLogAndThrow(getRequest.getClientProductId() != null, "Bad GET request");
	}

	private void validateGetResponse(final SquareServiceResponseBody getResponse, final ProductGetRequestBody getRequest)
	{
		assertAndIfNotLogAndThrow(getResponse.getClientProductId().equals(getRequest.getClientProductId()) &&
		                                    getResponse.getVersion().equals(getRequest.getLiteProduct().getVersion()) &&
		                                    optionalFieldsMatch(getResponse, getRequest), "Bad GET response from Square Service");
	}

	private boolean optionalFieldsMatch(final SquareServiceResponseBody getResponse, final ProductGetRequestBody getRequest)
	{
		return stringsMatch(getResponse.getName(), getRequest.getLiteProduct().getProductName()) &&
		       stringsMatch(getResponse.getProductType(), getRequest.getLiteProduct().getProductType()) &&
		       ofNullable(getResponse.getCostInCents()).equals(ofNullable(getRequest.getLiteProduct().getCostInCents())) &&
		       ofNullable(getResponse.getSquareItemId()).equals(ofNullable(getRequest.getLiteProduct().getSquareItemId()));

	}

	private void expandGetRequest(final ProductGetRequestBody getRequest, final LiteProduct liteProduct)
	{
		assertAndIfNotLogAndThrow(getRequest.getClientProductId().equals(liteProduct.getClientProductId()), "Mismatch in product IDs during GET.");
		getRequest.setLiteProduct(liteProduct);
	}

	/**
	 * Serve a GET ALL request. Paginates and sorts the output based on provided parameters. For speed, this method
	 * does not use {@link SquareService} functionality <i>at all</i> &#59; the entirety of the response comes from our
	 * {@link LiteProductRepository} and is paginated and sorted using {@link org.springframework.data.jpa.repository.JpaRepository}
	 * primitives.
	 *
	 * @param pageIdx the current index of the page in the paginated response.
	 * @param itemsInPage the number of items in the current page.
	 * @param sortByField the field of {@link LiteProduct} which we will use for sorting the products.
	 * @throws BackendServiceException if {@link SquareService} throws a {@link SquareServiceException}.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#getAll(Integer, Integer, String)
	 * @see LiteProductRepository#findAll()
	 * @see JpaRepository#findAll(Pageable)
	 */
	public Page<LiteProduct> getAllProducts(@NonNull final Integer pageIdx,
	                                                       @NonNull final Integer itemsInPage,
	                                                       @NonNull final String sortByField,
	                                                        @NonNull final String sortOrderString)
	{
		try
		{
			// Paginated and sorted output from the cache.
			final Sort sortOrder = determineSortOrder(sortByField, sortOrderString);
			return localRepo.findAll(PageRequest.of(pageIdx, itemsInPage, sortOrder));
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::getAllProducts");
			throw new BackendServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Sort determineSortOrder(final String sortByField, final String sortOrderString)
	{
		if(sortOrderString.equals("ASC"))
		{
			return Sort.by(sortByField).ascending();
		}
		else if(sortOrderString.equals("DESC"))
		{
			return Sort.by(sortByField).descending();
		}
		else
		{
			throw new IllegalArgumentException("Bad sort order string provided.");
		}
	}

	/**
	 * Send a PUT request for a specific product.
	 * @param putRequest The request body.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#putProduct(ProductUpsertRequestBody, String)
	 * @see SquareService#putProduct(ProductUpsertRequestBody)
	 */
	public BackendServiceResponseBody putProduct(@NonNull final ProductUpsertRequestBody putRequest)
													throws ProductNotFoundException, BackendServiceException
	{
		try
		{
			validatePutRequest(putRequest);
			// First, ensure that the product is already POSTed, otherwise client done messed up and they need to POST first.
			final String id = putRequest.getClientProductId();
			final Optional<LiteProduct> cachedProduct = localRepo.findByClientProductId(id);
			if (cachedProduct.isEmpty())
			{
				final ProductNotFoundException exc = new ProductNotFoundException(id);
				logException(exc, this.getClass().getName() + "::putProduct");
				throw exc;
			}
			else
			{
				putRequest.setVersion(cachedProduct.get().getVersion());      // The PUT request needs the previous version information.
				putRequest.setSquareItemId(cachedProduct.get().getSquareItemId());
				putRequest.setSquareItemVariationId(cachedProduct.get().getSquareItemVariationId());
				final SquareServiceResponseBody squareServiceResponse = squareService.putProduct(putRequest);
				validatePutResponse(squareServiceResponse, putRequest);
				localRepo.deleteByClientProductId(id);                      // Since we PUT, we have to replace entirely. TODO: would it be faster to execute an HDL-assisted SQL UPDATE query for LiteProductRepository?
				localRepo.save(LiteProduct.fromSquareResponse(squareServiceResponse));   // This will replace the version as well.
				return BackendServiceResponseBody.fromSquareResponse(squareServiceResponse);
			}
		}
		catch(SquareServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::putProduct");
			throw new BackendServiceException(exc, exc.getStatus());
		}
		catch(Throwable t)
		{
			logException(t, this.getClass().getName() + "::putProduct");
			throw new BackendServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validatePutRequest(final ProductUpsertRequestBody putRequest)
	{
		assertAndIfNotLogAndThrow(putRequest.getVersion() != null &&
		                          putRequest.getClientProductId() != null, "Bad PUT request");
	}

	private void validatePutResponse(final SquareServiceResponseBody putResponse, final ProductUpsertRequestBody putRequest)
	{
		validatePostResponse(putResponse, putRequest); // Identical logic
	}

	/**
	 * Send a PATCH request for a specific product.
	 * @param request The fields to update.
	 * @param id The unique ID of the field to patch.
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
	 * @param deleteRequest The body of the DELETE request prepared by the client.
	 * @return A {@link BackendServiceResponseBody} instance describing the work done by this layer.
	 * @see ProductController#deleteProduct(String)
	 * @see SquareService#deleteProduct(ProductDeleteRequestBody)
	 */
	public BackendServiceResponseBody deleteProduct(@NonNull final ProductDeleteRequestBody deleteRequest) throws BackendServiceException, ProductNotFoundException
	{
		try
		{
			validateDeleteRequest(deleteRequest);
			final String id = deleteRequest.getClientProductId();
			final Optional<LiteProduct> cached = localRepo.findByClientProductId(id);
			if (cached.isEmpty())
			{
				final ProductNotFoundException exc = new ProductNotFoundException(id);
				logException(exc, this.getClass().getName() + "::deleteProduct");
				throw exc;
			}
			else
			{
				expandDeleteRequest(deleteRequest, cached.get());
				final SquareServiceResponseBody squareServiceResponse = squareService.deleteProduct(deleteRequest);
				validateDeleteResponse(squareServiceResponse, deleteRequest);
				localRepo.deleteByClientProductId(id); // delete(cached) will probably be slower
				return BackendServiceResponseBody.fromSquareResponse(squareServiceResponse);
			}
		}
		catch(SquareServiceException exc)
		{
			logException(exc, this.getClass().getName() + "::deleteProduct");
			throw new BackendServiceException(exc, exc.getStatus());
		}
	}

	private void validateDeleteRequest(final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(deleteRequest.getClientProductId() != null , "Bad DELETE request");
	}

	private void expandDeleteRequest(final ProductDeleteRequestBody deleteRequest, final LiteProduct liteProduct)
	{
		assertAndIfNotLogAndThrow(deleteRequest.getClientProductId().equals(liteProduct.getClientProductId()), "Bad DELETE request");
		deleteRequest.setLiteProduct(liteProduct);
	}

	private void validateDeleteResponse(final SquareServiceResponseBody squareServiceResponse, final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(squareServiceResponse.getClientProductId().equals(deleteRequest.getClientProductId()) &&
		                          stringsMatch(squareServiceResponse.getName(), deleteRequest.getLiteProduct().getProductName()) &&
		                          stringsMatch(squareServiceResponse.getProductType(), deleteRequest.getLiteProduct().getProductType()) &&
		                          squareServiceResponse.getSquareItemId().equals(deleteRequest.getLiteProduct().getSquareItemId()) &&
		                          squareServiceResponse.getCostInCents().equals(deleteRequest.getLiteProduct().getCostInCents()) &&
		                          squareServiceResponse.getIsDeleted() && squareServiceResponse.getUpdatedAt() != null,
								  "Bad DELETE response");   // Remember; no version information for DELETE provided by Square.
	}
}
