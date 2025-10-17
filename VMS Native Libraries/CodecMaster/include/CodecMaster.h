/*
 * StreamMaster.h
 *
 *  Created on: 06-Oct-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_CODECMASTER_H_
#define INCLUDE_CODECMASTER_H_

	#ifdef HAVE_AV_CONFIG_H
	#undef HAVE_AV_CONFIG_H
	#endif
extern "C" {
	#include "libavcodec/avcodec.h"
	#include "libavformat/avformat.h"
	#include "libavutil/mathematics.h"
	#include "libavutil/pixfmt.h"
	#include "libswscale/swscale.h"
}

#include "DataTypes.h"
#include "ErrorTypes.h"
//#include "MediaTypes.h"
//#include "ColorSpace.h"

ErrorTypes Initializez();

ErrorTypes TearDown();

ErrorTypes AddDecoder(UINT8 *dataSpace, const CHAR8 mediaType,
		const CHAR8 colorSpace);

ErrorTypes RemoveDecoder(UINT8 *dataSpace);

void *Test(void *);

ErrorTypes Decode(UINT8 *dataSpace, UINT8 *encodedBuffer, UINT32 *decodedBuffer);

ErrorTypes Decode(const CHAR8 mediaType, const CHAR8 colorSpace,
		UINT8 *encodedBuffer, UINT32 *decodedBuffer);

ErrorTypes AddEncoder(UINT8 *dataSpace, const CHAR8 codec, const CHAR8 colorSpace,
		const UINT32 width, const UINT32 height, const UINT32 bitRate);

ErrorTypes RemoveEncoder(UINT8 *dataSpace);

ErrorTypes Encode(UINT8 *dataSpace, UINT32 *decodedBuffer, UINT8 *encodedBuffer);

#endif /* INCLUDE_CODECMASTER_H_ */
