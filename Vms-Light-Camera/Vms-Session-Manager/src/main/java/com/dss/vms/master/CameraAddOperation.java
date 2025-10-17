package com.dss.vms.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.MediaServer;
import com.dss.vms.common.response.VmsResponse;

public class CameraAddOperation implements Runnable, AbstractOperation {
	private static final Logger LOGGER = LoggerFactory.getLogger(CameraAddOperation.class);
	
	private MediaServer mediaServer;
	private VideoCamera camera;
	private boolean stopRequested;
	
	public CameraAddOperation(MediaServer mediaServer, VideoCamera camera) {
		this.mediaServer = mediaServer;
		this.camera = camera;
		this.stopRequested = false;
	}

	@Override
	public void run() {
		VmsResponse response = mediaServer.addCamera(camera);
		boolean success = response.isSuccess();
		
		while (true) {
			
			synchronized (CameraAddOperation.class) {
				if(stopRequested) {
					LOGGER.info("Stopping operation Camera-Add operation for camera [" + camera.getIp() + "],"
							+ " CameraID [" + camera.getId() + "], Camera-Name [" + camera.getName() + "]");
					break;
				}
			}
			
			if (success) {
				LOGGER.info("Camera Added successfully, URL : [" + camera.getIp() + "], CameraID [" + camera.getId() + "],"
						+ " Camera-Name [" + camera.getName() + "]");
				break;
			}

			LOGGER.debug("Failed to add camera  [" + camera + "], CameraID [" + camera.getId() + "],"
					+ " Camera-Name [" + camera.getName() + "]"
					+ " camera to media-server, Retring camera connection after 3sec..");
			try { Thread.sleep(3000); } catch (InterruptedException ie) {}

			response = mediaServer.addCamera(camera);
			success = response.isSuccess();
		}
	}
	
	@Override
	public synchronized void stop() {
		this.stopRequested = true;
	}

}
