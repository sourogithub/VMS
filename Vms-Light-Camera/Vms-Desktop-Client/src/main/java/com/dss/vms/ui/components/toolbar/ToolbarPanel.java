package com.dss.vms.ui.components.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.dss.vms.common.data.EventSession;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.CameraAddDialog;
import com.dss.vms.ui.components.LayoutSelectorDialog;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.constants.VmsIcons;
import com.dss.vms.ui.mediaplayer.components.MediaPlayerWindow;

/**
 * 
 * @author dss-02
 *
 */
public class ToolbarPanel extends JPanel implements VmsIcons {
	public static final int TOOLBAR_HEIGHT = 10;
	public static final int TOOLBAR_WIDTH = DesktopClientApplication.WINDOW_DIMENSION.width;
	public static final int DEFAULT_EVENT_COUNT = 0;

//	private JButton eventNotificationButton;
//	private JButton layoutSelectorButton;
	private JButton cameraAddButton;
	private JButton playerButton;
//	private JButton eventSearchButton;

	/** data related to Event Generation **/
//	private int eventGeneratedCount = DEFAULT_EVENT_COUNT;
//	private Vector<EventSession> eventStore = new Vector<>();
//	private boolean layoutDialogVisible = false;
//	private boolean notificationDialogVisible = false;
//	private EventNotificationDialog notificationDialog = null;
//	private LayoutSelectorDialog layoutDialog = null;

	public ToolbarPanel() {
		this.setSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		this.setMinimumSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		setupComponents();
	}

	private void setupComponents() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		this.setBackground(new Color(74, 79, 89));

		/** setting drag listener for undecorated Jframe **/
//		ComponentMovementListener dragListener = new ComponentMovementListener();
//		this.addMouseListener(dragListener);
//		this.addMouseMotionListener(dragListener);
		
		// event notifications
//		eventNotificationButton = new JButton("Events Notification");
//		eventNotificationButton.setIcon(eventDefault);
//		eventNotificationButton.setContentAreaFilled(false);
//		eventNotificationButton.setToolTipText("Events Notification");
//		eventNotificationButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent ae) {
//				if(notificationDialogVisible == true) {
//					closeNotificationDialog();
//				} else if(notificationDialogVisible == false){
//					showNotificationDialog();
//				}
//				
//			}
//		});

		/** button blinking actions **/
//		BlinkAction buttonBlinking = new BlinkAction();
//		buttonBlinking.startBlinkAction();

		/** layoutselector button **/
//		layoutSelectorButton = new JButton(gridLayoutIcon);
//		layoutSelectorButton.setToolTipText("select tile layout");
//		layoutSelectorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				if (layoutDialogVisible == true) {
//					closeLayoutDialog();
//				} else {
//					showLayoutDialog();
//				}
//			}
//		});

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
					MediaPlayerWindow playerWindow = MediaPlayerWindow.getPlayerInstance();
					playerWindow.setVisible(true);
				}
			});
		});
		
//		eventSearchButton = new JButton(eventSearch);
//		eventSearchButton.setToolTipText("Search Event");
//		eventSearchButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				EventSearchWindow window = EventSearchWindow.getEventWindowInstance();
//				window.setVisible(true);
//			}
//		});

		this.add(playerButton);
		this.add(cameraAddButton);
//		this.add(layoutSelectorButton);
//		this.add(eventSearchButton);
//		this.add(eventNotificationButton);

	}

	/**
	 * show notification dialog
	 */
//	public void showNotificationDialog() {
//		if(notificationDialog == null) {
//			this.notificationDialogVisible = true;
//			notificationDialog = new EventNotificationDialog(this);
//			notificationDialog.updateEventList(eventStore);
//			
//			Point buttonLocation = eventNotificationButton.getLocationOnScreen();
//			notificationDialog.show();
//			
//			int toolbarEndX = this.getLocationOnScreen().x + this.getWidth();
//			int notificationDialogX = notificationDialog.getLocationOnScreen().x + notificationDialog.getWidth();
//			notificationDialog.setLocation(toolbarEndX - notificationDialogX, 
//									buttonLocation.y + eventNotificationButton.getHeight() +10);
//		}
//	}

	/**
	 * Adds events to event store
	 * 
	 * @param session
	 */
//	public void eventGenerated(EventSession session) {
//		eventGeneratedCount++;
//		eventStore.add(session);
//		if(notificationDialogVisible && !eventStore.isEmpty() && eventStore != null) {
//			notificationDialog.updateEventList(eventStore);
//		}
//	}

	/**
	 * Closes the notification dialog
	 */
//	public void closeNotificationDialog() {
//		if(notificationDialog != null) {
//			this.notificationDialogVisible = false;
//			notificationDialog.setVisible(false);
//			notificationDialog.dispose();
//			notificationDialog = null;
//		}
//	}

	/**
	 * show layout selector dialog
	 */
//	public void showLayoutDialog() {
//		if (layoutDialog == null) {
//			this.layoutDialogVisible = true;
//			layoutDialog = new LayoutSelectorDialog(this);
//			Point buttonLocation = layoutSelectorButton.getLocationOnScreen();
//			layoutDialog.show();
//
//			layoutDialog.setLocation(buttonLocation.x, buttonLocation.y + layoutSelectorButton.getHeight() + 10);
//		}
//
//	}

	/**
	 * Closes the layout dialog
	 */
//	public void closeLayoutDialog() {
//		if (layoutDialog != null) {
////			this.layoutDialogVisible = false;
//			layoutDialog.setVisible(false);
//			layoutDialog.dispose();
//			layoutDialog = null;
//		}
//	}

	/**
	 * This class for generating blink icon
	 * 
	 * @author dss-02
	 *
	 */
//	class BlinkAction extends Thread {
//
//		public void startBlinkAction() {
//			this.start();
//		}
//
//		@Override
//		public void run() {
//			while(true) {
//				try {
//					if(eventGeneratedCount > 0) {
//						eventNotificationButton.setIcon(eventGenerated);
//						eventNotificationButton.setToolTipText(eventGeneratedCount+" Events Generated");
//						eventNotificationButton.setText(eventGeneratedCount+" Events Generated");
//					}else {
//						eventNotificationButton.setIcon(eventBlink);
//					}
//					Thread.sleep(1000);
//					eventNotificationButton.setIcon(eventDefault);
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}
}
