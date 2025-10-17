
/*
 * ProbeMaster.cpp
 *
 *  Created on: 06-Oct-2018
 *      Author: SHILPA*/
#include <dlib/dnn.h>
#include <dlib/gui_widgets.h>
#include <dlib/clustering.h>
#include <dlib/string.h>
#include <dlib/image_io.h>
#include <dlib/image_processing/frontal_face_detector.h>
#include <algorithm>
#include <vector>
#include <string>
#include <dlib/pixel.h>
#include <dlib/array2d.h>
#include <dlib/image_transforms.h>
#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <bits/stdc++.h>
#include <opencv2/opencv.hpp>
#include <dlib/opencv.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/imgcodecs.hpp>
#include <sstream>
<<<<<<< .mine
#include <dlib/opencv/cv_image.h>
#include <fstream> 
#include <experimental/filesystem>
||||||| .r6
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
=======
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
>>>>>>> .r18
#include "jpegIO.h"
#include "jpeglib.h"
#include "DataTypes.h"
#include "ErrorTypes.h"64
#include "config_SM.h"
#include "ProbeMaster.h"
#include "api_cpp.h"

#include "ProbeMasterJni.cpp"
//For resizing frames
#define FACE_DOWNSAMPLE_RATIO 3
//For skipping frames if required
#define SKIP_FRAMES 2
#define DEBUG 	0

using namespace dlib;
using namespace std;
using namespace cv;
// ----------------------------------------------------------------------------------------

// The next bit of code defines a ResNet network.
// except we replaced the loss
// layer with loss_metric and made the network somewhat smaller.  Go read the introductory
// dlib DNN examples to learn what all this stuff means.
//

// The dlib_face_recognition_resnet_model_v1 model used by this example was trained using
// essentially the code shown in dnn_metric_learning_on_images_ex.cpp except the
// mini-batches were made larger (35x15 instead of 5x5), the iterations without progress
// was set to 10000, and the training dataset consisted of about 3 million images instead of
// 55.  Also, the input layer was locked to images of size 150.
template<typename T>

template<template<int, template<typename > class, int, typename > class block,
		int N, template<typename > class BN, typename SUBNET>
using residual = add_prev1<block<N,BN,1,tag1<SUBNET>>>;

template<template<int, template<typename > class, int, typename > class block,
		int N, template<typename > class BN, typename SUBNET>
using residual_down = add_prev2<avg_pool<2,2,2,2,skip1<tag2<block<N,BN,2,tag1<SUBNET>>>>>>;

template<int N, template<typename > class BN, int stride, typename SUBNET>
using block = BN<con<N,3,3,1,1,relu<BN<con<N,3,3,stride,stride,SUBNET>>>>>;

template<int N, typename SUBNET> using ares = relu<residual<block,N,affine,SUBNET>>;
template<int N, typename SUBNET> using ares_down = relu<residual_down<block,N,affine,SUBNET>>;

template<typename SUBNET> using alevel0 = ares_down<256,SUBNET>;
template<typename SUBNET> using alevel1 = ares<256,ares<256,ares_down<256,SUBNET>>>;
template<typename SUBNET> using alevel2 = ares<128,ares<128,ares_down<128,SUBNET>>>;
template<typename SUBNET> using alevel3 = ares<64,ares<64,ares<64,ares_down<64,SUBNET>>>>;
template<typename SUBNET> using alevel4 = ares<32,ares<32,ares<32,SUBNET>>>;

using anet_type = loss_metric<fc_no_bias<128,avg_pool_everything<
alevel0<
alevel1<
alevel2<
alevel3<
alevel4<
max_pool<3,3,2,2,relu<affine<con<32,7,7,2,2,
input_rgb_image_sized<150>
>>>>>>>>>>>>;
// ----------------------------------------------------------------------------------------
// All this function does is make 100 copies of img, all slightly jittered by being
// zoomed, rotated, and translated a little bit differently. They are also randomly
// mirrored left to right.
std::vector<matrix<rgb_pixel>> jitter_image(const matrix<rgb_pixel> &img);

