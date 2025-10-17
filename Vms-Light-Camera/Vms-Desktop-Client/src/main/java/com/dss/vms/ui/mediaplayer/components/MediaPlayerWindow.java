package com.dss.vms.ui.mediaplayer.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.constants.VmsIcons;
import com.dss.vms.ui.mediaplayer.actions.SearchRecordActionListener;

public class MediaPlayerWindow extends JDialog implements VmsIcons {
	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 1200;

	private MediaPlayerContainer mediaContainer;
	/**
	 * 
	 */
	public MediaPlayerWindow() {
		setIconImage(vms_icon.getImage());
		setTitle("Record Viewer");
		setBackground(VmsComponentColor.IMAGETILE_BG);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setMaximumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent closing) {
				MediaPlayerWindow window = (MediaPlayerWindow) closing.getSource();
				window.dispose();
				mediaContainer.shutdown();
				WINDOW_INSTANCE = null;
			}
		});
		setResizable(false);
		setAlwaysOnTop(true);
		drawUIComponents();
		setVisible(true);
	}

	private void drawUIComponents() {
		mediaContainer = new MediaPlayerContainer();
		MediaSearchPanel headingPanel = new MediaSearchPanel(new SearchRecordActionListener() {
			@Override
			public void searchRecordsFound(List<RecordSession> records) {
				mediaContainer.updateList(records);
			}
		});

		setLayout(new BorderLayout(5, 5));
		add(headingPanel, BorderLayout.NORTH);
		add(mediaContainer, BorderLayout.CENTER);
	}

	private static MediaPlayerWindow WINDOW_INSTANCE = null;
	/**
	 * Get player instance
	 * 
	 * @return
	 */
	public static MediaPlayerWindow getPlayerInstance() {
		if (WINDOW_INSTANCE == null) {
			WINDOW_INSTANCE = new MediaPlayerWindow();
		}
		return WINDOW_INSTANCE;
	}
}
