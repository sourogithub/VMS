package com.dss.master.event;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.FrameType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.video.data.MediaFrame;
/**
 * 
 * @author dss-02
 *
 */
public class SqliteEventManagerImpl implements EventManager, EventSchema {
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
	private boolean DBExists() {
		// if DB Folder doesnt exists then Create a db Folder
		File dbPath = new File(DB_PATH.substring(0, DB_PATH.indexOf("/") + 1));
		if (!dbPath.exists()) { dbPath.mkdir(); }

		boolean exists = new File(DB_PATH).exists();
		return exists;
	}

	/**
	 * create audit database
	 * 
	 * @return
	 */
	private boolean createEventDatabase() {
		boolean success = false;
		
		final String createCommand = "CREATE TABLE IF NOT EXISTS " + EVENT_TABLE_NAME 
				+ " ( "
					+ EVENT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT , "
					+ EVENT_TYPE + " INTEGER NOT NULL , "
					+ EVENT_TIME + " INTEGER NOT NULL , "
					+ EVENT_URL + " TEXT NOT NULL , "
					+ EMPL_ID + " TEXT NOT NULL , "
					+ EMPL_NAME + " TEXT NOT NULL , "
					+ EMPL_DOB + " INTEGER NOT NULL , "
					+ EMPL_GENDER + " TEXT NOT NULL"
				+ " ) ";
		
		Statement statement = null;
		Connection connection = null;

		System.err.println(createCommand);
		try {
			connection = DriverManager.getConnection(EVENT_DB_PATH);
			statement = connection.createStatement();
			statement.execute(createCommand);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (statement != null) { try {statement.close();} catch (SQLException e) {}	}
			if (connection != null) { try {connection.close();} catch (SQLException e) {} }
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
	public synchronized VmsResponse createIntruderEventLog(GenericEvent event, String URL) {
		VmsResponse respose = new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
		return respose;
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
		if (!DBExists()) {
			response.setError(CommonResponseCode.ERROR, "Database not found");
			return response;
		}
		
		// now get log data and send via response
		Connection connection = null;
		Statement statement = null;
		String selectCommand = "SELECT * FROM " + EVENT_TABLE_NAME + " ORDER BY " + EVENT_TIME;
		if (limit != -1) {
			selectCommand += " LIMIT " + limit;
		}

		try {
			connection = DriverManager.getConnection(DB_PATH);
			statement = connection.createStatement();

			ResultSet result = statement.executeQuery(selectCommand);
			if (result != null) {
				Vector<GenericEvent> list = new Vector<>();

				while (result.next()) {
					// fetching datas
					AnalyticType type = AnalyticType.find((short) result.getInt(EVENT_TYPE));
					Long time = result.getLong(EVENT_TIME);
					String url = result.getString(EVENT_URL);

					if (type != null) {
						GenericEvent session = new GenericEvent(time, type);
						//add the url
						
						// adding to list
						list.add(session);
					}
				}

				// setting response
				response.setResponse(list);
			}
		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "error occured - " + e.getMessage());

		} finally {
			if (statement != null) { try {statement.close();} catch (SQLException e) {}	}
			if (connection != null) { try {connection.close();} catch (SQLException e) {} }
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
			String sqlSelectCommand = "SELECT " + EVENT_TYPE + " , " + EVENT_TIME + " , "
					+ EVENT_URL + " FROM " + EVENT_TABLE_NAME + " WHERE "
					+ EVENT_TIME + " BETWEEN " + startTime + " AND " + endTime;

			dbConnection = DriverManager.getConnection(EVENT_DB_PATH);
			queryStatement = dbConnection.createStatement();
			ResultSet result = queryStatement.executeQuery(sqlSelectCommand);

			response.setResponseCode(CommonResponseCode.SUCCESS);

			if (result != null) {
				/* Adding resultset values into serializable arraylist */
				Vector<GenericEvent> records = new Vector<>();
				while (result.next()) {
					AnalyticType type = AnalyticType.find((short) result.getInt(EVENT_TYPE));
					Long time = result.getLong(EVENT_TIME);
					String url = result.getString(EVENT_URL);
					GenericEvent session = null;
					
					if (type == AnalyticType.FACE) {
						session = new FaceRecognitionEvent(time, true);
						Employee employee = new Employee();
							
						FileInputStream readerStream = null;
						try {
							 File mediaFile = new File(url);
							 if(mediaFile.exists()) {
								 readerStream = new FileInputStream(mediaFile);
								 byte[] rawData = new byte[readerStream.available()];
								 readerStream.read(rawData);
								 
								 MediaFrame frame = new MediaFrame(FrameType.I_FRAME, -1, MediaType.JPEG, 
										 rawData, System.currentTimeMillis(), StreamType.ANALYTIC, 0, 0);
								 frame.setMediaType(MediaType.JPEG);
								 
								 employee.setFaces(frame);
							 }
						} catch (Exception e) {
							if(readerStream != null) {
								try { readerStream.close(); } catch (Exception ex) {}
							}
						}
						
						((FaceRecognitionEvent) session).setEmployee(employee);
					} 
					records.add(session);
				}

				response.setResponse(records);
				response.setResponseCode(CommonResponseCode.SUCCESS);
			}

		} catch (SQLException e) {
			response.setResponseCode(CommonResponseCode.ERROR);
			response.setMessage("Error Encountered while establishing Database connection " + e.getMessage());
			
		} finally {
			if (queryStatement != null) { try {queryStatement.close();} catch (SQLException e) {} }
			if (dbConnection != null) { try {dbConnection.close();} catch (SQLException e) {} }
		}
		return response;
	}

	@Override
	public VmsResponse createFREventLog(FaceRecognitionEvent event, String url) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		
		
		if (!DBExists()) {
			if (!createEventDatabase()) {
				response.setError(CommonResponseCode.ERROR, "failed to create Database ");
				return response;
			}
		}
		
		FaceRecognitionEvent frEvent = (FaceRecognitionEvent) event;
		Employee employee = frEvent.getEmployee();
		long time = event.getTimestamp();
		AnalyticType type = event.getType();
		
		// now generate log data
		String insertCommand = "INSERT INTO " + EVENT_TABLE_NAME 
				+ " ( " 
					+ EVENT_TYPE + " , "
					+ EVENT_TIME + " , "
					+ EVENT_URL + " , "
					+ EMPL_ID + " , "
					+ EMPL_NAME + " , "
					+ EMPL_DOB + " , "
					+ EMPL_GENDER 
				+ " ) " 
				+ " VALUES "
				+ "( " 
					+ type.value() + " , "
					+ time + " , "
					+ "'" + url + "' ,"
					+ "'" + employee.getEmployeeId() + "' ,"
					+ "'" + employee.getEmployeeName() + "' ,"
					+ employee.getEmployeeDoB().getTime() + " , "
					+ "'" + employee.getEmployeeGender() + "'"
				+ " )";
		
		Connection connection = null;
		Statement statement = null;

		try {
			connection = DriverManager.getConnection(EVENT_DB_PATH);
			statement = connection.createStatement();
			statement.execute(insertCommand);

		} catch (SQLException e) {
			response.setError(CommonResponseCode.ERROR, "error occured - " + e.getMessage());

		} finally {
			if (statement != null) { try { statement.close(); } catch (SQLException e) {} }
			if (connection != null) { try { connection.close(); } catch (SQLException e) {} }
		}
	return response;
}

}
