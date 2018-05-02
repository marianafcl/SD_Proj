package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.ResponseServerView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class GetBalanceIT extends BaseIT{
	private final static int X = 5;
	private final static int Y = 5;
	private final static int CAPACITY = 20;
	private final static int RETURN_PRIZE = 0;
	private final static String EMAIL = "a.a@b.b";
	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadInit_Exception {
		client.testClear();
		client.testInit(X, Y, CAPACITY, RETURN_PRIZE);
		client.setBalance(EMAIL, 20, 5);
	}

	@After
	public void tearDown() {
	}

	// main tests
	// assertEquals(expected, actual);

	/** Try to get a Bina , get one verify, one rented (less). */
	@Test
	public void getBalanceOneTest() {
		ResponseServerView infoClient = client.getBalance(EMAIL);

		assertEquals(20, infoClient.getCredit());
		assertEquals(5, infoClient.getTag());
	}
	
	@Test
	public void getBalanceEmailNull() {
		ResponseServerView infoClient = client.getBalance(null);
		assertEquals(-1, infoClient.getCredit());
		assertEquals(-1, infoClient.getTag());
	}
	
	/** Try to get a Bina but no Binas available. */
	@Test
	public void getBalanceClientNotExists() {
		ResponseServerView infoClient = client.getBalance("b.b@c.c");
		assertEquals(-1, infoClient.getCredit());
		assertEquals(-1, infoClient.getTag());
	}
}