// This split function for splitting every word of a line first parameter signifies the tokens,second parameter holds the line and third parameter holds the delimeter means separated by special symbol.
static void split(std::vector<string> &toks, const string &s,
		const string &delims) {
	toks.clear();

	string::const_iterator segment_begin = s.begin();

	string::const_iterator current = s.begin();
	string::const_iterator string_end = s.end();

	while (true) {
		if (current == string_end || delims.find(*current) != string::npos
				|| *current == '\r') {
			if (segment_begin != current)
				toks.push_back(string(segment_begin, current));

			if (current == string_end || *current == '\r')
				break;

			segment_begin = current + 1;
		}

		current++;
	}

}
//convertToString function basically for converting input to string 
string convertToString(char* a, int size) 
{ 
    int i; 
    std::string s = ""; 
    for (i = 0; i < size; i++) { 
        s = s + a[i]; 
    } 
    return s; 
} 
//extern "C" makes a function-name in C++ have 'C' linkage (compiler does not mangle the name) so that client C code can link to (i.e use) 
//your function using a 'C' compatible header file that contains just the declaration of your function. Your function definition is contained 
//in a binary format (that was compiled by your C++ compiler) that the client 'C' linker will then link to using the 'C' name.
extern "C" {

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
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer) {
	CameraData *cameraData =
			(CameraData*) (*((UINT64*) dataSpace + sizeof(char)));

<<<<<<< .mine
	
||||||| .r6
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer)
{
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char))) ;

	int OFFSET = 0;
	int bufferSize = *((int *) (regionBuffer + OFFSET));		/* fetching first 4bytes as  buffer size */
	OFFSET += sizeof(int);
	INT16 noOfRegions = *((INT16 *)(regionBuffer + OFFSET));	/* getting no of regions */
	OFFSET += sizeof(short);

	cameraData->region_Data->region_no = noOfRegions;
	cameraData->region_Data->regionHistory1.clear();

	for(int regionNumber = 1; regionNumber <= noOfRegions; regionNumber++) {
		int noofPoints = *((char *)(regionBuffer + OFFSET));
		OFFSET += sizeof(char);
		Region *newComp = new Region;						/* creating new Region */
		newComp->npoint = noofPoints;

		for(int pointNumber = 0; pointNumber < noofPoints; pointNumber++)
		{
			newComp->x[pointNumber] = *((int *) (regionBuffer + OFFSET));
			OFFSET += sizeof(int);
			newComp->y[pointNumber] = *((int *) (regionBuffer + OFFSET));
			OFFSET += sizeof(int);
		}

		//skip parameters [PARAM MARKER] for now
		OFFSET += sizeof(int) * 4;
		//pushing region into region list
		cameraData->region_Data->regionHistory1.push_back(newComp);
	}

=======
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer)
{
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char))) ;

	int OFFSET = 0;
	int bufferSize = *((int *) (regionBuffer + OFFSET));		/* fetching first 4bytes as  buffer size */
	OFFSET += sizeof(int);
	INT16 noOfRegions = *((INT16 *)(regionBuffer + OFFSET));	/* getting no of regions */
	OFFSET += sizeof(short);

	cameraData->region_Data->region_no = noOfRegions;
	cameraData->region_Data->regionHistory1.clear();

	for(int regionNumber = 1; regionNumber <= noOfRegions; regionNumber++) {
		int noofPoints = *((char *)(regionBuffer + OFFSET));
		OFFSET += sizeof(char);
		Region *newComp = new Region;						/* creating new Region */
		newComp->npoint = noofPoints;

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

>>>>>>> .r18
	return DSS_SUCCESS;
}

<<<<<<< .mine
//API SearchEvent does the main recognition work from UI side this API getting images(frames) by parameter imageBuffer and process those.After processing  send them to the UI by the parameter eventBuffer.  
ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer,
		UINT8 *eventBuffer) try{
 	
//For cameraData is for getting cameara information
	CameraData *cameraData =
			(CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
//buffSize variable storing the size of input buffer named imageBuffer.First 4byte of imageBuffer holding buffer size.
	int buffSize = *((int*) imageBuffer);
//In imageBuffer after buffer size next 8byte holding timestamp.
||||||| .r6

ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer, UINT8 *eventBuffer)
{
	struct timeval start,end;
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char)));
	int height = 0,width = 0;
	UINT8 *outbuffer = NULL;
	int buffSize = *((int*)imageBuffer);
