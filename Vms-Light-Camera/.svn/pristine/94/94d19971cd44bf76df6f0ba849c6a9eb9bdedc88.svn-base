package com.dss.vms.common.data;

import java.io.Serializable;

import com.dss.vms.common.constants.AnalyticType;

/**
 * 
 * @author dss-04
 *
 */
public class EventSession implements Serializable {
	private static final long serialVersionUID = 106065259910953831L;

	private String filePath = "";
	private Long time;
	private AnalyticType type;

	/**
	 * @param filePath
	 * @param time
	 * @param type
	 */
	public EventSession(String filePath, Long time, AnalyticType type) {
		super();
		this.filePath = filePath;
		this.time = time;
		this.type = type;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * @return the type
	 */
	public AnalyticType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AnalyticType type) {
		this.type = type;
	}
}