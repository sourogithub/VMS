package com.dss.master.record;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.VmsResponse;

public class TestGetRecords {
	private static final int TEST_CAMERA_ID = 0;
	private static final long TEST_START_TIME = 0;
	private static final long TEST_END_TIME = System.currentTimeMillis();
	
	private static DatabaseManager dbManager;

	@Before
	public void init() { dbManager = DatabaseManagerImpl.getInstance(); }
	
	@Test
	public void testGetRecords() {
		assertNotNull(dbManager);
		try {
			VmsResponse response = dbManager.getRecords(TEST_CAMERA_ID, TEST_START_TIME, TEST_END_TIME);
			assertNotNull(response);
		} catch (Exception e) {
			fail("Error occured while getting records from database.. Error- " + e);
		}
	}
	
	

}