=======
static int prev_data = 1;
static int flag1 = 0;
ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer, UINT8 *eventBuffer)
{
	struct timeval start,end;
	CameraData *cameraData = (CameraData *)(*((UINT64 *)dataSpace + sizeof(char)));
	int height = 0,width = 0;
	UINT8 *outbuffer = NULL;
	int buffSize = *((int*)imageBuffer);
>>>>>>> .r18
	UINT8 *timeStamp = (imageBuffer + sizeof(int));
//After getting timestamp, pointer points to the start point of the image data
	UINT8 *start_point = (imageBuffer + sizeof(int) + 2 * sizeof(int));
<<<<<<< .mine
//end_point denotes the end of the image
	UINT8 *end_point = (imageBuffer + sizeof(int)+  2 * sizeof(int) + buffSize);
||||||| .r6
	UINT8 *end_point   = (imageBuffer + sizeof(int) + buffSize);
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
	Mat frame;
	Mat fgbgDiff;
	InputArray iArr(inputImage);
	Mat frame1;
	Mat frame3,frame2;
	frame1 = imdecode(inputImage, 1);
	FLOAT32 scaleFactor = 1.0;
	FLOAT32 aspectRatio = frame1.rows / (FLOAT32)frame1.cols;
	if ( aspectRatio < 1.0 && frame1.cols > 320.0 )
	{
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
=======
	UINT8 *end_point   = (imageBuffer + sizeof(int) + buffSize);
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
	Mat frame;
	Mat fgbgDiff;
	InputArray iArr(inputImage);
	Mat frame1;
	Mat frame3,frame2;
	Mat frame4(640, 480, CV_8UC3, Scalar(0, 0, 0));
	int frame_count = 0;


	frame1 = imdecode(inputImage, 1);
	frame_count ++;
	FLOAT32 scaleFactor = 1.0;
	FLOAT32 aspectRatio = frame1.rows / (FLOAT32)frame1.cols;
	if ( aspectRatio < 1.0 && frame1.cols > 320.0 )
	{
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
>>>>>>> .r18
		resize( frame4, frame4, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
//ifstream data type represents the input file stream and is used to read information from files.
	ifstream myfile, myfile1, myfile2;
	string line;
	std::vector < string > nvmToks;
//TrainingData.dat file holds the face data of each person after training faces.
	myfile2.open("TrainingData.dat");
// In this 128D vector space, images from the same person will be close to each other
// but vectors from different people will be far apart.  So we can use these vectors to
// identify if a pair of images are from the same person or from different people.  
	std::vector<matrix<float, 0, 1>> face_descriptors;
//This person_id vector for storing person's Id.
	std::vector<string> person_id;
        std::vector<unsigned int> min_Index;
//This person_id vector for storing person's name.
	std::vector < string > person_name;
//This person_id vector for storing person's Date of birth.
        std::vector < string > person_DOB;
//This person_id vector for storing person's Gender.
        std::vector < string > person_Gender;
//Reading every line of TrainingData.dat file
	while (getline(myfile2, line)) {
//Splitting every value of .dat file sepated by " "
		split(nvmToks, line, " ");
//float_array for storing each float value of the face data present in the TrainingData.dat file
		dlib::array<float> float_array = dlib::array<float>();
//Read every value from the .dat file and store it into float_array
		for (int i = 0; i < 128; i++) {
			float t = stof(nvmToks[i]);
			float_array.push_back(t);
		}
//convert float_array to mat format & Store it to float_mat variable 
		auto float_mat = mat(float_array);
//Read 128 float data for each face along with person id, name, gender, date of birth & push them to the particular vector space(from TrainingData.dat file)
		auto face_desc = dlib::matrix<float, 0, 1>(float_mat);
		person_id.push_back(nvmToks[128]);
		person_name.push_back(nvmToks[129]);
                person_Gender.push_back(nvmToks[130]);
                person_DOB.push_back(nvmToks[131]);
		face_descriptors.push_back(face_desc);
	}
	myfile2.close();

//Reading image from buffer and store it into Mat format
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
        InputArray iArr(inputImage);
	cv::Mat temp, frame, temp1, image_gray,, img2, img3, crop, tempo;
//Declare flag = 0 ,this flag for event generation.************************************    
	int flag = 0;
        int flag1 = 0;
// We need a face detector.  We will use this to get bounding boxes for
// each face in an image
// find faces in the image we will need a face detector:
	frontal_face_detector detector     =  get_frontal_face_detector();
// And we also need a shape_predictor.  This is the tool that will predict face
// landmark positions given an image and face bounding box.  Here we are just
// loading the model from the shape_predictor_5_face_landmarks.dat file.
	shape_predictor sp;
	deserialize("lib/shape_predictor_5_face_landmarks.dat") >> sp;
	anet_type net;
// And finally we load the DNN responsible for face recognition.
	deserialize("lib/dlib_face_recognition_resnet_model_v1.dat") >> net;
//imdecode for decode input frames
	temp = imdecode(inputImage, 1);
//Store a copy of frame to a Mat type variable named temp1	
        temp1 = temp;
//Checking frame is empty or not.If empty then return DSS_ERROR message to the UI
	if (temp.empty()) {
		printf("stop");
		return DSS_ERROR;
		resize( frame4, frame4, Size( scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
	}
//resize original image frames as per requirement
   cv::resize(temp, frame, cv::Size(), 1.0/FACE_DOWNSAMPLE_RATIO, 1.0/FACE_DOWNSAMPLE_RATIO);
//Store a copy of resized frame into a Mat type variable
   img2 = frame;
//Checking jpeg is empty or not
       if (img2.empty()) { cout<<"empty jpeg"; }
         
	
	
//checking image is RGB or not.If RGB image then convert it into grayscale image.Time requirement for grayscale image is lesser than RGB image 
	if (frame.channels() > 2) {
		cv::cvtColor(frame, image_gray, CV_BGR2GRAY);
	}
<<<<<<< .mine
//convert grayscale image to dlib cv_image format     
	dlib::cv_image<unsigned char> img(image_gray);
//gettimeofday for time calculation of detection and recognition.
	gettimeofday(&start, NULL);
// Run the face detector on the image, and for each face extract a
// copy that has been normalized to 150x150 pixels in size and appropriately rotated
// and centered.
	std::vector<matrix<rgb_pixel>> faces;
        std::vector<dlib::rectangle> dets = detector(img);
	int cntr = 0;
	for (auto face : dets) {
		printf("Face Detected\n");
		auto shape = sp(img, face);
		matrix<rgb_pixel> face_chip;
		extract_image_chip(img, get_face_chip_details(shape, 150, 0.25),
				face_chip);
		faces.push_back(move(face_chip));
	                        }
//Checking if No faces found in image      
     if (faces.size() == 0) {
		cout << "No faces found in image!" << endl;
		}    
//if face detected then the faces count should be greater than 0.Then recognize faces.
	if(faces.size() > 0)
        {
//For storing 128 facial data of detected faces 
	std::vector<matrix<float, 0, 1>> face_descriptor_input;
//Store face data to face_descriptor_input
//This call asks the DNN to convert each face image in faces into a 128D vector.
// In this 128D vector space, images from the same person will be close to each other
// but vectors from different people will be far apart.  So we can use these vectors to
// identify if a pair of images are from the same person or from different people.  
	face_descriptor_input = net(faces);
	int minDist, minIndex;
//Converting dlib's rectangle to opencv's rect
	cv::Rect R;
	for (int i = 0; i < dets.size(); i++) {
		R.x = dets[0].left();
		R.y = dets[0].top();
		R.width = dets[0].width();
		R.height = dets[0].height();
		cv::rectangle(img2, R, cvScalar(0, 255, 0), 1, 1);
		img3 = img2;
||||||| .r6
	int imgRow = frame.rows;
	int imgCol = frame.cols;
	//FLOAT64 timestamp = 0; //timestamp will come from session Manager
	printf("lRate = %lf\n", cameraData->lRate);
	cameraData->pMOG2->apply(frame, fgbgDiff, cameraData->lRate);
	Mat fgImage;
	threshold(fgbgDiff, fgImage, 200, PIX_FOREGROUND, THRESH_BINARY);
	Mat element = getStructuringElement( MORPH_RECT, Size(4, 8));
	Mat tmp1;
	Mat tmp2;
	morphologyEx( fgImage, tmp1, MORPH_OPEN, element );
	Mat	labels(1, buffSize, CV_8UC1, start_point);
	Mat stats;
	Mat centroids;;
	int nComp = connectedComponentsWithStats(tmp1, labels, stats, centroids,8, CV_32SC1);
	printf("Component object = %d\n",nComp);

	for(int cIndex = 1; cIndex <= nComp; cIndex ++)
	{
		int compLabel = cIndex;
		int leftCol = stats.at<int>(Point(CC_STAT_LEFT, cIndex));
		int topRow = stats.at<int>(Point(CC_STAT_TOP, cIndex));
		int compWidth = stats.at<int>(Point(CC_STAT_WIDTH, cIndex));
		int compHeight = stats.at<int>(Point(CC_STAT_HEIGHT, cIndex));
		int compArea = stats.at<int>(Point(CC_STAT_AREA, cIndex));

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
=======
	int imgRow = frame.rows;
	int imgCol = frame.cols;
	Mat frame5;

	//FLOAT64 timestamp = 0; //timestamp will come from session Manager
	printf("lRate = %lf\n", cameraData->lRate);
	cameraData->pMOG2->apply(frame, fgbgDiff, cameraData->lRate);
	Mat fgImage;

	threshold(fgbgDiff, fgImage, 200, PIX_FOREGROUND, THRESH_BINARY);
	Mat element = getStructuringElement( MORPH_RECT, Size(4, 8));
	Mat tmp1;
	Mat tmp2;
	morphologyEx( fgImage, tmp1, MORPH_OPEN, element );
	Mat	labels(1, buffSize, CV_8UC1, start_point);
	Mat stats;
	Mat centroids;
	int nComp = connectedComponentsWithStats(tmp1, labels, stats, centroids,8, CV_32SC1);
	printf("Component object = %d\n",nComp);
	//******************************************************************************************************************

	//*******************************************************************************************************************
	for(int cIndex = 1; cIndex <= nComp; cIndex ++)
	{
		int compLabel = cIndex;
		int leftCol = stats.at<int>(Point(CC_STAT_LEFT, cIndex));
		int topRow = stats.at<int>(Point(CC_STAT_TOP, cIndex));
		int compWidth = stats.at<int>(Point(CC_STAT_WIDTH, cIndex));
		int compHeight = stats.at<int>(Point(CC_STAT_HEIGHT, cIndex));
		int compArea = stats.at<int>(Point(CC_STAT_AREA, cIndex));

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
>>>>>>> .r18
	}
//For cropping faces from whole image store it into crop(Mat variable)
     if(R.x >= 0 && R.y >= 0 && R.width + R.x < img3.cols && R.height + R.y < img3.rows)
     {
     
       crop = img3(R);
     }
// In particular, one simple thing we can do is face clustering.  This next bit of code
// creates a graph of connected faces and then uses the Chinese whispers graph clustering
// algorithm to identify how many people there are and which faces belong to whom.
	for (size_t i = 0; i < face_descriptor_input.size(); ++i) {
		minDist = 1.0;
		minIndex = -1;
               
		for (size_t j = 0; j < face_descriptors.size(); ++j) {
			float dist = length(face_descriptor_input[i] - face_descriptors[j]);
// Faces are connected in the graph if they are close enough.  Here we check if
// the distance between two face descriptors is less than 0.4 for our dataset, which is the
// decision threshold the network was trained to use.  Although you can
// certainly use any other threshold you find useful.
			if (dist < 0.4) {
				if (minDist > dist) {
					minDist = dist;
					minIndex = j;
                                          
				}
			}
		}
	}

//Declare 4 empty string variable for storing person id,name,Date of birth , gender	

<<<<<<< .mine
	std::string s_id = "";
        std::string s_name = "";
        std::string s_dob = "";
        std::string s_gender = "";
//Accessing recognized person's details   
        if (minIndex != -1) {
//Getting recognized person's id from the vector  and store it to a string variable
	 s_id     = person_id[minIndex].c_str();
         s_name   = person_name[minIndex].c_str();
         s_dob    = person_DOB[minIndex].c_str();
         s_gender = person_Gender[minIndex].c_str();
//change the flag values to 1
                flag = 1;
                flag1 = 1;
        }
   
//Printing the recognized person's data	
	cout<<"ID SIZE : "<<t_id<<" Empliyee ID:"<<s_id<<endl;
   	cout<<"NAME SIZE : "<<t_name<<" Employee Name : "<<s_name<<endl;
	cout<<"DOB SIZE  :"<<t_dob<<"DOB:"<<s_dob<<endl;
        cout<<"gender  size : "<<t_gender<<"gender"<<s_gender<<endl;     
//Getting output buffer size from event buffer first 4byte 
	int buffSize1 = *((int*) eventBuffer);
	std::vector<UINT8> jpegData;
//Encode cropped face & store it as jpeg image to the vector
	imencode(".jpg", crop, jpegData);
//Calculate the size of the encoded image size
||||||| .r6
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
		FindColorFeature(frame, labels, Rect(leftCol, topRow, compWidth, compHeight), compLabel, newComp->colorDesc);
		blobList->push_back(newComp);
	}

	cameraData->blobHistory.push_back(blobList);
	int lastFrameIndex = cameraData->blobHistory.size() - 1;
	blobList = cameraData->blobHistory[lastFrameIndex];
	for(int cIndex = 0; cIndex < blobList->size(); cIndex ++)
	{
		int leftCol = (int)((*blobList)[cIndex]->leftCol / scaleFactor);
		int topRow = (int)((*blobList)[cIndex]->topRow / scaleFactor);
		int rightCol = (int)((*blobList)[cIndex]->rightCol/ scaleFactor);
		int bottomRow = (int)((*blobList)[cIndex]->bottomRow / scaleFactor);
		rectangle(frame1, Point(leftCol, topRow), Point(rightCol, bottomRow), Scalar(0, 0, 255), 3);
	}

	area = imgRow * imgCol;

	if(totalArea > ((5 * area) / 10))
	{
		flag = 1;
	}

	printf(" compArea= %d ",totalArea);
	fflush(stdout);
	printf(" Area= %d\n",area);
	fflush(stdout);
	if(flag == 1)
	{
		cameraData->lRate =  0.005;
	}
	else if ( blobList->size() == 0 )
	{
		cameraData->lRate = -1.0;
	}
	else
	{
		cameraData->lRate = 0.0000000001;
	}

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
	int buffSize1 = *((int*)eventBuffer);
	UINT8* testData1D = eventBuffer + sizeof(int);
	//vector<UINT8> jpegData(testData1D + sizeof(int), testData1D + sizeof(int) + buffSize);
	vector<UINT8> jpegData;
	//imwrite("xx.jpg", frame2);

	imencode(".jpg", frame3, jpegData);
=======

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

		FindColorFeature(frame, labels, Rect(leftCol, topRow, compWidth, compHeight), compLabel, newComp->colorDesc);
		blobList->push_back(newComp);
	}

	cameraData->blobHistory.push_back(blobList);
	int lastFrameIndex = cameraData->blobHistory.size() - 1;
	blobList = cameraData->blobHistory[lastFrameIndex];
	/*for(int cIndex = 0; cIndex < blobList->size(); cIndex ++)
	{
		int leftCol = (int)((*blobList)[cIndex]->leftCol / scaleFactor);
		int topRow = (int)((*blobList)[cIndex]->topRow / scaleFactor);
		int rightCol = (int)((*blobList)[cIndex]->rightCol/ scaleFactor);
		int bottomRow = (int)((*blobList)[cIndex]->bottomRow / scaleFactor);
		rectangle(frame1, Point(leftCol, topRow), Point(rightCol, bottomRow), Scalar(0, 0, 255), 3);
	}*/
//**********************************************************************************************************************************





//**********************************************************************************************************************************
	area = imgRow * imgCol;

	if(totalArea > ((5 * area) / 10))
	{
		flag = 1;
	}

	printf(" compArea= %d ",totalArea);
	fflush(stdout);
	printf(" Area= %d\n",area);
	fflush(stdout);
	if(flag == 1)
	{
		cameraData->lRate =  0.005;
	}
	else if ( blobList->size() == 0 )
	{
		cameraData->lRate = -1.0;
	}
	else
	{
		cameraData->lRate = 0.0000000001;
	}

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

	//*************************************************************************************************************
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
	for ( int rIndex = 0; rIndex < frame4.rows; rIndex ++ ) {
		for ( int cIndex = 0; cIndex < frame4.cols; cIndex ++ ) {
			if ( frame4.at<UINT8>(rIndex, cIndex) == 128) {
				count_grey_pixel++;
			}
		}
	}

	cout<<"Grey Pixel count - "<<count_grey_pixel<<endl;
	if(prev_data == 1) {
		prev_data = count_grey_pixel;
	}

	if(prev_data > count_grey_pixel) {
		flag1 = 1;
	}

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
	int buffSize1 = *((int*)eventBuffer);
	UINT8* testData1D = eventBuffer + sizeof(int);

	vector<UINT8> jpegData;

	imencode(".jpg", frame3, jpegData);
>>>>>>> .r18
	int size = jpegData.size();
<<<<<<< .mine
//Put that size to the output buffer named event buffer
	*((int*) eventBuffer) = size;

||||||| .r6

=======
>>>>>>> .r18
<<<<<<< .mine
//If buffer size provided by UI side API is not equals to the encoded image size or lesser than encoded image size then return insuficient memory message as  DSS_INSUFICIENT_MEMORY.
	if (buffSize1 != size && buffSize1 < size) {
||||||| .r6
	*((int*)eventBuffer) = size;

	if ( buffSize1 != size  && buffSize1 < size)
	{
		printf("Error Error ");
=======
	*((int*)eventBuffer) = size;

	if ( buffSize1 != size  && buffSize1 < size){
		printf("Error Error ");
>>>>>>> .r18
		return DSS_INSUFICIENT_MEMORY;
	}
<<<<<<< .mine
||||||| .r6
	if(flag == 1)
	{
		*((char *) (eventBuffer + sizeof(int))) = 1;
	} else {
		*((char *) (eventBuffer + sizeof(int))) = 0;
	}
=======

	if(flag1 == 1)
	{
		*((char *) (eventBuffer + sizeof(int))) = 1;

	} else {
		*((char *) (eventBuffer + sizeof(int))) = 0;
	}
>>>>>>> .r18
	flag1 = 0;

//If flag value is 1 then  store 1 to the output buffer(eventbuffer) for signifing event generation otherwise write 0 to the output buffer(flag size 1byte)
        if (flag == 1) { *((char*) (eventBuffer + sizeof(int))) = 1; }
	else { *((char*) (eventBuffer + sizeof(int))) = 0; }
//After writing flag value then write image data to the output buffer(eventbuffer)     
	memcpy(eventBuffer + sizeof(int) + sizeof(char), &(jpegData[0]), size);
<<<<<<< .mine
//If flag1 is 1 means face recognized.Then write 1(in 1byte) into the output buffer.If not recognized write 0 to the output buffer. 
        if (flag1 == 1) { *((char*) (eventBuffer + sizeof(int) + sizeof(char) + size)) = 1; }
	else { *((char*) (eventBuffer + sizeof(int) + sizeof(char) + size)) = 0; }
//Put Recognized person's id size(1byte) in the output buffer after 1byte flag 
	*((short*) (eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char))) = (short)s_id.size() ;
//Copy Recognized Person's id to the eventbuffer
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) +  sizeof(short)), s_id.c_str());        
//Put Recognized Person's name size to the eventbuffer.Size 1byte
	/** name marker **/
	*((short*) (eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) +  t_id )) =  (short)s_name.size() ; 
//Copy Recognized  person's name to the output buffer
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_id + sizeof(short)),  s_name.c_str());          
//Putting Size of Date of birth of the recognized person to the event buffer(long type)	
	/** dob marker **/
	*((long*)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_id + sizeof(short) + t_name )) = (long)s_dob.size() ;
//  Copy Date of birth of Recognized person to the event buffer 
strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_id + sizeof(short) + t_name + sizeof(long)) , s_dob.c_str());
//Putting size of gender of the recognized person to the event buffer 1byte
       *((short*) (eventBuffer + sizeof(int) + sizeof(char) + size +  sizeof(short) + t_id + sizeof(short) + t_name + sizeof(long) + t_dob)) = (short)s_gender.size();
