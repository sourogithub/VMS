package com.dss.vms.probe;

import java.awt.Point;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.ProbeMaster;
import com.dss.vms.jni.interfaces.common.NativeConstants;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.probe.process.EventProcessor;
import com.dss.vms.video.data.MediaFrame;

/**
 * This is the child class of imageProbeImpl and each Probe corresponds to
 * single camera stream.
 * 
 * @author dss-02
 *
 */
public class Probe extends Observable implements NativeConstants, NativeRetun {
	private static final Logger LOGGER = LoggerFactory.getLogger(Probe.class);

	private static final boolean EVENT_SEARCH_ENABLED = false;

	// probe master instance
	private static ProbeMaster probeMaster = ProbeMaster.getInstance();
	private static EventProcessor eventProcessor = EventProcessor.getInstance();

	// data buffers
	private ByteBuffer mappingBuffer = null;
	private ByteBuffer regionBuffer = null;
	private ByteBuffer imageBuffer = null;
	private ByteBuffer eventBuffer = null;

	// allocation size
	private int allocatedImageBufferSize = INITIAL_IMAGE_SIZE + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE;
	private int allocatedEventBufferSize = INITIAL_IMAGE_SIZE + SIZE_OF_BUFFER_SIZE;

	// start marker
	AtomicBoolean startAnalyticRequested = new AtomicBoolean(false);

	public Probe() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * add stream for Probe
	 * 
	 * @return
	 */
	public VmsResponse addCamera() {
		VmsResponse response = new VmsResponse();
		mappingBuffer = ByteBuffer.allocateDirect(MAPPING_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE)
				.order(ByteOrder.nativeOrder());
		imageBuffer = ByteBuffer.allocateDirect(INITIAL_IMAGE_SIZE + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE)
				.order(ByteOrder.nativeOrder());
		eventBuffer = ByteBuffer.allocateDirect(INITIAL_EVENT_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE + EVENT_FLAG_SIZE)
				.order(ByteOrder.nativeOrder());

		// putting the size of mapping buffer
		mappingBuffer.putInt(MAPPING_BUFFER_SIZE);
		mappingBuffer.rewind();

		// putting the size of imagebuffer and event buffer
		eventBuffer.rewind();
		eventBuffer.putInt(INITIAL_EVENT_BUFFER_SIZE);
		imageBuffer.rewind();
		imageBuffer.putInt(INITIAL_IMAGE_SIZE);
		// setting allocation size
		allocatedEventBufferSize = INITIAL_EVENT_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE + EVENT_FLAG_SIZE;
		allocatedImageBufferSize = INITIAL_IMAGE_SIZE + TIMESTAMP_SIZE + SIZE_OF_BUFFER_SIZE;

		int res = probeMaster.addStream(mappingBuffer);
		if (res == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to set region in Probemaster");
		}
		return response;
	}

	/**
	 * deleteCamera for the Probe
	 * 
	 * @return
	 */
	public VmsResponse deleteCamera() {
		VmsResponse response = new VmsResponse();
		// TODO Auto-generated method stub

		int deleteResponse = probeMaster.removeStream(mappingBuffer);
		if (deleteResponse == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
			LOGGER.error("Success to delete camera from ImageProbe...");
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to delete camera in probe Master");
			LOGGER.error("Failed to delete camera in probe Master : native Return : {} " , deleteResponse);
		}
		return response;
	}

	/**
	 * Set region in Probe
	 * 
	 * @param cameraID
	 * @param analyticType
	 * @param regions
	 * @return
	 */
	public VmsResponse setRegion(AnalyticType analyticType, List<Region> regions) {
		VmsResponse response = new VmsResponse();
		int noOfRegions = regions.size();
		int totalPointsSize = 0;

		for (Region region : regions) {
			totalPointsSize += region.getControlPoint().size() * 2 * 4;
		}
		int sizeofBuffer = (NO_OF_REGION_SIZE + noOfRegions * (NO_OF_POINTS_MARKER_SIZE + PARAM_SIZE)
				+ totalPointsSize);

		regionBuffer = ByteBuffer
				.allocateDirect((SIZE_OF_BUFFER_SIZE + NO_OF_REGION_SIZE
						+ totalPointsSize
						+ noOfRegions * (NO_OF_POINTS_MARKER_SIZE + PARAM_SIZE) ))
				.order(ByteOrder.nativeOrder());

		/* setting buffer according to multiRegion buffer structure */
		regionBuffer.putInt(sizeofBuffer); /* SIZE OF THE BUFFER */
		LOGGER.debug("SizeofBuffer -" + sizeofBuffer);

		regionBuffer.putShort((short) regions.size()); /* NO OF REGIONS */
		LOGGER.debug("REGION SIZE -" + (short) regions.size());

		for (Region region : regions) {
			ArrayList<Point> controlPoints = (ArrayList<Point>) region.getControlPoint();

			regionBuffer.put((byte) controlPoints.size()); /* NO OF POINTS IN EACH REGION */
			LOGGER.debug("no of regions : " + (byte) controlPoints.size());
			
			for (Point currPoint : controlPoints) {
				regionBuffer.putInt(currPoint.x); /* POINT X VALUE */
				regionBuffer.putInt(currPoint.y); /* POINT Y VALUE */
			}
			regionBuffer.position(regionBuffer.position() + PARAM_SIZE); /* skipping 16 bytes for PARAMETER MARKER */
		}

		int setRegResponse = probeMaster.setRegion(mappingBuffer, regionBuffer);
		if (setRegResponse == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
			LOGGER.info("Success to setRegion in probe master...");
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to set region in Probemaster");
			LOGGER.error(response.getMessage());
		}
		return response;
	}

