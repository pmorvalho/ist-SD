package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;

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
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
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
	
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantityTest() throws BadQuantity_Exception, BadProductId_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1",-1);
	}
	
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantityTest() throws BadQuantity_Exception, BadProductId_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1",0);
	}

	// main tests
	
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductInsufficientQuantityTest() throws InsufficientQuantity_Exception, BadProductId_Exception, BadQuantity_Exception  {
		client.buyProduct("X1",11);
	}
	
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductOneTooManyTest() throws InsufficientQuantity_Exception, BadProductId_Exception, BadQuantity_Exception  {
		client.buyProduct("X1",10);	
		client.buyProduct("X1",1); //this is the call expected to fail, since the previous one is tested in buyProductAllTest
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductNonExistentTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception  {
		client.buyProduct("tik",1);
	}
	
	@Test
	public void buyProductSuccessTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception  {
		client.buyProduct("X1",1);
		assertEquals(client.getProduct("X1").getQuantity(),9);
	}
	
	@Test
	public void buyProductAllTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception  {
		client.buyProduct("X1",10);
		assertEquals(client.getProduct("X1").getQuantity(),0);
	}

}
