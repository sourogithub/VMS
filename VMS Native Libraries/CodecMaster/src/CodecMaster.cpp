/*
 * StreamMaster.cpp
 *
 *  Created on: 06-Oct-2018
 *      Author: dss-06
 */

#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <sys/time.h>
using namespace std;

#include "CodecMaster.h"
#include "DataTypes.h"
#include "ErrorTypes.h"
#include "config_SM.h"
//#include <saveframes.h.bak>
//#include "MediaTypes.h"
//#include "ColorSpace.h"

#define INBUF_SIZE 4096
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096

#define VIDEO_TMP_FILE "tmp.h264"
#define WIDTH 640
#define HEIGHT 480
//#define FINAL_FILE_NAME "record.mp4"

/*
 * Global dataspace for the module
 */
UINT8 *globalDataSpace_d = NULL;
pthread_mutex_t ml;

typedef struct _DecodeDataSpace
{
	AVCodec *pCodec;
	AVCodecContext *pCodecCtx;
	SwsContext *img_convert_ctx;
	int size2;
	AVFrame* pic,* picrgb;
	uint8_t* picture_buf2;
	AVPacket packet;
	int decodesize;
	char fname[100];
	int frameFinished, got_output;
	int bufferSize;
	int extra, len, fileLen, len_E;
	int count;
	unsigned char RGB[3];
	char data, alpha;
	int rgbData;
	int h_index, w_index;
	int new_width, new_height;

	_DecodeDataSpace()
	{}

	~_DecodeDataSpace()
	{
		printf("\nDecodeDataSpace Destructor is invoked\n");
		av_free(pCodec);
		pCodec= NULL;

		avcodec_close(pCodecCtx);
		av_free(pCodecCtx);
		pCodecCtx = NULL;
		av_freep(pic->data[0]);
		av_freep(picrgb->data[0]); // 231119
		avcodec_free_context(&pCodecCtx);
		av_frame_unref(pic);
		av_frame_free(&pic);
		av_frame_unref(picrgb);
		av_frame_free(&picrgb);
		free(picture_buf2);
		sws_freeContext(img_convert_ctx);
		img_convert_ctx = NULL;
	}
}DecodeDataSpace;

typedef struct _EncodeDataSpace
{
	AVCodec *pCodec_Encode;
	AVCodecContext *pCodecCtx_Encode;
	AVPacket packet_E;
	AVFrame *pic_Encode;
	int size_Encode;
	uint8_t *picture_buf_Encode;
	SwsContext* rgb2yuvConvertCtx;

	_EncodeDataSpace()
	{}

	~_EncodeDataSpace()
	{}
}EncodeDataSpace;


typedef enum _MediaTypes_
{
	H264 						= 0,
	H265						= 1,
	MJPEG						= 2,
	MPEG4 						= 3,
	MP4 						= 4,
	MOV 						= 5,
	AVI							= 6,
	MKV							= 7,
	FLV							= 8,
	JPEG						= 9,
	JPG							= 10
}MediaTypes;

typedef enum _ColorSpace_
{
	RGB 						= 0,
	CMYK						= 1,
	CIE							= 2,
	YUV 						= 3,
	HSV 						= 4,
	HSL 						= 5
}ColorSpace;

ErrorTypes Initializez()
{
	//TO DO: Call RTSP initializer here

	globalDataSpace_d = (UINT8 *)(new DecodeDataSpace);

	if ( NULL == globalDataSpace_d )
	{
		return DSS_ERROR;
	}

	avcodec_register_all();
	av_register_all();

	if (pthread_mutex_init(&ml, NULL) != 0){
		printf("\n mutex init has failed\n");
		return DSS_ERROR;
	}
	return DSS_SUCCESS;
}

