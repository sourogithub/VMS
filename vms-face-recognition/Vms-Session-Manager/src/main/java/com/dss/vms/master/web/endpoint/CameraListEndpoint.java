package com.dss.vms.master.web.endpoint;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.interfaces.rest.HttpMediaTypes;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.master.web.CameraData;
import com.dss.vms.web.util.CrossOriginBuilder;
import com.dss.vms.web.util.JSONBuilder;

@WebServlet(urlPatterns = "/getCameras")
public class CameraListEndpoint extends HttpServlet {
	private static final long serialVersionUID = -5260784587906001311L;
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp = CrossOriginBuilder.addCORSHeaders(resp);
			resp.setContentType(HttpMediaTypes.APPLICATION_JSON);
			
			VmsResponse response = sessionManager.getAllCameras();
			if (response.isSuccess()) {
				resp.setStatus(HttpServletResponse.SC_OK);
				Map<Integer, VideoCamera> cameraMap =  (Map<Integer, VideoCamera>) response.getResponse();
				List<CameraData> cameraList = new ArrayList<>();
				
				for(VideoCamera camera : cameraMap.values()) {
					cameraList.add(new CameraData(camera.getId(), camera.getName(), camera.getModel()));
				}
				String json = JSONBuilder.getJsonString(cameraList);
				PrintWriter writer = resp.getWriter();
				writer.write(json);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			resp.getWriter().close();
		} catch (Throwable e) {
			try {resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);} catch (Exception ex) {}
		}
	}

}
