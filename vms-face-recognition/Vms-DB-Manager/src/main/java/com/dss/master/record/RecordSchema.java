package com.dss.master.record;

public interface RecordSchema {
	String RECORD_TABLE_NAME = "RECORD_LOG";
	String RECORD_DB_NAME = "VMS_DB.db";
	String RECORD_DB_PATH = "jdbc:sqlite:db/VMS_DB.db";

	// Db table schema
	String RECORD_PK = "KEY";
	String RECORD_CAM_ID = "CAM_ID";
	String RECORD_START_TIME = "START_TIME";
	String RECORD_END_TIME = "END_TIME";
	String RECORD_KEEP = "KEEP";
	String RECORD_URL = "URL";

}
