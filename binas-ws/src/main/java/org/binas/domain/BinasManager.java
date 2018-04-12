package org.binas.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.NoBinaRentedException;
import org.binas.domain.exception.NoCreditException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.ws.BadInit;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.binas.ws.UserView;

public class BinasManager {
	
	private HashMap<String, User> users = new HashMap();
	private int userInitialPoints = 10;

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
	
	
	/**ActivateUser Method **/
	public UserView activateUser(String email) throws EmailExistsException, InvalidEmailException {
		//TODO: VERIFY EMAIL (USER?/?MANAGER) DENIS VOICU'S WORK
		User user = new User(email);
		users.put(email, user);
		
		UserView userView = new UserView();
		
		userView.setEmail(email);
		userView.setHasBina(user.getHasBina());
		userView.setCredit(user.getSaldo());
		
		return userView;
		
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


	
/** Retrieve List Stations **/
	
	public synchronized List<StationView> listStations(List<StationView> stations, int k, CoordinatesView coordinates) {
		List<StationView> closestStations = null;
		int maxDist = 0;
		for (StationView station : stations) {
			if(closestStations.size() > k) {
				if(maxDist > this.distance(station.getCoordinate(), coordinates)) {
					closestStations = this.remove(closestStations, coordinates);
					closestStations.add(station);
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

	public void returnBina(String email) throws NoBinaRentedException, UserNotExistsException {
		User user = users.get(email);
		if (user == null) {
			throw new UserNotExistsException();
		}
		if (user.getHasBina() == false) {
			throw new NoBinaRentedException();
		}
		user.setHasBina(false);
		users.put(email, user);
	}

	public void getBina(String email) throws AlreadyHasBinaException, NoCreditException, UserNotExistsException {
		User user = users.get(email);
		if (user == null) {
			throw new UserNotExistsException();
		}
		if (user.getHasBina() == true) {
			throw new AlreadyHasBinaException();
		}
		if(user.getSaldo() != 0 ) {
			throw new NoCreditException();
		}
		user.setHasBina(true);
		users.put(email, user);
	}

	public int getCredit(String email) throws UserNotExistsException {
		User user = users.get(email);
		if (user == null) {
			throw new UserNotExistsException();
		}
		return user.getSaldo();
	}

	public void reset() {
		users.clear();
		this.setUserInitialPoints(10);
	}

	public void init(int userInitialPoints) throws BadInitException {
		if(this.userInitialPoints < 0) {
			 throw new BadInitException();
		}
		this.setUserInitialPoints(userInitialPoints);
	}
	
	//---------Getters and Setters------------
	
	public int getUserInitialPoints() {
		return this.userInitialPoints;
	}
	 
	public void setUserInitialPoints(int userInitialPoints) {
		this.userInitialPoints = userInitialPoints;
	}
}
