package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {
	@Test
	public void successFewSlotsLeft() {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			
			assertEquals(6, client.getInfo().getFreeDocks());
			
			for(int i = 0; i < 3; i++) {
				client.returnBina();
			}
			
			assertEquals(3, client.getInfo().getFreeDocks());
		}
		catch(NoSlotAvail_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test
	public void successNoSlotsLeft() {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			
			assertEquals(6, client.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client.returnBina();
			}
			
			assertEquals(0, client.getInfo().getFreeDocks());
		}
		catch(NoSlotAvail_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void failNoSlotsLeft() throws NoSlotAvail_Exception {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			
			assertEquals(6, client.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client.returnBina();
			}
			
			assertEquals(0, client.getInfo().getFreeDocks());
		}
		catch(NoSlotAvail_Exception e) { fail(); }
		catch(NoBinaAvail_Exception e) { fail(); }
		
		client.returnBina();
	}
	
	@Test
	public void successTwoClientsOneStation() {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			assertEquals(6, client1.getInfo().getCapacity());
			
			for(int i = 0; i < 3; i++) {
				client.getBina();
				client1.getBina();
			}
			
			assertEquals(6, client.getInfo().getFreeDocks());
			assertEquals(6, client1.getInfo().getFreeDocks());
			
			for(int i = 0; i < 3; i++) {
				client.returnBina();
				client1.returnBina();
			}
			
			assertEquals(0, client.getInfo().getFreeDocks());
			assertEquals(0, client1.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void failTwoClientsOneStation() throws NoSlotAvail_Exception {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			assertEquals(6, client1.getInfo().getCapacity());
			
			for(int i = 0; i < 3; i++) {
				client.getBina();
				client1.getBina();
			}
			
			assertEquals(6, client.getInfo().getFreeDocks());
			assertEquals(6, client1.getInfo().getFreeDocks());
			
			for(int i = 0; i < 3; i++) {
				client.returnBina();
				client1.returnBina();
			}
			
			assertEquals(0, client.getInfo().getFreeDocks());
			assertEquals(0, client1.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
		
		client1.returnBina();
	}
	
	@Test
	public void successTwoStations() {
		try {
			assertEquals(6, client1.getInfo().getCapacity());
			assertEquals(12, client2.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
			}
			
			assertEquals(6, client1.getInfo().getFreeDocks());
			assertEquals(6, client2.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client1.returnBina();
				client2.returnBina();
			}
			
			assertEquals(0, client1.getInfo().getFreeDocks());
			assertEquals(0, client2.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoSlotAvail_Exception.class)
	public void failTwoStations() throws NoSlotAvail_Exception {
		try {
			assertEquals(6, client1.getInfo().getCapacity());
			assertEquals(12, client2.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
			}
			
			assertEquals(6, client1.getInfo().getFreeDocks());
			assertEquals(6, client2.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client1.returnBina();
				client2.returnBina();
			}
			
			assertEquals(0, client1.getInfo().getFreeDocks());
			assertEquals(0, client2.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
		
		client.returnBina();
	}
	
	@Test
	public void successThreeStations() {
		try {
			assertEquals(6, client1.getInfo().getCapacity());
			assertEquals(12, client2.getInfo().getCapacity());
			assertEquals(20, client3.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
				client3.getBina();
			}
			
			assertEquals(6, client1.getInfo().getFreeDocks());
			assertEquals(6, client2.getInfo().getFreeDocks());
			assertEquals(6, client3.getInfo().getFreeDocks());
			
			for(int i = 0; i < 6; i++) {
				client1.returnBina();
				client2.returnBina();
				client3.returnBina();
			}
			
			assertEquals(0, client1.getInfo().getFreeDocks());
			assertEquals(0, client2.getInfo().getFreeDocks());
			assertEquals(0, client3.getInfo().getFreeDocks());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		catch(NoSlotAvail_Exception e) { fail(); }
	}
}