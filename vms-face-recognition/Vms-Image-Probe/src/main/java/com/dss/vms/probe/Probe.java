package com.dss.vms.probe;
import java.awt.Point;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.common.constants.AnalyticCommand;
import com.dss.vms.common.constants.AnalyticType;
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
	
	private static final int FRAME_DELAY = 200;
	
	private static final boolean EVENT_SEARCH_ENABLED = true;

	// probe master instance
	private static ProbeMaster probeMaster = ProbeMaster.getInstance();
	private static EventProcessor eventProcessor = EventProcessor.getInstance();

	// data buffers
	private ByteBuffer mappingBuffer = null;
	private ByteBuffer regionBuffer = null;
	private ByteBuffer imageBuffer = null;
	private ByteBuffer eventBuffer = null;
	private ByteBuffer commandBuffer = null;
	
	// allocation size
	private int allocatedImageBufferSize = INITIAL_IMAGE_SIZE + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE;
	private int allocatedEventBufferSize = INITIAL_IMAGE_SIZE + SIZE_OF_BUFFER_SIZE;
	private int allocatedCommandBufferSize = INITIAL_COMMAND_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE;
	
	// start marker
	private boolean analyticRequested = false;
	private long lastUpdateFrame = 0;
	
	public Probe() {}

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
		eventBuffer = ByteBuffer.allocateDirect(INITIAL_EVENT_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE + FLAG_SIZE)
				.order(ByteOrder.nativeOrder());
		commandBuffer = ByteBuffer.allocateDirect(INITIAL_COMMAND_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE)
				.order(ByteOrder.nativeOrder());
		
		// putting the size of mapping buffer
		mappingBuffer.putInt(MAPPING_BUFFER_SIZE);
		mappingBuffer.rewind();

		// putting the size of imagebuffer and event buffer
		eventBuffer.rewind();
		eventBuffer.putInt(INITIAL_EVENT_BUFFER_SIZE);
		imageBuffer.rewind();
		imageBuffer.putInt(INITIAL_IMAGE_SIZE);
		
		//command buffer 
		commandBuffer.putInt(INITIAL_COMMAND_BUFFER_SIZE); 
		
		// setting allocation size
		allocatedEventBufferSize = INITIAL_EVENT_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE + FLAG_SIZE;
		allocatedImageBufferSize = INITIAL_IMAGE_SIZE + TIMESTAMP_SIZE + SIZE_OF_BUFFER_SIZE;
		allocatedCommandBufferSize = INITIAL_COMMAND_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE;
		
		int res = probeMaster.addStream(mappingBuffer);
		if (res == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to set region in Probemaster");
		}
		return response;
	}
	
	
	public VmsResponse trainData(Employee empl) {
		VmsResponse response = new VmsResponse();
		MediaFrame[] frames = empl.getFaces();
		
		if (frames != null && frames.length > 0) {
			short empNameLen = (short) empl.getEmployeeName().length();
			short empIdLen = (short) empl.getEmployeeId().length();
			short empGendLen = (short) empl.getEmployeeGender().length();

			// calculating buffer allocation size
			int bufferSize = SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE + EMP_NAME_SIZE + empNameLen;
			bufferSize += EMP_ID_SIZE + empIdLen + EMP_GENDER_SIZE + empGendLen;
			bufferSize += EMP_DOB + NO_OF_IMAGE_SIZE;

			for (MediaFrame frame : frames) {
				bufferSize += IMAGE_SIZE + frame.getRawFrame().length;
			}

			// if size is less then allocate new buffer
			if (bufferSize > allocatedCommandBufferSize) {
				LOGGER.info("Data buffer size receeded, " + allocatedCommandBufferSize + ", allocating new buffer size "
						+ bufferSize);
				commandBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
				allocatedCommandBufferSize = bufferSize;
			}
			
			commandBuffer.rewind();
			commandBuffer.putInt(bufferSize - SIZE_OF_BUFFER_SIZE);
			commandBuffer.putLong(System.currentTimeMillis());
			commandBuffer.putShort(empNameLen).put(empl.getEmployeeName().getBytes());
			commandBuffer.putShort(empIdLen).put(empl.getEmployeeId().getBytes());
			commandBuffer.putShort(empGendLen).put(empl.getEmployeeGender().getBytes());
			commandBuffer.putLong(empl.getEmployeeDoB().getTime());
			commandBuffer.putShort((short) frames.length);

			for (MediaFrame frame : frames) {
				byte[] rawFrame = frame.getRawFrame();
				commandBuffer.putInt(rawFrame.length);
				commandBuffer.put(rawFrame);
			}

			// calling execute command
			int status = probeMaster.execute(mappingBuffer, (int) AnalyticCommand.FR_TRAIN_DATASET.value(), commandBuffer);
			if (status == SUCCESS) {
				response.setResponseCode(CommonResponseCode.SUCCESS);
				LOGGER.info("Success to execute training routine.");
			} else {
				response.setError(CommonResponseCode.ERROR, "Error occured , Native-Return : " + status);
				LOGGER.error("Error occured , Native Return : " + status);
			}
		} else {
			response.setError(CommonResponseCode.ERROR, "No Image provided.");
		}

		return response;
	}
	
	public VmsResponse deleteData(Employee empl) {
		VmsResponse response = new VmsResponse();
		String employeeId = empl.getEmployeeId();

		if (employeeId.equals(Employee.NOT_AVAILABLE)) {
			response.setError(CommonResponseCode.ERROR, "No employee id provided");

		} else {

			short empIdLen = (short) empl.getEmployeeId().length();

			int bufferSize = SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE + EMP_ID_SIZE + empIdLen;

			if (bufferSize > allocatedCommandBufferSize) {
				LOGGER.info("Data buffer size receeded, " + allocatedCommandBufferSize + ", allocating new buffer size "
						+ bufferSize);
				commandBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
				allocatedCommandBufferSize = bufferSize;
			}

			commandBuffer.rewind();
			commandBuffer.putInt(bufferSize - SIZE_OF_BUFFER_SIZE);
			commandBuffer.putLong(System.currentTimeMillis());
			commandBuffer.putShort(empIdLen).put(empl.getEmployeeId().getBytes());

			// calling execute command
			int status = probeMaster.execute(mappingBuffer, (int) AnalyticCommand.FR_TRAIN_DATASET.value(), commandBuffer);
			if (status == SUCCESS) {
				response.setResponseCode(CommonResponseCode.SUCCESS);
				LOGGER.info("Success to execute delete-dataset routine.");
			} else {
				response.setError(CommonResponseCode.ERROR, "Error occured , Native-Return : " + status);
				LOGGER.error("Error occured , Native Return : " + status);
			}
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
		int deleteResponse = probeMaster.removeStream(mappingBuffer);
		if (deleteResponse == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
			System.out.println("Success to delete camera from ImageProbe...");
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to delete camera in probe Master");
			System.err.println("Failed to delete camera in probe Master : native Return - " + deleteResponse);
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
						+ noOfRegions * (NO_OF_POINTS_MARKER_SIZE + PARAM_SIZE) + totalPointsSize))
				.order(ByteOrder.nativeOrder());

		/* setting buffer according to multiRegion buffer structure */
		regionBuffer.putInt(sizeofBuffer); /* SIZE OF THE BUFFER */
		System.err.println("SizeofBuffer -" + sizeofBuffer);

		regionBuffer.putShort((short) regions.size()); /* NO OF REGIONS */
		System.err.println("REGION SIZE -" + (short) regions.size());

		for (Region region : regions) {
			regionBuffer.put((byte) region.getControlPoint().size()); /* NO OF POINTS IN EACH REGION */
			System.err.println("no of regions : " + (byte) region.getControlPoint().size());

			ArrayList<Point> controlPoints = (ArrayList<Point>) region.getControlPoint();

			for (Point currentPoint : controlPoints) {
				regionBuffer.putInt(currentPoint.x); /* POINT X VALUE */
				regionBuffer.putInt(currentPoint.y); /* POINT Y VALUE */
			}
			regionBuffer.position(regionBuffer.position() + PARAM_SIZE); /* skipping 16 bytes for PARAMETER MARKER */
		}

		int setRegResponse = probeMaster.setRegion(mappingBuffer, regionBuffer);
		if (setRegResponse == SUCCESS) {
			response.setResponseCode(CommonResponseCode.SUCCESS);
			System.out.println("Success to setRegion in probe master...");
		} else {
			response.setError(CommonResponseCode.ERROR, "Failed to set region in Probemaster");
			System.err.println(response.getMessage());
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
		if (analyticRequested) {
			response.setError(CommonResponseCode.ERROR, "Analytic already stared [ CamID = " + cameraID + " ]");
			System.out.println("Analyitc already started on the Running Cam");
		} else {
			analyticRequested = true;
		}
		return response;
	}

	public VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		if (!analyticRequested) {
			response.setError(CommonResponseCode.ERROR,
					"Analytics already stopped on Cam [ CamID = " + cameraID + " ]");
			System.out.println("Analyitc already stopped on the Running Cam [ CamID = " + cameraID + " ]");
		} else {
			if (probeMaster != null) {
				analyticRequested = false;
			} else {
				response.setError(CommonResponseCode.ERROR, "analytic start Failed");
				LOGGER.debug("Stop analytic failed.. PROBE = NULL");
			}
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
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		if(lastUpdateFrame != 0 && (frame.getTimestamp() - lastUpdateFrame) <= FRAME_DELAY) {
			response.setResponseCode(CommonResponseCode.WARNING);
			return response;
		}
		
		lastUpdateFrame = frame.getTimestamp();
		
		if (analyticRequested) {
			int sizeofimage = frame.getRawFrame().length;

			/* checking whether the image received is empty */
			if (sizeofimage != 0) {
				if ((allocatedImageBufferSize) < (sizeofimage + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE)) {
					// if the allocated size recedes then we allocate new buffer
					imageBuffer = ByteBuffer.allocateDirect(sizeofimage + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE)
							.order(ByteOrder.nativeOrder());
					allocatedImageBufferSize = sizeofimage + SIZE_OF_BUFFER_SIZE + TIMESTAMP_SIZE;
					System.out.println("Allocating new Image buffer memory [Size - " + allocatedImageBufferSize + " ]");
				}

				// adding event Buffer allocation marker
				eventBuffer.position(0);
				eventBuffer.putInt(allocatedEventBufferSize - SIZE_OF_BUFFER_SIZE);

				// adding image-buffer markers
				imageBuffer.position(0);
				imageBuffer.putInt(sizeofimage);
				imageBuffer.putLong(frame.getTimestamp());
				imageBuffer.put(frame.getRawFrame());

				int searchResponse = probeMaster.searchEvent(mappingBuffer, imageBuffer, eventBuffer);
				if (searchResponse == SUCCESS) {
					eventBuffer.rewind();
					int bufferSize = eventBuffer.getInt();
					byte eventFlag = eventBuffer.get();

					System.out.println("got event buffer size - " + bufferSize);
					byte[] rawData = new byte[bufferSize];

					eventBuffer.rewind();
					/* skipping initial size marker */
					eventBuffer.position(SIZE_OF_BUFFER_SIZE + FLAG_SIZE); 
					eventBuffer.get(rawData);
					
					if (eventFlag > 0) {
						AnalyticType analytic = AnalyticType.find((short) eventFlag);
						
						if (analytic == AnalyticType.FACE) {
							try {
//								int eventCount = eventBuffer.getInt();
//								List<FaceRecognitionEvent> events = new ArrayList<>();
//								/* multi-event support */
//								for (int eventIndex = 0; eventIndex < eventCount; eventIndex++) {
								
								MediaFrame eventFrame = (MediaFrame) frame.clone();
								eventFrame.setRawFrame(rawData);

								/** employee id **/
								short idSize = eventBuffer.getShort();
								System.err.println("got id size : " + idSize);
								byte[] empidBytes = new byte[idSize];
								eventBuffer.get(empidBytes);

								/** employee name **/
								short empNameSize = eventBuffer.getShort();
								System.err.println("got name size : " + empNameSize);
								byte[] empNamebytes = new byte[empNameSize];
								eventBuffer.get(empNamebytes);

								/** employee DoB **/
								//long dobTimestamp = eventBuffer.getLong();
								
								/** employee gender **/
//								byte[] gender = new byte[2];
//								eventBuffer.get(gender);

								Employee employee = new Employee();
								employee.setEmployeeId(new String(empidBytes));
								employee.setEmployeeName(new String(empNamebytes));
								//employee.setEmployeeDoB(new Date(dobTimestamp));
								employee.setFaces(eventFrame);
								
								System.err.println("employee : " + employee);
								
								FaceRecognitionEvent event = new FaceRecognitionEvent(System.currentTimeMillis(), true);
								event.setEmployee(employee);

//									events.add(event);
//								}

								response.setResponse(event);

								if (EVENT_SEARCH_ENABLED) { processEvent(event); }

							} catch (CloneNotSupportedException e) {
								String logMessage = "Failed to clone Media-Frame. Error - " + e;
								response.setError(CommonResponseCode.ERROR, logMessage);
								LOGGER.error(logMessage);
							}
						}
					}
					
				} else if (searchResponse == INSUFICIENT_MEMORY) { /* insufficient event buffer memory TESTING */
					System.out.println("Insufficient Event buffer memory [ Size - " + allocatedEventBufferSize + " ]");
					eventBuffer.position(0);
					int newEventBufferSize = eventBuffer.getInt();
					allocatedEventBufferSize = newEventBufferSize + SIZE_OF_BUFFER_SIZE + FLAG_SIZE;

					// allocating new size
					System.out.println("Allocating new Event buffer memory [Size - " + allocatedEventBufferSize + " ]");
					eventBuffer = ByteBuffer.allocateDirect(allocatedEventBufferSize).order(ByteOrder.nativeOrder());
					eventBuffer.putInt(newEventBufferSize);
					eventBuffer.position(0);

					response.setError(CommonResponseCode.ERROR,
							"ERROR ENCOUNTERED : Native response -" + searchResponse);
				} else if (searchResponse != SUCCESS) {
					String logMessage = "ERROR ENCOUNTERED : Native response -" + searchResponse;
					System.err.println(logMessage);
					response.setError(CommonResponseCode.ERROR, logMessage);
				}
			}
			
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
	private boolean processEvent(FaceRecognitionEvent event) {
		boolean success = true;
		try {
			//pre-processing.
			File directory = new File("events");
			if (!directory.exists()) {
				if (!directory.mkdir()) return false;
			}
			
			eventProcessor.submitFrame(event);

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
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
