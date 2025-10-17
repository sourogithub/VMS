package com.dss.vms.master.web;

public class CameraData {
	private Integer cameraID;
	private String cameraName;
	private String cameraModel;
	
	public CameraData(Integer cameraID, String cameraName, String cameraModel) {
		this.cameraID = cameraID;
		this.cameraName = cameraName;
		this.cameraModel = cameraModel;
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
}
