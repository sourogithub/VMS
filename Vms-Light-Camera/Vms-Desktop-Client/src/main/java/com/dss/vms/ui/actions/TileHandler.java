package com.dss.vms.ui.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.data.ChannelTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.ui.utility.VmsGridPainter;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author Sibendu
 */
public class TileHandler extends MouseAdapter {
	
	private static boolean dragging = false;
	private static boolean zoomMode = false; 
	
	private static VmsGridPainter vController = VmsGridPainter.getInstance();
	private static ChannelTable  channelTable = ChannelTable.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
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
	public TileHandler(DesktopClientApplication rootFrame) {
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
		currentActiveTile.setBorder(BorderFactory.createLineBorder(VmsComponentColor.ACTIVE_IMAGETILE, 3));
		currentActiveTile.repaint();
		if (previousImageTile != null && !currentActiveTile.equals(previousImageTile)) {
			previousImageTile.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
			previousImageTile.repaint();
		}
		previousImageTile = currentActiveTile;

		if ( mouseClick.getButton() == MouseEvent.BUTTON1 ) {
			int clickCount = mouseClick.getClickCount();
			if (clickCount == 2) {
				ImageTile currentTile = (ImageTile) mouseClick.getSource();
				if (zoomMode == false) {
					vController.setCurrentSelectedTile(currentTile);
					vController.drawTileZoom(currentTile, true);
					zoomMode = true;
					rootFrame.setTileZoom(zoomMode);
				} else {
					vController.setCurrentSelectedTile(currentTile);
					vController.drawTileZoom(currentTile, false);
					zoomMode = false;
					rootFrame.setTileZoom(zoomMode);
				}
			}
		}
		
		if (popup != null) {
			if ((mouseClick.getButton() == MouseEvent.BUTTON3)) {
				popup.show((Component) mouseClick.getSource(), mouseClick.getX(), mouseClick.getY());
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
		LiveViewPanel viewPanel = vController.getRegisteredViewPanel();
		if (viewPanel != null) {
			if (dragging) {
				Point locOnScreen = release.getLocationOnScreen();
				Point viewPanelLoc = viewPanel.getLocationOnScreen();
				Point coordinate = new Point((locOnScreen.x - viewPanelLoc.x),
						(locOnScreen.y - viewPanelLoc.y));
				Component component = viewPanel.getComponentAt(coordinate);
				if (component instanceof ImageTile) {
					ImageTile destTile = (ImageTile) viewPanel.getComponentAt(coordinate);
					if (!sourceTile.equals(destTile)) {
						swapChannel(sourceTile, destTile);
					}
				}
			}
		}
		dragging = false;
		rootFrame.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * swap channels
	 * @param source
	 * @param dest
	 */
	private void swapChannel(ImageTile source, ImageTile dest) {
		int srcChannel = tileBucket.find(source);
		int destChannel = tileBucket.find(dest);

		if (srcChannel != ChannelTable.INVALID_CHANNEL && destChannel != ChannelTable.INVALID_CHANNEL) {
			int tempChannel = channelTable.getChannel(srcChannel);
			channelTable.setChannel(channelTable.getChannel(destChannel), srcChannel);
			channelTable.setChannel(tempChannel, destChannel);

			BufferedImage dImage = dest.getBufferedImage();
			tileBucket.getTile(srcChannel).setBufferedImage(dImage);
			BufferedImage sImage = source.getBufferedImage();
			tileBucket.getTile(destChannel).setBufferedImage(sImage);
			
			channelTable.saveChanges();
		}
	}

	@Override
	public void mouseDragged(MouseEvent drag) {
		rootFrame.getRootPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
		dragging = true;
	}

}
