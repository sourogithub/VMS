package com.dss.vms.analytics.data;

import java.io.Serializable;

import com.dss.vms.common.constants.AnalyticType;

/**
 * 
 * @author dss-04
 *
 */
public class GenericEvent implements Serializable {
	private long timestamp;
	private AnalyticType type;
	private int channelID;

	/* for additional informations */
	private Serializable data;

	public GenericEvent(long timestamp, AnalyticType type) {
		this.timestamp = timestamp;
		this.type = type;
		this.channelID = -1;
	}

	public GenericEvent(long timestamp, AnalyticType type, int channelID) {
		this.timestamp = timestamp;
		this.type = type;
		this.channelID = channelID;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public AnalyticType getType() {
		return type;
	}

	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public int getChannelID() {
		return channelID;
	}

}