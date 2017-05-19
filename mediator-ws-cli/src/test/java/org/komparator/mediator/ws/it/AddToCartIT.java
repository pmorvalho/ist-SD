package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
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
	
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
		

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
			product.setPrice(15);
			product.setQuantity(8);
			supplierClients.get(1).createProduct(product);
			
			
		}
		{
			ProductView product = new ProductView();
			product.setId("X2");
			product.setDesc("Baseball");
			product.setPrice(15);
			product.setQuantity(15);
			supplierClients.get(1).createProduct(product);
			
		}
		{
			ProductView product = new ProductView();
			product.setId("X3");
			product.setDesc("Baseball");
			product.setPrice(10);
			product.setQuantity(5);
			supplierClients.get(2).createProduct(product);

		}
		
	}
	
//	 Success tests
	
    @Test
    public void success() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(0).getWsName());

    	mediatorClient.addToCart("Cart1", item, 8);
    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart1" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 8);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");	
    }
    
    @Test
    public void successAddTo3Carts() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(0).getWsName());
    	mediatorClient.addToCart("Cart1",item, 8);
    	
    	ItemIdView item2 = new ItemIdView();
		item2.setProductId("X2");
		item2.setSupplierId(supplierClients.get(1).getWsName());
    	mediatorClient.addToCart("Cart2", item2, 10);
    	
    	ItemIdView item3 = new ItemIdView();
		item3.setProductId("X3");
		item3.setSupplierId(supplierClients.get(2).getWsName());
		mediatorClient.addToCart("Cart3", item3, 4);
		
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(3,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart1" );
    	assertEquals(carts.get(1).getCartId(),"Cart2" );
    	assertEquals(carts.get(2).getCartId(),"Cart3" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 8);
    	assertEquals(carts.get(1).getItems().get(0).getQuantity(), 10);
    	assertEquals(carts.get(2).getItems().get(0).getQuantity(), 4);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");
    	assertEquals(carts.get(1).getItems().get(0).getItem().getItemId().getProductId(), "X2");
    	assertEquals(carts.get(2).getItems().get(0).getItem().getItemId().getProductId(), "X3"); 	
    }
    
    @Test
    public void successAddToCartItemsTwice() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 4);

    	mediatorClient.addToCart("Cart2", item, 10);
    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart2" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 14);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X2");	
    }
    
    @Test
    public void successAddToCartTwoItemsFromDiffSuppliers() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 10);

    	ItemIdView item2 = new ItemIdView();
		item2.setProductId("X3");
		item2.setSupplierId(supplierClients.get(2).getWsName());
    	mediatorClient.addToCart("Cart2", item2, 4);
    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart2" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 10);
    	assertEquals(carts.get(0).getItems().get(1).getQuantity(), 4);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X2");
    	assertEquals(carts.get(0).getItems().get(1).getItem().getItemId().getProductId(), "X3");
    }
   
//  same itemId from diff supplier
    @Test
    public void successAddToCartSameIdDiffSuppliers() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(1).getWsName());

    	ItemIdView item2 = new ItemIdView();
		item2.setProductId("X1");
		item2.setSupplierId(supplierClients.get(0).getWsName());
    	mediatorClient.addToCart("Cart2", item2, 4);
    	mediatorClient.addToCart("Cart2", item, 6);

    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart2" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 4);
    	assertEquals(carts.get(0).getItems().get(1).getQuantity(), 6);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");
    	assertEquals(carts.get(0).getItems().get(1).getItem().getItemId().getProductId(), "X1");
    }

//  same itemId to diff carts
    @Test
    public void successAddToDiffCartsSameItemId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(1).getWsName());

    	ItemIdView item2 = new ItemIdView();
		item2.setProductId("X1");
		item2.setSupplierId(supplierClients.get(1).getWsName());
    	mediatorClient.addToCart("Cart2", item, 3);
    	mediatorClient.addToCart("Cart3", item, 3);

    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(2,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart2" );
    	assertEquals(carts.get(1).getCartId(),"Cart3" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 3);
    	assertEquals(carts.get(1).getItems().get(0).getQuantity(), 3);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");
    	assertEquals(carts.get(1).getItems().get(0).getItem().getItemId().getProductId(), "X1");
    }
    
//   Input Tests
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNullCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart(null, item, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartEmptyCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("", item, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartBlankCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("   ", item, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNewLineCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("\n", item, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartTabCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("\t", item, 4);
    }
    
    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullItemId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", null, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void nonAlphaNumericCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("I_D", item, 4);
    }
    
    @Test
    public void alphaNumericCartId2() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("aB2", item, 4);
    }
    
    @Test(expected = InvalidCartId_Exception.class)
    public void alphaNumericSpacesCartId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("I D", item, 4);
    }
    
//    Exceptions tests
    
    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartNotEnoughItem() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 20);
    }
    
    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartNotEnoughItemAdd2Times() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 4);

    	mediatorClient.addToCart("Cart2", item, 12);
    }

    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartMaxQuantityAvailablePlusOne() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 15); 
//    	15 is the limit quantity of X2
    	mediatorClient.addToCart("Cart2", item, 1);
    }
    
    @Test
    public void addToCartMaxQuantityAvailable() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(0).getWsName());

    	mediatorClient.addToCart("Cart1", item, 10);
    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"Cart1" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 10);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");	
    }
    
    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartNegativeQuantity() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, -4);
    }
    
    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartZeroItems() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X2");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 0);
    }
    
    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartInvalidItemId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X4");
		item.setSupplierId(supplierClients.get(1).getWsName());
		
    	mediatorClient.addToCart("Cart2", item, 4);
    }
    
    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartInvalidSupplierId() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X3");
		item.setSupplierId("A68_Supplier7");
		
    	mediatorClient.addToCart("Cart2", item, 4);
    }

    // new test - primary Mediator will be shut down during addToCart but test should not fail
    @Test
    public void replacePrimary() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
    	ItemIdView item = new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(supplierClients.get(0).getWsName());

    	mediatorClient.addToCart("DiogoAlves", item, 8);
    	
    	List<CartView> carts = mediatorClient.listCarts();
    	assertEquals(1,carts.size());
    	assertEquals(carts.get(0).getCartId(),"DiogoAlves" );
    	assertEquals(carts.get(0).getItems().get(0).getQuantity(), 8);
    	assertEquals(carts.get(0).getItems().get(0).getItem().getItemId().getProductId(), "X1");	
    }
    
    @After
    public void deleteCarts(){
    	mediatorClient.clear();
    }
    
}
