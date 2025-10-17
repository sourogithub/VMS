package com.dss.vms.ui.components;

import javax.swing.JLabel;

import com.dss.vms.ui.constants.ComponentColors;

public class FormLabel extends JLabel {
	public FormLabel(String text) {
		super(text);
		setForeground(ComponentColors.LABEL_COLOR);
	}
}
