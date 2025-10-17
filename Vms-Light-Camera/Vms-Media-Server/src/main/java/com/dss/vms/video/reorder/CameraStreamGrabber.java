package com.dss.vms.video.reorder;

import java.util.Observable;
import java.util.Observer;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.common.response.GrabberResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.video.reorder.media.AbstractStreamGrabber;
import com.dss.vms.video.reorder.media.impls.HttpMjpegGrabber;
import com.dss.vms.video.reorder.media.impls.StreamMasterGrabber;

/**
 * @author Sibendu-PC
 */
public class CameraStreamGrabber extends Observable implements Observer {

	private VideoCamera videoCamera;
	private int cameraID;
	/**
	 * mainly for analytic purpose. suggested for HTTP MJPEG
	 */
	private AbstractStreamGrabber analyticStreamGrabber;
	private AbstractStreamGrabber miniStreamGrabber;
	private AbstractStreamGrabber macroStreamGrabber;
	private AbstractStreamGrabber microStreamGrabber;

	private static final boolean HTTP_MJPEG_SUPPORTED = true;
	private static final boolean RECORDING_SUPPORTED = true;
	
	/**
	 * @param videoCamera
	 * @throws VmsCommonException
	 */
	public CameraStreamGrabber(VideoCamera videoCamera) throws VmsCommonException {
		this.videoCamera = videoCamera;
		this.cameraID = videoCamera.getId();

		if (HTTP_MJPEG_SUPPORTED && videoCamera.getAnalyticStreamURL().contains("http")) {
			this.analyticStreamGrabber = new HttpMjpegGrabber(videoCamera.getModel(),
					videoCamera.getAnalyticStreamURL(), videoCamera.getStreamingUsername(),
					videoCamera.getStreamingPassword(), StreamType.ANALYTIC, videoCamera.getId());

			this.analyticStreamGrabber.addObserver(this);
		}
		
		if (RECORDING_SUPPORTED) {
			this.microStreamGrabber = new StreamMasterGrabber(videoCamera.getModel(),
					videoCamera.getMicroStreamURL(), videoCamera.getStreamingUsername(),
					videoCamera.getStreamingPassword(), StreamType.MICRO, videoCamera.getId());

			this.microStreamGrabber.addObserver(this);
		}
	}

	/**
	 * @return the videoCamera
	 */
	public VideoCamera getVideoCamera() {
		return videoCamera;
	}

	/**
	 * @return the cameraID
	 */
	public int getCameraID() {
		return cameraID;
	}

	/**
	 * pause all Grabbers
	 */
	public void pauseGrabberAll() {
		if (analyticStreamGrabber != null) {
			analyticStreamGrabber.pauseGrabber();
		}
		if (microStreamGrabber != null) {
			microStreamGrabber.pauseGrabber();
		}
		if (miniStreamGrabber != null) {
			miniStreamGrabber.pauseGrabber();
		}
		if (macroStreamGrabber != null) {
			macroStreamGrabber.pauseGrabber();
		}
	}
	
	/**
	 * Pause Grabber by stream type
	 * @param streamType
	 */
	public void pauseGrabber(StreamType streamType) {
		switch (streamType) {
		case ANALYTIC: 
			if(analyticStreamGrabber != null) analyticStreamGrabber.pauseGrabber();
			break;
		
		case MACRO:
			if(macroStreamGrabber != null) macroStreamGrabber.pauseGrabber();
			break;
		
		case MINI:
			if(miniStreamGrabber != null) miniStreamGrabber.pauseGrabber();
			break;
			
		case MICRO:
			if(microStreamGrabber != null) microStreamGrabber.pauseGrabber();
			break;
		}
	}

	/**
	 * Resume grabber all
	 */
	public void resumeGrabberAll() {
		if (analyticStreamGrabber != null) {
			analyticStreamGrabber.resumeGrabber();
		}
		if (microStreamGrabber != null) {
			microStreamGrabber.resumeGrabber();
		}
		if (miniStreamGrabber != null) {
			miniStreamGrabber.resumeGrabber();
		}
		if (macroStreamGrabber != null) {
			macroStreamGrabber.resumeGrabber();
		}
	}
	
	/**
	 * resume Grabber by stream type
	 * @param streamType
	 */
	public void resumeGrabber(StreamType streamType) {
		switch (streamType) {
		case ANALYTIC: 
			if(analyticStreamGrabber != null) analyticStreamGrabber.resumeGrabber();
			break;
		
		case MACRO:
			if(macroStreamGrabber != null) macroStreamGrabber.resumeGrabber();
			break;
		
		case MINI:
			if(miniStreamGrabber != null) miniStreamGrabber.resumeGrabber();
			break;
			
		case MICRO:
			if(microStreamGrabber != null) microStreamGrabber.resumeGrabber();
			break;
		}
	}

	/**
	 * @param object
	 */
	private void notify(Object object) {
		this.setChanged();
		notifyObservers(object);
	}

	@Override
	public void update(Observable sender, Object data) {
		notify(data);
	}

	public void startGrabbingAll() {
		if (analyticStreamGrabber != null)
			analyticStreamGrabber.startGrabber();
		if (miniStreamGrabber != null)
			miniStreamGrabber.startGrabber();
		if (macroStreamGrabber != null)
			macroStreamGrabber.startGrabber();
		if (microStreamGrabber != null)
			microStreamGrabber.startGrabber();
	}

	public void tearDown() {
		if (analyticStreamGrabber != null) {
			analyticStreamGrabber.deleteObservers();
			analyticStreamGrabber.stopGrabber();
		}

		if (miniStreamGrabber != null) {
			miniStreamGrabber.deleteObservers();
			miniStreamGrabber.stopGrabber();
		}

		if (macroStreamGrabber != null) {
			macroStreamGrabber.deleteObservers();
			macroStreamGrabber.stopGrabber();
		}

		if (microStreamGrabber != null) {
			microStreamGrabber.deleteObservers();
			microStreamGrabber.stopGrabber();
		}
	}

	public VmsResponse getMediaHeader(StreamType streamType) {
		VmsResponse vmsResponse = new VmsResponse();
		switch (streamType) {
		case ANALYTIC:
			if (analyticStreamGrabber != null) {
				vmsResponse = analyticStreamGrabber.getMediaHeader();
			} else {
				vmsResponse.setError(GrabberResponseCode.STREAM_NOT_AVAILABLE, streamType + " NOT Supported yet ..");
			}
			break;
		case MACRO:
			if (macroStreamGrabber != null) {
				vmsResponse = macroStreamGrabber.getMediaHeader();
			} else {
				vmsResponse.setError(GrabberResponseCode.STREAM_NOT_AVAILABLE, streamType + " NOT Supported yet ..");
			}
			break;
		case MICRO:
			if (microStreamGrabber != null) {
				vmsResponse = microStreamGrabber.getMediaHeader();
			} else {
				vmsResponse.setError(GrabberResponseCode.STREAM_NOT_AVAILABLE, streamType + " NOT Supported yet ..");
			}
			break;
		case MINI:
			if (miniStreamGrabber != null) {
				vmsResponse = miniStreamGrabber.getMediaHeader();
			} else {
				vmsResponse.setError(GrabberResponseCode.STREAM_NOT_AVAILABLE, streamType + " NOT Supported yet ..");
			}
			break;
		default:
			vmsResponse.setError(GrabberResponseCode.STREAM_NOT_SUPPORTED, streamType + " NOT Supported yet ..");
			break;
		}
		return vmsResponse;
	}

}
