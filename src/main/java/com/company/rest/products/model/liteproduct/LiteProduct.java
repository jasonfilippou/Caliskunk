package com.company.rest.products.model.liteproduct;
import com.company.rest.products.util.Util;
import com.company.rest.products.util.exceptions.InvalidProductTypeException;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * {@link LiteProduct} is a cached version of products, with only a subset of the fields a user might provide.
 * A local database of such instances are made, and the {@link String} id that the client provides is the primary key for those
 * instances.
 *
 * @see LiteProductRepository
 */
@Data
@Entity
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor // Required for Entities
public class LiteProduct
{
	@Id
	private String clientProductId;                         // Provided by user
	private String squareItemId;	                        // Provided by Square, guaranteed unique across all objects.
	private String squareItemVariationId;	                // Provided by Square, guaranteed unique across all objects. Useful for PUT.
	private String productName;
	private String productType;
	private Long costInCents;
	private Long version;                                    // Used by Square for updates.

	/**
	 * Our accepted product types. Implemented as a {@link HashSet} in case the types become many and we
	 * need fast retrieval. TODO: Move those outside LiteProduct and make a special attribute for storing on Square.
	 */
	public final static Set<String> PRODUCT_TYPES = new HashSet<>
			 (Arrays.asList("FLOWER", "TOPICAL", "VAPORIZER", "EDIBLE", "PREROLL", "CONCENTRATE",
			                "TINCTURE", "PET", "ACCESSORY", "OTHER"));
	/**
	 * All-args constructor.
	 * @param clientProductId The unique ID the client wishes to provide our application.
	 * @param costInCents The amount of money the product costs, in cents.
	 * @param productName  The name of the product.
	 * @param productType The type of the product. See {@link #PRODUCT_TYPES} for an acceptable set of such types.
	 * @param squareItemId  The ID for the {@link com.squareup.square.models.CatalogItem} component of our product,
	 *                         provided to us  by the Square API.
	 * @param squareItemVariationId  The ID for the {@link com.squareup.square.models.CatalogItemVariation} component of our product,
	 *                         provided to us  by the Square API.
	 * @param version  A {@link Long} provided to us by Square, essential for PUT queries to the API. Common for both the item
	 *                 and its variation, since we create both at the same API call.
	 */
	public LiteProduct(@NonNull final String clientProductId, @NonNull final String squareItemId,
	                   @NonNull final String squareItemVariationId,
	                   @NonNull final String productName, @NonNull final String productType,
	                   @NonNull final Long costInCents, @NonNull final Long version)
	{
		if(!PRODUCT_TYPES.contains(productType.strip().toUpperCase()))  // TODO: this check probably moved outside LiteProduct as per the TODO above.
		{
			final InvalidProductTypeException exc = new InvalidProductTypeException(productType);
			Util.logException(exc, this.getClass().getName() + "::LiteProduct");
			throw new InvalidProductTypeException(productType);
		}
		else
		{
			this.clientProductId = clientProductId;
			this.productName = productName.strip().toUpperCase();
			this.squareItemId = squareItemId;
			this.squareItemVariationId = squareItemVariationId;
			this.costInCents = costInCents;
			this.productType = productType.strip().toUpperCase();
			this.version = version;
		}
	}

	/* ****************************************************** */
	/* Some static methods to create LiteProducts on the fly  */
	/* ****************************************************** */

	/**
	 * Build a {@link LiteProduct} instance out of a {@link SquareServiceResponseBody} instance. This method
	 * can also supply {@code this} with information mined from Square and contained in the argument.
	 *
	 * @param response A {@link SquareServiceResponseBody} containing at least the necessary fields for creation
	 *                 of an instance of {@code this}.
	 * @return An instance of {@code this}.
	 */
	public static LiteProduct buildLiteProductFromSquareResponse(@NonNull final SquareServiceResponseBody response)
	{
		return LiteProduct.builder()
		                  .clientProductId(response.getClientProductId())
					      .squareItemId(response.getSquareItemId())
					      .squareItemVariationId(response.getSquareItemVariationId())
					      .productName(response.getName().trim().toUpperCase()) // Uppercasing name to make it case-insensitive
					      .productType(response.getProductType().trim().toUpperCase()) // Similar approach
					      .costInCents(response.getCostInCents())
					      .version(response.getVersion())
	                      .build();
	}

	/**
	 * Build a {@link LiteProduct} instance out of two {@link ProductUpsertRequestBody} instances, one for a PUT
	 * request and another for its originating POST. Since UPDATE requests (PUT, PATCH) are allowed to change as many fields
	 * as they can, some of the crucial fields for the creation of a {@link LiteProduct} might be {@literal null}.
	 * So we need both requests to build a {@link LiteProduct} instance.
	 *
	 * @param putRequest A {@link ProductUpsertRequestBody} instance corresponding to a PUT request.
	 * @param postRequest A {@link ProductUpsertRequestBody} instance corresponding to the POST request that {@code putRequest}
	 *                    comes after.
	 * @return An instance of {@code this}.
	 */
	public static LiteProduct buildLiteProductFromPostAndPutRequests(@NonNull final ProductUpsertRequestBody putRequest,
	                                                                 @NonNull final ProductUpsertRequestBody postRequest)
	{
		return LiteProduct.builder()
		                  .clientProductId(postRequest.getClientProductId())
		                  .productName(Optional.of(putRequest.getName()).orElse(postRequest.getName()))
		                  .productType(Optional.of(putRequest.getProductType()).orElse(postRequest.getProductType()))
		                  .costInCents(Optional.of(putRequest.getCostInCents()).orElse(postRequest.getCostInCents()))
		                  .version(putRequest.getVersion())
		                  .build();
	}
}