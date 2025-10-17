package com.dss.vms.common.response;

import java.io.Serializable;

import com.dss.vms.common.exceptation.VmsCommonException;

/**
 * @author jdeveloper
 * 
 */
public class VmsResponse implements Serializable {

	private static final long serialVersionUID = 5125655999617058931L;
	private VmsResponseCode responseCode = CommonResponseCode.NOT_IMPLEMENTED;
	private String message;
	private Serializable response;

	/**
	 */
	public VmsResponse() {
	}

	/**
	 * @param errorCode
	 */
	public VmsResponse(VmsResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public VmsResponse(VmsResponseCode responseCode, String message) {
		this.responseCode = responseCode;
		this.message = message;
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public VmsResponse(VmsCommonException e) {
		this.responseCode = e.getResponseCode();
		this.message = e.getMessage();
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param response
	 */
	public VmsResponse(VmsResponseCode responseCode, String message, Serializable response) {
		this.responseCode = responseCode;
		this.message = message;
		this.response = response;
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
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the response
	 */
	public Serializable getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(Serializable response) {
		this.response = response;
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public void setError(VmsResponseCode errorCode, String message) {
		this.responseCode = errorCode;
		this.message = message;
	}

	/**
	 * @return
	 */
	public boolean isSuccess() {
		return (this.responseCode == CommonResponseCode.SUCCESS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VmsResponse [responseCode=");
		builder.append(responseCode);
		builder.append(", message=");
		builder.append(message);
		builder.append(", response=");
		builder.append(response == null ? "NULL" : response.getClass().getSimpleName());
		builder.append("]");
		return builder.toString();
	}
}
