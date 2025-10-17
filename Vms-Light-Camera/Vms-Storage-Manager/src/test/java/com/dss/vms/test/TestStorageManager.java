package com.dss.vms.test;

import org.junit.Test;

import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.StorageManager;

import junit.framework.TestCase;

public class TestStorageManager extends TestCase {
	
	private static StorageManager storageManager = StorageManager.getInstance();
	
	@Test
	public void testGetDriveUsableSpace() {
		Long usableSpace = StorageManager.getDriveUsableSpace();
		assertNotNull(usableSpace);
		assert(usableSpace > 0);
	}

	@Test
	public void testInitialise() {
		VmsResponse response = storageManager.initialise();
		assertNotNull(response);
		assertTrue(response.isSuccess());
	}

	@Test
	public void testShutdown() {
		VmsResponse response = storageManager.shutdown();
		assertNotNull(response);
		assertTrue(response.isSuccess());
	}
	
	@Test
	public void testMultipleTimes() {
		for(int i = 0; i < 10; i++) {
			testInitialise();
			testShutdown();
		}
	}

}
