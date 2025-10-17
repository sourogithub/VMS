#include "liveMedia.hh"
#include "BasicUsageEnvironment.hh"
#include "testRTSPClient.h"
#include "ErrorTypes.h"

// FFMPEG
extern "C" {
#include <stdlib.h>
#include <iostream>
#include <stdio.h>
#include <string.h>
using namespace std;
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
#include "Base64.hh"

//static int count=1;
Authenticator* ourAuthenticator = NULL;
pthread_mutex_t lock1;

// A function that outputs a string that identifies each stream (for debugging output).  Modify this if you wish:
UsageEnvironment& operator<<(UsageEnvironment& env, const RTSPClient& rtspClient) {
	return env << "[URL:\"" << rtspClient.url() << "\"]: ";
}

// A function that outputs a string that identifies each subsession (for debugging output).  Modify this if you wish:
UsageEnvironment& operator<<(UsageEnvironment& env, const MediaSubsession& subsession) {
	return env << subsession.mediumName() << "/" << subsession.codecName();
}

void usage(UsageEnvironment& env, char const* progName) {
	env << "Usage: " << progName << " <rtsp-url-1> ... <rtsp-url-N>\n";
	env << "\t(where each <rtsp-url-i> is a \"rtsp://\" URL)\n";
}

char eventLoopWatchVariable = 0;

#pragma pack(1)
struct header
{
	//char tag[4]={'D','S','S','F'};
	char									tag1;
	char									tag2;
	char									tag3;
	char									tag4;		//{'D','S','S','F'};
	unsigned int							size_of_image;
	unsigned long long int					timestamp_millisec;
	unsigned int							timestamp_nanosec;
	long long int 							start_point;
	char 									media_type;
	char 									frame_type;
	char 									reserve1;
	char 									reserve2;
};
struct header *h = (struct header *)calloc(1,sizeof(struct header));

enum media_type {
	JPEG = 0,
	MJPEG,
	MPEG4,
	H264,
	H265
}mt;

enum frame_type {
	H_FRAME = 0,
	I_FRAME,
	P_FRAME,
	B_FRAME
}ft;





//int openURL(UsageEnvironment& env, char const* progName, char const* rtspURL);
//
//int main(int argc, char** argv) {
//	// Begin by setting up our usage environment:
//	TaskScheduler* scheduler = BasicTaskScheduler::createNew();
//	UsageEnvironment* env = BasicUsageEnvironment::createNew(*scheduler);
//	ourAuthenticator = new Authenticator("admin", "dssa#2018");
//	// We need at least one "rtsp://" URL argument:
//	if (argc < 2) {
//		usage(*env, argv[0]);
//		return 1;
//	}
//
//	// avcodec init
//	avcodec_register_all();
//	av_register_all();
//
//	// There are argc-1 URLs: argv[1] through argv[argc-1].  Open and start streaming each one:
//	for (int i = 1; i <= argc-1; ++i) {
//		openURL(*env, argv[0], argv[i]);
//	}
//
//	// All subsequent activity takes place within the event loop:
//	env->taskScheduler().doEventLoop(&eventLoopWatchVariable);
//	// This function call does not return, unless, at some point in time, "eventLoopWatchVariable" gets set to something non-zero.
//	return 0;
//
//	// If you choose to continue the application past this point (i.e., if you comment out the "return 0;" statement above),
//	// and if you don't intend to do anything more with the "TaskScheduler" and "UsageEnvironment" objects,
//	// then you can also reclaim the (small) memory used by these objects by uncommenting the following code:
//	/*
//    env->reclaim(); env = NULL;
//    delete scheduler; scheduler = NULL;
//	 */
//}
//
//int openURL(UsageEnvironment& env, char const* progName, char const* rtspURL) {
//	// Begin by creating a "RTSPClient" object.  Note that there is a separate "RTSPClient" object for each stream that we wish
//	// to receive (even if more than stream uses the same "rtsp://" URL).
//	RTSPClient* rtspClient = ourRTSPClient::createNew(env, rtspURL, RTSP_CLIENT_VERBOSITY_LEVEL, progName);
//	if (rtspClient == NULL) {
//		env << "Failed to create a RTSP client for URL \"" << rtspURL << "\": " << env.getResultMsg() << "\n";
//		return 1;
//	}
//
//	// Next, send a RTSP "DESCRIBE" command, to get a SDP description for the stream.
//	// Note that this command - like all RTSP commands - is sent asynchronously; we do not block, waiting for a response.
//	// Instead, the following function call returns immediately, and we handle the RTSP response later, from within the event loop:
//	rtspClient->sendDescribeCommand(continueAfterDESCRIBE, ourAuthenticator);
//
//	return 0;
//}
//of response data
//Started playing session
// Implementation of the RTSP 'response handlers':

void continueAfterDESCRIBE(RTSPClient* rtspClient, int resultCode, char* resultString) {
	printf("\ncontinueAfterDESCRIBEeeeeeeeeeeeeeeeeeeeeeeeeeeeee\n"); fflush(stdout);
	do {
		UsageEnvironment& env = rtspClient->envir(); // alias
		StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias

		if (resultCode != 0) {
			env << *rtspClient << "Failed to get a SDP description: " << resultString << "\n";
			delete[] resultString;
			break;
		}

		char* const sdpDescription = resultString;
		env << *rtspClient << "Got a SDP description:\n" << sdpDescription << "\n";
		// Create a media session object from this SDP description:
		scs.session = MediaSession::createNew(env, sdpDescription);
		delete[] sdpDescription; // because we don't need it anymore
		if (scs.session == NULL) {
			env << *rtspClient << "Failed to create a MediaSession object from the SDP description: " << env.getResultMsg() << "\n";
			break;
		} else if (!scs.session->hasSubsessions()) {
			env << *rtspClient << "This session has no media subsessions (i.e., no \"m=\" lines)\n";
			break;
		}

		// Then, create and set up our data source objects for the session.  We do this by iterating over the session's 'subsessions',
		// calling "MediaSubsession::initiate()", and then sending a RTSP "SETUP" command, on each one.
		// (Each 'subsession' will have its own data source.)
		scs.iter = new MediaSubsessionIterator(*scs.session);
		setupNextSubsession(rtspClient);
		return;
	} while (0);
	// An unrecoverable error occurred with this stream.
	shutdownStream(rtspClient);
}

// By default, we request that the server stream its data using RTP/UDP.
// If, instead, you want to request that the server stream via RTP-over-TCP, change the following to True:
#define REQUEST_STREAMING_OVER_TCP False

void setupNextSubsession(RTSPClient* rtspClient) {
	//printf("setupNextSubsession\n"); fflush(stdout);
	UsageEnvironment& env = rtspClient->envir(); // alias
	StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias
	scs.subsession = scs.iter->next();
	//cout<<"subsession before = "<< scs.subsession<<endl;
	//printf("subsession before = %llu\n",scs.subsession);

	if (scs.subsession != NULL) {
		if (!scs.subsession->initiate()) {
			env << *rtspClient << "Failed to initiate the \"" << *scs.subsession << "\" subsession: " << env.getResultMsg() << "\n";
			setupNextSubsession(rtspClient); // give up on this subsession; go to the next one
		} else {
			env << *rtspClient << "Initiated the \"" << *scs.subsession
					<< "\" subsession (client ports " << scs.subsession->clientPortNum() << "-" << scs.subsession->clientPortNum()+1 << ")\n";
			// Continue setting up this subsession, by sending a RTSP "SETUP" command:
			rtspClient->sendSetupCommand(*scs.subsession, continueAfterSETUP, False, REQUEST_STREAMING_OVER_TCP, false, ourAuthenticator);
		}
		//cout<<"subsession after = "<< scs.subsession<<endl;
		//printf("subsession after = %d\n",scs.subsession);
		return;
	}

	// We've finished setting up all of the subsessions.  Now, send a RTSP "PLAY" command to start the streaming:
	if (scs.session->absStartTime() != NULL) {
		// Special case: The stream is indexed by 'absolute' time, so send an appropriate "PLAY" command:
		rtspClient->sendPlayCommand(*scs.session, continueAfterPLAY, scs.session->absStartTime(), scs.session->absEndTime(), 1.0f, ourAuthenticator);
	} else {
		scs.duration = scs.session->playEndTime() - scs.session->playStartTime();
		rtspClient->sendPlayCommand(*scs.session, continueAfterPLAY, 0.0f, -1.0f, 1.0f, ourAuthenticator);
	}
}

void continueAfterSETUP(RTSPClient* rtspClient, int resultCode, char* resultString) {
	//printf("ContinueAfterSetup\n"); fflush(stdout);

	do {
		UsageEnvironment& env = rtspClient->envir(); // alias
		StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias
		if (resultCode != 0) {
			env << *rtspClient << "Failed to set up the \"" << *scs.subsession << "\" subsession: " << resultString << "\n";
			break;
		}

		env << *rtspClient << "Set up the \"" << *scs.subsession
				<< "\" subsession (client ports " << scs.subsession->clientPortNum() << "-" << scs.subsession->clientPortNum()+1 << ")\n";

		const char *sprop = scs.subsession->fmtp_spropparametersets();
		uint8_t const* sps = NULL;
		unsigned spsSize = 0;
		uint8_t const* pps = NULL;
		unsigned ppsSize = 0;

		if (sprop != NULL) {
			unsigned numSPropRecords;
			SPropRecord* sPropRecords = parseSPropParameterSets(sprop, numSPropRecords);
			for (unsigned i = 0; i < numSPropRecords; ++i) {
				if (sPropRecords[i].sPropLength == 0) continue; // bad data
				u_int8_t nal_unit_type = (sPropRecords[i].sPropBytes[0])&0x1F;
				if (nal_unit_type == 7/*SPS*/) {
					sps = sPropRecords[i].sPropBytes;
					spsSize = sPropRecords[i].sPropLength;
				} else if (nal_unit_type == 8/*PPS*/) {
					pps = sPropRecords[i].sPropBytes;
					ppsSize = sPropRecords[i].sPropLength;
				}
			}
		}

		// Having successfully setup the subsession, create a data sink for it, and call "startPlaying()" on it.
		// (This will prepare the data sink to receive data; the actual flow of data from the client won't start happening until later,
		// after we've sent a RTSP "PLAY" command.)
		scs.subsession->sink = DummySink::createNew(env, *scs.subsession, rtspClient->url());
		((DummySink *)scs.subsession->sink)->setFrameList(&((ourRTSPClient *)rtspClient)->m_FrameList);

		// perhaps use your own custom "MediaSink" subclass instead
		if (scs.subsession->sink == NULL) {
			env << *rtspClient << "Failed to create a data sink for the \"" << *scs.subsession
					<< "\" subsession: " << env.getResultMsg() << "\n";
			break;
		}

		env << *rtspClient << "Created a data sink for the \"" << *scs.subsession << "\" subsession\n";
		scs.subsession->miscPtr = rtspClient; // a hack to let subsession handle functions get the "RTSPClient" from the subsession
		if (sps != NULL) {
			((DummySink *)scs.subsession->sink)->setSprop(sps, spsSize);
		}
		if (pps != NULL) {
			((DummySink *)scs.subsession->sink)->setSprop(pps, ppsSize);
		}
		scs.subsession->sink->startPlaying(*(scs.subsession->readSource()),
				subsessionAfterPlaying, scs.subsession);
		// Also set a handler to be called if a RTCP "BYE" arrives for this subsession:
		if (scs.subsession->rtcpInstance() != NULL) {
			scs.subsession->rtcpInstance()->setByeHandler(subsessionByeHandler, scs.subsession);
		}
	} while (0);
	delete[] resultString;
	// Set up the next subsession, if any:
	setupNextSubsession(rtspClient);
}

void continueAfterPLAY(RTSPClient* rtspClient, int resultCode, char* resultString) {
	Boolean success = False;
	StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias
	//cout<<"subsession after coming out = "<< scs.subsession<<endl;
	//printf("subsession after out = %d\n",scs.subsession);
	do {
		UsageEnvironment& env = rtspClient->envir(); // alias
		StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias

		if (resultCode != 0) {
			env << *rtspClient << "Failed to start playing session: " << resultString << "\n";
			break;
		}
		// Set a timer to be handled at the end of the stream's expected duration (if the stream does not already signal its end
		// using a RTCP "BYE").  This is optional.  If, instead, you want to keep the stream active - e.g., so you can later
		// 'seek' back within it and do another RTSP "PLAY" - then you can omit this code.
		// (Alternatively, if you don't want to receive the entire stream, you could set this timer for some shorter value.)
		if (scs.duration > 0) {
			unsigned const delaySlop = 2; // number of seconds extra to delay, after the stream's expected duration.  (This is optional.)
			scs.duration += delaySlop;
			unsigned uSecsToDelay = (unsigned)(scs.duration*1000000);
			scs.streamTimerTask = env.taskScheduler().scheduleDelayedTask(uSecsToDelay, (TaskFunc*)streamTimerHandler, rtspClient);
		}
		env << *rtspClient << "Started playing session";
		if (scs.duration > 0) {
			env << " (for up to " << scs.duration << " seconds)";
		}
		env << "...\n";

		success = True;
	} while (0);
	delete[] resultString;

	if (!success) {
		// An unrecoverable error occurred with this stream.
		shutdownStream(rtspClient);
	}
}

// Implementation of the other event handlers:

void subsessionAfterPlaying(void* clientData) {
	MediaSubsession* subsession = (MediaSubsession*)clientData;
	RTSPClient* rtspClient = (RTSPClient*)(subsession->miscPtr);
	// Begin by closing this subsession's stream:
	Medium::close(subsession->sink);
	subsession->sink = NULL;

	// Next, check whether *all* subsessions' streams have now been closed:
	MediaSession& session = subsession->parentSession();
	MediaSubsessionIterator iter(session);
	while ((subsession = iter.next()) != NULL) {
		if (subsession->sink != NULL) return; // this subsession is still active
	}

	// All subsessions' streams have now been closed, so shutdown the client:
	shutdownStream(rtspClient);
}

void subsessionByeHandler(void* clientData) {
	MediaSubsession* subsession = (MediaSubsession*)clientData;
	RTSPClient* rtspClient = (RTSPClient*)subsession->miscPtr;
	UsageEnvironment& env = rtspClient->envir(); // alias

	env << *rtspClient << "Received RTCP \"BYE\" on \"" << *subsession << "\" subsession\n";

	// Now act as if the subsession had closed:
	subsessionAfterPlaying(subsession);
}

void streamTimerHandler(void* clientData) {
	ourRTSPClient* rtspClient = (ourRTSPClient*)clientData;
	StreamClientState& scs = rtspClient->scs; // alias

	scs.streamTimerTask = NULL;

	// Shut down the stream:
	shutdownStream(rtspClient);
}

void shutdownStream(RTSPClient* rtspClient) {
	UsageEnvironment& env = rtspClient->envir(); // alias
	StreamClientState& scs = ((ourRTSPClient*)rtspClient)->scs; // alias

	// First, check whether any subsessions have still to be closed:
	if (scs.session != NULL) {
		Boolean someSubsessionsWereActive = False;
		MediaSubsessionIterator iter(*scs.session);
		MediaSubsession* subsession;

		while ((subsession = iter.next()) != NULL) {
			if (subsession->sink != NULL) {
				Medium::close(subsession->sink);
				subsession->sink = NULL;

				if (subsession->rtcpInstance() != NULL) {
					subsession->rtcpInstance()->setByeHandler(NULL, NULL); // in case the server sends a RTCP "BYE" while handling "TEARDOWN"
				}

				someSubsessionsWereActive = True;
			}
		}

		if (someSubsessionsWereActive) {
			// Send a RTSP "TEARDOWN" command, to tell the server to shutdown the stream.
			// Don't bother handling the response to the "TEARDOWN".
			rtspClient->sendTeardownCommand(*scs.session, NULL, ourAuthenticator);
		}
	}

	env << *rtspClient << "Closing the stream.\n";
	Medium::close(rtspClient);
}


// Implementation of "ourRTSPClient":

ourRTSPClient* ourRTSPClient::createNew(UsageEnvironment& env, char const* rtspURL,
		int verbosityLevel, char const* applicationName, portNumBits tunnelOverHTTPPortNum) {
	return new ourRTSPClient(env, rtspURL, verbosityLevel, applicationName, tunnelOverHTTPPortNum);
}

ourRTSPClient::ourRTSPClient(UsageEnvironment& env, char const* rtspURL,
		int verbosityLevel, char const* applicationName, portNumBits tunnelOverHTTPPortNum)
: RTSPClient(env,rtspURL, verbosityLevel, applicationName, tunnelOverHTTPPortNum, -1) {
	lastFrame = 0;
}

ourRTSPClient::~ourRTSPClient() {
}


// Implementation of "StreamClientState":

StreamClientState::StreamClientState()
: iter(NULL), session(NULL), subsession(NULL), streamTimerTask(NULL), duration(0.0) {
}

StreamClientState::~StreamClientState() {
	delete iter;
	if (session != NULL) {
		// We also need to delete "session", and unschedule "streamTimerTask" (if set)
		UsageEnvironment& env = session->envir(); // alias

		env.taskScheduler().unscheduleDelayedTask(streamTimerTask);
		Medium::close(session);
	}
}


// Implementation of "DummySink":

// Even though we're not going to be doing anything with the incoming data, we still need to receive it.
// Define the size of the buffer that we'll use:
#define DUMMY_SINK_RECEIVE_BUFFER_SIZE 100000

DummySink* DummySink::createNew(UsageEnvironment& env, MediaSubsession& subsession, char const* streamId) {
	return new DummySink(env, subsession, streamId);
}

DummySink::DummySink(UsageEnvironment& env, MediaSubsession& subsession, char const* streamId)
: MediaSink(env), fSubsession(subsession)
{
	fStreamId = strDup(streamId);
	fReceiveBuffer = new u_int8_t[DUMMY_SINK_RECEIVE_BUFFER_SIZE];
	fReceiveBufferAV = new u_int8_t[DUMMY_SINK_RECEIVE_BUFFER_SIZE+4];
	fReceiveBufferAV[0] = 0;
	fReceiveBufferAV[1] = 0;
	fReceiveBufferAV[2] = 0;
	fReceiveBufferAV[3] = 1;

	av_init_packet(&avpkt);
	avpkt.flags |= AV_PKT_FLAG_KEY;
	avpkt.pts = avpkt.dts = 0;

	/* set end of buffer to 0 (this ensures that no overreading happens for damaged mpeg streams) */
	memset(inbuf + INBUF_SIZE, 0, FF_INPUT_BUFFER_PADDING_SIZE);

	//codec = avcodec_find_decoder(CODEC_ID_MPEG1VIDEO);
	codec = avcodec_find_decoder(AV_CODEC_ID_H264);
	if (!codec) {
		envir() << "codec not found!";
		exit(4);
	}

	c = avcodec_alloc_context3(codec);
	picture = av_frame_alloc();

	if (codec->capabilities & CODEC_CAP_TRUNCATED) {
		c->flags |= CODEC_FLAG_TRUNCATED; // we do not send complete frames
	}

	c->width = 640;
	c->height = 360;
	c->pix_fmt = AV_PIX_FMT_YUV420P;

	/* for some codecs width and height MUST be initialized there becuase this info is not available in the bitstream */

	if (avcodec_open2(c,codec,NULL) < 0) {
		envir() << "could not open codec";
		exit(5);
	}
}

DummySink::~DummySink() {
	delete[] fReceiveBuffer;
	delete[] fStreamId;
}

void DummySink::afterGettingFrame(void* clientData, unsigned frameSize, unsigned numTruncatedBytes,
		struct timeval presentationTime, unsigned durationInMicroseconds) {
	DummySink* sink = (DummySink*)clientData;
	sink->afterGettingFrame(frameSize, numTruncatedBytes, presentationTime, durationInMicroseconds);
}

void pgm_save(unsigned char *buf, int wrap, int xsize, int ysize, char *filename) {
	FILE *fp;
	int i;
	fp = fopen (filename,"wb");
	fprintf(fp,"P5\n%d %d\n%d\n",xsize,ysize,255);
	for(i=0;i<ysize;i++) {
		fwrite(buf + i * wrap,1,xsize,fp);
	}
	fclose(fp);
	exit(1);
}

void DummySink::setSprop(u_int8_t const* prop, unsigned size) {
	uint8_t *buf;
	uint8_t *buf_start;
	buf = (uint8_t *)malloc(1000);
	buf_start = buf + 4;
	avpkt.data = buf;
	avpkt.data[0]   = 0;
	avpkt.data[1]   = 0;
	avpkt.data[2]   = 0;
	avpkt.data[3]   = 1;
	memcpy (buf_start, prop, size);
	avpkt.size = size + 4;

	if (pthread_mutex_init(&lock1, NULL) != 0){
		printf("\n mutex init has failed\n");
	}
	pthread_mutex_lock(&lock1);
	Frame *newFrame = new Frame;
	newFrame->m_AllocSize = sizeof(avpkt.flags) + sizeof(avpkt.pts) + sizeof(avpkt.dts) + avpkt.size;
	newFrame->m_Buffer = new uint8_t[newFrame->m_AllocSize];
	newFrame->m_BuffSize = newFrame->m_AllocSize;

	memcpy(newFrame->m_Buffer, &(avpkt.flags),  sizeof(avpkt.flags));
	memcpy(newFrame->m_Buffer + sizeof(avpkt.flags), &(avpkt.pts),  sizeof(avpkt.pts));
	memcpy(newFrame->m_Buffer + sizeof(avpkt.flags) + sizeof(avpkt.pts), &(avpkt.dts),  sizeof(avpkt.dts));
	memcpy(newFrame->m_Buffer + sizeof(avpkt.flags) + sizeof(avpkt.pts) + sizeof(avpkt.dts) , avpkt.data,  avpkt.size );

	m_FrameList->push_back(newFrame);

	pthread_mutex_unlock(&lock1);

	len = avcodec_decode_video2 (c, picture, &got_picture, &avpkt);
	if (len < 0) {
		envir() << "Error while decoding frame" << frame;
	}
	envir() << "after setSprop\n";

}
#define DEBUG_PRINT_EACH_RECEIVED_FRAME 1
void DummySink::afterGettingFrame(unsigned frameSize, unsigned numTruncatedBytes,
		struct timeval presentationTime, unsigned /*durationInMicroseconds*/) {
	// We've just received a frame of data.  (Optionally) print out information about it:
#ifdef DEBUG_PRINT_EACH_RECEIVED_FRAME
	if (fStreamId != NULL) envir() << "Stream \"" << fStreamId << "\"; ";
	envir() << fSubsession.mediumName() << "/" << fSubsession.codecName() << ":\tReceived " << frameSize << " bytes";
	if (numTruncatedBytes > 0) envir() << " (with " << numTruncatedBytes << " bytes truncated)";
	char uSecsStr[6+1]; // used to output the 'microseconds' part of the presentation time
	sprintf(uSecsStr, "%06u", (unsigned)presentationTime.tv_usec);
	envir() << ".\tPresentation time: " << (int)presentationTime.tv_sec << "." << uSecsStr;
	if (fSubsession.rtpSource() != NULL && !fSubsession.rtpSource()->hasBeenSynchronizedUsingRTCP()) {
		envir() << "!"; // mark the debugging output to indicate that this presentation time is not RTCP-synchronized
	}
#ifdef DEBUG_PRINT_NPT
	envir() << "\tNPT: " << fSubsession.getNormalPlayTime(presentationTime);
#endif
	envir() << "\n";
#endif
	/*	h->tag1='D';
	h->tag2='S';
	h->tag3='S';
	h->tag4='F';

	h->reserve1='0' - 48;
	h->reserve2='0' - 48;
	  printf("task=%c",h->tag1);
	  printf("%c",h->tag2);
	  printf("%c",h->tag3);
	  printf("%c",h->tag4);
	  printf("\nReserve = %d",h->reserve1);

	  h->size_of_image=frameSize;
	  cout<<"Frame Size="<<h->size_of_image<<" bytes"<<endl;

	  h->timestamp_millisec=(unsigned long long int)(presentationTime.tv_sec*1000 + presentationTime.tv_usec/1000);
	  cout<<"Timestamp in milli="<<h->timestamp_millisec<<endl;

	  h->timestamp_nanosec=0000;
	  cout<<"Timestamp in nano="<<h->timestamp_nanosec<<endl;

	  h->start_point = 0;
	  cout<<"Start Point="<<h->start_point<<endl;

	  if(strcmp(fSubsession.codecName(), "H264") == 0)
	  		  h->media_type='3';
	  	  else if(strcmp(fSubsession.codecName(), "MJPEG") == 0)
	  		  h->media_type='1';
	  	  else if(strcmp(fSubsession.codecName(), "MPEG4") == 0)
	  		  h->media_type='2';
	  	  else if(strcmp(fSubsession.codecName(), "H265") == 0)
	  		  h->media_type='4';
	  	  else if(strcmp(fSubsession.codecName(), "JPEG") == 0)
	  		  h->media_type='0';
	  	  cout<<"Media type="<<h->media_type<<endl;

		  h->frame_type='0';
		  cout<<"Frame type="<<h->frame_type<<endl;*/

	if (strcmp(fSubsession.codecName(),"H264") == 0) {
		avpkt.data = fReceiveBufferAV;
		avpkt.size = frameSize + 4;
		if (avpkt.size != 0) {
			memcpy (fReceiveBufferAV + 4, fReceiveBuffer, frameSize);
			avpkt.data = fReceiveBufferAV; //+2;

			pthread_mutex_lock(&lock1);
			Frame *newFrame = new Frame;
			newFrame->m_AllocSize = sizeof(avpkt.flags) + sizeof(avpkt.pts) + sizeof(avpkt.dts) + avpkt.size;
			newFrame->m_Buffer = new uint8_t[newFrame->m_AllocSize];
			newFrame->m_BuffSize = newFrame->m_AllocSize;

			memcpy(newFrame->m_Buffer, &(avpkt.flags),  sizeof(avpkt.flags));
			memcpy(newFrame->m_Buffer + sizeof(avpkt.flags), &(avpkt.pts),  sizeof(avpkt.pts));
			memcpy(newFrame->m_Buffer + sizeof(avpkt.flags) + sizeof(avpkt.pts), &(avpkt.dts),  sizeof(avpkt.dts));
			memcpy(newFrame->m_Buffer + sizeof(avpkt.flags) + sizeof(avpkt.pts) + sizeof(avpkt.dts) , avpkt.data,  avpkt.size );
			//printf("@@@ddddddddddddddddddd@@@@@##### %d %d\n", newFrame->m_AllocSize, m_FrameList->size());
			m_FrameList->push_back(newFrame);

			pthread_mutex_unlock(&lock1);
			pthread_mutex_destroy(&lock1);

			//printf("frame size in test =%d\n",frameSize );

			len = avcodec_decode_video2 (c, picture, &got_picture, &avpkt);
			//char *filename = "abcd.mp4";
			if (len < 0)
			{
				envir() << "Error while decoding frame" << frame;
			}
			else
			{
				FILE *fp;
				fp = fopen("test1111.mp4","ab");
				if(fp)
				{
					fwrite(picture->data[0],avpkt.size,1,fp);
					fclose(fp);
				}

			}
			frame++;
			if (avpkt.data) {
				avpkt.size -= len;
				avpkt.data += len;
			}

		}
	}

	// Then continue, to request the next frame of data:
	continuePlaying();
}

Boolean DummySink::continuePlaying() {
	if (fSource == NULL) return False; // sanity check (should not happen)

	fSource->getNextFrame(fReceiveBuffer, DUMMY_SINK_RECEIVE_BUFFER_SIZE,
			afterGettingFrame, this,
			onSourceClosure, this);

	return True;
}
