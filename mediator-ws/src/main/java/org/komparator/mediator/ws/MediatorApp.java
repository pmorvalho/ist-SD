package org.komparator.mediator.ws;

import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

public class MediatorApp {

	private static final String NUMBER_PROPERTIES_FILE = "/number.properties";

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		
		Properties props = new Properties();
		try{
			props.load(MediatorApp.class.getResourceAsStream(NUMBER_PROPERTIES_FILE));
			System.out.println("Loaded properties:");
			System.out.println(props);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", NUMBER_PROPERTIES_FILE);
			System.out.println(msg);
			throw e;
		}

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			String number = props.getProperty("ws.i");
			System.out.println("number is "+number);
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			if(number.equals("1")){ //is primary
				System.out.println("This is the primary mediator");
				endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL,true);
			}
			else{ //is secondary
				System.out.println("This is a secondary mediator");
				endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL,false);
			}
			endpoint.setVerbose(true);
		}
		Timer timer = new Timer(true);
		LifeProof lifeProof = new LifeProof(endpoint);
		try {
			timer.schedule(lifeProof, 1000,5000);
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
			lifeProof.cancel();
			timer.cancel();
		}

	}

}
