package org.binas.ws.it;

import static org.junit.Assert.fail;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GentInfoStationIT extends BaseIT{
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
	
	@BeforeClass
	public static void setUp() throws BadInit_Exception {
		client.testInitStation(stationId1, x1, y1, capacity1, returnPrize1);
		client.testInitStation(stationId2, x2, y2, capacity2, returnPrize2);
		client.testInitStation(stationId3, x3, y3, capacity3, returnPrize3);
	}
	
	@Test
	public void getInfoStationITSuccessStation1() {
		try {
			StationView stationView = client.getInfoStation(stationId1);
			Assert.assertNotNull(stationView);
			Assert.assertEquals(x1, (int) stationView.getCoordinate().getX());
			Assert.assertEquals(y1, (int) stationView.getCoordinate().getY());
			Assert.assertEquals(stationId1, stationView.getId());
			Assert.assertEquals(capacity1, stationView.getCapacity());
		} catch (InvalidStation_Exception e) {
			fail();
		}
	}
	
	@Test
	public void getInfoStationITSuccessStation2() {
		try {
			StationView stationView = client.getInfoStation(stationId2);
			Assert.assertNotNull(stationView);
			Assert.assertEquals(x2, (int) stationView.getCoordinate().getX());
			Assert.assertEquals(y2, (int) stationView.getCoordinate().getY());
			Assert.assertEquals(stationId2, stationView.getId());
			Assert.assertEquals(capacity2, stationView.getCapacity());
		} catch (InvalidStation_Exception e) {
			fail();
		}
	}
	
	@Test
	public void getInfoStationITSuccessStation3() {
		try {
			StationView stationView = client.getInfoStation(stationId3);
			Assert.assertNotNull(stationView);
			Assert.assertEquals(x3, (int) stationView.getCoordinate().getX());
			Assert.assertEquals(y3, (int) stationView.getCoordinate().getY());
			Assert.assertEquals(stationId3, stationView.getId());
			Assert.assertEquals(capacity3, stationView.getCapacity());
		} catch (InvalidStation_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidStation_Exception.class)
	public void getInfoStationNoStationID() throws InvalidStation_Exception {
		client.getInfoStation("NonExistentName");
	}
	
	@After
	public void clean() {
		client.testClear();
	}
	
}
