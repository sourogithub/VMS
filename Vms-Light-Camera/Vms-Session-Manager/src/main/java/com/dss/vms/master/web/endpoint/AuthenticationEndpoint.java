package com.dss.vms.master.web.endpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.security.Credential.MD5;

import com.dss.vms.common.response.Authorization;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.web.util.CrossOriginBuilder;
import com.dss.vms.web.util.JSONBuilder;
/**
 * 
 * @author dss-02
 *
 */
public class AuthenticationEndpoint extends HttpServlet {
	private static final long serialVersionUID = -6624401540076097405L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp = CrossOriginBuilder.addCORSHeaders(resp);
		String user = req.getParameter("user");
		String pass = req.getParameter("pass");
		
		if (user != null && pass != null) {
			VmsResponse response = authenticate(user, pass);
			if(response.isSuccess()) {
				Authorization auth = (Authorization) response.getResponse();
				resp.getWriter().println(JSONBuilder.getJsonString(auth));
			} else {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		resp.getWriter().close();
	}
	
	/**
	 * Authenticate the User
	 * @param user
	 * @param pass
	 * @return
	 */
	private VmsResponse authenticate(String user, String pass) {
		return SessionManagerImpl.getInstance().authenticate(user, pass);
	}

}
