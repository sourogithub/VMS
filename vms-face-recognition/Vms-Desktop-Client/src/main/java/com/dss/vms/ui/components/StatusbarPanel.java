package com.dss.vms.ui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.dss.vms.master.StorageManager;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.GridLayout;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.utility.GridPainter;

public class StatusbarPanel extends JPanel implements Icons {
	private static GridPainter viewController = GridPainter.getInstance();
	
	private JLabel diskSpaceLabel;
	private DriveStatusCheck spaceChecker;

	public StatusbarPanel() {
		this.setupComponents();
		/** starting Threads **/
		spaceChecker = new DriveStatusCheck();
		spaceChecker.start();
	}

	private void setupComponents() {
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		diskSpaceLabel = new JLabel();
		diskSpaceLabel.setForeground(Color.WHITE);

		// creating Buttons
		JButton layoutButton1x1 = new JButton(grid_1x1);
		layoutButton1x1.addActionListener(action -> {
			viewController.drawImageTilesOnViewPanel(GridLayout.Layout1x1);
		});
		JButton layoutButton2x2 = new JButton(grid_2x2);
		layoutButton2x2.addActionListener(action-> {
			viewController.drawImageTilesOnViewPanel(GridLayout.Layout2x2);
		});

		JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		containerPanel.add(new FormLabel("Select Layout :"));
		containerPanel.add(layoutButton1x1);
		containerPanel.add(layoutButton2x2);
		
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
		this.add(containerPanel);
		this.add(diskSpaceLabel);
	}

	private class DriveStatusCheck extends Thread {
		private StorageManager storageManager = StorageManager.getInstance();

		@Override
		public void run() {
			while (true) {
				try {
					if (diskSpaceLabel != null) {
						File recordsLocation = new File("records/");
						double spaceinGB = 0;
						if (recordsLocation.exists()) {
							long currentSpace = storageManager.getDirectorySize(recordsLocation);
							spaceinGB = (double) currentSpace / (1024 * 1024 * 1024);
							if (currentSpace >= storageManager.getStorageLimit()) {
								diskSpaceLabel.setText("Warning storage space running out");
								diskSpaceLabel.setForeground(Color.red);
							}
						}
						diskSpaceLabel.setText("Drive Used Space : " 
								+ String.format("%.2f", spaceinGB) + " GB");
						diskSpaceLabel.setIcon(driveIcon);
					}
				} catch (Throwable e) {}

				try { Thread.sleep(30000); } catch (InterruptedException e) {}
			}
		}
	}
}
