package com.dss.vms.master.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;

public class AuthenticationTest {

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
		String username = "admin";
		String password = "admin";
		HttpClient client = new HttpClient();
		
		try {
			client.start();
			ContentResponse response = client.GET("http://localhost:" + VmsCommonPorts.REST_HTTP_PORT + "/authenticate?user="
			+ username + "&pass=" + password);
			assertNotNull(response);
			byte[] content = response.getContent();
			assertNotNull(content);
			System.out.println(new String(content));
		} catch (Exception e) {
			fail("Failed to GET /authenticate of [username = " + username + ", password =  " + password + "] , Error - " + e);
		} finally {
			try { client.stop(); } catch (Exception e) {}
		}
	}

}