ErrorTypes AddDecoder(UINT8 *dataSpace, const CHAR8 mediaType,
		const CHAR8 colorSpace)
{
	//std::cout << "CODECMaster AddDecoder: inside AddDecoder" << std::endl;
	if (*((INT32 *)dataSpace) < (INT32)sizeof(DecodeDataSpace *)){
		*((INT32 *)dataSpace) = sizeof(DecodeDataSpace *);
		return DSS_INSUFICIENT_MEMORY;
	}
	//std::cout << "CODECMaster AddDecoder: memory sufficiency analyzed" << std::endl;

	pthread_mutex_lock(&ml);
	DecodeDataSpace *sDataSpace = new DecodeDataSpace();
	pthread_mutex_unlock(&ml);
	//std::cout << "CODECMaster AddDecoder: decode buffer allocated" << std::endl;

	sDataSpace->pCodec = avcodec_find_decoder(AV_CODEC_ID_H264);
	//std::cout << "CODECMaster AddDecoder: found decoder" << std::endl;

	sDataSpace->pCodecCtx = avcodec_alloc_context3(sDataSpace->pCodec);
	sDataSpace->pCodecCtx->width = 1280;
	sDataSpace->pCodecCtx->height = 720;
	sDataSpace->new_width = sDataSpace->pCodecCtx->width;
	sDataSpace->new_height = sDataSpace->pCodecCtx->height;
	sDataSpace->pCodecCtx->pix_fmt = AV_PIX_FMT_YUV420P;

	avcodec_open2(sDataSpace->pCodecCtx, sDataSpace->pCodec, NULL);
	//std::cout << "CODECMaster AddDecoder: decoder opened" << std::endl;
	std::cout << "CODECMaster AddDecoder: Decoder found and initialized" << std::endl;

	sDataSpace->img_convert_ctx = sws_getContext(sDataSpace->pCodecCtx->width, sDataSpace->pCodecCtx->height, sDataSpace->pCodecCtx->pix_fmt, 1280, 720,
			AV_PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);

	sDataSpace->pic = av_frame_alloc();
	sDataSpace->picrgb = av_frame_alloc();
	sDataSpace->size2 = avpicture_get_size(AV_PIX_FMT_RGB24, 1280, 720);
	sDataSpace->picture_buf2 = (uint8_t*)(av_malloc(sDataSpace->size2));
	avpicture_fill((AVPicture *) sDataSpace->picrgb, sDataSpace->picture_buf2, AV_PIX_FMT_RGB24, 1280, 720);
	//std::cout << "CODECMaster AddDecoder: picture buffer filled" << std::endl;
	av_init_packet(&sDataSpace->packet);

	*((UINT64 *)dataSpace + sizeof(char)) = (UINT64)sDataSpace;
	//std::cout << "CODECMaster AddDecoder: decode buffer added" << std::endl;

	return DSS_SUCCESS;
}

#if 1
// backup
ErrorTypes Decode(UINT8 *dataSpace, UINT8 *encodedBuffer, UINT32 *decodedBuffer)
{
	//std::cout << "CODECMaster Decode: inside decode" << std::endl;
	pthread_mutex_lock(&ml);
	DecodeDataSpace *sDataSpace = (DecodeDataSpace *)(*((UINT64 *)dataSpace + sizeof(char)));
	pthread_mutex_unlock(&ml);
	//std::cout << "CODECMaster Decode: decode buffer isolated" << std::endl;

	//printf("Width before = %d\n",sDataSpace->pCodecCtx->width);
	//printf("Height before = %d\n",sDataSpace->pCodecCtx->height);
	//printf("Width after = %d\n",sDataSpace->new_width);
	//printf("Height after = %d\n",sDataSpace->new_height);

	sDataSpace->bufferSize = *(int *)encodedBuffer;
	//printf("Size of encoded buffer = %d\n",sDataSpace->bufferSize);
	sDataSpace->extra = sizeof(int) + sizeof(int64_t) + sizeof(int64_t);

	sDataSpace->packet.flags = *((int *)(encodedBuffer + sizeof(int)));
	sDataSpace->packet.pts = *((int64_t *)(encodedBuffer + sizeof(int) + sizeof(int)));
	sDataSpace->packet.dts = *((int64_t *)(encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t)));
	sDataSpace->packet.data = encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t) + sizeof(int64_t);
	sDataSpace->packet.size = sDataSpace->bufferSize - sDataSpace->extra;
	//std::cout << "CODECMaster Decode: encoded packet recreated" << std::endl;
	std::cout << "CODECMaster Decode: Recreated packet size: " << sDataSpace->packet.size << std::endl;

	//std::cout << "CODECMaster Decode: about to decode" << std::endl;
	sDataSpace->len = avcodec_decode_video2(sDataSpace->pCodecCtx, sDataSpace->pic, &sDataSpace->frameFinished, &sDataSpace->packet);
	//printf("Status of avcodec = %d\n",sDataSpace->len);
	if(sDataSpace->len < 0) {
		std::cout << "CODECMaster Decode: decode failed" << std::endl;
 		return DSS_ERROR;
	}
	std::cout << "CODECMaster Decode: Packet Decoded" << std::endl;

