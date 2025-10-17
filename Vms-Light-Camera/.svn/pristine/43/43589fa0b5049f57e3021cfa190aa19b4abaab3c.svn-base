
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.dss.vms.common.exceptation.ServerInitialisationException;
import com.dss.vms.common.exceptation.ServerShutdownException;
import com.dss.vms.master.web.WebServer;

public class WebServerTest {
	
	private static WebServer server;

	@Test
	public void testStart() {
		try {
			server = WebServer.getInstance();
			
			assertNotNull(server);
		} catch (ServerInitialisationException e) {
			fail("failed to initialise server " + e.getMessage());
		}
		
	}

	@Test
	public void testShutdown() {
		assertNotNull(server);
		
		try {
			server.shutdown();
		} catch (ServerShutdownException e) {
			fail("failed to shutdown server " + e.getMessage());
		}
		
	}

}
