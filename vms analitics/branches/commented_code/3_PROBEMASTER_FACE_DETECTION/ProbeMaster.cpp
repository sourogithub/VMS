
/*
 * ProbeMaster.cpp
 *
 *  Created on: 06-Oct-2018
 *      Author: SHILPA
 */
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <sstream>
#include <vector>
#include <setjmp.h>
#include <string.h>
#include<time.h>
#include<sys/time.h>


#include "opencv2/imgcodecs.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/videoio.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/video.hpp>
#include "opencv2/opencv.hpp"
#include "opencv2/bgsegm.hpp"
#include "opencv2/objdetect.hpp"
#include "opencv2/tracking.hpp"

#include "jpegIO.h"
#include "jpeglib.h"
#include "DataTypes.h"
#include "ErrorTypes.h"
#include "config_SM.h"
#include "ProbeMaster.h"

using namespace cv;
using namespace std;


/*
 * Global dataspace for the module
 */

#define MAX_POSSIBLE_ZONE 	        8
#define MAX_POSSIBLE_POINT 	        8
#define PARAMETER_SIZE                  4
#define DEBUG 				0
#define REGION_SEARCHING                0
#pragma pack(1)


//Structure to hold camera information
typedef struct __CameraData {
//camid variable for holding camera id
	int camId;

	__CameraData(int _camId) {
		camId = _camId;

	}

	~__CameraData() {

	}
} CameraData;
//Structure for storing all information means camera list camera id
typedef struct __DataSpace {
	int nCam;
//Vector for storing camera list
	std::vector<CameraData*> camList;
	int idGen;
//constructor for number of camera and id
	__DataSpace() {
		nCam = 0;
		idGen = 0;
	}
//Destructor for delete the camera 
	~__DataSpace() {
		for (int cIndex = 0; cIndex < nCam; cIndex++) {
			delete camList[cIndex];
		}
		nCam = 0;
	}
} DataSpace;
//Delaring the global data space for holding overall information
static DataSpace *globalDataSpace = NULL;
// PreDefined trained XML classifiers with facial features 
static CascadeClassifier cascade;

//In the time while execution started
ErrorTypes Initialize()
{
	bool loadSuccess = false;
//Create data space
	globalDataSpace = new DataSpace();
	if(!globalDataSpace) {
		return DSS_ERROR;
	}
// Load classifiers from "opencv/data/haarcascades" directory  
	loadSuccess = cascade.load( "data/haarcascades/haarcascade_frontalface_default.xml" ) ;
	if(loadSuccess) {
		return DSS_ERROR;
	}
	return DSS_SUCCESS;
}
ErrorTypes Teardown() {
//Delete whole data space
	delete globalDataSpace;
	globalDataSpace = NULL;
	return DSS_SUCCESS;
}
//Adding Camera
ErrorTypes AddStream(UINT8 *dataSpace) {
//New camera id generated
	CameraData *cameraData = new CameraData(globalDataSpace->idGen++);
	if (cameraData == NULL) {
		return DSS_ERROR;
	}
//Put the added camera data to the global data space
	globalDataSpace->camList.push_back(cameraData);
//Increasing Number of camera
	globalDataSpace->nCam++;
//Printing Buffer size
	printf("DataSpace Size = %d", *((UINT32*) dataSpace));
//Put camera data to the buffer
	*((UINT64*) dataSpace + sizeof(char)) = (UINT64) cameraData;
	return DSS_SUCCESS;
}
//Remove Stream for the time of delete camera from UI side
ErrorTypes RemoveStream(UINT8 *dataSpace) {
//For camera information
	CameraData *cameraData =
			(CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
//Taking id of camera to be deleted
	int camId = cameraData->camId;
	cout << "Camera id to be deleted : " << cameraData->camId << endl;
//No of camera before delete from the global dataspace
	std::cout << "no of camera before delete"
			<< ((DataSpace*) globalDataSpace)->nCam << endl;
//Delete a particular camera from the list of camera
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->nCam;
			cIndex++) {
		if (((DataSpace*) globalDataSpace)->camList[cIndex]->camId == camId) {
			((DataSpace*) globalDataSpace)->camList.erase(
					((DataSpace*) globalDataSpace)->camList.begin() + cIndex);
			((DataSpace*) globalDataSpace)->nCam--;
			break;
		}
	}
//Free the Deleted camera information
	free(cameraData);
//No of camera after delete
	cout << "no of camera after delete" << ((DataSpace*) globalDataSpace)->nCam
			<< endl;
//After delete return success to the UI
	return DSS_SUCCESS;
}
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer)
{
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char))) ;
	
	return DSS_SUCCESS;
}

//API SearchEvent does the main recognition work from UI side this API getting images(frames) by parameter imageBuffer and process those.After processing  send them to the UI by the parameter eventBuffer. 
ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer, UINT8 *eventBuffer)
{
//For cameraData is for getting cameara information
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char)));
//buffSize variable storing the size of input buffer named imageBuffer.First 4byte of imageBuffer holding buffer size.
	int buffSize = *((int*)imageBuffer);
//In imageBuffer after buffer size next 8byte holding timestamp.
	UINT8 *timeStamp = (imageBuffer + sizeof(int));
