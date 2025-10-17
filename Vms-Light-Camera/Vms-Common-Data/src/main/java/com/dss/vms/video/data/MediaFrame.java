package com.dss.vms.video.data;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;

import com.dss.vms.common.constants.FrameType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.StreamType;

/**
 * @author jdeveloper
 */
public class MediaFrame implements Serializable {
	/**
	 */
	private static final long serialVersionUID = 1L;

	public static final int NA = -1;
	
	private FrameType frameType;
	private int channelID;
	private MediaType mediaType;
	private byte[] rawFrame;
	private long timestamp;
	private StreamType streamType;
	private int bitrate = NA;
	private int fps = NA;
	private transient int[] rgbFrame;
	private transient int rgbWidth;
	private transient int rgbHeight;
	private transient boolean decoded = false;
	private transient BufferedImage bufferedImage = null;
	
	/**
	 * 
	 * @param frameType
	 * @param channelID
	 * @param mediaType
	 * @param rawFrame
	 * @param timestamp
	 * @param streamType
	 * @param bitrate
	 * @param fps
	 * @param rgbFrame
	 * @param rgbWidth
	 * @param rgbHeight
	 */
	public MediaFrame(FrameType frameType, int channelID, MediaType mediaType, byte[] rawFrame, long timestamp,
			StreamType streamType, int bitrate, int fps) {
		super();
		this.frameType = frameType;
		this.channelID = channelID;
		this.mediaType = mediaType;
		this.rawFrame = rawFrame;
		this.timestamp = timestamp;
		this.streamType = streamType;
		this.bitrate = bitrate;
		this.fps = fps;
	}
	

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	/**
	 * @return the frameType
	 */
	public FrameType getFrameType() {
		return frameType;
	}

	/**
	 * @param frameType the frameType to set
	 */
	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	/**
	 * @return the channelID
	 */
	public Integer getChannelID() {
		return channelID;
	}

	/**
	 * @param channelID the channelID to set
	 */
	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}

	/**
	 * @return the mediaType
	 */
	public MediaType getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return the rawFrame
	 */
	public byte[] getRawFrame() {
		return rawFrame;
	}

	/**
	 * @param rawFrame the rawFrame to set
	 */
	public void setRawFrame(byte[] rawFrame) {
		this.rawFrame = rawFrame;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the streamType
	 */
	public StreamType getStreamType() {
		return streamType;
	}

	/**
	 * @param streamType the streamType to set
	 */
	public void setStreamType(StreamType streamType) {
		this.streamType = streamType;
	}

	/**
	 * @return the bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate(Integer bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * @param fps the fps to set
	 */
	public void setFps(Integer fps) {
		this.fps = fps;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		byte[] newRawFrame = new byte[this.rawFrame.length];
		System.arraycopy(rawFrame, 0, newRawFrame, 0, rawFrame.length);
		return new MediaFrame(frameType, channelID, mediaType, newRawFrame, timestamp, streamType, bitrate, fps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Integer maxLen = 50;
		StringBuilder builder = new StringBuilder();
		builder.append("MediaFrame [");
		if (frameType != null) {
			builder.append("frameType=");
			builder.append(frameType);
			builder.append(", ");
		}
		builder.append("channelID=");
		builder.append(channelID);
		builder.append(", ");
		if (mediaType != null) {
			builder.append("mediaType=");
			builder.append(mediaType);
			builder.append(", ");
		}
		if (rawFrame != null) {
			builder.append("rawFrame=");
			builder.append(Arrays.toString(Arrays.copyOf(rawFrame, Math.min(rawFrame.length, maxLen))));
			builder.append(", ");
		}
		builder.append("timestamp=");
		builder.append(timestamp);
		builder.append(", ");
		if (streamType != null) {
			builder.append("streamType=");
			builder.append(streamType);
			builder.append(", ");
		}
		builder.append("bitrate=");
		builder.append(bitrate);
		builder.append(", fps=");
		builder.append(fps);
		builder.append("]");
		return builder.toString();
	}

	public int[] getRgbFrame() {
		return rgbFrame;
	}

	public void setRgbFrame(int[] rgbFrame) {
		this.rgbFrame = rgbFrame;
	}

	public void setRgbWidth(Integer rgbWidth) {
		this.rgbWidth = rgbWidth;
	}

	public void setRgbHeight(Integer rgbHeight) {
		this.rgbHeight = rgbHeight;
	}

	public int getRgbWidth() {
		return rgbWidth;
	}

	public int getRgbHeight() {
		return rgbHeight;
	}

	public boolean isDecoded() {
		return decoded;
	}

	public void setDecoded(boolean decoded) {
		this.decoded = decoded;
	}

	public void setRgbWidth(int rgbWidth) {
		this.rgbWidth = rgbWidth;
	}

	public void setRgbHeight(int rgbHeight) {
		this.rgbHeight = rgbHeight;
	}
}
