package org.binas.ws;

import org.binas.domain.BinasManager;

public class BinasApp {

	public static void main(String[] args) throws Exception {		
			// Check arguments
			if (args.length < 2) {
				System.err.println("Argument(s) missing!");
				System.err.println("Usage: java " +  BinasApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
				return;
			}
			String wsName = args[0];
			String wsURL = args[1];
			String uddiURL;
			BinasEndpointManager endpoint;
			
			// handle UDDI arguments
			if (args.length == 3) {
				uddiURL = args[2];
				endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
			}
			else {
				endpoint = new BinasEndpointManager(wsName, wsURL);
			}
			

			System.out.println(BinasApp.class.getSimpleName() + " running");

			 try {
			endpoint.start();
			endpoint.awaitConnections();
			} finally {
			endpoint.stop();
			}
		
	}

}