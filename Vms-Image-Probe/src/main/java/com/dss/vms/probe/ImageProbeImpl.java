package com.dss.vms.probe;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.AnalyticType;
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		if (probeMap.containsKey(cameraID)) {
			response = probeMap.get(cameraID).stopAnaytic(cameraID, analyticType);
		} else {
			response.setError(CommonResponseCode.ERROR, "Camera ID not present in probemap...");
		}
		return response;
	}

	@Override
	public void update(Observable sender, Object data) {
//		System.out.println(" # DATA FLOW # " + getClass().getSimpleName() + " Received data from " + sender + ", Data Type: " + data);
		if (data instanceof MediaFrame) {
			MediaFrame frame = (MediaFrame) data;
			Integer channelID = frame.getChannelID();

			if (probeMap.containsKey(channelID)) {
				VmsResponse response = probeMap.get(channelID).searchEvent(frame);
				if (response.isSuccess()) {
					notify(response.getResponse());
				}
			}
		} else {
			notify(data);
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

	private static ImageProbe INSTANCE;

	private ImageProbeImpl() {
	}

}
