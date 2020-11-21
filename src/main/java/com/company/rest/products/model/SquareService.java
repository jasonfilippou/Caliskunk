package com.company.rest.products.model;
import com.company.rest.products.util.exceptions.SquareServiceException;
import com.company.rest.products.util.exceptions.UnimplementedMethodPlaceholder;
import com.company.rest.products.util.request_bodies.ProductDeleteRequestBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import com.company.rest.products.util.request_bodies.SquareServiceResponseBody;
import com.squareup.square.models.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.company.rest.products.util.Util.*;
import static java.util.Optional.ofNullable;
/**
 * Square service class. Uses {@link CatalogWrapper}'s calls to interact with Square API and returns response
 * to {@link BackendService}.
 *
 * @see BackendService
 * @see CatalogWrapper
 * @see SquareServiceException
 * @see SquareServiceResponseBody
 */
@Slf4j
@Component
public class SquareService
{
	public static final String PRICE_MODEL = "FIXED_PRICING";
	public static final String DEFAULT_ITEM_VARIATION_NAME_SUFFIX = "_DEFAULT_VARIATION";
	public static final String DEFAULT_ITEM_VARIATION_ID_SUFFIX = "_VAR";
	public static final String CURRENCY = "USD";
	public static final String CODE_FOR_CATALOG_ITEMS = "ITEM";
	public static final String CODE_FOR_CATALOG_ITEM_VARIATIONS = "ITEM_VARIATION";
	public static final Integer ABBRV_CHARS = 3;
	public static final String DEFAULT_SQUARE_CATALOG_ITEM_TYPE = "REGULAR";
	private final CatalogWrapper catalogWrapper;

	/**
	 * Constructor takes an autowired {@link CatalogWrapper} instance  as a parameter.
	 *
	 * @param catalogWrapper A - usually {@code @Autowired} - {@link CatalogWrapper} instance.
	 */
	@Autowired
	public SquareService(@NonNull final CatalogWrapper catalogWrapper)
	{
		this.catalogWrapper = catalogWrapper;
	}
	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity does <i>not</i> already exist in the repo by checking cached instances.
	 *
	 * @param clientPostRequest A {@link ProductUpsertRequestBody} instance containing details of the POST request.
	 * @return A serialized response containing the information of this layer.
	 * @throws SquareServiceException if any Exception is sent to us by Square.
	 */
	public SquareServiceResponseBody postProduct(@NonNull final ProductUpsertRequestBody clientPostRequest) throws SquareServiceException
	{
		//	For now, we have a 1-1 correspondence between them; every newly inserted
		//	product will consist of a CatalogItem and a CatalogItemVariation registered to it.
		try
		{
			validatePostRequest(clientPostRequest);
			final UpsertCatalogObjectRequest squarePostRequest = prepareCatalogPostRequest(clientPostRequest);
			final UpsertCatalogObjectResponse squarePostResponse = catalogWrapper.upsertObject(squarePostRequest);
			validatePostResponse(squarePostResponse, clientPostRequest);
			return SquareServiceResponseBody.fromUpsertRequestAndResponse(clientPostRequest, squarePostResponse);
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::upsertProduct");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validatePostRequest(final ProductUpsertRequestBody postRequest)
	{
		assertAndIfNotLogAndThrow(postRequest.getClientProductId() != null &&
		                          isValidProductName(postRequest.getName()),
		                          "Bad POST request");
	}

	private UpsertCatalogObjectRequest prepareCatalogPostRequest(final ProductUpsertRequestBody postRequest)
	{
		return new UpsertCatalogObjectRequest.Builder(UUID.randomUUID().toString(), prepareCatalogObjectForPost(postRequest)).build();
	}

	private CatalogObject prepareCatalogObjectForPost(final ProductUpsertRequestBody postRequest)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, postRequest.getClientProductId())
				.itemData(prepareCatalogItemFieldForPost(postRequest))
				.build();
	}

	private CatalogItem prepareCatalogItemFieldForPost(final ProductUpsertRequestBody postRequest)
	{
		final CatalogObject variation = prepareCatalogItemVariationForPost(postRequest);
		return new CatalogItem.Builder()
				.name(postRequest.getName())
				.abbreviation(abbreviate(postRequest.getName(), ABBRV_CHARS))
				.availableElectronically(postRequest.getAvailableElectronically())
				.availableForPickup(postRequest.getAvailableForPickup())
				.availableOnline(postRequest.getAvailableOnline())
				.description(postRequest.getDescription())
				.labelColor(postRequest.getLabelColor())
				.variations(Collections.singletonList(variation))
				.build();
	}

