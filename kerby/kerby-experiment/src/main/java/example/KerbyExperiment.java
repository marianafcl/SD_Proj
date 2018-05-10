package example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.Date;
import java.security.Key;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.RequestTimeView;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.AuthView;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import static javax.xml.bind.DatatypeConverter.printHexBinary;


public class KerbyExperiment {
	private static final String MAC_ALGO = "HmacSHA256";

    public static void main(String[] args) throws Exception {
        System.out.println("Hi!");

        System.out.println();

        // receive arguments
        System.out.printf("Received %d arguments%n", args.length);

        System.out.println();

        // load configuration properties
        try {
            InputStream inputStream = KerbyExperiment.class.getClassLoader().getResourceAsStream("config.properties");
            // TODO IMPORTANT FOR LATER IMPLEMENTATION
            // variant for non-static methods:
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties properties = new Properties();
            properties.load(inputStream);

            System.out.printf("Loaded %d properties%n", properties.size());

        } catch (IOException e) {
            System.out.printf("Failed to load configuration: %s%n", e);
        }

        System.out.println();
        
        
        
        
        
        // TODO both client (binas-ws-cli) and server (binas-ws) communicate
        //		with kerby-ws-cli for authentication via kerby-lib
        // TODO pom dependency of above modules on kerby-lib
        
        
        
        
		// client-side code experiments
        System.out.println("Experiment with Kerberos client-side processing");
       
        String server = "binas@CXX.binas.org"; /*constant*/
        String email = "alice@CXX.binas.org";
        String client_password = "Zd8hqDu23t"; /*get this argument in binas-ws-cli methods along with email*/
        String url = "http://localhost:8888/kerby"; /*constant*/
        int duration = 120;
        long nounce = new Random().nextLong();
        Date date = new Date();
        
        
        Key kc = SecurityHelper.generateKeyFromPassword(client_password);
        
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
		
		System.out.print("Client's SessionKey (KCS included): "); System.out.println(sessionkey);
        
        
        CipheredView cipheredAuth = (new Auth(email, date)).cipher(sessionkey.getKeyXY());
        
        /*handler sends ticket+auth views (ciphered ticket and cv) + request*/
        // FALTA OS MACS EHEHEHHEHEHEHEHHEHEHEHEHEHEHHEHEHEHEHEHEH
        
        /*Mac Request */
        Key kcs = sessionkey.getKeyXY();
        /** Plain text to protect with the message authentication code. */
    	final String plainText = "This is the plain text!";
    	/** Plain text bytes. */
    	final byte[] plainBytes = plainText.getBytes();
    	
    	// make MAC
    	System.out.println("Signing ...");
    	byte[] cipherDigest = makeMAC(plainBytes, kcs);
    	System.out.println("CipherDigest:");
    	System.out.println(printHexBinary(cipherDigest));
        
        System.out.println();

        
        
        
        
		// server-side code experiments
        System.out.println("Experiment with Kerberos server-side processing");
        
        
        String server_password = "MTbvC3"; /*constant*/
        
        
        Key ks = SecurityHelper.generateKeyFromPassword(server_password);
        
        System.out.println("Got KS.");
        
        
        Ticket ticket = new Ticket(cipheredTicket, ks);
        ticket.validate();
        
        
        System.out.print("Server's Ticket (KCS included): "); System.out.println(ticket);
        
        
        kcs = ticket.getKeyXY();
        Auth auth = new Auth(cipheredAuth, kcs);
        auth.validate();
        
        Date now = new Date();
        if(!ticket.getX().equals(auth.getX()) || now.before(ticket.getTime1()) || now.after(ticket.getTime2())) {
        	throw new KerbyException();
        }
        
        System.out.print("Server's Client's Auth: {"); System.out.print(auth.getX()); System.out.print("; "); System.out.print(auth.getTimeRequest()); System.out.println("}");
        
        // verify the MAC
     	System.out.println("Verifying ...");
     	boolean resultAux = verifyMAC(cipherDigest, plainBytes, kcs);
     	System.out.println("MAC is " + (resultAux ? "right" : "wrong"));
   
        CipheredView cipheredTimeRequest = (new RequestTime(auth.getTimeRequest())).cipher(kcs);
        
        /*handler sends timerequest view (cipheredTimeRequest) + response*/
        /**TODO Gerar novamente MAC para enviar para o cliente**/
        
        System.out.println();
        
        
        
        
        
        // client-side code experiments
        System.out.println("Experiment with Kerberos client-side processing");
        
        RequestTime tr = new RequestTime(cipheredTimeRequest, sessionkey.getKeyXY());
        if(!date.equals(tr.getTimeRequest())) {
        	throw new KerbyException();
        }
        
        System.out.print("Client's Received Time Request: "); System.out.println(tr);
        
        System.out.println();
		
		System.out.println("Bye!");
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


