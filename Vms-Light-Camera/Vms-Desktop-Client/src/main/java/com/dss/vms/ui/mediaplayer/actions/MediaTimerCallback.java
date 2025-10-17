package com.dss.vms.ui.mediaplayer.actions;

public interface MediaTimerCallback {
	
	public void mediaPlayStarted();
	
	public void mediaPlayed(float percentage);
	
	public void mediaPlayComplete();
	
	public void playInterrupted();
}
