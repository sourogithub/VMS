import java.io.IOException;
import java.net.UnknownHostException;

import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
/**
 * 
 * @author dss-02
 *
 */
public class Main {
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		VmsResponse response = sessionManager.wakeup();
		if (response.isSuccess()) {
			/** streaming server **/
//			RemoteStreamingServer streamingServer = new RemoteStreamingServer();
//			((SessionManagerImpl) sessionManager).addObserver(streamingServer);
			/** adding runtime hook to gracefully shutdown upon termination**/
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					sessionManager.sleep();
				}
			}));
		} else {
			System.err.println("Failed to initialise session manager , response - " + response);
		}
			
	}

}
