package com.dss.vms.ui.data;

import java.util.ArrayList;
import java.util.List;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.master.SessionManagerImpl;

/**
 * CameraBucket is local cache for all the cameras added to the system at
 * present. It is primarily used to reducing the calls to
 * {@linkplain SessionManagerImpl} and providing the information across all
 * UI-Components
 * 
 * @author dss-02
 *
 */
public class CameraBucket {

	private ArrayList<VideoCamera> cameras;

	private static CameraBucket INSTANCE = null;

	public static CameraBucket getInstance() {
		synchronized (CameraBucket.class) {
			if (INSTANCE == null) {
				INSTANCE = new CameraBucket();
			}
		}
		return INSTANCE;
	}

	private CameraBucket() {
		this.cameras = new ArrayList<>();
	}

	/**
	 * Add Camera to the list
	 * 
	 * @param camera
	 */
	public void putCamera(VideoCamera camera) {
		cameras.add(camera);
	}

	/**
	 * @param cameras the cameras to set
	 */
	public void putCamera(List<VideoCamera> cameras) {
		this.cameras.addAll(cameras);
	}

	/**
	 * delete Camera from the List
	 * 
	 * @param camera
	 */
	public void removeCamera(VideoCamera camera) {
		cameras.remove(camera);
	}
	
	/**
	 * remove all cameras
	 */
	public void removeAll() {
		cameras.removeAll(cameras);
	}
	
	/**
	 * get Particular Camera from the list
	 * 
	 * @param index
	 * @return
	 */
	public VideoCamera getCamera(int index) {
		if (cameras.size() <= index) {
			return null;
		}
		return cameras.get(index);
	}
	
	/**
	 * search channel id
	 * @param cameraId
	 * @return
	 */
	public VideoCamera searchChannelID(int cameraId) {
		if(cameraId == VideoCamera.INVALID_CAM_ID) return null;
		else {
			for (VideoCamera camera : cameras) {
				if (camera.getId() == cameraId) { return camera; }
			}
		}
		return null;
	}
	
	/***
	 * get All cameras
	 * @return
	 */
	public List<VideoCamera> getAllCameras() {
		return cameras;
	}

}
