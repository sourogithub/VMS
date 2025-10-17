package com.dss.vms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dss.vms.master.StorageManager;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.constants.VmsIcons;

public class StatusbarPanel extends JPanel implements VmsIcons {
	public static final int PANEL_HEIGHT = 30;
	public static final int PANEL_WIDTH = DesktopClientApplication.WINDOW_DIMENSION.width;
	private JLabel diskSpaceLabel;
	private DriveFreeSpaceChecker spaceChecker;

	public StatusbarPanel() {
		this.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		setupComponents();
	}

	private void setupComponents() {
		this.setLayout(new BorderLayout());
		JPanel container = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		diskSpaceLabel = new JLabel("");
		diskSpaceLabel.setForeground(Color.WHITE);

		container.add(diskSpaceLabel);
		this.add(container, BorderLayout.CENTER);

		/** starting Threads **/
		spaceChecker = new DriveFreeSpaceChecker();
		spaceChecker.start();
	}

	/**
	 * this class checks for drive's usable space and reports to the disk-space
	 * Label
	 * 
	 * @author dss-02
	 *
	 */
	private class DriveFreeSpaceChecker extends Thread {
		private long STORAGE_THRESHOLD_LIMIT = (long) (StorageManager.getDriveUsableSpace() * 0.7);

		@Override
		public void run() {
			while (true) {
				try {
					if (diskSpaceLabel != null) {
						File recordsLocation = new File("records/");
						double spaceinGB = 0;
						if (recordsLocation.exists()) {
							long currentSpace = StorageManager.getDirectorySize(recordsLocation);
							spaceinGB = (double) currentSpace / (1024 * 1024 * 1024);
							if (currentSpace >= STORAGE_THRESHOLD_LIMIT) {
								diskSpaceLabel.setText("storage space running out : "
										+  String.format("%.2f", spaceinGB));
								diskSpaceLabel.setForeground(Color.red);
								diskSpaceLabel.setIcon(driveIcon);
							} else {
								diskSpaceLabel.setText("Drive Used Space : " 
										+ String.format("%.2f", spaceinGB) + " GB");
								diskSpaceLabel.setIcon(driveIcon);
								diskSpaceLabel.setForeground(Color.WHITE);
							}
						}
						
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}

				try { Thread.sleep(30000); } catch (InterruptedException e) {}
			}
		}
	}
}
