package com.dss.vms.master;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;

public class TestAddCamera {
	private static final String TEST_IP = "192.168.1.200";
	private static final String TEST_STREAM_ADDRESS_RTSP = "rtsp://192.168.1.200:554";
	private static final String TEST_STREAM_ADDRESS_MJPEG = "http://192.168.1.200:80/axis-cgi/mjpg/video.cgi?subtype=1";
	private static final String TEST_USERNAME = "admin";
	private static final String TEST_PASSWORD = "123456";
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();

	@BeforeClass
	public static void setup() {
		VmsResponse response = sessionManager.wakeup();
		assertNotNull(response);
		assertEquals(response.getResponseCode(), CommonResponseCode.SUCCESS);
	}
	
	@Test
	public void test() {
		VideoCamera camera = new VideoCamera();
		camera.setId(0);
		camera.setName("Video Camera");
		camera.setModel("CP-PLUS");
		camera.setIp(TEST_IP);
		camera.setAnalyticStreamURL(TEST_STREAM_ADDRESS_MJPEG);
		camera.setMicroStreamURL(TEST_STREAM_ADDRESS_RTSP);
		camera.setMiniStreamURL(TEST_STREAM_ADDRESS_RTSP);
		camera.setMacroStreamURL(TEST_STREAM_ADDRESS_RTSP);
		camera.setStreamingUsername(TEST_USERNAME);
		camera.setStreamingPassword(TEST_PASSWORD);
		camera.setControlUsername(TEST_USERNAME);
		camera.setControlPassword(TEST_PASSWORD);
		
//		VmsResponse response = sessionManager.addCamera(camera);
//		assertNotNull(response);
//		assertEquals(response.getResponseCode(), CommonResponseCode.SUCCESS);
	}

}
