package com.dss.vms.video.reorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.common.interfaces.MediaServer;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.GrabberResponseCode;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author jdeveloper
 */
@SuppressWarnings("deprecation")
public class MediaServerImpl extends Observable implements Observer, MediaServer {

	private Map<Integer, CameraStreamGrabber> currentCameraMap = new ConcurrentHashMap<Integer, CameraStreamGrabber>();

	@Override
	public VmsResponse addCamera(VideoCamera... cameras) {
		VmsResponse response = new VmsResponse();
		if (cameras == null || cameras.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera/Camera List an not be null or empty...");
			return response;
		}

		/* Making default as success, will be updated on any failure */
		response.setResponseCode(CommonResponseCode.SUCCESS);

		List<Integer> newCamIds = new ArrayList<>(cameras.length);
		/* Initializing all grabber --> success only on buffer alocation. */
		for (VideoCamera newCamera : cameras) {

			/* check for duplicate camera id */
			if (currentCameraMap.containsKey(newCamera.getId())) {
				LOGGER.warn("Duplicate Camera Additin request ... Camera already added having camera id ["
						+ newCamera.getId() + "], Ignoring re initialization... ");
				continue;
			}

			try {
				CameraStreamGrabber grabber = new CameraStreamGrabber(newCamera);
				grabber.addObserver(this);
				currentCameraMap.put(newCamera.getId(), grabber);
				newCamIds.add(newCamera.getId());
			} catch (VmsCommonException e) {
				LOGGER.error("Failed to create Stream Grabber for camera \n[" + newCamera + "]\n, Error - " + e);
				System.err.println("Failed to create Stream Grabber for camera \n[" + newCamera + "]\n, Error - " + e);

				response = new VmsResponse(e);
				response.setMessage("Failed to create Stream Grabber for camera \n[" + newCamera + "]\n, Error - " + e);
				break;
			}
		}

		/*
		 * Start all grabber when all camera addition is success, or delete partly
		 * operation by closing the grabbers
		 */
		if (response.isSuccess()) {
			LOGGER.debug("Starting all grabbers for camera IDs " + newCamIds);
			for (Integer newlyAddedCameraId : newCamIds) {
				currentCameraMap.get(newlyAddedCameraId).startGrabbingAll();
			}
		} else {
			LOGGER.debug("Rollback for newly added camera as all camera not added successfully " + newCamIds);
			for (Integer newlyAddedCameraId : newCamIds) {
				currentCameraMap.get(newlyAddedCameraId).tearDown();
				currentCameraMap.get(newlyAddedCameraId).deleteObservers();
				currentCameraMap.remove(newlyAddedCameraId);
			}
		}

		return response;
	}

	@Override
	public VmsResponse deleteCamera(Integer... cameraIDs) {
		VmsResponse response = new VmsResponse();
		if (cameraIDs == null || cameraIDs.length == 0) {
			response.setError(CommonResponseCode.NULL_OR_EMPTY_PARAMETER_VALUE,
					"Camera IDs can not be null or empty...");
			return response;
		}

		/* Making default as success, will be updated on any failure */
		response.setResponseCode(CommonResponseCode.SUCCESS);

		for (Integer cameraId : cameraIDs) {
			/* check for duplicate camera id */
			if (!currentCameraMap.containsKey(cameraId)) {
				LOGGER.warn("Invalid Camera Deleet Request ... No such cameraavailable having camera id [" + cameraId
						+ "], Ignoring Deletion... ");
				continue;
			}

			LOGGER.debug("Stopping all grabbers for camera ID " + cameraId);
			CameraStreamGrabber cameraStreamGrabber = currentCameraMap.get(cameraId);
			cameraStreamGrabber.deleteObservers();
			cameraStreamGrabber.tearDown();
			currentCameraMap.remove(cameraId);
			LOGGER.info("Success to remove camera havingcamera ID [" + cameraId + "]");

		}

		return response;

	}

	@Override
	public VmsResponse pauseCamera(Integer... cameraIDs) {
		if (cameraIDs == null || cameraIDs.length == 0) {
			return new VmsResponse(CommonResponseCode.ERROR);
		}

		for (int index = 0; index < cameraIDs.length; index++) {
			System.out.println("Pause Camera in Media Server Pause Camera " + cameraIDs[index]);
			currentCameraMap.get(cameraIDs[index]).pauseGrabberAll();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public VmsResponse resumeCamera(Integer... cameraIDs) {
		// TODO Auto-generated method stub
		if (cameraIDs == null || cameraIDs.length == 0) {
			return new VmsResponse(CommonResponseCode.ERROR);
		}

		for (int index = 0; index < cameraIDs.length; index++) {
			currentCameraMap.get(cameraIDs[index]).resumeGrabberAll();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public void update(Observable sender, Object data) {
		notify(data);
	}

	@Override
	public VmsResponse getMediaHeader(Integer cameraID, StreamType streamType) {
		if (currentCameraMap.containsKey(cameraID)) {
			return currentCameraMap.get(cameraID).getMediaHeader(streamType);
		} else {
			return new VmsResponse(GrabberResponseCode.CAMERA_NOT_PRESENT,
					"No such cameraavailable having camera id [" + cameraID + "]");
		}
	}

	@Override
	public VmsResponse changeRecordingStream(Integer cameraID, StreamType streamType) {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	/**
	 * @param object
	 */
	private void notify(Object object) {
		this.setChanged();
		notifyObservers(object);
	}

	/**
	 * @return @SessionManager instance
	 */
	public static MediaServer getInstance() {
		if (INSTANCE == null) {
			synchronized (MediaServerImpl.class) {
				if (INSTANCE == null) {
					INSTANCE = new MediaServerImpl();
				}
			}
		}
		return INSTANCE;
	}

	private static MediaServer INSTANCE;
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaServerImpl.class);

	private MediaServerImpl() {
	}

}
