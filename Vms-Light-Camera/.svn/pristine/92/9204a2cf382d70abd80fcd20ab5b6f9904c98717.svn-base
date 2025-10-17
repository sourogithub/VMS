package com.dss.vms.master.web;

import java.io.File;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.common.exceptation.ServerInitialisationException;
import com.dss.vms.common.exceptation.ServerShutdownException;
import com.dss.vms.common.interfaces.rest.WebServerConstants;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.master.web.endpoint.AuthenticationEndpoint;
import com.dss.vms.master.web.endpoint.CameraListEndpoint;
import com.dss.vms.master.web.endpoint.FileDownloadEndpoint;
import com.dss.vms.master.web.endpoint.RecordListEndpoint;

/**
 * VMS_WebServer Implementation
 * 
 * @author dss-02
 */
public class WebServer implements WebServerConstants {

	private Server server;
	private HandlerList hList;

	/**
	 * Setup Endpoints for the server
	 */
	private void setupEndpoints() {
		this.hList = new HandlerList();

		/** Setting up REST Servlets **/
		ServletContextHandler ctxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ctxHandler.addServlet(CameraListEndpoint.class, "/getCameras");
		ctxHandler.addServlet(RecordListEndpoint.class, "/getRecord");
		ctxHandler.addServlet(FileDownloadEndpoint.class, "/fileDownload");
		ctxHandler.addServlet(AuthenticationEndpoint.class, "/authenticate");

		this.hList.addHandler(ctxHandler);
	}

	/**
	 * Initialize web-server
	 * 
	 * @throws ServerInitialisationException
	 */
	private void initialize() {
			/** max 100 threads and min 10threads **/
			QueuedThreadPool threadPool = new QueuedThreadPool(100, 10);
			this.server = new Server(threadPool);

			/** https connector **/
			if (VmsCommonPorts.REST_HTTPS_ENABLE) {
				HttpConfiguration httpConfig = new HttpConfiguration();
				httpConfig.addCustomizer(new SecureRequestCustomizer());
				SslContextFactory sslCtxFactory = new SslContextFactory();

				File certificateFile = new File(SSL_CERT_PATH);
				sslCtxFactory.setKeyStorePath(certificateFile.getAbsolutePath());
				sslCtxFactory.setKeyStorePassword(SSL_CERT_KEYSTORE_PWD);
				sslCtxFactory.setKeyManagerPassword(SSL_CERT_KEY_MANAGER_PWD);

				ServerConnector sslConnector = new ServerConnector(server,
						new SslConnectionFactory(sslCtxFactory, "http/1.1"),
						new HttpConnectionFactory(httpConfig));

				sslConnector.setPort(VmsCommonPorts.REST_HTTPS_PORT);
				// sslConnector.setHost(SERV_HOST);
				this.server.addConnector(sslConnector);
			}

			/**
			 * Adding HTTP Connector
			 */
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(VmsCommonPorts.REST_HTTP_PORT);
			connector.setName(SERV_NAME);
			this.server.addConnector(connector);
			this.server.setHandler(hList);
		
	}

	/**
	 * Start the server
	 * @throws Exception 
	 */
	public void start() throws ServerInitialisationException {
		try {
			this.setupEndpoints();
			this.initialize();
			this.server.start();
		} catch (Throwable e) {
			throw new ServerInitialisationException(CommonResponseCode.ERROR, e.getMessage());
		}
	}

	/**
	 * shutdown server
	 * @throws Exception 
	 * @throws ServerInitialisationException 
	 * 
	 * @throws ServerShutdownException
	 */
	public void shutdown() throws ServerShutdownException {
		try {
			this.server.stop();
			this.server.join();
			this.server.destroy();
		} catch (Throwable e) {
			throw new ServerShutdownException(CommonResponseCode.ERROR, e.getMessage());
		}
	}
	
	private static WebServer INSTANCE = null;
	public static WebServer getInstance() {
		synchronized (WebServer.class) {
			if (INSTANCE == null) {
				INSTANCE = new WebServer();
			}
		}
		return INSTANCE;
	}
	private WebServer() {}
	
}
