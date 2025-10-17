package com.dss.master.event;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.data.EventSession;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
/**
 * 
 * @author dss-02
 *
 */
public class SqliteEventManagerImpl implements EventManager {
	private static final String DB_PATH = "db/VMS_EVENT_DB.db";

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check for Db existence
	 * 
	 * @return
	 */
	private boolean databaseExists() {

		// if DB Folder doesnt exists then Create a db Folder
		File dbPath = new File(DB_PATH.substring(0, DB_PATH.indexOf("/") + 1));

		if (!dbPath.exists()) {
			dbPath.mkdir();
		}

		boolean exists = new File(DB_PATH).exists();

		return exists;
	}

	/**
	 * create audit database
	 * 
	 * @return
	 */
	private boolean createDatabase() {
		boolean success = false;
		
		final String createCommand = "CREATE TABLE IF NOT EXISTS " + EventSchema.EVENT_TABLE_NAME + " ( "
				+ EventSchema.EVENT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT , " + EventSchema.EVENT_TYPE
				+ " INTEGER NOT NULL , " + EventSchema.EVENT_TIME + " INTEGER NOT NULL , " + EventSchema.EVENT_URL
				+ " TEXT NOT NULL " + " ) ";
		
		Statement statement = null;
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(EventSchema.EVENT_DB_PATH);
			statement = connection.createStatement();
			success = statement.execute(createCommand);
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
		
			if (statement != null) {
				
				try {statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				
				try {connection.close();} catch (SQLException e) {}
			}
		}
		
		return success;
	}

	/**
	 * generate Event log to database
	 * 
	 * @param type
	 * @param time
	 * @param URL
	 * @return
	 */
	@Override
	public synchronized VmsResponse createEventLog(AnalyticType type, Long time, String URL) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		if (!databaseExists()) {
			if (!createDatabase()) {
				response.setError(CommonResponseCode.ERROR, "failed to create Database");
				return response;
			}
		}

		// now generate log data
		String insertCommand = "INSERT INTO " + EventSchema.EVENT_TABLE_NAME + " ( " + EventSchema.EVENT_TYPE + " , "
				+ EventSchema.EVENT_TIME + " , " + EventSchema.EVENT_URL + " ) " + " VALUES ( " + type.value() + " , "
				+ time + " , " + "'" + URL + "' " + " )";

		Connection connection = null;
		Statement statement = null;

		try {
			connection = DriverManager.getConnection(EventSchema.EVENT_DB_PATH);
			statement = connection.createStatement();
			if (!statement.execute(insertCommand)) {
				response.setError(CommonResponseCode.ERROR, "failed to add Event log to Database");
				return response;
			}
			
		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "error occured - " + e.getMessage());

		} finally {
			if (statement != null) {
				try { statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				try { connection.close();} catch (SQLException e) {}
			}
		}
		
		return response;
	}

	/**
	 * get audit generated to get all the records till date set limit -1
	 * 
	 * @param limit
	 * @return
	 */
	@Override
	public VmsResponse getEventLog(Integer limit) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		// check if database exists
		if (!databaseExists()) {
			response.setError(CommonResponseCode.ERROR, "Database not found");
			return response;
		}
		
		// now get log data and send via response
		Connection connection = null;
		Statement statement = null;
		String command = "SELECT * FROM " + EventSchema.EVENT_TABLE_NAME + " ORDER BY " + EventSchema.EVENT_TIME;
		if (limit > 0) { command += " LIMIT " + limit; }

		try {
			connection = DriverManager.getConnection(DB_PATH);
			statement = connection.createStatement();

			ResultSet result = statement.executeQuery(command);
			if (result != null) {
				Vector<EventSession> list = new Vector<>();

				while (result.next()) {
					AnalyticType type = AnalyticType.find((short) result.getInt(EventSchema.EVENT_TYPE));
					Long time = result.getLong(EventSchema.EVENT_TIME);
					String url = result.getString(EventSchema.EVENT_URL);

					if (type != null) {
						EventSession session = new EventSession(url, time, type);
						list.add(session);
					}
				}

				// setting response
				response.setResponse(list);
			}
		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "error occured - " + e.getMessage());

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
	 * Filter event Log by time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public VmsResponse getLogBetween(Long startTime, Long endTime) {
		VmsResponse response = new VmsResponse();
		Connection dbConnection = null;
		Statement queryStatement = null;

		try {
			String sqlSelectCommand = "SELECT " + EventSchema.EVENT_TYPE + " , " + EventSchema.EVENT_TIME + " , "
					+ EventSchema.EVENT_URL + " FROM " + EventSchema.EVENT_TABLE_NAME + " WHERE "
					+ EventSchema.EVENT_TIME + " BETWEEN " + startTime + " AND " + endTime;

			dbConnection = DriverManager.getConnection(EventSchema.EVENT_DB_PATH);
			queryStatement = dbConnection.createStatement();
			ResultSet result = queryStatement.executeQuery(sqlSelectCommand);

			response.setResponseCode(CommonResponseCode.SUCCESS);

			if (result != null) {
				ArrayList<EventSession> records = new ArrayList<EventSession>();

				while (result.next()) {
					AnalyticType type = AnalyticType.find((short) result.getInt(EventSchema.EVENT_TYPE));
					Long time = result.getLong(EventSchema.EVENT_TIME);
					String url = result.getString(EventSchema.EVENT_URL);

					EventSession session = new EventSession(url, time, type);
					records.add(session);
				}

				response.setResponse(records);
				response.setResponseCode(CommonResponseCode.SUCCESS);
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

}
