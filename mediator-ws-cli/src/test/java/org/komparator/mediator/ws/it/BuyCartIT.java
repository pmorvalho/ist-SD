package org.komparator.mediator.ws.it;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
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
	
	@Before
	public void SetUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
		
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
		// cart for special case where primary mediator is shut down
		{
			ItemIdView id = new ItemIdView();
			id.setProductId("Z1");
			id.setSupplierId(supplierClients.get(2).getWsName());
			mediatorClient.addToCart("killBuyCart",id, 1);
		}
		
		
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void emptyCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void newLineCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\n","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void tabCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\t","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void spacesCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("         ","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void nullCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(null,"4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void nonExistentCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart750","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void nonAlphanumericCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("t_i_k","4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void alphanumericSpacesCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("t i k","4024007102923926");
	}
	
	
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void emptyCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1","");
	}
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void newLineCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1","\n");
	}
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void tabCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1","\t");
	}
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void spacesCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1","       ");
	}
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void nullCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1",null);
	}
	
	@Test(expected=InvalidCreditCard_Exception.class)
	public void invalidLuhnCreditCard() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("Cart1","4929733993641562");
	}
	
    @Test
    public void complete() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.COMPLETE,shoppingResult.getResult());
    	assertEquals(3,shoppingResult.getPurchasedItems().size());
    	assertEquals(0,shoppingResult.getDroppedItems().size());
    	assertEquals(60,shoppingResult.getTotalPrice());
    	assertEquals(1,mediatorClient.shopHistory().size());
    }
    
    @Test
    public void completeMultiple() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart3","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.COMPLETE,shoppingResult.getResult());
    	assertEquals(3,shoppingResult.getPurchasedItems().size());
    	assertEquals(0,shoppingResult.getDroppedItems().size());
    	assertEquals(1300,shoppingResult.getTotalPrice());
    	assertEquals(1,mediatorClient.shopHistory().size());
    }
    
    @Test
    public void partial()throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	mediatorClient.buyCart("Cart2","4024007102923926");
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult2",shoppingResult.getId());
    	assertEquals(Result.PARTIAL,shoppingResult.getResult());
    	assertEquals(2,shoppingResult.getPurchasedItems().size());
    	assertEquals(1,shoppingResult.getDroppedItems().size());
    	assertEquals(40,shoppingResult.getTotalPrice());
    	assertEquals(2,mediatorClient.shopHistory().size());
    }
    
    @Test
    public void empty()throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	mediatorClient.buyCart("Cart3","4024007102923926");
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("Cart1","4024007102923926");
    	assertEquals("CartResult2",shoppingResult.getId());
    	assertEquals(Result.EMPTY,shoppingResult.getResult());
    	assertEquals(0,shoppingResult.getPurchasedItems().size());
    	assertEquals(3,shoppingResult.getDroppedItems().size());
    	assertEquals(0,shoppingResult.getTotalPrice());
    	assertEquals(2,mediatorClient.shopHistory().size());
    }
    
    @Test
    public void duplicate() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
    	ShoppingResultView shoppingResult_1 = mediatorClient.buyCart("Cart1","4024007102923926");
    	ShoppingResultView shoppingResult_2 = mediatorClient.buyCart("Cart1","4024007102923926");
    	
    	assertEquals("CartResult1",shoppingResult_1.getId());
    	assertEquals(Result.COMPLETE,shoppingResult_1.getResult());
    	assertEquals(3,shoppingResult_1.getPurchasedItems().size());
    	assertEquals(0,shoppingResult_1.getDroppedItems().size());
    	assertEquals(60,shoppingResult_1.getTotalPrice());
    	
    	assertEquals("CartResult2",shoppingResult_2.getId());
    	assertEquals(Result.COMPLETE,shoppingResult_2.getResult());
    	assertEquals(3,shoppingResult_2.getPurchasedItems().size());
    	assertEquals(0,shoppingResult_2.getDroppedItems().size());
    	assertEquals(60,shoppingResult_2.getTotalPrice());
    	
    	assertEquals(2,mediatorClient.shopHistory().size());
    	
    }
    
    // new test - primary Mediator will be shut down during buyCart but test should not fail
    @Test
    public void replacePrimary() throws InvalidItemId_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
    	ShoppingResultView shoppingResult = mediatorClient.buyCart("killBuyCart","4024007102923926");
    	assertEquals("CartResult1",shoppingResult.getId());
    	assertEquals(Result.COMPLETE,shoppingResult.getResult());
    	assertEquals(1,shoppingResult.getPurchasedItems().size());
    	assertEquals(0,shoppingResult.getDroppedItems().size());
    	assertEquals(30,shoppingResult.getTotalPrice());
    	assertEquals(1,mediatorClient.shopHistory().size());
    }
    
    
    @After
    public void deleteCarts(){
    	mediatorClient.clear();
    }
}