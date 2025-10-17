package com.dss.vms.watchdog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
/**
 * 
 * @author dss-02
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class WatchdogService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogService.class);
	private static final String PROPERTIES_FILE = "generic.properties";
	private static final int DEFAULT_LOCK_PORT = 51415;
	
	private static Properties properties = new Properties();
	private static int port = DEFAULT_LOCK_PORT;
	
	private static String applicationHome = "/usr/bin/vms";
	private static String executeCommand = "/usr/bin/java -Djava.library.path=lib -jar "
			+ "vms-desktop-client-0.0.1-RELEASE.jar";
	private static String applicationType = "AIO";
	
	static {
		try {
			LOGGER.info("Loading configurations...");
			properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
			port = Integer.parseInt(properties.getProperty("Watchdog.Port"));
			applicationType = properties.getProperty("ApplicationType");
			applicationHome = properties.getProperty("Application." + applicationType + ".Home");
			executeCommand = properties.getProperty("Application." + applicationType + ".Exec");
			LOGGER.info("Configurations loaded successfully..");
		} catch (Throwable e) {
			LOGGER.error("Failed to load port configuration from file : " + PROPERTIES_FILE 
					+ ", using default configuration");
		}
	}

	public void start() {
		ServerSocket lockingSocket = null;
		
		try {
			LOGGER.info("Trying to obtain lock for daemon process..");
			lockingSocket = new ServerSocket(port);
			LOGGER.info("Lock acquired successfully..");
			
			while (true) {
				try {
					String commandArguments[] = executeCommand.split(" ");
					ProcessBuilder processBuilder = new ProcessBuilder(commandArguments)
							.directory(new File(applicationHome))
							.inheritIO();
					Process process = processBuilder.start();
					LOGGER.info("Process successfully forked..");
					int status = process.waitFor();
					LOGGER.info("Process stopped unintentionally with exit code = " + status + ", restarting process after 3 Seconds");
				} catch (Throwable e) {
					LOGGER.error("Error occured while forking process, Error - " + e);
				}
			}
			
		} catch (IOException e) {
			LOGGER.error("Failed to lock on current application instance, "
							+ "as another instance is already running...");
		} finally {
			try { lockingSocket.close(); } catch (Exception e) {}
		}
	}
	
	public static void main(String[] args) {
		WatchdogService watchdogService = new WatchdogService();
		watchdogService.start();
	}
}
