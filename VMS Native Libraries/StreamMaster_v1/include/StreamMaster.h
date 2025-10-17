/*
 * StreamMaster.h
 *
 *  Created on: 06-Oct-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_STREAMMASTER_H_
#define INCLUDE_STREAMMASTER_H_

#include "DataTypes.h"
#include "ErrorTypes.h"
#include <stdlib.h>

typedef enum _MediaTypes_
{
	H264 						= 0,
	H265						= 1,
	MJPEG						= 2,
	MPEG4 						= 3,
	MP4 						= 4,
	MOV 						= 5,
	AVI							= 6,
	MKV							= 7,
	FLV							= 8,
	JPEG						= 9,
	JPG							= 10
}MediaTypes;

ErrorTypes Initialize();

ErrorTypes TestInitialize();

ErrorTypes TearDown();

UINT32 createFolder(char *);

void *Test(void *arg);

//ErrorTypes Stream(void *arg);

ErrorTypes AddStream(UINT8 *dataSpace, const CHAR8 *cameraModel,
		const CHAR8 *cameraURL, const CHAR8 *userId, const CHAR8 *pass, UINT32 camId);

ErrorTypes RemoveStream(UINT8 *dataSpace);

ErrorTypes GetContent(UINT8 *dataSpace, UINT8 *contentBuff);

ErrorTypes TestGetContent(UINT8 *dataSpace, UINT8 *contentBuff, UINT8* encodedBuffer);

ErrorTypes PauseStream(UINT8 *dataSpace);

ErrorTypes ResumeStream(UINT8 *dataSpace);

#endif /* INCLUDE_STREAMMASTER_H_ */
