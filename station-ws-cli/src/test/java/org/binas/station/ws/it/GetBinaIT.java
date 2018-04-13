package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.junit.Test;

public class GetBinaIT extends BaseIT {
 
	@Test
	public void successFewBinasLeft() {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 3; i++) {
				client.getBina();
			}
			
			assertEquals(3, client.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test
	public void successNoBinasLeft() {
		try {
			assertEquals(6, client.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client.getBina();
			}
			
			assertEquals(0, client.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failNoBinasLeft() throws NoBinaAvail_Exception {
		assertEquals(6, client.getInfo().getCapacity());
		
		for(int i = 0; i < 6; i++) {
			client.getBina();
		}
			
		client.getBina();
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
			
			assertEquals(0, client.getInfo().getAvailableBinas());
			assertEquals(0, client1.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failTwoClientsOneStation() throws NoBinaAvail_Exception {
		assertEquals(6, client.getInfo().getCapacity());
		assertEquals(6, client1.getInfo().getCapacity());
			
		for(int i = 0; i < 6; i++) {
			client.getBina();
		}
			
		client1.getBina();
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
			
			assertEquals(0, client1.getInfo().getAvailableBinas());
			assertEquals(6, client2.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
	@Test(expected = NoBinaAvail_Exception.class)
	public void failTwoStations() throws NoBinaAvail_Exception {
		try {
			assertEquals(6, client1.getInfo().getCapacity());
			assertEquals(12, client2.getInfo().getCapacity());
			
			for(int i = 0; i < 6; i++) {
				client1.getBina();
				client2.getBina();
			}
			
			assertEquals(6, client2.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
		
		client.getBina();
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
			
			assertEquals(0, client1.getInfo().getAvailableBinas());
			assertEquals(6, client2.getInfo().getAvailableBinas());
			assertEquals(14, client3.getInfo().getAvailableBinas());
		}
		catch(NoBinaAvail_Exception e) { fail(); }
	}
	
}