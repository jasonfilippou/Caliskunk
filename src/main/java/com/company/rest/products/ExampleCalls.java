package com.company.rest.products;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.models.CatalogItem;
import com.squareup.square.models.CatalogObject;
import com.squareup.square.models.UpsertCatalogObjectRequest;
import com.squareup.square.models.UpsertCatalogObjectResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
				.name("Culeothesis Necrosis")
				.description("Will eat your face.")
				.productType("REGULAR")
				.abbreviation("Cul")
				.labelColor("7FFFD4")
				.availableOnline(true)
				.availableElectronically(true)
				.availableForPickup(true)
				.build();
		System.out.println("CatalogItem created...");

		final CatalogObject bodyObject = new CatalogObject
				.Builder("ITEM", "#RANDOM_ID")
				.itemData(bodyObjectItemData)
				.build();
		System.out.println("CatalogObject wrapper of CatalogItem created...");

		final UpsertCatalogObjectRequest request = new UpsertCatalogObjectRequest.Builder(UUID.randomUUID().toString(), bodyObject).build();

		System.out.println("UpsertCatalogObjectRequest created... performing query:");

		try
		{
			final UpsertCatalogObjectResponse response = catalogApi.upsertCatalogObjectAsync(request).get();
			System.out.println("We received a response: " + response);
		}
		catch (Throwable e)
		{
			System.out.println("Caught a " + e.getClass().getSimpleName() + " with message: " + e.getMessage() + ".");
		}
//		catalogApi.upsertCatalogObjectAsync(request).thenAccept(response -> System.out.println("We received a response: " + response))
//		          .exceptionally(exception -> {
//                                        System.out.println("An exception occurred: " + exception.getClass().getName()
//                                                     + " with message: " + exception.getMessage());
//                                        return null;
//		                                        }
//                                );
//	}
	}
}
