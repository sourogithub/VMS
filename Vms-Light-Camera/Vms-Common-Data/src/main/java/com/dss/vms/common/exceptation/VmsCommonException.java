package com.dss.vms.common.exceptation;

import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponseCode;

public abstract class VmsCommonException extends Throwable {
	private VmsResponseCode responseCode = CommonResponseCode.NOT_IMPLEMENTED;
	private String message;

	/**
	 * @param responseCode
	 * @param message
	 */
	public VmsCommonException(VmsResponseCode responseCode, String message) {
		super();
		this.responseCode = responseCode;
		this.message = message;
	}

	/**
	 * @return the responseCode
	 */
	public VmsResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(VmsResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
