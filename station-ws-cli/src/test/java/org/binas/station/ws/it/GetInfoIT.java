package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.binas.station.ws.cli.StationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;

public class GetInfoIT extends BaseIT {
	
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps1;
	protected static StationClient client1;
 
 @Before
	public void oneSetup() throws Exception {
		testProps1 = new Properties();
		try {
			testProps1.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps1.getProperty("uddi.enabled");
		final String verboseEnabled = testProps1.getProperty("verbose.enabled");

		final String uddiURL = testProps1.getProperty("uddi.url");
		final String wsName = testProps1.getProperty("ws.name");
		final String wsURL = testProps1.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client1 = new StationClient(uddiURL, wsName);
		} else {
			client1 = new StationClient(wsURL);
		}
		client1.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}
 
	
	@Test
	public void success() {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			assertEquals(10, client.getInfo().getCoordinate().getX());
			assertEquals(20, client.getInfo().getCoordinate().getY());
			
			for(int i = 0; i < 40; i++) {
				client.getBina();
			}
			for(int i = 0;  i < 30; i++) {
				client.returnBina();
			}
			
			assertEquals(40, client.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test
	public void success1() {
		try { 
			client1.testInit(5, 7, 100, 1000);
			assertEquals(100, client1.getInfo().getCapacity());
			assertEquals(5, client1.getInfo().getCoordinate().getX());
			assertEquals( 7, client1.getInfo().getCoordinate().getY());
			
			for(int i = 0; i < 40; i++) {
				client.getBina();
			}
			for(int i = 0;  i < 30; i++) {
				client.returnBina();
			}
			
			assertEquals( 90, client.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@After
	public void clean() {
		client.testClear();
		client1.testClear();
	}
}
