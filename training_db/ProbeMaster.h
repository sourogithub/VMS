//dlib
#include <dlib/dnn.h>
#include <dlib/gui_widgets.h>
#include <dlib/clustering.h>
#include <dlib/string.h>
#include <dlib/image_io.h>
#include <dlib/image_processing/frontal_face_detector.h>
#include <dlib/pixel.h>
#include <dlib/array2d.h>
#include <dlib/image_transforms.h>
#include <dlib/opencv.h>
//#include <dlib/opencv/cv_image.h>
//c++ in-built
#include <algorithm>
#include <vector>
#include <string>
#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <bits/stdc++.h>
#include <setjmp.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <fstream> 
#include <experimental/filesystem>
#include <sstream>
//opencv
#include <opencv2/video/background_segm.hpp>
#include "opencv2/imgcodecs.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/videoio.hpp"
#include <opencv2/highgui.hpp>
#include <opencv2/video.hpp>
#include "opencv2/opencv.hpp"
//#include "opencv2/bgsegm.hpp"
#include "opencv2/objdetect.hpp"
//sqlite
#include <sqlite3.h>
//postgresql
#include<pqxx/pqxx>
//jpeg library
//#include "jpegIO.h"
#include "jpeglib.h"
//vms library
#include "DataTypes.h"
#include "ErrorTypes.h"
#include "config_SM.h"
//#include "api_cpp.h"
//#include "ProbeMasterJni.cpp"

//For resizing frames
#define FACE_DOWNSAMPLE_RATIO 3
//For skipping frames if required
#define SKIP_FRAMES 2
#define DEBUG 	0

using namespace dlib;
using namespace std;
using namespace cv;

template<typename T>

template <template<int,template<typename>class,int,typename> class block, int N, template<typename>class BN, typename SUBNET>
using residual = add_prev1<block<N,BN,1,tag1<SUBNET>>>;


template <template <int,template<typename>class,int,typename> class block, int N, template<typename>class BN, typename SUBNET>
using residual_down = add_prev2<avg_pool<2,2,2,2,skip1<tag2<block<N,BN,2,tag1<SUBNET>>>>>>;


template <int N, template <typename> class BN, int stride, typename SUBNET> 
using block  = BN<con<N,3,3,1,1,relu<BN<con<N,3,3,stride,stride,SUBNET>>>>>;

template <int N, typename SUBNET> using ares      = relu<residual<block,N,affine,SUBNET>>;
template <int N, typename SUBNET> using ares_down = relu<residual_down<block,N,affine,SUBNET>>;

template <typename SUBNET> using alevel0 = ares_down<256,SUBNET>;
template <typename SUBNET> using alevel1 = ares<256,ares<256,ares_down<256,SUBNET>>>;
template <typename SUBNET> using alevel2 = ares<128,ares<128,ares_down<128,SUBNET>>>;
template <typename SUBNET> using alevel3 = ares<64,ares<64,ares<64,ares_down<64,SUBNET>>>>;
template <typename SUBNET> using alevel4 = ares<32,ares<32,ares<32,SUBNET>>>;

using anet_type = loss_metric<fc_no_bias<128,avg_pool_everything<
                            alevel0<
                            alevel1<
                            alevel2<
                            alevel3<
                            alevel4<
                            max_pool<3,3,2,2,relu<affine<con<32,7,7,2,2,
                            input_rgb_image_sized<150>
			    >>>>>>>>>>>>;

static void split(std::vector<string> &toks, const string &s,const string &delims) {
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
/*
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
*/

class Region
{
	public:
		int m_NoOfPoints;
		std::vector<int> m_X,m_Y;
		Region();
		~Region();
};
class RegionData
{
	public:
		int m_RegionNo;
		std::vector<Region*> m_RegionHistory;
		RegionData();
		~RegionData();
};

class CameraData
{
	public:
		int m_CamId;
		cv::Mat m_Image;
		CameraData(int camid);
		~CameraData();
};

class DataSpace
{
	public:
		int m_NoOfCam;
		std::vector<CameraData*> m_CamList;
		int m_IdGen;
		DataSpace();
		~DataSpace();
		int initialize();
		void teardown();
		CameraData* addStream(std::vector<CameraData*>& camlist);
		void removeStream(std::vector<CameraData*> camlist,int camid);
		void setRegion(std::vector<CameraData*> camlist,int camid);
		std::tuple<std::vector<UINT8>,std::vector<std::string>> searchEvent(std::vector<CameraData*> camlist,int camid);
};
static DataSpace *globalDataSpace = NULL;
static RegionData* region_Data = NULL;

ErrorTypes Initialize();
ErrorTypes Teardown();
ErrorTypes AddStream(UINT8 *dataSpace);
ErrorTypes RemoveStream(UINT8 *dataSpace);
ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer);
ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer,UINT8 *eventBuffer);
ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput);
void generate_database(std::string var1,std::string var2); //var1 = "DSS_FOLD/" , var2 = "TrainingData.dat"
				
/*
typedef struct __Region {
	int nPoints;
	std::vector<int> x,y;
	__Region() {
		
	}
	~__Region() {
		for(int i = 0 ; i < nPoints ; i++)
		{
			delete &x[i];
			delete &y[i];
		}
		nPoints = 0;
	}
} Region;

typedef struct __RegionData {
	int region_no;
	std::vector<Region*> regionHistory1;
	__RegionData() {
	}
	~__RegionData() {
		for(int i = 0 ; i < region_no ; i++)
		{
			delete regionHistory1[i];								
		}
		region_no = 0;
	}
} RegionData;

typedef struct __CameraData {
	int camId;
	RegionData *region_Data;
	__CameraData(int _camId) 
	{
		camId = _camId;
	}
	~__CameraData() 
	{

	}
} CameraData;

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
*/


