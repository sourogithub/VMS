package com.dss.vms.jni.interfaces.common;

/**
 * @author jdeveloper
 */
public interface NativeRetun {

	int SUCCESS = 0;
	int WARNING = 1;
	int ERROR = 2;
	int FILE_ERROR = 3;
	int IO_ERROR = 4;
	int MEMORY_ALLOCATION_FAILURE = 5;
	int INVALID_URL = 6;
	int AUTHENTICATION_ERROR = 7;
	int BUFFER_OVERFLOW = 8;
	int INSUFICIENT_MEMORY = 9;
	int LIB_NOT_READY = 10;
	int DATA_NOT_READY = 11;
//	int CAMERA_NOT_CONNECTED = 12;

}
