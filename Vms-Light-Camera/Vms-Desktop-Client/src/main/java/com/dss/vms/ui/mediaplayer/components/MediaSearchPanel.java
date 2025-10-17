package com.dss.vms.ui.mediaplayer.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.components.datechooser.DateTimeChooserPanel;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.constants.VmsIcons;
import com.dss.vms.ui.mediaplayer.actions.CameraDropdownRenderer;
import com.dss.vms.ui.mediaplayer.actions.SearchRecordActionListener;

/**
 * 
 * @author dss-02
 *
 */
public class MediaSearchPanel extends JPanel implements VmsIcons {
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 60;
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private SessionManager sessionManager = SessionManagerImpl.getInstance();

	private DateTimeChooserPanel startDTChooser;
	private DateTimeChooserPanel endDTChooser;
	private JComboBox<Object> cameraList;
	private SearchRecordActionListener searchAction;

	public MediaSearchPanel(SearchRecordActionListener searchAction) {
		this.searchAction = searchAction;
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 10));

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		this.setLayout(new BorderLayout(3, 3));

		JLabel lblModelName = new JLabel("Camera: ");
		lblModelName.setForeground(VmsComponentColor.LABEL_COLOR);

		cameraList = new JComboBox<>();
		cameraList.setRenderer(new CameraDropdownRenderer());
		cameraList.addItem("Select Camera");
//		cameraList.addActionListener(e-> { searchRecord(); });
		
		/** getting cameras from Session-Manager **/
		VmsResponse response = sessionManager.getAllCameras();
		if (response.isSuccess() && response.getResponse() != null) {
			Map<Integer, VideoCamera> cameraMap = (Map<Integer, VideoCamera>) response.getResponse();
			for (VideoCamera camera : cameraMap.values()) {
				cameraList.addItem(camera);
			}
			//TODO: get deleted cameras from record-database 
		}

		this.startDTChooser = new DateTimeChooserPanel();
//		this.startDTChooser.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent event) { searchRecord(); }
//		});
		this.endDTChooser = new DateTimeChooserPanel();
//		this.endDTChooser.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent event) { searchRecord(); }
//		});
		/** setting default date time **/
		setDefaultDateTime();
		
		JLabel lblStart = new JLabel("Start:");
		lblStart.setForeground(VmsComponentColor.LABEL_COLOR);

		JLabel lblEnd = new JLabel("End:");
		lblEnd.setForeground(VmsComponentColor.LABEL_COLOR);

		JButton search = new JButton("Search");
		search.setToolTipText("Search Record");
		search.setIcon(searchIcon);
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) { searchRecord(); }
		});

		headerPanel.add(lblModelName);
		headerPanel.add(cameraList);
		headerPanel.add(lblStart);
		headerPanel.add(startDTChooser);
		headerPanel.add(lblEnd);
		headerPanel.add(endDTChooser);
		headerPanel.add(search);
		add(headerPanel, BorderLayout.CENTER);
	}
	
	
	
	public void searchRecord() {
		try {
			Object selectedItem = cameraList.getSelectedItem();
			if (selectedItem instanceof VideoCamera) {
				VideoCamera selectedCam = (VideoCamera) selectedItem;
				Long startTime = DATE_TIME_FORMAT.parse(startDTChooser.getDisplayText()).getTime();
				Long endTime = DATE_TIME_FORMAT.parse(endDTChooser.getDisplayText()).getTime();
				if ((endTime - startTime) >= 0) {
					VmsResponse response = sessionManager.fetchRecords(selectedCam.getId(), startTime, endTime);
					if (response.isSuccess() && response.getResponse() != null) {
						ArrayList<RecordSession> recordSessions = (ArrayList<RecordSession>) response.getResponse();
						searchAction.searchRecordsFound(recordSessions);
					}
				} else {
					showDialog("Invalid DateTime Range Specified", "Error");
				}
			} else {
				showDialog("No Camera selected", "Error");
			}
		} catch (Exception e) {
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
