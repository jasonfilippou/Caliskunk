package com.company.rest.products.test.end_to_end;
import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProduct;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductGetRequestBody;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import lombok.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.company.rest.products.test.requests_responses.get.GoodGetRequests.GOOD_GETS;
import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * End-to-end GET tests.
 *
 * This unit test suite is actually subsumed by {@link EndToEndPostTests}.
 *
 * @see EndToEndPostTests
 * @see EndToEndDeleteTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndGetTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@Autowired
	private ProductController controller;

	@Autowired
	private LiteProductRepository repository;


	private boolean responseMatchesGetRequest(@NonNull ProductGetRequestBody getRequestBody,
	                                           @NonNull ProductResponseBody responseBody)
	{
		return	getRequestBody.getClientProductId().equals(responseBody.getClientProductId());
	}


	/* *********************************************************************************************************** */
	/* ***************************************** TESTS *********************************************************** */
	/* *********************************************************************************************************** */

	@Before
	public void setUp()
	{
		flushRepo(repository);
	}

	@After
	public void tearDown()
	{
		flushRepo(repository);
	}

	@Test
	public void testOneGet()
	{
		// Do a POST first, so that we can retrieve it afterwards.
		final String productId = "#TEST_ITEM_FOR_GET_ID";
		final ResponseEntity<ResponseMessage> postResponseEntity = makeAPost(productId);
		final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

		// Now do the corresponding GET..
		final ProductGetRequestBody request = new ProductGetRequestBody(productId);

		final ResponseEntity<ResponseMessage> getResponseEntity = controller.getProduct(productId);
		final ProductResponseBody getResponse = checkEntityStatusAndFetchResponse(getResponseEntity, HttpStatus.FOUND);
		assertTrue("Request did not match response", responseMatchesGetRequest(request, getResponse));
	}

	private ResponseEntity<ResponseMessage> makeAPost(final String clientProductId)
	{
		// Make post request
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
														.productName("Pengolin's Revenge")
														.productType("Vaporizer")
														.clientProductId(clientProductId)
														.costInCents(13000L) // 'L for long literal
														.description("We're done.")
														.labelColor("7FFFD4")
														.upc("RANDOM_UPC")
														.sku("RANDOM_SKU")
													.build();

		// Make the call to the controller
		return controller.postProduct(request);
	}

	@Test
	public void testManyGets()
	{

		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			////////////////////////////////
			// First we POST the resource://
			////////////////////////////////


			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(GOOD_POSTS[i]);
			final ProductResponseBody postResponse = checkEntityStatusAndFetchResponse(postResponseEntity, HttpStatus.CREATED);

			//Optionally, check the POST response (ostensibly there's no need since there's already a POST test suite).
			// assertTrue("Request did not match response", responseMatchesPostRequest(GOOD_POSTS[i],  postResponse));

			///////////////////////////////////
			// And now we check the GET call.//
			///////////////////////////////////

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
		// First, make several POST requests.
		final long totalElements = GOOD_POSTS.length;
		for (ProductUpsertRequestBody goodPost : GOOD_POSTS)
		{
			// Call controller
			final ResponseEntity<ResponseMessage> postResponseEntity = controller.postProduct(goodPost);
		}
		final int totalPages  = Math.min(DEFAULT_NUM_PAGES, GOOD_POSTS.length);
		final String sortBy = "costInCents";    // TODO: vary this
		final Map<String, Comparator<ProductUpsertRequestBody>> sortingStrategies = createSortingStrategies();
		Arrays.sort(GOOD_POSTS, sortingStrategies.get(sortBy)); // Sorts in place.
		for(int i = 0; i < totalPages; i++)
		{
			final int expectedNumElementsInPage = getNumElementsInPage(i, totalPages, totalElements);
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
