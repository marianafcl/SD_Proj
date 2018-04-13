package org.binas.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.binas.ws.cli.BinasClient;
import org.binas.ws.it.BaseIT;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivateUserTest extends BaseIT {
	private final static String email = "joaquina.bernardina@ist.bah";
	private final static String email1 = "joaquina.bernardino@ist.bah";
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps1;
	protected static BinasClient client1;
	protected static BinasClient client2;
	
	@Before
	public void oneSetup() throws Exception {
		testProps1 = new Properties();
		try {
			testProps1.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps1.getProperty("uddi.enabled");
		final String verboseEnabled = testProps1.getProperty("verbose.enabled");

		final String uddiURL = testProps1.getProperty("uddi.url");
		final String wsName = testProps1.getProperty("ws.name");
		final String wsURL = testProps1.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client1 = new BinasClient(uddiURL, wsName);
		} else {
			client1 = new BinasClient(wsURL);
		}
		client1.setVerbose("true".equalsIgnoreCase(verboseEnabled));
		
		client2 = new BinasClient(uddiURL, "A48_Station2");
	}
	
	@Test
	public void success() {
		try {
			UserView userView = client.activateUser(email1);
			
			assertEquals(email1, userView.getEmail());
			assertEquals(10, (int)userView.getCredit());
		} catch (EmailExists_Exception e) {
			fail();
		} catch (InvalidEmail_Exception e) {
			fail();
		}
	}
	
	@Test(expected = EmailExists_Exception.class)
	public void emailExistsException() throws EmailExists_Exception {
		try {
			UserView userView = client.activateUser(email1);
			UserView userView1 = client1.activateUser(email1);
		} catch (InvalidEmail_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmailExceptionNoDomain() throws InvalidEmail_Exception{
		try {
			UserView userView = client.activateUser("bunica.bate.toba@");
		} catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmailExceptionWrongDomain() throws InvalidEmail_Exception{
		try {
			UserView userView = client.activateUser("bunica.bate.toba@bunica");
		} catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmailExceptionNoUser() throws InvalidEmail_Exception{
		try {
			UserView userView = client.activateUser("@bunica.tare");
		} catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmailExceptionWrongUser() throws InvalidEmail_Exception{
		try {
			UserView userView = client.activateUser("bunica@bunica.tare");
		} catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmailExceptionNoUserNoDomain() throws InvalidEmail_Exception{
		try {
			UserView userView = client.activateUser("@");
		} catch (EmailExists_Exception e) {
			fail();
		}
	}
	
	@After
	public void clean() {
		client.testClear();
		client1.testClear();
		client2.testClear();
	}
}