	private CatalogObject prepareCatalogItemVariationForPost(final ProductUpsertRequestBody postRequest)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEM_VARIATIONS,
		                                 postRequest.getClientProductId() + DEFAULT_ITEM_VARIATION_ID_SUFFIX)
				.itemVariationData(prepareCatalogItemVariationFieldForPost(postRequest))
				.build();
	}

	private CatalogItemVariation prepareCatalogItemVariationFieldForPost(final ProductUpsertRequestBody postRequest)
	{
		return new CatalogItemVariation.Builder()
				.name(postRequest.getName())
				.itemId(postRequest.getClientProductId())
				.pricingType(PRICE_MODEL)
				.priceMoney(new Money(postRequest.getCostInCents(), CURRENCY))
				.sku(postRequest.getSku())
				.upc(postRequest.getUpc())
				.build();
	}

	private void validatePostResponse(final UpsertCatalogObjectResponse postResponse, final ProductUpsertRequestBody postRequest)
	{
		final CatalogObject catalogObject = postResponse.getCatalogObject();
		final CatalogItem catalogItem = catalogObject.getItemData();
		assertAndIfNotLogAndThrow(catalogItem.getVariations() != null && catalogItem.getVariations().size() == 1,
		                          "Square API should have returned one CatalogItem and one CatalogItemVariation.");
		final CatalogItemVariation catalogItemVariation = catalogItem.getVariations().get(0).getItemVariationData();
		assertAndIfNotLogAndThrow(noErrorsInResponse(postResponse) &&
		                          catalogObject.getType().equals(CODE_FOR_CATALOG_ITEMS) &&
		                          !catalogObject.getIsDeleted() &&
		                          postResponse.getIdMappings().size() == 2 &&
		                          postResponse.getIdMappings().stream().map(CatalogIdMapping::getClientObjectId)
                                                                       .collect(Collectors.toList())
                                                                       .containsAll(Arrays.asList(postRequest.getClientProductId(),
					                                                                              postRequest.getClientProductId() + DEFAULT_ITEM_VARIATION_ID_SUFFIX)) &&
		                          postResponse.getContext().getResponse().getStatusCode() == HttpStatus.OK.value() &&
		                          catalogItemVariation.getItemId().equals(catalogObject.getId()) &&
		                          catalogItemVariation.getPriceMoney().getCurrency().equals(CURRENCY) &&
		                          catalogItem.getName().equals(postRequest.getName()) &&    // This subsumes abbreviation test
		                          catalogItemVariation.getName().equals(catalogItem.getName() + DEFAULT_ITEM_VARIATION_NAME_SUFFIX) &&
		                          optionalFieldsMatch(catalogItem, catalogItemVariation, postRequest),
		                          "Bad Upsert response from Square API"
		                         );
	}

	private boolean noErrorsInResponse(final UpsertCatalogObjectResponse postResponse)
	{
		return (postResponse.getErrors() == null || postResponse.getErrors().size() == 0);
	}

	private boolean optionalFieldsMatch(final CatalogItem catalogItem, final CatalogItemVariation catalogItemVariation,
	                                    final ProductUpsertRequestBody postRequest)
	{
		return 	ofNullable(catalogItem.getAvailableElectronically()).equals(ofNullable(postRequest.getAvailableElectronically())) &&
				ofNullable(catalogItem.getAvailableOnline()).equals(ofNullable(postRequest.getAvailableOnline())) &&
				ofNullable(catalogItem.getAvailableForPickup()).equals(ofNullable(postRequest.getAvailableForPickup())) &&
				ofNullable(catalogItem.getDescription()).equals(ofNullable(postRequest.getDescription())) &&
				ofNullable(catalogItem.getLabelColor()).equals(ofNullable(postRequest.getLabelColor())) &&
				ofNullable(catalogItemVariation.getPriceMoney().getAmount()).equals(ofNullable(postRequest.getCostInCents())) &&
				ofNullable(catalogItemVariation.getSku()).equals(ofNullable(postRequest.getSku())) &&
				ofNullable(catalogItemVariation.getUpc()).equals(ofNullable(postRequest.getUpc()));
	}

	/**
	 * Handle a POST request to Square's API. The caller must have already ensured
	 * that the entity <i>does</i> already exist in the repo by checking cached instances.
	 *
	 * @param clientPutRequest A {@link ProductUpsertRequestBody} instance containing details of the request.
	 * @return A serialized response containing the information of this layer.
	 * @throws SquareServiceException if any Exception is sent to us by Square.
	 */
	public SquareServiceResponseBody putProduct(@NonNull final ProductUpsertRequestBody clientPutRequest) throws SquareServiceException
	{
		try
		{
			validatePutRequest(clientPutRequest);
			final UpsertCatalogObjectRequest squarePutRequest = prepareCatalogPutRequest(clientPutRequest);
			final UpsertCatalogObjectResponse squarePutResponse = catalogWrapper.upsertObject(squarePutRequest);
			validatePutResponse(squarePutResponse, clientPutRequest);
			return SquareServiceResponseBody.fromUpsertRequestAndResponse(clientPutRequest, squarePutResponse);
		}
		catch (InterruptedException | ExecutionException e)
		{
			logException(e, this.getClass().getName() + "::upsertProduct");
			throw new SquareServiceException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validatePutRequest(final ProductUpsertRequestBody putRequest)
	{
		assertAndIfNotLogAndThrow(putRequest.getClientProductId() != null &&
		                                    putRequest.getSquareProductId() != null &&
		                                    putRequest.getVersion() != null && isValidProductName(putRequest.getName()),
		                          "Need valid version ID for PUT request");
	}
	
	private void validatePutResponse(final UpsertCatalogObjectResponse putResponse, final ProductUpsertRequestBody putRequest)
	{
		validatePostResponse(putResponse, putRequest);  // Identical logic
	}

	private UpsertCatalogObjectRequest prepareCatalogPutRequest(final ProductUpsertRequestBody putRequest)
	{
		return new UpsertCatalogObjectRequest.Builder(UUID.randomUUID().toString(), prepareCatalogObjectForPut(putRequest)).build();
	}

	private CatalogObject prepareCatalogObjectForPut(final ProductUpsertRequestBody putRequest)
	{
		return new CatalogObject.Builder(CODE_FOR_CATALOG_ITEMS, putRequest.getSquareProductId()) // Instead of the clientProductId!
				.itemData(prepareCatalogItemFieldForPost(putRequest))                             // Identical logic.
				.version(putRequest.getVersion())                                                 // Alongside the right ID above, necessary for the PUT to succeed.
				.build();
	}
	
	/**
	 * Send a GET request for a specific product.
	 *
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 * @throws SquareServiceException if Square sends an Exception.
	 * @see BackendService#getProduct(ProductGetRequestBody)
	 * @see CatalogWrapper#retrieveObject(String)
	 */
	public SquareServiceResponseBody getProduct(@NonNull final ProductGetRequestBody clientGetRequest) throws SquareServiceException
	{
		try
		{
			validateGetRequest(clientGetRequest);
			final RetrieveCatalogObjectResponse squareGetResponse = catalogWrapper.retrieveObject(clientGetRequest.getLiteProduct().getSquareItemId());
			validateGetResponse(squareGetResponse, clientGetRequest);
			return SquareServiceResponseBody.fromGetRequestAndResponse(clientGetRequest, squareGetResponse);
		}
		catch (Throwable t)
		{
			logException(t, "::getProduct()");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void validateGetRequest(final ProductGetRequestBody getRequest)
	{
		assertAndIfNotLogAndThrow(getRequest.getClientProductId() != null &&
		                                    getRequest.getClientProductId().equals(getRequest.getLiteProduct().getClientProductId()) &&
		                                    getRequest.getLiteProduct().getSquareItemId() != null &&
											isValidProductName(getRequest.getLiteProduct().getProductName()),
		                          "Bad GET request supplied.");
	}
	
	private void validateGetResponse(final RetrieveCatalogObjectResponse getResponse, final ProductGetRequestBody getRequest)
	{
		final CatalogObject catalogObject = getResponse.getObject();
		final CatalogItem catalogItem = catalogObject.getItemData();
		assertAndIfNotLogAndThrow(catalogItem.getVariations() != null && catalogItem.getVariations().size() == 1,
		                          "Square API should have returned one CatalogItem and one CatalogItemVariation.");
		final CatalogItemVariation catalogItemVariation = catalogItem.getVariations().get(0).getItemVariationData();
		assertAndIfNotLogAndThrow(noErrorsInResponse(getResponse) &&
		                          catalogObject.getId().equals(getRequest.getLiteProduct().getSquareItemId()) &&
		                          catalogObject.getType().equals(CODE_FOR_CATALOG_ITEMS) &&
		                          !catalogObject.getIsDeleted() &&
		                          getResponse.getContext().getResponse().getStatusCode() == HttpStatus.OK.value() &&
		                          catalogItemVariation.getItemId().equals(catalogObject.getId()) &&
		                          catalogItemVariation.getPriceMoney().getCurrency().equals(CURRENCY) &&
		                          catalogItem.getName().equals(getRequest.getLiteProduct().getProductName()) &&
		                          catalogItemVariation.getName().equals(catalogItem.getName() + DEFAULT_ITEM_VARIATION_NAME_SUFFIX) &&
		                          ofNullable(catalogItemVariation.getPriceMoney().getAmount()).equals(ofNullable(catalogItemVariation.getPriceMoney().getAmount())),
					 "Bad GET response from Square API");
	}

	private boolean noErrorsInResponse(final RetrieveCatalogObjectResponse getResponse)
	{
		return (getResponse.getErrors() == null || getResponse.getErrors().size() == 0);
	}

	/**
	 * Send a PATCH request for a specific product.
	 *
	 * @param id      The product's unique id.
	 * @param putRequest The JSON request that contains the information we want to patch the product with.
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 * @throws SquareServiceException if Square sends an Exception.
	 * @see CatalogWrapper#upsertObject(UpsertCatalogObjectRequest)
	 */
	public SquareServiceResponseBody patchProduct(@NonNull final ProductUpsertRequestBody putRequest, @NonNull final String id) throws SquareServiceException
	{
		throw new UnimplementedMethodPlaceholder();
	}
	/**
	 * Send a DELETE request for a specific product.
	 *
	 * @param clientDeleteRequest A {@link ProductDeleteRequestBody} instance with information pertaining to the DELETE request.
	 * @return A {@link SquareServiceResponseBody} describing the output of this layer.
	 * @throws SquareServiceException if Square sends an Exception of some kind.
	 * @see BackendService#deleteProduct(ProductDeleteRequestBody)
	 * @see CatalogWrapper#deleteObject(String)
	 */
	public SquareServiceResponseBody deleteProduct(@NonNull final ProductDeleteRequestBody clientDeleteRequest) throws SquareServiceException
	{
		// Just as a reminder, deletions on Square are cascading, so that deletion of a CatalogItem
		// will also yield the deletion of all of its CatalogItemVariations. Therefore, we don't need information
		// about the variation ID as an arg.
		try
		{
			validateDeleteRequest(clientDeleteRequest);
			final DeleteCatalogObjectResponse squareDeleteResponse = catalogWrapper.deleteObject(clientDeleteRequest.getLiteProduct().getSquareItemId());
			validateDeleteResponse(squareDeleteResponse, clientDeleteRequest);
			return SquareServiceResponseBody.fromDeleteRequestAndResponse(clientDeleteRequest, squareDeleteResponse);
		}
		catch (Throwable t)
		{
			logException(t, this.getClass().getName() + "::deleteProduct");
			throw new SquareServiceException(t, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateDeleteRequest(final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(deleteRequest.getClientProductId() != null &&
		                          deleteRequest.getClientProductId().equals(deleteRequest.getLiteProduct().getClientProductId()) &&
		                          deleteRequest.getLiteProduct().getSquareItemId() != null &&
		                          isValidProductName(deleteRequest.getLiteProduct().getProductName()),
		                          "Bad DELETE request");
	}

	private void validateDeleteResponse(final DeleteCatalogObjectResponse deleteResponse, final ProductDeleteRequestBody deleteRequest)
	{
		assertAndIfNotLogAndThrow(noErrorsInResponse(deleteResponse) &&
		                          deleteResponse.getContext().getResponse().getStatusCode() == HttpStatus.OK.value() &&
		                          deleteResponse.getDeletedObjectIds() != null && deleteResponse.getDeletedObjectIds().size() == 2 &&
		                          deleteResponse.getDeletedObjectIds().containsAll(Arrays.asList(deleteRequest.getLiteProduct().getSquareItemId(),
		                                                                                         deleteRequest.getLiteProduct().getSquareItemId() + DEFAULT_ITEM_VARIATION_ID_SUFFIX)) &&
		                          deleteResponse.getDeletedAt() != null,
		                          "Bad DELETE response from Square API"
		                         );
	}

	private boolean noErrorsInResponse(final DeleteCatalogObjectResponse deleteResponse)
	{
		return (deleteResponse.getErrors() == null || deleteResponse.getErrors().size() == 0);
	}

}
