
import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.master.web.WebServer;

public class TestCameraEndpoint {

	private static SessionManager sessionManager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionManager = SessionManagerImpl.getInstance();
		VmsResponse response = sessionManager.wakeup();
		
		assertNotNull(response);
		assertTrue(response.isSuccess());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		assertNotNull(sessionManager);
		
		VmsResponse response = sessionManager.sleep();

		assertNotNull(response);
		assertTrue(response.isSuccess());
	}

	@Test
	public void testGetCameras() {
		
		HttpClient client = new HttpClient();

		try {
			ContentResponse response = client.GET("http://localhost:1206/getCameras");
			System.out.println(new String(response.getContent()));
		
		} catch (Throwable e) {
			fail(e.getMessage());
		}
	}

}
