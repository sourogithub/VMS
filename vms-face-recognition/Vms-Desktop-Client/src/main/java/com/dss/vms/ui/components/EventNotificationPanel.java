package com.dss.vms.ui.components;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.ui.components.analytics.components.EventNotificationRenderer;
import com.dss.vms.ui.constants.ComponentColors;
/**
 * 
 * @author dss-02
 *
 */
public class EventNotificationPanel extends JPanel {
	public static final int MAX_NO_OF_EVENTS = 5;
	
	private DefaultListModel<GenericEvent> eventModel = new DefaultListModel<>();
	private int eventCount = 0;
	
	public EventNotificationPanel() {
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JList<GenericEvent> eventList = new JList<>();
		eventList.setModel(eventModel);
		eventList.setCellRenderer(new EventNotificationRenderer());
		eventList.setBackground(ComponentColors.TREE_BG);

		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().add(eventList);
		scrollPane.getViewport().setBackground(ComponentColors.TREE_BG);

		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * @param events
	 */
	public void eventGenerated(GenericEvent...events) {
//		if(eventCount >= MAX_NO_OF_EVENTS) {
//			eventModel.removeAllElements();
//			eventCount = 1;
//		}
		
		for(GenericEvent evnt : events) {
			eventModel.addElement(evnt);
		}
		
		eventCount += events.length;
	}
	
}
