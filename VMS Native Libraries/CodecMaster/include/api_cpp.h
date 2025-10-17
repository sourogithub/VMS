/*
 * api_cpp.h
 *
 *  Created on: 13-Oct-2018
 *      Author: dss-06
 */
#include "DataTypes.h"

#ifndef INCLUDE_API_CPP_H_
#define INCLUDE_API_CPP_H_

#ifdef __cplusplus
extern "C" {
#endif

__attribute__((visibility("default"))) int d_initialize();

__attribute__((visibility("default"))) int tearDown();

__attribute__((visibility("default"))) void *test(void *arg);

__attribute__((visibility("default"))) int addDecoder(unsigned char* mappingBuffer, const char mediaType,
		const char colorSpace);

__attribute__((visibility("default"))) int decode(unsigned char *mappingBuffer, unsigned char *encodedBuffer, unsigned int *decodedBuffer);

__attribute__((visibility("default"))) int addEncoder(UINT8* mappingBuffer, const CHAR8 codec, const CHAR8 colorSpace,
		const UINT32 width, const UINT32 height, const UINT32 bitRate);

__attribute__((visibility("default"))) int encode(UINT8* mappingBuffer, UINT32* decodedBuffer, UINT8* encodedBuffer);

#ifdef __cplusplus
}
#endif

#endif /* INCLUDE_API_CPP_H_ */
