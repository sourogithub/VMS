package com.dss.vms.master.web.endpoint;

import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.interfaces.rest.HttpMediaTypes;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.master.web.VideoCameraLink;
import com.dss.vms.web.util.CrossOriginBuilder;
import com.dss.vms.web.util.JSONBuilder;

@WebServlet(urlPatterns = "/getCameras")
public class CameraListEndpoint extends HttpServlet {
	private static final long serialVersionUID = -5260784587906001311L;
	
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();
	private static DatabaseManager dbManager = DatabaseManagerImpl.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp = CrossOriginBuilder.addCORSHeaders(resp);
			resp.setContentType(HttpMediaTypes.APPLICATION_JSON);
			
			VmsResponse response = sessionManager.getAllCameras();
			if (response.isSuccess()) {
				resp.setStatus(HttpServletResponse.SC_OK);
				Map<Integer, VideoCamera> cameraMap =  (Map<Integer, VideoCamera>) response.getResponse();
				List<VideoCameraLink> cameraList = new ArrayList<>();
				
				for(VideoCamera camera : cameraMap.values()) {
					
					boolean linkStatus = false;
					boolean recordingStatus = false;

					try {
						String ipaddr = camera.getIp();
						ipaddr = ipaddr.contains("://") ? ipaddr.substring(ipaddr.indexOf("://") + "://".length()) : ipaddr;
						ipaddr = ipaddr.contains("/") ? ipaddr.substring(0, ipaddr.indexOf("/")) : ipaddr;
						ipaddr = ipaddr.contains(":") ? ipaddr.substring(0, ipaddr.indexOf(":")) : ipaddr;

						InetAddress inetAddress = InetAddress.getByName(ipaddr);
						linkStatus = inetAddress.isReachable(500);
					} catch (Exception e) {}

					try {
						long endTime = System.currentTimeMillis();
						//check records from last 15mins
						long startTime = endTime - 900000;
						
						VmsResponse mediaResponse = dbManager.getRecords(camera.getId(), startTime, endTime);
						if (mediaResponse.isSuccess()) {
							Serializable data = mediaResponse.getResponse();
							if (data != null && data instanceof ArrayList) {
								ArrayList<RecordSession> records = (ArrayList<RecordSession>) mediaResponse.getResponse();
								
								if (records.size() >= 0) { recordingStatus = true; }
							}
						}
					} catch (Exception e) {}
					
					cameraList.add(new VideoCameraLink(camera.getId(), camera.getName(),
							camera.getModel(), linkStatus, recordingStatus));
				}
				
				String json = JSONBuilder.getJsonString(cameraList);
				PrintWriter writer = resp.getWriter();
				writer.write(json);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			resp.getWriter().close();
		} catch (Throwable e) {
			e.printStackTrace();
			try {resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);} catch (Exception ex) {}
		}
	}

}
