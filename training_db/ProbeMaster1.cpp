#include "ProbeMaster.h"
#include <ctime>
DataSpace* obj= NULL;
std::vector<CameraData*> camlist = {};
//static int id = 1;
ErrorTypes Initialize() {
	std::cout << "This is Initialize" << std::endl;
	obj = new DataSpace();
	int j = obj->initialize();
	if(j==1)
		return DSS_SUCCESS;
	else
		return DSS_ERROR;
}

ErrorTypes Teardown() {
	std::cout << "This is Teardown" << std::endl;
	obj->teardown();
	delete obj;
	obj= NULL;
	return DSS_SUCCESS;
}

ErrorTypes AddStream(UINT8 *dataSpace) {
	std::cout << "This is AddStream" << std::endl;
	CameraData *cameraData = obj->addStream(camlist);
//Printing Buffer size
	if (cameraData == NULL) {
		return DSS_ERROR;
	}
	printf("DataSpace Size = %d", *((UINT32*) dataSpace));
//Put camera data to the buffer
	*((UINT64*) dataSpace + sizeof(char)) = (UINT64) cameraData;
	return DSS_SUCCESS;
}

ErrorTypes RemoveStream(UINT8 *dataSpace) {
	std::cout << "This is RemoveStream" << std::endl;
	CameraData *cameraData = (CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
	//Taking id of camera to be deleted
	int camid = cameraData->m_CamId;
	obj->removeStream(camlist,camid);
	//After delete return success to the UI
	return DSS_SUCCESS;
}

ErrorTypes SetRegion(UINT8 *dataSpace, UINT8 *regionBuffer) {
	std::cout << "This is SetRegion" << std::endl;
	CameraData *cameraData = (CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
	int camid = cameraData->m_CamId;
	obj->setRegion(camlist,camid);
	return DSS_SUCCESS;
}	

ErrorTypes SearchEvent(UINT8 *dataSpace, UINT8 *imageBuffer,UINT8 *eventBuffer) {
	time_t now = time(0);
	char* dt = ctime(&now);
	std::cout << "The local date and time is: " << dt << std::endl;
	std::string time = dt;
	std::cout << "This is SearchEvent" << std::endl;
	CameraData *cameraData = (CameraData*) (*((UINT64*) dataSpace + sizeof(char)));
	//std::cout << "D1" << std::endl;
	int camid = cameraData->m_CamId;
	//std::cout << "D2" << std::endl;
	int buffSize = *((int*) imageBuffer);
	//std::cout << "D3" << std::endl;
	struct timeval start, end;
	UINT8 *timeStamp = (imageBuffer + sizeof(int));//getting timestamp
	//std::cout << "D4" << std::endl;
	UINT8 *start_point = (imageBuffer + sizeof(int) + 2 * sizeof(int));//pointer points to the start point of the image data
	//std::cout << "D5" << std::endl;
	UINT8 *end_point = (imageBuffer + sizeof(int)+  2 * sizeof(int) + buffSize);//end_point denotes the end of the image
	//std::cout << "D6" << std::endl;
	Mat inputImage(1, buffSize, CV_8UC1, start_point);
	//std::cout << "D7" << std::endl;
	cv::Mat frame1;
	frame1 = imdecode(inputImage, 1);
	std::cout << "PM Dumping image" << std::endl;
	std::string res = "/home/souro/Downloads/result.jpg";
	cv::imwrite(res,frame1);
	std::cout << "PM image dumped" << std::endl;
	//std::cout << "D1" << std::endl;
	//std::cout << "camid: "  << camid << std::endl;
	//std::cout << "D2" << std::endl;
	CameraData* cd= camlist[camid];
	//std::cout << "D3" << std::endl;
	cd->m_Image = frame1;
	//std::cout << "D4" << std::endl;
	std::tuple<std::vector<UINT8>,std::vector<std::string>> geek;
 	//std::vector<std::string> output;
	geek = obj->searchEvent(camlist,camid);
	std::cout << "PM After searchEvent" << std::endl;
	std::vector<UINT8> tv0= get<0>(geek);
	std::vector<std::string> tv1= get<1>(geek);
	if(tv1.size() == 0) {
		return DSS_SUCCESS;
	}
	
	int size = std::stoi(get<1>(geek).at(0));
	int flag = std::stoi(get<1>(geek).at(1));
	std::vector<UINT8> jpegData = get<0>(geek);
	int flag1 = std::stoi(get<1>(geek).at(2)); 
	std::string s_id = get<1>(geek).at(3);
	size_t t_id = s_id.size();
	std::string s_empid = get<1>(geek).at(4);
	size_t t_empid = s_empid.size();
	std::string s_firstname = get<1>(geek).at(5);
	size_t t_firstname = s_firstname.size();
	std::string s_lastname = get<1>(geek).at(6);
	size_t t_lastname = s_lastname.size();
	std::string s_status = get<1>(geek).at(7);
	size_t t_status = s_status.size();
	std::string location = get<1>(geek).at(8);
	std::cout << location << std::endl;
	sqlite3 *db;
   	char* ErrMsg;
   	int rc;
	//ifstream myfile1;
	sqlite3_stmt* t_statement;
	rc = sqlite3_open("VMS_ATTENDENCE_DB.db", &db);
   	if( rc ) {
      		fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
		return DSS_ERROR;
   	} 
	else {
      		fprintf(stderr, "Opened database successfully\n");
   	}	
	std::string sql1= "INSERT INTO ATTENDENCELOG (empid, temparatue, dayFirstInTime, dayLastOutTime, lastInTime, doorid, totalInDuration, cur_snap,cur_time) VALUES ('" +s_empid + "','0','0','0','0','" +std::to_string(camid)+ "','0','"+location+"','"+ time +"');";
	//id++;
        const char* sql2=reinterpret_cast< const char * >(sql1.c_str());
	rc = sqlite3_exec(db, sql2, NULL, 0, &ErrMsg);
	if (rc != SQLITE_OK) { 
        std::cerr << "Error inserting into Table" << std::endl; 
        //sqlite3_free(zErrMsg); 
	std::cout << ErrMsg << std::endl;
    	} 
	else
	{
	std::cout << "insert in table successfull" << std::endl;
	}
	sqlite3_close(db); 	
	int buffSize1 = *((int*)eventBuffer);
	UINT8* testData1D = eventBuffer + sizeof(int);
	//Put that size to the output buffer named event buffer
	*((int*) eventBuffer) = size;
	//If buffer size provided by UI side API is not equals to the encoded image size or lesser than encoded image size then return insuficient memory message as  DSS_INSUFICIENT_MEMORY.
	if ( buffSize1 == size || buffSize1 < size)
	{
		printf("ProbeMaster SearchEvent: Insufficient memory");

		return DSS_INSUFICIENT_MEMORY;
		
	}
	if(flag == 1)
	{
		*((char *) (eventBuffer + sizeof(int))) = 1;
	} 
	else {
		*((char *) (eventBuffer + sizeof(int))) = 0;
	}
	//After writing flag value then write image data to the output buffer(eventbuffer)     
	memcpy(eventBuffer + sizeof(int) + sizeof(char), &(jpegData[0]), size);
	//If flag1 is 1 means face recognized.Then write 1(in 1byte) into the output buffer.If not recognized write 0 to the output buffer. 
        if (flag1 == 1) 
	{
		*((char*) (eventBuffer + sizeof(int) + sizeof(char) + size)) = 1; 
	}
	else 
	{		
		*((char*) (eventBuffer + sizeof(int) + sizeof(char) + size)) = 0; 
	}
	//Put Recognized person's id size(1byte) in the output buffer after 1byte flag 
	*((short*) (eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char))) = (short)s_empid.size() ;
	//Copy Recognized Person's id to the eventbuffer
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) +  sizeof(short)), s_empid.c_str());        	
	//Put Recognized Person's name size to the eventbuffer.Size 1byte
	/** name marker **/
	*((short*) (eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) +  t_empid )) =  (short)s_firstname.size() ; 
	//Copy Recognized  person's name to the output buffer
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_empid + sizeof(short)),  s_firstname.c_str());          
	//Putting Size of Date of birth of the recognized person to the event buffer(long type)	
	/** dob marker **/
	*((long*)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_empid + sizeof(short) + t_firstname )) = (long)s_lastname.size() ;
	//  Copy Date of birth of Recognized person to the event buffer 
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_empid + sizeof(short) + t_firstname + sizeof(long)) , s_lastname.c_str());
	//Putting size of gender of the recognized person to the event buffer 1byte
       	*((short*) (eventBuffer + sizeof(int) + sizeof(char) + size +  sizeof(short) + t_empid + sizeof(short) + t_firstname + sizeof(long) + t_lastname)) = (short)s_status.size();
	//Copy gender of the recognized person to the event buffer
	strcpy((char *)(eventBuffer + sizeof(int) + sizeof(char) + size + sizeof(char) + sizeof(short) + t_empid + sizeof(short) + t_firstname + sizeof(long) + t_lastname + sizeof(short)) ,s_status.c_str());
	return DSS_SUCCESS;
}	

