package com.dss.vms.ui.utility;

import java.util.regex.Pattern;

/**
 * @author Sibendu
 */
public class CameraFormValidator {
	private static final String IP_REGEX = "\\b(https?|rtsp)(://)([0-9]{1,3})(\\.)([0-9]{1,3})(\\.)([0-9]{1,3})(\\.)([0-9]{1,3})([:])([0-9]+)/(.*)";

	/**
	 * validate usernames
	 * @param username
	 * @return
	 */
	public static boolean validateUserNames(String username) {
		return (username.equals("") ? false : true);
	}

	/**
	 * validate password 
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		return (password.equals("") ? false : true);
	}

	/**
	 * ip address validator
	 * @param ip
	 * @return
	 */
	public static boolean validateIPAddress(String ip) {
		return Pattern.matches(IP_REGEX, ip);
	}

	/**
	 * validate camera urls
	 * @param addresses
	 * @return
	 */
	public static boolean validateStreamAddresses(String...addresses) {
		boolean success = true;
		try {
			for (String streamAddress : addresses) {
				success = Pattern.matches(IP_REGEX, streamAddress);
				if (!success)break;
			}
		} catch (Exception e) {
			success = false;
		}
		
		return success;
	}

	public static boolean validateCameraName(String name) {
		return (name.equals("") ? false : true);
	}
	
}
