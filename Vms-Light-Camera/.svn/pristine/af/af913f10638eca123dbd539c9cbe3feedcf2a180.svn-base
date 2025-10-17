package com.dss.master.record;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.VmsResponse;

public class TestGetOldestRecord {

	private static DatabaseManager dbManager;
	
	@Before
	public void init() { dbManager = DatabaseManagerImpl.getInstance(); }
	
	@Test
	public void testGetBatchRecords() {
		
		assertNotNull(dbManager);
		
		try {
			VmsResponse response = dbManager.fetchOutdatedRecords();
			assertNotNull(response);
			
			if (response.isSuccess()) {
				Serializable data = response.getResponse();
				
				assert data instanceof ArrayList;
				
				if (data instanceof ArrayList) {
					ArrayList<RecordSession> recordList = (ArrayList<RecordSession>) data;
					
					for (RecordSession record : recordList) {
						Date startDate = new Date(record.getStartTime());
						Date endDate = new Date(record.getEndTime());

						assertNotNull(record);
						assertNotNull(startDate);
						assertNotNull(endDate);
					}
				}
			}
			
		} catch (Exception e) {
			fail("Error occured while getting records from database.. Error- " + e);
		}
	}
}
