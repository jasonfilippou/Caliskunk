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
	private enum LOC
	{
		BEGIN, END
	}

	/* Utilities */
	private String msg(String method, LOC pointInMethod, JoinPoint jp)
	{
		String args = Arrays.toString(jp.getArgs());

		return ((pointInMethod == LOC.BEGIN) ? "Serving" : "Served") + " a " + method + " request via a call to " +
		       jp.getSignature().getDeclaringTypeName() + "." + jp.getSignature().getName() + "(" +
		       ((args.length() == 0) ? "" : args.substring(1, args.length() - 1)) + ")";
	}

	/* GET */
	@Before("execution(* com.company.rest.products.controller.ProductController.get*(..))")
	public void beforeGetRequests(JoinPoint jp)
	{
		log.info(msg("GET", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.get*(..))")
	public void afterGetRequests(JoinPoint jp)
	{
		log.info(msg("GET", LOC.END, jp));
	}

	/* POST */
	@Before("execution(* com.company.rest.products.controller.ProductController.post*(..))")
	public void beforePostRequests(JoinPoint jp)
	{
		log.info(msg("POST", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.post*(..))")
	public void afterPostRequests(JoinPoint jp)
	{
		log.info(msg("POST", LOC.END, jp));
	}

	/* PUT */
	@Before("execution(* com.company.rest.products.controller.ProductController.put*(..))")
	public void beforePutRequests(JoinPoint jp)
	{
		log.info(msg("PUT", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.put*(..))")
	public void afterPutRequests(JoinPoint jp)
	{
		log.info(msg("PUT", LOC.END, jp));
	}

	/* PATCH */
	@Before("execution(* com.company.rest.products.controller.ProductController.patch*(..))")
	public void beforePatchRequests(JoinPoint jp)
	{
		log.info(msg("PATCH", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.put*(..))")
	public void afterPatchRequests(JoinPoint jp)
	{
		log.info(msg("PATCH", LOC.END, jp));
	}

	/* DELETE */
	@Before("execution(* com.company.rest.products.controller.ProductController.delete*(..))")
	public void beforeDeleteRequests(JoinPoint jp)
	{
		log.info(msg("DELETE", LOC.BEGIN, jp));
	}

	@After("execution(* com.company.rest.products.controller.ProductController.delete*(..))")
	public void afterDeleteRequests(JoinPoint jp)
	{
		log.info(msg("DELETE", LOC.END, jp));
	}

}
