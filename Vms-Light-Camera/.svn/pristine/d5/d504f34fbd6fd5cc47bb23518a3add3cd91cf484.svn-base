package com.dss.vms.common.interfaces;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author Sibendu-PC
 */
public interface MediaServer {

	/**
	 * @param cameras
	 * @return @VmsResponse
	 */
	VmsResponse addCamera(VideoCamera... cameras);

	/**
	 * @param cameraIDs
	 * @return @VmsResponse
	 */
	VmsResponse deleteCamera(Integer... cameraIDs);

	/**
	 * @param cameraIds
	 * @return
	 */
	VmsResponse pauseCamera(Integer... cameraIDs);

	/**
	 * @param cameraIDs
	 * @return
	 */
	VmsResponse resumeCamera(Integer... cameraIDs);

	/**
	 * @param cameraID
	 * @param streamType
	 * @return
	 */
	VmsResponse getMediaHeader(Integer cameraID, StreamType streamType);

	/**
	 * @param cameraID
	 * @param streamType
	 * @return
	 */
	VmsResponse changeRecordingStream(Integer cameraID, StreamType streamType);
}
