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
#include "ProbeMaster.h"

int initialize()
{
	ErrorTypes eT = DSS_SUCCESS;

	if ( DSS_SUCCESS != (eT = Initialize()) )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)(eT);
}

int tearDown()
{
	ErrorTypes eT = DSS_SUCCESS;

	if ( DSS_SUCCESS != (eT = Teardown()) )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)eT;
}

int searchEvent(unsigned char* mappingBuffer, unsigned char *imageBuffer, unsigned char *eventBuffer)
{
	ErrorTypes eT = DSS_SUCCESS;

	if ( DSS_SUCCESS != (eT = SearchEvent(mappingBuffer, imageBuffer, eventBuffer)) )
	{
		//TO DO: Handle the error

		return (UINT32)DSS_ERROR;
	}

	return (UINT32)(eT);
}

int addStream(unsigned char* mappingBuffer)
{
	return (UINT32)AddStream(mappingBuffer);
}

