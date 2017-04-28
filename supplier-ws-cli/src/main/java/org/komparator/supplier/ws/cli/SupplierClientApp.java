package org.komparator.supplier.ws.cli;

/** Main class that starts the Supplier Web Service client. */
public class SupplierClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierClientApp.class.getName() + " wsURL");
			return;
		}
		else if (args.length > 2) {
			System.err.println("Too many arguments!");
			return;
		}
		
		SupplierClient client;
		if (args.length == 2) {
			String uddiURL = args[0];
			String wsName =  args[1];
		
			// Create client
			System.out.printf("Creating client for server %s%n", wsName);
			client = new SupplierClient(uddiURL, wsName);
		}
		else {
			String wsURL = args[0];
		
			// Create client
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new SupplierClient(wsURL);
		}

		// the following remote invocations are just basic examples
		// the actual tests are made using JUnit

		System.out.println("Invoke ping()...");
		String result = client.ping("client");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
