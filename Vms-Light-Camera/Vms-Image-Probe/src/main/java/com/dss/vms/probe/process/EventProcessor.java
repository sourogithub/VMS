package com.dss.vms.probe.process;
/**
 * Event processor handles all the image frames submitted by 
 * probes to dump them in seperate thread
 * @author dss-02
 *
 */

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.interfaces.DatabaseManager;

public class EventProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);
	private static DatabaseManager databaseManager = DatabaseManagerImpl.getInstance();
	private ExecutorService executer = null;
	private static EventProcessor INSTANCE = null;

	public static EventProcessor getInstance() {
		if (INSTANCE == null) {
			synchronized (EventProcessor.class) {
				INSTANCE = new EventProcessor();
			}
		}
		return INSTANCE;
	}

	private EventProcessor() {
		// creating a thread pool of available processors
		BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>(100);
		
		executer = new ThreadPoolExecutor(1, 10, 30, TimeUnit.SECONDS, 
				linkedBlockingDeque, new ThreadPoolExecutor.CallerRunsPolicy());
		
		// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	/**
	 * Submit frame to processor
	 * @param frame
	 * @return
	 */
	public void submitFrame(byte[] rawData, long timestamp, String mediaPath, AnalyticType analyticType) {
		if (executer != null) {
			executer.submit(() -> {
				File eventFile = new File(mediaPath);
				OutputStream fos = null;
				try {
					fos = Files.newOutputStream(eventFile.toPath());
					fos.write(rawData);
					LOGGER.info("Executing Database operation in Execution Batch");
					databaseManager.generateEventLog(analyticType, timestamp, mediaPath);
				} catch (IOException e) {
					LOGGER.error("Error occured while writing file {}, Error - {} ", mediaPath, e);
				} finally {
					if (fos != null) {
						try { fos.close(); } catch (IOException e) {}
					}
				}
			});
		}
	}

	/**
	 * shutdown the processor
	 */
	public void requestShutdown() {
		if (executer != null) {
			executer.shutdown();
			try {
				if (executer.awaitTermination(3000, TimeUnit.MILLISECONDS)) executer.shutdownNow();
			} catch (InterruptedException e) {
				executer.shutdownNow();
			}
		}
	}

}
