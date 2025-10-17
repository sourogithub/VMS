package com.dss.vms.ui.mediaplayer.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.ui.mediaplayer.components.MediaPlayerContainer;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
/**
 * MediaTimer is runnable implementation for 
 * checking the current media state and updating the media-slider 
 * accordingly, also updating the media-play time upon user input.
 * @see MediaPlayerContainer
 * @author dss-02
 *
 */
public class MediaTimerAction implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaTimerAction.class);
	private MediaPlayer player;
	private boolean stopRequest;
	private Thread thread = null;
	private MediaTimerCallback callback;
	
	public MediaTimerAction(MediaPlayer player, MediaTimerCallback callback) {
		this.player = player;
		this.callback = callback;
	}

	@Override
	public void run() {
		callback.mediaPlayStarted();
		stopRequest = false;
		while (!stopRequest) {
			try {
				float currentPos = player.status().position();
				float percentagePlayed = Math.abs(currentPos * 100f);
				callback.mediaPlayed(percentagePlayed);

				Thread.sleep(1000);
				if (percentagePlayed >= 100f) {
					callback.mediaPlayComplete();
					break;
				}
				
			} catch (Throwable e) {
				stopRequest = true;
				break;
			}
		}
		
		if(stopRequest) callback.playStopped();
	}

	public void start() {
		/** stop previously running media **/
		stopAction();
		LOGGER.info("Starting new thread action..");
		thread = new Thread(this);
		thread.start();
		LOGGER.info("Thread action sucessfully started..");
	}
	
	public void stopAction() {
		if(thread != null) {
			LOGGER.info("Trying to stop previous running thread.");
			this.stopRequest =  true;
			thread.interrupt();
			/** wait till the previous action is finished **/
			try {thread.join();} catch (InterruptedException e) {}
			LOGGER.info("Previous running thread successfully stopped ...");
		}
	}
	
}