//After getting timestamp, pointer points to the start point of the image data
	UINT8 *start_point = (imageBuffer + sizeof(int) + 2 * sizeof(int));
//end_point denotes the end of the image
	UINT8 *end_point   = (imageBuffer + sizeof(int) +  2 * sizeof(int) + buffSize);
//Reading image from buffer and store it into Mat format
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
        InputArray iArr(inputImage);
	Mat frame, fgbgDiff,frame1, frame2, frame3, fgImage,gray ,smallImg, croppedImage; 
	int flag = 0;
        vector<Rect> faces, faces2;
	double scale = 2;
	double fx = 1 / scale;
//imdecode for decode input frames
	frame1 = imdecode(inputImage, 1);

//Resizing frames from its current size to 320/240
	FLOAT32 scaleFactor = 1.0;
	FLOAT32 aspectRatio = frame1.rows / (FLOAT32)frame1.cols;
	if ( aspectRatio < 1.0 && frame1.cols > 320.0 )
	{
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);

	}
	else if ( aspectRatio > 1.0 && frame1.rows > 240.0)
	{
		scaleFactor = 240.0 / frame1.rows;
		printf("Col %d\n", (int)(scaleFactor * frame1.cols));
		resize( frame1, frame, Size(scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
	}
	else
	{
		frame = frame1;
	}
//Convert to gray scale image
	cvtColor( frame, gray, COLOR_BGR2GRAY );
// Resize the Grayscale Image
	resize( gray, smallImg, Size(), fx, fx, INTER_LINEAR );
	equalizeHist( smallImg, smallImg );
// Detect faces of different sizes using cascade classifier  
	cascade.detectMultiScale(gray, faces, 1.1,2, CV_HAAR_SCALE_IMAGE | CV_HAAR_DO_CANNY_PRUNING, cvSize(30,30), cvSize(200,200));
//counter for number of face count
	int cntr = 0;
// Draw circles around the faces 
	for(int i=0; i < faces.size();i++)
	{
		Rect r = faces[i];
		double aspect_ratio = (double)r.width/r.height;
		if( 0.75 < aspect_ratio && aspect_ratio < 1.3)
		{
//Getting four points of rectangle
			int x1 = faces[i].x+faces[i].width;
			int y1 = faces[i].y+faces[i].height;
			int x2 = faces[i].x;
			int y2 = faces[i].y;
			Point pt1(x1, y1);
			Point pt2(x2, y2);
			Mat faceROI = gray(faces[i]);
//Drawing rectangle
			rectangle(frame, pt1, pt2, cvScalar(0,255,0), 2, 8, 0);
//Select region of interest
			Rect myROI(faces[i].x,faces[i].y,faces[i].width,faces[i].height);
//Name of the image files to be saved
			std::string savingName =  std::to_string(++cntr) + ".jpg";
//Crop face
			croppedImage = frame(myROI);
//Save cropped images			
			imwrite(savingName, croppedImage);
//Face detected and change flag value to 1
			flag = 1;
//Number of faces count
			int k = faces.size();
			char str[200];
			sprintf(str,"NO OF FACES: %d",k);
//Writing number of faces on the frame
			putText(frame, str , Point(5,225), FONT_HERSHEY_SIMPLEX, 0.25, Scalar(0, 0, 255), 1);

		}
	}
//Resize frames to 640/480
	frame1 = frame;
	if ( aspectRatio < 1.0 )
	{
		scaleFactor = 640.0 /frame1.cols;
		resize( frame1, frame2, Size( 640, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
	}

	else
	{
		scaleFactor = 480.0 /frame1.rows;
		resize( frame1, frame2, Size( (int)(scaleFactor * frame1.cols), 480), 0, 0, INTER_CUBIC);
	}

	frame3 = frame2;
//Getting output buffer size from event buffer first 4byte 
	int buffSize1 = *((int*)eventBuffer);
	UINT8* testData1D = eventBuffer + sizeof(int);
	vector<UINT8> jpegData;
//Encode cropped face & store it as jpeg image to the vector
	imencode(".jpg", frame3, jpegData);
//Calculate the size of the encoded image size
	int size = jpegData.size();
//Put that size to the output buffer named event buffer
	*((int*)eventBuffer) = size;
//If buffer size provided by UI side API is not equals to the encoded image size or lesser than encoded image size then return insuficient memory message as  DSS_INSUFICIENT_MEMORY.
	if ( buffSize1 != size  && buffSize1 < size)
	{
		printf("Error Error ");
		return DSS_INSUFICIENT_MEMORY;
	}
////If flag value is 1 then  store 1 to the output buffer(eventbuffer) for signifing event generation otherwise write 0 to the output buffer(flag size 1byte)
	if(flag == 1)
	{
		*((char *) (eventBuffer + sizeof(int))) = 1;
	}

	else {
		*((char *) (eventBuffer + sizeof(int))) = 0;
	}
//coping image to the event buffer
	memcpy(eventBuffer + sizeof(int) + sizeof(char), &(jpegData[0]), size);

	return DSS_SUCCESS;
}
//Execute for any other task if required taking command input from UI side and perform as per requirement
ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput)
{
	return DSS_SUCCESS;
}



