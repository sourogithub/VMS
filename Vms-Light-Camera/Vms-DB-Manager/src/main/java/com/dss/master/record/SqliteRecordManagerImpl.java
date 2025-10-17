package com.dss.master.record;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;

import java.sql.*;
import java.util.ArrayList;

public class SqliteRecordManagerImpl implements RecordManager {
	private static final int EXECUTE_SUCCESS = 0;
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get records by IDs
	 * 
	 * @param cameraID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public VmsResponse getRecords(int cameraID, long startTime, long endTime) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		
		/* sql query for selecting records within given time ranges */
		Connection dbConnection = null;
		Statement queryStatement = null;

		try {
			String sqlSelectCommand = "SELECT " + RecordSchema.RECORD_CAM_ID + " , " + RecordSchema.RECORD_START_TIME
					+ " , " + RecordSchema.RECORD_END_TIME + " , " + RecordSchema.RECORD_URL + " FROM "
					+ RecordSchema.RECORD_TABLE_NAME + " WHERE " + RecordSchema.RECORD_START_TIME + " BETWEEN "
					+ startTime + " AND " + endTime + " AND " + RecordSchema.RECORD_CAM_ID + " = " + cameraID;

			dbConnection = DriverManager.getConnection(RecordSchema.RECORD_DB_PATH);
			queryStatement = dbConnection.createStatement();
			ResultSet result = queryStatement.executeQuery(sqlSelectCommand);

			if (result != null) {
				/** Adding resultset values into serializable arraylist **/
				ArrayList<RecordSession> records = new ArrayList<RecordSession>();
				
				while (result.next()) {
					int ID = result.getInt(RecordSchema.RECORD_CAM_ID);
					long sTime = result.getLong(RecordSchema.RECORD_START_TIME);
					long eTime = result.getLong(RecordSchema.RECORD_END_TIME);
					String mediaUrl = result.getString(RecordSchema.RECORD_URL);

					RecordSession record = new RecordSession(mediaUrl, ID, sTime, eTime);
					records.add(record);
				}

				response.setResponse(records);
			}

		} catch (SQLException e) {
			response.setResponseCode(CommonResponseCode.ERROR);
			response.setMessage("Error Encountered while establishing Database connection " + e.getMessage());
		} finally {
			
			if (queryStatement != null) {
				try {queryStatement.close();} catch (SQLException e) {}
			}

			if (dbConnection != null) {
				try {dbConnection.close();} catch (SQLException e) {}
			}
		}
		return response;
	}

	/**
	 * get old 10 records from Db
	 * 
	 * @return
	 */
	@Override
	public VmsResponse getOldestRecords(Integer limit) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		Connection connection = null;
		Statement statement = null;

		String query = "SELECT * FROM " + RecordSchema.RECORD_TABLE_NAME + " LIMIT " + limit;

		try {
			connection = DriverManager.getConnection(RecordSchema.RECORD_DB_PATH);
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);

			if (result != null) {
				ArrayList<RecordSession> records = new ArrayList<>(limit);

				while (result.next()) {
					int ID = result.getInt(RecordSchema.RECORD_CAM_ID);
					long sTime = result.getLong(RecordSchema.RECORD_START_TIME);
					long eTime = result.getLong(RecordSchema.RECORD_END_TIME);
					String mediaUrl = result.getString(RecordSchema.RECORD_URL);

					RecordSession currentRec = new RecordSession(mediaUrl, ID, sTime, eTime);
					records.add(currentRec);
				}
				response.setResponse(records);
			} 
		} catch (SQLException sql) {
			response.setError(CommonResponseCode.ERROR, "failed to retrieve data from Database, Error - " + sql);
		
		} finally {
			if (statement != null) {
				try {statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				try {connection.close();} catch (SQLException e) {}
			}
		}

		return response;
	}

	/**
	 * delete oldest 10records
	 * 
	 * @return
	 */
	@Override
	public VmsResponse deleteOldestRecords(int batchSize) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		
		Connection connection = null;
		Statement statement = null;

		String command = " DELETE FROM " + RecordSchema.RECORD_TABLE_NAME + " WHERE " + RecordSchema.RECORD_PK + " IN "
				+ " ( " + "SELECT " + RecordSchema.RECORD_PK + "  FROM " + RecordSchema.RECORD_TABLE_NAME + " ORDER BY "
				+ RecordSchema.RECORD_PK + " LIMIT " + batchSize + " ) ";

		try {
			connection = DriverManager.getConnection(RecordSchema.RECORD_DB_PATH);
			statement = connection.createStatement();
			int resultCode = statement.executeUpdate(command);
//			if(resultCode != EXECUTE_SUCCESS) response.setError(CommonResponseCode.ERROR, "Failed to delete records..");
		
		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "Failed to delete batch of records");

		} finally {
			if (statement != null) try {statement.close();} catch (SQLException e) {}
			if (connection != null) try {connection.close();} catch (SQLException e) {}
		}

		return response;
	}

	/**
	 * delete record by ID
	 * 
	 * @param CameraId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public VmsResponse deleteRecord(int CameraId, long startTime, long endTime) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		Connection connection = null;
		Statement statement = null;

		String command = " DELETE FROM " + RecordSchema.RECORD_TABLE_NAME + " WHERE " + RecordSchema.RECORD_CAM_ID
				+ " = " + CameraId + " AND " + RecordSchema.RECORD_START_TIME + " = " + startTime + " AND "
				+ RecordSchema.RECORD_END_TIME + " = " + endTime;

		try {
			connection = DriverManager.getConnection(RecordSchema.RECORD_DB_PATH);
			statement = connection.createStatement();

			boolean success = statement.executeUpdate(command) == EXECUTE_SUCCESS;
//			if(!success) response.setResponseCode(CommonResponseCode.ERROR);
			
		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "Failed to delete batch of records");

		} finally {
			
			if (statement != null) {
				try {statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				try {connection.close();} catch (SQLException e) {}
			}
		}

		return response;
	}

	/**
	 * add record to DB
	 * 
	 * @param cameraID
	 * @param startTime
	 * @param endTime
	 * @param mediaURL
	 * @return
	 */
	@Override
	public VmsResponse addRecord(int cameraID, long startTime, long endTime, String mediaURL) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		final String InsertCommand = "INSERT INTO " + RecordSchema.RECORD_TABLE_NAME + " ( "
				+ RecordSchema.RECORD_CAM_ID + " , " + RecordSchema.RECORD_START_TIME + " , "
				+ RecordSchema.RECORD_END_TIME + " , " + RecordSchema.RECORD_KEEP + " , " + RecordSchema.RECORD_URL
				+ " ) " + " VALUES ( " + cameraID + " , " + startTime + " , " + endTime + ", 1 , '" + mediaURL + "' )";
		
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = DriverManager.getConnection(RecordSchema.RECORD_DB_PATH);
			statement = connection.createStatement();
			boolean success = statement.executeUpdate(InsertCommand) == EXECUTE_SUCCESS;
//			if(!success) {
//				response.setError(CommonResponseCode.ERROR, "Failed to insert record for cameraID: " + cameraID 
//						+ " mediaURL : " + mediaURL);
//			}
		} catch (Exception e) {
			response.setError(CommonResponseCode.ERROR, "Error Encountered : " + e.getMessage());
		} finally {
			if (statement != null) {
				try { statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				try {connection.close();} catch (SQLException e) {}
			}
		}
		return response;
	}

}
