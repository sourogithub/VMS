package com.dss.vms.common.interfaces;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.AuditType;
import com.dss.vms.common.response.VmsResponse;

public interface DatabaseManager {
	/**
	 * Fetch records by CamIDm, StartTime and EndTime
	 * 
	 * @param cameraID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public VmsResponse getRecords(int cameraID, long startTime, long endTime);

	/**
	 * fetch first 10batch of records
	 * 
	 * @return
	 */
	public VmsResponse fetchOutdatedRecords();

	/**
	 * Delete last 10batch of records
	 * 
	 * @return
	 */
	public VmsResponse deleteTop10Records();

	/**
	 * Delete Records by identifiers
	 * 
	 * @param CameraId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public VmsResponse deleteRecord(int CameraId, long startTime, long endTime);

	/**
	 * generate audit log
	 * 
	 * @param type
	 * @param description
	 * @param time
	 * @return
	 */
	public VmsResponse generateAuditLog(AuditType type, String description, Long time);

	/**
	 * get log : limit by value
	 * 
	 * @param limit
	 * @return
	 */
	public VmsResponse getAuditLog(Integer limit);

	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public VmsResponse getAuditLogBetween(Long startTime, Long endTime);

	/**
	 * get audit Log by type
	 * 
	 * @param type
	 * @param limit
	 * @return
	 */
	public VmsResponse getAuditLogByType(AuditType type, Integer limit);

	/**
	 * 
	 * @param type
	 * @param time
	 * @param URL
	 * @return
	 */
	public VmsResponse generateEventLog(GenericEvent event, String URL);

	/**
	 * get log by limit value.
	 * 
	 * @param limit
	 * @return Vector<EventSession>
	 */
	public VmsResponse getEventLog(Integer limit);

	/**
	 * get Log between given time frames
	 * 
	 * @param start
	 * @param end
	 * @return Vector<Eventsession> data
	 */
	public VmsResponse getEventLogBetween(Long start, Long end);

}
