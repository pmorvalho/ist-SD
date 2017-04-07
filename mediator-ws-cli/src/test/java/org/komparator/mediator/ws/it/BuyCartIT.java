package org.komparator.mediator.ws.it;


import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;


/**
 * Test suite
 */
public class BuyCartIT extends BaseIT {

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
		}
		{
			ProductView product = new ProductView();
			product.setId("Y1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(20);
			supplierClients.get(1).createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z1");
			product.setDesc("Football");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClients.get(2).createProduct(product);
		}
		
		
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("X1");
			id.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart1",id, 1);
		}
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("Y1");
			id.setSupplierId(supplierClients.get(1).getWsName());
			mediatorClient.addToCart("Cart1",id, 1);
		}
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("Z1");
			id.setSupplierId(supplierClients.get(2).getWsName());
			mediatorClient.addToCart("Cart1",id, 1);
		}
		
		
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("X1");
			id.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart2",id, 10);
		}
		
		
		
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("X1");
			id.setSupplierId(supplierClients.get(0).getWsName());
			mediatorClient.addToCart("Cart3",id, 10);
		}
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("Y1");
			id.setSupplierId(supplierClients.get(1).getWsName());
			mediatorClient.addToCart("Cart3",id, 20);
		}
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("Z1");
			id.setSupplierId(supplierClients.get(2).getWsName());
			mediatorClient.addToCart("Cart3",id, 30);
		}
		
		
	}
	
	
    @Test
    public void complete() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.COMPLETE,shoppingResult.getResult());
    	assertEquals(3,shoppingResult.getPurchasedItems().size());
    	assertEquals(0,shoppingResult.getDroppedItems().size());
    	assertEquals(60,shoppingResult.getTotalPrice());
    	assertEquals(2,mediatorClient.listCarts().size());
    }
    
    @Test
    public void partial()throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	ShoppingResultView setupShoppingResult = mediatorClient.buyCart("Cart2","4024007102923926");
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.PARTIAL,shoppingResult.getResult());
    	assertEquals(2,shoppingResult.getPurchasedItems().size());
    	assertEquals(1,shoppingResult.getDroppedItems().size());
    	assertEquals(40,shoppingResult.getTotalPrice());
    	assertEquals(1,mediatorClient.listCarts().size());
    }
    
    @Test
    public void empty()throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	ShoppingResultView setupShoppingResult = mediatorClient.buyCart("Cart3","4024007102923926");
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.EMPTY,shoppingResult.getResult());
    	assertEquals(0,shoppingResult.getPurchasedItems().size());
    	assertEquals(3,shoppingResult.getDroppedItems().size());
    	assertEquals(0,shoppingResult.getTotalPrice());
    	assertEquals(1,mediatorClient.listCarts().size());
    }
    
    @Test(expected=InvalidCartId_Exception.class)
    public void duplicate() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	ShoppingResultView SetupshoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    }
}