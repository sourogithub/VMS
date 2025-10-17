package com.dss.vms.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller client for sending controll messages
 * 
 * @author dss-02
 *
 */
public class ControllerClient extends Observable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerClient.class);

	private Socket clientSocket;
	private InetAddress destIpAddr;
	private Integer destPort;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private boolean stopRequested;
	
	/**
	 * 
	 * @param sessionMgrAddr
	 * @param port
	 * @throws IOException
	 */
	public ControllerClient(InetAddress sessionMgrAddr, int port) throws IOException {
		this.destIpAddr = sessionMgrAddr;
		this.destPort = port;
		this.stopRequested = false;
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			if (!createConnection()) {
				LOGGER.debug("Retrying connection after 3Seconds ");
				try {
					Thread.sleep(3000);
				} catch (Exception e) {}
			}

			try {
				outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				inputStream = new ObjectInputStream(clientSocket.getInputStream());

				while (true) {
					try {
						// inputStream.readObject();
						/** notify the observers **/
						//notify(signal);
						
						synchronized (this) {
							if(stopRequested) break;
						}
						
					} catch (Exception e) {
						LOGGER.error("Failed to receive control signal from " + destIpAddr + ", Error  - " + e.getMessage());
						break;
					}
				}

				closeConnection();
			} catch (Exception e) {
				LOGGER.error("Failed to receive session-manager call, reason : " + e);
			}
			
			synchronized (this) {
				if(stopRequested) break;
			}
		}
	}
	
	public synchronized void teardownChannel() {
		this.stopRequested = true;
	}

	private void closeConnection() {
		try {
			outputStream.close();
			inputStream.close();
			clientSocket.close();
		} catch (Exception e) {}
	}

	private boolean createConnection() {
		try {
			this.clientSocket = new Socket(destIpAddr, destPort);
		} catch (Exception e) {
			LOGGER.error("Failed to connect to session-manager " + destIpAddr + ", Port: " + destPort);
			return false;
		}
		return true;
	}
	
	private void notify(Serializable signal) {
		this.setChanged();
		this.notifyObservers(signal);
	}
}
