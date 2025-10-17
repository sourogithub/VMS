package com.dss.master.event;

import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author sibendu
 */
public interface EventManager {

	public VmsResponse createIntruderEventLog(GenericEvent event, String URL);
	
	public VmsResponse createFREventLog(FaceRecognitionEvent event, String url);

	public VmsResponse getEventLog(Integer limit);

	public VmsResponse getLogBetween(Long startTime, Long endTime);

}
