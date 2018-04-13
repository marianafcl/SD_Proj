package org.binas.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetBinaMethodTest extends BaseIT {
	private final static String email = "joaquina.bernardina@ist.bah";
	private final static String email1 = "joaquina.bernardin@ist.bah";
	private final static String email2= "joaquina.bernardi@ist.bah";
	private final static String email3 = "joaquina.bernard@ist.bah";
	private final static String email4 = "joaquina.bernar@ist.bah";
	private final static String email5 = "joaquina.berna@ist.bah";
	private final static String email6 = "joaquina.bern@ist.bah";
	private static final String stationId1 = "A48_Station1";
	private static final int x1 = 22;
	private static final int y1 = 7;
	private static final int capacity1 = 6;
	private static final int returnPrize1 = 2;
	private UserView userView;
	
	@Before
	public void setUp() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception {
		client.testInitStation(stationId1, x1, y1, capacity1, returnPrize1);
		client.activateUser(email);
	}

	@Test
	public void success() {
		try {
			client.rentBina(stationId1, email);
			StationView stationView = client.getInfoStation(stationId1);
			assertEquals(1, stationView.getFreeDocks());
			assertEquals(1, stationView.getTotalGets());
		}catch(InvalidStation_Exception e) {
			fail();
		}catch(UserNotExists_Exception e) {
			fail();
		}catch(NoCredit_Exception e) {
			fail();
		}catch(NoBinaAvail_Exception e) {
			fail();
		}catch(AlreadyHasBina_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void invalidStationException() throws InvalidStation_Exception {
		try {
			StationView stationView = client.getInfoStation("A48_Station2");
			client.rentBina(stationId1, email);
		}catch(UserNotExists_Exception e) {
			fail();
		}catch(NoCredit_Exception e) {
			fail();
		}catch(NoBinaAvail_Exception e) {
			fail();
		}catch(AlreadyHasBina_Exception e) {
			fail();
		}
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExistsException() throws UserNotExists_Exception {
		try {
			StationView stationView = client.getInfoStation(stationId1);
			client.rentBina(stationId1, "moboy.c@yoyo.yo");
		}catch(InvalidStation_Exception e) {
			fail();
		}catch(NoCredit_Exception e) {
			fail();
		}catch(NoBinaAvail_Exception e) {
			fail();
		}catch(AlreadyHasBina_Exception e) {
			fail();
		}
	}
	
	@Test(expected = NoCredit_Exception.class)
	public void noCreditException() throws NoCredit_Exception {
		try {
			StationView stationView = client.getInfoStation(stationId1);
			client.rentBina(stationId1, email);
		}catch(InvalidStation_Exception e) {
			fail();
		}catch(UserNotExists_Exception e) {
			fail();
		}catch(NoBinaAvail_Exception e) {
			fail();
		}catch(AlreadyHasBina_Exception e) {
			fail();
		}
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void noBinaAvailException() throws NoBinaAvail_Exception {
		try {
			StationView stationView = client.getInfoStation(stationId1);
			client.activateUser(email1);
			client.activateUser(email2);
			client.activateUser(email3);
			client.activateUser(email4);
			client.activateUser(email5);
			client.activateUser(email6);
			client.rentBina(stationId1, email);
			client.rentBina(stationId1, email1);
			client.rentBina(stationId1, email2);
			client.rentBina(stationId1, email3);
			client.rentBina(stationId1, email4);
			client.rentBina(stationId1, email5);
			client.rentBina(stationId1, email6);	
		}catch(InvalidStation_Exception e) {
			fail();
		}catch(UserNotExists_Exception e) {
			fail();
		}catch(NoCredit_Exception e) {
			fail();
		}catch(AlreadyHasBina_Exception e) {
			fail();
		}catch(InvalidEmail_Exception e) {
			fail();
		}catch(EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = AlreadyHasBina_Exception.class)
	public void alreadyHasBinaException() throws AlreadyHasBina_Exception {
		try {
			StationView stationView = client.getInfoStation(stationId1);
			client.rentBina(stationId1, email);
			client.rentBina(stationId1, email);
		}catch(InvalidStation_Exception e) {
			fail();
		}catch(UserNotExists_Exception e) {
			fail();
		}catch(NoCredit_Exception e) {
			fail();
		}catch(NoBinaAvail_Exception e) {
			fail();
		}
	}
	
	@After
	public void clean() {
		client.testClear();
	}
}
