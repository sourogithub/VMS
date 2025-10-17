package com.dss.vms.jni.interfaces;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.jni.interfaces.common.NativeLibrary;

/**
 * 
 * @author jdeveloper
 *
 */
public class StreamMaster implements NativeLibrary {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamMaster.class);
	private static StreamMaster INSTANCE;
	private static final String LIBRARY_NAME = "StreamMaster";

	public static StreamMaster getInstance() {
		if (INSTANCE == null) {
			synchronized (StreamMaster.class) {
				if (INSTANCE == null) {
					INSTANCE = new StreamMaster();
					int initReturn = INSTANCE.initialize();
					if (initReturn == SUCCESS) {
						LOGGER.info("Success to initialize [" + LIBRARY_NAME + "]...");
					} else {
						LOGGER.info("Failed to initialize [" + LIBRARY_NAME + "]...Native Error - " + initReturn);
					}
				}
			}
		}
		return INSTANCE;
	}

	static {
		try {
			System.loadLibrary(LIBRARY_NAME);
			LOGGER.info("Success to load native library [" + LIBRARY_NAME + "]...");
		} catch (UnsatisfiedLinkError e) {
			LOGGER.error("Failed to load native library [" + LIBRARY_NAME + "]...Error - " + e);
		} catch (Exception e) {
			LOGGER.error("Failed to load native library [" + LIBRARY_NAME + "]...Error - " + e);
		}
	}

	@Override
	public int initialize() {
		return _initialize();
	}

	@Override
	public int tearDown() {
		return _tearDown();
	}

	@Override
	protected void finalize() throws Throwable {
		tearDown();
	}

	/**
	 * @return
	 */
	native int _initialize();

	/**
	 * @return
	 */
	native int _tearDown();

	/**
	 * @param mapppingBuffer
	 * @param cameraModel
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	native int _addStream(ByteBuffer mapppingBuffer, String cameraModel, String url, String username, String password,
			int cameraID);

	/**
	 * @param mapppingBuffer
	 * @return
	 */
	native int _removeStream(ByteBuffer mapppingBuffer);

	/**
	 * @param mapppingBuffer
	 * @param dataBuffer
	 * @return
	 */
	native int _getContent(ByteBuffer mapppingBuffer, ByteBuffer dataBuffer);

	/**
	 * 
	 * @param mapppingBuffer
	 * @return
	 */
	native int _pauseStream(ByteBuffer mapppingBuffer);

	/**
	 * @param mapppingBuffer
	 * @return
	 */
	native int _resumeStream(ByteBuffer mapppingBuffer);

	/**
	 * @param mapppingBuffer
	 * @param cameraModel
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public int addStream(ByteBuffer mapppingBuffer, String cameraModel, String url, String username, String password,
			Integer cameraID) {
		return _addStream(mapppingBuffer, cameraModel, url, username, password, cameraID.intValue());
	}

	/**
	 * @param mapppingBuffer
	 * @return
	 */
	public int removeStream(ByteBuffer mapppingBuffer) {
		return _removeStream(mapppingBuffer);
	}

	/**
	 * @param mapppingBuffer
	 * @param dataBuffer
	 * @return
	 */
	public int getContent(ByteBuffer mapppingBuffer, ByteBuffer dataBuffer) {
		return _getContent(mapppingBuffer, dataBuffer);
	}

	/**
	 * @param mapppingBuffer
	 * @return
	 */
	public int pauseStream(ByteBuffer mapppingBuffer) {
		return _pauseStream(mapppingBuffer);
	}

	/**
	 * @param mapppingBuffer
	 * @return
	 */
	public int resumeStream(ByteBuffer mapppingBuffer) {
		return _resumeStream(mapppingBuffer);
	}
}
