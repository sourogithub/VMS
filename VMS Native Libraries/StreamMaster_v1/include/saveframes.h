/*
 * saveframes.h
 *
 *  Created on: 17-Nov-2018
 *      Author: dss-06
 */

#ifndef INCLUDE_SAVEFRAMES_H_
#define INCLUDE_SAVEFRAMES_H_

//#define VIDEOCAPTURE_API __declspec(dllexport)
#define VIDEOCAPTURE_API
#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <fstream>
#include <cstring>
#include <math.h>
#include <string.h>
#include <algorithm>
#include <string>

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavcodec/avfft.h"

#include "libavdevice/avdevice.h"

#include "libavfilter/avfilter.h"
#include "libavfilter/avfiltergraph.h"
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"

#include "libavformat/avformat.h"
#include "libavformat/avio.h"

// libav resample

#include "libavutil/opt.h"
#include "libavutil/common.h"
#include "libavutil/channel_layout.h"
#include "libavutil/imgutils.h"
#include "libavutil/mathematics.h"
#include "libavutil/samplefmt.h"
#include "libavutil/time.h"
#include "libavutil/opt.h"
#include "libavutil/pixdesc.h"
#include "libavutil/file.h"
#include "libswscale/swscale.h"

typedef void(*FuncPtr)(const char *);

FuncPtr ExtDebug;
char errbuf[32];
pthread_mutex_t mlh;

void Debug(std::string str, int err) {
	std::cout << str << "  " << err << std::endl;
}

int Recording();

typedef struct _VideoCapture
{
	AVOutputFormat *oformat;
	AVFormatContext *ofctx;

	AVStream *videoStream;
	AVFrame *videoFrame;

	AVCodec *codec;
	AVCodecContext *cctx;

	SwsContext *swsCtx;

	AVFormatContext *ifmt_ctx, *ofmt_ctx;
	//AVRational framerate;

	int frameCounter;

	int fps;

	int ts;

	char *ftemp;

	_VideoCapture()
	{
		oformat = NULL;
		ofctx = NULL;
		videoStream = NULL;
		videoFrame = NULL;
		swsCtx = NULL;
		frameCounter = 0;
		ifmt_ctx = NULL;
		ofmt_ctx = NULL;
		ts = 0;
		//ftemp = a;
	}

	~_VideoCapture()
	{
		/*av_free(oformat);
		oformat= NULL;
		avformat_free_context(ofctx);
		ofctx = NULL;
		avcodec_close(videoStream->codec);
		av_free(videoStream);
		videoStream = NULL;
		av_frame_unref(videoFrame);
		av_frame_free(&videoFrame);
		videoFrame = NULL;
		sws_freeContext(swsCtx);
		swsCtx = NULL;
		avformat_free_context(ifmt_ctx);
		ifmt_ctx = NULL;
		avformat_free_context(ofmt_ctx);
		ofmt_ctx = NULL;
		avcodec_close(cctx);
		avcodec_free_context(&cctx);
		//av_free(cctx);
		cctx= NULL;
		av_free(codec);
		codec= NULL;
		//Free();*/
	}

}VideoCapture;


/*
class VideoCapture {
public:

	VideoCapture() {
		oformat = NULL;
		ofctx = NULL;
		videoStream = NULL;
		videoFrame = NULL;
		swsCtx = NULL;
		frameCounter = 0;

		// Initialize libavcodec
		//av_register_all();
		//av_log_set_callback(avlog_cb);
		//av_register_all();
		//avformat_network_init();
	}

	~VideoCapture() {
		//Free();
	}

	void Init(int width, int height, int fpsrate, int bitrate, UINT8 *mapping);

	void AddFrame(uint8_t *data);

	void AddFrame(AVFrame *videoFrameTest, UINT8 *mapping);

	void Finish(UINT8 *mapping);

	//void Free(UINT8 *mapping);

private:

	AVOutputFormat *oformat;
	AVFormatContext *ofctx;

	AVStream *videoStream;
	AVFrame *videoFrame;

	AVCodec *codec;
	AVCodecContext *cctx;

	SwsContext *swsCtx;

	int frameCounter;

	int fps;

	void Free(UINT8 *);

	void Remux(UINT8 *);
};
 */

void Initz(int width, int height, int fps, int bitrate, UINT8 *mapping, bool isForTest);

void AddFrame(AVFrame *, UINT8 *);

//void Finish(VideoCapture *vc, UINT8 *mapping);

void Finish(UINT8 *, bool isForTest);

//void AddFrame();

void Free(UINT8 *);

void Remux(UINT8 *, bool isForTest);

void SetDebug(FuncPtr fp) {
	ExtDebug = fp;
}
}



#endif /* INCLUDE_SAVEFRAMES_H_ */
