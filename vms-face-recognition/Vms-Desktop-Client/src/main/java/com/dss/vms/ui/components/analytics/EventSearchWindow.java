package com.dss.vms.ui.components.analytics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.ui.actions.SearchRecordAction;
import com.dss.vms.ui.components.analytics.components.EventSearchContainer;
import com.dss.vms.ui.components.analytics.components.EventSearchPanel;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;
/**
 * Event-Search Window is a <b> Singleton instance </b> 
 * For event searching related work.
 * @author dss-02
 *
 */
public class EventSearchWindow extends JDialog implements Icons {
	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 1200;

	private EventSearchWindow() {
		setIconImage(vms_icon.getImage());
		setTitle("Event Viewer");
		setBackground(ComponentColors.IMAGETILE_BG);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setMaximumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent closing) {
				EventSearchWindow.this.setVisible(false);
				EventSearchWindow.this.dispose();
			}
		});
		setResizable(false);
		setAlwaysOnTop(true);
		drawUIComponents();
		setVisible(true);
	}
	
	private void drawUIComponents() {
		// TODO : Add event Search container
		EventSearchContainer container = new EventSearchContainer();
		EventSearchPanel headingPanel = new EventSearchPanel(new SearchRecordAction<GenericEvent>() {
			@Override
			public void recordsFound(List<GenericEvent> records) {
				container.updateList(records);
			}
		});
		setLayout(new BorderLayout(5, 5));
		add(headingPanel, BorderLayout.NORTH);
		add(container, BorderLayout.CENTER);
	}

	private static EventSearchWindow WINDOW_INSTANCE = null;
	/**
	 * Get player instance
	 * 
	 * @return
	 */
	public static EventSearchWindow getWindowInstance() {
		if (WINDOW_INSTANCE == null) {
			WINDOW_INSTANCE = new EventSearchWindow();
		}
		return WINDOW_INSTANCE;
	}
	
	public static void main(String[] args) {
		new EventSearchWindow().show();
	}
}
