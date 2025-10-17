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
	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popupMenu;
	private MenuItem addCamera;
	private MenuItem exitOption;

	private VmsSystemTrayUtility() {
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

	private static VmsSystemTrayUtility INSTANCE = null;
	public static void createSystemTrayIcon() {
		synchronized (VmsSystemTrayUtility.class) {
			if (INSTANCE == null) {
				INSTANCE = new VmsSystemTrayUtility();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getSource().equals(exitOption)) {
			DesktopClientApplication.getRootFrame().shutdownApp();
		} else if (action.getSource().equals(addCamera)) {
			CameraAddDialog addCameraDialog = new CameraAddDialog(DesktopClientApplication.getRootFrame());
			addCameraDialog.setVisible(true);
		}
	}
}
