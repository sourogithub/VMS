package com.dss.vms.probe.event.callback;

/**
 * implements Callback for Executor
 * 
 * @author dss-02
 *
 */
public interface EventActionListener {

	public void onSubmit();

	public void onFailure();

	public void onSuccess();

	public void onShutdown();
}
