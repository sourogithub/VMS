package com.dss.vms.master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;

/**
 * @author dss-02
 */
public class StorageManager implements Runnable {
	private static final String LOG_FILE_NAME = "storage_activity_log.csv";
	private static final int SLEEP_INTERVAL = 30000;
	/** 9-Gigs Default storage allocation **/
	private static final long DEFAULT_STORAGE_LIMIT = (long) (9 * 1024 * 1024);
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageManager.class);
	private static Object lock = new Object();
	private static DatabaseManager dbManager = DatabaseManagerImpl.getInstance();
	private static long STORAGE_LIMIT_IN_BYTES = DEFAULT_STORAGE_LIMIT;

	private File mediaLocation = new File("records/");
	private boolean stopRequested = false;
	private Thread thread = null;

	static {
		/* calculating 70% of free space for the current drive... */
		try {
			LOGGER.info("Trying to calculate storage limit...");
			File currentDrive = new File(System.getProperty("user.dir"));
			STORAGE_LIMIT_IN_BYTES = (long) (currentDrive.getUsableSpace() * 0.7);
			LOGGER.info("Storage limit calculated successfully (in bytes) : " + STORAGE_LIMIT_IN_BYTES);
		} catch (Exception e) {
			LOGGER.error("Failed to calculate storage limit, using Default Value , Error - " + e);
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				synchronized (lock) {
					if (stopRequested) break;
				}
				
				if (mediaLocation != null && mediaLocation.exists()) {
					long currentStorageSpace = getDirectorySize(mediaLocation);
					/**
					 * then fetch batch of 10 records from DB and delete the batch from Db as well
					 * as medias by accessing their URLs
					 **/
					while (currentStorageSpace >= STORAGE_LIMIT_IN_BYTES) {
						VmsResponse response = dbManager.fetchOutdatedRecords();
						if (response.isSuccess() && response.getResponse() instanceof ArrayList) {
							ArrayList<RecordSession> recordSessions = (ArrayList<RecordSession>) response.getResponse();

							for (RecordSession currSession : recordSessions) {
								File file = new File(currSession.getMediaUrl());
								if (file != null && file.exists()) {
									boolean deleteSuccess = file.delete();
									if (deleteSuccess) {
										LOGGER.info("Record Deleted successfully, File - " + file);
										//writeLog(currSession);
									} else {
										LOGGER.debug("Failed to delete record, File - " + file);
									}
								}
								LOGGER.info("Deleting record from database - [MediaURL = " + currSession.getMediaUrl() + "] ,"
										+ " CameraID = [" + currSession.getCameraID() + "]");
								dbManager.deleteRecord(currSession.getCameraID(), 
										currSession.getStartTime(), currSession.getEndTime());
							}
						}
						currentStorageSpace = getDirectorySize(mediaLocation);
						try {Thread.sleep(200);} catch (InterruptedException e) {}
					}
				}
				
				try {Thread.sleep(SLEEP_INTERVAL);} catch (InterruptedException e) {}
			
			} catch (Throwable e) {
				LOGGER.error("Error occured while processing , Error : " + e + ", Continuing operation.. ");
			}
		}
		
		LOGGER.info("Storage Manager Successfully shutdown...");
	}

	/**
	 * create record Log
	 * 
	 * @param recordSession
	 */
	public void writeLog(RecordSession... recordSessions) {
		File dir = new File(LOG_FILE_NAME);
		FileWriter writer = null;
		if (!dir.exists())
			dir.mkdir();
		try {
			writer = new FileWriter(new File(dir.getName() + File.separator + LOG_FILE_NAME), true);
			for (RecordSession recordSession : recordSessions) {
				String logMessage = "deleted Record [" + recordSession.getMediaUrl() + "] start Time ["
						+ new Date(recordSession.getStartTime()).toGMTString() + "] " + "end Time ["
						+ new Date(recordSession.getEndTime()).toGMTString() + "]";
				try {writer.write(logMessage);} catch (Exception e) {}
			}
		} catch (Throwable e) {
		} finally {
			if (writer != null) {
				try {writer.close();} catch (IOException e) {}
			}
		}
	}


	/**
	 * get the total size of directory
	 * 
	 * @return
	 */
	public Long getDirectorySize(File location) {
		File[] list = location.listFiles();
		Long size = 00l;
		for (File currentFile : list) {
			if (!currentFile.isDirectory()) {
				size += currentFile.length();
			} else {
				size += getDirectorySize(currentFile);
			}
		}
		return size;
	}

	/**
	 * start Storage-Manager
	 * 
	 * @return
	 */
	public synchronized VmsResponse initialise() {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		try {
			LOGGER.info("Trying to start Storage-Manager execution....");
			thread = new Thread(this);
			thread.setName("Storage-Manager Thread");
			stopRequested = false;
			thread.start();
			LOGGER.info("Storage-Manager successfully started..");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occured while initiating storage-manager , Reason - " + e);
			response.setError(CommonResponseCode.ERROR,
					"Error occured while initialising storage manager , Error - " + e);
		}
		return response;
	}

	/**
	 * shutdown storage-Manager
	 * 
	 * @return
	 */
	public synchronized VmsResponse shutdown() {
		LOGGER.info("Trying to shutdown storage-manager...");
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		stopRequested = true;
		/** interrupting storage manager to wake from sleep **/
		if(thread != null) {
			LOGGER.info("Interrupting Storage-Manager to stop execution...");
			thread.interrupt();
			try {thread.join();} catch (InterruptedException e) {}
		}
		return response;
	}

	public static StorageManager getInstance() {
		synchronized (StorageManager.class) {
			if (INSTANCE == null)
				INSTANCE = new StorageManager();
		}
		return INSTANCE;
	}
	private volatile static StorageManager INSTANCE = null;
	private StorageManager() {}

	/**
	 * @param media record location to set
	 */
	public void setRecordLocation(File location) {
		this.mediaLocation = location;
	}
	
	public long getStorageLimit() {
		return STORAGE_LIMIT_IN_BYTES;
	}
}
