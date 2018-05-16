package org.binas.ws.handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.crypto.Mac;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;




public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {
	private static final String MAC_ALGO = "HmacSHA256";
	private static final String url = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
	private static final String server_password = "FBiMOd9e";
	/** XML transformer factory. */
	private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	/** XML transformer property name for XML indentation amount. */
	private static final String XML_INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	/** XML indentation amount to use (default=0). */
	private static final Integer XML_INDENT_AMOUNT_VALUE = 2;

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("Make MAC.");
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
			
			Source src = msg.getSOAPPart().getContent();
			String message;
			StringWriter outWriter = new StringWriter();
			StreamResult result = new StreamResult( outWriter );
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(XML_INDENT_AMOUNT_PROPERTY, XML_INDENT_AMOUNT_VALUE.toString());
			transformer.transform(src, result);
			StringBuffer m = outWriter.getBuffer(); 
			String finalstring = m.toString();
			String parts[] = finalstring.split("<S:Body>");
			
			byte[] messageBytes = parts[1].getBytes();
			System.out.println("Recebido cipherDigest");
			
			
			if (outboundElement.booleanValue()) {
				//GetSessionkey
				Key kcs = (Key) smc.get("SessionKey");
				System.out.println(kcs);
				// make MAC
				System.out.println("Signing ...");
				byte[] cipherDigest = makeMAC(messageBytes, kcs);
				System.out.println("CipherDigest:");
				System.out.println(printHexBinary(cipherDigest));

				Name name = se.createName("MacHeader", "mac", url);
				SOAPHeaderElement elementMac = sh.addHeaderElement(name);
				elementMac.addTextNode(printHexBinary(cipherDigest));
			
				msg.saveChanges();
			}
			else {
				//GetMac
				Name name = se.createName("MacHeader", "mac", url);
				Iterator<?> it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("MAC Header element not found.");
					throw new RuntimeException();
				}
				SOAPElement element = (SOAPElement) it.next();
				
				byte[] cipherDigest = parseHexBinary(element.getValue());
				System.out.println(new String(cipherDigest));
				//Get Ticket
				Name nameTicket = se.createName("TicketHeader", "ticket", url);
				it = sh.getChildElements(nameTicket);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					throw new RuntimeException();
				}
				SOAPElement elementTicket = (SOAPElement) it.next();
				
				CipherClerk clerk = new CipherClerk();
				byte[] aux = parseHexBinary(elementTicket.getValue());
				CipheredView cipheredTicket = clerk.cipherFromXMLBytes(aux);
				
				Key ks = SecurityHelper.generateKeyFromPassword(server_password);

				System.out.println("Got KS.");
				Ticket ticket = new Ticket(cipheredTicket, ks);
				Key kcs = ticket.getKeyXY();
				System.out.println(kcs);
				// verify the MAC
		     	System.out.println("Verifying ...");
		     	boolean resultAux = verifyMAC(cipherDigest, messageBytes, kcs);
		     	System.out.println(resultAux);
		     	System.out.println("MAC is " + (resultAux ? "right" : "wrong"));
				
			}
		} catch (SOAPException e1) {
			e1.printStackTrace();
			throw new RuntimeException();
		} catch (Exception e) {
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


	/** Makes a message authentication code. */
	private static byte[] makeMAC(byte[] bytes, Key key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipherDigest = cipher.doFinal(bytes);

		return cipherDigest;
	}


	private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, Key key) throws Exception {
		byte[] cipheredBytes = makeMAC(bytes, key);
		for(int i = 0; i < cipheredBytes.length; i++) { 
			if (cipheredBytes[i] != cipherDigest[i]) {
				return false;
			}
		}
		return true;
	}

}
