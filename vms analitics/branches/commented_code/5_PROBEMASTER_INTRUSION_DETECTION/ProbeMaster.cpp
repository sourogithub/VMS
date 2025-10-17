	


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
#include <time.h>
#include <sqlite3.h>
#include<sys/time.h>


#include "opencv2/imgcodecs.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/videoio.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/video.hpp>
#include "opencv2/opencv.hpp"
#include "opencv2/bgsegm.hpp"
#include "opencv2/objdetect.hpp"
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
#define AREA_TH_1 			50
#define PIX_FOREGROUND		        255
#define PIX_BACKGROUND		        0
#define DESCRIPTOR_LENGTH 	        80

#define MAX_POSSIBLE_ZONE 	        8
#define MAX_POSSIBLE_POINT 	        8
#define PARAMETER_SIZE                  4
#define DEBUG 				0

#pragma pack(1)
//This structure for storing information for  components
typedef struct _CompInfo
{
	int topRow;
	int leftCol;
	int bottomRow;
	int rightCol;
	FLOAT32 colorDesc[DESCRIPTOR_LENGTH];
	int status;
}CompInfo;
//Storing the information about the selected region
typedef struct _Region
{
	int npoint ;					//this represents the total no of points
	int x[MAX_POSSIBLE_POINT];
	int y[MAX_POSSIBLE_POINT];
	int param[PARAMETER_SIZE];
}Region;

typedef struct _RegionHistory{
	int region_no = -1;
	vector<Region  *> regionHistory1;
}RegionHistory;
//Structure for 
typedef struct __CameraData
{
	int camId;
//MOG2 Background subtractor
	Ptr<BackgroundSubtractorMOG2> pMOG2;
	FLOAT64 lRate;
	BOOL toggleFlag;
	vector<vector<CompInfo *> *> blobHistory;
	RegionHistory  *region_Data;

	__CameraData(int _camId)
	{
		camId = _camId;
//create Background Subtractor objects
		pMOG2 = createBackgroundSubtractorMOG2();
		lRate = -1.0;
		toggleFlag = false;
		region_Data = new RegionHistory();
	}

	~__CameraData()
	{
		delete pMOG2;
	}
}CameraData;

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



//Delaring the global data space for holding overall information
static DataSpace *globalDataSpace = NULL;
//global variables
static int prev_data = 1;
static int flag1 = 0;
//In the time while execution started
ErrorTypes Initialize() {
//Create data space
	globalDataSpace = new DataSpace();
	if (!globalDataSpace) {
		return DSS_ERROR;
	}
	return DSS_SUCCESS;
}
//End of whole execution
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
//If requirement of analytics for a particular region
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer)
{
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char))) ;
//OFFSET holds size of each 
+buffer element 
	int OFFSET = 0;
/* fetching first 4bytes as  buffer size */
	int bufferSize = *((int *) (regionBuffer + OFFSET));		
	OFFSET += sizeof(int);
/* getting no of regions */
	INT16 noOfRegions = *((INT16 *)(regionBuffer + OFFSET));	
	OFFSET += sizeof(short);

	cameraData->region_Data->region_no = noOfRegions;
	cameraData->region_Data->regionHistory1.clear();

	for(int regionNumber = 1; regionNumber <= noOfRegions; regionNumber++) {
		int noofPoints = *((char *)(regionBuffer + OFFSET));
		OFFSET += sizeof(char);
/* creating new Region */
		Region *newComp = new Region;						
		newComp->npoint = noofPoints;
//Getting points for a paricular region
		for(int pointNumber = 0; pointNumber < noofPoints; pointNumber++)
		{
			newComp->x[pointNumber] = *((int *) (regionBuffer + OFFSET));
			OFFSET += sizeof(int);
			newComp->y[pointNumber] = *((int *) (regionBuffer + OFFSET));
			OFFSET += sizeof(int);
		}

		OFFSET += sizeof(int) * 4;

		cameraData->region_Data->regionHistory1.push_back(newComp);
	}

	return DSS_SUCCESS;
}

