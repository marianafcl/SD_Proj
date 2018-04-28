package org.binas.station.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.GetBinaResponse;
import org.binas.station.ws.GetInfoResponse;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.ResponseServerView;
import org.binas.station.ws.ReturnBinaResponse;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;
import org.binas.station.ws.StationView;
import org.binas.station.ws.TestClearResponse;
import org.binas.station.ws.TestInitResponse;
import org.binas.station.ws.TestPingResponse;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class StationClient implements StationPortType {

	/** WS service */
	StationService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	StationPortType port = null;

	/** UDDI server URL */
	private String uddiURL = null;

	/** WS name */
	private String wsName = null;

	/** WS end point address */
	private String wsURL = null; // default value is defined inside WSDL

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public StationClient(String wsURL) throws StationClientException {
		this.wsURL = wsURL;
		createStub();
	}

	/** constructor with provided UDDI location and name */
	public StationClient(String uddiURL, String wsName) throws StationClientException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		uddiLookup();
		createStub();
	}

	/** UDDI lookup */
	private void uddiLookup() throws StationClientException {
		try {
			if (verbose)
				System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);

			if (verbose)
				System.out.printf("Looking for '%s'%n", wsName);
			wsURL = uddiNaming.lookup(wsName);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", uddiURL);
			throw new StationClientException(msg, e);
		}

		if (wsURL == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", wsName, uddiURL);
			throw new StationClientException(msg);
		}
	}

	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new StationService();
		port = service.getStationPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}

	// remote invocation methods ----------------------------------------------

	@Override
	public StationView getInfo() {
		return port.getInfo();
	}

	@Override
	public void getBina() throws NoBinaAvail_Exception {
		port.getBina();
	}

	@Override
	public int returnBina() throws NoSlotAvail_Exception {
		return port.returnBina();
	}
	
	@Override
	public ResponseServerView getBalance(String email) {
		return port.getBalance(email);
	}

	@Override
	public void setBalance(String email, int credit, int tag) {
		port.setBalance(email, credit, tag);
		
	}

	// test control operations ------------------------------------------------

	@Override
	public String testPing(String inputMessage) {
		return port.testPing(inputMessage);
	}

	@Override
	public void testClear() {
		port.testClear();
	}

	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		port.testInit(x, y, capacity, returnPrize);
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	@Override
	public Response<GetBalanceResponse> getBalanceAsync(String email) {
		return port.getBalanceAsync(email);
	}

	@Override
	public Response<SetBalanceResponse> setBalanceAsync(String email, int credit, int tag) {
		return port.setBalanceAsync(email, credit, tag);
	}

	@Override
	public Response<GetInfoResponse> getInfoAsync() {
		return port.getInfoAsync();
	}

	@Override
	public Response<GetBinaResponse> getBinaAsync() {
		return port.getBinaAsync();
	}

	@Override
	public Response<ReturnBinaResponse> returnBinaAsync() {
		return port.returnBinaAsync();
	}

	@Override
	public Response<TestPingResponse> testPingAsync(String inputMessage) {
		return port.testPingAsync(inputMessage);
	}

	@Override
	public Response<TestClearResponse> testClearAsync() {
		return port.testClearAsync();
	}

	@Override
	public Response<TestInitResponse> testInitAsync(int x, int y, int capacity, int returnPrize) {
		return port.testInitAsync(x, y, capacity, returnPrize);
	}

	//--------------------------------Future--------------------------------------------------------
	
	@Override
	public Future<?> testInitAsync(int x, int y, int capacity, int returnPrize,
			AsyncHandler<TestInitResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> testClearAsync(AsyncHandler<TestClearResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> testPingAsync(String inputMessage, AsyncHandler<TestPingResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> returnBinaAsync(AsyncHandler<ReturnBinaResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> getBinaAsync(AsyncHandler<GetBinaResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> getBalanceAsync(String email, AsyncHandler<GetBalanceResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> getInfoAsync(AsyncHandler<GetInfoResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Future<?> setBalanceAsync(String email, int credit, int tag, AsyncHandler<SetBalanceResponse> asyncHandler) {
		// TODO Auto-generated method stub
		return null;
	}
}
