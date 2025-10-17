/*
 * DataTypes.h
 *
 *  Created on: 06-Oct-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_DATATYPES_H_
#define INCLUDE_DATATYPES_H_


#include <stdio.h>

typedef unsigned char 			UINT8;
typedef char 					CHAR8;
typedef unsigned short			UINT16;
typedef short					INT16;
typedef unsigned int			UINT32;
typedef int						INT32;
typedef unsigned long long int	UINT64;
typedef long long int			INT64;
typedef float           		FLOAT32;
typedef double          		FLOAT64;
typedef bool            		BOOL;

typedef struct _Data
{
	UINT8 buffer[36];
	const CHAR8 *model;
	const CHAR8 *url;
	const CHAR8 *user;
	const CHAR8 *pass;
	UINT8 *dataBuffer;
	UINT32 camId;
	UINT32 count;
}Data;

#endif /* INCLUDE_DATATYPES_H_ */
