package com.dss.vms.ui.mediaplayer.actions;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.ui.constants.VmsIcons;

public class RecordRenderer extends DefaultListCellRenderer implements VmsIcons {
	private static DateFormat dateFormat = DateFormat.getDateTimeInstance();
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null) {
			if (value instanceof RecordSession) {
				RecordSession record = (RecordSession) value;
				String startTime = "N/A";
				String endTime = "N/A";
				
				try {
					Date startDate = new Date(record.getStartTime());
					startTime = dateFormat.format(startDate);
					endTime = dateFormat.format(new Date(record.getEndTime()));
					String label = "Record [" + startTime + "] - [" + endTime + "]";
					renderer.setText(label);
					renderer.setToolTipText(label);

					renderer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
							BorderFactory.createLineBorder(Color.GRAY, 2, true)));
					renderer.setIcon(recorded_clip_icon);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (isSelected) {
					renderer.setForeground(Color.white);
					renderer.setBackground(Color.DARK_GRAY);
					renderer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
							BorderFactory.createLineBorder(Color.WHITE, 2, true)));
				}
			}
		}
		return renderer;
	}
}