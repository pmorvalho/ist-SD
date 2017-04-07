package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class AddToCartIT extends BaseIT {
	
    // tests
    // assertEquals(expected, actual);

    // public String ping(String x)
	
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
		
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
			ItemIdView item = new ItemIdView();
			item.setProductId("X1");
			item.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart5",item, 8);
			
		}
		{
			ProductView product = new ProductView();
			product.setId("X2");
			product.setDesc("Baseball");
			product.setPrice(15);
			product.setQuantity(15);
			supplierClients.get(1).createProduct(product);
			ItemIdView item = new ItemIdView();
			item.setProductId("X2");
			item.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart4",item, 10);
			
		}
		{
			ProductView product = new ProductView();
			product.setId("X3");
			product.setDesc("Baseball");
			product.setPrice(10);
			product.setQuantity(5);
			supplierClients.get(2).createProduct(product);
			ItemIdView item = new ItemIdView();
			item.setProductId("X3");
			item.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart3",item, 4);
		}
	}
	
	
    @Test
    public void success() throws InvalidItemId_Exception {
    	List<CartView> items = mediatorClient.listCarts();
    	assertEquals(3,items.size());
    	assertEquals(items.get(0).getCartId(),"Cart1" );
    	assertEquals(items.get(1).getCartId(),"Cart2" );
    	assertEquals(items.get(2).getCartId(),"Cart3" );
    	assertEquals(items.get(0).getItems().get(0).getQuantity(), 8);
    	assertEquals(items.get(1).getItems().get(0).getQuantity(), 10);
    	assertEquals(items.get(2).getItems().get(0).getQuantity(), 4);
    	assertEquals(items.get(0).getItems().get(0).getItem().getItemId(), "X1");
    	assertEquals(items.get(0).getItems().get(0).getItem().getItemId(), "X2");
    	assertEquals(items.get(0).getItems().get(0).getItem().getItemId(), "X3"); 	
    }
    


}