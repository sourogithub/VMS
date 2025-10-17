package com.dss.vms.master.web;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;

public class GetCamerasTest {

	private static SessionManager sessionManager = null;

	@BeforeClass
	public static void setUp() throws Exception {
		sessionManager = SessionManagerImpl.getInstance();
		assertNotNull(sessionManager);
		VmsResponse wakeup = sessionManager.wakeup();
		assertNotNull(wakeup);
		assertTrue(wakeup.isSuccess());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		VmsResponse sleep = sessionManager.sleep();
		assertNotNull(sleep);
		assertTrue(sleep.isSuccess());
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
	
		HttpClient client = new HttpClient();
		try {
			client.start();
			ContentResponse response = client.GET("http://localhost:" + VmsCommonPorts.REST_HTTP_PORT + "/getCameras");
			assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
			byte[] content = response.getContent();
			assertNotNull(content);
			System.out.println(new String(content));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error occured while sending /getCameras request , Error = " + e);
		} finally {
			try {client.stop();} catch (Exception e) {}
		}
	}

}