//API SearchEvent does the main recognition work from UI side this API getting images(frames) by parameter imageBuffer and process those.After processing  send them to the UI by the parameter eventBuffer.  
ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer,
		UINT8 *eventBuffer) try{
 	
//For cameraData is for getting cameara information
	CameraData *cameraData =
			(CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
//buffSize variable storing the size of input buffer named imageBuffer.First 4byte of imageBuffer holding buffer size.
	int buffSize = *((int*) imageBuffer);
//In imageBuffer after buffer size next 8byte holding timestamp.
	UINT8 *timeStamp = (imageBuffer + sizeof(int));
//After getting timestamp, pointer points to the start point of the image data
	UINT8 *start_point = (imageBuffer + sizeof(int) + 2 * sizeof(int));
//end_point denotes the end of the image
	UINT8 *end_point = (imageBuffer + sizeof(int) +  2 * sizeof(int) + buffSize);
//Reading image from buffer and store it into Mat format
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
	Mat frame, fgbgDiff,frame3,frame2,frame1 ;
	int height = 0,width = 0;
	InputArray iArr(inputImage);
	Mat frame4(640, 480, CV_8UC3, Scalar(0, 0, 0));
	int frame_count = 0;
//imdecode for decode input frames
	frame1 = imdecode(inputImage, 1);
	frame_count ++;
//Resize input frames 320*240
	FLOAT32 scaleFactor = 1.0;
	FLOAT32 aspectRatio = frame1.rows / (FLOAT32)frame1.cols;
	if ( aspectRatio < 1.0 && frame1.cols > 320.0 )
	{
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
		resize( frame4, frame4, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
	}
	else if ( aspectRatio > 1.0 && frame1.rows > 240.0)
	{
		scaleFactor = 240.0 / frame1.rows;
		printf("Col %d\n", (int)(scaleFactor * frame1.cols));
		resize( frame1, frame, Size( scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
		resize( frame4, frame4, Size( scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
	}
	else
	{
		frame = frame1;
	}
//Getting height width of the frame
	int imgRow = frame.rows;
	int imgCol = frame.cols;
	Mat frame5;
	printf("lRate = %lf\n", cameraData->lRate);
//update the background model
	cameraData->pMOG2->apply(frame, fgbgDiff, cameraData->lRate);
	Mat fgImage;

//fgbgDiff: Our input image
//fgImage: Destination (output) image
//200: The threshold value with respect to which the thresholding operation is made
//PIX_FOREGROUND: The value used with the Binary thresholding operations (to set the chosen pixels)
//THRESH_BINARY: One of the 5 thresholding operations.

	threshold(fgbgDiff, fgImage, 200, PIX_FOREGROUND, THRESH_BINARY);
//We manually created a structuring elements. It is rectangular shape. So for this purpose, OpenCV has a function,getStructuringElement(). You just pass the shape and size of the kernel, you get the desired kernel.
	Mat element = getStructuringElement( MORPH_RECT, Size(4, 8));
	Mat tmp1;
	Mat tmp2;
//perform the morphology transformations is morphologyEx.we use four arguments
//fgImage : Source (input) image
//tmp1: Output image
//operation: The kind of morphology transformation to be performed.
//element: The kernel to be used. We use the function cv::getStructuringElement to define our own structure.
	morphologyEx( fgImage, tmp1, MORPH_OPEN, element );
	Mat	labels(1, buffSize, CV_8UC1, start_point);
	Mat stats;
	Mat centroids;
//computes the connected components labeled image  
	int nComp = connectedComponentsWithStats(tmp1, labels, stats, centroids,8, CV_32SC1);
	printf("Component object = %d\n",nComp);
//Background Subtraction and removes smaller unnecessary blobs	
	for(int cIndex = 1; cIndex <= nComp; cIndex ++)
	{
		int compLabel = cIndex;
//Getting height,width,top row left col and calculates area  from them
		int leftCol = stats.at<int>(Point(CC_STAT_LEFT, cIndex));
		int topRow = stats.at<int>(Point(CC_STAT_TOP, cIndex));
		int compWidth = stats.at<int>(Point(CC_STAT_WIDTH, cIndex));
		int compHeight = stats.at<int>(Point(CC_STAT_HEIGHT, cIndex));
		int compArea = stats.at<int>(Point(CC_STAT_AREA, cIndex));
//compares threshold with calculated area of component
		if ( compArea < AREA_TH_1 )
		{
			for ( int rIndex = 0; rIndex < imgRow; rIndex ++ )
			{
				for ( int cIndex = 0; cIndex < imgCol; cIndex ++ )
				{
					if ( labels.at<int>(rIndex, cIndex) == compLabel )
					{
						tmp1.at<UINT8>(rIndex, cIndex) = PIX_BACKGROUND;
					}
				}
			}
		}
	}

	Mat	labelsFg, statsFg, centroidsFg;
//computes the connected components labeled image  
	int nCompFg = connectedComponentsWithStats(tmp1, labelsFg, statsFg, centroidsFg,8);
	tmp2 = Scalar::all(PIX_FOREGROUND) - tmp1;
	Mat	labelsBg, statsBg, centroidsBg;
//computes the connected components labeled image  
	int nCompBg = connectedComponentsWithStats(tmp2, labelsBg, statsBg, centroidsBg, 8);

	for(int fgIndex = 1; fgIndex < nCompFg; fgIndex ++)
	{
//Getting height,width,top row left col and bottom row right column(Four points of rectangle)
		int compLabel = fgIndex;
		int leftCol = statsFg.at<int>(Point(CC_STAT_LEFT, fgIndex));
		int topRow = statsFg.at<int>(Point(CC_STAT_TOP, fgIndex));
		int compWidth = statsFg.at<int>(Point(CC_STAT_WIDTH, fgIndex));
		int compHeight = statsFg.at<int>(Point(CC_STAT_HEIGHT, fgIndex));
		int rightCol = leftCol + compWidth - 1;
		int bottomRow = topRow + compHeight - 1;
//This loop is for component labelling
		for(int bgIndex = 1; bgIndex < nCompBg; bgIndex ++)
		{
			int compLabelBg = bgIndex;
			int leftColBg = statsBg.at<int>(Point(CC_STAT_LEFT, bgIndex));
			int topRowBg = statsBg.at<int>(Point(CC_STAT_TOP, bgIndex));
			int compWidthBg = statsBg.at<int>(Point(CC_STAT_WIDTH, bgIndex));
			int compHeightBg = statsBg.at<int>(Point(CC_STAT_HEIGHT, bgIndex));
			int rightColBg = leftColBg + compWidthBg - 1;
			int bottomRowBg = topRowBg + compHeightBg - 1;

			if ( topRow < topRowBg && leftCol < leftColBg &&
					bottomRow > bottomRowBg && rightCol > rightColBg)
			{
				for ( int rIndex = 0; rIndex < imgRow; rIndex ++ )
				{
					for ( int cIndex = 0; cIndex < imgCol; cIndex ++ )
					{
						if ( labelsBg.at<int>(rIndex, cIndex) == compLabelBg )
						{
							tmp1.at<UINT8>(rIndex, cIndex) = PIX_FOREGROUND;
						}
					}
				}
			}
		}
	}
//perform the morphology transformations is morphologyEx
	element = getStructuringElement( MORPH_RECT, Size(5,5));
	morphologyEx( tmp1, fgImage, MORPH_CLOSE, element );
//Getting number of connected component
	nComp = connectedComponentsWithStats(fgImage, labels, stats, centroids,8, CV_32SC1);
	vector<CompInfo *> *blobList = new vector<CompInfo *>;
	int totalArea = 0,area = 0;
	int flag = 0;

//Push the values to the structure component wise
	for(int cIndex = 1; cIndex < nComp; cIndex ++)
	{
		int compLabel = cIndex;
		int leftCol = stats.at<int>(Point(CC_STAT_LEFT, cIndex));
		int topRow = stats.at<int>(Point(CC_STAT_TOP, cIndex));
		int compWidth = stats.at<int>(Point(CC_STAT_WIDTH, cIndex));
		int compHeight = stats.at<int>(Point(CC_STAT_HEIGHT, cIndex));
		int compArea = stats.at<int>(Point(CC_STAT_AREA, cIndex));
		int rightCol = leftCol + compWidth - 1;
		int bottomRow = topRow + compHeight - 1;
		totalArea = compArea + totalArea;
		CompInfo *newComp = new CompInfo;
		newComp->topRow = topRow;
		newComp->leftCol = leftCol;
		newComp->bottomRow = bottomRow;
		newComp->rightCol = rightCol;
		blobList->push_back(newComp);
	}

	cameraData->blobHistory.push_back(blobList);
	int lastFrameIndex = cameraData->blobHistory.size() - 1;
	blobList = cameraData->blobHistory[lastFrameIndex];


	area = imgRow * imgCol;
//Checking if the components total area is 50% of image area change flag value
	if(totalArea > ((5 * area) / 10))
	{
		flag = 1;
	}
//Getting learning rate
	if(flag == 1)
	{
		cameraData->lRate =  0.005;
	}
//if no blob learning rate to -1.0
	else if ( blobList->size() == 0 )
	{
		cameraData->lRate = -1.0;
	}
	else
	{
		cameraData->lRate = 0.0000000001;
	}
//Resize final image to 640 * 480
	if ( aspectRatio < 1.0 )
	{
		scaleFactor = 640.0 /frame1.cols;
		resize( frame1, frame2, Size( 640, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
		resize( frame4, frame4, Size( 640, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
	}
	else
	{
		scaleFactor = 480.0 /frame1.rows;
		resize( frame1, frame2, Size( (int)(scaleFactor * frame1.cols), 480), 0, 0, INTER_CUBIC);
		resize( frame4, frame4, Size( (int)(scaleFactor * frame1.cols), 480), 0, 0, INTER_CUBIC);
	}

//Drawing Polygon after getting region information type of polygon and points of polygon	
	if(cameraData->region_Data->region_no > 0)
	{

		for(int regionNumber = 0; regionNumber < cameraData->region_Data->regionHistory1.size(); regionNumber++) {
			Point points[1][cameraData->region_Data->regionHistory1[regionNumber]->npoint];
			for(int pointNumber = 0; pointNumber < cameraData->region_Data->regionHistory1[regionNumber]->npoint  ; pointNumber++)
			{
				points[0][pointNumber] = Point(cameraData->region_Data->regionHistory1[regionNumber]->x[pointNumber],
						cameraData->region_Data->regionHistory1[regionNumber]->y[pointNumber]);
			}
			const Point* ppt[1] = { points[0] };
			int npt[] = { cameraData->region_Data->regionHistory1[regionNumber]->npoint };
//Draw polygon
			fillPoly( frame4, ppt, npt, 1, Scalar(128, 128, 128), 8 );
		}
	}
	for(int cIndex = 0; cIndex < blobList->size(); cIndex ++)
	{

		Point points[1][4];
		int leftCol = (int)((*blobList)[cIndex]->leftCol / scaleFactor);
		int topRow = (int)((*blobList)[cIndex]->topRow / scaleFactor);
		int rightCol = (int)((*blobList)[cIndex]->rightCol/ scaleFactor);
		int bottomRow = (int)((*blobList)[cIndex]->bottomRow / scaleFactor);

		points[0][0] = Point(leftCol,topRow);
		points[0][1] = Point(rightCol,topRow);
		points[0][2] = Point(rightCol,bottomRow);
		points[0][3] = Point(leftCol , bottomRow);

		const Point* ppt[1] = { points[0] };
		int npt[] = { 4 };
		fillPoly(frame4,ppt,npt, 1, Scalar(255, 255, 255 ), 8 );
	}


	int count_grey_pixel = 0;
//Give gray colour to the pixels in the region and count the number of gray pixel
	for ( int rIndex = 0; rIndex < frame4.rows; rIndex ++ ) {
		for ( int cIndex = 0; cIndex < frame4.cols; cIndex ++ ) {
			if ( frame4.at<UINT8>(rIndex, cIndex) == 128) {
				count_grey_pixel++;
			}
		}
	}

//For first time
	if(prev_data == 1) {
		prev_data = count_grey_pixel;
	}
//If number of gray pixel decreases means event generated in the region.Gray pixel are replaced by contour/blob color.
	if(prev_data > count_grey_pixel) {
		flag1 = 1;
	}
//if event generation flag is 1 then means no object detected so draw  region in red colour 
	if(flag1 == 1)
	{
		if(cameraData->region_Data->region_no > 0)
		{
			for(int regionNumber = 0; regionNumber < cameraData->region_Data->regionHistory1.size(); regionNumber++) {

				for(int pointNumber = 0; pointNumber < cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1; pointNumber++)
				{
					int x1 = cameraData->region_Data->regionHistory1[regionNumber]->x[pointNumber];
					int y1 = cameraData->region_Data->regionHistory1[regionNumber]->y[pointNumber];
					int x2 = cameraData->region_Data->regionHistory1[regionNumber]->x[pointNumber + 1];
					int y2 = cameraData->region_Data->regionHistory1[regionNumber]->y[pointNumber + 1];

					line( frame2, Point(x1, y1), Point(x2, y2), Scalar(0, 0, 255), 1);
				}
				//catch last line
				int x0 = cameraData->region_Data->regionHistory1[regionNumber]->x[0];
				int y0 = cameraData->region_Data->regionHistory1[regionNumber]->y[0];
				int xn = cameraData->region_Data->regionHistory1[regionNumber]->x[cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1];
				int yn = cameraData->region_Data->regionHistory1[regionNumber]->y[cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1];

				line( frame2, Point(x0, y0), Point(xn, yn), Scalar( 0, 0, 255 ),1);
			}
		}

	}
//if event generation flag is 0 then means no object detected draw region green colour
	if(flag1 == 0)
	{
		if(cameraData->region_Data->region_no > 0)
		{
			for(int regionNumber = 0; regionNumber < cameraData->region_Data->regionHistory1.size(); regionNumber++) {

				for(int pointNumber = 0; pointNumber < cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1; pointNumber++)
				{
					int x1 = cameraData->region_Data->regionHistory1[regionNumber]->x[pointNumber];
					int y1 = cameraData->region_Data->regionHistory1[regionNumber]->y[pointNumber];
					int x2 = cameraData->region_Data->regionHistory1[regionNumber]->x[pointNumber + 1];
					int y2 = cameraData->region_Data->regionHistory1[regionNumber]->y[pointNumber + 1];

					line( frame2, Point(x1, y1), Point(x2, y2), Scalar(0, 255, 0), 1);
				}
				//catch last line
				int x0 = cameraData->region_Data->regionHistory1[regionNumber]->x[0];
				int y0 = cameraData->region_Data->regionHistory1[regionNumber]->y[0];
				int xn = cameraData->region_Data->regionHistory1[regionNumber]->x[cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1];
				int yn = cameraData->region_Data->regionHistory1[regionNumber]->y[cameraData->region_Data->regionHistory1[regionNumber]->npoint - 1];

				line( frame2, Point(x0, y0), Point(xn, yn), Scalar( 0, 255, 0 ),1);
			}
		}

	}

	frame3 = frame2;
//Getting output buffer size provided by UI side
	int buffSize1 = *((int*)eventBuffer);
	UINT8* testData1D = eventBuffer + sizeof(int);

	vector<UINT8> jpegData;
//Encode image to jpeg image
	imencode(".jpg", frame3, jpegData);
	int size = jpegData.size();
	*((int*)eventBuffer) = size;
//Checking buffer space
	if ( buffSize1 != size  && buffSize1 < size){
		printf("Error Error ");
		return DSS_INSUFICIENT_MEMORY;
	}
//Event generation flag value setting
	if(flag1 == 1)
	{
		*((char *) (eventBuffer + sizeof(int))) = 1;

	} else {
		*((char *) (eventBuffer + sizeof(int))) = 0;
	}
	flag1 = 0;
//Copy final output image to output buffer
	memcpy(eventBuffer + sizeof(int) + sizeof(char), &(jpegData[0]), size);


	return DSS_SUCCESS;
}
//Execute for any other task if required taking command input from UI side and perform as per requirement
ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput)
{
	return DSS_SUCCESS;
}


