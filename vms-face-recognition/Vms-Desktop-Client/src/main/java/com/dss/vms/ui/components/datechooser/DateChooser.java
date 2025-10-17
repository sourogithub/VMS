package com.dss.vms.ui.components.datechooser;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dss.vms.ui.constants.Icons;

/**
 * Hello world!
 *
 */
public class DateChooser extends JPanel implements Icons {
	private static final int PANEL_HEIGHT = 30;
	private static final int PANEL_WIDTH = 70;

	private CalendarDialog calDialog;
	private JTextField dateDisplay;
	private JButton datePicker;
	private boolean isDialogShowing = false;

	public DateChooser() {
//		this.setMinimumSize(dimension);
//		this.setPreferredSize(dimension);
		createUI();
	}

	/**
	 * creates the UI
	 */
	private void createUI() {
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		dateDisplay = new JTextField(5);
		datePicker = new JButton(calendarIcon);
		datePicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				if (isDialogShowing == false) {
					isDialogShowing = true;
					calDialog = new CalendarDialog(DateChooser.this);
					Point currentButtonLoc = dateDisplay.getLocationOnScreen();
					calDialog.setLocation(currentButtonLoc.x, currentButtonLoc.y + dateDisplay.getHeight());
					calDialog.show();
				} else {
					disposeDialog();
				}
			}
		});
		this.add(dateDisplay);
		this.add(datePicker);
	}

	/**
	 * setText of display
	 * 
	 * @param finalText
	 */
	public void setDisplayText(String finalText) {
		if (dateDisplay != null) {
			dateDisplay.setText(finalText);
		}
	}

	/**
	 * get displayed text
	 * 
	 * @return
	 */
	public String getDisplayText() {
		return dateDisplay.getText();
	}

	/**
	 * set calendar dialog as modal
	 * 
	 * @param value
	 */
	public void setDialogModal(boolean value) {
		if (calDialog != null) {
			calDialog.setModal(value);
		}
	}

	public void setDialogOnTop(boolean value) {
		if (calDialog != null) {
			calDialog.setAlwaysOnTop(value);
		}
	}

	/**
	 * for disposing the dialog
	 * 
	 */
	public void disposeDialog() {
		if (calDialog != null) {
			isDialogShowing = false;
			calDialog.setVisible(false);
			calDialog.dispose();
		}
	}
}
