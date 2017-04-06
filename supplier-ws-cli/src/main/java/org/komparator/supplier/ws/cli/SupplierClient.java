package org.komparator.supplier.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.PurchaseView;
import org.komparator.supplier.ws.SupplierPortType;
import org.komparator.supplier.ws.SupplierService;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class SupplierClient implements SupplierPortType {

	/** WS service */
	SupplierService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	SupplierPortType port = null;

	/** WS end point address */
	private String wsURL = null; // default value is defined inside WSDL
	
	private String uddiURL = null;
	
	private String wsName = null;

	public String getWsURL() {
		return wsURL;
	}
	
	/** output option **/
	private boolean verbose = false;
	
	

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public SupplierClient(String wsURL) throws SupplierClientException {
		this.wsURL = wsURL;
		createStub();
	}
	/** constructor with provided UDDI url and WebService name in UDDI */
	public SupplierClient(String uddiURL, String wsName){
		if (uddiURL == null)
			throw new NullPointerException("UDDI Address cannot be null!");
		this.uddiURL = uddiURL;
		
		if (wsName == null)
			throw new NullPointerException("Web Service Name cannot be null!");
		this.wsName = wsName;
		
		UDDINaming uddiNaming;
		
		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		try{
			uddiNaming = new UDDINaming(uddiURL);
		}
		catch(UDDINamingException e){
			System.out.println("Could not find UDDI Server");
			return;
		}
		
		System.out.printf("Looking for '%s'%n", wsName);
		String endpointAddress;
		try{
			endpointAddress = uddiNaming.lookup(wsName);
		}
		catch(UDDINamingException e){
			System.out.printf("Could not find service %s%n", wsName);
			return;
		}
		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}
		
		createStub();
		
	}
	
	public void setWsName(String wsName){
		this.wsName=wsName;
	}
	
	public String getWsName(){
		return wsName;
	}

	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new SupplierService();
		port = service.getSupplierPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}

	// remote invocation methods ----------------------------------------------

	@Override
	public ProductView getProduct(String productId) throws BadProductId_Exception {
		return port.getProduct(productId);
	}

	@Override
	public List<ProductView> searchProducts(String descText) throws BadText_Exception {
		return port.searchProducts(descText);
	}

	@Override
	public String buyProduct(String productId, int quantity)
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		return port.buyProduct(productId, quantity);
	}

	@Override
	public String ping(String name) {
		return port.ping(name);
	}

	@Override
	public void clear() {
		port.clear();
	}

	@Override
	public void createProduct(ProductView productToCreate) throws BadProductId_Exception, BadProduct_Exception {
		port.createProduct(productToCreate);
	}

	@Override
	public List<ProductView> listProducts() {
		return port.listProducts();
	}

	@Override
	public List<PurchaseView> listPurchases() {
		return port.listPurchases();
	}

}
