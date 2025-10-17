#include "ProbeMaster.h"
#include "training.h"
Training *train = NULL;
FILE *fpData = fopen("employee_rec.txt", "w");
static int count_img = 1;
static int flagbit = 0;
Region::Region()
{
}
Region::~Region()
{
	for(int i = 0 ; i < m_NoOfPoints ; i++)
	{
		delete &m_X[i];
		delete &m_Y[i];
	}
	m_NoOfPoints = 0;
}

RegionData::RegionData()
{
}
RegionData::~RegionData()
{
	for(int i = 0 ; i < m_RegionNo ; i++)
	{
		delete m_RegionHistory[i];								
	}
	m_RegionNo = 0;
}

CameraData::CameraData(int camid)
{
	m_CamId = camid;
}
CameraData::~CameraData()
{
	delete &m_Image;
}
DataSpace::DataSpace()
{
	m_NoOfCam = 0;
	m_IdGen = 0;
}
DataSpace::~DataSpace()
{
	for (int cIndex = 0; cIndex < m_NoOfCam; cIndex++)
	{
		delete m_CamList[cIndex];
	}
	m_NoOfCam = 0;
}
int DataSpace::initialize()
{
	std::cout << "FR: inside initialize" << std::endl;
	globalDataSpace = new DataSpace();
	if (!globalDataSpace) {
		return 0;
	}
	return 1;
}
void DataSpace::teardown()
{
	std::cout << "FR: inside tearDown" << std::endl;
	delete globalDataSpace;
	globalDataSpace = NULL;
	
}
CameraData* DataSpace::addStream(std::vector<CameraData*>& camlist)
{
	std::cout << "FR: inside addStream" << std::endl;
	CameraData *cameraData = new CameraData(globalDataSpace->m_IdGen++);
	globalDataSpace->m_CamList.push_back(cameraData);
	std::cout << cameraData->m_CamId << endl;
	camlist = globalDataSpace->m_CamList;
	globalDataSpace->m_NoOfCam++;
	return cameraData;	
}
void DataSpace::removeStream(std::vector<CameraData*> camlist,int camid)
{
	std::cout << "inside removeStream" << std::endl;
	std::cout << "no of camera before delete" << ((DataSpace*) globalDataSpace)->m_NoOfCam << endl;
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->m_NoOfCam;cIndex++) {
		if (((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_CamId == camid) {
			((DataSpace*) globalDataSpace)->m_CamList.erase(((DataSpace*) globalDataSpace)->m_CamList.begin() + cIndex);
			((DataSpace*) globalDataSpace)->m_NoOfCam--;
			break;
		}
	}
	std::cout << "no of camera after delete" << ((DataSpace*) globalDataSpace)->m_NoOfCam << endl;	
}

void DataSpace::setRegion(std::vector<CameraData*> camlist,int camid)
{
	std::cout << "FR: inside setRegion" << std::endl;
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->m_NoOfCam;cIndex++) {
		if (((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_CamId == camid) {
			//RegionData* region_data = ((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_Region;
			std::cout << "no of regions:";
			int noOfRegions;
			std::cin >> noOfRegions;
			//std::cout << "a" << endl;
			//cout << &(region_data->m_RegionNo) << endl;
			region_Data = new RegionData();
			region_Data->m_RegionNo = noOfRegions;
			for(int regionNumber = 1; regionNumber <= noOfRegions; regionNumber++) {
				Region *newComp = new Region();
				int noofPoints;
				std::cout << "no of points:";
				std::cin >> noofPoints;
			        newComp->m_NoOfPoints = noofPoints;
				int x_point,y_point;		
				for(int pointNumber = 0; pointNumber < noofPoints; pointNumber++)
				{
					std::cout << "point_x:";
					std::cin >> x_point;
					newComp->m_X.push_back(x_point);
					std::cout << "point_y:";
					std::cin >> y_point;
					newComp->m_Y.push_back(y_point);
				}
				region_Data->m_RegionHistory.push_back(newComp);
			}
		}
	}
}

std::tuple<std::vector<UINT8>,std::vector<std::string>> DataSpace::searchEvent(std::vector<CameraData*> camlist,int camid)
{//2
	//train = new Training();
	std::cout << "FR: inside searchEvent" << std::endl;
	std::vector<std::string> output;
	std::tuple<std::vector<UINT8>,std::vector<std::string>> geek;
	int height = 0,width = 0;
	static int prev_data = 1;
	std::string location = "image" + std::to_string(count_img) + ".jpg";
	static int flag1 = 0;
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->m_NoOfCam;cIndex++) {
	if (((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_CamId == camid) {
	Mat inputImage = ((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_Image;
	//std::cout << "Dumping image" << std::endl;
	//std::string res = "/home/souro/Downloads/result1.jpg";
	//cv::imwrite(res.c_str(),inputImage);
	//std::cout << "image dumped" << std::endl;
	//RegionData* region_Data;			
//	Mat frame;
	Mat fgbgDiff;
	size_t sizeInBytes = inputImage.total() * inputImage.elemSize();
	int buffSize = (int)sizeInBytes;
	UINT8 *start_point = inputImage.data;
	size_t t_id,t_empid,t_firstname,t_lastname,t_status;
	InputArray iArr(inputImage);
	Mat frame1;
	Mat frame3,frame2,frame4;
	frame1 = inputImage;
	FLOAT32 scaleFactor = 1.0;
	FLOAT32 aspectRatio = frame1.rows / (FLOAT32)frame1.cols;
	if ( aspectRatio < 1.0 && frame1.cols > 320.0 )
	{//3
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		//resize( frame1, frame, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
		//resize( frame4, frame4, Size( 320, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
		ifstream myfile, myfile1, myfile2;
		string line;
		std::vector<string> nvmToks;
		myfile2.open("TrainingData1.dat");//TrainingData.dat file holds the face data of each person after training faces.
						// In this 128D vector space, images from the same person will be close to each other
						// but vectors from different people will be far apart.  So we can use these vectors to
						// identify if a pair of images are from the same person or from different people.  
		std::vector<matrix<float, 0, 1>> face_descriptors;
		std::vector<string> id;//This person_id vector for storing person's Id.
        	std::vector<unsigned int> min_Index;
		std::vector<string> firstname;//This person_id vector for storing person's name.
        	std::vector<string> lastname;//This person_id vector for storing person's Date of birth.
	        std::vector<string> empid;//This person_id vector for storing person's Gender.
		std::vector<string> status;
		//Reading every line of TrainingData.dat file
		while (getline(myfile2, line)) {//4
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
			id.push_back(nvmToks[128]);
			empid.push_back(nvmToks[129]);
                	firstname.push_back(nvmToks[130]);
                	lastname.push_back(nvmToks[131]);
			status.push_back(nvmToks[132]);
			face_descriptors.push_back(face_desc);
		}//4
		//std::cout << "D9" << std::endl;
		myfile2.close();
		//std::cout << "D10" << std::endl;
		//Reading image from buffer and store it into Mat format
		//Mat inputImage(1, buffSize, CV_8UC1, start_point);
        	//InputArray iArr(inputImage);
		cv::Mat temp, frame, temp1, image_gray, img2, img3, crop, tempo;
		//Declare flag = 0 ,this flag for event generation.************************************    
		int flag = 0;
        	int flag1 = 0;
		// We need a face detector.  We will use this to get bounding boxes for each face in an image
		// find faces in the image we will need a face detector:
		dlib::frontal_face_detector detector =  dlib::get_frontal_face_detector();
		//std::cout << "D11" << std::endl;
		// And we also need a shape_predictor.  This is the tool that will predict face
		// landmark positions given an image and face bounding box.  Here we are just
		// loading the model from the shape_predictor_5_face_landmarks.dat file.
		
		dlib::shape_predictor sp;
		deserialize("shape_predictor_68_face_landmarks.dat") >> sp;
		anet_type net;
		// And finally we load the DNN responsible for face recognition.
		deserialize("dlib_face_recognition_resnet_model_v1.dat") >> net;
		//imdecode for decode input frames
		temp = inputImage;
		//Store a copy of frame to a Mat type variable named temp1	
        	temp1 = temp;
		//Checking frame is empty or not.If empty then return DSS_ERROR message to the UI
		if (temp.empty()) {
			printf("stop");
			return geek;
			//resize( frame4, frame4, Size( scaleFactor * frame1.cols, 240.0), 0, 0, INTER_CUBIC);
		}
		//resize original image frames as per requirement
   		//cv::resize(temp, frame, cv::Size(), 1.0/FACE_DOWNSAMPLE_RATIO, 1.0/FACE_DOWNSAMPLE_RATIO);
		//Store a copy of resized frame into a Mat type variable
   		//img2 = frame;
		img2 = temp;
		frame = temp;
		//Checking jpeg is empty or not
       		if (img2.empty()) { cout<<"empty jpeg"; }
		//std::cout << "www" << endl;	
		//checking image is RGB or not.If RGB image then convert it into grayscale image.Time requirement for grayscale image is lesser than RGB image 
		//if (img2.channels() > 2) {
		//cv::cvtColor(img2, image_gray, COLOR_BGR2GRAY);
		//}
		IplImage ipl_img = cvIplImage(img2);
		//std::cout << "www" << endl;
        	//cv_image<bgr_pixel> cimg(ipl_img);
		//convert grayscale image to dlib cv_image format     
		cv_image<bgr_pixel> img(ipl_img);
		//std::cout << "www" << endl;
		//gettimeofday for time calculation of detection and recognition.
		//gettimeofday(&start, NULL);
		// Run the face detector on the image, and for each face extract a
		// copy that has been normalized to 150x150 pixels in size and appropriately rotated and centered.
		std::vector<matrix<rgb_pixel>> faces;
        	std::vector<dlib::rectangle> dets = detector(img);
        	//std::cout << "Dumping dets" << std::endl;
        	//std::cout << "Dets content: " << dets[0].height() << ", "  << dets[0].width() << std::endl;
        	//std::cout << "dets dumped" << std::endl;
		//std::cout << "www" << endl;
		int cntr = 0;
		for (auto face : dets) {
			printf("Face Detected\n");
			auto shape = sp(img, face);
			matrix<rgb_pixel> face_chip;
			extract_image_chip(img, get_face_chip_details(shape, 150, 0.25),face_chip);
			faces.push_back(move(face_chip));
		}
		//Checking if No faces found in image      
     		if (faces.size() == 0) {
			cout << "No faces found in image!" << endl;
		}    
		//if face detected then the faces count should be greater than 0.Then recognize faces.
		if(faces.size() > 0)
        	{//4
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
			Ptr<BackgroundSubtractor> pMOG2;
			pMOG2 = createBackgroundSubtractorMOG2();
			for (int i = 0; i < dets.size(); i++)
 			{//5	
				R.x = dets[0].left();
				R.y = dets[0].top();
				R.width = dets[0].width();
				R.height = dets[0].height();
				cv::rectangle(img2, R, cvScalar(0, 255, 0), 1, 1);
				img3 = img2;
				int imgRow = frame.rows;
				int imgCol = frame.cols;
				//FLOAT64 timestamp = 0; //timestamp will come from session Manager
				//printf("lRate = %lf\n", cameraData->lRate);
				pMOG2->apply(frame, fgbgDiff);
				Mat fgImage;
				threshold(fgbgDiff, fgImage, 200, 255, THRESH_BINARY);
				Mat element = getStructuringElement( MORPH_RECT, Size(4, 8));
				Mat tmp1;
				Mat tmp2;
				morphologyEx( fgImage, tmp1, MORPH_OPEN, element );
				Mat labels(1, buffSize, CV_8UC1, start_point);
				Mat stats;
				Mat centroids;
				int nComp = connectedComponentsWithStats(tmp1, labels, stats, centroids,8, CV_32SC1);
				printf("Component object = %d\n",nComp);
				for(int cIndex = 1; cIndex <= nComp; cIndex ++)
				{//6
					int compLabel = cIndex;
					int leftCol = stats.at<int>(Point(CC_STAT_LEFT, cIndex));
					int topRow = stats.at<int>(Point(CC_STAT_TOP, cIndex));
					int compWidth = stats.at<int>(Point(CC_STAT_WIDTH, cIndex));
					int compHeight = stats.at<int>(Point(CC_STAT_HEIGHT, cIndex));
					int compArea = stats.at<int>(Point(CC_STAT_AREA, cIndex));	
					if ( compArea < 100 )
					{
						for ( int rIndex = 0; rIndex < imgRow; rIndex ++ )
						{
							for ( int cIndex = 0; cIndex < imgCol; cIndex ++ )
							{
								if ( labels.at<int>(rIndex, cIndex) == compLabel )
								{
									tmp1.at<UINT8>(rIndex, cIndex) = 0;
								}
							}
						}
					}
					//For cropping faces from whole image store it into crop(Mat variable)
     					if(R.x >= 0 && R.y >= 0 && R.width + R.x < img3.cols && R.height + R.y < img3.rows)
     					{ 
    					   crop = img3(R);
     					}
					// In particular, one simple thing we can do is face clustering.  This next bit of code
					// creates a graph of connected faces and then uses the Chinese whispers graph clustering
					// algorithm to identify how many people there are and which faces belong to whom.
					for (size_t i = 0; i < face_descriptor_input.size(); ++i) 
					{
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
					std::string s_id = "";
		        		std::string s_empid = "";
		        		std::string s_firstname = "";
		        		std::string s_lastname = "";
					std::string s_status = "";
					//Accessing recognized person's details   
		        		if (minIndex != -1) {
						//Getting recognized person's id from the vector  and store it to a string variable
			 			s_id  = id[minIndex].c_str();
		        			s_empid  = empid[minIndex].c_str();
		         			s_firstname = firstname[minIndex].c_str();
		         			s_lastname = lastname[minIndex].c_str();
						s_status = status[minIndex].c_str();
						//change the flag values to 1
	                			flag = 1;
	                			flag1 = 1;
						t_id = s_id.size();
						t_empid = s_empid.size();
						t_firstname = s_firstname.size();
						t_lastname = s_lastname.size();
						t_status = s_status.size();
						//Printing the recognized person's data	
						cout<<"ID SIZE : "<<t_id<<"ID:"<<s_id<<endl;
						fprintf(fpData,"ID SIZE : %s",std::to_string(t_id));
						fprintf(fpData," Employee ID:%s\n",s_id);
	   					cout<<"Empid SIZE : "<<t_empid<<" Employee ID: "<<s_empid<<endl;
						fprintf(fpData,"Empid SIZE : %s",std::to_string(t_empid));
						fprintf(fpData," Employee ID : %s\n",s_empid);
						cout<<"firstname SIZE  :"<<t_firstname<<"firstname:"<<s_firstname<<endl;
						fprintf(fpData,"firstname SIZE  : %s",std::to_string(t_firstname));
						fprintf(fpData," firstname : %s\n",s_firstname);
	       					cout<<"lastname  size : "<<t_lastname<<"lastname:"<<s_lastname<<endl;
						fprintf(fpData,"lastname  size : %s",std::to_string(t_lastname));
						fprintf(fpData," lastname : %s\n",s_lastname);
						cout<<"status size :"<<t_status<<"status:"<<s_status<<endl;
						fprintf(fpData,"status size : %s",std::to_string(t_status));
						fprintf(fpData," status: %s\n",s_status);
						cv::imwrite(location.c_str(),img2);
						count_img++;
	        			}  
					/*   
					else if(flagbit == 0)
					{
						flagbit = 1;
						cout<<"detected face cannot be recognized"<<endl;
						fprintf(fpData,"detected face cannot be recognized");
						train->generate_database();
						continue;
					} 
					*/  
					else 
					{
						cout<<"detected face cannot be recognized"<<endl;
						continue;
					}
					//Getting output buffer size from event buffer first 4byte 
					//int buffSize1 = *((int*) eventBuffer);
					//std::vector<UINT8> jpegData;
					//Encode cropped face & store it as jpeg image to the vector
					//imencode(".jpg", crop, jpegData);
					//Calculate the size of the encoded image size
					int totalArea;
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
					}
					int area = imgRow * imgCol;
					if(totalArea > ((5 * area) / 10))
					{
						flag = 1;
					}
					printf(" compArea= %d ",totalArea);
					fflush(stdout);
					printf(" Area= %d\n",area);
					fflush(stdout);
					if ( aspectRatio < 1.0 )
					{
						scaleFactor = 640.0 /frame1.cols;
						//resize( frame1, frame2, Size( 640, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
						//resize( frame4, frame4, Size( 640, (int)(scaleFactor * frame1.rows)), 0, 0, INTER_CUBIC);
					}
					else
					{
						scaleFactor = 480.0 /frame1.rows;
						//resize( frame1, frame2, Size( (int)(scaleFactor * frame1.cols), 480), 0, 0, INTER_CUBIC);
						//resize( frame4, frame4, Size( (int)(scaleFactor * frame1.cols), 480), 0, 0, INTER_CUBIC);
					}
					//std::cout << "5" << endl;

					/*
					if(region_Data->m_RegionNo > 0)
					{
					for(int regionNumber = 0; regionNumber < region_Data->m_RegionHistory.size(); regionNumber++) 
					{	
					Point points[1][region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints];
					for(int pointNumber = 0; pointNumber<region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints  ; pointNumber++)		
					{
					points[0][pointNumber] = Point(region_Data->m_RegionHistory[regionNumber]->m_X[pointNumber],region_Data->m_RegionHistory[regionNumber]->m_Y[pointNumber]);
					}
					const Point* ppt[1] = { points[0] };
					int npt[] = { region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints };
					fillPoly( frame4, ppt, npt, 1, Scalar(128, 128, 128), 8 );
					}
					}

					//std::cout << "5" << endl;
	
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



					//std::cout << "5" << endl;
					if(flag1 == 1)
					{
						if(region_Data->m_RegionNo > 0)
						{
						for(int regionNumber = 0; regionNumber < region_Data->m_RegionHistory.size(); regionNumber++) 							{		
						for(int pointNumber = 0; pointNumber < region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1; pointNumber++)
						{
							int x1 = region_Data->m_RegionHistory[regionNumber]->m_X[pointNumber];	
							int y1 = region_Data->m_RegionHistory[regionNumber]->m_Y[pointNumber];	
							int x2 = region_Data->m_RegionHistory[regionNumber]->m_X[pointNumber + 1];	
							int y2 = region_Data->m_RegionHistory[regionNumber]->m_Y[pointNumber + 1];
							cv::line( frame2, Point(x1, y1), Point(x2, y2), Scalar(0, 0, 255), 1);
						}
						//catch last line
						int x0 = region_Data->m_RegionHistory[regionNumber]->m_X[0];
						int y0 = region_Data->m_RegionHistory[regionNumber]->m_Y[0];
						int xn = region_Data->m_RegionHistory[regionNumber]->m_X[region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1];
						int yn = region_Data->m_RegionHistory[regionNumber]->m_Y[region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1];
						cv::line( frame2, Point(x0, y0), Point(xn, yn), Scalar( 0, 0, 255 ),1);
						}
						}
					}
					if(flag1 == 0)
					{
						if(region_Data->m_RegionNo > 0)
						{
						for(int regionNumber = 0; regionNumber < region_Data->m_RegionHistory.size(); regionNumber++) 							{		
						for(int pointNumber = 0; pointNumber < region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1; pointNumber++)
						{
							int x1 = region_Data->m_RegionHistory[regionNumber]->m_X[pointNumber];	
							int y1 = region_Data->m_RegionHistory[regionNumber]->m_Y[pointNumber];	
							int x2 = region_Data->m_RegionHistory[regionNumber]->m_X[pointNumber + 1];	
							int y2 = region_Data->m_RegionHistory[regionNumber]->m_Y[pointNumber + 1];
							cv::line( frame2, Point(x1, y1), Point(x2, y2), Scalar(0, 0, 255), 1);
						}
						//catch last line
						int x0 = region_Data->m_RegionHistory[regionNumber]->m_X[0];
						int y0 = region_Data->m_RegionHistory[regionNumber]->m_Y[0];
						int xn = region_Data->m_RegionHistory[regionNumber]->m_X[region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1];
						int yn = region_Data->m_RegionHistory[regionNumber]->m_Y[region_Data->m_RegionHistory[regionNumber]->m_NoOfPoints - 1];
						cv::line( frame2, Point(x0, y0), Point(xn, yn), Scalar( 0, 0, 255 ),1);
						}
						}
					}
					*/
					//std::cout << "5" << endl;
					std::vector<UINT8> jpegData;
					imencode(".jpg", frame1, jpegData);
					int size = jpegData.size();
					//std::string s = {jpegData.begin(),jpegData.end()};
					output.push_back(std::to_string(size));
					output.push_back(std::to_string(flag));
					//output.push_back(s.c_str());
					output.push_back(std::to_string(flag1));
					//output.push_back(std::to_string(t_id));
					output.push_back(s_id.c_str());
					//output.push_back(std::to_string(t_name));
					output.push_back(s_empid.c_str());
					//output.push_back(std::to_string(t_dob));
					output.push_back(s_firstname.c_str());
					//output.push_back(std::to_string(t_gender));
					output.push_back(s_lastname.c_str());
					//output.push_back(std::to_string(t_status));
					output.push_back(s_status.c_str());
					output.push_back(location.c_str());
					geek = make_tuple(jpegData,output);	
				}//6	
			}//5
		}//4
	}//3
}
}
//delete train;
//train = NULL;
std::cout << "DEnd" << std::endl;
return geek;	
}//2
