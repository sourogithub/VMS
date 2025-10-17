package com.dss.master;

import com.dss.master.audit.AuditManager;
import com.dss.master.audit.SqliteAuditManagerImpl;
import com.dss.master.event.EventManager;
import com.dss.master.event.SqliteEventManagerImpl;
import com.dss.master.record.RecordManager;
import com.dss.master.record.SqliteRecordManagerImpl;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.AuditLog;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.VmsResponse;

import java.util.Date;

/**
 * @author sibendu
 */
public class DatabaseManagerImpl implements DatabaseManager {
	private static final int RECORD_BATCH_SIZE = 10;
	private RecordManager recordManager;
	private AuditManager auditManager;
	private EventManager eventManager;
	
	@Override
	public VmsResponse getRecords(int cameraID, long startTime, long endTime) {
		return recordManager.getRecords(cameraID, startTime, endTime);
	}

	public VmsResponse addRecord(int cameraID, long startTime, long endTime, String mediaURL) {
		return recordManager.addRecord(cameraID, startTime, endTime, mediaURL);
	}

	@Override
	public VmsResponse fetchOutdatedRecords() {
		return recordManager.getOldestRecords(RECORD_BATCH_SIZE); 
	}

	@Override
	public VmsResponse deleteOldestBatchRecords() {
		return recordManager.deleteOldestRecords(RECORD_BATCH_SIZE);
	}

	@Override
	public VmsResponse deleteRecord(int CameraId, long startTime, long endTime) {
		VmsResponse response = recordManager.deleteRecord(CameraId, startTime, endTime);
		if (response.isSuccess()) {
			generateAuditLog(AuditLog.CAMERA_DELETE, AuditLog.RECORD_DELETE.getMessage() 
					+ "Camera ID - " + CameraId + "Start Time -" 
					+ (new Date(startTime)).toLocaleString() + "End Time" + (new Date(endTime)).toLocaleString(),
					System.currentTimeMillis());
		}

		return response;
	}

	@Override
	public VmsResponse generateAuditLog(AuditLog type, String description, Long time) {
		return auditManager.generateAuditLog(type, description, time);
	}

	@Override
	public VmsResponse getAuditLog(Integer limit) {
		return auditManager.getAuditLogs(limit);
	}

	@Override
	public VmsResponse getAuditLogBetween(Long startTime, Long endTime) {
		return auditManager.getLogBetween(startTime, endTime);
	}

	@Override
	public VmsResponse getAuditLogByType(AuditLog type, Integer limit) {
		return auditManager.getAuditLogByType(type, limit);
	}

	@Override
	public VmsResponse generateEventLog(AnalyticType type, Long time, String URL) {
		return eventManager.createEventLog(type, time, URL);
	}

	@Override
	public VmsResponse getEventLog(Integer limit) {
		return eventManager.getEventLog(limit);
	}

	@Override
	public VmsResponse getEventLogBetween(Long start, Long end) {
		return eventManager.getLogBetween(start, end);
	}
	
	/**
	 * get instance of DB-Manager
	 * @return
	 */
	public static DatabaseManagerImpl getInstance() {
		if (INSTANCE == null) {
			synchronized (DatabaseManagerImpl.class) {
				INSTANCE = new DatabaseManagerImpl();
			}
		}
		return INSTANCE;
	}
	
	private static DatabaseManagerImpl INSTANCE = null;
	
	private DatabaseManagerImpl() {
		recordManager = new SqliteRecordManagerImpl();
		auditManager = new SqliteAuditManagerImpl();
		eventManager = new SqliteEventManagerImpl();
	}

}