//Copy gender of the recognized person to the event buffer
strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_id + sizeof(short) + t_name + sizeof(long) + t_dob + sizeof(short)) ,s_gender.c_str());
	return DSS_SUCCESS;
}
}
//Catch block
catch (std::exception& e)
{ 
    cout << e.what() << endl;
}
//Execute for any other task if required taking command input from UI side and perform as per requirement
ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput) {
	
			
return DSS_SUCCESS;
||||||| .r6
	tmp1.release();
	tmp2.release();
	frame.release();
	frame1.release();
	frame2.release();
	frame3.release();
	fgImage.release();
	labels.release();
	stats.release();
	centroids.release();
	labelsFg.release();
	statsFg.release();
	centroidsFg.release();
	tmp2.release();
	labelsBg.release();
	statsBg.release();
	centroidsBg.release();
=======
>>>>>>> .r18


<<<<<<< .mine
}
std::vector<matrix<rgb_pixel>> jitter_image(const matrix<rgb_pixel> &img) {
// All this function does is make 100 copies of img, all slightly jittered by being
// zoomed, rotated, and translated a little bit differently. They are also randomly
// mirrored left to right.
	thread_local dlib::rand rnd;

	std::vector<matrix<rgb_pixel>> crops;
	for (int i = 0; i < 100; ++i)
		crops.push_back(move(img));

	return crops;
}



