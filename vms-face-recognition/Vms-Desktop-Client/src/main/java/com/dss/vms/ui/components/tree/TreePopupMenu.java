package com.dss.vms.ui.components.tree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.data.CameraLookupTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

public class TreePopupMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static CameraLookupTable  channelTable = CameraLookupTable.getInstance();
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	
	private JMenuItem deleteCamera;
	private VideoCamera currentCamera = null;
//	private JMenuItem pauseCamera;
//	private JMenuItem resumeCamera;
	private JMenuItem startAnalytics;
	private JMenuItem stopAnalytics;
	

	/**
	 * @param currentCamera
	 */
	public TreePopupMenu(VideoCamera camera) {
		this.currentCamera = camera;

		this.deleteCamera = new JMenuItem("Delete Camera");
		this.deleteCamera.addActionListener(this);
//		this.pauseCamera = new JMenuItem("Pause Streaming");
//		this.pauseCamera.addActionListener(this);
//		this.resumeCamera = new JMenuItem("Resume Streaming");
//		this.resumeCamera.addActionListener(this);
		this.startAnalytics = new JMenuItem("Start Analytics");
		this.startAnalytics.addActionListener(this);
		this.stopAnalytics = new JMenuItem("Stop Analytics");
		this.stopAnalytics.addActionListener(this);
		
		/** adding items to Menu **/
		this.add(deleteCamera);
//		this.add(pauseCamera);
//		this.add(resumeCamera);
		this.add(startAnalytics);
		this.add(stopAnalytics);
		
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		int cameraID = currentCamera.getId();
		if (action.getSource().equals(deleteCamera)) {
			JPanel panel = new JPanel();
			JLabel message = new JLabel("Are you sure? ");
			message.setForeground(Color.WHITE);
			panel.add(message);
			int option = JOptionPane.showConfirmDialog(null, panel, "Delete Camera",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
			if (option == JOptionPane.OK_OPTION) {
				sessionManager.deleteCamera(cameraID);
				cameraBucket.removeCamera(currentCamera);
				
				for (int tileIndex = 0; tileIndex < CameraLookupTable.NO_OF_TILES; tileIndex++) {
					if (channelTable.getChannel(tileIndex) == cameraID) {
						channelTable.setChannel(CameraLookupTable.INVALID_CHANNEL, tileIndex);
						ImageTile tile = tileBucket.getTile(tileIndex);
						tile.setBufferedImage(ImageTileBucket.getDefaultBackgroundImage());
					}
				}
				DesktopClientApplication.getRootFrame().updateTreeUI();
			}
		} 
//		else if (action.getSource().equals(pauseCamera)) {
//			VmsResponse response = sessionManager.pauseCamera(cameraID);
//
//			if (response.isSuccess()) {
//				this.pauseCamera.setEnabled(false);
//				this.resumeCamera.setEnabled(true);
//			}
//		} else if (action.getSource().equals(resumeCamera)) {
//			VmsResponse response = sessionManager.resumeCamera(cameraID);
//			if (response.isSuccess()) {
//				this.pauseCamera.setEnabled(true);
//				this.resumeCamera.setEnabled(false);
//			}
//		}
		else if (action.getSource().equals(startAnalytics)) {
			VmsResponse response = sessionManager.startAnaytic(currentCamera.getId(), AnalyticType.INTRUDER);
			if(response.isSuccess()) {
				showDialog("Success", "Analytics started on " + currentCamera.getName());
			} else {
				showDialog("Failure", "Failed to start Analytics on " 
								+ currentCamera.getName() + " " + response.getMessage());
			}
		} else if (action.getSource().equals(stopAnalytics)) {
			VmsResponse response = sessionManager.stopAnaytic(currentCamera.getId(), AnalyticType.INTRUDER);
			if(response.isSuccess()) {
				showDialog("Success", "Analytics stopped on " + currentCamera.getName());
			} else {
				showDialog("Failure", "Failed to stop Analytics on " 
								+ currentCamera.getName() + " " + response.getMessage());
			}
		}
	}
	
	/**
	 * Show Dialog 
	 * @param header
	 * @param message
	 */
	private void showDialog(String header, String message) {
		JLabel label = new JLabel(message);
		label.setForeground(ComponentColors.LABEL_COLOR);
		JOptionPane.showMessageDialog(this, new JPanel().add(label), header, JOptionPane.PLAIN_MESSAGE);
	}
}
