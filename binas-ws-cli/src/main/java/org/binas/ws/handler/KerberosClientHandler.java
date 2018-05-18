package org.binas.ws.handler;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;

import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;
import pt.ulisboa.tecnico.sdis.kerby.Auth;


import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;


public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext>{
	private static final Set<String> OPERATION_NAME = new HashSet<String>(Arrays.asList(new String[] {"getCredit","getCreditResponse","activateUser","activateUserResponse", "rentBina", "RentBinaResponse", "returnBina", "returnBinaResponse"}));
	private static final String url = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
	private static final String server = "binas@A48.binas.org";
	private Key Kcs;
	private Date date;

	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddHeaderHandler: Handling message in client.");
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		SOAPMessage msg = smc.getMessage();
		SOAPPart sp = msg.getSOAPPart();
		SOAPEnvelope se;
		try {
			se = sp.getEnvelope();
			SOAPBody sb = se.getBody();
			SOAPHeader sh = se.getHeader();
			if (sh == null) { sh = se.addHeader(); }
			QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
			QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);

		
			if (!OPERATION_NAME.contains(opn.getLocalPart())) {return true; }
			if (outboundElement.booleanValue()) {
				processOutbound(smc, sb, se, sh);
				msg.saveChanges();
			}
			
			else {
				processInbound(se, sh);
			}

		}	 catch (SOAPException e1) {
			throw new RuntimeException();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException();
		} catch (DOMException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		} catch (KerbyClientException e) {
			throw new RuntimeException();
		} catch (BadTicketRequest_Exception e) {
			throw new RuntimeException();
		} catch (KerbyException e) {
			throw new RuntimeException();
		} catch (JAXBException e) {
			throw new RuntimeException();
		}

		return true;
	}


	/** The handleFault method is invoked for fault message processing. */
	public boolean handleFault(SOAPMessageContext smc) {
		/*TODO Implementar caso dê erro*/
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	public void close(MessageContext messageContext) {

	}
	
	/**Process inbound message**/
	
	
	/**Process outbound message
	 * @param smc 
	 * @throws IOException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KerbyClientException 
	 * @throws BadTicketRequest_Exception 
	 * @throws KerbyException 
	 * @throws SOAPException 
	 * @throws JAXBException **/
	public void processOutbound(SOAPMessageContext smc, SOAPBody sb, SOAPEnvelope se, SOAPHeader sh) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KerbyClientException, BadTicketRequest_Exception, KerbyException, SOAPException, JAXBException {
		Key kc = null;
		String email = null;
		this.date = new Date();
		long nounce = new Random().nextLong();
		int duration = 120;
		NodeList children = sb.getFirstChild().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node argument = (Node) children.item(i);
			if (argument.getNodeName().equals("email")) {
				//InputStream inputStream = KerberosClientHandler.class.getResourceAsStream("/A48-secrets.txt");
				BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Alexandra Figueiredo\\A48-SD18Proj\\A48-secrets.txt"));
				String line;
				String sec = argument.getTextContent();
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				byteOut.write(sec.getBytes());
				email = byteOut.toString();
				System.out.println(email);
				while((line = reader.readLine()) != null) {
					line = line.trim();
					if(line.startsWith("#") || !line.contains(","))
						continue;
					String[] values = line.split(",");
					if(values[0].equals(email)) {
						kc = SecurityHelper.generateKeyFromPassword(values[1]);
						break;
					}
				}
			}
		}
	
		System.out.println("Got KC.");
		System.out.printf("email:",email);

		KerbyClient client = new KerbyClient(url);
		SessionKeyAndTicketView result = client.requestTicket(email, server, nounce, duration);

		System.out.println("Got client's ticket.");

		
		CipheredView cipheredSessionKey = result.getSessionKey();
		CipheredView cipheredTicket = result.getTicket();


		SessionKey sessionkey = new SessionKey(cipheredSessionKey, kc);
		if(!(nounce == sessionkey.getNounce())) {
			throw new KerbyException();
		}
		
		CipheredView cipheredAuth = (new Auth(email, date)).cipher(sessionkey.getKeyXY());

		System.out.print("Client's SessionKey (KCS included): "); System.out.println(sessionkey);

		
		Name name = se.createName("TicketHeader", "ticket", url);
		SOAPHeaderElement element = sh.addHeaderElement(name);

		CipherClerk clerk = new CipherClerk();
		element.addTextNode(printHexBinary(clerk.cipherToXMLBytes(cipheredTicket, "TicketHeader")));
		
		Name nameAuth = se.createName("AuthHeader", "auth", url);
		SOAPHeaderElement elementAuth = sh.addHeaderElement(nameAuth);
		elementAuth.addTextNode(printHexBinary(clerk.cipherToXMLBytes(cipheredAuth, "AuthHeader")));
		
		this.Kcs = sessionkey.getKeyXY();
		smc.put("SessionKey", sessionkey.getKeyXY());
	}
	
	/**Process inbound message
	 * @throws SOAPException 
	 * @throws JAXBException 
	 * @throws KerbyException **/
	public void processInbound(SOAPEnvelope se, SOAPHeader sh) throws SOAPException, JAXBException, KerbyException {
		Name name = se.createName("RequestTimeHeader", "requestTime", url);
		Iterator<?> it = sh.getChildElements(name);
		// check header element
		if (!it.hasNext()) {
			System.out.println("RequestTimeHeader element not found.");
		}
		SOAPElement element = (SOAPElement) it.next();
		
		CipherClerk clerk = new CipherClerk();
		CipheredView cipheredTimeRequest = clerk.cipherFromXMLBytes(parseHexBinary(element.getValue()));
		
		RequestTime tr = new RequestTime(cipheredTimeRequest, this.Kcs);
        if(!date.equals(tr.getTimeRequest())) {
        	throw new KerbyException();
        }
	}
}
