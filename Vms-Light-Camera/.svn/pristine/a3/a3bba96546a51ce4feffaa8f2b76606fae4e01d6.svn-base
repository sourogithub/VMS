package com.dss.vms.common.data;

import java.io.Serializable;

import com.dss.vms.common.constants.StreamType;

/**
 * @author Sibendu
 */
public class VideoCamera implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int INVALID_CAM_ID = -1;

	private String ip;
	private boolean ptz;
	private String model;
	private String streamingUsername;
	private String streamingPassword;
	private String controlUsername;
	private String controlPassword;
	private int id = INVALID_CAM_ID;
	private String analyticStreamURL;
	private String microStreamURL;
	private String miniStreamURL;
	private String macroStreamURL;
	private StreamType recordingStream;
	private String name;
	/**
	 * 
	 */
	public VideoCamera() {
	}

	/**
	 * @param ip
	 * @param ptz
	 * @param model
	 * @param cameraID
	 * @param analyticStreamURL
	 * @param microStreamURL
	 * @param miniStreamURL
	 * @param macroStreamURL
	 * @param recordingStream
	 */
	public VideoCamera(String ip, String name, boolean ptz, String model, String analyticStreamURL,
			String microStreamURL, String miniStreamURL, String macroStreamURL, StreamType recordingStream) {
		super();
		this.ip = ip;
		this.ptz = ptz;
		this.model = model;
		this.analyticStreamURL = analyticStreamURL;
		this.microStreamURL = microStreamURL;
		this.miniStreamURL = miniStreamURL;
		this.macroStreamURL = macroStreamURL;
		this.recordingStream = recordingStream;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VideoCamera [ip=");
		builder.append(ip);
		builder.append(", ptz=");
		builder.append(ptz);
		builder.append(", model=");
		builder.append(model);
		builder.append(", streamingUsername=");
		builder.append(streamingUsername);
		builder.append(", streamingPassword=");
		builder.append(streamingPassword);
		builder.append(", controlUsername=");
		builder.append(controlUsername);
		builder.append(", controlPassword=");
		builder.append(controlPassword);
		builder.append(", id=");
		builder.append(id);
		builder.append(", analyticStreamURL=");
		builder.append(analyticStreamURL);
		builder.append(", microStreamURL=");
		builder.append(microStreamURL);
		builder.append(", miniStreamURL=");
		builder.append(miniStreamURL);
		builder.append(", macroStreamURL=");
		builder.append(macroStreamURL);
		builder.append(", recordingStream=");
		builder.append(recordingStream);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the ptz
	 */
	public boolean isPtz() {
		return ptz;
	}

	/**
	 * @param ptz the ptz to set
	 */
	public void setPtz(boolean ptz) {
		this.ptz = ptz;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the streamingUsername
	 */
	public String getStreamingUsername() {
		return streamingUsername;
	}

	/**
	 * @param streamingUsername the streamingUsername to set
	 */
	public void setStreamingUsername(String streamingUsername) {
		this.streamingUsername = streamingUsername;
	}

	/**
	 * @return the streamingPassword
	 */
	public String getStreamingPassword() {
		return streamingPassword;
	}

	/**
	 * @param streamingPassword the streamingPassword to set
	 */
	public void setStreamingPassword(String streamingPassword) {
		this.streamingPassword = streamingPassword;
	}

	/**
	 * @return the controlUsername
	 */
	public String getControlUsername() {
		return controlUsername;
	}

	/**
	 * @param controlUsername the controlUsername to set
	 */
	public void setControlUsername(String controlUsername) {
		this.controlUsername = controlUsername;
	}

	/**
	 * @return the controlPassword
	 */
	public String getControlPassword() {
		return controlPassword;
	}

	/**
	 * @param controlPassword the controlPassword to set
	 */
	public void setControlPassword(String controlPassword) {
		this.controlPassword = controlPassword;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the analyticStreamURL
	 */
	public String getAnalyticStreamURL() {
		return analyticStreamURL;
	}

	/**
	 * @param analyticStreamURL the analyticStreamURL to set
	 */
	public void setAnalyticStreamURL(String analyticStreamURL) {
		this.analyticStreamURL = analyticStreamURL;
	}

	/**
	 * @return the microStreamURL
	 */
	public String getMicroStreamURL() {
		return microStreamURL;
	}

	/**
	 * @param microStreamURL the microStreamURL to set
	 */
	public void setMicroStreamURL(String microStreamURL) {
		this.microStreamURL = microStreamURL;
	}

	/**
	 * @return the miniStreamURL
	 */
	public String getMiniStreamURL() {
		return miniStreamURL;
	}

	/**
	 * @param miniStreamURL the miniStreamURL to set
	 */
	public void setMiniStreamURL(String miniStreamURL) {
		this.miniStreamURL = miniStreamURL;
	}

	/**
	 * @return the macroStreamURL
	 */
	public String getMacroStreamURL() {
		return macroStreamURL;
	}

	/**
	 * @param macroStreamURL the macroStreamURL to set
	 */
	public void setMacroStreamURL(String macroStreamURL) {
		this.macroStreamURL = macroStreamURL;
	}

	/**
	 * @return the recordingStream
	 */
	public StreamType getRecordingStream() {
		return recordingStream;
	}

	/**
	 * @param recordingStream the recordingStream to set
	 */
	public void setRecordingStream(StreamType recordingStream) {
		this.recordingStream = recordingStream;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the invalidCamId
	 */
	public static int getInvalidCamId() {
		return INVALID_CAM_ID;
	}

}
