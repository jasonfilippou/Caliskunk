package com.company.rest.products;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogObject;
import com.squareup.square.models.UpsertCatalogObjectRequest;
import com.squareup.square.models.UpsertCatalogObjectResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class ExampleCalls
{

	private static final SquareClient client = new SquareClient
								.Builder()
									.environment(com.squareup.square.Environment.SANDBOX)
									.accessToken(System.getenv("SQUARE_SANDBOX_ACCESS_TOKEN"))
								.build();

	private static final CatalogApi catalogApi = client.getCatalogApi();
	
	public static void main(String[] args)
	{

		final CatalogItem bodyObjectItemData = new CatalogItem
				.Builder()
				    .name("Cocoa")
				    .description("Hot chocolate")
				    .abbreviation("Ch")
				    .labelColor("A52A2A")
				    .availableOnline(true)
                .build();
		System.out.println("CatalogItem created...");

		final CatalogObject bodyObject = new CatalogObject
				.Builder("ITEM","#Cocoa")
                    .isDeleted(false)
					.itemData(bodyObjectItemData)
                .build();
		System.out.println("CatalogObject wrapper of CatalogItem created...");

		final UpsertCatalogObjectRequest body = new UpsertCatalogObjectRequest.Builder(UUID.randomUUID().toString(), bodyObject).build();

		System.out.println("UpsertCatalogObjectRequest created... performing query:");
		try
		{
			final UpsertCatalogObjectResponse response = catalogApi.upsertCatalogObjectAsync(body).get();
			System.out.println("We received a response: " + response);
		}
		catch (InterruptedException | ExecutionException e)
		{
			System.out.println("During the query, we received a " + e.getClass().getName() +
			                   " with message: " + e.getMessage());
		}
	}
}
