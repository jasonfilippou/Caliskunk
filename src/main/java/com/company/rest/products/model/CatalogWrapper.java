package com.company.rest.products.model;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.BatchRetrieveCatalogObjectsRequest;
import com.squareup.square.models.BatchRetrieveCatalogObjectsResponse;
import com.squareup.square.models.UpsertCatalogObjectRequest;
import com.squareup.square.models.UpsertCatalogObjectResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * An abstraction over the <a href="https://github.com/square/square-java-sdk/blob/master/doc/catalog.md">Square
 * Catalog Java API calls</a>.
 *
 * @see SquareClient
 * @see CatalogApi
 */
@Component
@Data
public class CatalogWrapper
{
	private final SquareClient client;
	private final CatalogApi catalog;

	@Autowired
	public CatalogWrapper()
	{
		client = new SquareClient.Builder().environment(com.squareup.square.Environment.SANDBOX)
											.accessToken(System.getenv("SQUARE_SANDBOX_ACCESS_TOKEN"))
									.build();
		catalog  = client.getCatalogApi();
	}

	/**
	 * Sends an upsert (PUT) request to the Square API.
	 * @throws IOException if Square throws it to us.
	 * @throws ApiException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 */
	public UpsertCatalogObjectResponse upsertObject(UpsertCatalogObjectRequest request)
															throws ExecutionException, InterruptedException
	{
	    return catalog.upsertCatalogObjectAsync(request).get();  // Moved from the async call to this for now
	}


	/**
	 * Sends a batch retrieve (GET) request to the Square API.
	 * @throws IOException if Square throws it to us.
	 * @throws ApiException if Square throws it to us.
	 * @see UpsertCatalogObjectRequest
	 * @see UpsertCatalogObjectResponse
	 */
	public BatchRetrieveCatalogObjectsResponse batchRetrieveObjects(BatchRetrieveCatalogObjectsRequest request)
																			throws ExecutionException, InterruptedException
	{
		return catalog.batchRetrieveCatalogObjectsAsync(request).get();
	}
}
