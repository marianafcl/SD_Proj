package org.binas.ws.handler;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.crypto.Cipher;
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
import javax.xml.ws.handler.MessageContext.Scope;

import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;
import pt.ulisboa.tecnico.sdis.kerby.Auth;


public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext>{
	private static final String url = "http://localhost:8888/kerby";
	private static final String server = "binas@CXX.binas.org";

	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddHeaderHandler: Handling message in client.");
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

			//if (!opn.getLocalPart().equals(OPERATION_NAME)) {return true; }
			/*TODO Perguntar se existem métodos que não sejam necessários encriptar, por exemplo testInit?*/
			Key kc = null;
			String email = null;
			Date date = new Date();
			long nounce = new Random().nextLong();
			int duration = 120;
			NodeList children = sb.getFirstChild().getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node argument = (Node) children.item(i);
				if (argument.getNodeName().equals("email")) {
					InputStream inputStream = KerberosClientHandler.class.getResourceAsStream("/A48-secrets.txt");
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					email = argument.getTextContent();
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
			/*TODO Se key null, manda excepção???*/
			System.out.println("Got KC.");

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

			/*TODO fix url*/
			Name name = se.createName("TicketHeader", "ticket", url);
			SOAPHeaderElement element = sh.addHeaderElement(name);
		
			CipherClerk clerk = new CipherClerk();
			element.addChildElement(clerk.cipherToXMLNode(cipheredTicket, "TicketHeader").getLocalName());

			Name nameAuth = se.createName("AuthHeader", "auth", url);
			SOAPHeaderElement elementAuth = sh.addHeaderElement(nameAuth);
			elementAuth.addChildElement(clerk.cipherToXMLNode(cipheredAuth, "AuthHeader").getLocalName());

			/*sb.addChildElement(clerk.cipherToXMLNode(cipheredSessionKey, "KeySession").getLocalName());*/

			smc.put("SessionKey", sessionkey.getKeyXY());
			msg.saveChanges();


			/* TODO Acrescentar o if, para ver se está a receber ou a ler*/

		}	 catch (SOAPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KerbyClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadTicketRequest_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KerbyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
