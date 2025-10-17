package com.dss.vms.ui.components.tree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.data.ChannelTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

public class TreePopupMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static ChannelTable  channelLookupTable = ChannelTable.getInstance();
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	
	private JMenuItem deleteCamera;
	private VideoCamera selectedCam = null;
//	private JMenuItem pauseCamera;
//	private JMenuItem resumeCamera;
//	private JMenu menu;
//	private JMenuItem startAnalytics;
//	private JMenuItem stopAnalytics;
	

	/**
	 * @param selectedCam
	 */
	public TreePopupMenu() {
		deleteCamera = new JMenuItem("Delete Camera");
		deleteCamera.addActionListener(this);
		this.add(deleteCamera);
		
//		pauseCamera = new JMenuItem("Pause Streaming");
//		pauseCamera.addActionListener(this);
//		this.add(pauseCamera);
//		
//		resumeCamera = new JMenuItem("Resume Streaming");
//		resumeCamera.addActionListener(this);
//		this.add(resumeCamera);
//		
//		startAnalytics = new JMenuItem("Start Analytics");
//		startAnalytics.addActionListener(this);
//		this.add(startAnalytics);
//		
//		stopAnalytics = new JMenuItem("Stop Analytics");
//		stopAnalytics.addActionListener(this);
//		this.add(stopAnalytics);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		int cameraID = selectedCam.getId();
		if (action.getSource().equals(deleteCamera)) {
			JPanel panel = new JPanel();
			JLabel message = new JLabel("Are you sure? ");
			message.setForeground(Color.WHITE);
			panel.add(message);
			int option = JOptionPane.showConfirmDialog(null, panel, "Delete Camera",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
			if (option == JOptionPane.OK_OPTION) {
				sessionManager.deleteCamera(cameraID);
				cameraBucket.removeCamera(selectedCam);
				
				for (int tileIndex = 0; tileIndex < ChannelTable.NO_OF_TILES; tileIndex++) {
					if (channelLookupTable.getChannel(tileIndex) == cameraID) {
						channelLookupTable.setChannel(ChannelTable.INVALID_CHANNEL, tileIndex);
						channelLookupTable.saveChanges();

						ImageTile tile = tileBucket.getTile(tileIndex);

						try { Thread.sleep(500); } catch (Exception e) {}
						tile.setBufferedImage(ImageTileBucket.getDefaultImage());
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
//		else if (action.getSource().equals(startAnalytics)) {
//			
//			VmsResponse response = sessionManager.startAnaytic(selectedCam.getId(), AnalyticType.INTRUDER);
//			
//			if(response.isSuccess()) {
//				JPanel messagePan = new JPanel();
//				JLabel message = new JLabel("Analytics started on " + selectedCam.getName());
//				message.setForeground(Color.white);
//				messagePan.add(message);
//				JOptionPane.showMessageDialog(this, messagePan, "Success", JOptionPane.PLAIN_MESSAGE);
//			} else {
//				JPanel messagePan = new JPanel();
//				JLabel message = new JLabel("Failed to started Analytics on " + selectedCam.getName() + response.getMessage());
//				message.setForeground(Color.white);
//				messagePan.add(message);
//				JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.PLAIN_MESSAGE);
//			}
//		} else if (action.getSource().equals(stopAnalytics)) {
//			
//			VmsResponse response = sessionManager.stopAnaytic(selectedCam.getId(), AnalyticType.INTRUDER);
//			
//			if(response.isSuccess()) {
//				JPanel messagePan = new JPanel();
//				JLabel message = new JLabel("Analytics stopped on " + selectedCam.getName());
//				message.setForeground(Color.white);
//				messagePan.add(message);
//				JOptionPane.showMessageDialog(this, messagePan, "Success", JOptionPane.PLAIN_MESSAGE);
//			} else {
//				JPanel messagePan = new JPanel();
//				JLabel message = new JLabel("Failed to stop Analytics on " + selectedCam.getName() + response.getMessage());
//				message.setForeground(Color.white);
//				messagePan.add(message);
//				JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.PLAIN_MESSAGE);
//			}
//		}
	}

	/**
	 * @return the selectedCam
	 */
	public VideoCamera getSelectedCam() {
		return selectedCam;
	}

	/**
	 * @param selectedCam the selectedCam to set
	 */
	public void setSelectedCam(VideoCamera selectedCam) {
		this.selectedCam = selectedCam;
	}
}