ErrorTypes Execute(UINT8 *dataSpace, UINT32 command, UINT8 *cmdInput)
{
	return DSS_SUCCESS;
}

//var1 = "DSS_FOLD/" , var2 = "TrainingData.dat"
void generate_database(std::string var1,std::string var2)
{
	frontal_face_detector detector = get_frontal_face_detector();
        shape_predictor sp;
        deserialize("shape_predictor_68_face_landmarks.dat") >> sp;
        anet_type net;
        deserialize("dlib_face_recognition_resnet_model_v1.dat") >> net;
        matrix<rgb_pixel> img1;

        ifstream myfile, myfile1;
	string line;
	std::vector <string> nvmToks;
	FILE *fpData = fopen(var2.c_str(), "w");
	std::string string1 = var1 + "PersonList.txt";
	myfile.open(string1);
	while(getline(myfile, line))
	{
		//std::cout << "dfbvbfsj" << endl;
		split(nvmToks, line, " ");
//getting id of the person
		string id = nvmToks[0];
//Getting folder name for a particular person
		string folderName = var1 + nvmToks[1];
		//printf("%s\n",folderName);
//Getting name of the person 
		string personName = nvmToks[2];
//Getting gender of the person
                string personGender = nvmToks[3];
//Getting Date of birth of the person
                string personDob = nvmToks[4];
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
	    //cout << "N-Faces " << faces1.size() << "  " << id<<"  "<<personName <<"  "<< personGender<<"  "<<personDob<<endl;
           
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