#if 1
	//Testing
	sDataSpace->fileLen = sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height * sizeof(int);	// Multiply by 4 because of int type convertion  from char
	//printf("File length = %d\n",sDataSpace->fileLen);
	//std::cout << "CODECMaster Decode: memory sufficiency analyzed" << std::endl;
	if (*((INT32 *)decodedBuffer) < sDataSpace->fileLen + sizeof(int) + sizeof(int)){
		*((INT32 *)decodedBuffer) = sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height;
		printf("Returning INSUFFICIENT MEMORY: SIZE REQUIRED - %d \n", sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height);
		return DSS_INSUFICIENT_MEMORY;
	}

	*(decodedBuffer + sizeof(char)) = sDataSpace->new_width;
	//printf("Width in c = %d\n", *(decodedBuffer + sizeof(char)));

	*(decodedBuffer + sizeof(char) + sizeof(char)) = sDataSpace->new_height;
	//printf("Height in c = %d\n", *(decodedBuffer + sizeof(char) + sizeof(char)));

	sws_scale(sDataSpace->img_convert_ctx, sDataSpace->pic->data, sDataSpace->pic->linesize, 0, sDataSpace->pCodecCtx->height, sDataSpace->picrgb->data, sDataSpace->picrgb->linesize);
	//std::cout << "CODECMaster Decode: decode frame is scaled" << std::endl;
	sDataSpace->count = 3;//sizeof(char) + sizeof(char) + sizeof(char);
	//writing data

	for(sDataSpace->h_index = 0; sDataSpace->h_index < sDataSpace->new_height; sDataSpace->h_index++)
	{
		//		unsigned char RGB[3];
		for(sDataSpace->w_index = 1; sDataSpace->w_index <= sDataSpace->new_width * 3; sDataSpace->w_index++)
		{
			sDataSpace->data = (sDataSpace->picrgb->data[0] + sDataSpace->h_index * sDataSpace->picrgb->linesize[0])[sDataSpace->w_index - 1];
			sDataSpace->RGB[(sDataSpace->w_index - 1) % 3] = sDataSpace->data;

			if(sDataSpace->w_index % 3 == 0) {
				// i have got RGB values
				//now all we need to do is put into the buffer in correct format
				sDataSpace->rgbData = 0;
				sDataSpace->alpha = 255;
				//RGB -> 0:red  1:green  2:blue
				sDataSpace->rgbData = (sDataSpace->alpha << 24) | (sDataSpace->RGB[0] << 16) | (sDataSpace->RGB[1] << 8) | (sDataSpace->RGB[2] << 0);

				decodedBuffer[sDataSpace->count++] = sDataSpace->rgbData;
			}
		}
	}
	//std::cout << "CODECMaster Decode: decoded frame written in memory" << std::endl;

#endif

#if 0
	FILE *fp;
	static int c = 0;
	sprintf(sDataSpace->fname, "result_from_c_%d.ppm", c++);
	fp = fopen(sDataSpace->fname, "wb");
	if(fp)
	{
		sws_scale(sDataSpace->img_convert_ctx, sDataSpace->pic->data, sDataSpace->pic->linesize, 0, sDataSpace->pCodecCtx->height, sDataSpace->picrgb->data, sDataSpace->picrgb->linesize);
		fprintf(fp,"P6\n%d %d\n%d\n",sDataSpace->pCodecCtx->width,sDataSpace->pCodecCtx->height,255);

		for(int i=0;i<sDataSpace->pCodecCtx->height;i++) {
			fwrite(sDataSpace->picrgb->data[0] + i * sDataSpace->picrgb->linesize[0], 1, sDataSpace->pCodecCtx->width * 3, fp);
		}
		fclose(fp);
	}
	else
	{
		printf("Error opening file\n");
		exit(1);
	}
