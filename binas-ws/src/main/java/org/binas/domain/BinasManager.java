package org.binas.domain;

import java.util.Collections;
import java.util.List;

import org.binas.ws.BadInit;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;

public class BinasManager {
	
	

	// Singleton -------------------------------------------------------------

	private BinasManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	/**InitTest with userInitialPoints **/
	public synchronized void TestInit(int userInitialPoints) throws BadInit_Exception{
		if(userInitialPoints < 0) {
			//TODO throw new BadInit_Exception("", null); Corrigir
		}
	}

	/** Retrieve List Stations **/
	
	public synchronized List<StationView> ListStations(List<StationView> stations, CoordinatesView coordinates, int k) {
		List<StationView> closestStations = null;
		int maxDist = 0;
		for (StationView station : stations) {
			if(closestStations.size() > k) {
				if(maxDist > this.distance(station.getCoordinate(), coordinates)) {
					closestStations = this.remove(closestStations, coordinates);
					maxDist = this.max(closestStations, coordinates);
				}
			}
			else {
				closestStations.add(station);
				if (maxDist < this.distance(station.getCoordinate(), coordinates)) {
					maxDist = this.distance(station.getCoordinate(), coordinates);
				}
			}
		}
		return closestStations;
	}
	
	
	/**Calculates Distance **/
	public int distance (CoordinatesView coordinate, CoordinatesView coordinateUser) {
		return (int) Math.sqrt((coordinate.getX()-coordinateUser.getX())^2 + (coordinate.getY() - coordinateUser.getY())^2);
	}
	
	/**Bigger distance **/
	public List<StationView> remove(List<StationView> stations, CoordinatesView coordinates) {
		int max = 0;
		StationView stationAux = stations.get(0);
		for (StationView station : stations) {
			if(max < this.distance(station.getCoordinate(), coordinates)) {
				max = this.distance(station.getCoordinate(), coordinates);
				stationAux = station;
			}
		}
		stations.remove(stationAux);
		return stations;
	}
	
	/** MaxDistance **/
	public int max(List<StationView> stations, CoordinatesView coordinates) {
		int max = 0;
		for (StationView station : stations) {
			if(max < this.distance(station.getCoordinate(), coordinates)) {
				max = this.distance(station.getCoordinate(), coordinates);
				
			}
		}
		return max;
	}
	
	//---------Getters and Setters------------
	
	

}
