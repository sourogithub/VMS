package com.dss.vms.jni.interfaces.common;

public interface NativeConstants {

	int MAPPING_BUFFER_SIZE = 32;
	int INITIAL_ENCODED_FRAME_BUFFER = 65536;
	int SIZE_OF_BUFFER_SIZE = 4;

	/* for image probe buffer */
	int PARAM_SIZE = 16;
	int NO_OF_REGION_SIZE = 2;
	int NO_OF_POINTS_MARKER_SIZE = 1;

	/* for image buffer */
	int TIMESTAMP_SIZE = 8;
	int INITIAL_IMAGE_SIZE = 16997; /* value put by testing on the fly */

	/* for event buffer */
	int INITIAL_EVENT_BUFFER_SIZE = 54000; /* value put by testing on the fly */
	int FLAG_SIZE = 1;

	/* for decode buffer */
	int INITIAL_ENCODER_BUFFER_SIZE = 1000;
	int INITIAL_DECODER_BUFFER_SIZE = 1000;
	
	//command buffer
	int INITIAL_COMMAND_BUFFER_SIZE = 1026;
	int EMP_NAME_SIZE = 2;
	int EMP_ID_SIZE = 2;
	int EMP_GENDER_SIZE = 2;
	int EMP_DOB = 8;
	int NO_OF_IMAGE_SIZE = 2;
	int IMAGE_SIZE = 4;
}
