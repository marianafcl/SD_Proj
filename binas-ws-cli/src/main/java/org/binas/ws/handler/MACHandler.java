package org.binas.ws.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
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
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static javax.xml.bind.DatatypeConverter.printHexBinary;




public class MACHandler implements SOAPHandler<SOAPMessageContext> {
	private static final String MAC_ALGO = "HmacSHA256";
	private static final String url = "http://localhost:8888/kerby";
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("Make MAC.");
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
			
			
			Key kcs = (Key) smc.get("SessionKey");
			
			String message = msg.toString();
			byte[] messageBytes = message.getBytes();
			// make MAC
	    	System.out.println("Signing ...");
	    	byte[] cipherDigest = makeMAC(messageBytes, kcs);
	    	System.out.println("CipherDigest:");
	    	System.out.println(printHexBinary(cipherDigest));
			
	    	
	    	Name name = se.createName("MacHeader", "mac", url);
			SOAPHeaderElement elementMac = sh.addHeaderElement(name);
			elementMac.addChildElement(printHexBinary(cipherDigest));
			
	    	
			msg.saveChanges();
		} catch (SOAPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//if (!opn.getLocalPart().equals(OPERATION_NAME)) {return true; }
		/*TODO Perguntar se existem métodos que não sejam necessários encriptar, por exemplo testInit?*/
		/*Key kc = null;
			String email = null;
			Date date = new Date();
			long nounce = new Random().nextLong();
			int duration = 120;
			CipherClerk clerk = new CipherClerk();
			CipheredView cipheredSessionKey = null;
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
				else if (argument.getNodeName().equals("KeySession")) {
					cipheredSessionKey = clerk.cipherFromXMLNode(argument);
				}
			}


			SessionKey sessionkey = new SessionKey(cipheredSessionKey, kc);
			if(!(nounce == sessionkey.getNounce())) {
				throw new KerbyException();
			}*/


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
		return Arrays.equals(cipherDigest, cipheredBytes);
	}

}
