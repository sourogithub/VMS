package com.dss.vms.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.analytics.AnalyticsTrainingDialog;
import com.dss.vms.ui.components.analytics.EventSearchWindow;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.mediaplayer.components.MediaPlayerWindow;

/**
 * 
 * @author dss-02
 *
 */
public class ToolbarPanel extends JPanel implements Icons {
	public static final int TOOLBAR_HEIGHT = 10;
	public static final int TOOLBAR_WIDTH = DesktopClientApplication.WINDOW_DIMENSION.width;
	public static final int DEFAULT_EVENT_COUNT = 0;

	private JButton cameraAddButton;
	private JButton playerButton;
	private JButton eventSearchButton;
	private JButton dataTrainButton;

	public ToolbarPanel() {
		this.setSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		this.setMinimumSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		setupComponents();
	}

	private void setupComponents() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		this.setBackground(ComponentColors.APP_TOOLBAR_BG);

		/** camera Adding controls **/
		cameraAddButton = new JButton(addCamera);
		cameraAddButton.setToolTipText("Add Camera");
		cameraAddButton.addActionListener(action -> {
			CameraAddDialog addCameraDialog = 
					new CameraAddDialog(DesktopClientApplication.getRootFrame());
			addCameraDialog.setVisible(true);
		});

		/** playrecords Controls **/
		playerButton = new JButton(searchRecord);
		playerButton.setToolTipText("Playback Records");
		playerButton.addActionListener(action -> {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					MediaPlayerWindow playerWindow = MediaPlayerWindow.getWindowInstance();
					playerWindow.setVisible(true);
				}
			});
		});
		
		eventSearchButton = new JButton(eventSearchIcon);
		eventSearchButton.setToolTipText("Event Search");
		eventSearchButton.addActionListener(action -> {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					EventSearchWindow eventSearchWindow = EventSearchWindow.getWindowInstance();
					eventSearchWindow.setVisible(true);
				}
			});
		});
		
		dataTrainButton = new JButton(trainingIcon);
		dataTrainButton.setToolTipText("Train Facial Data");
		dataTrainButton.addActionListener(e -> {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					AnalyticsTrainingDialog dialog = AnalyticsTrainingDialog.getWindowInstance();
					JRootPane rootPane = getRootPane();
					dialog.setLocationRelativeTo(rootPane);
					dialog.setVisible(true);
				}
			});
		});

		
		this.add(cameraAddButton);
		this.add(dataTrainButton);
		this.add(playerButton);
		this.add(eventSearchButton);
	}
}
