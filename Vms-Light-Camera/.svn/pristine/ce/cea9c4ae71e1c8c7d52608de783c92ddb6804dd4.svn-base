package com.dss.streaming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.HttpHeaders;
import com.dss.vms.common.constants.VmsCommonPorts;

public class RecordStreamingServer extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordStreamingServer.class);
	
	private ServerSocket serverSocket;
	
	public RecordStreamingServer() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			if (bindServer(VmsCommonPorts.RECORD_STREAM_SERVER_PORT)) {
				LOGGER.info("Record Streaming Server Successfully bind to port " + VmsCommonPorts.RECORD_STREAM_SERVER_PORT);

				while (true) {
					try {
						Socket clientSocket = serverSocket.accept();
						LOGGER.info("Connection received from client - " + clientSocket);
						new ClientHandler(clientSocket);
					} catch (IOException e) {
						LOGGER.error("Failed to accept incomming connection, Error -  " + e);
						break;
					}
				}

				LOGGER.info("Closing connection of Record Streaming Server");
				closeConnection();
			} else {
				LOGGER.info("Restarting server after 3seconds...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private boolean bindServer(int serverPort) {
		try {
			LOGGER.info("Trying to bind Server Running on port " + serverPort);
			this.serverSocket = new ServerSocket(serverPort);
			this.serverSocket.setReuseAddress(true);
			LOGGER.info("Success to bind Streaming Server Running on port " + serverPort);
			return true;
		} catch (Throwable e) {
			LOGGER.error("Failed to bind Server Running on port " + serverPort + ", Error - " + e);
		}
		return false;
	}
	
	private void closeConnection() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			LOGGER.error("Failed to close server connection , Error - " + e);
		}
	}

	private class ClientHandler extends Thread implements HttpHeaders {
		private Socket streamingSocket;
		private DataInputStream controlStream = null;
		private DataOutputStream videoStream = null;
		private String fileUrl = "";

		public ClientHandler(Socket clientSocket) throws IOException {
			this.streamingSocket = clientSocket;
			this.videoStream = new DataOutputStream(streamingSocket.getOutputStream());
			this.controlStream = new DataInputStream(streamingSocket.getInputStream());
			this.start();
		}

		@Override
		public void run() {
			try {
				String httpRequest = controlStream.readLine();
				boolean parseSuccess = parseRequest(httpRequest);
				File videoFile = new File(fileUrl);
				boolean firstPacket = true;
				if (videoFile.exists() && parseSuccess) {
					try {
						FileInputStream fileInputstream = new FileInputStream(videoFile);
						while (fileInputstream.available() > 0) {
							/** TCP max packet size = 65536 , mostly ethernet MTU sizes = 1500 **/
							int readbyte = fileInputstream.available() > 65536 ? 65536 : fileInputstream.available();
							byte[] videoData = new byte[readbyte];
							fileInputstream.read(videoData);
							if(firstPacket) {
								videoStream.write(SUCCESS_HEADER.getBytes());
								for(String header : mp4StreamHeaders) {
									videoStream.write(header.getBytes());
								}
								firstPacket = false;
							}
							
							videoStream.write(videoData);
							videoStream.flush();
							try {Thread.sleep(50);} catch (InterruptedException e) {}
						}
						fileInputstream.close();
					} catch (IOException e) {
						LOGGER.error("Failed to write data, closing connection....");
					}
				} else {
					try {
						videoStream.write(SERVICE_UNAVAILABLE_HEADER.getBytes());
						videoStream.flush();
					} catch (Exception e1) {}
				}
				
				closeClientConnection();
			} catch (Throwable e) {
				LOGGER.error("Failed to parse HTTP Request from " + streamingSocket + " Error - " + e);
				try {videoStream.write(SERVICE_UNAVAILABLE_HEADER.getBytes());} catch (Exception e1) {}
			}

		}

		/** http://<ip>:8094/videostream?record=<fileurl>.mp4 **/
		private boolean parseRequest(Object requestData) {
			try {
				String request = (String) requestData;
				request = request.replace("%20", " ");
				String query = request.substring(request.indexOf("/videostream?") + "/videostream?".length());
				query = query.substring(0, query.indexOf("HTTP/1.1")).trim();
				String qparams[] = query.split("=");
				boolean isValidURL = false;
				switch (qparams[0]) {
				case "record": {
					String url = qparams[1];
					if (url.contains(".mp4") || url.contains(".MP4")) {
						this.fileUrl = url;
						isValidURL = true;
					}
				}
				break;
				}
				return isValidURL;
			} catch (Exception e) {
				LOGGER.error("failed to parse request , Error - " + e);
			}
			return false;
		}

		protected void closeClientConnection() {
			LOGGER.info("Closing connection " + streamingSocket);
			try {videoStream.close();} catch (Exception e) {}
			try{controlStream.close();} catch (Exception e) {}
			try {streamingSocket.close();} catch (Exception e) {}
			LOGGER.info("Connection for " + streamingSocket + " successfully closed...");
		}

	}
	
	public static void main(String[] args) {
		new RecordStreamingServer();
	}

}
