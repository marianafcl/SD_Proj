package org.binas.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.cli.BinasClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


/*
 * Base class of tests
 * Loads the properties in the file
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static BinasClient client;
	
	//----Tests Vars
	protected static final String stationId1 = "A48_Station1";
	protected static final int x1 = 22;
	protected static final int y1 = 7;
	protected static final int capacity1 = 6;
	protected static final int returnPrize1 = 2;
	protected static final String stationId2 = "A48_Station2";
	protected static final int x2 = 80;
	protected static final int y2 = 20;
	protected static final int capacity2 = 12;
	protected static final int returnPrize2 = 1;
	protected static final String stationId3 = "A48_Station3";
	protected static final int x3 = 50;
	protected static final int y3 = 50;
	protected static final int capacity3 = 20;
	protected static final int returnPrize3 = 0;
	protected final static String email = "joaquina.bernardina@ist.bah";

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

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new BinasClient(uddiURL, wsName);
		} else {
			client = new BinasClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));

	}
	@Before
	public void setUp() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception {
		client.testInitStation(stationId1, x1, y1, capacity1, returnPrize1);
		client.testInitStation(stationId2, x2, y2, capacity2, returnPrize2);
		client.testInitStation(stationId3, x3, y3, capacity3, returnPrize3);
		client.activateUser(email);
	}

	@After
	public void clean() {
		client.testClear();
	}

}
