package com.dss.master.audit;

import com.dss.vms.common.constants.AuditLog;
import com.dss.vms.common.response.VmsResponse;

/**
 * 
 * @author sibendu
 *
 */
public interface AuditManager {

	public VmsResponse generateAuditLog(AuditLog type, String description, Long time);

	public VmsResponse getAuditLogs(Integer limit);

	public VmsResponse getLogBetween(Long startTime, Long endTime);

	public VmsResponse getAuditLogByType(AuditLog type, Integer limit);
}
