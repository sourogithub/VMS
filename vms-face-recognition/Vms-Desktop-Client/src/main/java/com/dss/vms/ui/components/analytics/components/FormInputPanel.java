package com.dss.vms.ui.components.analytics.components;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.ui.components.FormLabel;
import com.dss.vms.ui.components.datechooser.DateTimeChooserPanel;
import com.dss.vms.ui.constants.ComponentColors;
/**
 * 
 * @author dss-02
 *
 */
public class FormInputPanel extends JPanel {
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final String GENDER_OPTION[] = new String[] { "Select Gender", "Male", "Female", "Other"};
	private JTextField personName;
	private DateTimeChooserPanel personDoB;
	private JComboBox<String> personGender;
	private JTextField employeeID;
	private JButton submit;
	private JButton reset;

	public FormInputPanel() {
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY),
				BorderFactory.createEmptyBorder(10, 20, 10, 10)));
		
		setupComponents();
	}
	
	private void setupComponents() {
		this.personName = new JTextField(8);
		this.personDoB = new DateTimeChooserPanel();
		this.personDoB.setDisplayFieldSize(18);
		this.personGender = new JComboBox<>(GENDER_OPTION);
		this.employeeID = new JTextField(8);

		JPanel formContainer = new JPanel();
		formContainer.setLayout(new GridLayout(4, 2, 5, 5));
		formContainer.add(new FormLabel("Name:"));
		formContainer.add(personName);
		formContainer.add(new FormLabel("Date of Birth:"));
		formContainer.add(personDoB);
		formContainer.add(new FormLabel("Gender:"));
		formContainer.add(personGender);
		formContainer.add(new FormLabel("Employee ID:"));
		formContainer.add(employeeID);
		
		this.submit = new JButton("Submit");
		this.reset = new JButton("Reset");
		JPanel control = new JPanel(new FlowLayout());
		control.add(submit);
		control.add(reset);
		/* Drawing layout */
		setLayout(new BorderLayout(5, 5));
		add(formContainer, BorderLayout.CENTER);
		add(control, BorderLayout.SOUTH);
	}

	/**
	 * reset all fields
	 */
	public void resetAllFields() {
		personDoB.setDisplayDate("");
		personName.setText("");
		personGender.setSelectedIndex(0);
		employeeID.setText("");
	}
	
	/**
	 * Add action listener to reset button
	 * @param resetAction
	 */
	public void setResetAction(ActionListener resetAction) {
		this.reset.addActionListener(resetAction);
	}
	
	/**
	 * Add action listener to submit button
	 * @param submitAction
	 */
	public void setSubmitAction(ActionListener submitAction) {
		this.submit.addActionListener(submitAction);
	}
	
	/**
	 * get Person's Name
	 * @return
	 * @throws Exception
	 */
	public String getEmployeeName() throws Exception {
		String name = this.personName.getText();
		if(name.equals("")) throw new Exception("Person name not specified.");
		return name;
	}
	
	/**
	 * Get Person's Age
	 * @return
	 * @throws Exception
	 */
	public Date getEmployeeDoB() throws Exception {
		String dateTime = personDoB.getDisplayText();
		if(dateTime.equals("")) throw new Exception("Employee Date of Birth Incorrect");
		Date dob = DATE_TIME_FORMAT.parse(dateTime);
		return dob;
	}
	
	/**
	 * Get Employee-id
	 * @return
	 */
	public String getEmployeeId() {
		return this.employeeID.getText();
	}
	
	/**
	 * get person gender
	 * @return
	 * @throws Exception
	 */
	public String getEmployeeGender() throws Exception {
		int index = this.personGender.getSelectedIndex();
		if(index == 0) throw new Exception("Gender not selected.");
		return GENDER_OPTION[index];
	}
}
