package com.company.rest.products.util;
import com.squareup.square.http.client.HttpContext;
import com.squareup.square.models.CatalogObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Collection;
/**
 * A class of utilities that can be used project-wide.
 */
@Slf4j
public class Util
{
	/**
	 * A project-wide constant to assist with mocked tests that need to see a version field in various responses.
	 * @see CatalogObject#getVersion()
	 */
	public static final Long DEFAULT_VERSION_FOR_TESTS = 1000000L;

	/**
	 * Examines if a provided {@link String} could represent a valid product name.
	 * @param productName A {@link String} that may or may not be a valid product name.
	 * @return {@literal true} if {@code productName} is a valid product name, {@literal false} otherwise.
	 */
	public static boolean isValidProductName(@NonNull final String productName)
	{
		return  productName.length() > 0; // && productName.chars().allMatch(Character::isUpperCase);
	}

	/**
	 * Examines the provided condition. If {@literal true}, does nothing, otherwise logs at ERROR level and throws an instance of {@link AssertionError}.
	 * @param condition A {@literal boolean} condition to evaluate for truth.
	 * @param errorMessage An error-descriptive {@link String} that will be fed to the {@link AssertionError} that we will log and
	 *                     throw if {@code condition} is {@literal false}
	 * @throws AssertionError if {@code condition} is {@code false}.
	 */
	public static void assertAndIfNotLogAndThrow(@NonNull final Boolean condition, @NonNull final String errorMessage) throws AssertionError
	{
		if(!condition)
		{
			log.error(errorMessage);
			throw new AssertionError(errorMessage);
		}
	}
	/**
	 * Abbreviates an input {@link String} from its first character up to the character denoted by {@code idx}.
	 *
	 * @param str  The string to abbreviate
	 * @param idx The index of the character that ends the substring generation. If we call it n, the substring generated is s[0,...n-1].
	 *
	 * @throws AssertionError if {@code idx} is less than 1, or if {@code str} has zero length.
	 * @see String#substring(int, int)
	 *
	 * @return {@code str}, appropriately abbreviated.
	 */
	public static String abbreviate(@NonNull final String str, @NonNull Integer idx) throws AssertionError
	{
		assertAndIfNotLogAndThrow(str.length() > 0 && idx >= 1, " Bad params: str = " + str +
		                                                        " and idx = " + idx + ".");
		return str.length() > idx ? str.substring(0, idx) : str;
	}

	/**
	 * Logs a provided {@link Throwable} from the method described by {@code methodName} at {@literal ERROR} level.
	 * @param thrown The {@link Throwable} that was caught in method {@code methodName}.
	 * @param methodName The name of the method that threw the exception.
	 * @see Slf4j
	 */
	public static void logException(@NonNull final Throwable thrown, @NonNull final String methodName)
	{
		log.error("Method " + methodName + " received an instance of " + thrown.getClass().getName() +
		          ", with message: " + thrown.getMessage());
	}
	/**
	 * Ensures that the {@link String} provided begins with the character provided, otherwise prepends the character.
	 *
	 * @param s A {@link String} to check the first character of.
	 * @param c The {@code char} to check against.
	 *
	 * @return {@code s} itself, if it begins with {@code c}, otherwise {@code cs}.
	 */
	public static String ensureFirstCharIs(@NonNull final String s, @NonNull final Character c)
	{
		return (s.length() > 1 && s.charAt(0) == c) ? s : (c + s);
	}

	/**
	 * Assesses if the provided {@link Boolean} is {@literal null} or {@link Boolean#FALSE}.
	 * @param val A {@link Boolean} value.
	 * @return {@literal true} if the param is {@literal null}  or {@link Boolean#FALSE}.
	 */
	public static boolean nullOrFalse(final Boolean val)
	{
		return val == null || val == Boolean.FALSE;
	}

	/**
	 * Assesses if the provided {@link Collection} is {@literal null} or empty.
	 * @param collection A {@link Collection}.
	 * @return {@literal true} if the param is {@literal null}  or an empty collection.
	 */
	public static boolean nullOrEmpty(final Collection<?> collection)
	{
		return collection == null || collection.isEmpty();
	}
	/**
	 * Examines if the provided {@link HttpContext} is of the provided {@link HttpStatus}.
	 * @param httpContext An {@link HttpContext} instance.
	 * @param  target An {@link HttpStatus} instance.
	 * @return {@literal true} if {@code target} is the HTTP status of {@code httpContext}, {@literal false}
	 * otherwise.
	 */
	public static boolean nullOrProvidedStatus(final HttpContext httpContext, final HttpStatus target)
	{
		return httpContext == null || httpContext.getResponse().getStatusCode() == target.value();
	}

	/**
	 * Simple helper to determine if two {@link String}s match, ignoring case, and checking for {@literal null}s.
	 *
	 * @param name1  A {@link String} name.
	 * @param name2  A {@link String} name.
	 *
	 * @return {@literal true} if the names are both null or if they match by ignoring case.
	 */
	public static boolean stringsMatch(final String name1, final String name2)
	{
		if(name1 == null && name2 == null)
		{
			return true;
		}
		else if(name1 != null && name2 != null)
		{
			return name1.equalsIgnoreCase(name2);
		}
		else
		{
			return false;
		}
	}
}
