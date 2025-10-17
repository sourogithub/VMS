package com.dss.vms.ui.actions;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.utility.VmsGridPainter;

public class ViewResizeHandler implements ComponentListener {
	private static VmsGridPainter viewController = VmsGridPainter.getInstance();
	private DesktopClientApplication application = null;
	
	public ViewResizeHandler(DesktopClientApplication parent) {
		this.application = parent;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent resize) {
		if (application.isTileZoomMode()) {
			viewController.drawTileZoom(viewController.getCurrentSelectedTile(), true);
			return;
		} 
		
		viewController.drawTileZoom(viewController.getCurrentSelectedTile(), false);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}

}
