package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.ResponseServerView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SetBalanceIT extends BaseIT{
	private final static int X = 5;
	private final static int Y = 5;
	private final static int CAPACITY = 20;
	private final static int RETURN_PRIZE = 0;
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
	}

	@After
	public void tearDown() {
	}

	// main tests
	// assertEquals(expected, actual);

	/** Try to get a Bina , get one verify, one rented (less). */
	@Test
	public void setBalanceCreateNewClient() {
		client.setBalance("a.a@b.b", 20, 2);

		assertEquals(20, client.getBalance("a.a@b.b").getCredit());
		assertEquals(2, client.getBalance("a.a@b.b").getTag());
	}
	
	@Test
	public void setBalance() {
		client.setBalance("c.c@b.b", 20, 2);
		client.setBalance("c.c@b.b", 30, 3);
		assertEquals(30, client.getBalance("c.c@b.b").getCredit());
		assertEquals(3, client.getBalance("c.c@b.b").getTag());
	}
	
	
}
