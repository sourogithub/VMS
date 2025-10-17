package com.dss.vms.ui.components.analytics.components;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.data.RecordSession;
import com.dss.vms.ui.constants.Icons;

public class EventListRenderer  extends DefaultListCellRenderer implements Icons {
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null) {
			if (value instanceof RecordSession) {
				GenericEvent event = (GenericEvent) value;
				
				try {
					Date date = new Date(event.getTimestamp());
					String label = "Event [" + date + "]";
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
