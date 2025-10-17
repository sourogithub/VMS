package com.dss.master.audit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.dss.vms.common.constants.AuditType;
import com.dss.vms.common.data.AuditSession;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
/**
 * 
 * @author dss-02
 *
 */
public class SqliteAuditManagerImpl implements AuditManager {
	private static final String DB_PATH = "db/VMS_AUDIT_DB.db";

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
	private boolean createAuditDatabase() {
		boolean success = false;
		
		final String createCommand = "CREATE TABLE IF NOT EXISTS " + AuditSchema.AUDIT_TABLE_NAME + " ( "
				+ AuditSchema.AUDIT_PK + " INTEGER PRIMARY KEY AUTOINCREMENT , " + AuditSchema.AUDIT_TYPE
				+ " INTEGER NOT NULL , " + AuditSchema.AUDIT_DESC + " TEXT NOT NULL , " + AuditSchema.AUDIT_TIME
				+ " INTEGER NOT NULL " + " ) ";

		Statement statement = null;
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(AuditSchema.AUDIT_DB_PATH);
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
	 * add generated user audit to database
	 * 
	 * @param type
	 * @param description
	 * @param time
	 * @return
	 */
	@Override
	public VmsResponse generateAuditLog(AuditType type, String description, Long time) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		if (!DBExists()) {
			if (!createAuditDatabase()) {
				response.setError(CommonResponseCode.ERROR, "failed to create Database");
				return response;
			}
		}

		// now generate log data
		String insertCommand = "INSERT INTO " + AuditSchema.AUDIT_TABLE_NAME + " ( " + AuditSchema.AUDIT_TYPE + " , "
				+ AuditSchema.AUDIT_DESC + " , " + AuditSchema.AUDIT_TIME + " ) " + " VALUES ( " + type.getAuditID()
				+ " , " + "'" + description + "'" + " ," + time + " ) ";
		Connection connection = null;
		Statement statement = null;

		try {
			connection = DriverManager.getConnection(AuditSchema.AUDIT_DB_PATH);
			statement = connection.createStatement();

			if (!statement.execute(insertCommand)) {
				response.setError(CommonResponseCode.ERROR, "failed to add log to Database");
				return response;
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
	 * get audit generated to get all the records till date set limit -1
	 * 
	 * @param limit
	 * @return
	 */
	@Override
	public VmsResponse getAuditLog(Integer limit) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		// check if database exists
		if (!DBExists()) {
			response.setError(CommonResponseCode.ERROR, "Database not found");
			return response;
		}
		// now get log data and send via response
		Connection connection = null;
		Statement statement = null;
		
		String selectCommand = "SELECT * FROM " + AuditSchema.AUDIT_TABLE_NAME + " ORDER BY " + AuditSchema.AUDIT_TIME;
		if (limit != -1) {
			selectCommand += " LIMIT " + limit;
		}

		try {
			connection = DriverManager.getConnection(DB_PATH);
			statement = connection.createStatement();

			ResultSet result = statement.executeQuery(selectCommand);
			if (result != null) {
				ArrayList<AuditSession> list = new ArrayList<>();

				while (result.next()) {
					AuditSession session = new AuditSession();

					// fetching datas
					Integer id = result.getInt(AuditSchema.AUDIT_TYPE);
					String description = result.getString(AuditSchema.AUDIT_DESC);
					Long time = result.getLong(AuditSchema.AUDIT_TIME);

					AuditType type = AuditType.find(id);
					if (type != null) {
						session.setAuditType(type);
						session.setDescription(description);
						session.setTimestamp(time);
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
	 * get log between particular time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public VmsResponse getLogBetween(Long startTime, Long endTime) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		// check if database exists
		if (!DBExists()) {
			response.setError(CommonResponseCode.ERROR, "Database not found");
			return response;
		}
		
		// now get log data and send via response
		Connection connection = null;
		Statement statement = null;
		String selectCommand = "SELECT * FROM " + AuditSchema.AUDIT_TABLE_NAME + " WHERE " + AuditSchema.AUDIT_TIME
				+ " BETWEEN " + startTime + " AND " + endTime + " ORDER BY " + AuditSchema.AUDIT_TIME;

		try {
			connection = DriverManager.getConnection(DB_PATH);
			statement = connection.createStatement();

			ResultSet result = statement.executeQuery(selectCommand);
			if (result != null) {
				ArrayList<AuditSession> list = new ArrayList<>();

				while (result.next()) {
					AuditSession session = new AuditSession();

					// fetching datas
					Integer id = result.getInt(AuditSchema.AUDIT_TYPE);
					String description = result.getString(AuditSchema.AUDIT_DESC);
					Long time = result.getLong(AuditSchema.AUDIT_TIME);

					AuditType type = AuditType.find(id);
					if (type != null) {
						session.setAuditType(type);
						session.setDescription(description);
						session.setTimestamp(time);

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
	 * search log by log type
	 * 
	 * @param type
	 * @param limit
	 * @return
	 */
	@Override
	public VmsResponse getAuditLogByType(AuditType type, Integer limit) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

		// check if database exists
		if (!DBExists()) {
			response.setError(CommonResponseCode.ERROR, "Database not found");
			return response;
		}
		
		// now get log data and send via response
		Connection connection = null;
		Statement statement = null;
		
		String selectCommand = "SELECT * FROM " + AuditSchema.AUDIT_TABLE_NAME + " WHERE " + AuditSchema.AUDIT_TYPE
				+ " = " + type + " ORDER BY " + AuditSchema.AUDIT_TIME;
		if (limit != -1) {
			selectCommand += " LIMIT " + limit;
		}

		try {
			connection = DriverManager.getConnection(DB_PATH);
			statement = connection.createStatement();

			ResultSet result = statement.executeQuery(selectCommand);
			if (result != null) {
				ArrayList<AuditSession> list = new ArrayList<>();

				while (result.next()) {
					AuditSession session = new AuditSession();

					// fetching datas
					Integer id = result.getInt(AuditSchema.AUDIT_TYPE);
					String description = result.getString(AuditSchema.AUDIT_DESC);
					Long time = result.getLong(AuditSchema.AUDIT_TIME);

					AuditType audType = AuditType.find(id);
					if (type != null) {
						session.setAuditType(audType);
						session.setDescription(description);
						session.setTimestamp(time);

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
			if (statement != null) {
				try {statement.close();} catch (SQLException e) {}
			}
			
			if (connection != null) {
				try {connection.close();} catch (SQLException e) {}
			}
		}

		return response;
	}
}
