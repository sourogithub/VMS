#include <iostream>
#include <stdio.h>
#include<unistd.h>
#include<string.h>
#include <algorithm> 
#include <dlib/dnn.h>
#include <dlib/gui_widgets.h>
#include <dlib/clustering.h>
#include <dlib/string.h>
#include <dlib/image_io.h>
#include <dlib/image_processing/frontal_face_detector.h>
 #include <algorithm> 
#include <vector> 
#include<string>
#include <bits/stdc++.h> 
#include <opencv2/opencv.hpp>
#include <fstream> 
#include <experimental/filesystem>

using namespace dlib;
using namespace std;
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

template <template <int,template<typename>class,int,typename> class block, int N, template<typename>class BN, typename SUBNET>
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
// ----------------------------------------------------------------------------------------
// All this function does is make 100 copies of img, all slightly jittered by being
// zoomed, rotated, and translated a little bit differently. They are also randomly
// mirrored left to right.
std::vector<matrix<rgb_pixel>> jitter_image(
    const matrix<rgb_pixel>& img
);
// This split function for splitting every word of a line 
static void split(std::vector<string> &toks, const string &s, const string &delims)
{
	toks.clear();

	string::const_iterator segment_begin = s.begin();
	string::const_iterator current = s.begin();
	string::const_iterator string_end = s.end();

	while (true)
	{
		if (current == string_end || delims.find(*current) != string::npos || *current == '\r')
		{
			if (segment_begin != current)
				toks.push_back(string(segment_begin, current));

			if (current == string_end || *current == '\r')
				break;

			segment_begin = current + 1;
		}

		current++;
	}

}
int main(int argc, char** argv) {

   if (argc < 2) {
//In Command line Passing the name of the folder(folder name is as per person's id) to be deleted  
      cerr << "Usage: " << argv[0] << " [dir name]" << endl;
   }
 
   string arg1 = "";
//Taking outer folder name in a string
   arg1 = "DSS_FOLDER/";
   string arg2 = "";
//inner folder to be deleted taking that in string named arg2
   arg2 = argv[1];
//Concat two string for getting the path of the folder to be deleted
  std::string directory = arg1 + arg2;
//To remove directory
std::experimental::filesystem::remove_all(directory.c_str());
rmdir(directory.c_str());
        remove(directory.c_str()); 
        fstream myfile;
        fstream myfile1;
	string line,line1;
	std::vector <string> nvmToks;
//Read  text file were the details of each person is taking that file as an input file
	myfile.open("DSS_FOLDER/PersonList.txt",ios::in);
//Opening another text file as an output file which will keep the person details after deletion of a specific person      
        myfile1.open("DSS_FOLDER/PersonList1.txt",ios::out);  
//This Outer text file contains all the information of the person.Fetching the id of the person who's data will not present in the text file     
	while(getline(myfile, line))
	{
		split(nvmToks, line, " ");

//Fetching person's id		
                string id   = nvmToks[0];
//compare id with the input argument(id to be deleted)
//if matches then the id and other details should not present in the text file after delete operation		
		if(strcmp(arg2.c_str(),id.c_str()) != 0)
                 {
//write the data to the output text file
                      myfile1 << line << endl;
                  }
	    	}
//close both input and output file
myfile1.close();
myfile.close();
        myfile.open("DSS_FOLDER/PersonList.txt",ios::out | ios::trunc);
        myfile1.open("DSS_FOLDER/PersonList1.txt",ios::in);
//This is for copy the PersonList1.txt(holds person informations after delete a particular person) to the main  file from which further operations will be done
while(getline(myfile1, line))
	{
		split(nvmToks, line, " ");
                myfile<< line << endl;
                  
	    	}
		
	
    return 0;
}
