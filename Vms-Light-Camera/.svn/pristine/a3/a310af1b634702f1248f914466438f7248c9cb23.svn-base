package com.dss.vms.web.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * 
 * @author dss-02
 *
 */
public class JSONBuilder {
	private static GsonBuilder builder;
	private static Gson gson;
	
	static {
		builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.serializeNulls();
		gson = builder.create();
	}
	
	/**
	 * get JSON String from Data
	 * @param data
	 * @return
	 */
	public static String getJsonString(Object data) {
		return gson.toJson(data);
	}
	
}
