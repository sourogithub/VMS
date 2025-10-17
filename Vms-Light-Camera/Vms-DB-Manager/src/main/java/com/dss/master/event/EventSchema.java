package com.dss.master.event;

public interface EventSchema {
	String EVENT_TABLE_NAME = "EVENT_LOG";
	String EVENT_DB_NAME = "VMS_EVENT_DB.db";
	String EVENT_DB_PATH = "jdbc:sqlite:db/VMS_EVENT_DB.db";

	// Db table schema
	String EVENT_PK = "KEY";
	String EVENT_TYPE = "EVENT_TYPE";
	String EVENT_TIME = "TIME";
	String EVENT_URL = "URL";

}
