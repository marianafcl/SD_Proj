package org.binas.station.ws.cli;

/** 
 * Client application. 
 * 
 * Looks for Stations using UDDI and arguments provided in pom.xml
 */
public class StationClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments.
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationClientApp.class.getName() + " wsURL OR uddiURL wsName");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		if (args.length == 1) {
			wsURL = args[0];
		} else if (args.length >= 2) {
			uddiURL = args[0];
			wsName = args[1];
		}

		// Create client.
		StationClient client = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new StationClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new StationClient(uddiURL, wsName);
		}

		// The following remote invocation is just a basic example.
		// The actual tests are made using JUnit.

		System.out.println("Invoke ping()...");
		String result = client.testPing("client");
		System.out.print("Result: ");
		System.out.println(result);
		
		StationClient client2 = new StationClient(uddiURL, "A48_Station2");
		StationClient client3 = new StationClient(uddiURL, "A48_Station3");
		System.out.println("Press 1 to reset Station1, 2 for Station2 and 3 for Station3");
		System.out.println("Press S to shutdown");
		boolean flag = true;
		while(flag) {
			switch(System.in.read()) {
				case 49:
					client.testClear();
					break;
				case 50:
					client2.testClear();
					break;
				case 51:
					client3.testClear();
					break;
				case 83:
				case 115:
					flag = false;
					break;
			}
		}
	}

}