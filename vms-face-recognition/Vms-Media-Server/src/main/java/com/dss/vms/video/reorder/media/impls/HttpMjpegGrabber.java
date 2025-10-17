package com.dss.vms.video.reorder.media.impls;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.FrameType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.exceptation.InvalidSourceException;
import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.GrabberResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.video.data.MediaFrame;
import com.dss.vms.video.reorder.MediaServerConstant;
import com.dss.vms.video.reorder.media.AbstractStreamGrabber;

/**
 * @author Sibendu-PC
 */
public class HttpMjpegGrabber extends AbstractStreamGrabber implements MediaServerConstant {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpMjpegGrabber.class);
	private DataInputStream cameraInputStream = null;
	private byte[] networkDataBuffer = new byte[INITIAL_MJOGE_BUFFER_SIZE];
	private int networkbufferIndex = INITIAL_MJOGE_BUFFER_SIZE;
	private int networkbufferSize = 0;
	private long startTimeStamp;

	private boolean pause = false;
	private boolean alive = true;
	private boolean connected = false;
	private Thread runner = null;

	public HttpMjpegGrabber(String cameraModel, String url, String username, String password, StreamType streamType,
			int cameraID) throws VmsCommonException {
		super(cameraModel, url, username, password, streamType, cameraID);
	}

	private boolean connectAndClearBuffer() {
		networkDataBuffer = new byte[INITIAL_MJOGE_BUFFER_SIZE];
		networkbufferIndex = INITIAL_MJOGE_BUFFER_SIZE;
		networkbufferSize = 0;
		return openConnection();
	}

	private boolean openConnection() {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(this.url);
			connection = (HttpURLConnection) url.openConnection();
			Authenticator.setDefault(new HttpAuthenticator(username, password));
			connection.setDoInput(true);
			connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT_MS);
			connection.connect();
			cameraInputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			return true;
		} catch (Exception e) {
			LOGGER.error("Failed to open HTTP connection using URL [" + url + "] Username [" + username + "] Password ["
					+ password + "] Error - " + e.toString());
			return false;
		}
	}

	private class HttpAuthenticator extends Authenticator {
		private String username = "admin";
		private String password = "admin";

		public HttpAuthenticator(String username, String password) {
			this.username = username;
			this.password =  password;
		}

		// This method is called when a password-protected URL is accessed
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			// Get information about the request
			String promptString = getRequestingPrompt();
			String hostname = getRequestingHost();
			InetAddress ipaddr = getRequestingSite();
			int port = getRequestingPort();
			// Return the information
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}

	/**
	 * @return
	 */
	private byte[] searchJpegFrame() {
		while (true) {
			// initialize data spaces
			byte[] imgBuffer = new byte[300000];
			boolean jpegHeaderFound = false;
			byte lastByte = 0;
			int jpegBufferIndex = 0;
			startTimeStamp = System.currentTimeMillis();

			while (true) {
				try {
					networkbufferIndex++;
					byte presentByte;
					if (networkbufferIndex < networkbufferSize) {
						/* If next byte is already in buffer, then return next byte */
						presentByte = networkDataBuffer[networkbufferIndex];
					} else {
						presentByte = readNetwokBufer();
					}

					/* Start image copying if header FFD8 found. */
					if (jpegHeaderFound) {
						imgBuffer[jpegBufferIndex] = presentByte;
						jpegBufferIndex++;
					}

					if (lastByte == 0xFFFFFFFF) {
						if (presentByte == 0xFFFFFFD8) {
							/* If JPEG header found */
							jpegHeaderFound = true;
							imgBuffer[0] = 0xFFFFFFFF;
							imgBuffer[1] = 0xFFFFFFD8;
							jpegBufferIndex = 2;
						} else if (presentByte == 0xFFFFFFD9) {
							/* If JPEG trailer found */
							byte[] jpegImage = new byte[jpegBufferIndex];
							jpegHeaderFound = false;
							System.arraycopy(imgBuffer, 0, jpegImage, 0, jpegBufferIndex);
							jpegBufferIndex = 0;
							return jpegImage;
						}
					}
					lastByte = presentByte;
				} catch (ArrayIndexOutOfBoundsException ae) {
					LOGGER.info("Recalculating image buffer size as initial buffer not enough...Old size "
							+ imgBuffer.length);
					/* Handling image of size more than 64K */
					byte[] tempImageBuffer = new byte[imgBuffer.length << 1];
					System.arraycopy(imgBuffer, 0, tempImageBuffer, 0, imgBuffer.length);
					imgBuffer = tempImageBuffer;
//					return searchJpegFrame();
					break;
				} catch (Throwable e) {
					LOGGER.error("Failed to grab JPEG image from [" + url + "], Error " + e);
					return null;
				}
			}
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private byte readNetwokBufer() throws Throwable {
		while (true) {
//			LOGGER.info("Thread before available  Theadname = [" + runner.getName() + "]");

			networkbufferSize = cameraInputStream.available();

//			LOGGER.info("Thread after available  Theadname = [" + runner.getName() + "]" + " + available size ["
//					+ cameraInputStream.available() + "]");

			if (networkbufferSize > 65536) {
				networkbufferSize = 65536;
			}

//			LOGGER.info("network buffer size for Thread [" + runner.getName() + "]" 
//			+ " NetworkBufferSize [" +networkbufferSize + "]");

			if (networkbufferSize > 0) {
				networkDataBuffer = new byte[networkbufferSize];
				networkbufferSize = cameraInputStream.read(networkDataBuffer, 0, networkbufferSize);
				networkbufferIndex = 0;
				return networkDataBuffer[0];
			} else if (System.currentTimeMillis() - startTimeStamp < HTTP_CONNECTION_TIMEOUT_MS) {
				try {Thread.sleep(20);} catch (Exception e) {}
//			return readNetwokBufer();
				continue;
			} else {
				throw new InvalidSourceException(GrabberResponseCode.NOT_A_MJEPG_SOURCE, "Not a mjpeg source.....");
			}
		}
	}

	@Override
	public void run() {

		LOGGER.info("Started Stream Grabbing for : model [" + cameraModel + "], URL [" + url + "], Username ["
				+ username + "], Password [" + password + "]");
		
		while (alive) {

			if (!connected) {
				connected = connectAndClearBuffer();
			}

			if (!connected) {
				LOGGER.error("Reconnecting ... [" + url + "]after " + HTTP_VIDEO_TIMEOUT_MS + " MS.");
				try { Thread.sleep(HTTP_VIDEO_TIMEOUT_MS); } catch (InterruptedException e) {}
				continue;
			}

			synchronized (this) {
				if ((this.pause)) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						LOGGER.info("Unable to pause channel controller " + e.toString());
					}
				}
			}

			byte[] newJpegImage = searchJpegFrame();
			if (newJpegImage != null) {
//					LOGGER.info("Success to get stream content .. Length " + newJpegImage.length);
				MediaFrame mediaFrame = new MediaFrame(FrameType.I_FRAME, cameraID, MediaType.MJPEG, newJpegImage,
						System.currentTimeMillis(), streamType, 0, 0);
				notify(mediaFrame);
			} else {
				connected = false;
				LOGGER.error("Failed to get any JPEG image from [" + url + "]..Connection closed or reset..");
			}
		}

		LOGGER.info("Ended Stream Grabbing for : model [" + cameraModel + "], URL [" + url + "], Username [" + username
				+ "], Password [" + password + "]");
	}

	@Override
	public synchronized VmsResponse pauseGrabber() {
		if (!this.pause) {
			this.pause = true;
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public synchronized VmsResponse resumeGrabber() {
		if (this.pause) {
			this.pause = false;
			this.notify();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public synchronized VmsResponse stopGrabber() {
		this.alive = false;
		if (this.pause) {
			this.notify();
		}
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public VmsResponse startGrabber() {
		runner = new Thread(this);
//		runner.setPriority(Thread.MAX_PRIORITY);
//		runner.setDaemon(true);
		runner.setName("Camera URL [" + url + "]" + "camera Model [" + cameraModel + "]");
		runner.start();
		return new VmsResponse(CommonResponseCode.SUCCESS);
	}

	@Override
	public VmsResponse getMediaHeader() {
		return new VmsResponse(GrabberResponseCode.NO_HEADER_FRAME_REQUIRED);
	}


}