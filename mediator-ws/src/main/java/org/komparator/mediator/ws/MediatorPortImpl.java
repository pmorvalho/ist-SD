package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jws.WebService;

import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.PurchaseView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)
public class MediatorPortImpl implements MediatorPortType{

	// end point manager
	private MediatorEndpointManager endpointManager;
	
	//shop list
	private ArrayList<ShoppingResultView> shoppingResults = new ArrayList<ShoppingResultView>();

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------
	
	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		
		Collection<UDDIRecord> suppliers=getSuppliers();
    	List<SupplierClient> supClientList = getSupplierClients(suppliers);
    	
    	List<ItemView> itemList = new ArrayList<ItemView>();
    	for(SupplierClient client : supClientList){
    		try{
    			itemList.add(createItemView(client.getProduct(productId), client));
    		}
    		catch(Exception e){
    			throwInvalidItemId("Item ID is incorrect, failed.");
    			return null;
    		}
    	}
    	
    	Collections.sort(itemList,new ItemPriceComparator() );
		return itemList;
	}
	
	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
    
	// Auxiliary operations --------------------------------------------------	
	
	
	public Collection<UDDIRecord> getSuppliers(){
		Collection<UDDIRecord> suppliers;
    	//É para fazer try catch ou throws? !!!!!!!!!!!!!!!!!!! TODO
    	try{
    		suppliers = endpointManager.getUddiNaming().listRecords("A68_Supplier%");
    	}
    	catch(UDDINamingException e){
    		System.out.println("Could not list suppliers");
    		return null;
    	}
    	return suppliers;
	} 
	
	public List<SupplierClient> getSupplierClients(Collection<UDDIRecord> suppliers){
    	List<SupplierClient> supClientList = new ArrayList<SupplierClient>();
    	for(UDDIRecord record : suppliers){
    		try{
    			SupplierClient client = new SupplierClient(record.getUrl());
    			client.setWsName(record.getOrgName());
    			supClientList.add(client);
    			
    		}
    		catch(SupplierClientException e){
    			System.out.println("");
    		}
    	}
    	return supClientList;
    	
	}
	
	
    public String ping(String arg0){
    	
    	Collection<UDDIRecord> suppliers=getSuppliers();
    	List<SupplierClient> supClientList = getSupplierClients(suppliers);
    	String result = "";
    	int i = 0;
    	for(SupplierClient client : supClientList){
    		result+=client.ping("Supplier Client " + i)+ "\n";
    		i++;
    	}
    	
    	return result;
    	
    }

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<CartView> listCarts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return shoppingResults;
	}

	
	// View helpers -----------------------------------------------------
	
	
	class ItemPriceComparator implements Comparator<ItemView> {
	    @Override
	    public int compare(ItemView a, ItemView b) {
	        return a.getPrice() < b.getPrice() ? -1 : a.getPrice() == b.getPrice() ? 0 : 1 ;
	    }
	}
	
	public ItemView createItemView(ProductView product, SupplierClient client ){
		ItemView item = new ItemView();
		
		ItemIdView id  = new ItemIdView();
		id.setProductId(product.getId());
		id.setSupplierId(client.getWsName());
		
		item.setDesc(product.getDesc());
		item.setItemId(id);
		item.setPrice(product.getPrice());
		return item;
		
	}
    
	public ShoppingResultView createShoppingResultView(String id, Result res, int price) {
		ShoppingResultView view = new ShoppingResultView();
		view.setId(id);
		view.setResult(res);
		view.setTotalPrice(price);
		return view;
	}

    
	// Exception helpers -----------------------------------------------------
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}
    // TODO

}
