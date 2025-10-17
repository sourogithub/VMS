package com.dss.streaming;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.socket.AbstractServer;

public class StreamReceiverServer extends AbstractServer implements Observer {

	static {
		setLogger(LoggerFactory.getLogger(StreamReceiverServer.class));
	}

	public StreamReceiverServer() {
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			if (bindServer(VmsCommonPorts.REMOTE_STREAM_RECV_PORT)) {
				while (true) {
					try {
						Socket clientSocket = acceptIncommingConnections();
						clientSocket.setKeepAlive(true);
						clientSocket.setTcpNoDelay(true);
						clientSocket.setSoTimeout(20000);
						info("Connection established to client " + clientSocket.getRemoteSocketAddress());
						//new ClientHandler(clientSocket);
					} catch (Throwable e) {
						error("Failed to accept new connection on " + VmsCommonPorts.REMOTE_STREAM_RECV_PORT + ", Error - " + e);
						break;
					}
				}

				closeConnection();
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void update(Observable sender, Object data) {
		info("#Dataflow# : Dataframe received in " + this.getClass() + " notifying observers ");
		notify(data);
	}

	/**
	 * 
	 * @author dss-02
	 *
	 */
//	private class ClientHandler extends Thread {
//		private ObjectInputStream ois;
//
//		public ClientHandler(Socket clientSocket) throws IOException {
//			
//			this.ois = new ObjectInputStream(inputStream);
//
//			new Thread(this).start();
//		}
//
//		public void run() {
//			while (true) {
//				try {
//					MediaFrame frame = (MediaFrame) ois.readObject();
//					info("Data Frame received " + frame);
//					StreamReceiverServer.this.notify(frame);
//				} catch (Throwable e) {
//					e.printStackTrace();
//					error("Failed to receive data  , Error occured - " + e + " , Closing connection...");
//					break;
//				}
//			}
//
//			closeClientConnection();
//		}
//
//		@Override
//		protected void closeClientConnection() {
//			super.closeClientConnection();
//			try {
//				ois.close();
//			} catch (Exception e) {
//			}
//		}
//
//	}

}
