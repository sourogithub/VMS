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
#include "CodecMaster.h"
#include "DataTypes.h"

int d_initialize()
{
	ErrorTypes eT = DSS_SUCCESS;
	printf("I am initialize in codec master api\n");
	if ( DSS_SUCCESS != (eT = Initializez()) )
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

int addDecoder(unsigned char* mappingBuffer, const char mediaType,
		const char colorSpace)
{
	ErrorTypes eT = AddDecoder(mappingBuffer, mediaType, colorSpace);
	return (UINT32)(eT);
}

int decode(unsigned char *mappingBuffer, unsigned char *encodedBuffer, unsigned int *decodedBuffer)
{
	ErrorTypes eT = Decode(mappingBuffer, encodedBuffer, decodedBuffer);
	return (UINT32)(eT);
}

int addEncoder(UINT8 *dataSpace, const CHAR8 codec, const CHAR8 colorSpace,
		const UINT32 width, const UINT32 height, const UINT32 bitRate)
{
	ErrorTypes eT = AddEncoder(dataSpace, codec, colorSpace, width, height, bitRate);
	return (UINT32)(eT);
}

int encode(UINT8 *dataSpace, UINT32 *decodedBuffer, UINT8 *encodedBuffer)
{
	ErrorTypes eT = Encode(dataSpace, decodedBuffer, encodedBuffer);
	return (UINT32)(eT);
}

