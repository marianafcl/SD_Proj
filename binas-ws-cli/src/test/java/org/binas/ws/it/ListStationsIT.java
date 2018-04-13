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
	private static final String stationId1 = "A48_Station1";
	private static final int x1 = 22;
	private static final int y1 = 7;
	private static final int capacity1 = 6;
	private static final int returnPrize1 = 2;
	private static final String stationId2 = "A48_Station2";
	private static final int x2 = 80;
	private static final int y2 = 20;
	private static final int capacity2 = 12;
	private static final int returnPrize2 = 1;
	private static final String stationId3 = "A48_Station3";
	private static final int x3 = 50;
	private static final int y3 = 50;
	private static final int capacity3 = 20;
	private static final int returnPrize3 = 0;
	private static CoordinatesView coordinates = new CoordinatesView();
	
	@BeforeClass
	public static void setUp() throws BadInit_Exception {
		client.testInitStation(stationId1, x1, y1, capacity1, returnPrize1);
		client.testInitStation(stationId2, x2, y2, capacity2, returnPrize2);
		client.testInitStation(stationId3, x3, y3, capacity3, returnPrize3);
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
