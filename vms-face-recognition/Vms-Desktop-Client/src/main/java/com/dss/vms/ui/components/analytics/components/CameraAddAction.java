package com.dss.vms.ui.components.analytics.components;

import com.dss.vms.common.data.VideoCamera;

public interface CameraAddAction {
	
	public void cameraAddFailed();
	
	public void cameraAddSuccess(VideoCamera camera);
	
}
