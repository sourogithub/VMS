package com.dss.vms.probe.process;
/**
 * Event processor handles all the image frames submitted by 
 * probes to dump them in seperate thread along with creating database record 
 * @author dss-02
 *
 */
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dss.master.DatabaseManagerImpl;
import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.common.interfaces.DatabaseManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.probe.event.callback.EventActionListener;
import com.dss.vms.probe.event.callback.NullCallback;
import com.dss.vms.video.data.MediaFrame;

public class EventProcessor {
	private static DatabaseManager databaseManager = DatabaseManagerImpl.getInstance();
	private ExecutorService executer = null;
	private EventActionListener callback;
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
		BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>(100);
		executer = new ThreadPoolExecutor(1, 10, 30, TimeUnit.SECONDS, queue,
				new ThreadPoolExecutor.CallerRunsPolicy());
		
		// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		callback = new NullCallback();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() { requestShutdown(); }
		}));
	}

	/**
	 * Submit frame to processor
	 * 
	 * @param frame
	 * @return
	 */
	public void submitFrame(FaceRecognitionEvent event) {
		if (executer != null) {
			callback.onSubmit();
			executer.submit(() -> {
				String mediaPath = "events/Event" + (new Date(event.getTimestamp())).toLocaleString() + ".jpg";
				
				Employee employee = event.getEmployee();
				MediaFrame[] frames = employee.getFaces();
					
				if (frames != null && frames.length > 0) {
					File eventFile = new File(mediaPath);
					OutputStream fos = null;
					try {
						fos = Files.newOutputStream(eventFile.toPath());
						fos.write(frames[0].getRawFrame());

						//saving event to database
						VmsResponse response = databaseManager.generateEventLog(event, mediaPath);

						if (response.isSuccess()) {
							callback.onSuccess();
						} else {
							callback.onFailure();
						}
						
					} catch (Throwable e) {
						callback.onFailure();
					} finally {
						if (fos != null) {
							try { fos.close(); } catch (IOException e) {}
						}
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
				if (executer.awaitTermination(3000, TimeUnit.MILLISECONDS)) { executer.shutdownNow(); }
			} catch (InterruptedException e) {
				executer.shutdownNow();
			}
		}
		callback.onShutdown();
	}

	/**
	 * @param callback the callback to set
	 */
	public void setExecutionHandler(EventActionListener callback) {
		this.callback = callback;
	}
}