||||||| .r6
#if DEBUG
	static int frameCount = 0;
	char fileName[50];
	sprintf(fileName, "%d l.jpg", frameCount ++);
	FILE *fp = fopen(fileName, "wb");
	if(fp != NULL)
	{
		fwrite(&(jpegData[0]) , size, 1, fp);
		fclose(fp);
	}
#endif
	//******************************************************************************************************************************************
	/*

	 UINT32 lastPtrX = 0,lastptrY = 0;
	int pointSize[MAX_POSSIBLE_POINT];
	int x[MAX_POSSIBLE_POINT];
	int y[MAX_POSSIBLE_POINT];

	for(int i = 0;i < cameraData->region_Data->region_no;i++)
	{
		x[i] = cameraData->region_Data->regionHistory1[i]->x[i];
		y[i] = cameraData->region_Data->regionHistory1[i]->y[i];
		pointSize[i] = cameraData->region_Data->regionHistory1[i]->npoint[i];
		line( frame1, Point( x[i],y[i]), Point( x[i+1],y[i+1]), Scalar( 110, 220, 0 ),  2, 8 );
		lastPtrX = x[i+1];
		lastptrY = y[i+1];

	}
	line( frame1, Point( x[0],y[0]), Point(lastPtrX,lastptrY), Scalar( 110, 220, 0 ),  2, 8 );
	//cameraData->regionHistory.push_back(regionList);

	 */


	//****************************************************************************************************
	return DSS_SUCCESS;
}

ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput)
{
	return DSS_SUCCESS;
}
=======
	return DSS_SUCCESS;
}

ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput)
{
	return DSS_SUCCESS;
}

>>>>>>> .r18
