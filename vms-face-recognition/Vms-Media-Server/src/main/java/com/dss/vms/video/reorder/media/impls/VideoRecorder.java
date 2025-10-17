package com.dss.vms.video.reorder.media.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.CommonConstant;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.common.NativeConstants;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.video.reorder.media.AbstractStreamGrabber;
/**
 * 
 * @author dss-02
 *
 */
public class VideoRecorder extends AbstractStreamGrabber implements NativeConstants, NativeRetun, CommonConstant {
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoRecorder.class);
	private static final String EXECUTABLE_FILE = "./Media_Recorder ";
	private Process process;
//	private Long pid;
	private String executeCmd;
	private int servPort = 8080;

	/**
	 * 
	 * @param cameraModel
	 * @param url
	 * @param username
	 * @param password
	 * @param streamType
	 * @param cameraID
	 */
	public VideoRecorder(String cameraModel, String url, String username, String password,
			StreamType streamType, int cameraID) {
		super(cameraModel, url, username, password, streamType, cameraID);
		this.executeCmd = EXECUTABLE_FILE + cameraID + " " + username + " " + password + " " + url + " " + servPort;
	}

	@Override
	public void run() {}

	@Override
	public VmsResponse pauseGrabber() {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse resumeGrabber() {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse stopGrabber() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		LOGGER.info("Stopping Recorder [camera id : " + cameraID + "] [URL = " + url + "]");
		try {
			if (process != null && process.isAlive()) process.destroyForcibly();
			try{Thread.sleep(2000);} catch (InterruptedException e) {}
//			Runtime.getRuntime().exec("kill " + pid);
			LOGGER.info("Successfully Stopped Recorder [camera id : " + cameraID + "] [URL = " + url + "]");
		} catch (Exception e) {
			LOGGER.error("Failed to stop Recorder [camera id : " + cameraID + "] [URL = " + url + "] , Error - " + e);
		}

		return response;
	}

	@Override
	public VmsResponse startGrabber() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		try {
			LOGGER.info("Trying to start Recorder for [Camera Id :" + cameraID + "]  [URL :" + url + "]");
			process = Runtime.getRuntime().exec(executeCmd);
//			pid = process.pid();
			LOGGER.info("Recorder started successfully for [Camera Id :" + cameraID + "]  [URL :" + url + "]");
		} catch (Exception e) {
			LOGGER.error("Failed to start Recorder for camera : " + url + " , Error : " + e);
			response.setResponseCode(CommonResponseCode.ERROR);
		}
		return response;
	}

	@Override
	public VmsResponse getMediaHeader() {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

}
