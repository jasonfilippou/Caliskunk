package com.company.rest.products.util.loggers;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * A logger for {@link com.company.rest.products.controller.ProductController}. Uses AOP features to
 * ensure that appropriate logging with Sl4j.
 *
 * @see lombok.extern.slf4j.XSlf4j
 */
@Component
@Aspect
@Slf4j
public class ProductControllerLogger
{
	/* Fields and Utilities */
	private enum LOC
	{
		BEGIN, END
	}


	private String msg(final String method, final LOC pointInMethod, final JoinPoint jp)
	{
		final String args = Arrays.toString(jp.getArgs());

		return ((pointInMethod == LOC.BEGIN) ? "Serving" : "Served") + " a " + method + " request via a call to " +
		       jp.getSignature().getDeclaringTypeName() + "." + jp.getSignature().getName() + "(" +
		       ((args.length() == 0) ? "" : args.substring(1, args.length() - 1)) + ")";
	}

	/* GET (id) */
	@Before("execution(* com.company.rest.products.controller.ProductController.getProduct(..))")
	public void beforeGetRequests(final JoinPoint jp)
	{
		log.info(msg("GET(id)", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.getProduct(..))")
	public void afterGetRequests(final JoinPoint jp)
	{
		log.info(msg("GET(id)", LOC.END, jp));
	}

	/* GET ALL */
	@Before("execution(* com.company.rest.products.controller.ProductController.getAll(..))")
	public void beforeGetAllRequests(final JoinPoint jp)
	{
		log.info(msg("GET ALL", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.getAll(..))")
	public void afterGetAllRequests(final JoinPoint jp)
	{
		log.info(msg("GET ALL", LOC.END, jp));
	}
	/* POST */
	@Before("execution(* com.company.rest.products.controller.ProductController.post*(..))")
	public void beforePostRequests(final JoinPoint jp)
	{
		log.info(msg("POST", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.post*(..))")
	public void afterPostRequests(final JoinPoint jp)
	{
		log.info(msg("POST", LOC.END, jp));
	}

	/* PUT */
	@Before("execution(* com.company.rest.products.controller.ProductController.put*(..))")
	public void beforePutRequests(final JoinPoint jp)
	{
		log.info(msg("PUT", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.put*(..))")
	public void afterPutRequests(final JoinPoint jp)
	{
		log.info(msg("PUT", LOC.END, jp));
	}

	/* PATCH */
	@Before("execution(* com.company.rest.products.controller.ProductController.patch*(..))")
	public void beforePatchRequests(final JoinPoint jp)
	{
		log.info(msg("PATCH", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.patch*(..))")
	public void afterPatchRequests(final JoinPoint jp)
	{
		log.info(msg("PATCH", LOC.END, jp));
	}

	/* DELETE */
	@Before("execution(* com.company.rest.products.controller.ProductController.delete*(..))")
	public void beforeDeleteRequests(final JoinPoint jp)
	{
		log.info(msg("DELETE", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.delete*(..))")
	public void afterDeleteRequests(final JoinPoint jp)
	{
		log.info(msg("DELETE", LOC.END, jp));
	}

}
