package com.dss.vms.common.exceptation;

import com.dss.vms.common.response.VmsResponseCode;

public class NativeException extends VmsCommonException {

	public NativeException(VmsResponseCode responseCode, String message) {
		super(responseCode, message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
