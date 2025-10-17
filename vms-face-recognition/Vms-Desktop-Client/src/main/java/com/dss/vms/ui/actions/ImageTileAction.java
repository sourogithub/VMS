package com.dss.vms.ui.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.data.CameraLookupTable;
import com.dss.vms.ui.utility.GridPainter;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author Sibendu
 */
public class ImageTileAction extends MouseAdapter {

	private static boolean dragging = false;
	private static boolean zoomMode = false; 
	private static GridPainter viewController = GridPainter.getInstance();
	private static CameraLookupTable  channelLookupTable = CameraLookupTable.getInstance();
	private DesktopClientApplication rootFrame = null;
		
	/*
	 * This instance stores the Previous Selected image tile such that whenever new
	 * imageTile is CLicked previous one is painted back to initial Color 
	 */
	private static ImageTile previousImageTile = null;
	private ImageTile sourceTile = null;
	private JPopupMenu popup = null;

	/**
	 * 
	 * @param rootFrame
	 * @param currentTile
	 */
	public ImageTileAction(DesktopClientApplication rootFrame) {
		this.rootFrame = rootFrame;
	}
	
	/**
	 * 
	 * @param popupMenu
	 */
	public void setPopupMenu(JPopupMenu popupMenu) {
		this.popup = popupMenu;
	}

	@Override
	public void mouseClicked(MouseEvent mouseClick) {
		ImageTile currentActiveTile = (ImageTile) mouseClick.getSource();
		currentActiveTile.setBorder(BorderFactory.createLineBorder(ComponentColors.ACTIVE_IMAGETILE, 3));
		currentActiveTile.repaint();
		if (previousImageTile != null  && !currentActiveTile.equals(previousImageTile)) {
			previousImageTile.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
			previousImageTile.repaint();
		}
		previousImageTile = currentActiveTile;

		if ((mouseClick.getButton() == MouseEvent.BUTTON1)) {
			int clickCount = mouseClick.getClickCount();
			if (clickCount == 2) {
				ImageTile currentTile = (ImageTile) mouseClick.getSource();
				if (zoomMode == false) {
					viewController.setCurrentSelectedTile(currentTile);
					viewController.drawTileZoom(currentTile, true);
					zoomMode = true;
					rootFrame.setTileZoom(zoomMode);
				} else {
					viewController.setCurrentSelectedTile(currentTile);
					viewController.drawTileZoom(currentTile, false);
					zoomMode = false;
					rootFrame.setTileZoom(zoomMode);
				}
			}
		}
		
		if (popup != null) {
			if ((mouseClick.getButton() == MouseEvent.BUTTON3)) {
				/** show popup **/
				Component source = (Component) mouseClick.getSource();
				popup.show(source, mouseClick.getX(), mouseClick.getY());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent press) {
		sourceTile = (ImageTile) press.getSource();
		rootFrame.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseReleased(MouseEvent release) {
		LiveViewPanel viewPanel = viewController.getRegisteredContainerPanel();
		if (viewPanel != null && sourceTile != null) {
			if (dragging) {
				Point locOnScreen = release.getLocationOnScreen();
				Point viewPanelLoc = viewPanel.getLocationOnScreen();
				Point location = new Point((locOnScreen.x - viewPanelLoc.x),
						(locOnScreen.y - viewPanelLoc.y));
				Component component = viewPanel.getComponentAt(location);
				if (component instanceof ImageTile) {
					ImageTile destTile = (ImageTile) viewPanel.getComponentAt(location);
					if (!sourceTile.equals(destTile)) {
						channelLookupTable.swapEntry(sourceTile, destTile);
						sourceTile = null;
					}
				}
			}
		}
		dragging = false;
		rootFrame.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseDragged(MouseEvent drag) {
		if(!dragging) {
			rootFrame.getRootPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
			dragging = true;
		}
	}

}
