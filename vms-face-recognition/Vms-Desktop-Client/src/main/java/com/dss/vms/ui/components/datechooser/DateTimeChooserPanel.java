package com.dss.vms.ui.components.datechooser;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dss.vms.ui.constants.Icons;

public class DateTimeChooserPanel extends JPanel implements Icons {
	private static final int DEFAULT_DISPLAYFIELD_WIDTH = 12;
	
	private boolean isDialogOpen = false;
	private JButton picker;
	private JTextField display;
	private DateTimePopup choiceDialog = null;
	private ChangeListener changeListener = null;
	
	public DateTimeChooserPanel() {
		this.picker = new JButton("Select Date/Time");
		this.picker.setIcon(calendarIcon);
		picker.addActionListener(e -> {
			if (this.isDialogOpen) closeDialog();
			else showDialog();
		});
		
		this.display = new JTextField(DEFAULT_DISPLAYFIELD_WIDTH);
		this.display.setEditable(false);
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		this.add(display);
		this.add(picker);
		this.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent move) {
				if (isDialogOpen) {
					Point currentButtonLocation = picker.getLocationOnScreen();
					choiceDialog.setLocation(currentButtonLocation.x, currentButtonLocation.y + picker.getHeight());
				}
			}
		});
	}
	
	public void setDisplayFieldSize(int size) {
		this.display.setColumns(size);
		this.repaint();
	}
	
	public void addChangeListener(ChangeListener listener) {
		this.changeListener = listener;
	}

	/**
	 * @param dateTime
	 */
	public void setDisplayDate(String dateTime) {
		display.setText(dateTime);
		if(changeListener != null) changeListener.stateChanged(new ChangeEvent(this));
	}
	
	/**
	 * set Default Text
	 * @param dateTime
	 */
	public void setDefaultText(String dateTime) {
		display.setText(dateTime);
	}

	/**
	 * @return the display text
	 */
	public String getDisplayText() {
		return display.getText();
	}

	/**
	 * set Visibility of popup
	 * 
	 * @param isVisible
	 */
	public void closeDialog() {
		if (choiceDialog != null) {
			choiceDialog.setVisible(false);
			choiceDialog.dispose();
			choiceDialog = null;
			isDialogOpen = false;
		}
	}

	public void showDialog() {
		if (choiceDialog == null) {
			isDialogOpen = true;
			choiceDialog = new DateTimePopup(this);
			Point currentButtonLocation = picker.getLocationOnScreen();
			choiceDialog.setLocation(currentButtonLocation.x, currentButtonLocation.y + picker.getHeight());
			choiceDialog.setVisible(true);
		}
	}

}
