package com.company.rest.products.model;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * An abstraction over the <a href="https://github.com/square/square-java-sdk/blob/master/doc/catalog.md">Square
 * Catalog Java API</a>. Used extensively by {@link SquareService}.
 *
 * @see SquareClient
 * @see CatalogApi
 * @see SquareService
 */
@Component
public class CatalogWrapper
{

	private final CatalogApi catalog;

	/**
	 * {@code @Autowired} default constructor. Opens connection to Square by using stored access token.
	 */
	@Autowired
	public CatalogWrapper(@Value("${square.sandbox.accesstoken}") String accessToken)
	{
		final SquareClient client = new SquareClient.Builder().environment(com.squareup.square.Environment.SANDBOX)
		                                                      .accessToken(accessToken)
		                                                      .build();
		catalog  = client.getCatalogApi();
	}

	/**
	 * Sends a POST request to the Square API.
	 * @param request An {@link UpsertCatalogObjectRequest} instance containing the request data.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 * @return an instance of {@link UpsertCatalogObjectResponse} containing the Square server's response.
	 */
	public UpsertCatalogObjectResponse postObject(@NonNull final UpsertCatalogObjectRequest request)
															throws ExecutionException, InterruptedException
	{
	    return catalog.upsertCatalogObjectAsync(request).get();
	}

	/**
	 * Sends a PUT request to the Square API.
	 * @param request An {@link UpsertCatalogObjectRequest} instance containing the request data.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 * @return an instance of {@link UpsertCatalogObjectResponse} containing the Square server's response.
	 */
	public UpsertCatalogObjectResponse putObject(@NonNull final UpsertCatalogObjectRequest request)
			throws ExecutionException, InterruptedException
	{
		return catalog.upsertCatalogObjectAsync(request).get();
	}


	/**
	 * Sends a PATCH request to the Square API.
	 * @param request An {@link UpsertCatalogObjectRequest} instance containing the request data.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 * @return an instance of {@link UpsertCatalogObjectResponse} containing the Square server's response.
	 */
	public UpsertCatalogObjectResponse patchObject(@NonNull final UpsertCatalogObjectRequest request)
			throws ExecutionException, InterruptedException
	{
		throw new UnimplementedMethodPlaceholder();
	}



	/**
	 * Sends a retrieve (GET) request to the Square API.
	 * @param getRequest A {@link ProductGetRequestBody} containing the Square - provided ID of the product to retrieve.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see RetrieveCatalogObjectResponse
	 * @return An instance of {@link RetrieveCatalogObjectResponse} which contains the data requested from the Square API.
	 */
	public RetrieveCatalogObjectResponse retrieveObject(@NonNull final ProductGetRequestBody getRequest)
																			throws ExecutionException, InterruptedException
	{
		return catalog.retrieveCatalogObjectAsync(getRequest.getLiteProduct().getSquareItemId(), false).get();
	}

	/**
	 * Sends a DELETE(id) request to the Square API. Note that Square soft-deletes; you can still use the
	 * <a href="https://developer.squareup.com/explorer/square/catalog-api/list-catalog">List Catalog</a> call to
	 * find deleted {@link CatalogObject} instances, which will, however, have the field {@code is_deleted} set to
	 * {@literal true}.
	 *
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 *
	 * @param deleteRequest A {@link ProductDeleteRequestBody} containing the unique ID Square provided to the {@link CatalogObject} to be deleted.
	 * @see CatalogApi#deleteCatalogObjectAsync(String)
	 * @see CatalogApi#deleteCatalogObject(String)
	 * @return An instance of {@link DeleteCatalogObjectResponse} which contains the data that the Square API sent us after
	 *              a successful or unsuccessful deletion.
	 */
	public DeleteCatalogObjectResponse deleteObject(@NonNull final ProductDeleteRequestBody deleteRequest) throws ExecutionException, InterruptedException
	{
		return catalog.deleteCatalogObjectAsync(deleteRequest.getLiteProduct().getSquareItemId()).get();
	}
}
