package com.dss.vms.master.web;

public class VideoCameraLink {
	private Integer cameraID;
	private String cameraName;
	private String cameraModel;
	private boolean linkStatus;
	private boolean recordingStatus;
	
	public VideoCameraLink(Integer cameraID, String cameraName, String cameraModel, boolean linkStat, boolean recordStatus) {
		this.cameraID = cameraID;
		this.cameraName = cameraName;
		this.cameraModel = cameraModel;
		this.linkStatus = linkStat;
		this.recordingStatus = recordStatus;
	}

	public Integer getCameraID() {
		return cameraID;
	}

	public String getCameraName() {
		return cameraName;
	}

	public String getCameraModel() {
		return cameraModel;
	}

	public boolean isLinkStatus() {
		return linkStatus;
	}

	public void setLinkStatus(boolean linkStatus) {
		this.linkStatus = linkStatus;
	}

	public boolean isRecordingStatus() {
		return recordingStatus;
	}

	public void setRecordingStatus(boolean recordingStatus) {
		this.recordingStatus = recordingStatus;
	}
}
