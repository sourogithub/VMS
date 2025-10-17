

#include <iostream>
#include <string>
#include <vector>
#include <stdlib.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/dnn.hpp>
#include <sys/time.h>
#include<time.h>
using namespace cv;
using namespace std;
using namespace cv::dnn;

const size_t inWidth =  800;
const size_t inHeight = 480;
const double inScaleFactor = 1.0;
const float confidenceThreshold = 0.5;
const cv::Scalar meanVal(104.0, 177.0, 123.0);
struct faceParams {
	int noOfFaces;
	vector<float> xCoords;
	vector<float> yCoords;
};
#define CAFFE

const std::string caffeConfigFile = "/home/dss-04/opencv/samples/dnn/face_detector/deploy.prototxt";
const std::string caffeWeightFile = "res10_300x300_ssd_iter_140000.caffemodel";

const std::string tensorflowConfigFile = "/home/dss-04/opencv/samples/dnn/face_detector/opencv_face_detector.pbtxt";
const std::string tensorflowWeightFile = "opencv_face_detector_uint8.pb";

void detectFaceOpenCVDNN(Net net, Mat &frameOpenCVDNN)
{
	int frameHeight = frameOpenCVDNN.rows;
	int frameWidth = frameOpenCVDNN.cols;
	faceParams* fParams = new faceParams();
	//resize(frameOpenCVDNN, frameOpenCVDNN, Size(300, 300));


#ifdef CAFFE
	cv::Mat inputBlob = cv::dnn::blobFromImage(frameOpenCVDNN, inScaleFactor, cv::Size(inWidth,inHeight), meanVal, false, false);
#else
	cv::Mat inputBlob = cv::dnn::blobFromImage(frameOpenCVDNN, inScaleFactor, cv::Size(inWidth, inHeight), meanVal, true, false);
#endif

	net.setInput(inputBlob, "data");
	cv::Mat detection = net.forward("detection_out");

	cv::Mat detectionMat(detection.size[2], detection.size[3], CV_32F, detection.ptr<float>());

	char buf[100];
	//char buf1[100];
	//	gcvt(confidence, 6, buf);
	char str[200];
	int noOfFaces = 0;
	for(int i = 0; i < detectionMat.rows; i++)
	{
		float confidence = detectionMat.at<float>(i, 2);
		//cout << confidence << endl;

		if(confidence > confidenceThreshold)
		{



			sprintf(buf,"FACE: %.2f",confidence);
			int x1 = static_cast<int>(detectionMat.at<float>(i, 3) * frameWidth);
			int y1 = static_cast<int>(detectionMat.at<float>(i, 4) * frameHeight);
			int x2 = static_cast<int>(detectionMat.at<float>(i, 5) * frameWidth);
			int y2 = static_cast<int>(detectionMat.at<float>(i, 6) * frameHeight);

			cv::rectangle(frameOpenCVDNN, cv::Point(x1, y1), cv::Point(x2, y2), cv::Scalar(0, 255, 0),2, 4);
			noOfFaces++;
			sprintf(str,"NO OF FACES: %d",noOfFaces);
			putText(frameOpenCVDNN, buf , Point2f(x1,y1), FONT_HERSHEY_SIMPLEX, 0.5,  Scalar(255,255,255), 0.5);



		}

	}

	putText(frameOpenCVDNN,str, Point(4,100), FONT_HERSHEY_DUPLEX, 1, Scalar(0,255,0), 1);
	fParams->noOfFaces = noOfFaces;
}


int main( int argc, const char** argv )
{

#ifdef CAFFE
	Net net = cv::dnn::readNetFromCaffe(caffeConfigFile, caffeWeightFile);
#else
	Net net = cv::dnn::readNetFromTensorflow(tensorflowWeightFile, tensorflowConfigFile);
#endif


	VideoCapture capture;
	Mat frame, image;
	capture.open("/home/dss-04/eclipse-workspace1/PeopleDetection/src/submit.mp4");//With local machine recorded video
	//capture.open("rtsp:admin@123456:192.168.1.253/");//With camera stream
	/*VideoCapture source;
  if (argc == 1)
      source.open(0);
  else
      source.open(argv[1]);*/



	if( capture.isOpened() )
	{
		// Capture frames from video and detect faces
		cout << "Face Detection Started...." << endl;
		while(1)
		{
			capture >> frame;
			//	cout << "Channels: " + to_string(frame.channels()) << endl;
			/*struct timeval start,end;
			double tt_opencvDNN = 0;
			double fpsOpencvDNN = 0;*/
			if( frame.empty() )
				break;
			Mat frame1 = frame.clone();
			//gettimeofday(&start,NULL);
			float scaleFactor = 1;
			float aspectRatio = frame.cols/frame.rows;
			if (aspectRatio < 1.0 && frame1.cols > 640.0) {
				scaleFactor = 640.0 / frame1.cols;
				//printf("Row %d\n", (int)(scaleFactor * frame1.rows));
				resize(frame1, frame,
						Size(640, (int) (scaleFactor * frame1.rows)), 0, 0,
						INTER_CUBIC);

			} else if (aspectRatio > 1.0 && frame1.rows > 480.0) {
				scaleFactor = 480.0 / frame1.rows;
				//	printf("Col %d\n", (int)(scaleFactor * frame1.cols));
				resize(frame1, frame,
						Size(scaleFactor * frame1.cols, 480.0), 0, 0,
						INTER_CUBIC);
			} else {
				frame = frame1;
			}
			frame = frame1;
			detectFaceOpenCVDNN ( net,frame);
			//gettimeofday(&end,NULL);
			//	printf("time %ld\n",((end.tv_sec*1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec)));
			namedWindow("Face Detection",WINDOW_NORMAL);



			resizeWindow("Face Detection", 1280,720);
			imshow( "Face Detection",frame);
			char c = (char)waitKey(10);

			// Press q to exit from window
			if( c == 27 || c == 'q' || c == 'Q' )
				break;
		}
	}
	else
		cout<<"Could not Open Camera";
	return 0;


}

