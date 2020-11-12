package com.company.rest.products.test.util;


import com.company.rest.products.model.liteproduct.LiteProductRepository;
import com.company.rest.products.util.ResponseMessage;
import com.company.rest.products.util.request_bodies.ProductResponseBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * A class of utilities for unit tests. We leverage {@code jUnit4} assertions.
 * @see ResponseEntity
 * @see ResponseMessage
 * @see org.junit.Assert
 * @see AssertionError
 */
public class TestUtil
{
//	/**
//	 * Ensure a given {@link ResponseEntity} has status {@code HTTP OK} and return the query data.
//	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
//	 * @return the data contained in {@code responseEntity}.
//	 */
//	public static ProductResponseBody checkHttpOkAndGet(final ResponseEntity<ResponseMessage> responseEntity)
//	{
//		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//		return getResponseData(responseEntity);
//	}

	/**
	 * Ensure that a provided {@link ResponseEntity} has the provided {@link HttpStatus}.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @param status The {@link HttpStatus} that we want to check against.
	 */
	public static void checkEntityStatus(final ResponseEntity<ResponseMessage> responseEntity, final HttpStatus status)
	{
		assertEquals(status, responseEntity.getStatusCode());
	}


	/**
	 * Ensure that the provided {@link ResponseEntity} has the appropriate {@link HttpStatus} and return the query data.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @param status The {@link HttpStatus} to check against.
	 * @see #checkHttpOkAndGet(ResponseEntity)
	 * @see #checkEntityStatus(ResponseEntity, HttpStatus)
	 * @return The information contained within {@code responseEntity}.
	 */
	public static ProductResponseBody checkEntityStatusAndFetchResponse(final ResponseEntity<ResponseMessage> responseEntity,
	                                                                    final HttpStatus status)
	{
		checkEntityStatus(responseEntity, status);
		return getResponseData(responseEntity);
	}


	/**
	 * Retrieve the query data contained in the {@link ResponseEntity} argument.
	 * @param responseEntity The response of the {@link com.company.rest.products.controller.ProductController}.
	 * @return The information contained within {@code responseEntity}.
	 */
	public static ProductResponseBody getResponseData(final ResponseEntity<ResponseMessage> responseEntity)
	{
		return (ProductResponseBody) Objects.requireNonNull(responseEntity.getBody()).getData();
	}


	/**
	 * Delete all the instances of the {@link LiteProductRepository} argument. Useful for testing when using
	 * a persistent local database, such as MySQL, PostGres, Mongo, etc
	 * @param repo The - usually {@code @Autowired} {@link com.company.rest.products.model.liteproduct.LiteProductRepository}
	 *             instance that we want to flush.
	 * @see JpaRepository#deleteAll()
	 */
	public static void flushRepo(LiteProductRepository repo)
	{
		repo.deleteAll();
	}
}