#endif

	av_packet_unref(&sDataSpace->packet);
	av_free(sDataSpace->packet.data);
	av_free_packet(&sDataSpace->packet);
	av_init_packet(&sDataSpace->packet);
	//av_freep(sDataSpace->pic->data[0]); // 231119
	//av_freep(sDataSpace->picrgb->data[0]); // 231119
	//std::cout << "CODECMaster Decode: end of decoding" << std::endl;

	return DSS_SUCCESS;
}
#endif
#if 0
ErrorTypes Decode(UINT8 *dataSpace, UINT8 *encodedBuffer, UINT32 *decodedBuffer)
{
	pthread_mutex_lock(&ml);
	DecodeDataSpace *sDataSpace = (DecodeDataSpace *)(*((UINT64 *)dataSpace + sizeof(char)));
	pthread_mutex_unlock(&ml);

	sDataSpace->bufferSize = *(int *)encodedBuffer;
	sDataSpace->extra = sizeof(int) + sizeof(int64_t) + sizeof(int64_t);

	sDataSpace->packet.flags = *((int *)(encodedBuffer + sizeof(int)));
	sDataSpace->packet.pts = *((int64_t *)(encodedBuffer + sizeof(int) + sizeof(int)));
	sDataSpace->packet.dts = *((int64_t *)(encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t)));
	sDataSpace->packet.data = encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t) + sizeof(int64_t);
	sDataSpace->packet.size = sDataSpace->bufferSize - sDataSpace->extra;
	std::cout << "CODECMaster Decode: Recreated packet size: " << sDataSpace->packet.size << std::endl;

	sDataSpace->len = avcodec_decode_video2(sDataSpace->pCodecCtx, sDataSpace->pic, &sDataSpace->frameFinished, &sDataSpace->packet);
	//printf("Status of avcodec = %d\n",sDataSpace->len);
	if(sDataSpace->len < 0) {
		std::cout << "CODECMaster Decode: decode failed" << std::endl;
 		return DSS_ERROR;
	}
	std::cout << "CODECMaster Decode: Packet Decoded" << std::endl;

#if 1
	sws_scale(sDataSpace->img_convert_ctx, sDataSpace->pic->data, sDataSpace->pic->linesize, 0, sDataSpace->pCodecCtx->height, sDataSpace->picrgb->data, sDataSpace->picrgb->linesize);
	std::cout << "CODECMaster Decode: decode frame is scaled" << std::endl;
	//Testing
	int totalDataSize= 0;
	for (int i=0;i<AV_NUM_DATA_POINTERS;i++) {
		totalDataSize += sDataSpace->picrgb->linesize[i];
	}

	sDataSpace->fileLen = sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height * sizeof(int);
	if (*((INT32 *)decodedBuffer) < sDataSpace->fileLen + sizeof(int) + sizeof(int) + (8*sizeof(int)) + totalDataSize){
		*((INT32 *)decodedBuffer) = (sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height) + (8*sizeof(int)) + totalDataSize;
		printf("Returning INSUFFICIENT MEMORY: SIZE REQUIRED - %d \n", sDataSpace->pCodecCtx->width * sDataSpace->pCodecCtx->height);
		return DSS_INSUFICIENT_MEMORY;
	}

	*(decodedBuffer + sizeof(char)) = sDataSpace->new_width;

	*(decodedBuffer + sizeof(char) + sizeof(char)) = sDataSpace->new_height;



	// dumping picRGB data to decodedBuffer
	sDataSpace->count= sizeof(int) * 3;
	for(int i=0; i< AV_NUM_DATA_POINTERS; i++) {
		memcpy(((UINT8*)decodedBuffer) + sDataSpace->count,&(sDataSpace->picrgb->linesize[i]),sizeof(int));
		sDataSpace->count += sizeof(int);
		memcpy(((UINT8*)decodedBuffer)+sDataSpace->count,sDataSpace->picrgb->data[i],sDataSpace->picrgb->linesize[i]);
		sDataSpace->count += sDataSpace->picrgb->linesize[i];
	}

	//sDataSpace->count= 3;//sizeof(char) + sizeof(char) + sizeof(char);
	//writing data

	for(sDataSpace->h_index = 0; sDataSpace->h_index < sDataSpace->new_height; sDataSpace->h_index++)
	{
		//		unsigned char RGB[3];
		for(sDataSpace->w_index = 1; sDataSpace->w_index <= sDataSpace->new_width * 3; sDataSpace->w_index++)
		{
			sDataSpace->data = (sDataSpace->picrgb->data[0] + sDataSpace->h_index * sDataSpace->picrgb->linesize[0])[sDataSpace->w_index - 1];
			sDataSpace->RGB[(sDataSpace->w_index - 1) % 3] = sDataSpace->data;

			if(sDataSpace->w_index % 3 == 0) {
				// i have got RGB values
				//now all we need to do is put into the buffer in correct format
				sDataSpace->rgbData = 0;
				sDataSpace->alpha = 255;
				//RGB -> 0:red  1:green  2:blue
				sDataSpace->rgbData = (sDataSpace->alpha << 24) | (sDataSpace->RGB[0] << 16) | (sDataSpace->RGB[1] << 8) | (sDataSpace->RGB[2] << 0);

				decodedBuffer[sDataSpace->count++] = sDataSpace->rgbData;
			}
		}
	}
	//std::cout << "CODECMaster Decode: decoded frame written in memory" << std::endl;

