//This code is for test our face detection accuracy to FDDB dataset.
/*
 * rest.cpp
 *
 *  Created on: 26-Feb-2019
 *      Author: dss-04
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <sstream>
#include <string.h>
#include <fstream>
#include <vector>
#include <math.h>

using namespace std;

#define DIST_TH 40.0
//Structure for storing centre of ellipse 
typedef struct FaceCenter
{
	int X;
	int Y;
}FaceCenter;
//Structure for storing image name,face centered ellipse details
typedef struct Frame
{
	string imgName;
	vector<FaceCenter> faceList;
}Frame;

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

int main(int argc,char **argv)
{
	ifstream myfile1;
	string line;
	vector <string> nvmToks;
	string fname1 = "";
	string fname2 = "";
	string fname3 = "";
//Three arguments are 1.FDDB dataset ellipse information containing file 2.File made by our face detection algo 2. Result file
	if( argc == 4 ) {
		fname1 = argv[1];
		fname2 = argv[2];
		fname3 = argv[3];
	}


	myfile1.open(fname1);


	vector<Frame *> gtFaceList;
//Outer loop parsing file content
	while (getline(myfile1, line) )
	{
		Frame *newFrame = new Frame();
//Getting name of the image from file
		newFrame->imgName = line;

		getline(myfile1, line);
//splitting line separated by " "
		split(nvmToks, line, " ");
//get number of faces from a image 
		int nFaces = stoi(nvmToks[0]);
//inner loop for getting the centre of face centered ellipse
		for ( int i = 0; i < nFaces; i ++ )
		{
			getline(myfile1, line);
			split(nvmToks, line, " ");
			int cenX = stof(nvmToks[3]);
			int cenY = stof(nvmToks[4]);
			newFrame->faceList.push_back({cenX,cenY});
		}

		gtFaceList.push_back(newFrame);
	}
	myfile1.close();
//Openning second file which is created by our program
	myfile1.open(fname2);
	vector<Frame *> resFaceList;
//Outer loop parsing file content
	while (getline(myfile1, line) )
	{
		Frame *newFrame1 = new Frame();
		newFrame1->imgName = line;

		getline(myfile1, line);
		split(nvmToks, line, " ");
//get number of faces from a image 
		int nFaces = stoi(nvmToks[0]);
//inner loop for getting the centre of face centered ellipse
		for ( int i = 0; i < nFaces; i ++ )
		{
			getline(myfile1, line);
			split(nvmToks, line, " ");
			int cenX = stof(nvmToks[3]);
			int cenY = stof(nvmToks[4]);

			newFrame1->faceList.push_back({cenX,cenY});
		}

		resFaceList.push_back(newFrame1);
	}
	myfile1.close();
	int faceCount = 1;
	int totalFaces = 0;
	int rightlyDetected = 0;
	int wronglyDetected = 0;
	int notDetected = 0;
	ofstream myfile2;
//This file for writing output result
	myfile2.open(fname3);
//These are the Columns of the file
	myfile2<<"SL";
	myfile2<<",";
	myfile2<<"Image";
	myfile2<<",";
	myfile2<<"GT_Center_X";
	myfile2	<<",";
	myfile2	<<"GT_Center_Y";
	myfile2	<<",";
	myfile2<< "Res_Center_X";
	myfile2	<<",";
	myfile2<< "Res_Center_Y";
	myfile2<<",";
	myfile2<< "Distance";
	myfile2<<",";
	myfile2<<"Result";
	myfile2<<"\n";
	cout << gtFaceList.size() << endl;
	cout << resFaceList.size() << endl;

	//Itterate for every image
	for ( int i = 0; i < gtFaceList.size(); i ++ )
	{
		Frame *gtFrame = gtFaceList[i];
		totalFaces += gtFrame->faceList.size();

		if ( gtFrame != NULL )
		{
			//Search for the same image in result
			for ( int j = 0; j < resFaceList.size(); j ++ )
			{
				Frame *resFrame = resFaceList[j];

				if ( gtFrame->imgName == resFrame->imgName )
				{
					printf("Name matched\n");
					//Found the image
					//Itterate for each faces and match
					for ( int k = 0; k <  gtFrame->faceList.size(); k ++ )
					{
						myfile2<<faceCount ++;
						myfile2	<<",";
						myfile2	<< gtFrame->imgName.c_str();
						myfile2	<<",";
						myfile2	<< gtFrame->faceList[k].X;
						myfile2	<<",";
						myfile2	<< gtFrame->faceList[k].Y;
						myfile2	<<",";
						bool  matchFlag = false;
						for ( int l = 0; l <  resFrame->faceList.size(); l ++ )
						{
//Distance calculation for each point
							int dx = gtFrame->faceList[k].X - resFrame->faceList[l].X;
							int dy = gtFrame->faceList[k].Y - resFrame->faceList[l].Y;
//Euclidean Distance calculation
							float error = (float)sqrt((double)(dx*dx + dy*dy));
//If distance is lesser than threshold.
							if ( error < DIST_TH )
							{
								rightlyDetected ++;
								matchFlag = true;
//Write output to the result file
								myfile2<<resFrame->faceList[k].X;
								myfile2<<",";
								myfile2	<<resFrame->faceList[k].Y;
								myfile2<<",";
								myfile2	<<error;

								break;
							}
						}
//If flag value not change means wrong detection
						if ( !matchFlag )
						{
							wronglyDetected ++;
						}

						myfile2<<"\n";
					}

//Calculate Not detected faces
					int fDiff = gtFrame->faceList.size() - resFrame->faceList.size();
					notDetected += (fDiff > 0 ? fDiff : 0);
				}
			}
		}

	}
//Print result

	printf("Total frames: %d, total faces: %d, rightly detected: %d,wrongly detected %d, Accuraccy: %f percent\n",
			gtFaceList.size(), totalFaces, rightlyDetected, wronglyDetected,(100.0f * rightlyDetected)/(float)totalFaces );
	return 0;
}

