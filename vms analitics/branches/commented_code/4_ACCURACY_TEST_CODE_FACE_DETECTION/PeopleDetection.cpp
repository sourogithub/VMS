#include <stdio.h>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <stdlib.h>
#include <fstream>

#include "opencv2/imgcodecs.hpp"
#include "opencv2/videoio.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/video.hpp>
#include "opencv2/opencv.hpp"
#include "opencv2/objdetect.hpp"
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/dnn.hpp>
#include <sys/time.h>
#include<time.h>
using namespace cv;
using namespace std;
using namespace cv::dnn;
//**This code is basically for testing purpose after face detection by DNN reading images from fddb specific file & Write the details of co-ordinate to a file.Later compare the to the fddb benchmark stream file to know the percentage of accuracy
const size_t inWidth = 640;
const size_t inHeight = 480;
const double inScaleFactor = 1.0;
const float confidenceThreshold = 0.7;
const cv::Scalar meanVal(104.0, 177.0, 123.0);
static  float x33;
static  float y33;
int cntr1 = 0;
#define CAFFE
//We load the required model. If we want to use floating point model of Caffe, we use the caffemodel and prototxt files. Otherwise, we use the quantized tensorflow model. Also note the difference in the way we read the networks for Caffe and Tensorflow
const std::string caffeConfigFile = "/home/dss-04/opencv/samples/dnn/face_detector/deploy.prototxt";
const std::string caffeWeightFile = "res10_300x300_ssd_iter_140000.caffemodel";
const std::string tensorflowConfigFile = "/home/dss-04/opencv/samples/dnn/face_detector/opencv_face_detector.pbtxt";
const std::string tensorflowWeightFile = "opencv_face_detector_uint8.pb";

