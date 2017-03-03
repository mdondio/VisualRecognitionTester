package net.mybluemix.visualrecognitiontester.loginsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * This class allows user to authenticate.
 * 
 * @author Marco Dondio
 *
 */

public class Authenticator {

	// Authorized users
	// XXX not very safe perhaps, good for POC
	private static Map<String, String> validUsers = new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;
		{
			put("marco", "marco");
			put("", ""); // default no password
		}
	};

	/**
	 * This method checks for valid credentials
	 * 
	 * @param username
	 * @param password
	 * @return username of logged user, null if no valid credentials are
	 *         provided
	 */
	public static String authenticateUser(String username, String password) {

		if (username == null || password == null)
			return null;

		// If valid users return username
		String pw = validUsers.get(username);

		if (pw != null && password.matches(pw))
			return username;

		// If no user it returns null
		return null;
	}

}
