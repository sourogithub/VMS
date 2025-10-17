package com.dss.vms.master.web.endpoint;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.interfaces.rest.HttpMediaTypes;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.web.util.CrossOriginBuilder;
import com.dss.vms.web.util.JSONBuilder;

public class RecordListEndpoint extends HttpServlet {

	private static final long serialVersionUID = 2643306726798396441L;
	private static final Long DEFAULT_START_TIME = 0l;
	private SessionManager sessionManager = SessionManagerImpl.getInstance();
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp = CrossOriginBuilder.addCORSHeaders(resp);
			resp.setContentType(HttpMediaTypes.APPLICATION_JSON);

			Integer id = Integer.parseInt(req.getParameter("cam_id"));
			Long sTime = DEFAULT_START_TIME;
			Long eTime = System.currentTimeMillis();

			try {sTime = Long.parseLong(req.getParameter("start"));} catch (Exception e) {}
			try {eTime = Long.parseLong(req.getParameter("end"));} catch (Exception e) {}

			PrintWriter writer = resp.getWriter();
			VmsResponse recResp = sessionManager.fetchRecords(id, sTime, eTime);
			if (recResp.isSuccess()) {
				resp.setStatus(HttpServletResponse.SC_OK);
				String json = JSONBuilder.getJsonString(recResp.getResponse());
				writer.println(json);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			writer.close();
		} catch (Throwable e) {
			try {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().close();
			} catch (Exception e1) {}
		}
	}

}