#endif

	av_packet_unref(&sDataSpace->packet);
	av_free(sDataSpace->packet.data);
	av_free_packet(&sDataSpace->packet);
	av_init_packet(&sDataSpace->packet);


	return DSS_SUCCESS;
}
#endif

ErrorTypes RemoveDecoder(UINT8 *dataSpace)
{
	pthread_mutex_lock(&ml);
	DecodeDataSpace *sDataSpace = (DecodeDataSpace *)(*((UINT64 *)dataSpace + sizeof(char)));
	pthread_mutex_unlock(&ml);

	//Encoding
	/*avcodec_close(sDataSpace->pCodecCtx);
	av_free(sDataSpace->pCodecCtx);
	av_freep(&sDataSpace->pic->data[0]);
	avcodec_free_context(&sDataSpace->pCodecCtx);


	av_frame_unref(sDataSpace->pic);
	av_frame_free(&sDataSpace->pic);
	av_frame_unref(sDataSpace->picrgb);
	av_frame_free(&sDataSpace->picrgb);
	//free(sDataSpace->picture_buf);
	free(sDataSpace->picture_buf2);

	//avcodec_free_context(&sDataSpace->pCodecCtx);
	sDataSpace->pCodecCtx = NULL;

	sws_freeContext(sDataSpace->img_convert_ctx);
	sDataSpace->img_convert_ctx = NULL;*/

	delete sDataSpace;
	sDataSpace = NULL;

	return DSS_SUCCESS;
}

//void *Test(void *arg)
//{
//	if (pthread_mutex_init(&ml, NULL) != 0){
//		printf("\n mutex init has failed\n");
//		return NULL;
//	}
//
//	//StreamDataSpace *sDataSpace;
//	Data *d = (Data *)arg;
//
//	d->count = 1;
//	while(true)
//	{
//		if(d->count == 1)
//		{
//			AddStream(d->buffer, d->model, d->url, d->user, d->pass, d->camId);
//			d->count++;
//		}
//
//		int x = GetContent(d->buffer, d->dataBuffer);
//		printf("\nReturn Status = %d\n",x);
//	}
//	pthread_mutex_destroy(&ml);
//	return NULL;
//}

ErrorTypes Decode(const CHAR8 mediaType, const CHAR8 colorSpace,
		UINT8 *encodedBuffer, UINT32 *decodedBuffer)
{
	return DSS_SUCCESS;
}

/* -------------------------------- Encoder section ------------------------------ */

