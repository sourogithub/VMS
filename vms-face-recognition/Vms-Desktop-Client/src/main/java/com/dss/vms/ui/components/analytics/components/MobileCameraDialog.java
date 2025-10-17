package com.dss.vms.ui.components.analytics.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.components.FormLabel;

public class MobileCameraDialog extends JDialog {
	private static final int DEFAULT_CAMERA_ID = 0;
	private static final int MIN_HEIGHT = 200;
	private static final int MIN_WIDTH = 400;
	
	private JTextField tfStreamUrl = new JTextField(15);
	private JTextField tfUsername = new JTextField(15);
	private JTextField tfPassword = new JPasswordField(15);
	private JButton OKButton = new JButton("Ok");
	
	public MobileCameraDialog() {
		this.setUndecorated(true);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.addWindowFocusListener(new WindowAdapter() {
			public void windowLostFocus(WindowEvent e) {
				MobileCameraDialog.this.setVisible(false);
				MobileCameraDialog.this.dispose();
			}
		});

		GridLayout gridLayout = new GridLayout(4, 1, 5, 5);
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 60, 5);
		
		JPanel form = new JPanel(gridLayout);
		JPanel ipPanel = new JPanel(flowLayout);
		ipPanel.add(new FormLabel("Camera Url:"));
		ipPanel.add(tfStreamUrl);
		JPanel userPanel = new JPanel(flowLayout);
		userPanel.add(new FormLabel("Username:"));
		userPanel.add(tfUsername);
		JPanel passPanel = new JPanel(flowLayout);
		passPanel.add(new FormLabel("Password:"));
		passPanel.add(tfPassword);
		JPanel control = new JPanel(flowLayout);
		control.add(OKButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(action -> {
			this.setVisible(false);
			this.dispose();
		});
		control.add(cancelButton);
		
		form.add(ipPanel);
		form.add(userPanel);
		form.add(passPanel);
		form.add(control);
		
		/* setting ok button as default button */
		this.getRootPane().setDefaultButton(OKButton);
		
		this.setLayout(new BorderLayout(5, 5));
		this.add(form, BorderLayout.CENTER);
		this.requestFocus();
	}

	public void setCameraAddAction(CameraAddAction action) {
		this.OKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String camUrl = tfStreamUrl.getText();
				/* mjpeg stream */
				boolean validationSuccess = camUrl.contains("http");
				if(validationSuccess) {
					String ipAddress = camUrl.substring(camUrl.indexOf("://") + "://".length());
					int index = ipAddress.lastIndexOf(":");
					if (index > 0) {
						ipAddress = ipAddress.substring(0, index);
					}
					index = ipAddress.lastIndexOf("/");
					if(index > 0) {
						ipAddress = ipAddress.substring(0, index);
					}
					String usr = tfUsername.getText();
					String pass = tfPassword.getText();
					/* creating camera object */
					VideoCamera camera = new VideoCamera(ipAddress, "Mobile camera", false, "Mobile Camera", 
							camUrl, camUrl, camUrl, camUrl, StreamType.ANALYTIC);
					camera.setId(DEFAULT_CAMERA_ID);
					camera.setStreamingUsername(usr);
					camera.setStreamingPassword(pass);
					action.cameraAddSuccess(camera);
				} else {
					action.cameraAddFailed();
				}
			}
		});
	}

}
