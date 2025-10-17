package com.dss.vms.ui.components.analytics.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.data.CameraBucket;

public class EventSearchListRenderer extends DefaultListCellRenderer implements Icons {
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
		label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createLineBorder(Color.GRAY, 2, true)));
		// setting font size
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		label.setForeground(Color.BLACK);

		if (isSelected) {
			label.setForeground(Color.white);
			label.setBackground(Color.DARK_GRAY);
			label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
					BorderFactory.createLineBorder(Color.WHITE, 2, true)));
		}
		
		if (value != null) {
			if(value instanceof GenericEvent) {
				GenericEvent event = (GenericEvent) value;
				
				String time = "time : " + new Date(event.getTimestamp());
				String labelText;
				//searching for camera 
				int channelID = event.getChannelID();
				VideoCamera camera = cameraBucket.searchChannelID(channelID);

				if (camera != null) {
					labelText = "Camera : " + camera.getName();
				}
				
				switch (event.getType()) {
				case FACE:
					label.setIcon(face_icon);
					
					FaceRecognitionEvent faceEvent = (FaceRecognitionEvent) event;
					Employee employee = faceEvent.getEmployee();
					
					labelText = "Face Detection Event , Employee : " + employee.getEmployeeName();
					labelText += " " + time;

					label.setText(labelText);
					break;
					
				case INTRUDER: 
					label.setIcon(recorded_clip_icon);
					
					labelText = "Intruder Detection Event , " + time;
					label.setText(labelText);
					
					break;
				}
				
				label.setToolTipText(time);
			}
		}
		
		return label;
	}
}