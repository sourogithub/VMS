package com.dss.streaming;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.HttpHeaders;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.VmsCommonPorts;
import com.dss.vms.video.JpegCodec;
import com.dss.vms.video.data.MediaFrame;

public class MultiChannelStreamingServer extends Thread implements Observer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiChannelStreamingServer.class);
	
	private static JpegCodec jpegCodec = new JpegCodec();
	
	private ServerSocket serverSocket;
	private Map<Integer, MediaFrame> imageBucket = new ConcurrentHashMap<Integer, MediaFrame>();
	private Map<Integer, Integer> channelCounter = new ConcurrentHashMap<Integer, Integer>();
	
	public MultiChannelStreamingServer() {
		this.start();
	}

	public void run() {
		while (true) {
			if (bindServer(56100)) {
				while (true) {
					try {
						Socket clientSocket = serverSocket.accept();
						new HttpClientHandler(clientSocket);
					} catch (Throwable e) {
						LOGGER.error("Failed to accept new connection on " + 56100 + ", error - " + e);
						break;
					}
				}

				LOGGER.info("Trying to shutdown server...");
				closeConnection();
			} else {
				try {Thread.sleep(3000);} catch (InterruptedException e) {}
			}
		}
	}
	
	private void closeConnection() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			LOGGER.error("Failed to close server connection , Error - " + e);
		}
	}
	
	private boolean bindServer(int serverPort) {
		try {
			LOGGER.info("Trying to bind Server Running on port " + serverPort);
			this.serverSocket = new ServerSocket(serverPort);
			this.serverSocket.setReuseAddress(true);
			LOGGER.info("Success to bind Streaming Server Running on port " + serverPort);
			return true;
		} catch (Throwable e) {
			LOGGER.error("Failed to bind Server Running on port " + serverPort + ", error - " + e);
		}
		return false;
	}

	public void update(Observable sender, Object data) {
		if (data instanceof MediaFrame) {
			MediaFrame frame = (MediaFrame) data;
			imageBucket.put(frame.getChannelID(), frame);
		}
	}

	public static void main(String[] args) {
		new MultiChannelStreamingServer();
	}
	
	/**
	 * 
	 * @author dss-02
	 *
	 */
	private final class HttpClientHandler extends Thread implements HttpHeaders {
		private static final long NO_FRAME_SENT = 0;
		private List<Integer> requestedChannels = new ArrayList<Integer>();
		private Dimension dimension = new Dimension(640, 480);
		private Integer bitrate = 2048;
		private int fps = 15;
		private Socket socket;
		private DataInputStream controlStream;
		private DataOutputStream mediaStream;
		private MediaType reqMediaType = MediaType.MJPEG;
		private long lastFrameTimestamp = NO_FRAME_SENT;

		/**
		 * Client Handler for HTTP Stream
		 * 
		 * @param clientSocket
		 * @throws IOException
		 */
		public HttpClientHandler(Socket socket) throws IOException {
			this.socket = socket;
			this.socket.setTcpNoDelay(true);
			this.socket.setSoTimeout(20000);
			this.controlStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			this.mediaStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			LOGGER.info("Received new connection : " + socket);
			this.start();
		}

		public void run() {
			boolean parseSuccess = false;
			try {
				String httpRequest = controlStream.readLine(); //new String(httpRequestBytes);
				parseSuccess = parseRequest(httpRequest);
			} catch (Exception e) {
				LOGGER.error("Failed to parse HTTP Request from " + socket + " LOGGER.error - " + e);
				try {mediaStream.write(SERVICE_UNAVAILABLE_HEADER.getBytes());} catch (Exception e1) {}
			}

			if (requestedChannels.size() > 0 && parseSuccess) {
				for (Integer channelNumber : requestedChannels) {
					increaseChannelUses(channelNumber);
				}
				
				/* Sending Header */
				boolean sendingHeaderSuccess = false;
				switch (this.reqMediaType) {
					case MJPEG: {
						try {
							mediaStream.write(SUCCESS_HEADER.getBytes());
	
							for (String header : mjpegStreamHeaders) {
								mediaStream.write(header.getBytes());
							}
							mediaStream.flush();
							sendingHeaderSuccess = true;
						} catch (Exception e) {
							LOGGER.error("LOGGER.error occured while sending headers..." + e);
						}
						break;
					}
	
					default: {
						try {
							mediaStream.write(SERVICE_UNAVAILABLE_HEADER.getBytes());
							mediaStream.flush();
						} catch (Exception e) {
							LOGGER.error("LOGGER.error occured while sending headers..." + e);
						}
						break;
					}
				}

				long fpsInterval = 1200/this.fps;
				if (sendingHeaderSuccess) {
					while (true) {
						List<BufferedImage> frames = new ArrayList<>(requestedChannels.size());
						byte[] bytesToWrite = null;
						int frameIndex = 0;
						for(int channel : requestedChannels) {
							try {
								MediaFrame frame = imageBucket.get(channel);
								if (frame != null) {
									BufferedImage bFrame = jpegCodec.decode(frame.getRawFrame());
									frames.add(frameIndex++, bFrame);
								} else frames.add(frameIndex++, jpegCodec.getNoVideoImage());
							} catch (Exception e) {
								frames.add(frameIndex++, jpegCodec.getNoVideoImage());
							}
						}
						
						try {
							/** join all the images based on the count use layout **/
							BufferedImage joinedImage = jpegCodec.joinImages(dimension, frames);
							ByteArrayOutputStream buffArrOutStream = new ByteArrayOutputStream();
							ImageIO.write(joinedImage, "jpg", buffArrOutStream);
							buffArrOutStream.flush();
							bytesToWrite = buffArrOutStream.toByteArray();
							buffArrOutStream.close();
							
							if (this.reqMediaType == MediaType.MJPEG) mediaStream.write(jpegBoundary.getBytes());
							mediaStream.write(bytesToWrite);
							mediaStream.flush();
							
							/** sleep invocation **/
							try {sleep(fpsInterval);} catch (InterruptedException ie) {}
						
						} catch (Throwable e) {
							LOGGER.error("Client connection for Remote Streaming has been closed from " + socket);
							break;
						}
					}
				}

				for (Integer channelNumber : requestedChannels) {
					decreaseChannelUses(channelNumber);
				}
			}
			/** finally close the connection **/
			closeClientConnection();
		}

		private void closeClientConnection() {
			LOGGER.info("Closing all external resource for connection : " + socket);
			try {controlStream.close();} catch (Exception e) {}
			try {mediaStream.close();} catch (Exception e) {}
			try {socket.close();} catch (Exception e) {}
			
		}

		private boolean parseRequest(Object requestData) {
			String httpRequest = (String) requestData;
			String queryParams = httpRequest
					.substring(httpRequest.indexOf("GET /videostream?") + "GET /videostream?".length());
			queryParams = queryParams.substring(0, queryParams.indexOf("HTTP")).trim();
			String[] parameters = queryParams.split("&");
			for (String parameterValue : parameters) {
				if (parameterValue.contains("=")) {
					String[] tokens = parameterValue.split("=");
					String qParam = tokens[0];
					String qvalue = tokens[1];
					switch (qParam) {
					case "camid": {
						String[] camIds = qvalue.split(",");
						for (String camId : camIds) {
							this.requestedChannels.add(Integer.parseInt(camId));
						}
						break;
					}

					case "res": {
						String[] qdimensions = qvalue.split("x");
						this.dimension = new Dimension(Integer.parseInt(qdimensions[0]),
								Integer.parseInt(qdimensions[1]));
						break;
					}

					case "bitrate": {
						this.bitrate = Integer.parseInt(qvalue);
						break;
					}
					
					case "fps": {
						this.fps = Integer.parseInt(qvalue);
						this.fps = this.fps > 20 ? 20 : this.fps;
						this.fps = this.fps < 3 ? 3 : this.fps;
						break;
					}

					case "media": {
						try {
							this.reqMediaType = MediaType.valueOf(qvalue);
						} catch (Exception e) {
							this.reqMediaType = MediaType.MJPEG;
						}
						break;
					}

					}
				}
			}
			return true;
		}

		/**
		 * 
		 * @param channelId
		 */
		public synchronized void decreaseChannelUses(Integer channelId) {
			int currentUses = 0;
			if (channelCounter.containsKey(channelId)) {
				currentUses = channelCounter.get(channelId) - 1;
			}
			channelCounter.put(channelId, currentUses);
		}

		/**
		 * 
		 * @param channelId
		 */
		public synchronized void increaseChannelUses(Integer channelId) {
			int currentUses = 0;
			if (channelCounter.containsKey(channelId)) {
				currentUses = channelCounter.get(channelId) + 1;
			}
			channelCounter.put(channelId, currentUses);
		}
	}
}
