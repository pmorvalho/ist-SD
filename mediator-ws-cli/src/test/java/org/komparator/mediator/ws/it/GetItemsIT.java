package org.komparator.mediator.ws.it;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;


/**
 * Test suite
 */
public class GetItemsIT extends BaseIT {

    // tests
    // assertEquals(expected, actual);

    // public String ping(String x)
	
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		
		// clear remote service state before all tests

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(10);
			supplierClients.get(0).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Baseball");
			product.setPrice(10);
			product.setQuantity(20);
			supplierClients.get(1).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Baseball");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients.get(2).createProduct(product);
		}
		
		{
			ProductView product = new ProductView();
			product.setId("Y1");
			product.setDesc("Basketball");
			product.setPrice(20);
			product.setQuantity(10);
			supplierClients.get(0).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z1");
			product.setDesc("Tennisball");
			product.setPrice(10);
			product.setQuantity(20);
			supplierClients.get(1).createProduct(product);
		}
	}
	
	// Bad input tests

	@Test(expected = InvalidItemId_Exception.class)
	public void searchProductNullDescTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void searchProductWhitespaceDescTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void searchProductEmptyDescTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void searchProductNewLineDescTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void searchProductTabDescTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}
	

	// Main tests
	
    @Test
    public void success() throws InvalidItemId_Exception {
    	List<ItemView> items = mediatorClient.getItems("X1");
    	assertEquals(3,items.size());
    	assertEquals(items.get(0).getItemId().getSupplierId(),"A68_Supplier2" );
    	assertEquals(items.get(1).getItemId().getSupplierId(),"A68_Supplier1" );
    	assertEquals(items.get(2).getItemId().getSupplierId(),"A68_Supplier3" );
    }
    
    @Test
    public void oneItemSuccess() throws InvalidItemId_Exception {
    	List<ItemView> items = mediatorClient.getItems("Y1");
    	assertEquals(1,items.size());
    	assertEquals(items.get(0).getItemId().getSupplierId(),"A68_Supplier1" );
    }
    
    @Test
    public void anotherOneItemSuccess() throws InvalidItemId_Exception {
    	List<ItemView> items = mediatorClient.getItems("Z1");
    	assertEquals(1,items.size());
    	assertEquals(items.get(0).getItemId().getSupplierId(),"A68_Supplier2" );
    }

    @Test
    public void noItemsExist() throws InvalidItemId_Exception {
    	List<ItemView> items = mediatorClient.getItems("ABBA");
    	assertEquals(0,items.size());
    }
    
    @Test
    public void lowercaseNotExists() throws InvalidItemId_Exception {
    	// product identifiers are case sensitive,
    	// so "x1" is not the same as "X1"
    	List<ItemView> items = mediatorClient.getItems("x1");
    	assertEquals(0,items.size());
    }

}