package com.dss.master.event;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.VmsResponse;

public class TestEventInsert {

	private static DatabaseManager dbManager = DatabaseManagerImpl.getInstance();

	@Test
	public void test() {
		FaceRecognitionEvent event = new FaceRecognitionEvent(System.currentTimeMillis(), true);
		String url = "events/testFile.jpg";
		event.setEmployee(new Employee());

		VmsResponse response = dbManager.generateEventLog(event, url);
		System.out.println(response);
		assertNotNull(response);
		assertTrue(response.isSuccess());
	}

}
