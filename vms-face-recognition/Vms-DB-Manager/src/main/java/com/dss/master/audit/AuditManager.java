package com.dss.master.audit;

import com.dss.vms.common.constants.AuditType;
import com.dss.vms.common.response.VmsResponse;

/**
 * 
 * @author sibendu
 *
 */
public interface AuditManager {

	public VmsResponse generateAuditLog(AuditType type, String description, Long time);

	public VmsResponse getAuditLog(Integer limit);

	public VmsResponse getLogBetween(Long startTime, Long endTime);

	public VmsResponse getAuditLogByType(AuditType type, Integer limit);
}