	/**
	 * start Analytics for current Probe
	 * 
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	public VmsResponse startAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		if (startAnalyticRequested.get() == true) {
			response.setError(CommonResponseCode.ERROR, "Analytic already stared [ CamID = " + cameraID + " ]");
			LOGGER.error("Analyitc already started on the Running Cam");
			return response;
		} 
		
		startAnalyticRequested.set(true);
		return response;
	}

	public VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		if (startAnalyticRequested.get() == false) {
			final String errMessage = "Analytics already stopped on Cam [ CamID = " + cameraID + " ]";
			
			response.setError(CommonResponseCode.ERROR, errMessage);
			LOGGER.info(errMessage);
			return response;
		} 
		
		if (probeMaster != null) {
			startAnalyticRequested.set(false);
		} else {
			response.setError(CommonResponseCode.ERROR, "analytic start Failed");
			LOGGER.error("Stop analytic failed.. PROBE = NULL");
		}
		
		return response;
	}

	/**
	 * send data frame for event search
	 * 
	 * @param frame
	 * @return a response object containing Media-Frame if succesfull
	 */
	public VmsResponse searchEvent(MediaFrame frame) {
		VmsResponse response = new VmsResponse();

		if (startAnalyticRequested.get()) {
			int sizeofimage = frame.getRawFrame().length;

			/* checking whether the image received is empty */
			if (sizeofimage != 0) {
				if ((allocatedImageBufferSize) < (SIZE_OF_BUFFER_SIZE + sizeofimage + TIMESTAMP_SIZE)) {
					// if the allocated size recedes then we allocate new buffer
					imageBuffer = ByteBuffer.allocateDirect(sizeofimage + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE)
							.order(ByteOrder.nativeOrder());
					allocatedImageBufferSize = sizeofimage + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE;
					LOGGER.debug("Allocating new Image buffer memory [Size - " + allocatedImageBufferSize + " ]");
				}
				// adding event Buffer allocation marker
				eventBuffer.position(0);
				eventBuffer.putInt(allocatedEventBufferSize - SIZE_OF_BUFFER_SIZE);

				// adding image-buffer markers
				imageBuffer.position(0);
				imageBuffer.putInt(sizeofimage);
				imageBuffer.putLong(System.currentTimeMillis()); /* setting current timestamp */
				imageBuffer.put(frame.getRawFrame());

				int searchResponse = probeMaster.searchEvent(mappingBuffer, imageBuffer, eventBuffer);
				if (searchResponse == SUCCESS) {
					eventBuffer.rewind();
					int buffSize = eventBuffer.getInt();
					byte eventFlag = eventBuffer.get();

					LOGGER.debug("got event buffer size - " + buffSize);
					byte[] rawData = new byte[buffSize];
//					eventBuffer.rewind();
//					eventBuffer.position(SIZE_OF_BUFFER_SIZE + EVENT_FLAG_SIZE); /* skipping initial size marker */
					eventBuffer.get(rawData);

					if (EVENT_SEARCH_ENABLED) {
						processEvent(rawData, AnalyticType.find((short) eventFlag));
					}

					frame.setRawFrame(rawData);
					frame.setMediaType(MediaType.JPEG); /* sending JPEG frames for now */

					response.setResponseCode(CommonResponseCode.SUCCESS);
					response.setResponse(frame);

				} else if (searchResponse == INSUFICIENT_MEMORY
						|| searchResponse != SUCCESS) { /* insufficient event buffer memory TESTING */
					System.out.println("Insufficient Event buffer memory [ Size - " + allocatedEventBufferSize + " ]");
					eventBuffer.position(0);
					int newEventBufferSize = eventBuffer.getInt();
					allocatedEventBufferSize = newEventBufferSize + SIZE_OF_BUFFER_SIZE + EVENT_FLAG_SIZE;

					// allocating new size
					LOGGER.debug("Allocating new Event buffer memory [Size - " + allocatedEventBufferSize + " ]");
					eventBuffer = ByteBuffer.allocateDirect(allocatedEventBufferSize).order(ByteOrder.nativeOrder());
					eventBuffer.putInt(newEventBufferSize);
					eventBuffer.position(0);

					response.setError(CommonResponseCode.ERROR,
							"ERROR ENCOUNTERED : Native response -" + searchResponse);
				} else if (searchResponse == ERROR) {
					LOGGER.error("ERROR ENCOUNTERED : Native response -" + searchResponse);
					response.setError(CommonResponseCode.ERROR,
							"ERROR ENCOUNTERED : Native response -" + searchResponse);
				}
			}
			
			try { Thread.sleep(20); } catch (InterruptedException e) {}
			
		} else {
			response.setResponseCode(CommonResponseCode.WARNING);
			response.setMessage("Analytics not started current Probe");
		}

		return response;
	}

	/**
	 * Dumps the Event Frame recieved (For Testing Purpose)
	 * 
	 * @param filepath
	 * @param data
	 * @return
	 */
	private boolean processEvent(byte[] data, AnalyticType analyticType) {
		boolean success = false;
		try {
			File directory = new File("events");
			if (!directory.exists()) {
				boolean status = directory.mkdir();
				if (status == false)  return status;
			}

			Long currentTime = System.currentTimeMillis();
			String filePath = "events/Event" + (new Date(currentTime)).toLocaleString() + ".jpg";
			
			// notifying event data with current Timestamp
			eventProcessor.submitFrame(data, currentTime, filePath, analyticType);
			success = true;
		} catch (Exception e) {
			LOGGER.error("Error occured while processing event data Error : ", e);
		}
		
		return success;
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
}
