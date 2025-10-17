/*
 * api_cpp.h
 *
 *  Created on: 13-Oct-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_API_CPP_H_
#define INCLUDE_API_CPP_H_

#ifdef __cplusplus
extern "C" {
#endif

__attribute__((visibility("default"))) int initialize();

__attribute__((visibility("default"))) int tearDown();

__attribute__((visibility("default"))) void *test(void *arg);

__attribute__((visibility("default"))) int addStream(unsigned char* mappingBuffer, const char* cameraModel,
		const char* url, const char* username, const char* password, unsigned int camId);

__attribute__((visibility("default"))) int removeStream(unsigned char* mappingBuffer);

__attribute__((visibility("default"))) int getContent(unsigned char* mappingBuffer, unsigned char* dataBuffer);

#ifdef __cplusplus
}
#endif

#endif /* INCLUDE_API_CPP_H_ */
