package org.binas.ws.cli;

import java.util.List;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;

/**
 * Class that contains the main of the BinasClient
 * 
 * Looks for Binas using arguments that come from pom.xm
 *
 */
public class BinasClientApp {

    public static void main(String[] args) throws Exception {
    	String email = "alice@A48.binas.org";
    	String station1 = "A48_Station1";
    	String station2 = "A48_Station2";
    	String station3 = "A48_Station3";
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BinasClientApp.class.getName()
                    + " wsURL OR uddiURL wsName");
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

        // Create client
        BinasClient client = null;

        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new BinasClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new BinasClient(uddiURL, wsName);
        }

        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit
        
        System.out.println("Invoke ping()...");
        //String result = client.testPing("client");
        //System.out.print(result);
        
        client.activateUser(email);
        /*client.testInitStation(station1, 22, 7, 6, 1);
        client.testInitStation(station2, 22, 7, 6, 1);
        client.testInitStation(station3, 22, 7, 6, 1);
        
        while(true) {
        	int credit = client.getCredit(email);
        	System.out.println("	Getting credit...");
        	System.out.println("	User:" + email + "has" + credit + "credit");
        	
        	System.out.println("Unsafe to reset Bina!!!");
        	client.rentBina(station1, email);
        	System.out.println("	Renting bina...");
        	System.out.println("	Renting bina from " + station1 + "with email:" + email);
       	
        	client.returnBina(station1, email);
        	System.out.println("	Returning bina...");
        	System.out.println("	Returning bina to " + station1 + "with email:" + email);
        	
        	System.out.println("Safe to reset Bina!!!");
        	Thread.sleep(10000);
        }*/
        
	 }
}

