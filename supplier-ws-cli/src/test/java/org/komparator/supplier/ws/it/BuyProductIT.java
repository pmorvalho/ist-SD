package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		client.clear();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before each test
		client.clear();

		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
	}

	@After
	public void tearDown() {
		client.clear();
	}

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullIdTest() throws BadProductId_Exception, InsufficientQuantity_Exception, BadQuantity_Exception {
		client.buyProduct(null,1);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhitespaceIdTest() throws BadProductId_Exception, InsufficientQuantity_Exception, BadQuantity_Exception {
		client.buyProduct(" ",1);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyIdTest() throws BadProductId_Exception, InsufficientQuantity_Exception, BadQuantity_Exception {
		client.buyProduct("",1);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewLineIdTest() throws BadProductId_Exception, InsufficientQuantity_Exception, BadQuantity_Exception {
		client.buyProduct("\n",1);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabIdTest() throws BadProductId_Exception, InsufficientQuantity_Exception, BadQuantity_Exception {
		client.buyProduct("\t",1);
	}
	
	@Test
	public void buyProductNegativeQuantityTest() throws BadProductId_Exception, InsufficientQuantity_Exception {
		int quantity = client.getProduct("X1").getQuantity();
		try{
			client.buyProduct("X1",-1);
			fail();
		}
		catch(BadQuantity_Exception e){
			assertEquals(client.getProduct("X1").getQuantity(),quantity);
		}
	}
	
	@Test
	public void buyProductZeroQuantityTest() throws BadProductId_Exception, InsufficientQuantity_Exception {
		int quantity = client.getProduct("X1").getQuantity();
		try{
			client.buyProduct("X1",0);
			fail();
		}
		catch(BadQuantity_Exception e){
			assertEquals(client.getProduct("X1").getQuantity(),quantity);
		}
	}

	// main tests
	
	@Test
	public void buyProductInsufficientQuantityTest() throws BadProductId_Exception, BadQuantity_Exception  {
		int quantity = client.getProduct("X1").getQuantity();
		try{
			client.buyProduct("X1",quantity+1);
			fail();
		}
		catch(InsufficientQuantity_Exception e){
			assertEquals(client.getProduct("X1").getQuantity(),quantity);
		}
	}
	
	@Test
	public void buyProductSuccessTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception  {
		int quantity = client.getProduct("X1").getQuantity();
		client.buyProduct("X1",1);
		assertEquals(client.getProduct("X1").getQuantity(),quantity-1);

	}
	
	@Test
	public void buyProductAllTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception  {
		int quantity = client.getProduct("X1").getQuantity();
		client.buyProduct("X1",quantity);
		assertEquals(client.getProduct("X1").getQuantity(),0);

	}
	
}
