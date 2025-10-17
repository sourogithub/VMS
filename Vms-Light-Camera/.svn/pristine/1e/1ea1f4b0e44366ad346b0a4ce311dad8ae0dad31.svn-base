package com.dss.vms.video.reorder.media;

import java.util.Observable;

import org.slf4j.Logger;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.response.VmsResponse;

/**
 * 
 * @author jdeveloper
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractStreamGrabber extends Observable implements Runnable {
	protected String cameraModel, url, username, password;
	protected StreamType streamType;
	protected int cameraID;

	/**
	 * @param cameraModel
	 * @param url
	 * @param username
	 * @param password
	 * @param streamType
	 * @param cameraID
	 */
	public AbstractStreamGrabber(String cameraModel, String url, String username, String password,
			StreamType streamType, int cameraID) {
		this.cameraModel = cameraModel;
		this.url = url;
		this.username = username;
		this.password = password;
		this.streamType = streamType;
		this.cameraID = cameraID;
	}

	/**
	 * @return
	 */
	public abstract VmsResponse pauseGrabber();

	/**
	 * @return
	 */
	public abstract VmsResponse resumeGrabber();

	/**
	 * @return
	 */
	public abstract VmsResponse stopGrabber();

	/**
	 * @return
	 */
	public abstract VmsResponse startGrabber();

	/**
	 * @return
	 */
	public abstract VmsResponse getMediaHeader();

	protected void notify(Object data) {
		this.setChanged();
		notifyObservers(data);
	}

}
