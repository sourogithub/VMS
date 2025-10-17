package com.dss.vms.ui.utility;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.imageio.ImageIO;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.CameraAddDialog;

public class VmsSystemTrayUtility implements ActionListener {

	private static VmsSystemTrayUtility INSTANCE = null;
	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popupMenu;
	private MenuItem addCamera;
	private MenuItem exitOption;

	private CameraAddDialog addCameraDialog;

	private VmsSystemTrayUtility() {
		createUI();
	}

	public static void createSystemTrayIcon() {
		if (INSTANCE == null) {
			INSTANCE = new VmsSystemTrayUtility();
		}
	}

	private void createUI() {
		try {
			if (SystemTray.isSupported()) {
				Image icon = ImageIO.read(new File("res/camera.png"));
				trayIcon = new TrayIcon(icon);
				trayIcon.setImageAutoSize(true);

				popupMenu = new PopupMenu();
				exitOption = new MenuItem("Exit");
				exitOption.addActionListener(this);
				addCamera = new MenuItem("Add Camera");
				addCamera.addActionListener(this);
				popupMenu.add(addCamera);
				popupMenu.addSeparator();
				popupMenu.add(exitOption);

				trayIcon.setPopupMenu(popupMenu);
				tray = SystemTray.getSystemTray();
				tray.add(trayIcon);
			}
		} catch (Throwable e) {}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getSource() == exitOption) {
			DesktopClientApplication.getRootFrame().shutdownApp();
		} else if (action.getSource() == addCamera) {
			addCameraDialog = new CameraAddDialog(DesktopClientApplication.getRootFrame());
			addCameraDialog.setVisible(true);
		}
	}
}
