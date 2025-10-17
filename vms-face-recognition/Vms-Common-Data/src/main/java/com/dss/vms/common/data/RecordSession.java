package com.dss.vms.common.data;

import java.io.Serializable;

public final class RecordSession implements Serializable {
	private static final long serialVersionUID = 6941271202174389180L;
	
	private String mediaUrl;
	private int cameraID;
	private Long startTime;
	private Long endTime;

	/**
	 * @param mediaUrl
	 * @param cameraID
	 * @param startTime
	 * @param endTime
	 */
	public RecordSession(String mediaUrl, int cameraID, Long startTime, Long endTime) {
		this.mediaUrl = mediaUrl;
		this.cameraID = cameraID;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * @param mediaUrl the mediaUrl to set
	 */
	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	/**
	 * @param cameraID the cameraID to set
	 */
	public void setCameraID(int cameraID) {
		this.cameraID = cameraID;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the mediaUrl
	 */
	public String getMediaUrl() {
		return mediaUrl;
	}

	/**
	 * @return the cameraID
	 */
	public int getCameraID() {
		return cameraID;
	}

	/**
	 * @return the startTime
	 */
	public Long getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Long getEndTime() {
		return endTime;
	}
}
