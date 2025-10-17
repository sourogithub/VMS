package com.dss.master.event;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author sibendu
 */
public interface EventManager {

	public VmsResponse createEventLog(AnalyticType type, Long time, String URL);

	public VmsResponse getEventLog(Integer limit);

	public VmsResponse getLogBetween(Long startTime, Long endTime);

}
