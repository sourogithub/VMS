package com.dss.vms.probe;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.common.constants.AnalyticCommand;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.interfaces.ImageProbe;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.common.NativeConstants;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.video.data.MediaFrame;

public class ImageProbeImpl extends Observable implements ImageProbe, Observer, NativeConstants, NativeRetun {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageProbe.class);

	// probe map contains entry for each Probe along with Camera ID
	private Map<Integer, Probe> probeMap = new ConcurrentHashMap<>();

	@Override
	public VmsResponse addCamera(Integer cameraID) {
		VmsResponse response = new VmsResponse();
		if (!probeMap.containsKey(cameraID)) {
			Probe probe = new Probe();
			VmsResponse probeAddResponse = probe.addCamera();
			if (probeAddResponse.isSuccess()) {
				// adding entry to probeMap
				probeMap.put(cameraID, probe);

				// adding observers
				probe.addObserver(this);

				response.setResponseCode(CommonResponseCode.SUCCESS);
			} else {
				LOGGER.error("Failed to add camera for CameraID [ {0} ]", cameraID);
				response.setError(CommonResponseCode.ERROR, " Failed to add camera for CameraID [" + cameraID + "]");
			}
		}
		return response;
	}

	@Override
	public VmsResponse deleteCamera(Integer... cameraID) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		for (Integer camID : cameraID) {
			if (probeMap.containsKey(camID)) {
				VmsResponse deleteResponse = probeMap.get(camID).deleteCamera();
				if (!deleteResponse.isSuccess()) {
					LOGGER.debug("failed to delete camera for Probe Master CameraID = [" + camID + "]");
				}

				// irrespective of delete Response delete the entry from probeMap
				probeMap.remove(camID);
			} else {
				LOGGER.debug("camera ID [ " + camID + " ] not present in ProbeMap");
			}
		}
		return response;
	}

	@Override
	public VmsResponse setRegion(Integer cameraID, AnalyticType analyticType, List<Region> regions) {
		VmsResponse response = new VmsResponse();
		if (probeMap.containsKey(cameraID)) {
			response = probeMap.get(cameraID).setRegion(analyticType, regions);
		} else {
			response.setError(CommonResponseCode.ERROR, "Camera ID not present in probemap...");
		}
		return response;
	}

	@Override
	public VmsResponse startAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		if (probeMap.containsKey(cameraID)) {
			response = probeMap.get(cameraID).startAnaytic(cameraID, analyticType);
		} else {
			response.setError(CommonResponseCode.ERROR, "Camera ID not present in probemap...");
		}
		return response;
	}

	@Override
	public VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		if (probeMap.containsKey(cameraID)) {
			response = probeMap.get(cameraID).stopAnaytic(cameraID, analyticType);
		} else {
			response.setError(CommonResponseCode.ERROR, "Camera ID not present in probemap...");
		}
		return response;
	}
	
	@Override
	public VmsResponse executeCommand(AnalyticCommand command, Employee data) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		
		Set<Integer> cameraIds = probeMap.keySet();
		
		if (cameraIds.size() > 0) {
			int cameraID = cameraIds.iterator().next();
			Probe probe = probeMap.get(cameraID);
			
			switch (command) {
			case FR_TRAIN_DATASET:
				// get any available camera and call execute
				response = probe.trainData(data);
				break;

			case FR_DELETE_DATASET:
				// delete employee data
				response = probe.deleteData(data);
				break;

			default:
				response.setError(CommonResponseCode.NOT_IMPLEMENTED,
						"Command :" + command + " is not available");
				break;
			}
		} else {
			response.setError(CommonResponseCode.ERROR, "No Cameras added to probe");
		}
		return response;
	}
	
	private static Set<String> recognisedIds = new HashSet<>();
	
	@Override
	public void update(Observable sender, Object data) {
		
		if (data instanceof MediaFrame) {
			MediaFrame frame = (MediaFrame) data;
			
			// send H264 data back.
			if(frame.getMediaType() == MediaType.H264) return;
			
			Integer channelID = frame.getChannelID();

			if (probeMap.containsKey(channelID)) {
				VmsResponse response = probeMap.get(channelID).searchEvent(frame);
				if (response.isSuccess()) {
					Serializable analyticData = response.getResponse();
					notify(analyticData);
					
					/** notify person's face only once **/
//					if(analyticData instanceof FaceRecognitionEvent) {
//						FaceRecognitionEvent faceEvent = (FaceRecognitionEvent) analyticData;
//						if(faceEvent.isRecognised()) {
//							String employeeId = faceEvent.getEmployee().getEmployeeId();
//							if(recognisedIds.contains(employeeId)) { return; }
//						}
//						notify(faceEvent);
//					}
					/*	else if(analyticData instanceof List) {
						List<GenericEvent> eventList = (List<GenericEvent>) analyticData;
						for(GenericEvent event : eventList) {
							notify(event);
						}
					} */
				}
			}
		} 
	}
	
	

	/**
	 * notify of all changes
	 * 
	 * @param data
	 */
	protected void notify(Object data) {
		this.setChanged();
		this.notifyObservers(data);
	}

	/**
	 * @return @SessionManager instance
	 */
	public static ImageProbe getInstance() {
		if (INSTANCE == null) {
			synchronized (ImageProbeImpl.class) {
				if (INSTANCE == null) {
					INSTANCE = new ImageProbeImpl();
				}
			}
		}
		return INSTANCE;
	}

	private static ImageProbe INSTANCE = null;
	private ImageProbeImpl() {}

}
