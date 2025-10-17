package com.dss.vms.ui.components.analytics.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.actions.SearchRecordAction;
import com.dss.vms.ui.components.FormLabel;
import com.dss.vms.ui.components.datechooser.DateTimeChooserPanel;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.mediaplayer.actions.ComboBoxCameraRenderer;

/**
 * 
 * @author dss-02
 *
 */
public class EventSearchPanel extends JPanel implements Icons {
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 60;
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private SessionManager sessionManager = SessionManagerImpl.getInstance();

	private DateTimeChooserPanel startDTChooser;
	private DateTimeChooserPanel endDTChooser;
	private SearchRecordAction<GenericEvent> searchAction;

	public EventSearchPanel(SearchRecordAction<GenericEvent> searchAction) {
		this.searchAction = searchAction;
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		this.setLayout(new BorderLayout(3, 3));

		this.startDTChooser = new DateTimeChooserPanel();
		this.endDTChooser = new DateTimeChooserPanel();
		/** setting default date time **/
		setDefaultDateTime();

		JButton search = new JButton("Search");
		search.setToolTipText("Search Record");
		search.setIcon(searchIcon);
		search.addActionListener(action -> searchRecord());

		headerPanel.add(new FormLabel(" Camera: "));
		headerPanel.add(new FormLabel("Start:"));
		headerPanel.add(startDTChooser);
		headerPanel.add(new FormLabel("End:"));
		headerPanel.add(endDTChooser);
		headerPanel.add(search);
		add(headerPanel, BorderLayout.CENTER);
	}
	
	
	
	public void searchRecord() {
		try {
			Long startTime = DATE_TIME_FORMAT.parse(startDTChooser.getDisplayText()).getTime();
			Long endTime = DATE_TIME_FORMAT.parse(endDTChooser.getDisplayText()).getTime();
			if ((endTime - startTime) >= 0) {
				VmsResponse response = sessionManager.getEventLog(startTime, endTime);
				if (response.isSuccess() && response.getResponse() != null) {
					Vector<GenericEvent> list = (Vector<GenericEvent>) response.getResponse();
					searchAction.recordsFound(list);
				}
			} else {
				showDialog("Invalid DateTime Range Specified", "Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showDialog("Error occured while searching Records. Please try again.", "Error");
		}
	}
	
	/**
	 * initialise start time as 12 hours before current timestamp 
	 * and end time as current timestamp
	 */
	private void setDefaultDateTime() {
		try {
			long currentTimestamp = System.currentTimeMillis();
			Date startDate = new Date(currentTimestamp - Duration.ofHours(12).toMillis());
			Date endDate = new Date(currentTimestamp);
			startDTChooser.setDefaultText(DATE_TIME_FORMAT.format(startDate));
			endDTChooser.setDefaultText(DATE_TIME_FORMAT.format(endDate));
		} catch (Exception e) {}
	}

	/**
	 * 
	 * @param message
	 * @param heading
	 */
	private void showDialog(String message, String heading) {
		JLabel label = new JLabel(message);
		label.setForeground(Color.white);
		JOptionPane.showMessageDialog(this, new JPanel().add(label), heading, JOptionPane.PLAIN_MESSAGE);
	}
	
}
