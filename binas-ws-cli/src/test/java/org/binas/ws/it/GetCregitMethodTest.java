package org.binas.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Test;

public class GetCregitMethodTest extends BaseIT {
	private final static String email = "joaquina.bernardina@ist.bah";

	@Test
	public void success() {
		int credit;
		try {
			UserView userView = client.activateUser(email);
			credit = client.getCredit(email);
			assertEquals((int)userView.getCredit(), credit);
		} catch (EmailExists_Exception e) {
			fail();
		} catch (InvalidEmail_Exception e) {
			fail();
		} catch(UserNotExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExistsException() throws UserNotExists_Exception {
		int credit;
		try {
			UserView userView = client.activateUser(email);
			credit = client.getCredit("bunica");
		} catch (EmailExists_Exception e) {
			fail();
		} catch (InvalidEmail_Exception e) {
			fail();
		}
	}
	
	@After
	public void clean() {
		client.testClear();
	}
}
