package org.binas.ws.it;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;



public class ListStationsIT extends BaseIT{
	private static CoordinatesView coordinates = new CoordinatesView();
	
	@BeforeClass
	public static void setUpCoordinates() throws BadInit_Exception {
	    coordinates.setX(55);
	    coordinates.setY(55);
	}
	
	
	@Test
	public void succesThreeStations() {
		Assert.assertNotNull(client.listStations(3, coordinates));
			
	}
	
	@Test
	public void successOneStation() {
		List<StationView> stations = client.listStations(1, coordinates);
		try {
			Assert.assertEquals(client.getInfoStation(stationId3).getId(), stations.get(0).getId());
		}
		catch(InvalidStation_Exception e) { 
			fail();
		}
	}
	
}
