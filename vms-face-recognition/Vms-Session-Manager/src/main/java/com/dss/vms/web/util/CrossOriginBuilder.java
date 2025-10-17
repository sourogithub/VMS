package com.dss.vms.web.util;

import javax.servlet.http.HttpServletResponse;

public class CrossOriginBuilder {
	
	public static HttpServletResponse addCORSHeaders(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Methods", "GET");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
		return resp;
	}
	
	
}
