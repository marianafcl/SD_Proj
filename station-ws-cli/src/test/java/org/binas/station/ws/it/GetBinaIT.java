package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetBinaIT extends BaseIT {
	
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps1;
	protected static StationClient client1;
	protected static StationClient client2;
	
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
		
		client2 = new StationClient(uddiURL, "A48_Station2");
	}
 
	@Test
	public void successFewBinasLeft() {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			
			for(int i = 0; i < 40; i++) {
				client.getBina();
			}
			
			assertEquals(10, client.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test
	public void successNoBinasLeft() {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			
			for(int i = 0; i < 50; i++) {
				client.getBina();
			}
			
			assertEquals(0, client.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failNoBinasLeft() throws NoBinaAvail_Exception {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			
			for(int i = 0; i < 50; i++) {
				client.getBina();
			}
			
			client.getBina();
		}
		catch(BadInit_Exception e) { fail(); }
	}
	
	@Test
	public void successTwoClientsOneStation() {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			assertEquals(50, client1.getInfo().getCapacity());
			
			for(int i = 0; i < 25; i++) {
				client.getBina();
				client1.getBina();
			}
			
			assertEquals(0, client.getInfo().getAvailableBinas());
			assertEquals(0, client1.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failTwoClientsOneStation() throws NoBinaAvail_Exception {
		try { 
			client.testInit(10, 20, 50, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			assertEquals(50, client1.getInfo().getCapacity());
			
			for(int i = 0; i < 50; i++) {
				client.getBina();
			}
			
			client1.getBina();
		}
		catch(BadInit_Exception e) { fail(); }
	}
	
	@Test
	public void successTwoStations() {
		try { 
			client.testInit(10, 20, 50, 1000);
			client2.testInit(10, 20, 100, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			assertEquals(100, client2.getInfo().getCapacity());
			
			for(int i = 0; i < 50; i++) {
				client.getBina();
				client2.getBina();
			}
			
			assertEquals(0, client.getInfo().getAvailableBinas());
			assertEquals(50, client2.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failTwoStations() throws NoBinaAvail_Exception {
		try { 
			client.testInit(10, 20, 50, 1000);
			client2.testInit(10, 20, 25, 1000);
			assertEquals(50, client.getInfo().getCapacity());
			assertEquals(25, client2.getInfo().getCapacity());
			
			for(int i = 0; i < 25; i++) {
				client.getBina();
				client2.getBina();
			}
			
			assertEquals(25, client.getInfo().getAvailableBinas());
		}
		catch(BadInit_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
		
		client2.getBina();
	}
	
	@After
	public void clean() {
		client.testClear();
		client1.testClear();
		client2.testClear();
	}
}