package com.dss.master.record;

import com.dss.vms.common.response.VmsResponse;

/**
 * RecordManager Interface
 * 
 * @author sibendu
 *
 */
public interface RecordManager {
	public VmsResponse getRecords(int cameraID, long startTime, long endTime);

	public VmsResponse getOldestRecords(Integer limit);

	public VmsResponse deleteOldestRecords(int batchSize);

	public VmsResponse deleteRecord(int CameraId, long startTime, long endTime);

	public VmsResponse addRecord(int cameraID, long startTime, long endTime, String mediaURL);
}
