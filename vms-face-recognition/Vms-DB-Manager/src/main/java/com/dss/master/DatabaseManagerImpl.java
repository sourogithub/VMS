package com.dss.master;

import com.dss.master.audit.AuditManager;
import com.dss.master.audit.SqliteAuditManagerImpl;
import com.dss.master.event.EventManager;
import com.dss.master.event.SqliteEventManagerImpl;
import com.dss.master.record.RecordManager;
import com.dss.master.record.SqliteRecordManagerImpl;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.constants.AuditType;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author sibendu
 */
public class DatabaseManagerImpl implements DatabaseManager {

	private RecordManager recordManager = null;
	private AuditManager auditManager = null;
	private EventManager eventManager = null;
	
	@Override
	public VmsResponse getRecords(int cameraID, long startTime, long endTime) {
		return recordManager.getRecords(cameraID, startTime, endTime);
	}

	public VmsResponse addRecord(int cameraID, long startTime, long endTime, String mediaURL) {
		return recordManager.addRecord(cameraID, startTime, endTime, mediaURL);
	}

	@Override
	public VmsResponse fetchOutdatedRecords() {
		return recordManager.getBatchOfRecords(10); 
	}

	@Override
	public VmsResponse deleteTop10Records() {
		return recordManager.deleteTop10Records();
	}

	@Override
	public VmsResponse deleteRecord(int CameraId, long startTime, long endTime) {
		VmsResponse response = recordManager.deleteRecord(CameraId, startTime, endTime);
//		if (response.isSuccess()) {
//			generateAuditLog(AuditType.CAMERA_DELETE, AuditType.RECORD_DELETE.getAuditDescription() 
//					+ "Camera ID - " + CameraId + "Start Time -" 
//					+ (new Date(startTime)).toLocaleString() + "End Time" + (new Date(endTime)).toLocaleString(),
//					System.currentTimeMillis());
//		}

		return response;
	}

	@Override
	public VmsResponse generateAuditLog(AuditType type, String description, Long time) {
		return auditManager.generateAuditLog(type, description, time);
	}

	@Override
	public VmsResponse getAuditLog(Integer limit) {
		return auditManager.getAuditLog(limit);
	}

	@Override
	public VmsResponse getAuditLogBetween(Long startTime, Long endTime) {
		return auditManager.getLogBetween(startTime, endTime);
	}

	@Override
	public VmsResponse getAuditLogByType(AuditType type, Integer limit) {
		return auditManager.getAuditLogByType(type, limit);
	}

	@Override
	public VmsResponse generateEventLog(GenericEvent event, String URL) {
		VmsResponse response = new VmsResponse();
		switch (event.getType()) {
		case FACE:
			System.err.println("database face recognition.");
			response = eventManager.createFREventLog((FaceRecognitionEvent) event, URL);	
			break;

		case INTRUDER : 
			response = eventManager.createIntruderEventLog(event, URL);
			break;
			
		default:
			response.setError(CommonResponseCode.ERROR, "Analytic Not Supported");
			break;
		}
		return response;
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
