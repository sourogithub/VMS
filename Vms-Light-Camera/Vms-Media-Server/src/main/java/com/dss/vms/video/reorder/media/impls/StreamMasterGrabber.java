package com.dss.vms.video.reorder.media.impls;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.CommonConstant;
import com.dss.vms.common.constants.FrameType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.exceptation.NativeException;
import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.GrabberResponseCode;
import com.dss.vms.common.response.StreamMasterResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.jni.interfaces.StreamMaster;
import com.dss.vms.jni.interfaces.common.NativeConstants;
import com.dss.vms.jni.interfaces.common.NativeRetun;
import com.dss.vms.video.data.MediaFrame;
import com.dss.vms.video.reorder.media.AbstractStreamGrabber;

/**
 * @author Sibendu
 */
public class StreamMasterGrabber extends AbstractStreamGrabber implements NativeConstants, NativeRetun, CommonConstant {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStreamGrabber.class);
	private static StreamMaster streamMaster = StreamMaster.getInstance();
	private ByteBuffer mappingBuffer;
	private ByteBuffer dataBuffer;
	private boolean alive = true;
	private boolean pause = false;
	private MediaFrame latestHeader = null;
	
	public StreamMasterGrabber(String cameraModel, String url, String username, String password, StreamType streamType,
			int cameraID) throws VmsCommonException {
		super(cameraModel, url, username, password, streamType, cameraID);

		/* Allocating Buffers */
		mappingBuffer = ByteBuffer.allocateDirect(MAPPING_BUFFER_SIZE + SIZE_OF_BUFFER_SIZE)
				.order(ByteOrder.nativeOrder());
		dataBuffer = ByteBuffer.allocateDirect(INITIAL_ENCODED_FRAME_BUFFER + 4).order(ByteOrder.nativeOrder());

		mappingBuffer.position(0);
		mappingBuffer.putInt(MAPPING_BUFFER_SIZE);
		int addResponse = streamMaster.addStream(mappingBuffer, cameraModel, url, username, password, cameraID);

		if (addResponse == SUCCESS) {
			LOGGER.info("Success to add camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
					+ "], Username [" + username + "], Password [" + password + "]");
		} else {

			LOGGER.error("Failed to add camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
					+ "], Username [" + username + "], Password [" + password + "], Native Error - " + addResponse);

			throw new NativeException(StreamMasterResponseCode.FAILED_TO_ADD_STREAM,
					"Failed to add camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
							+ "], Username [" + username + "], Password [" + password + "]");

		}
	}

	@Override
	public synchronized VmsResponse pauseGrabber() {
		if (!this.pause) {
			this.pause = true;
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public synchronized VmsResponse resumeGrabber() {
		if (this.pause) {
			this.pause = false;
			this.notify();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public synchronized VmsResponse stopGrabber() {
		this.alive = false;
		if (this.pause) {
			this.notify();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public VmsResponse startGrabber() {
		Thread thread = new Thread(this);
		thread.setName("Stream-Master Grabber , Camera [" + url + "]");
		thread.start();
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public void run() {
		LOGGER.info("Started Stream Grabbing for : model [" + cameraModel + "], URL [" + url + "], Username ["
				+ username + "], Password [" + password + "]");

		while (alive) {
			try {
				/* If Pause */
				synchronized (this) {
					if ((this.pause)) {
						try {
							if (streamMaster.pauseStream(mappingBuffer) != SUCCESS) {
								System.err.println("Uanble to pause StreamGrabber library...");
							}
							this.wait();
						} catch (InterruptedException e) {
							System.err.println("Unable to pause channel controller " + e.toString());
						}
					}
				}
	
				dataBuffer.position(0);
				dataBuffer.putInt(dataBuffer.capacity() - SIZE_OF_BUFFER_SIZE);
	
				int grabResponse = streamMaster.getContent(mappingBuffer, dataBuffer);
	
				if (grabResponse == SUCCESS) {
					dataBuffer.position(0);
					int usedSize = dataBuffer.getInt();
	
					/* DO NOT CHANGE THE SEQUENCE */
	//				dataBuffer.position(dataBuffer.position() + DSSF_FLAG_LENGTH);
	//				int frameSize = dataBuffer.getInt();
	//				long timestampInMs = dataBuffer.getLong();
	//				dataBuffer.position(dataBuffer.position() + TIME_STAMP_NANO_SEC_LENGTH);
	//				dataBuffer.position(dataBuffer.position() + START_POINTER_LENGTH);
	//				byte mediaByte = dataBuffer.get();
	//				byte frameByte = dataBuffer.get();
	//				byte[] rawFrame = new byte[frameByte];
	//				dataBuffer.get(rawFrame);
	
	//				System.err.println("Success to get stream content .. Length " + rawFrame.length + ","
	//						+ " MediaType = " + MediaType.find(mediaByte) + ", FrameType= " + FrameType.find(frameByte));
	//				MediaFrame mediaFrame = new MediaFrame(FrameType.find(frameByte), cameraID, 
	//						MediaType.find(mediaByte), rawFrame, timestampInMs, streamType);
	
					// ----for temporary purpose----
					byte[] rawFrame = new byte[usedSize];
					dataBuffer.get(rawFrame);
	
					MediaFrame mediaFrame = new MediaFrame(FrameType.I_FRAME, cameraID, MediaType.H264,
							rawFrame, System.currentTimeMillis(), streamType, 0, 0);
	
					if (mediaFrame.getFrameType() == FrameType.H_FRAME) {
						LOGGER.info("New Connect Header Found ... " + mediaFrame);
						this.latestHeader = mediaFrame;
					}
	
					notify(mediaFrame);
	
				} else if (grabResponse == DATA_NOT_READY) {
					LOGGER.trace("Data not ready for streamMaster: model [" + cameraModel + "], URL [" + url
							+ "], Username [" + username + "], Password [" + password + "], Calling after 20 MS");
					try {
						Thread.currentThread();
						Thread.sleep(20);
					} catch (InterruptedException e) {
						LOGGER.error("" + e);
					}
				} else if (grabResponse == INSUFICIENT_MEMORY) {
	
					dataBuffer.position(0);
					int requiredBufferSize = dataBuffer.getInt();
					LOGGER.error("INSUFICIENT NATIVE MEMORY, Requested Size " + requiredBufferSize);
					dataBuffer = ByteBuffer.allocateDirect(requiredBufferSize + SIZE_OF_BUFFER_SIZE)
							.order(ByteOrder.nativeOrder());
				} 
				/* on network error */
				else if(grabResponse == IO_ERROR) { 
					
					LOGGER.error("IO_ERROR, Camera not connected, Native Return " + grabResponse);
					
					int deleteResponse = streamMaster.removeStream(mappingBuffer);
					if(deleteResponse == SUCCESS) {
						LOGGER.info("Success to delete camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
								+ "], Username [" + username + "], Password [" + password + "], Native Error - " + deleteResponse);
					} else if (deleteResponse != SUCCESS) {
						LOGGER.error("Failed to delete camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
								+ "], Username [" + username + "], Password [" + password + "], Native Error - " + deleteResponse);
					}
					
					while(alive) {
						try {
							int addResponse = streamMaster.addStream(mappingBuffer, cameraModel, url, username, password, cameraID);
							if (addResponse == SUCCESS) {
								LOGGER.info("Success to add camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
										+ "], Username [" + username + "], Password [" + password + "]");
								break;
							} else {
								LOGGER.error("Failed to add camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
										+ "], Username [" + username + "], Password [" + password + "], Native Error - " + addResponse);
							}
							
							LOGGER.info("Retrying connection to connect to camera after 3secs");
							Thread.currentThread();
							Thread.sleep(3000);
						} catch (Exception ie) {}
					}
					
				} else {
					LOGGER.error("Failed to get stream content from streamMaster: model [" + cameraModel + "], URL [" + url
							+ "], Username [" + username + "], Password [" + password + "], Native Error - "
							+ grabResponse);
				}
				
			} catch (Throwable  t) {
				LOGGER.error("Failed to grab stream [CameraID = " + url + " ] , Error " + t.getMessage());
			}
		}

		int deleteResponse = streamMaster.removeStream(mappingBuffer);

		if (deleteResponse != SUCCESS) {
			LOGGER.error("Failed to delete camera stream to streamMaster: model [" + cameraModel + "], URL [" + url
					+ "], Username [" + username + "], Password [" + password + "], Native Error - " + deleteResponse);
		}

		LOGGER.info("Ended Stream Grabbing for : model [" + cameraModel + "], URL [" + url + "], Username [" + username
				+ "], Password [" + password + "]");
	}

	@Override
	public VmsResponse getMediaHeader() {
		VmsResponse vmsResponse = new VmsResponse();
		if (latestHeader != null) {
			vmsResponse.setResponseCode(CommonResponseCode.SUCCESS);
			try {
				vmsResponse.setResponse((MediaFrame) latestHeader.clone());
			} catch (CloneNotSupportedException e) {
				vmsResponse.setResponse(latestHeader);
			}
		} else {
			vmsResponse.setResponseCode(GrabberResponseCode.NO_HEADER_FRAME_AVAILABLE);
		}
		return vmsResponse;
	}

}
