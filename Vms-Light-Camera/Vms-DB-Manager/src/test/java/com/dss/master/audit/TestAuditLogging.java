package com.dss.master.audit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.dss.vms.common.constants.AuditLog;
import com.dss.vms.common.response.VmsResponse;


public class TestAuditLogging {
	
	private AuditManager auditManager;
	
	@Before
	public void init() { auditManager = new SqliteAuditManagerImpl(); }
	
	@Test
	public void testLogGeneration() {
		AuditLog log = AuditLog.SERVER_SLEEP;
		long timestamp = System.currentTimeMillis();
		VmsResponse response = auditManager.generateAuditLog(log, log.getMessage(), timestamp);
		assertTrue(response.isSuccess());
		
		//check if database exists
		File database = new File("db/" + AuditSchema.AUDIT_DB_NAME);
		if(!database.exists()) fail(" database not created..");
	}

}
