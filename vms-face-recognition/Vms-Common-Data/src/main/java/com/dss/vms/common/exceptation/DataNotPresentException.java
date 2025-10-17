package com.dss.vms.common.exceptation;

import com.dss.vms.common.response.VmsResponseCode;

public class DataNotPresentException extends VmsCommonException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2433166133258363484L;

	public DataNotPresentException(VmsResponseCode responseCode, String message) {
		super(responseCode, message);
	}

}
