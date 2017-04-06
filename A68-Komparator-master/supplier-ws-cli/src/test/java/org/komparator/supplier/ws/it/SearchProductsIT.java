package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		client.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer Ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void searchProductNullDescTest() throws BadText_Exception {
		client.searchProducts(null);
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductWhitespaceDescTest() throws BadText_Exception {
		client.searchProducts(" ");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductEmptyDescTest() throws BadText_Exception {
		client.searchProducts("");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductNewLineDescTest() throws BadText_Exception {
		client.searchProducts("\n");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductTabDescTest() throws BadText_Exception {
		client.searchProducts("\t");
	}
	
	// main tests

	@Test
	public void searchProductLowerCaseResultsTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("ball");
		assertEquals(result.size(), 2);
	}

	@Test
	public void searchProductUpperCaseResultsTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("Ball");
		assertEquals(result.size(), 1);
	}
	
	@Test
	public void searchProductOneResultTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("Soccer");
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getId(),"Z3");
	}
	
	@Test
	public void searchProductNoResultsTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("tik");
		assertEquals(result.size(), 0);
	}

}
