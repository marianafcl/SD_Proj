package org.binas.ws.handler;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext>{
	private static final String server = "binas@A48.binas.org";
	private static final String server_password = "FBiMOd9e";
	private static final String url = "http://localhost:8888/kerby";
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		
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
			
			//Get Ticket
			Name name = se.createName("TicketHeader", "ticket", url);
			Iterator<?> it = sh.getChildElements(name);
			// check header element
			if (!it.hasNext()) {
				System.out.println("Header element not found.");
				return true;
			}
			SOAPElement element = (SOAPElement) it.next();
			
			//Get Auth
			Name nameAuth = se.createName("AuthHeader", "auth", url);
			it = sh.getChildElements(nameAuth);
			// check header element
			if (!it.hasNext()) {
				System.out.println("Header element not found.");
				return true;
			}
			SOAPElement elementAuth = (SOAPElement) it.next();
			
			
			
			CipherClerk clerk = new CipherClerk();
			CipheredView cipheredAuth = clerk.cipherFromXMLNode(elementAuth);
			
			
			Key ks = SecurityHelper.generateKeyFromPassword(server_password);
	        
	        System.out.println("Got KS.");
	        
			
			Ticket ticket = new Ticket(cipheredTicket, ks);
	        ticket.validate();
			
			System.out.print("Server's Ticket (KCS included): "); System.out.println(ticket);
	        
	        
	        Key kcs = ticket.getKeyXY();
	        Auth auth = new Auth(cipheredAuth, kcs);
	        auth.validate();
	        
	        Date now = new Date();
	        if(!ticket.getX().equals(auth.getX()) || now.before(ticket.getTime1()) || now.after(ticket.getTime2())) {
	        	throw new KerbyException();
	        }
	        
	        System.out.print("Server's Client's Auth: {"); System.out.print(auth.getX()); System.out.print("; "); System.out.print(auth.getTimeRequest()); System.out.println("}");

		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KerbyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
