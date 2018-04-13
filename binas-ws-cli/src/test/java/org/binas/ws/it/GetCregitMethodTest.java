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

	@Test
	public void success() {
		int credit;
		try {
			credit = client.getCredit(email);
			assertEquals(10, credit);
		} catch(UserNotExists_Exception e) {
			fail();
		}
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void userNotExistsException() throws UserNotExists_Exception {
		client.getCredit("bunica");
	}
	
	
}
