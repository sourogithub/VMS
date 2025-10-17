package com.dss.vms.ui.mediaplayer.actions;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.common.data.VideoCamera;

public class CameraDropdownRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null && value instanceof VideoCamera) {
			renderer.setText(((VideoCamera) value).getName());
			renderer.setBackground(Color.WHITE);
			renderer.setForeground(Color.BLACK);
			renderer.setAutoscrolls(true);
		}
		
		if(isSelected) {
			renderer.setBackground(Color.GRAY);
		}
		
		return renderer;
	}

}
