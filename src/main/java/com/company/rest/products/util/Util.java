package com.company.rest.products.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * A class of utilities that can be used project-wide.
 */

@Slf4j
public class Util
{
    public static void assertAndIfNotLogAndThrow(boolean condition, String errorMessage)
	{
		if(!condition)
		{
			log.error(errorMessage);
			throw new AssertionError(errorMessage);
		}
	}

	public static String abbreviate(@NonNull String str, int idx)
	{
		assertAndIfNotLogAndThrow(str.length() > 0 && idx >= 1, " Bad params: str = " + str +
		                                                        " and idx = " + idx + ".");
		return str.length() > idx ? str.substring(0, idx) : str;
	}

	public static void logException(Throwable thrown, String methodName)
	{
		log.error("Method " + methodName + " received an instance of " + thrown.getClass().getName() +
		          ", with message: " + thrown.getMessage() + ".");
	}
}
