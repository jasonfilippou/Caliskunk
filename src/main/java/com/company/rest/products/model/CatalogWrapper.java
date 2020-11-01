package com.company.rest.products.model;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * An abstraction over the <a href="https://github.com/square/square-java-sdk/blob/master/doc/catalog.md">Square
 * Catalog Java API</a>.
 *
 * @see SquareClient
 * @see CatalogApi
 */
@Component
public class CatalogWrapper
{

	private final CatalogApi catalog;

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
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 */
	public UpsertCatalogObjectResponse upsertObject(@NonNull final UpsertCatalogObjectRequest request)
															throws ExecutionException, InterruptedException
	{
	    return catalog.upsertCatalogObjectAsync(request).get();  // Moved from the async call to this for now
	}


	/**
	 * Sends a batch retrieve (GET) request to the Square API.
	 * @throws ExecutionException if Square throws it to us.
	 * @throws InterruptedException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 */
	public BatchRetrieveCatalogObjectsResponse batchRetrieveObjects(@NonNull final BatchRetrieveCatalogObjectsRequest request)
																			throws ExecutionException, InterruptedException
	{
		return catalog.batchRetrieveCatalogObjectsAsync(request).get();
	}

	public DeleteCatalogObjectResponse deleteCatalogObject(@NonNull final String squareItemID) throws IOException, ApiException
	{
		return catalog.deleteCatalogObject(squareItemID);
	}
}
