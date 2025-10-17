
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



class Training
{
     public:
	ErrorTypes generate_database();
	Training();
	~Training();
};
