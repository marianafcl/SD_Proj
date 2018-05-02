package org.binas.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;

import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.InsufficientCreditsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.StationNotFoundException;
import org.binas.domain.exception.UserAlreadyExistsException;
import org.binas.domain.exception.UserAlreadyHasBinaException;
import org.binas.domain.exception.UserHasNoBinaException;
import org.binas.domain.exception.UserNotFoundException;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.ResponseServerView;
import org.binas.station.ws.ReturnBinaResponse;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import org.binas.station.ws.NoBinaAvail;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/**
 * BinasManager class 
 * 
 * Class that have the methods used to get/Return Bina, beginning a station, querying all stations, etc.
 *
 */
public class BinasManager {
	/**
	 * UDDI server URL
	 */
	private String uddiURL = null;

	/**
	 * Station name
	 */
	private String stationTemplateName = null;

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

	// Binas Logic ----------------------------------------------------------

	public User createUser(String email) throws UserAlreadyExistsException, InvalidEmailException {
		return UsersManager.getInstance().RegisterNewUser(email);
	}

	public User getUser(String email) throws UserNotFoundException {
		return UsersManager.getInstance().getUser(email);
	}

	public void rentBina(String stationId, String email) throws UserNotFoundException, InsufficientCreditsException, 
	UserAlreadyHasBinaException, StationNotFoundException, NoBinaAvail_Exception {
		getBalance(email);
		User user = getUser(email);
		synchronized (user) {

			//validate user can rent
			user.validateCanRentBina();

			//validate station can rent
			StationClient stationCli = getStation(stationId);
			try {
				stationCli.getBinaAsync().get();
			} catch(Exception e) {
				throw new NoBinaAvail_Exception(e.getCause().getMessage(), new NoBinaAvail());
			}
			//apply rent action to user
			user.effectiveRent();
			setBalance(email, user.getCredit());
		}
	}

	public void returnBina(String stationId, String email) throws UserNotFoundException, NoSlotAvail_Exception, UserHasNoBinaException, StationNotFoundException {
		getBalance(email);
		User user = getUser(email);
		synchronized (user) {

			//validate user can rent
			user.validateCanReturnBina();

			//validate station can rent
			StationClient stationCli = getStation(stationId);
			int prize = 0;
			try {
				ReturnBinaResponse aux = stationCli.returnBinaAsync().get();
				prize = aux.getReturnBina();
			} catch (Exception e) {
				throw new NoSlotAvail_Exception(e.getCause().getMessage(), new NoSlotAvail());
			}

			//apply rent action to user
			user.effectiveReturn(prize);
			setBalance(email, user.getCredit());
		}		
	}

	public StationClient getStation(String stationId) throws StationNotFoundException {

		Collection<String> stations = this.getStations();
		String uddiUrl = BinasManager.getInstance().getUddiURL();

		for (String s : stations) {
			try {
				StationClient sc = new StationClient(uddiUrl, s);
				org.binas.station.ws.StationView sv = sc.getInfo();
				String idToCompare = sv.getId();
				if (idToCompare.equals(stationId)) {
					return sc;
				}
			} catch (StationClientException e) {
				continue;
			}
		}

		throw new StationNotFoundException();
	}

