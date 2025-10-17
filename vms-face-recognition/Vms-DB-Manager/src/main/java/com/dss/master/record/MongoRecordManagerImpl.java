package com.dss.master.record;

import java.util.ArrayList;
import java.util.Iterator;

import org.bson.Document;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.response.CommonResponseCode;
import com.dss.vms.common.response.VmsResponse;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongodB implementation of Record Manager
 * 
 * @author sibendu
 */
public class MongoRecordManagerImpl implements MongoDbParams, RecordManager {

	@Override
	public VmsResponse getRecords(int cameraID, long startTime, long endTime) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		MongoClient client = null;
		ServerAddress server = null;
		try {
			server = new ServerAddress(HOST, PORT);
			client = new MongoClient(server);

			if (AUTH_SUPPORTED) {
				MongoCredential credentials = MongoCredential.createCredential(USERNAME, DB_NAME, PASSWD.toCharArray());
			}
			
			if (client.isLocked()) {
				response.setError(CommonResponseCode.ERROR, "Database locked cannot access database");
				return response;
			}
			
			MongoDatabase database = client.getDatabase(DB_NAME);
			MongoCollection<Document> collections = database.getCollection(TABLE_NAME);
			FindIterable<Document> iter = collections.find();
			Iterator<Document> iterator = iter.iterator();
			ArrayList<RecordSession> result = new ArrayList<RecordSession>();

			while (iterator.hasNext()) {
				Document doc = iterator.next();
				int camID = doc.getInteger(CAMERA_ID);
				long start = doc.getLong(START_TIMESTAMP);
				long end = doc.getLong(END_TIMESTAMP);
				String url = doc.getString(URL);
				if (camID == cameraID && startTime <= start && endTime >= end) {
					RecordSession dbData = new RecordSession(url, camID, start, end);
					result.add(dbData);
				}
			}
			response.setResponse(result);
		} catch (Throwable e) {
			response.setError(CommonResponseCode.ERROR, "Error occured while fetching data : " + e.getMessage());
		} finally {
			if (client != null) client.close();
		}
		return response;
	}

	public VmsResponse getOldestBatchOfRecords() {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse deleteTop10Records() {
		return new VmsResponse(CommonResponseCode.NOT_IMPLEMENTED);
	}

	@Override
	public VmsResponse addRecord(int cameraID, long startTime, long endTime, String mediaURL) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		MongoClient client = null;
		ServerAddress server = null;
		
		try {
			server = new ServerAddress(HOST, PORT);
			client = new MongoClient(server);

			if (AUTH_SUPPORTED) {
				MongoCredential credentials = MongoCredential.createCredential(USERNAME, DB_NAME, PASSWD.toCharArray());
			}

			if (client.isLocked()) {
				response.setError(CommonResponseCode.ERROR, "Database locked cannot access database");
				return response;
			}

			MongoDatabase database = client.getDatabase(DB_NAME);
			MongoCollection<Document> collections = database.getCollection(TABLE_NAME);
			Document doc = new Document().append(CAMERA_ID, cameraID).append(START_TIMESTAMP, startTime)
					.append(END_TIMESTAMP, endTime).append(KEEP, true).append(URL, mediaURL);
			collections.insertOne(doc);
		} catch (Throwable e) {
			response.setError(CommonResponseCode.ERROR, "Error occured while fetching data : " + e.getMessage());
		} finally {
			if (client != null) client.close();
		
		}
		
		return response;
	}

	@Override
	public VmsResponse getBatchOfRecords(Integer limit) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		MongoClient client = null;
		ServerAddress server = null;

		try {
			server = new ServerAddress(HOST, PORT);
			client = new MongoClient(server);

			if (AUTH_SUPPORTED) {
				MongoCredential credentials = MongoCredential.createCredential(USERNAME, DB_NAME, PASSWD.toCharArray());
			}

			if (client.isLocked()) {
				response.setError(CommonResponseCode.ERROR, "Database locked cannot access database");
				return response;
			}

			MongoDatabase database = client.getDatabase(DB_NAME);
			MongoCollection<Document> collections = database.getCollection(TABLE_NAME);
			FindIterable<Document> iterable = collections.find().limit(limit);
			Iterator<Document> iterator = iterable.iterator();
			ArrayList<RecordSession> result = new ArrayList<RecordSession>();

			while (iterator.hasNext()) {
				Document doc = iterator.next();

				int camID = doc.getInteger(CAMERA_ID);
				long start = doc.getLong(START_TIMESTAMP);
				long end = doc.getLong(END_TIMESTAMP);
				String url = doc.getString(URL);
				RecordSession dbData = new RecordSession(url, camID, start, end);
				result.add(dbData);
			}
			response.setResponse(result);
		
		} catch (Throwable e) {
			response.setError(CommonResponseCode.ERROR, "Error occured while fetching data : " + e.getMessage());
		} finally {
			if (client != null)
				client.close();
		}
		return response;
	}

	@Override
	public VmsResponse deleteRecord(int cameraId, long startTime, long endTime) {
		VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);
		MongoClient client = null;
		ServerAddress server = null;
		
		try {
			server = new ServerAddress(HOST, PORT);
			client = new MongoClient(server);

			if (AUTH_SUPPORTED) {
				MongoCredential credentials = MongoCredential.createCredential(USERNAME, DB_NAME, PASSWD.toCharArray());
			}

			if (client.isLocked()) {
				response.setError(CommonResponseCode.ERROR, "Database locked cannot access database");
				return response;
			}

			MongoDatabase database = client.getDatabase(DB_NAME);
			MongoCollection<Document> collections = database.getCollection(TABLE_NAME);
			collections.deleteOne(Filters.and(Filters.eq(CAMERA_ID, cameraId), Filters.eq(START_TIMESTAMP, startTime),
					Filters.eq(END_TIMESTAMP, endTime)));
		} catch (Throwable e) {
			response.setError(CommonResponseCode.ERROR, "Error occured while fetching data : " + e.getMessage());
		} finally {
			if (client != null) client.close();
		}
		return response;
	}
}
