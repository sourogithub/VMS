/*
 * testRTSPClient.h
 *
 *  Created on: 09-Oct-2018
 *      Author: Soumyadip Maity
 */

#ifndef SRC_RTSP_RTSPCLIENT_INCLUDE_TESTRTSPCLIENT_H_
#define SRC_RTSP_RTSPCLIENT_INCLUDE_TESTRTSPCLIENT_H_

#include <iostream>
#include <vector>
#include "liveMedia.hh"
#include "BasicUsageEnvironment.hh"

// FFMPEG
extern "C" {
	#include <stdlib.h>
	#include <stdio.h>
	#include <string.h>

	#ifdef HAVE_AV_CONFIG_H
	#undef HAVE_AV_CONFIG_H
	#endif

	#include "libavcodec/avcodec.h"
	#include "libavformat/avformat.h"
	#include "libavutil/mathematics.h"
	#include "libavutil/pixfmt.h"
	#include "libswscale/swscale.h"

	#define INBUF_SIZE 4096
	#define AUDIO_INBUF_SIZE 20480
	#define AUDIO_REFILL_THRESH 4096
}

#define RTSP_CLIENT_VERBOSITY_LEVEL 1 // by default, print verbose output from each "RTSPClient"

typedef struct _Frame
{
	int 			m_AllocSize;
	int				m_BuffSize;
	unsigned char 	*m_Buffer;
}Frame;

void continueAfterDESCRIBE(RTSPClient* rtspClient, int resultCode, char* resultString);
void continueAfterSETUP(RTSPClient* rtspClient, int resultCode, char* resultString);
void continueAfterPLAY(RTSPClient* rtspClient, int resultCode, char* resultString);

// Other event handler functions:
void subsessionAfterPlaying(void* clientData); // called when a stream's subsession (e.g., audio or video substream) ends
void subsessionByeHandler(void* clientData); // called when a RTCP "BYE" is received for a subsession
void streamTimerHandler(void* clientData);
// called at the end of a stream's expected duration (if the stream has not already signaled its end using a RTCP "BYE")

// Used to iterate through each stream's 'subsessions', setting up each one:
void setupNextSubsession(RTSPClient* rtspClient);

// Used to shut down and close a stream (including its "RTSPClient" object):
void shutdownStream(RTSPClient* rtspClient);



// Define a class to hold per-stream state that we maintain throughout each stream's lifetime:

class StreamClientState {
public:
	StreamClientState();
	virtual ~StreamClientState();

public:
	MediaSubsessionIterator* iter;
	MediaSession* session;
	MediaSubsession* subsession;
	TaskToken streamTimerTask;
	double duration;
};

// If you're streaming just a single stream (i.e., just from a single URL, once), then you can define and use just a single
// "StreamClientState" structure, as a global variable in your application.  However, because - in this demo application - we're
// showing how to play multiple streams, concurrently, we can't do that.  Instead, we have to have a separate "StreamClientState"
// structure for each "RTSPClient".  To do this, we subclass "RTSPClient", and add a "StreamClientState" field to the subclass:

class ourRTSPClient: public RTSPClient {
public:
	static ourRTSPClient* createNew(UsageEnvironment& env, char const* rtspURL,
			int verbosityLevel = 0,
			char const* applicationName = NULL,
			portNumBits tunnelOverHTTPPortNum = 0);

protected:
	ourRTSPClient(UsageEnvironment& env, char const* rtspURL,
			int verbosityLevel, char const* applicationName, portNumBits tunnelOverHTTPPortNum);
	// called only by createNew();
	virtual ~ourRTSPClient();

public:
	StreamClientState scs;
	int lastFrame;
	std::vector<Frame*> m_FrameList;
};

// Define a data sink (a subclass of "MediaSink") to receive the data for each subsession (i.e., each audio or video 'substream').
// In practice, this might be a class (or a chain of classes) that decodes and then renders the incoming audio or video.
// Or it might be a "FileSink", for outputting the received data into a file (as is done by the "openRTSP" application).
// In this example code, however, we define a simple 'dummy' sink that receives incoming data, but does nothing with it.

class DummySink: public MediaSink {
public:
	static DummySink* createNew(UsageEnvironment& env,
			MediaSubsession& subsession, // identifies the kind of data that's being received
			char const* streamId = NULL); // identifies the stream itself (optional)

private:
	DummySink(UsageEnvironment& env, MediaSubsession& subsession, char const* streamId);
	// called only by "createNew()"
	virtual ~DummySink();

	static void afterGettingFrame(void* clientData, unsigned frameSize,
			unsigned numTruncatedBytes,
			struct timeval presentationTime,
			unsigned durationInMicroseconds);
	void afterGettingFrame(unsigned frameSize, unsigned numTruncatedBytes,
			struct timeval presentationTime, unsigned durationInMicroseconds);

private:
	// redefined virtual functions:
	virtual Boolean continuePlaying();

private:
	u_int8_t* fReceiveBuffer;
	u_int8_t* fReceiveBufferAV;
	MediaSubsession& fSubsession;
	char* fStreamId;

private: //H264
	u_int8_t const* sps;
	unsigned spsSize;
	u_int8_t const* pps;
	unsigned ppsSize;
public:
	void setSprop(u_int8_t const* prop, unsigned size);

	std::vector<Frame*>* getFrameList()
	{
		return m_FrameList;
	}

	void setFrameList(std::vector<Frame*>* frameList)
	{
		m_FrameList = frameList;
	}

private: //FFMPEG
	AVCodec *codec;
	AVCodecContext *c;
	int frame;
	int got_picture;
	int len;
	AVFrame *picture;
	uint8_t inbuf[INBUF_SIZE + FF_INPUT_BUFFER_PADDING_SIZE];
	char buf[1024];
	AVPacket avpkt;
	std::vector<Frame*> *m_FrameList;
};

#endif /* SRC_RTSP_RTSPCLIENT_INCLUDE_TESTRTSPCLIENT_H_ */
