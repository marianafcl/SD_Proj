package org.binas.station.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.cli.StationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Base class for testing a station Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static StationClient client;
	protected static StationClient client1;
	protected static StationClient client2;
	protected static StationClient client3;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");
		
		final String wsName2 = "A48_Station2";
		final String wsURL2 = "http://localhost:8082/station-ws/endpoint";
		
		final String wsName3 = "A48_Station3";
		final String wsURL3 = "http://localhost:8083/station-ws/endpoint";

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new StationClient(uddiURL, wsName);
			client1 = new StationClient(uddiURL, wsName);
			client2 = new StationClient(uddiURL, wsName2);
			client3 = new StationClient(uddiURL, wsName3);
		} else {
			client = new StationClient(wsURL);
			client1 = new StationClient(wsURL);
			client2 = new StationClient(wsURL2);
			client3 = new StationClient(wsURL3);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
		client1.setVerbose("true".equalsIgnoreCase(verboseEnabled));
		client2.setVerbose("true".equalsIgnoreCase(verboseEnabled));
		client3.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(22, 7, 6, 2);
		client2.testInit(80, 20, 12, 1);
		client3.testInit(50, 50, 20, 0);
	}

	@After
	public void cleanup() {
		client.testClear();
		client1.testClear();
		client2.testClear();
		client3.testClear();
	}

}
