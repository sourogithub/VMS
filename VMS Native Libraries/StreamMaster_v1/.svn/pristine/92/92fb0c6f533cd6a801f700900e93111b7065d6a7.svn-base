/*
 * api_cpp.cpp
 *
 *  Created on: 13-Oct-2018
 *      Author: dss-06
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "api_cpp.h"
#include "ErrorTypes.h"
#include "StreamMaster.h"

int initialize()
{
	ErrorTypes eT = Initialize();

	if ( DSS_SUCCESS != eT )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)(eT);
}

int tearDown()
{
	ErrorTypes eT = DSS_SUCCESS;
	printf("I am teardown in test\n");
	if ( DSS_SUCCESS != (eT = TearDown()) )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)eT;
}

void *test(void *arg)
{
	Test(arg);
//
//	if ( DSS_SUCCESS != (eT = TearDown()) )
//	{
//		//TO DO: Handle the error
//
//		return (UINT32)DSS_ERROR;
//	}
//
//	return (UINT32)eT;
}


int addStream(unsigned char* mappingBuffer, const char* cameraModel,
		const char* url, const char* username, const char* password, unsigned int camId)
{
	printf("Say something\n");
	INT32 bufferSize = *((INT32 *)mappingBuffer);

#if DEBUG_PRINT
	printf("Buffer Size = %d\n", bufferSize);
#endif

#if DEBUG_PRINT
	printf("Camera model:  %s\n",cameraModel);
	printf("Camera URL:  %s\n", url);
	printf("Camera UId:  %s\n", username);
	printf("Camera Pass:  %s\n", password);
#endif


	 // Call AddStream with all inputs

	ErrorTypes eT = AddStream(mappingBuffer, cameraModel, url, username, password, camId);

	if ( DSS_SUCCESS != eT )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)(eT);

}


int removeStream(unsigned char* mappingBuffer)
{
	ErrorTypes eT = DSS_SUCCESS;

	INT32 bufferSize = *((INT32 *)mappingBuffer);

	eT = RemoveStream(mappingBuffer);

	return (UINT32)(eT);

}

int getContent(unsigned char* mappingBuffer, unsigned char* dataBuffer)
{
	ErrorTypes eT = DSS_SUCCESS;

	eT = GetContent(mappingBuffer, dataBuffer);
//	if ( DSS_SUCCESS != eT )
//	{
//		//TO DO: Handle the error
//
//		return (UINT32)eT;
//	}

	return (UINT32)(eT);
}

