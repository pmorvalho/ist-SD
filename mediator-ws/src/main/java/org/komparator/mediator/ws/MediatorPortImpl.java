package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

// TODO annotate to bind with WSDL
// TODO implement port type interface
public class MediatorPortImpl {

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------
	
    // TODO
	
    
	// Auxiliary operations --------------------------------------------------	
	
	
    public String ping(){
    	Collection<String> suppliers;
    	//É para fazer try catch ou throws? !!!!!!!!!!!!!!!!!!! TODO
    	try{
    		suppliers = endpointManager.getUddiNaming().list("A68_Supplier%");
    	}
    	catch(UDDINamingException e){
    		System.out.println("Could not list suppliers");
    		return null;
    	}
    	
    	List<SupplierClient> supClientList = new ArrayList<SupplierClient>();
    	for(String url : suppliers){
    		try{
    			supClientList.add(new SupplierClient(url));
    		}
    		catch(SupplierClientException e){
    			System.out.println("");
    		}
    	}
    	
    	String result = "";
    	int i = 0;
    	for(SupplierClient client : supClientList){
    		result+=client.ping("Supplier Client " + i)+ "\n";
    	}
    	
    	return result;
    	
    }

	
	// View helpers -----------------------------------------------------
	
    // TODO

    
	// Exception helpers -----------------------------------------------------

    // TODO

}
