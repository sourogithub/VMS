package com.dss.vms.master.web;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
/**
 * 
 * @author dss-02
 *
 */
public class GetRecordTest {

	private static SessionManager sessionManager = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionManager = SessionManagerImpl.getInstance();
		assertNotNull(sessionManager);
		VmsResponse wakeup = sessionManager.wakeup();
		assertNotNull(wakeup);
		assertTrue(wakeup.isSuccess());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		VmsResponse sleep = sessionManager.sleep();
		assertNotNull(sleep);
		assertTrue(sleep.isSuccess());
	}
	
	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		long startTime = 0;
		long endTime = System.currentTimeMillis();
		int camid = 0;
		HttpClient client = new HttpClient();
		try {
			client.start();
			ContentResponse response = client.GET("http://localhost:" + VmsCommonPorts.REST_HTTP_PORT + "/getRecord"
					+ "?cam_id=" + camid + "&start=" + startTime + "&end=" + endTime);
			assertNotNull(response);
			byte[] content = response.getContent();
			assertNotNull(content);
			System.out.println(new String(content));
		} catch (Exception e) {
			fail("Failed to GET /records of [camid = " + camid + " start Time = " +startTime
					+ " endtime=" + endTime + "] , Error - " + e);
		} finally {
			try { client.stop(); } catch (Exception e) {}
		}
	}

}
