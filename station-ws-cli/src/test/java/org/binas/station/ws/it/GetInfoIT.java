package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;

public class GetInfoIT extends BaseIT {
		
	@Test
	public void successOneClient() {
		try {
			assertEquals(22, client.getInfo().getCoordinate().getX());
			assertEquals(7, client.getInfo().getCoordinate().getY());
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			for(int i = 0;  i < 4; i++) {
				client.returnBina();
			}
			
			assertEquals(4, client.getInfo().getAvailableBinas());
			assertEquals(2, client.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test
	public void successTwoClients() {
		try {
			assertEquals(22, client.getInfo().getCoordinate().getX());
			assertEquals(7, client.getInfo().getCoordinate().getY());
			assertEquals(6, client.getInfo().getCapacity());
			
			assertEquals(22, client1.getInfo().getCoordinate().getX());
			assertEquals(7, client1.getInfo().getCoordinate().getY());
			assertEquals(6, client1.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			for(int i = 0;  i < 4; i++) {
				client.returnBina();
			}
			
			assertEquals(4, client.getInfo().getAvailableBinas());
			assertEquals(2, client.getInfo().getFreeDocks());
			
			assertEquals(4, client1.getInfo().getAvailableBinas());
			assertEquals(2, client1.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test
	public void successThreeStations() {
		try {
			assertEquals(22, client1.getInfo().getCoordinate().getX());
			assertEquals(7, client1.getInfo().getCoordinate().getY());
			assertEquals(6, client1.getInfo().getCapacity());
			
			assertEquals(80, client2.getInfo().getCoordinate().getX());
			assertEquals(20, client2.getInfo().getCoordinate().getY());
			assertEquals(12, client2.getInfo().getCapacity());
			
			assertEquals(50, client3.getInfo().getCoordinate().getX());
			assertEquals(50, client3.getInfo().getCoordinate().getY());
			assertEquals(20, client3.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
				client3.getBina();
			}
			for(int i = 0;  i < 6; i++) {
				client1.returnBina();
				client2.returnBina();
				client3.returnBina();
			}
			
			assertEquals(6, client1.getInfo().getAvailableBinas());
			assertEquals(0, client1.getInfo().getFreeDocks());
			
			assertEquals(12, client2.getInfo().getAvailableBinas());
			assertEquals(0, client2.getInfo().getFreeDocks());
			
			assertEquals(20, client3.getInfo().getAvailableBinas());
			assertEquals(0, client3.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test
	public void success() {
		try {
			assertEquals(22, client1.getInfo().getCoordinate().getX());
			assertEquals(7, client1.getInfo().getCoordinate().getY());
			assertEquals(6, client1.getInfo().getCapacity());
			
			assertEquals(80, client2.getInfo().getCoordinate().getX());
			assertEquals(20, client2.getInfo().getCoordinate().getY());
			assertEquals(12, client2.getInfo().getCapacity());
			
			assertEquals(50, client3.getInfo().getCoordinate().getX());
			assertEquals(50, client3.getInfo().getCoordinate().getY());
			assertEquals(20, client3.getInfo().getCapacity());
			
			assertEquals(6, client1.getInfo().getAvailableBinas());
			assertEquals(0, client1.getInfo().getFreeDocks());
			
			assertEquals(12, client2.getInfo().getAvailableBinas());
			assertEquals(0, client2.getInfo().getFreeDocks());
			
			assertEquals(20, client3.getInfo().getAvailableBinas());
			assertEquals(0, client3.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
				client3.getBina();
			}
			for(int i = 0; i < 6; i++) {
				client2.getBina();
				client3.getBina();
			}
			for(int i = 0; i < 8; i++) {
				client3.getBina();
			}
			
			assertEquals(0, client1.getInfo().getAvailableBinas());
			assertEquals(6, client1.getInfo().getFreeDocks());
			
			assertEquals(0, client2.getInfo().getAvailableBinas());
			assertEquals(12, client2.getInfo().getFreeDocks());
			
			assertEquals(0, client3.getInfo().getAvailableBinas());
			assertEquals(20, client3.getInfo().getFreeDocks());
			
			for(int i = 0; i < 9; i++) {
				client2.returnBina();
				client3.returnBina();
			}
			client1.returnBina();
			
			assertEquals(1, client1.getInfo().getAvailableBinas());
			assertEquals(5, client1.getInfo().getFreeDocks());
			
			assertEquals(9, client2.getInfo().getAvailableBinas());
			assertEquals(3, client2.getInfo().getFreeDocks());
			
			assertEquals(9, client3.getInfo().getAvailableBinas());
			assertEquals(11, client3.getInfo().getFreeDocks());
			
			for(int i = 0; i < 9; i++) {
				client2.getBina();
				client3.getBina();
			}
			client1.getBina();
			
			assertEquals(0, client1.getInfo().getAvailableBinas());
			assertEquals(6, client1.getInfo().getFreeDocks());
			
			assertEquals(0, client2.getInfo().getAvailableBinas());
			assertEquals(12, client2.getInfo().getFreeDocks());
			
			assertEquals(0, client3.getInfo().getAvailableBinas());
			assertEquals(20, client3.getInfo().getFreeDocks());
			
			for(int i = 0;  i < 6; i++) {
				client1.returnBina();
				client2.returnBina();
				client3.returnBina();
			}
			for(int i = 0; i < 6; i++) {
				client2.returnBina();
				client3.returnBina();
			}
			for(int i = 0; i < 8; i++) {
				client3.returnBina();
			}
			
			assertEquals(6, client1.getInfo().getAvailableBinas());
			assertEquals(0, client1.getInfo().getFreeDocks());
			
			assertEquals(12, client2.getInfo().getAvailableBinas());
			assertEquals(0, client2.getInfo().getFreeDocks());
			
			assertEquals(20, client3.getInfo().getAvailableBinas());
			assertEquals(0, client3.getInfo().getFreeDocks());
			
			assertEquals(7, client1.getInfo().getTotalGets());
			assertEquals(7, client1.getInfo().getTotalReturns());
			
			assertEquals(21, client2.getInfo().getTotalGets());
			assertEquals(21, client2.getInfo().getTotalReturns());
			
			assertEquals(29, client3.getInfo().getTotalGets());
			assertEquals(29, client3.getInfo().getTotalReturns());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
}