ErrorTypes AddEncoder(UINT8 *dataSpace, const CHAR8 codec, const CHAR8 colorSpace,
		const UINT32 width, const UINT32 height, const UINT32 bitRate)
{
#if 1

	if (*((INT32 *)dataSpace) < (INT32)sizeof(EncodeDataSpace *)) {
		*((INT32 *)dataSpace) = sizeof(EncodeDataSpace *);
		return DSS_INSUFICIENT_MEMORY;
	}

	pthread_mutex_lock(&ml);
	EncodeDataSpace *sDataSpace = new EncodeDataSpace();
	pthread_mutex_unlock(&ml);

	if((int)codec == H264)
		sDataSpace->pCodec_Encode = avcodec_find_encoder(AV_CODEC_ID_H264);
	else
		sDataSpace->pCodec_Encode = avcodec_find_encoder(AV_CODEC_ID_H264);
	if (!sDataSpace->pCodec_Encode) {
		printf("Encoding: Codec not found\n");
		return DSS_ERROR;
	}
	std::cout << "Encoding: codec found" << std::endl;
	sDataSpace->pCodecCtx_Encode = avcodec_alloc_context3(sDataSpace->pCodec_Encode);
	if (!sDataSpace->pCodecCtx_Encode)
	{
		printf("Encoding: Could not allocate video codec context\n");
		return DSS_ERROR;
	}
	std::cout << "Encoding: codec context allocated" << std::endl;

	sDataSpace->pCodecCtx_Encode->bit_rate = 400000;
	sDataSpace->pCodecCtx_Encode->width = 1280;
	sDataSpace->pCodecCtx_Encode->height = 720;
	sDataSpace->pCodecCtx_Encode->time_base= (AVRational){1,25};
	sDataSpace->pCodecCtx_Encode->pix_fmt = AV_PIX_FMT_YUV420P;

	if(avcodec_open2(sDataSpace->pCodecCtx_Encode, sDataSpace->pCodec_Encode, NULL) < 0) {
		printf("Encoding: Could not open codec\n");
		return DSS_ERROR;
	}
	std::cout << "Encoding: Encoder opened" << std::endl;

	sDataSpace->pic_Encode = av_frame_alloc();
	if (!sDataSpace->pic_Encode) {
		printf("Encoding: Could not allocate video frame\n");
		return DSS_ERROR;
	}
	sDataSpace->pic_Encode->format = sDataSpace->pCodecCtx_Encode->pix_fmt;
	sDataSpace->pic_Encode->width  = sDataSpace->pCodecCtx_Encode->width;
	sDataSpace->pic_Encode->height = sDataSpace->pCodecCtx_Encode->height;
	std::cout << "Encoding: YUV Frame allocated and initialized" << std::endl;

	// to convert frames from RGB color-base to YUV color-base
	sDataSpace->rgb2yuvConvertCtx = sws_getContext(sDataSpace->pCodecCtx_Encode->width, sDataSpace->pCodecCtx_Encode->height, AV_PIX_FMT_RGB24, sDataSpace->pCodecCtx_Encode->width, sDataSpace->pCodecCtx_Encode->height, sDataSpace->pCodecCtx_Encode->pix_fmt, SWS_BILINEAR, NULL, NULL, NULL);
	if(!sDataSpace->rgb2yuvConvertCtx) {
		std::cout << "Encoding: failed to get sws converter context" << std::endl;
		return DSS_ERROR;
	}
	std::cout << "Encoding: sws context allocated" << std::endl;

	sDataSpace->size_Encode = avpicture_get_size(AV_PIX_FMT_YUV420P, sDataSpace->pCodecCtx_Encode->width, sDataSpace->pCodecCtx_Encode->height);
	sDataSpace->picture_buf_Encode = (uint8_t*)(av_malloc(sDataSpace->size_Encode));

	avpicture_fill((AVPicture *) sDataSpace->pic_Encode, sDataSpace->picture_buf_Encode, AV_PIX_FMT_YUV420P, sDataSpace->pCodecCtx_Encode->width, sDataSpace->pCodecCtx_Encode->height);
	std::cout << "Encoding: buffer added to the YUV frame" << std::endl;

	av_init_packet(&sDataSpace->packet_E);

	*((UINT64 *)dataSpace + sizeof(char) + sizeof(char)) = (UINT64)sDataSpace;

#endif

	return DSS_SUCCESS;
}

ErrorTypes RemoveEncoder(UINT8 *dataSpace) {
#if 1

	pthread_mutex_lock(&ml);
	EncodeDataSpace *sDataSpace = (EncodeDataSpace *)(*((UINT64 *)dataSpace + sizeof(char) + sizeof(char)));
	pthread_mutex_unlock(&ml);

	avcodec_close(sDataSpace->pCodecCtx_Encode);
	avcodec_free_context(&sDataSpace->pCodecCtx_Encode);
	sDataSpace->pCodecCtx_Encode = NULL;
	av_freep(&sDataSpace->pic_Encode->data[0]);

	av_frame_unref(sDataSpace->pic_Encode);
	av_frame_free(&sDataSpace->pic_Encode);

	free(sDataSpace->picture_buf_Encode);
	sws_freeContext(sDataSpace->rgb2yuvConvertCtx);
	sDataSpace->rgb2yuvConvertCtx = NULL;

	delete(sDataSpace);
	sDataSpace = NULL;

#endif

	return DSS_SUCCESS;
}

