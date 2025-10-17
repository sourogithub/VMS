package com.dss.vms.common.constants;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public final class VmsCommonPorts {
	public static final String CONF_FILE = "server-config.properties";
	
	public static int REST_HTTP_PORT = 58304;
	
	public static boolean REST_HTTPS_ENABLE = false;
	
	public static int REST_HTTPS_PORT = 58306;
	
	public static int REMOTE_STREAM_HTTP_PORT = 56000;
	
	public static int RECORD_STREAM_SERVER_PORT = 8098;
	
	public static int REMOTE_STREAM_RECV_PORT = 8052;
	
	public static int CONTROL_SIGNALLING_PORT = 8090;
	
	static {
		try {
			System.out.println("loading configuration from [" + CONF_FILE + "]");
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File(CONF_FILE)));
			
			REST_HTTP_PORT = Integer.parseInt(properties.getProperty("dss.vms.web.port"));
			REST_HTTPS_ENABLE = Boolean.parseBoolean(properties.getProperty("dss.vms.web.ssl.enable"));
			REST_HTTPS_PORT = Integer.parseInt(properties.getProperty("dss.vms.web.ssl.port"));
			
			/** not used at this moment **/
			REMOTE_STREAM_HTTP_PORT = Integer.parseInt(properties.getProperty("dss.vms.stream.port"));
			
			RECORD_STREAM_SERVER_PORT = Integer.parseInt(properties.getProperty("dss.vms.stream.serv.port"));
			REMOTE_STREAM_RECV_PORT = Integer.parseInt(properties.getProperty("dss.vms.stream.receiver.port"));
			
			CONTROL_SIGNALLING_PORT = Integer.parseInt(properties.getProperty("dss.vms.mgmt.control.port"));
			
			System.out.println("Configurations loaded successfully.");
		} catch (Exception e) {
			System.err.println("Failed to load configuration.. using default configuration");
		}
	}
}
