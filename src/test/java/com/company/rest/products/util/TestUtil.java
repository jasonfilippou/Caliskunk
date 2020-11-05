package com.company.rest.products.util;


import com.company.rest.products.util.request_bodies.ProductResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * A class of utilities for unit tests.
 */
public class TestUtil
{
	public static ProductResponseBody checkAndGet(final ResponseEntity<ResponseMessage> responseEntity)
	{
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		return getResponseData(responseEntity);
	}


	public static void checkEntityStatus(final ResponseEntity<ResponseMessage> responseEntity, final HttpStatus status)
	{
		assertEquals(responseEntity.getStatusCode(), status);
	}


	public static ProductResponseBody checkEntityStatusAndFetchResponse(final ResponseEntity<ResponseMessage> responseEntity, final HttpStatus status)
	{
		checkEntityStatus(responseEntity, status);
		return getResponseData(responseEntity);
	}


	public static ProductResponseBody getResponseData(final ResponseEntity<ResponseMessage> responseEntity)
	{
		return (ProductResponseBody) Objects.requireNonNull(responseEntity.getBody()).getData();
	}

	public static ProductResponseBody getAndCheckResponse(final ResponseEntity<ResponseMessage> responseEntity)
	{
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		return getResponseData(responseEntity);
	}

}