ErrorTypes Encode(UINT8* dataSpace, UINT32* decodedBuffer, UINT8* encodedBuffer) {
#if 1

	pthread_mutex_lock(&ml);
	EncodeDataSpace *sDataSpace = (EncodeDataSpace *)(*((UINT64 *)dataSpace + sizeof(char) + sizeof(char)));
	pthread_mutex_unlock(&ml);

	// recreate RGB frame from decodedBuffer
	int count= 1;
	AVFrame* frameRGB= av_frame_alloc();
	frameRGB->width= *(decodedBuffer + (count++));
	frameRGB->height= *(decodedBuffer + (count++));

	count *= sizeof(int);
	for(int i=0; i< AV_NUM_DATA_POINTERS; i++) {
		memcpy(&frameRGB->linesize[i],((UINT8*)decodedBuffer) + count, sizeof(int));
		count += sizeof(int);
		frameRGB->data[i]= new UINT8[frameRGB->linesize[i]];
		memcpy(frameRGB->data[i],((UINT8*)decodedBuffer) + count,frameRGB->linesize[i]);
		count += frameRGB->linesize[i];
	}

	std::cout << "ENCODE DEBUG 0" << std::endl;
	// alter the color-base from RGB24 to YUV420
	sws_scale(sDataSpace->rgb2yuvConvertCtx,frameRGB->data,frameRGB->linesize,0,sDataSpace->pCodecCtx_Encode->height,sDataSpace->pic_Encode->data,sDataSpace->pic_Encode->linesize);
	std::cout << "ENCODE DEBUG 1" << std::endl;
	// encode YUV Frame into H264 Packet
	int res= avcodec_send_frame(sDataSpace->pCodecCtx_Encode,sDataSpace->pic_Encode);
	std::cout << "ENCODE DEBUG 2" << std::endl;
	if(!res) {
		res= avcodec_receive_packet(sDataSpace->pCodecCtx_Encode,&(sDataSpace->packet_E));
		if(!res) {
			std::cout << "Received H264 Packet" << std::endl;
		}
		else {
			std::cout << "Failed to receive Encoded Packet" << std::endl;
			return DSS_ERROR;
		}
	}
	else {
		std::cout << "Failed to send Frame" << std::endl;
		return DSS_ERROR;
	}

	// check for buffer sufficiency
	int extra = sizeof(int) + sizeof(int64_t) + sizeof(int64_t);
	if (*((INT32 *)encodedBuffer) < sDataSpace->packet_E.size + extra) {
		*((INT32 *)encodedBuffer) = sDataSpace->packet_E.size + extra;
		return DSS_INSUFICIENT_MEMORY;
	}

	// write H264 packet into encodeBuffer
	memcpy(encodedBuffer + sizeof(int), &sDataSpace->packet_E.flags, sizeof(int) );
	memcpy(encodedBuffer + sizeof(int) + sizeof(int), &sDataSpace->packet_E.pts, sizeof(int64_t));
	memcpy(encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t), &sDataSpace->packet_E.dts, sizeof(int64_t));
	memcpy(encodedBuffer + sizeof(int) + sizeof(int) + sizeof(int64_t) + sizeof(int64_t), sDataSpace->packet_E.data, sDataSpace->packet_E.size);

	av_frame_unref(frameRGB);
	av_frame_free(&frameRGB);

#endif

	return DSS_SUCCESS;
}

/* ----------------------- End of Encoder section ----------------------- */

/*
ErrorTypes addEncoder(UINT8 *dataSpace, const CHAR8 codec, const CHAR8 colorSpace, const UINT32 width, const UINT32 height, const UINT32 bitRate)
{
	return DSS_SUCCESS;
}

ErrorTypes removeEncoder(UINT8 *dataSpace)
{
	return DSS_SUCCESS;
}

ErrorTypes encode(UINT8 *dataSpace, UINT8 *encodedBuffer, UINT32 *decodedBuffer)
{
	return DSS_SUCCESS;
}
*/


ErrorTypes TearDown()
{
	//TO DO: close RTSP stream here
	delete (DecodeDataSpace *)globalDataSpace_d;
	if ( NULL != globalDataSpace_d )
	{
		return DSS_ERROR;
	}

	return DSS_SUCCESS;
}