	public int[] getBalance(String email) {
		Collection<String> stations = getStations();
		User user = null;
		boolean fault = false;
		int stationsResponses = 0;
		try {
			user = getUser(email);
		}catch (UserNotFoundException e1) {
			ArrayList<int[]> infoClients = new ArrayList<>();
			for(String station : stations) {
				if(stationsResponses == 2) {
					break;
				}
				try {
					StationClient stationCli = getStation(station);
					Response<GetBalanceResponse> response = stationCli.getBalanceAsync(email);
					while (!response.isDone()) {
						Thread.sleep(100);
					}
					ResponseServerView responseServerView = response.get().getServerResponse();
					if(responseServerView.getTag() != -1 && responseServerView.getCredit() != -1) {
						int[] aux3 = {responseServerView.getCredit(), responseServerView.getTag()};
						infoClients.add(aux3);
					}
					stationsResponses++;

				} catch (StationNotFoundException | InterruptedException | ExecutionException e) {
					if(fault == true) {
						e.printStackTrace();
					}
					else {
						fault = true;
					}
				} 	
			}
			if(infoClients.isEmpty()) {
				return null;
			}
			int[] maxValue = { infoClients.get(0)[0], infoClients.get(0)[1]};
			for(int i = 1; i < infoClients.size(); i++) {
				if(infoClients.get(i)[1] > maxValue[1]) {
					maxValue = infoClients.get(i);

				}
			}
			try {
				user = BinasManager.getInstance().createUser(email);
				user.setBalance(maxValue[0]);
				user.setTag(maxValue[1]);
			} catch (UserAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidEmailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return maxValue;
		}
		int[] aux = {user.getCredit(), user.getTag()};
		return aux; 
	}

	public void setBalance(String email, int credit) throws UserNotFoundException {
		int[] clientInfo = getBalance(email);
		if(clientInfo == null) {
			throw new UserNotFoundException();
		}
		Collection<String> stations = getStations();
		User user = null;
		boolean fault = false;
		int stationsResponses = 0;
		int auxTag = clientInfo[1] + 1;
		for(String station : stations) {
			if(stationsResponses == 2) {
				break;
			}
			try {
				StationClient stationCli = getStation(station);
				Response<SetBalanceResponse> response = stationCli.setBalanceAsync(email, credit, auxTag);
				while (!response.isDone()) {
					Thread.sleep(100);
				}
				stationsResponses++;
			} catch (StationNotFoundException | InterruptedException e) {
				if(fault == true) {
					e.printStackTrace();
				}
				else {
					fault = true;
				}
			}
		}
		user = BinasManager.getInstance().getUser(email);
		user.setBalance(credit);
		user.setTag(auxTag);
	}


	// UDDI ------------------------------------------------------------------

	public void initUddiURL(String uddiURL) {
		setUddiURL(uddiURL);
	}

	public void initStationTemplateName(String stationTemplateName) {
		setStationTemplateName(stationTemplateName);
	}

	public String getUddiURL() {
		return uddiURL;
	}

	private void setUddiURL(String url) {
		uddiURL = url;
	}

	private void setStationTemplateName(String sn) {
		stationTemplateName = sn;
	}

	public String getStationTemplateName() {
		return stationTemplateName;
	}

	/**
	 * Get list of stations for a given query
	 * 
	 * @return List of stations
	 */
	public Collection<String> getStations() {
		Collection<UDDIRecord> records = null;
		Collection<String> stations = new ArrayList<String>();
		try {
			UDDINaming uddi = new UDDINaming(uddiURL);
			records = uddi.listRecords(stationTemplateName + "%");
			for (UDDIRecord u : records)
				stations.add(u.getOrgName());
		} catch (UDDINamingException e) {
		}
		return stations;
	}

	public void reset() {
		UsersManager.getInstance().reset();
	}

	public void init(int userInitialPoints) throws BadInitException {
		if(userInitialPoints < 0) {
			throw new BadInitException();
		}
		UsersManager.getInstance().init(userInitialPoints);
	}

	/**
	 * 
	 * Inits a Station with a determined ID, coordinates, capacity and returnPrize
	 * 
	 * @param stationId
	 * @param x
	 * @param y
	 * @param capacity
	 * @param returnPrize
	 * @throws BadInitException
	 * @throws StationNotFoundException
	 */
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInitException, StationNotFoundException {
		//validate station can rent
		StationClient stationCli;
		try {
			stationCli = getStation(stationId);
			stationCli.testInit(x, y, capacity, returnPrize);
		} catch (BadInit_Exception e) {
			throw new BadInitException(e.getMessage());
		}

	}
}
