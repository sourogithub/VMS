package com.dss.vms.ui.constants;

import com.dss.vms.common.constants.StreamingProtocol;

public enum SupportedCameraModel {
	// Models
//	GW_SECURITY(false, "GW-Security", 554, 554, 554, 554,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp"),

//	HIKVISION(false, "Hikvision", 554, 554, 554, 554,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			StreamingProtocol.RTSP,
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp",
//			"h264_ulaw.sdp"),

	CP_PLUS_DA10L3S(false, "CP-Plus UNC-DA10L3S", 80, 554, 554, 554,
			StreamingProtocol.HTTP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			"axis-cgi/mjpg/video.cgi?subtype=1", "", "", ""),
	
	CP_PLUS_DA20L3S(false, "CP-Plus UNC-DA20L3S", 80, 554, 554, 554,
			StreamingProtocol.HTTP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			"axis-cgi/mjpg/video.cgi?subtype=1", "", "", ""),

	CUSTOM(false, "Custom", 554, 554, 554, 554,
			StreamingProtocol.HTTP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			StreamingProtocol.RTSP,
			"", "", "", "");

	private boolean ptz;
	private String model;

	private int analyticPort;
	private int microPort;
	private int macroPort;
	private int miniPort;

	private StreamingProtocol analyticProto;
	private StreamingProtocol microProto;
	private StreamingProtocol macroProto;
	private StreamingProtocol miniProto;

	private String analyticUrl;
	private String microUrl;
	private String macroUrl;
	private String miniUrl;
	
	private SupportedCameraModel(boolean ptz, String model, int analyticPort, int microPort, int macroPort, int miniPort,
			StreamingProtocol analyticProto, StreamingProtocol microProto, StreamingProtocol macroProto,
			StreamingProtocol miniProto, String analyticUrl, String microUrl, String macroUrl, String miniUrl) {
		this.ptz = ptz;
		this.model = model;
		this.analyticPort = analyticPort;
		this.microPort = microPort;
		this.macroPort = macroPort;
		this.miniPort = miniPort;
		this.analyticProto = analyticProto;
		this.microProto = microProto;
		this.macroProto = macroProto;
		this.miniProto = miniProto;
		this.analyticUrl = analyticUrl;
		this.microUrl = microUrl;
		this.macroUrl = macroUrl;
		this.miniUrl = miniUrl;
	}

	public boolean isPtz() {
		return ptz;
	}

	public String getModel() {
		return model;
	}

	public int getAnalyticPort() {
		return analyticPort;
	}

	public int getMicroPort() {
		return microPort;
	}

	public int getMacroPort() {
		return macroPort;
	}

	public int getMiniPort() {
		return miniPort;
	}

	public StreamingProtocol getAnalyticProto() {
		return analyticProto;
	}

	public StreamingProtocol getMicroProto() {
		return microProto;
	}

	public StreamingProtocol getMacroProto() {
		return macroProto;
	}

	public StreamingProtocol getMiniProto() {
		return miniProto;
	}

	public String getAnalyticUrl() {
		return analyticUrl;
	}

	public String getMicroUrl() {
		return microUrl;
	}

	public String getMacroUrl() {
		return macroUrl;
	}

	public String getMiniUrl() {
		return miniUrl;
	}

	@Override
	public String toString() {
		return this.model;
	}
}
