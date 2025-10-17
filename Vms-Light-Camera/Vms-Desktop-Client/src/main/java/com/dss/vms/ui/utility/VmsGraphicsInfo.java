package com.dss.vms.ui.utility;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.GraphicsDevice.WindowTranslucency;

public class VmsGraphicsInfo {
	public static GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice device = graphicsEnv.getDefaultScreenDevice();

	public static boolean translucencySupported() {
		return device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
	}

	public static boolean isFullScrSupported() {
		return device.isFullScreenSupported();
	}

	/**
	 * Fullscreen displayview
	 * 
	 * @param window
	 */
	public static void setFullScreen(Window window) {
		if (isFullScrSupported()) {
			device.setFullScreenWindow(window);
		}
	}
}
