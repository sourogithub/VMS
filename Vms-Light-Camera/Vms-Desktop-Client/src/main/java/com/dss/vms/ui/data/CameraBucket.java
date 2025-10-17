package com.dss.vms.ui.data;

import java.util.ArrayList;
import java.util.List;

import com.dss.vms.common.data.VideoCamera;
/**
 * 
 * @author dss-02
 *
 */
public class CameraBucket {

	private List<VideoCamera> cameras;

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
	public void putCameras(List<VideoCamera> cameras) {
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
	 * @param cameraId
	 * @return
	 */
	public VideoCamera getCamera(int cameraId) {
		for(VideoCamera camera : cameras) {
			if(camera.getId() == cameraId) return camera;
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
