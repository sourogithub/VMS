#include "ProbeMaster1.h"
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

std::vector<matrix<rgb_pixel>> DataSpace::faceDet(std::vector<CameraData*> camlist,int camid)
{
	std::cout << "FR: inside searchEvent" << std::endl;
	std::cout << "FR: inside facedetection" << std::endl;
	std::vector<matrix<rgb_pixel>> faces;
	int height = 0,width = 0;
	static int prev_data = 1;
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->m_NoOfCam;cIndex++) {
	if (((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_CamId == camid) {
	Mat inputImage = ((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_Image;
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
	{
		scaleFactor = 320.0 /frame1.cols;
		printf("Row %d\n", (int)(scaleFactor * frame1.rows));
		cv::Mat temp, frame, temp1, image_gray, img2, img3, crop, tempo;   
		int flag = 0;
        	int flag1 = 0;
		dlib::frontal_face_detector detector =  dlib::get_frontal_face_detector();
		dlib::shape_predictor sp;
		deserialize("shape_predictor_68_face_landmarks.dat") >> sp;
		temp = inputImage;	
        	temp1 = temp;
		if (temp.empty()) {
			printf("stop");
			return faces;
		}
		img2 = temp;
		frame = temp;
       		if (img2.empty()) { cout<<"empty jpeg"; }
		IplImage ipl_img = cvIplImage(img2);   
		cv_image<bgr_pixel> img(ipl_img);
        	std::vector<dlib::rectangle> dets = detector(img); 
		for (auto face : dets)
		{
			printf("Detected\n");
			full_object_detection shape = sp(img, face);
			int n_x = shape.part(34).x() ; 
			int n_y = shape.part(34).y() ;
			int e1_x = shape.part(37).x(); 
			int e1_y = shape.part(37).y();
			int e2_x = shape.part(46).x(); 
			int e2_y = shape.part(46).y();
			float left_dist = sqrt((e1_x - n_x)*(e1_x - n_x) + (e1_y - n_y)*(e1_y - n_y));
			float right_dist = sqrt((e2_x - n_x)*(e2_x - n_x) + (e2_y - n_y)*(e2_y - n_y));
			cout << left_dist << endl;
			cout << right_dist << endl;
			float score = (left_dist - right_dist)/(left_dist + right_dist)*100;
			cout << score << endl;
			if (score > 30)
			{
				cout << "left turned face" << endl;
			}
			else if (score < -30)
			{
				cout << "right turned face" << endl;
			}
			else
			{
				cout << "front face" << endl;
			}
			matrix<rgb_pixel> face_chip;
			extract_image_chip(img, get_face_chip_details(shape, 150,0.25), face_chip);
			faces.push_back(move(face_chip));
		}
        	if (faces.size() == 0)
        	{
           		cout << "No faces found in image!" << endl;
           		continue;        
        	} 

	} 
	}
	}
	return faces;
}

std::tuple<std::vector<UINT8>,std::vector<std::string>> DataSpace::faceRec(std::vector<matrix<rgb_pixel>> faces,std::vector<CameraData*> camlist,int camid)
{
	std::cout << "FR: inside facerecognition" << std::endl;
	std::vector<std::string> output;
	std::tuple<std::vector<UINT8>,std::vector<std::string>> geek;
	std::string location = "image" + std::to_string(count_img) + ".jpg";
	Mat inputImage;
	int height = 0,width = 0;
	static int prev_data = 1;
	for (int cIndex = 0; cIndex < ((DataSpace*) globalDataSpace)->m_NoOfCam;cIndex++) {
	if (((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_CamId == camid) {
	inputImage = ((DataSpace*) globalDataSpace)->m_CamList[cIndex]->m_Image;
	}
	}	
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
	cv::Mat temp, frame, temp1, image_gray, img2, img3, crop, tempo;   
	dlib::frontal_face_detector detector =  dlib::get_frontal_face_detector();
	int flag = 0;
        int flag1 = 0;
	temp = inputImage;	
        temp1 = temp;
	if (temp.empty()) {
		printf("stop");
		return geek;
	}
	img2 = temp;
	frame = temp;
      	if (img2.empty()) { cout<<"empty jpeg"; }
	IplImage ipl_img = cvIplImage(img2);   
	cv_image<bgr_pixel> img(ipl_img);
       	std::vector<dlib::rectangle> dets = detector(img);


	ifstream myfile, myfile1, myfile2;
	string line;
	std::vector<string> nvmToks;
	myfile2.open("TrainingData1.dat"); 
	static std::vector<matrix<float, 0, 1>> face_descriptors;
	static std::vector<string> id;
        static std::vector<unsigned int> min_Index;
	static std::vector<string> firstname;
        static std::vector<string> lastname;
	static std::vector<string> empid;
	static std::vector<string> status;
	if (flagbit == 0)
	{
	while (getline(myfile2, line)) 
	{
	
		split(nvmToks, line, " ");
			
		dlib::array<float> float_array = dlib::array<float>();
		
		for (int i = 0; i < 128; i++) {
			float t = stof(nvmToks[i]);
			float_array.push_back(t);
		}
	
		auto float_mat = mat(float_array);
		
		auto face_desc = dlib::matrix<float, 0, 1>(float_mat);
		id.push_back(nvmToks[128]);
		empid.push_back(nvmToks[129]);
	        firstname.push_back(nvmToks[130]);
	        lastname.push_back(nvmToks[131]);
		status.push_back(nvmToks[132]);
		face_descriptors.push_back(face_desc);
	}
	myfile2.close();
	flagbit = 1;
	}		
	dlib::shape_predictor sp;
	deserialize("shape_predictor_68_face_landmarks.dat") >> sp;
	anet_type net;	
	deserialize("dlib_face_recognition_resnet_model_v1.dat") >> net;
		
		
	if(faces.size() > 0)
        {
			
		std::vector<matrix<float, 0, 1>> face_descriptor_input;
			 
		face_descriptor_input = net(faces);
		int minDist, minIndex;
			
		cv::Rect R;
		Ptr<BackgroundSubtractor> pMOG2;
		pMOG2 = createBackgroundSubtractorMOG2();
		for (int i = 0; i < dets.size(); i++)
 		{	
			R.x = dets[0].left();
			R.y = dets[0].top();
			R.width = dets[0].width();
			R.height = dets[0].height();
			cv::rectangle(img2, R, cvScalar(0, 255, 0), 1, 1);
			img3 = img2;
			int imgRow = frame.rows;
			int imgCol = frame.cols;
			
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
			{
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
				
     				if(R.x >= 0 && R.y >= 0 && R.width + R.x < img3.cols && R.height + R.y < img3.rows)
     				{ 
    				   crop = img3(R);
     				}
					
				for (size_t i = 0; i < face_descriptor_input.size(); ++i) 
				{
					minDist = 1.0;
					minIndex = -1;
					for (size_t j = 0; j < face_descriptors.size(); ++j) {
						float dist = length(face_descriptor_input[i] - face_descriptors[j]);
							
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
				else 
				{
					cout<<"detected face cannot be recognized"<<endl;
					continue;
				}	
					
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
			}	
		}
	}
}
		
