package com.dss.vms.jni.interfaces;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.jni.interfaces.common.NativeLibrary;

public class ProbeMaster implements NativeLibrary {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProbeMaster.class);
	private static ProbeMaster INSTANCE;
	private static final String LIBRARY_NAME = "ProbeMaster";

	public static ProbeMaster getInstance() {
		if (INSTANCE == null) {
			synchronized (ProbeMaster.class) {
				if (INSTANCE == null) {
					INSTANCE = new ProbeMaster();
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

	/**
	 * @return
	 */
	native int _initialize();

	/**
	 * @return
	 */
	native int _tearDown();

	/**
	 * 
	 * @param mapingBuffer
	 * @return
	 */
	native int _addStream(ByteBuffer mapingBuffer);

	/**
	 * 
	 * @param mapingBuffer
	 * @return
	 */
	native int _removeStream(ByteBuffer mapingBuffer);

	/**
	 * 
	 * @param mapingBuffer
	 * @param regionBuffer
	 * @return
	 */
	native int _setRegion(ByteBuffer mapingBuffer, ByteBuffer regionBuffer);

	/**
	 * 
	 * @param mapingBuffer
	 * @param imageBuffer
	 * @param eventBuffer
	 * @return
	 */
	native int _searchEvent(ByteBuffer mapingBuffer, ByteBuffer imageBuffer, ByteBuffer eventBuffer);

	/**
	 * 
	 * @param mapingBuffer
	 * @param command
	 * @param cmdInput
	 * @return
	 */
	native int _execute(ByteBuffer mapingBuffer, int command, ByteBuffer cmdInput);

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
	 * 
	 * @param mapingBuffer
	 * @return
	 */
	public int addStream(ByteBuffer mapingBuffer) {
		return _addStream(mapingBuffer);
	}

	/**
	 * 
	 * @param mapingBuffer
	 * @return
	 */
	public int removeStream(ByteBuffer mapingBuffer) {
		return _removeStream(mapingBuffer);
	}

	/**
	 * 
	 * @param mapingBuffer
	 * @param regionBuffer
	 * @return
	 */
	public int setRegion(ByteBuffer mapingBuffer, ByteBuffer regionBuffer) {
		return _setRegion(mapingBuffer, regionBuffer);
	}

	/**
	 * 
	 * @param mapingBuffer
	 * @param imageBuffer
	 * @param eventBuffer
	 * @return
	 */
	public int searchEvent(ByteBuffer mapingBuffer, ByteBuffer imageBuffer, ByteBuffer eventBuffer) {
		return _searchEvent(mapingBuffer, imageBuffer, eventBuffer);
	}

	/**
	 * 
	 * @param mapingBuffer
	 * @param command
	 * @param cmdInput
	 * @return
	 */
	public int execute(ByteBuffer mapingBuffer, int command, ByteBuffer cmdInput) {
		return _execute(mapingBuffer, command, cmdInput);
	}

}
