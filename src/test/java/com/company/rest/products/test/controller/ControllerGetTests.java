package com.company.rest.products.test.controller;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.test.model.backend.BackendServiceGetTests;
import com.company.rest.products.test.model.square.SquareServiceGetTests;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.BackendServiceResponseBody;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static com.company.rest.products.test.controller.MockedBackendServiceGetResponses.MOCKED_BACKEND_GET_RESPONSES;
import static com.company.rest.products.test.controller.MockedBackendServicePostResponses.MOCKED_BACKEND_POST_RESPONSES;
import static com.company.rest.products.test.requests_responses.get.GoodGetRequests.GOOD_GETS;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Mocked GET request tests for {@link ProductController}.
 *
 * This unit test suite is actually subsumed by {@link ControllerPostTests}.
 *
 * @see ProductController
 * @see BackendServiceGetTests
 * @see SquareServiceGetTests
 * @see com.company.rest.products.test.end_to_end.EndToEndGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerGetTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@InjectMocks
	private ProductController controller; // The class we are testing

	@Mock
	private BackendService backendService;     // The class that will be mocked

	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Test
	public void testOneGet()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_GET_ID";
		final ProductUpsertRequestBody postRequest = ProductUpsertRequestBody
				.builder()
				.productName("Pengolin's Revenge")
				.productType("Vaporizer")
				.clientProductId(productId)
				.costInCents(13000L) // 'L for long literal
				.description("We're done.")
				.labelColor("7FFFD4")
				.upc("RANDOM_UPC")
				.sku("RANDOM_SKU")
				.build();

		// Define mocked answer
		final BackendServiceResponseBody mockedResponse = BackendServiceResponseBody
				.builder()
				.name(postRequest.getProductName())
				.clientProductId(postRequest.getClientProductId())
				.squareItemId("#RANDOM_SQUARE_ITEM_ID")
				.updatedAt(DEFAULT_UPDATED_AT_STRING)
				.productType(postRequest.getProductType())
				.costInCents(postRequest.getCostInCents())
				.isDeleted(false)
				.sku(postRequest.getSku())
				.upc(postRequest.getUpc())
				.description(postRequest.getDescription())
				.labelColor(postRequest.getLabelColor())
				.version(DEFAULT_VERSION)
				.build();

		// Mock the call to the backend service
		when(backendService.postProduct(postRequest)).thenReturn(mockedResponse);

		// Make the call to the controller
		final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(postRequest);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding GET, and ensure it works. Mock the backend call.
		final ProductGetRequestBody getRequest = new ProductGetRequestBody(productId);
		when(backendService.getProduct(getRequest)).thenReturn(BackendServiceResponseBody.fromProductResponseBody(postResponse));

		final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(productId);
		final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);
		assertTrue("Request did not match response", responseMatchesGetRequest(getRequest, getResponse));
	}

	@Test
	public void testManyGets()
	{
		assert GOOD_GETS.length == GOOD_POSTS.length : "Mismatch between GET Request array length and POST Request array length.";
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////

			// Mock the backend POST call.
			when(backendService.postProduct(GOOD_POSTS[i])).thenReturn(MOCKED_BACKEND_POST_RESPONSES[i]);

			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GOOD_POSTS[i]);
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

			///////////////////////////////////
			// And now we check the GET call.//
			///////////////////////////////////

			// Mock the backend GET call.
			when(backendService.getProduct(GOOD_GETS[i])).thenReturn(MOCKED_BACKEND_GET_RESPONSES[i]);    // You will still be getting the data from POST!

			// Call controller
			final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(GOOD_GETS[i].getClientProductId());
			final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);

			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).", responseMatchesGetRequest(GOOD_GETS[i], getResponse));

		}
	}

	@Test
	public void testGetAll()
	{
		final int DEFAULT_NUM_PAGES = 10;
		final long totalElements = GOOD_POSTS.length;
		final int totalPages  = Math.min(DEFAULT_NUM_PAGES, GOOD_POSTS.length);
		final String sortBy = "costInCents";    // TODO: vary this
		final Map<String, Comparator<ProductUpsertRequestBody>> sortingStrategies = createSortingStrategies();
		Arrays.sort(GOOD_POSTS, sortingStrategies.get(sortBy)); // Sorts in place.
		for(int i = 0; i < totalPages; i++)
		{
			final int expectedNumElementsInPage = getNumElementsInPage(i, totalPages, totalElements);
			// Mock backend GET ALL call
			when(backendService.getAllProducts(i, expectedNumElementsInPage, sortBy)).thenReturn(mockedPage(i * expectedNumElementsInPage, expectedNumElementsInPage, GOOD_POSTS));
			@SuppressWarnings("unchecked")
			final Page<LiteProduct> page = (Page<LiteProduct>) Objects.requireNonNull(controller.getAll(i, expectedNumElementsInPage, sortBy).getBody()).getData();
			// Evaluate response
			assertEquals("Unexpected number of elements in returned page", page.getNumberOfElements(), expectedNumElementsInPage);
			assertTrue("Page did not return the appropriate posted elements." , pageMatchesPostedElements(page, i*expectedNumElementsInPage, GOOD_POSTS));
			assertTrue("Page has correct successor page information", checkPageSuccessor(i, totalPages, page));
		}
	}

	private int getNumElementsInPage(final int pageIdx, final int totalPages, final long totalElements)
	{
		return (int) (pageIdx < totalPages - 1 ? totalElements / totalPages : totalElements % totalPages);
	}

	private Page<LiteProduct> mockedPage(final int startElementIdx, final int elementsInPage, final ProductUpsertRequestBody[] sortedRequests)
	{
		final List<LiteProduct> transformedRequests = Arrays.stream(sortedRequests).map(this::toyLiteProduct).collect(Collectors.toList());
		return new PageImpl<>(transformedRequests.subList(startElementIdx, elementsInPage));
	}

	private LiteProduct toyLiteProduct(final ProductUpsertRequestBody request)
	{
		return LiteProduct.builder()
		                  .clientProductId(Optional.of(request.getClientProductId()).orElseThrow(() -> new IllegalArgumentException("Request did not have client product ID")))
		                  .productName(Optional.of(request.getProductName()).orElse(DEFAULT_PRODUCT_NAME))
		                  .productType(Optional.of(request.getProductType()).orElse(DEFAULT_PRODUCT_TYPE))
		                  .costInCents(Optional.of(request.getCostInCents()).orElse(DEFAULT_COST_IN_CENTS))
		                  .squareItemId(Optional.of(request.getSquareItemId()).orElse(DEFAULT_SQUARE_ITEM_ID))
		                  .squareItemVariationId(Optional.of(request.getSquareItemVariationId()).orElse(DEFAULT_SQUARE_ITEM_VARIATION_ID))
		                  .version(Optional.of(request.getVersion()).orElse(DEFAULT_VERSION))
		                  .build();
	}
}
