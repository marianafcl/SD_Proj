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
