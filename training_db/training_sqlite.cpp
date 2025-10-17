#include "ProbeMaster1.h"
#include "training.h"
#include <ctime>
Training::Training()
{
	std::cout << "Training has started" << std::endl; 
}
Training::~Training()
{
	std::cout << "Training has completed" << std::endl;
}
ErrorTypes Training::generate_database()
{
	
	sqlite3 *db;
   	const char* zErrMsg;
   	int rc;
	//ifstream myfile1;
	sqlite3_stmt* t_statement;
	std::vector<std::string> results;
	matrix<rgb_pixel> img1;
	//cv::Mat img1;
	std::string id,empid,firstname,lastname,status;
	frontal_face_detector detector = get_frontal_face_detector();
    	shape_predictor sp;
    	deserialize("shape_predictor_68_face_landmarks.dat") >> sp;
    	anet_type net;
    	deserialize("dlib_face_recognition_resnet_model_v1.dat") >> net;
	FILE *fpData = fopen("TrainingData1.dat", "w");
   	rc = sqlite3_open("./VMS_0808/Vms-Session-Manager/db/VMS_EMP_DB.db", &db);
   	if( rc ) {
      		fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
		return DSS_ERROR;
   	} 
	else {
      		fprintf(stderr, "Opened database successfully\n");
   	}	
	const char* sql="SELECT faceidpath FROM EMPLOYEEDETAILS;";
	
	//size_t t_len = strlen(sql);
        rc = sqlite3_prepare_v2(db, sql, -1, &t_statement, &zErrMsg);
	while ( rc == SQLITE_OK && sqlite3_step( t_statement ) == SQLITE_ROW )
 	{
		fprintf(stderr, "Opened table successfully\n");
		std::string extract = std::string( reinterpret_cast< const char * >( sqlite3_column_text( t_statement, 0 )));
		cout << extract << endl;
		results.push_back(extract);
	}
	sqlite3_finalize(t_statement);
	for(int a = 0; a < results.size(); a++)
	{//5
		cout << results.size() << endl;
		fprintf(stderr, "fetch successfull\n");
		sqlite3_stmt* t_statement1;
		std::string sql1="SELECT * FROM EMPLOYEEDETAILS WHERE faceidpath = '" + results[a] + "';";
		const char* sql2=reinterpret_cast< const char * >(sql1.c_str());
		cout << sql2 << endl;
		rc = sqlite3_prepare_v2(db, sql2, -1, &t_statement1, &zErrMsg);
		while ( rc == SQLITE_OK && sqlite3_step( t_statement1 ) == SQLITE_ROW )
 		{
		id = std::string( reinterpret_cast< const char * >( sqlite3_column_text( t_statement1, 0 )));
		empid = std::string( reinterpret_cast< const char * >( sqlite3_column_text( t_statement1, 1 )));
		firstname = std::string( reinterpret_cast< const char * >( sqlite3_column_text( t_statement1, 2 )));	
		lastname = std::string(reinterpret_cast< const char * >( sqlite3_column_text( t_statement1, 3 )));
		status = std::string(reinterpret_cast< const char * >( sqlite3_column_text( t_statement1, 5 )));
		}		
		sqlite3_finalize(t_statement1);	
		std::vector<matrix<rgb_pixel>> faces1;
		std::string imgListFile = results[a] + "/*.jpg";
		//cout << imgListFile << endl;
		/*
		myfile1.open(imgListFile.c_str());
		string imgFile1,line;
		while(getline(myfile1, line))
		{
			imgFile1 = line;
			string imgFile = results[a] + "/" + line;
			load_image(img1, imgFile.c_str());
		*/
		std::vector<std::string> fn;
		cv::glob(imgListFile, fn, false);
		//vector<Mat> images;
		size_t count = fn.size(); //number of png files in images folder
		for (size_t i=0; i<count; i++)
		{
    			//images.push_back(cv::imread(fn[i]));
			//img1 = cv::imread(fn[i]);
			load_image(img1,fn[i].c_str());
			for (auto face : detector(img1))
			{
				auto shape = sp(img1, face);
				matrix<rgb_pixel> face_chip1;
				extract_image_chip(img1, get_face_chip_details(shape,150,0.25), face_chip1);
				faces1.push_back(move(face_chip1));
			}
		}	
		//myfile1.close();
		if (faces1.size() == 0)
		{
			cout << "No faces found for " << empid << endl;
			return DSS_ERROR;
		}		
		std::vector<matrix<float,0,1>> face_descriptors1 = net(faces1);
		for (int index1 = 0; index1 < face_descriptors1.size(); index1++ )
	    	{
	    		std::vector<float> tmpArr(face_descriptors1[index1].begin(), face_descriptors1[index1].end());
	
	    		for ( int index2 = 0; index2 < tmpArr.size(); index2++ )
	    		{
	    			fprintf(fpData, "%f ", tmpArr[index2]);
				//cout << tmpArr[index2] << endl;
	    		}
	
    			fprintf(fpData, "%s %s %s %s %s\n", id.c_str(), empid.c_str(),firstname.c_str(),lastname.c_str(),status.c_str());
    		}
	}	
	fclose(fpData);
	sqlite3_close(db);
	return DSS_SUCCESS;			
}
