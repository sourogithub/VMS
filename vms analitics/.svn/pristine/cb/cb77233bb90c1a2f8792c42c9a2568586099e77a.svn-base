


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

// This split function for splitting every word of a line first parameter signifies the tokens,second parameter holds the line and third parameter holds the delimeter means separated by special symbol.
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
int main(int argc, char** argv) try
{
    if (argc != 2)
    {
        cout << "Run this example by invoking it like this: " << endl;
        cout << "   ./generate_database bald_guys.jpg" << endl;
        cout << endl;
        return 1;
    }

// The first thing we are going to do is load all our models.  First, since we need to
// find faces in the image we will need a face detector:
    frontal_face_detector detector = get_frontal_face_detector();
// We will also use a face landmarking model to align faces to a standard pose:  (see face_landmark_detection_ex.cpp for an introduction)
    shape_predictor sp;
    deserialize("shape_predictor_5_face_landmarks.dat") >> sp;
// And finally we load the DNN responsible for face recognition.
    anet_type net;
    deserialize("dlib_face_recognition_resnet_model_v1.dat") >> net;
    matrix<rgb_pixel> img1;

        ifstream myfile, myfile1;
	string line;
	std::vector <string> nvmToks;
//Open a .dat file named TrainingData.dat.It will store the all the data of training faces
	FILE *fpData = fopen("TrainingData.dat", "w");

	//Read the outer text file where the details of each person is
	myfile.open("DSS_FOLDER/PersonList.txt");
	while(getline(myfile, line))
	{
		split(nvmToks, line, " ");
//getting id of the person
		string id = nvmToks[0];
//Getting folder name for a particular person
		string folderName = "DSS_FOLDER/" + nvmToks[0];
//Getting name of the person 
		string personName = nvmToks[1];
//Getting gender of the person
                string personGender = nvmToks[2];
//Getting Date of birth of the person
                string personDob = nvmToks[3];
   
		std::vector<matrix<rgb_pixel>> faces1;
//Getting List of images for a person from the image_list.txt file
		string imgListFile = folderName + "/image_list.txt";
//Read the inner text file where the image files are
		myfile1.open(imgListFile.c_str());
                string imgFile;
		while(getline(myfile1, line))
		{
			imgFile = folderName + "/" + line;
//Load every images in a file
			load_image(img1, imgFile.c_str());
// Run the face detector on the image, and for each face extract a
// copy that has been normalized to 150x150 pixels in size and appropriately rotated
// and centered.
	    	for (auto face : detector(img1))
	    	{
	    		auto shape = sp(img1, face);
	    		matrix<rgb_pixel> face_chip1;
	    		extract_image_chip(img1, get_face_chip_details(shape,150,0.25), face_chip1);
	    		faces1.push_back(move(face_chip1));
	    	}
		}
		myfile1.close();
//Checking if No faces found in image  
	    if (faces1.size() == 0)
	    {
	        cout << "No faces found for " << personName << endl;
	        continue;
	    }
//printing Number of detected faces, person's id, person's name,person's gender,person's date of birth
	    cout << "N-Faces " << faces1.size() << "  " << id<<"  "<<personName <<"  "<< personGender<<"  "<<personDob<<endl;
           
// This call asks the DNN to convert each face image in faces into a 128D vector.
// In this 128D vector space, images from the same person will be close to each other
// but vectors from different people will be far apart.  So we can use these vectors to
// identify if a pair of images are from the same person or from different people.  
	    std::vector<matrix<float,0,1>> face_descriptors1 = net(faces1);
// In particular, one simple thing we can do is face clustering.  This next bit of code
// creates a graph of connected faces and then uses the Chinese whispers graph clustering
// algorithm to identify how many people there are and which faces belong to whom
	    for (int index1 = 0; index1 < face_descriptors1.size(); index1 ++ )
	    {
	    	std::vector<float> tmpArr(face_descriptors1[index1].begin(), face_descriptors1[index1].end());
//inner loop for putting all the face descriptors float value to the TrainingData.dat file
	    	for ( int index2 = 0; index2 < tmpArr.size(); index2 ++ )
	    	{
	    		fprintf(fpData, "%f ", tmpArr[index2]);
	    	}
//With respect to face descriptor values putting persons id ,name,dob,gender to the text file
	    	fprintf(fpData, "%s %s %s %s\n", id.c_str(), personName.c_str(),personGender.c_str(),personDob.c_str());
	    }
	}
	fclose(fpData);
          
}
//Catch block
catch (std::exception& e)
{
    cout << e.what() << endl;
}

// ----------------------------------------------------------------------------------------
// All this function does is make 100 copies of img, all slightly jittered by being
// zoomed, rotated, and translated a little bit differently. They are also randomly
// mirrored left to right.
std::vector<matrix<rgb_pixel>> jitter_image(
    const matrix<rgb_pixel>& img
)
{
    
    thread_local dlib::rand rnd;

    std::vector<matrix<rgb_pixel>> crops; 
    for (int i = 0; i < 100; ++i)
        crops.push_back(jitter_image(img,rnd));

    return crops;
}

// ----------------------------------------------------------------------------------------



