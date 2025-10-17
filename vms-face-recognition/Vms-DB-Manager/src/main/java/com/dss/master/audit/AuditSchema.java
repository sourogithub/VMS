package com.dss.master.audit;

public interface AuditSchema {
	String AUDIT_TABLE_NAME = "AUDIT_LOG";
	String AUDIT_DB_NAME = "VMS_AUDIT_DB.db"; 
	String AUDIT_DB_PATH = "jdbc:sqlite:db/VMS_AUDIT_DB.db";

	// Db table schema
	String AUDIT_PK = "KEY"; 
	String AUDIT_TYPE = "AUDIT_TYPE";
	String AUDIT_TIME = "TIME";
	String AUDIT_DESC = "DESCIPTION";
}
