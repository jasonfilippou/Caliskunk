package com.company.rest.products.test.end_to_end;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import com.company.rest.products.util.request_bodies.ProductUpsertRequestBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.company.rest.products.test.requests_responses.post.GoodPostRequests.GOOD_POSTS;
import static com.company.rest.products.test.util.TestUtil.UpsertType.POST;
import static com.company.rest.products.test.util.TestUtil.*;
import static org.junit.Assert.assertTrue;

/**
 * End-to-end POST tests.
 *
 * @see EndToEndDeleteTests
 * @see EndToEndGetTests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EndToEndPostTests
{

	/* *********************************************************************************************************** */
	/* ************************************ Fields and utilities ************************************************** */
	/* *********************************************************************************************************** */

	@Autowired
	private ProductController controller;

	@Autowired
	private LiteProductRepository repository;

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
	public void testOnePost()
	{
		final ProductUpsertRequestBody request = ProductUpsertRequestBody
													.builder()
													.name("Ramses V")
													.productType("topical")
													.clientProductId("#RANDOM_ID")
													.costInCents(10000L) // 'L for long literal
													.description("Will challenge his father for the throne")
													.labelColor("7FAAD4")
													.upc("RANDOM_UPC")
													.sku("RANDOM_SKU")



													.build();
		final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(request);
		final ProductResponseBody response = checkEntityStatusAndFetchResponse(responseEntity, HttpStatus.CREATED);
		assertTrue("Request did not match response", responseMatchesUpsertRequest(request, response, POST));
	}

	@Test
	public void testManyPosts()
	{
		final int numRequests = GOOD_POSTS.length;
		for(int i = 0; i <  numRequests; i++)
		{
			// Call controller
			final ResponseEntity<ResponseMessage> responseEntity = controller.postProduct(GOOD_POSTS[i]);
			final ProductResponseBody response = checkEntityStatusAndFetchResponse(responseEntity, HttpStatus.CREATED);
			// Assess response
			assertTrue("Mismatch in response #" + i + " (0-indexed).",
			           responseMatchesUpsertRequest(GOOD_POSTS[i], response, POST));
		}
	}

}
