package com.dss.vms.video.streamer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.video.data.MediaFrame;
/**
 * 
 * @author dss-02
 *
 */
public class LiveStreamingClient extends Thread implements Observer {
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveStreamingClient.class);

	private InetAddress serverAddr;
	private int serverPort;
	private Socket streamingSocket;
	private boolean connected = false;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	/**
	 * 
	 * @param serverAddr
	 * @param serverPort
	 */
	public LiveStreamingClient(InetAddress serverAddr, int serverPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
		this.setName("Live Streaming client TCP Data-channel");
		this.start();
	}

	@Override
	public void update(Observable sender, Object data) {
		if (data instanceof MediaFrame) {
			connected = sendFrame((MediaFrame) data);
		}
	}

	@Override
	public void run() {
		while (true) {
			if (!connected) {
				connected = openConnection();
			}
			if(!connected) {
				LOGGER.debug("Failed to open connection to server " + serverAddr + ", retrying connection after 300ms");
				try {sleep(300);} catch (Exception e) {}
				continue;
			} else {
				try {
					System.err.println("Obtaining I/O channel from socket...");
					out = new ObjectOutputStream(streamingSocket.getOutputStream());
//					in = new ObjectInputStream(streamingSocket.getInputStream());
					System.err.println("Successfully obtained I/O channel from socket...");
					while(connected) { 
						/** non blocking call **/
						try{
							sleep(100);
						} catch (InterruptedException e) {
							System.err.println("Thread Interrupted due to connection loss....");
							closeConnection();
							break;
						} 
					} 
				} catch (Throwable e) {
					LOGGER.error("Failed to open streams from socket , Error - " + e);
					closeConnection();
					continue;
				}
			}
		}
	}

	private void closeConnection() {
		try {
			this.connected = false;
			out.close();
			in.close();
			streamingSocket.close();
		} catch (Exception e) {}
	}

	private boolean openConnection() {
		try {
			LOGGER.info("Trying to openconnection to server " + serverAddr + " on port " + serverPort);
			streamingSocket = new Socket(serverAddr, serverPort);
			streamingSocket.setReuseAddress(true);
			streamingSocket.setKeepAlive(true);
			streamingSocket.setTcpNoDelay(true);
			streamingSocket.setSoTimeout(20000);
			LOGGER.info("Successfully opened connection to server : " + serverAddr + ", on port : " + serverPort);
			return true;
		} catch (IOException e) {LOGGER.error("Failed to open connection...Reason : " + e);}
		return false;
	}
	
	/**
	 * Send Media frame to remote streamer
	 * @param frame
	 * @return
	 */
	Lock lock = new ReentrantLock(true);
	private boolean sendFrame(MediaFrame frame) {
		try {
			lock.lock();
			LOGGER.debug("Sending frame channelid " + frame.getChannelID());
			out.writeObject(frame);
			out.flush();
			lock.unlock();
			return true;
		} catch (Exception e) {
			LOGGER.info("Failed to write media-frame , client not connected to server , Error - " + e);
			this.interrupt();
		}
		return false;
	}
}
