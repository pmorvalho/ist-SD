package org.komparator.supplier.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.komparator.security.KomparatorSecurityManager;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


/** End point manager */
public class SupplierEndpointManager {

	/** Web Service location to publish */
	private String wsURL = null;

	private String addrUDDI = null;

	private String wsName = null;
	
	private UDDINaming uddiNaming = null;
	
	/** Port implementation */
	private SupplierPortImpl portImpl = new SupplierPortImpl(this);

// TODO
//	/** Obtain Port implementation */
//	public SupplierPortType getPort() {
//		return portImpl;
//	}

	/** Web Service end point */
	private Endpoint endpoint = null;
	
	/** output option **/
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public SupplierEndpointManager(String wsURL) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		this.wsURL = wsURL;
	}

	/* end point management */

	public SupplierEndpointManager(String wsURL, String uddiURL, String wsName) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		this.wsURL = wsURL;
		
		if (uddiURL == null)
			throw new NullPointerException("UDDI Address cannot be null!");
		this.addrUDDI = uddiURL;
		
		if (wsName == null)
			throw new NullPointerException("Web Service Name cannot be null!");
		this.wsName = wsName;
		
		KomparatorSecurityManager.setWsName(wsName);
		
	}

	public void start() throws Exception {
		try {
			// publish end point
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
			
			if(wsName!=null && addrUDDI!=null){
				// publish to UDDI
				if(verbose){
					System.out.printf("Publishing '%s' to UDDI at %s%n", wsName, addrUDDI);
				}
				uddiNaming = new UDDINaming(addrUDDI);
				uddiNaming.rebind(wsName, wsURL);
			}
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		
		try {
			if (uddiNaming != null && wsName!=null) {
				// delete from UDDI
				uddiNaming.unbind(wsName);
				System.out.printf("Deleted '%s' from UDDI%n", wsName);
			}
		} catch (Exception e) {
			System.out.printf("Caught exception when deleting: %s%n", e);
		}
	}

}
