package org.binas.ws.handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;



public class EVILHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Set<String> OPERATION_NAME = new HashSet<String>(Arrays.asList(new String[] {"getCredit","getCreditResponse","activateUser","activateUserResponse", "rentBina", "RentBinaResponse", "returnBina", "returnBinaResponse"}));
	private static final String MAC_ALGO = "HmacSHA256";
	private static final String url = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
	/** XML transformer factory. */
	private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	/** XML transformer property name for XML indentation amount. */
	private static final String XML_INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	/** XML indentation amount to use (default=0). */
	private static final Integer XML_INDENT_AMOUNT_VALUE = 2;
	private Key Kcs;

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("Hacking...");
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
				processOutBound(sb);
				msg.saveChanges();
			}
		} catch (SOAPException e1) {
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
	
	
	/**Process the inbound messages
	 * @throws Exception **/
	public void processOutBound(SOAPBody sb) throws Exception {
		NodeList children = sb.getFirstChild().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			System.out.println("Estou dentro do for");
			Node argument = (Node) children.item(i);
			if (argument.getNodeName().equals("email")) {
				//InputStream inputStream = KerberosClientHandler.class.getResourceAsStream("/A48-secrets.txt");
				BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Alexandra Figueiredo\\A48-SD18Proj\\A48-secrets.txt"));
				String line;
				String sec = argument.getTextContent();
				argument.setTextContent("charlie@A48.binas.org");
			}
		}
     
	}




}