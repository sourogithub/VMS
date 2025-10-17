package com.dss.vms.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

import org.slf4j.Logger;

public abstract class AbstractServer extends Observable implements Runnable {
	protected static Logger LOGGER = null;
	protected ServerSocket serverSocket;

	protected boolean bindServer(int serverPort) {
		try {
			info("Trying to bind Server Running on port " + serverPort);
			this.serverSocket = new ServerSocket(serverPort);
			this.serverSocket.setReuseAddress(true);
			info("Success to bind Streaming Server Running on port " + serverPort);
			return true;
		} catch (Throwable e) {
			error("Failed to bind Server Running on port " + serverPort + ", Error - " + e);
		}
		return false;
	}
	
	protected Socket acceptIncommingConnections() throws IOException {
		Socket clientSocket = serverSocket.accept();
		return clientSocket;
	}
	
	protected final void closeConnection() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			error("Failed to close server connection , Error - " + e);
		}
	}
	
	public void notify(Object data) {
		this.setChanged();
		this.notifyObservers(data);
	}

	public static void setLogger(Logger logger) {
		LOGGER = logger;
	}
	
	public static final void error(String errmsg) {
		if(LOGGER != null) LOGGER.error(errmsg);
	}
	
	public static final void info(String infoMsg) {
		if(LOGGER != null) LOGGER.info(infoMsg);
	}
	
	public static final void debug(String msg) {
		if(LOGGER != null) LOGGER.debug(msg);
	}
}