int main( int argc, const char **argv )
{
//Initialize network
#ifdef CAFFE
	Net net = cv::dnn::readNetFromCaffe(caffeConfigFile, caffeWeightFile);
#else
	Net net = cv::dnn::readNetFromTensorflow(tensorflowWeightFile, tensorflowConfigFile);
#endif
	VideoCapture capture;
	Mat frame, image, frameOpenCVDNN;
	

	std::ifstream myfile;
	std::ofstream myfile1;
	string line;
	string fname1 = "";
	string fname2 = "";
//Arguments are two files.1. FDDB provided folder containing images 2. After detection target file where face details will write
	if( argc == 3 ) {
		fname1 = argv[1];
		fname2 = argv[2];
	}

	myfile.open(fname1);
	myfile1.open(fname2);

//Opening file and read images
	if (myfile.is_open())
	{
		if (myfile1.is_open())
		{
			while ( getline (myfile,line) ){
				String t_line = line;
				line.append(".jpg");
				Mat frame1 = imread(line, CV_LOAD_IMAGE_COLOR);
//Resizing frames 320*240


				float scaleFactor = 1;
				float aspectRatio = frame1.rows / (float)frame1.cols;
				if ( aspectRatio < 1.0 && frame1.cols > 320.0 )	{
					scaleFactor = 320.0 /frame1.cols;
					resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);

				}
				else if ( aspectRatio > 1.0 && frame1.rows > 240.0)	{
					scaleFactor = 240.0 / frame1.rows;
					resize( frame1, frame, Size(scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
				}
				else {
					frame = frame1;
				}
// Check for invalid input
				if(! frame.data )                              
				{
					cout <<  "Could not open or find the image" << std::endl ;
					return -1;

				}
//Number of channel checking for images
				cout << "Channels: " + to_string(frame.channels()) << endl;
				
				double tt_opencvDNN = 0;
				double fpsOpencvDNN = 0;
//Checking frames are empty or not
				if( frame.empty() )
					continue;
				frameOpenCVDNN = frame;
//Getting the height and width of image				
				int frameHeight = frameOpenCVDNN.rows;
				int frameWidth = frameOpenCVDNN.cols;

// load the input image and construct an input blob for the image
//by resizing to a fixed 640x480 pixels and then normalizing it
#ifdef CAFFE
				cv::Mat inputBlob = cv::dnn::blobFromImage(frameOpenCVDNN, inScaleFactor, cv::Size(frame.rows, frame.cols), meanVal, false, false);
#else
				cv::Mat inputBlob = cv::dnn::blobFromImage(frameOpenCVDNN, inScaleFactor, cv::Size(inWidth, inHeight), meanVal, true, false);
#endif
//pass the blob through the network and obtain the detections and
//predictions
				net.setInput(inputBlob, "data");
//Image is converted to a blob and passed through the network using the forward() function. The output detections is a 4-D matrix, 
				cv::Mat detection = net.forward("detection_out");
//This is for detect face 
				cv::Mat detectionMat(detection.size[2], detection.size[3], CV_32F, detection.ptr<float>());
//Write name of image with directory to the output file
				myfile1 << t_line;
				myfile1 <<"\n";
				int k = 0;
//This section for getting 4 Points from frame(after detection).Create ellipse from using 4points and draw ellipse over detected faces .Store the points of ellipse and number of faces to the output file 
				for(int i = 0; i < detectionMat.rows; i++)
				{
//Get confidence of the detection
					float confidence = detectionMat.at<float>(i, 2);
//Compare with predefined confidence by program side.If calculated confidence is higher than predefined confidence(70%) then draw ellipse and keep ellipse data				
					if(confidence > confidenceThreshold)
					{

						char buf[100];
// This function is used to convert a floating point number to string.
						gcvt(confidence, 6, buf);

						int x1 = static_cast<int>(detectionMat.at<float>(i, 3) * frameWidth);
						int y1 = static_cast<int>(detectionMat.at<float>(i, 4) * frameHeight);
						int x2 = static_cast<int>(detectionMat.at<float>(i, 5) * frameWidth);
						int y2 = static_cast<int>(detectionMat.at<float>(i, 6) * frameHeight);
//Getting four points
						Point pt1(x1,y1);
						Point pt2(x2,y2);
						Point pt3(x1,y2);
						Point pt4(x2,y1);
//This portion for getting ellipse points 
						int a = (x2 - x1)/2;
						int b = (y1 - y2)/2;
						Point pt5(x1,y2+b);
						Point pt6(x1+a,y2);
						Point pt7(x1+a,y2+b);
						float x5 = x2 + a;
						x33 = x5;
						float y5 = y2 + b;
						y33 = y5;
//Draw ellipse

						ellipse( frameOpenCVDNN, cv::Point(x1+a,y2+b),cv::Size(a*1.2,1.9*a), 180, 0, 360, cv::Scalar( 255, 0, 0 ), 2, 8 );
					
						k++;
//Write confidence on the frame
						putText(frameOpenCVDNN, buf , Point2f(x1,y1), FONT_HERSHEY_SIMPLEX, 0.5,  Scalar(255,255,255), 1);


					}
				}
//Write Number of faces to the file ***Writing information as per FDDB benchmark file is structured.//Basically it is for comparison
//<major_axis_radius minor_axis_radius angle center_x center_y detection_score//We have to compare our generated ellipse centre to FDDB benchmark ellipse centre//
				myfile1 << k;
				myfile1 <<"\n";
				myfile1<<0;
				myfile1<<" ";
				myfile1<<0;
				myfile1<<" ";
				myfile1<<0;
				myfile1<<" ";
				myfile1<< x33;
				myfile1<< " ";
				myfile1<< y33;
				myfile1<<" ";
				myfile1<<" ";
				myfile1<<"1";
				myfile1<<"\n";
				std::string savingName =  "results/" + std::to_string(++cntr1) + ".jpg";
				imwrite(savingName, frame);



			}
		} else
			cout << "Unable to open file";
	}
	else
		cout << "Unable to open file";
	return 0;
}
