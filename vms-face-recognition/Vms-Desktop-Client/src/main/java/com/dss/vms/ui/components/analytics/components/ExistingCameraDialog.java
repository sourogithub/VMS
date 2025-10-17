package com.dss.vms.ui.components.analytics.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.mediaplayer.actions.ComboBoxCameraRenderer;

public class ExistingCameraDialog extends JDialog {
	private static final Dimension DIALOG_DIMENSION = new Dimension(150, 50);
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	private JComboBox<Object> cameraList = new JComboBox<>();
	
	public ExistingCameraDialog() {
		this.setUndecorated(true);
		this.setMaximumSize(DIALOG_DIMENSION);
		this.setMinimumSize(DIALOG_DIMENSION);
		this.setAlwaysOnTop(true);
		this.addWindowFocusListener(new WindowAdapter() {
			public void windowLostFocus(WindowEvent e) {
				ExistingCameraDialog.this.setVisible(false);
				ExistingCameraDialog.this.dispose();
			}
		});
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.cameraList.setRenderer(new ComboBoxCameraRenderer());
		this.cameraList.addItem("Select Camera");
		List<VideoCamera> cameras = cameraBucket.getAllCameras();

		for(VideoCamera camera : cameras) {
			this.cameraList.addItem(camera);
		}
		this.add(cameraList);
		this.pack();
	}
	

	public void setCameraSelectionAction(ActionListener action) {
		this.cameraList.addActionListener(action);
	}
}
