package com.dss.vms.master;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.master.DatabaseManagerImpl;
import com.dss.streaming.RemoteStreamingServer;
import com.dss.vms.analytics.data.AnalyticSession;
import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.AuditType;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.exceptation.ServerShutdownException;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.interfaces.ImageProbe;
import com.dss.vms.common.interfaces.MediaServer;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.Authorization;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.SessionManagerResponseCode;
import com.dss.vms.common.response.UserType;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.web.WebServer;
import com.dss.vms.probe.ImageProbeImpl;
import com.dss.vms.video.reorder.MediaServerImpl;

/**
 * @author jdeveloper
 */
public class SessionManagerImpl extends Observable implements Observer, SessionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerImpl.class);
	
	/**
	 * Temporary used to generate camera ID until the RDBMS is available
	 */
	private static AtomicInteger cameraID = new AtomicInteger(0);

	private static final boolean PROBE_ENABLE = true;
	private static final boolean STORAGE_MANAGER_ENABLE = true;
	private static final boolean SESSION_STORE_ENABLE = true;
	private static final boolean WEB_SERVER_ENABLE = false;
	private static final boolean REMOTE_STREAMING_SERVER_ENABLE = false;
	
	/* CMap<CamID, VideoCamera> */
	private Map<Integer, VideoCamera> cameraMap = new ConcurrentHashMap<Integer, VideoCamera>();

	/* Map<CamID, Map<AnalyticType, Region>> */
	private Map<Integer, Map<AnalyticType, AnalyticSession>> analyticSessionMap = new HashMap<Integer, Map<AnalyticType, AnalyticSession>>();

	private static MediaServer mediaServer = MediaServerImpl.getInstance();
	private static ImageProbe imageProbe;
	private static DatabaseManager dbManager = DatabaseManagerImpl.getInstance();
	private static WebServer webServer = WebServer.getInstance();
	private static StorageManager storageManager = StorageManager.getInstance();

	/**
	 * @return
	 */
	private int generateCamId() {
		return (cameraID.get() == Integer.MAX_VALUE) ? VideoCamera.INVALID_CAM_ID : cameraID.getAndIncrement();
	}

	@Override
	public void update(Observable sender, Object data) {
		notify(data);
	}

	/**
	 * @param object
	 */
	private void notify(Object object) {
		this.setChanged();
		notifyObservers(object);
	}

	@Override
	public VmsResponse addCamera(VideoCamera... cameras) {
		VmsResponse response = new VmsResponse();
		if (cameras == null || cameras.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera/Camera List an not be null or empty...");
			return response;
		}

		/* Generating Camera ID for each camera */
		for (VideoCamera newCamera : cameras) {
			int cameraId = generateCamId();
			if (cameraId == VideoCamera.INVALID_CAM_ID) {
				response.setError(SessionManagerResponseCode.CAMERA_ID_OUT_OF_BOUND,
						"Camera ID already crossed maximum limit, can not generate new camera ID");
				return response;
			} else {
				newCamera.setId(cameraId);
			}
		}

		response = mediaServer.addCamera(cameras);
		if (!response.isSuccess()) {
			LOGGER.error("Failed to add cameras to media server .. Ignoring further operation ...");
			return response;
		}

		/* Adding camera to Image probe one by one */
		for (VideoCamera newCamera : cameras) {
			if (PROBE_ENABLE) {
				VmsResponse probeResponse = imageProbe.addCamera(newCamera.getId());
				if (!probeResponse.isSuccess()) {
					String logMessage = "\nFailed to add cameras to image probe... No analytics will be available for the camera "
							+ "[\n" + newCamera + "\n] Error Details: " + probeResponse;
					response.setError(CommonResponseCode.WARNING, response.getMessage() + logMessage);
					LOGGER.error(logMessage);
				}
			}

			/* Adding camera to map irrespective of success or failure in image probe */
			cameraMap.put(newCamera.getId(), newCamera);
		
			/* Creating analytic Session for all available Analytic */
			Map<AnalyticType, AnalyticSession> cameraAnalyticSessionMap = new HashMap<AnalyticType, AnalyticSession>();
			
			for (AnalyticType type : AnalyticType.values()) {
				cameraAnalyticSessionMap.put(type, new AnalyticSession());
			}
			
			analyticSessionMap.put(newCamera.getId(), cameraAnalyticSessionMap);
		}
		

		return response;
	}

	@Override
	public VmsResponse startAnaytic(Integer cameraID, AnalyticType analyticType) {

		VmsResponse response = new VmsResponse();
		
		if (!cameraMap.containsKey(cameraID)) {
			response.setError(SessionManagerResponseCode.INVALID_CAMERA_ID,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + cameraMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());

			return response;
		}

		if (!analyticSessionMap.containsKey(cameraID) || analyticSessionMap.get(cameraID) == null
				|| !analyticSessionMap.get(cameraID).containsKey(analyticType)) {

			response.setError(SessionManagerResponseCode.INVALID_ANALYTIC_SESSION,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + analyticSessionMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());

			return response;
		}

		if (PROBE_ENABLE) {
			response = imageProbe.startAnaytic(cameraID, analyticType);
			if (response.isSuccess()) {
				LOGGER.info("Success to start [" + analyticType + "] for camera ID " + cameraID);
				analyticSessionMap.get(cameraID).get(analyticType).setEnable(true);
			}
		} else {
			response.setError(SessionManagerResponseCode.PROBE_NOT_ENABLE,
					"Image probe not available.. Please contact Admin...");
			LOGGER.error(response.toString());
		}

		return response;
	}

	@Override
	public VmsResponse setRegion(Integer cameraID, AnalyticType analyticType, List<Region> regions) {
		VmsResponse response = new VmsResponse();
		
		if (!cameraMap.containsKey(cameraID)) {
			response.setError(SessionManagerResponseCode.INVALID_CAMERA_ID,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + cameraMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());
			return response;
		}

		if (!analyticSessionMap.containsKey(cameraID) || analyticSessionMap.get(cameraID) == null
				|| !analyticSessionMap.get(cameraID).containsKey(analyticType)) {

			response.setError(SessionManagerResponseCode.INVALID_ANALYTIC_SESSION,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + analyticSessionMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());

			return response;
		}

		if (PROBE_ENABLE) {
			response = imageProbe.setRegion(cameraID, analyticType, regions);
			if (response.isSuccess()) {
				LOGGER.info("Success to set region for [" + analyticType + "] for camera ID [" + cameraID
						+ "], Regions: " + regions);
				analyticSessionMap.get(cameraID).get(analyticType).setRegions(regions);
			}
		} else {
			response.setError(SessionManagerResponseCode.PROBE_NOT_ENABLE,
					"Image probe not available.. Please contact Admin...");
			LOGGER.error(response.toString());
		}

		return response;
	}

	@Override
	public VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse();
		
		if (!cameraMap.containsKey(cameraID)) {
			response.setError(SessionManagerResponseCode.INVALID_CAMERA_ID,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + cameraMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());
			return response;
		}

		if (!analyticSessionMap.containsKey(cameraID) || analyticSessionMap.get(cameraID) == null
				|| !analyticSessionMap.get(cameraID).containsKey(analyticType)) {

			response.setError(SessionManagerResponseCode.INVALID_ANALYTIC_SESSION,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + analyticSessionMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());
			return response;
		}

		if (PROBE_ENABLE) {
			response = imageProbe.stopAnaytic(cameraID, analyticType);
			if (response.isSuccess()) {
				LOGGER.info("Success to stop [" + analyticType + "] for camera ID " + cameraID);
				analyticSessionMap.get(cameraID).get(analyticType).setEnable(false);
			}
		} else {
			response.setError(SessionManagerResponseCode.PROBE_NOT_ENABLE,
					"Image probe not available.. Please contact Admin...");
			LOGGER.error(response.toString());
		}

		return response;
	}

	@Override
	public VmsResponse deleteCamera(Integer... cameraIDs) {
		VmsResponse response = new VmsResponse();
		
		if (cameraIDs == null || cameraIDs.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera/Camera Ids an not be null or empty...");
			return response;
		}

		response = mediaServer.deleteCamera(cameraIDs);
		if (!response.isSuccess()) {
			LOGGER.error("Failed to delete cameras to media server .. Ignoring further operation ...Camera Ids "
					+ Arrays.toString(cameraIDs) + ", Error " + response);
			return response;
		}

		/* Adding camera to Image probe one by one */
		for (int deletedCamera : cameraIDs) {

			if (PROBE_ENABLE) {
				VmsResponse probeResponse = imageProbe.deleteCamera(deletedCamera);
				if (!probeResponse.isSuccess()) {
					String logMessage = "\nFailed to delete cameras from image probe... Error Details: "
							+ probeResponse;
					response.setError(CommonResponseCode.WARNING, response.getMessage() + logMessage);
					LOGGER.error(logMessage);
				}
			}

			/* Adding camera to map irrespective of success or failure in image probe */
			cameraMap.remove(deletedCamera);
			analyticSessionMap.remove(deletedCamera);
		}
		return response;
	}

	@Override
	public VmsResponse pauseCamera(Integer... cameraIDs) {
		VmsResponse response = new VmsResponse();
		
		if (cameraIDs == null || cameraIDs.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera/Camera Ids an not be null or empty...");
			return response;
		}

		response = mediaServer.pauseCamera(cameraIDs);

		if (response.isSuccess()) {
			LOGGER.info("camera successfully paused..");
		} else {
			LOGGER.error("Failed to pause cameras to media server .. Ignoring further operation ...Camera Ids "
					+ Arrays.toString(cameraIDs) + ", Error " + response);
		}
		return response;
	}

	@Override
	public VmsResponse resumeCamera(Integer... cameraIDs) {
		VmsResponse response = new VmsResponse();
		
		if (cameraIDs == null || cameraIDs.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera/Camera Ids an not be null or empty...");
			return response;
		}

		response = mediaServer.resumeCamera(cameraIDs);

		if (response.isSuccess()) {
			LOGGER.info("camera successfully resumed..");
		} else {
			LOGGER.error("Failed to pause cameras to media server .. Ignoring further operation ...Camera Ids "
					+ Arrays.toString(cameraIDs) + ", Error " + response);
		}

		return response;
	}

	@Override
	public VmsResponse getMediaHeader(Integer cameraID, StreamType streamType) {
		VmsResponse response = new VmsResponse();
		if (!cameraMap.containsKey(cameraID)) {
			response.setError(SessionManagerResponseCode.INVALID_CAMERA_ID,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + cameraMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());
			return response;
		}

		response = mediaServer.getMediaHeader(cameraID, streamType);
		LOGGER.error("Failed to getMediaHeader for camera [" + cameraID + "], Error " + response);
		return response;
	}

	@Override
	public VmsResponse changeRecordingStream(Integer cameraID, StreamType streamType) {
		VmsResponse response = new VmsResponse();
		if (!cameraMap.containsKey(cameraID)) {
			response.setError(SessionManagerResponseCode.INVALID_CAMERA_ID,
					"Invalid camera ID [" + cameraID + "]," + "\nAvailable camera Ids " + cameraMap.keySet()
							+ "\nAvailable Analytic Sessions " + analyticSessionMap.get(cameraID));

			LOGGER.error(response.toString());

			return response;
		}

		response = mediaServer.changeRecordingStream(cameraID, streamType);
		LOGGER.error("Failed to chnageRecordingStream for camera [" + cameraID + "], Error " + response);
		return response;
	}

	@Override
	public VmsResponse getRegion(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse();
		AnalyticSession currentSession = analyticSessionMap.get(cameraID).get(analyticType);
		response.setResponse((Serializable) currentSession.getRegions());
		return response;
	}

	@Override
	public VmsResponse isAnalyticStarted(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse();
		AnalyticSession currentSession = analyticSessionMap.get(cameraID).get(analyticType);
		
		if (currentSession.isEnable()) {
			response.setResponse(true);
			response.setMessage("analytic session started");
		} else {
			response.setResponse(false);
			response.setMessage("Analytic session not started");
		}
		return response;
	}

	@Override
	public VmsResponse getAllCameras() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		response.setResponse((Serializable) cameraMap);
		return response;
	}

	@Override
	public VmsResponse sleep() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		if (SESSION_STORE_ENABLE) {
			serializateToXML(cameraMap, "camera_session.xml");
			serializateToXML(analyticSessionMap, "analytic_session.xml");
		}
		
		/** deleting cameras one by one **/
		for (VideoCamera camera : cameraMap.values()) {
			response = mediaServer.deleteCamera(camera.getId());
		}
		
		if (WEB_SERVER_ENABLE) {
			try {
				LOGGER.info("Shutting down web-server...");
				webServer.shutdown();
				LOGGER.info("Web-Server shutdown successfully..");
			} catch (ServerShutdownException e) {
				LOGGER.error("Error occured while shutting down webserver , Error : " + e.getMessage());
			}
		}
		
		if (STORAGE_MANAGER_ENABLE) {
			try {
				response = storageManager.shutdown();
			} catch (Exception e) {
				LOGGER.error("Error occured while shutting down Storage-Manager , Error - " + e);
				response.setError(CommonResponseCode.ERROR, "Error occured while shuttingdown storage-manager.");
			}
		}

		return response;
	}

	/**
	 * 
	 * @param objectToDump
	 * @param fileName
	 * @return
	 */
	private boolean serializateToXML(Object objectToDump, String fileName) {
		FileOutputStream fos = null;
		XMLEncoder encoder = null;

		try {
			fos = new FileOutputStream(fileName, false);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(objectToDump);
			fos.flush();
			return true;
		} catch (Exception e) {
			LOGGER.error("Error occured while serializing [" + fileName + "] , Error" + e);
		} finally {
			try { encoder.close(); } catch (Exception e) {}
			try { fos.close(); } catch (Exception e) {}
		}
		
		return false;
	}

	@Override
	public VmsResponse wakeup() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		try {
			if (SESSION_STORE_ENABLE) {
				Object cameraMapObj = deserializeFromXML("camera_session.xml");
				if (cameraMapObj != null) {
					cameraMap = (Map<Integer, VideoCamera>) cameraMapObj;
				}
				
				Object analyticSessionObj = deserializeFromXML("analytic_session.xml");
				if (analyticSessionObj != null) {
					analyticSessionMap = (Map<Integer, Map<AnalyticType, AnalyticSession>>) analyticSessionObj;
				}
				/* adding cameras one by one */
				if (cameraMap != null) {
					for (VideoCamera camera : cameraMap.values()) {
						this.addCamera(camera);
					}
				}
			}
			
			/**starting Web Server **/
			if (WEB_SERVER_ENABLE) {
				try {
					LOGGER.info("Trying to Start webserver...");
					webServer.start();
					LOGGER.info("Webserver started successfully...");
				} catch (Throwable e) {
					LOGGER.error("Error occured while starting web server , Error - "  + e);
				}
			}

			/** init storage manager **/
			if (STORAGE_MANAGER_ENABLE) {
				try { response = storageManager.initialise(); } 
				catch (Exception e) { LOGGER.error("Error occured while initialising Storage-Manager , Error - " + e); }
			}
			
			/** remote-streaming server **/
			if (REMOTE_STREAMING_SERVER_ENABLE) {
				RemoteStreamingServer streamingServer = new RemoteStreamingServer();
				((SessionManagerImpl) this).addObserver(streamingServer);
			}
			
		} catch (Throwable e) {
			response.setError(CommonResponseCode.ERROR, "Error occured while waking up session-manager , Error - " + e.getMessage());
		}
		
		return response;
	}

	/**
	 * 
	 * @param objectToSet
	 * @param fileName
	 * @return
	 */
	private Object deserializeFromXML(String fileName) {
		XMLDecoder decoder = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		Object objToReturn = null;
		File inputFile = new File(fileName);

		if (inputFile.exists()) {
			try {
				fis = new FileInputStream(fileName);
				bis = new BufferedInputStream(fis);
				decoder = new XMLDecoder(bis);
				objToReturn = decoder.readObject();
				return objToReturn;
			} catch (Exception e) {
				LOGGER.error("Error occured while deserializing [" + fileName + "] , Error" + e);
			} finally {
				try {fis.close();} catch (Exception e) {}
				try {bis.close();} catch (Exception e) {}
				try {decoder.close();} catch (Exception e) {}
			}
		}
		return null;
	}

	/**
	 * @return @SessionManager instance
	 */
	public static SessionManager getInstance() {
		if (INSTANCE == null) {
			synchronized (SessionManagerImpl.class) {
				if (INSTANCE == null) {
					INSTANCE = new SessionManagerImpl();
				}
			}
		}
		
		return INSTANCE;
	}

	private static SessionManager INSTANCE;

	private SessionManagerImpl() {
		((MediaServerImpl) mediaServer).addObserver(this);

		if (PROBE_ENABLE) {
			imageProbe = ImageProbeImpl.getInstance();
			((MediaServerImpl) mediaServer).addObserver((ImageProbeImpl) imageProbe);
			((ImageProbeImpl) imageProbe).addObserver(this);
		}
	}

	@Override
	public VmsResponse fetchRecords(Integer cameraID, Long startTime, Long endTime) {
		return dbManager.getRecords(cameraID, startTime, endTime);
	}

	@Override
	public VmsResponse createAudit(AuditType type, String desc, Long time) {
		return dbManager.generateAuditLog(type, desc, time);
	}

	@Override
	public VmsResponse getAuditLog(Integer limit) {
		return dbManager.getAuditLog(limit);
	}

	@Override
	public VmsResponse getAuditLog(Long startTime, Long endTime) {
		return dbManager.getAuditLogBetween(startTime, endTime);
	}

	@Override
	public VmsResponse getAuditLog(AuditType type, Integer limit) {
		return dbManager.getAuditLogByType(type, limit);
	}

	@Override
	public VmsResponse getEventLog(Integer limit) {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse getEventLog(Long startTime, Long endTime) {
		return dbManager.getEventLogBetween(startTime, endTime);
	}

	@Override
	public VmsResponse getEventLog(AnalyticType type, Integer limit) {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse authenticate(String username, String password) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		response.setResponse(new Authorization(true, UserType.ADMIN));
		return response;
	}

}
