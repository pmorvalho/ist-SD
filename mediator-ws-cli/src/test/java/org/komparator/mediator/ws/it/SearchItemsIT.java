package org.komparator.mediator.ws.it;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;


/**
 * Test suite
 */
public class SearchItemsIT extends BaseIT {

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
			product.setId("Y1");
			product.setDesc("Baseball");
			product.setPrice(10);
			product.setQuantity(20);
			supplierClients.get(1).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z1");
			product.setDesc("Baseball");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients.get(2).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("W1");
			product.setDesc("Frisbee");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients.get(2).createProduct(product);
		}
	}
	
	// TODO input tests
	
    @Test
    public void success() throws InvalidText_Exception {
    	List<ItemView> items = mediatorClient.searchItems("ball");
    	assertEquals(3,items.size());
    	assertEquals("X1", items.get(0).getItemId().getProductId());
    	assertEquals("Y1", items.get(1).getItemId().getProductId());
    	assertEquals("Z1", items.get(2).getItemId().getProductId());
    }
    
    @Test
    public void sameIdDiffPrice() throws InvalidText_Exception, BadProductId_Exception, BadProduct_Exception {
    	ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Football");
		product.setPrice(5);
		product.setQuantity(10);
		supplierClients.get(1).createProduct(product);
    	
    	List<ItemView> items = mediatorClient.searchItems("ball");
    	assertEquals(4,items.size());
    	assertEquals("X1", items.get(0).getItemId().getProductId());
    	assertEquals("A68_Supplier2", items.get(0).getItemId().getSupplierId());
    	assertEquals("X1", items.get(1).getItemId().getProductId());
    	assertEquals("A68_Supplier1", items.get(1).getItemId().getSupplierId());
    	assertEquals("Y1", items.get(2).getItemId().getProductId());
    	assertEquals("Z1", items.get(3).getItemId().getProductId());
    }
    
    @Test
    public void noProductsMatch() throws InvalidText_Exception {
    	List<ItemView> items = mediatorClient.searchItems("sdfg");
    	assertEquals(0,items.size());
    }
}