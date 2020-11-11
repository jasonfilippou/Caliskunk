package com.company.rest.products.model;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
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
	public CatalogWrapper()
	{
		final SquareClient client = new SquareClient.Builder().environment(com.squareup.square.Environment.SANDBOX)
											.accessToken(System.getenv("SQUARE_SANDBOX_ACCESS_TOKEN"))
									.build();
		catalog  = client.getCatalogApi();
	}

	/**
	 * Sends an upsert (PUT) request to the Square API.
	 * @param request An {@link UpsertCatalogObjectRequest} instance containing the request data.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 * @return an instance of {@link UpsertCatalogObjectResponse} containing the Square server's response.
	 */
	public UpsertCatalogObjectResponse upsertObject(@NonNull final UpsertCatalogObjectRequest request)
															throws ExecutionException, InterruptedException
	{
	    return catalog.upsertCatalogObjectAsync(request).get();
	}


	/**
	 * Sends a batch retrieve (GET) request to the Square API.
	 * @param request An {@link BatchRetrieveCatalogObjectsRequest} instance containing the request for {@link CatalogObject}
	 *                instances stored in the Square servers.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see BatchRetrieveCatalogObjectsRequest
	 * @see BatchRetrieveCatalogObjectsResponse
	 * @see UpsertCatalogObjectResponse
	 * @return An instance of {@link BatchRetrieveCatalogObjectsResponse} which contains the data requested from the Square API.
	 */
	public BatchRetrieveCatalogObjectsResponse batchRetrieveObjects(@NonNull final BatchRetrieveCatalogObjectsRequest request)
																			throws ExecutionException, InterruptedException
	{
		return catalog.batchRetrieveCatalogObjectsAsync(request).get();
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
	 * @param squareItemID the unique ID Square provided to the {@link CatalogObject} to be deleted.
	 * @see CatalogApi#deleteCatalogObjectAsync(String)
	 * @see CatalogApi#deleteCatalogObject(String)
	 * @return An instance of {@link DeleteCatalogObjectResponse} which contains the data that the Square API sent us after
	 *              a successful or unsuccessful deletion.
	 */
	public DeleteCatalogObjectResponse deleteCatalogObject(@NonNull final String squareItemID) throws ExecutionException, InterruptedException
	{
		return catalog.deleteCatalogObjectAsync(squareItemID).get();
	}
}
