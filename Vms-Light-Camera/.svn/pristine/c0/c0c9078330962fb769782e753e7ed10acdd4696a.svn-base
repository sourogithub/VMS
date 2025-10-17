import com.dss.streaming.RemoteStreamingServer;
import com.dss.streaming.StreamReceiverServer;

public class Main {
	
	
	public static void main(String[] args) {
		StreamReceiverServer receiverServer = new StreamReceiverServer();
		RemoteStreamingServer streamingServer = new RemoteStreamingServer();
		receiverServer.addObserver(streamingServer);
	}
}
