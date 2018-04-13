package org.binas.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;
import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.NoBinaRentedException;
import org.binas.domain.exception.NoCreditException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */

 @WebService(endpointInterface = "org.binas.ws.BinasPortType",
		 wsdlLocation = "binas.wsdl",
		 name ="BinasWebService",
		 portName = "BinasPort",
		 targetNamespace="http://ws.binas.org/",
		 serviceName = "BinasService"
 )
 
public class BinasPortImpl implements BinasPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private BinasEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	 //Main operations -------------------------------------------------------

	 /** Retrieve list of stations. */
	 @Override
	 public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		 try {
			 List<StationView> stations = new ArrayList<StationView>();
			 Collection<String> urls = endpointManager.listUDDI();
		 
			 for(String s : urls) {
				StationView sv = new StationView();
				StationClient sc = new StationClient(s);
	
				CoordinatesView cv = new CoordinatesView();
				cv.setX(sc.getInfo().getCoordinate().getX());
				cv.setY(sc.getInfo().getCoordinate().getY());
				
				sv.setAvailableBinas(sc.getInfo().getAvailableBinas());
				sv.setCapacity(sc.getInfo().getCapacity());
				sv.setCoordinate(cv);
				sv.setFreeDocks(sc.getInfo().getFreeDocks());
				sv.setId(sc.getInfo().getId());
				sv.setTotalGets(sc.getInfo().getTotalGets());
				sv.setTotalReturns(sc.getInfo().getTotalReturns());
				stations.add(sv);
			 }
		 
			 //stations = BinasManager.getInstance().listStations(stations, numberOfStations, coordinates);		 
		 
			 return stations;
		 }
		 catch(StationClientException e) {
			 System.out.printf("Caught exception when stopping: %s%n", e);
			 return null;
		 }
	 }	 
	 
	 /** Retrieve station. */
	 @Override
	 public StationView getInfoStation(String stationID) throws InvalidStation_Exception {
		StationClient sc = null;
		StationView sv = new StationView();
		
		String wsURL = endpointManager.lookUpUDDI(stationID);
		if (wsURL == null) {
			throwInvalidStationException("Error: Invalid station");
		}
		try {
			sc = new StationClient(wsURL);
		} catch (StationClientException e) {
			throwInvalidStationException("Error: Invalid station");
		}
		
		
		CoordinatesView cv = new CoordinatesView();
		cv.setX(sc.getInfo().getCoordinate().getX());
		cv.setY(sc.getInfo().getCoordinate().getY());
		
		sv.setAvailableBinas(sc.getInfo().getAvailableBinas());
		sv.setCapacity(sc.getInfo().getCapacity());
		sv.setCoordinate(cv);
		sv.setFreeDocks(sc.getInfo().getFreeDocks());
		sv.setId(sc.getInfo().getId());
		sv.setTotalGets(sc.getInfo().getTotalGets());
		sv.setTotalReturns(sc.getInfo().getTotalReturns());
			
		return sv;
	 }
	
	 /** Return a user's bike to a station. */
	 @Override
	 public void returnBina(String stationID, String email) throws FullStation_Exception, InvalidStation_Exception,
	 NoBinaRented_Exception, UserNotExists_Exception {
		 StationClient sc;
		 try {
			BinasManager.getInstance().returnBina(email);
			sc = new StationClient(endpointManager.lookUpUDDI(stationID));
			sc.returnBina();
		 }
		 catch(org.binas.station.ws.NoSlotAvail_Exception e) {
			throwNoSlotAvailException("Error: station is full");
		 }
		 catch(StationClientException e) {
			 throwInvalidStationException("Error: station id is invalid");
		 }
		 catch(NoBinaRentedException e) {
			 throwNoBinaRentedException("Error: no bina rented");
		 }
		 catch(UserNotExistsException e) {
			 throwUserNotExistsException("Error: user doesn't exist");
		 }
	 }
	
	 /** Take a user's bike from a station. */
	 @Override
	 public void rentBina(String stationID, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
	 NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		StationClient sc;
		String wsURL = endpointManager.lookUpUDDI(stationID);
		if (wsURL == null) {
			throwInvalidStationException("Error: station id is invalid");
		}
		try {
			BinasManager.getInstance().getBina(email);
			sc = new StationClient(wsURL);
			sc.getBina();
		}
		catch(AlreadyHasBinaException e) {
			throwAlreadyHasBinaException("Error: already has bina");
		}
		catch(StationClientException e) {
			 throwInvalidStationException("Error: station id is invalid");
		}
		catch(org.binas.station.ws.NoBinaAvail_Exception e) {
			 throwNoBinaAvailException("Error: no bina available");
		}
		catch(NoCreditException e) {
			 throwNoCreditException("Error: no credit");
		}
		catch(UserNotExistsException e) {
			 throwUserNotExistsException("Error: user doesn't exist");
		}
	 }
	 
	 /** Get a user's credit. */
	 @Override
	 public int getCredit(String email) throws UserNotExists_Exception {
		 try {
			 return BinasManager.getInstance().getCredit(email);
		 }
		 catch(UserNotExistsException e) {
			 throwUserNotExistsException("Error: user doesn't exist");
			 return 0;
		 }
	 }
	 
	 /** Activate a user. */
	 @Override
	 public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		 try {
			 return BinasManager.getInstance().activateUser(email);
		 }
		 catch(EmailExistsException e) {
			 throwEmailExistsException("Error: email already in use");
			 return null;
		 }
		 catch(InvalidEmailException e) {
			 throwInvalidEmailException("Error: invalid email address");
			 return null;
		 }
	 }

	 //Test Control operations -----------------------------------------------

	 /** Diagnostic operation to check if service is running. */
	 @Override
	 public String testPing(String inputMessage) {
		 try {
			 Collection<String> urls = endpointManager.listUDDI();
		 
			 StringBuilder builder = new StringBuilder();
		 
			 for(String s : urls) {
				 StationClient sc = new StationClient(s);
				 builder.append(sc.testPing(inputMessage));
			 }
		 
			 return builder.toString();
		 }
		 catch(StationClientException e) {
			 System.out.printf("Caught exception when stopping: %s%n", e);
			 return null;
		 }
	 }
	
	 /** Return all binas variables to default values. */
	 @Override
	 public void testClear() {
		 BinasManager.getInstance().reset();
		 try {
			 Collection<String> urls = endpointManager.listUDDI();
		 
			 for(String s : urls) {
				 StationClient sc = new StationClient(s);
				 sc.testClear();
			 }
		 
		 }
		 catch(StationClientException e) {
			 System.out.printf("Caught exception when stopping: %s%n", e);

		 }
	 }
	
	 /** Set station variables with specific values. */
	 @Override
	 public void testInitStation(String StationID, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		 try {
			 StationClient sc = new StationClient(endpointManager.lookUpUDDI(StationID));
			 sc.testInit(x, y, capacity, returnPrize);
		 }
		 catch(StationClientException e) {
			 throwBadInitException("Error: station with invalid id");
		 }
		 catch (org.binas.station.ws.BadInit_Exception e) {
			 throwBadInitException("Error: invalid arguments");
		 }
	 }
	 
	 /** Set binas variables with specific values. */
	 @Override
	 public void testInit(int userInitialPoints) throws BadInit_Exception {
		 try {
			 BinasManager.getInstance().init(userInitialPoints);
		 } catch(BadInitException e) {
			 throwBadInitException("Invalid initialization values!");
		 }
	 }

	 //Exception helpers -----------------------------------------------------

	 /** Helper to throw a new InvalidStation_Exception exception. */
	 private void throwInvalidStationException(final String message) throws InvalidStation_Exception {
		 InvalidStation faultInfo = new InvalidStation();
		 faultInfo.message = message;
		 throw new InvalidStation_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new BadInit exception. */
	 private void throwBadInitException(final String message) throws BadInit_Exception {
		 BadInit faultInfo = new BadInit();
		 faultInfo.message = message;
		 throw new BadInit_Exception(message, faultInfo);
	 }
	
	 /** Helper to throw a new NoSlotAvail exception. */
	 private void throwNoSlotAvailException(final String message) throws FullStation_Exception {
		 FullStation faultInfo = new FullStation();
		 faultInfo.message = message;
		 throw new FullStation_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new NoBinaAvail exception. */
	 private void throwNoBinaAvailException(final String message) throws NoBinaAvail_Exception {
		 NoBinaAvail faultInfo = new NoBinaAvail();
		 faultInfo.message = message;
		 throw new NoBinaAvail_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new NoBinaRented exception. */
	 private void throwNoBinaRentedException(final String message) throws NoBinaRented_Exception {
		 NoBinaRented faultInfo = new NoBinaRented();
		 faultInfo.message = message;
		 throw new NoBinaRented_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new AlreadyHasBina exception. */
	 private void throwAlreadyHasBinaException(final String message) throws AlreadyHasBina_Exception {
		 AlreadyHasBina faultInfo = new AlreadyHasBina();
		 faultInfo.message = message;
		 throw new AlreadyHasBina_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new UserNotExists exception. */
	 private void throwUserNotExistsException(final String message) throws UserNotExists_Exception {
		 UserNotExists faultInfo = new UserNotExists();
		 faultInfo.message = message;
		 throw new UserNotExists_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new NoCredit exception. */
	 private void throwNoCreditException(final String message) throws NoCredit_Exception {
		 NoCredit faultInfo = new NoCredit();
		 faultInfo.message = message;
		 throw new NoCredit_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new EmailExists exception. */
	 private void throwEmailExistsException(final String message) throws EmailExists_Exception {
		 EmailExists faultInfo = new EmailExists();
		 faultInfo.message = message;
		 throw new EmailExists_Exception(message, faultInfo);
	 }
	 
	 /** Helper to throw a new InvalidEmail exception. */
	 private void throwInvalidEmailException(final String message) throws InvalidEmail_Exception {
		 InvalidEmail faultInfo = new InvalidEmail();
		 faultInfo.message = message;
		 throw new InvalidEmail_Exception(message, faultInfo);
	 }
}
