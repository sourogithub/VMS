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

__attribute__((visibility("default"))) int addStream(unsigned char* mappingBuffer);

__attribute__((visibility("default"))) int setRegion(const char* mappingBuffer, unsigned char *regionBuffer);

__attribute__((visibility("default"))) int searchEvent(unsigned char* mappingBuffer, unsigned char *imageBuffer, unsigned char *eventBuffer);

__attribute__((visibility("default"))) int execute(const char* mappingBuffer, unsigned int command, unsigned char *cmdInput);

#ifdef __cplusplus
}
#endif

#endif /* INCLUDE_API_CPP_H_ */
