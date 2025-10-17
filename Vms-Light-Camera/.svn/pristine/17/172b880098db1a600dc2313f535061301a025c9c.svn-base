package com.dss.vms.master.web.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.interfaces.rest.HttpMediaTypes;
import com.dss.vms.web.util.CrossOriginBuilder;

@WebServlet(urlPatterns = "/fileDownload")
public class FileDownloadEndpoint extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadEndpoint.class);
	
	public static int BUFFER_SIZE = 1024 * 100;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response = CrossOriginBuilder.addCORSHeaders(response);
		this.sendFile(request, response);
	}

	/**
	 * send the recorded file through servlet
	 * @param request
	 * @param response
	 */
	public void sendFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			String filePath = request.getParameter("fileName");
			System.err.println("File Path = " + filePath);
			System.err.println("File Exists = " + new File(filePath).exists());
			
			File file = new File(filePath);
			OutputStream outStream = null;
			FileInputStream inputStream = null;
			if (file.exists()) {
				response.setContentType(HttpMediaTypes.APPLICATION_OCTET_STREAM);
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
				response.setHeader(headerKey, headerValue);
				try {
					outStream = response.getOutputStream();
					inputStream = new FileInputStream(file);
					byte[] buffer = new byte[BUFFER_SIZE];
					int bytesRead = -1;

					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
						outStream.flush();
					}
				} catch (IOException ioe) {
					LOGGER.error("Failed to send record file ... Error - " + ioe);
				} finally {
					try {
						if (inputStream != null) inputStream.close();
					} catch (IOException e) {}

					try {
						if (outStream != null) outStream.close();
					} catch (Exception e) {}
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Throwable e) {
			LOGGER.error("Error occured while sending record data , Error - " + e);
			try {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} catch (IOException e1) {}
		}
	}
}
