package com.dss.vms.ui.components.datechooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.dss.vms.ui.constants.ComponentColors;

public class DateTimePopup extends JDialog implements WindowFocusListener {
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 70;
	private static final SpinnerNumberModel HOUR_MODEL = new SpinnerNumberModel(0, 0, 24, 1);
	private static final SpinnerNumberModel MIN_MODEL = new SpinnerNumberModel(0, 0, 59, 1);
	private static final SpinnerNumberModel SEC_MODEL = new SpinnerNumberModel(0, 0, 59, 1);

	private DateTimeChooserPanel parent;

	public DateTimePopup(DateTimeChooserPanel parent) {
//		this.setModal(true);
//		this.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		/** allowing the popup to block all application input until closed **/
//		this.setModalityType(ModalityType.APPLICATION_MODAL);	
		this.parent = parent;
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setBackground(Color.GRAY);

		createUI();
		this.setFocusable(true);
		this.requestFocus();
		this.addWindowFocusListener(this);
		this.pack();
	}

	private void createUI() {
		this.setLayout(new BorderLayout(2, 2));
		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
	
		DateChooser dateChooser = new DateChooser();

		JLabel lblDate = new JLabel("Select Date");
		lblDate.setForeground(ComponentColors.LABEL_COLOR);

		JLabel lbltime = new JLabel("Select Time");
		lbltime.setForeground(ComponentColors.LABEL_COLOR);

		JLabel lblseperator = new JLabel(":");
		lblseperator.setForeground(ComponentColors.LABEL_COLOR);

		JSpinner hourSpinner = new JSpinner(HOUR_MODEL);
		JSpinner minSpinner = new JSpinner(MIN_MODEL);
		JSpinner secSpinner = new JSpinner(SEC_MODEL);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(action -> {
			String timeToSet = hourSpinner.getValue() + ":" + minSpinner.getValue() + ":" + secSpinner.getValue();
			String dateToset = dateChooser.getDisplayText();
			if (!dateToset.equals("")) {
				parent.setDisplayDate(dateToset + " " + timeToSet);
			} else {
				JPanel msgPanel = new JPanel();
				JLabel message = new JLabel("Date Should not be Empty");
				message.setForeground(Color.WHITE);
				msgPanel.add(message);
				JOptionPane.showMessageDialog(null, msgPanel, "Invalid input", JOptionPane.PLAIN_MESSAGE);
			}
			parent.closeDialog();
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e-> { parent.closeDialog(); });

		header.add(lblDate);
		header.add(dateChooser);
		header.add(lbltime);
		header.add(hourSpinner);
		header.add(lblseperator);
		header.add(minSpinner);
		header.add(lblseperator);
		header.add(secSpinner);
		bottom.add(okButton);
		bottom.add(cancelButton);
		this.add(header, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);
	}

	@Override
	public void windowGainedFocus(WindowEvent wGainedFocus) {}

	@Override
	public void windowLostFocus(WindowEvent wLostFocus) {
		if (!(wLostFocus.getOppositeWindow() instanceof CalendarDialog)) {
			parent.closeDialog();
		}
	}

}
