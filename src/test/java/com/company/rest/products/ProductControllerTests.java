package com.company.rest.products;

import com.company.rest.products.controller.ProductController;
import com.company.rest.products.model.BackendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductControllerTests
{

	@Autowired
	private ProductController controller;

	@MockBean
	private BackendService service;

	@Test
	public void testGetAll()
	{

	}


	@Test
	public void testGetOne()
	{

	}


	@Test
	public void testPost()
	{

	}


	@Test
	public void testPut()
	{

	}


	@Test
	public void testPatch()
	{

	}

}
