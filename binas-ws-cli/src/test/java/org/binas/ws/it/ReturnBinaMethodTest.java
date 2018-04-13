package org.binas.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReturnBinaMethodTest extends BaseIT {
	public class GetBinaMethodTest extends BaseIT {
		
		@Test
		public void success() {
			try {
				client.rentBina(stationId1, email);
				client.returnBina(stationId1, email);
				
				StationView stationView = client.getInfoStation(stationId1);
				assertEquals(0, stationView.getFreeDocks());
				assertEquals(1, stationView.getTotalGets());
				assertEquals(1, stationView.getTotalReturns());
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
			}catch(NoBinaRented_Exception e) {
				fail();
			}catch(FullStation_Exception e) {
				fail();
			}
		}
		
		@Test(expected = InvalidStation_Exception.class)
		public void invalidStationException() throws InvalidStation_Exception {
			try {
				client.returnBina("A48_Station9", email);
			}catch(UserNotExists_Exception e) {
				fail();
			}catch(NoBinaRented_Exception e) {
				fail();
			}catch(FullStation_Exception e) {
				fail();
			}
		}
		
		@Test(expected = UserNotExists_Exception.class)
		public void userNotExistsException() throws UserNotExists_Exception {
			try {
				client.returnBina(stationId1, "moboy.c@yoyo.yo");
			}catch(InvalidStation_Exception e) {
				fail();
			}catch(NoBinaRented_Exception e) {
				fail();
			}catch(FullStation_Exception e) {
				fail();
			}
		}
		
		@Test(expected = FullStation_Exception.class)
		public void fullStationException() throws FullStation_Exception {
			try {
				client.rentBina(stationId1, email);
				client.returnBina(stationId2, email);
			}catch(InvalidStation_Exception e) {
				fail();
			}catch(UserNotExists_Exception e) {
				fail();
			}catch(NoBinaRented_Exception e) {
				fail();
			}catch(NoCredit_Exception e) {
				fail();
			}catch(NoBinaAvail_Exception e) {
				fail();
			}catch(AlreadyHasBina_Exception e) {
				fail();
			}catch(FullStation_Exception e) {
				fail();
			}
		}
		
		@Test(expected = NoBinaRented_Exception.class)
		public void noBinaRentedException() throws NoBinaRented_Exception {
			try {
				client.returnBina(stationId1, email);
			}catch(InvalidStation_Exception e) {
				fail();
			}catch(UserNotExists_Exception e) {
				fail();
			}catch(NoBinaRented_Exception e) {
				fail();
			}catch(FullStation_Exception e) {
				fail();
			}
		}
		
	}

}
