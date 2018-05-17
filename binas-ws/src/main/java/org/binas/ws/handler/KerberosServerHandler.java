package org.binas.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
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
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext>{
	private static final String server_password = "FBiMOd9e";
	private static final String server = "binas@A48.binas.org";
	private static final String url = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
	private Key Kcs;
	private Auth auth;
	private HashMap<String, Date> clientsTreq = new HashMap<>();

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
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

			if (outboundElement.booleanValue()) { 
				CipheredView cipheredTimeRequest = (new RequestTime(auth.getTimeRequest())).cipher(Kcs);
				
				Name name = se.createName("RequestTimeHeader", "requestTime", url);
				SOAPHeaderElement element = sh.addHeaderElement(name);
		
				CipherClerk clerk = new CipherClerk();
				element.addTextNode(printHexBinary(clerk.cipherToXMLBytes(cipheredTimeRequest, "RequestTimeHeader")));
			}
			else {
				//Get Ticket
				Name name = se.createName("TicketHeader", "ticket", url);
				Iterator<?> it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("TicketHeader element not found.");
					throw new RuntimeException();
				}
				SOAPElement element = (SOAPElement) it.next();

				//Get Auth
				Name nameAuth = se.createName("AuthHeader", "auth", url);
				it = sh.getChildElements(nameAuth);
				// check header element
				if (!it.hasNext()) {
					System.out.println("AuthHeader element not found.");
					throw new RuntimeException();
				}
				SOAPElement elementAuth = (SOAPElement) it.next();



				CipherClerk clerk = new CipherClerk();
				CipheredView cipheredAuth = clerk.cipherFromXMLBytes(parseHexBinary(elementAuth.getValue()));
				CipheredView cipheredTicket = clerk.cipherFromXMLBytes(parseHexBinary(element.getValue()));


				Key ks = SecurityHelper.generateKeyFromPassword(server_password);

				System.out.println("Got KS.");


				Ticket ticket = new Ticket(cipheredTicket, ks);
				ticket.validate();

				System.out.print("Server's Ticket (KCS included): "); System.out.println(ticket);

		
				Key kcs = ticket.getKeyXY();
				Auth auth = new Auth(cipheredAuth, kcs);
				auth.validate();
				this.Kcs = kcs;
				this.auth = auth;
				Date now = new Date();
				if(!ticket.getX().equals(auth.getX()) || !ticket.getY().equals(server)|| now.before(ticket.getTime1()) || now.after(ticket.getTime2())) {
					throw new KerbyException();
				}

				smc.put("SessionKey", kcs);
				msg.saveChanges();

				System.out.print("Server's Client's Auth: {"); System.out.print(auth.getX()); System.out.print("; "); System.out.print(auth.getTimeRequest()); System.out.println("}");
				
				//Verificar se TimeRequest é valido
				String email = auth.getX();
				if(clientsTreq.containsKey(email)) {
					if(clientsTreq.get(email).after(auth.getTimeRequest())) {
						throw new RuntimeException();
					}
				}
				
				clientsTreq.put(email, auth.getTimeRequest());
			}
		} catch (SOAPException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (KerbyException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException();
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
