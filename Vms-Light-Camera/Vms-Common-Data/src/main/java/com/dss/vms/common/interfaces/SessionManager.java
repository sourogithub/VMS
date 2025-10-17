package com.dss.vms.common.interfaces;

import java.util.List;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.AuditLog;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.response.Authorization;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author jdeveloper
 */
public interface SessionManager extends MediaServer {
	/**
	 * @return
	 */
	VmsResponse sleep();

	/**
	 * @return
	 */
	VmsResponse wakeup();

	/**
	 * @param cameraID
	 * @param analyticType
	 * @param regions
	 * @return
	 */
	VmsResponse setRegion(Integer cameraID, AnalyticType analyticType, List<Region> regions);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse getRegion(Integer cameraID, AnalyticType analyticType);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse startAnaytic(Integer cameraID, AnalyticType analyticType);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse stopAnaytic(Integer cameraID, AnalyticType analyticType);

	/**
	 * @param cameraID
	 * @param analyticType
	 * @return
	 */
	VmsResponse isAnalyticStarted(Integer cameraID, AnalyticType analyticType);

	/**
	 * @return
	 */
	VmsResponse getAllCameras();

	/**
	 * @param cameraID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	VmsResponse fetchRecords(Integer cameraID, Long startTime, Long endTime);

	/**
	 * generate Audit log
	 * 
	 * @param type
	 * @param desc
	 * @param time
	 * @return
	 */
	VmsResponse createAudit(AuditLog type, String desc, Long time);

	/**
	 * get log : limit by value
	 * 
	 * @param limit
	 * @return
	 */
	VmsResponse getAuditLog(Integer limit);

	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	VmsResponse getLogBetween(Long startTime, Long endTime);

	/**
	 * get audit Log by type
	 * 
	 * @param type
	 * @param limit
	 * @return
	 */
	VmsResponse getAuditLogByType(AuditLog type, Integer limit);

	/**
	 * get events
	 * 
	 * @param limit
	 * @return
	 */
	VmsResponse getEventLog(Integer limit);

	/**
	 * get event log between time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return Vector<EventSession>
	 */
	VmsResponse getEventLogBetween(Long startTime, Long endTime);

	/**
	 * filter by type
	 * 
	 * @param type
	 * @return
	 */
	VmsResponse getEventLogByType(AnalyticType type, Integer limit);

	/**
	 * Authenticate the user
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	VmsResponse authenticate(String username, String password);
}
