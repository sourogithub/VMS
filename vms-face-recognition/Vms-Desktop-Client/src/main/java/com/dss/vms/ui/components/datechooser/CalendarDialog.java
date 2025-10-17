package com.dss.vms.ui.components.datechooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dss.vms.ui.utility.GraphicsInfo;

/**
 * 
 * @author Sibendu-PC
 *
 */
public class CalendarDialog extends JDialog {
	private static final int DIALOG_WIDTH = 500;
	private static final int DIALOG_HEIGHT = 250;
	private static final Dimension DEFAULT_BUTTON_DIMENSION = new Dimension(40, 30);
	private static final Dimension dimension = new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT);
	public static final String MONTHS[] = { "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST",
			"SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER" };
	private static final String DAYS[] = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY",
			"SATURDAY" };

	private static GregorianCalendar gregCalendar = new GregorianCalendar();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");

	private MonthAndYearPanel monthYearSelectionPanel;
	private JPanel calendarPanel;
	private String currentMonth;
	private String currentYear;
	private String finalDate;
	private DateChooser parent;

	/**
	 * 
	 * @param parent
	 */
	public CalendarDialog(DateChooser parent) {
		this.parent = parent;
		this.setMinimumSize(dimension);
		this.setPreferredSize(dimension);
		this.setSize(dimension);
		this.setBackground(Color.WHITE);
		this.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				parent.disposeDialog();
			}
		});
		this.setResizable(false);
		this.setUndecorated(true);
		if (GraphicsInfo.translucencySupported()) {
			this.setOpacity(0.9f);
		}
		this.setAlwaysOnTop(true);
		this.requestFocus();
		this.createUI();
		currentMonth = MONTHS[gregCalendar.get(Calendar.MONTH)];
		updateChange(gregCalendar.get(Calendar.YEAR), currentMonth);
	}

	private void createUI() {
		this.setLayout(new BorderLayout());
		monthYearSelectionPanel = new MonthAndYearPanel();
		this.add(monthYearSelectionPanel, BorderLayout.NORTH);
		calendarPanel = new JPanel(new GridLayout(7, 7, 2, 2));
		// calendarPanel.setBackground(Color.DARK_GRAY);
		this.add(calendarPanel, BorderLayout.CENTER);
	}

	/**
	 * change update
	 * @param year
	 * @param month
	 */
	public void updateChange(Integer year, String month) {
		int monthIndex = findMonth(month);
		gregCalendar.set(year, monthIndex, 1); /* default date setting 1 */
		int numberOfDays = gregCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String day = dateFormat.format(gregCalendar.getTime());
		int dayIndex = findDay(day);
		currentMonth = month;
		currentYear = year.toString();
		buildCalendar(numberOfDays, dayIndex);
	}
	
	/**
	 * Month and Year Panel
	 * @author SIBENDU
	 *
	 */
	private class MonthAndYearPanel extends JPanel {
		private static final int PANEL_WIDTH = 300;
		private static final int PANEL_HEIGHT = 30;

		private Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		private SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(calendar.get(Calendar.YEAR), 1995, 2030, 1);

		private JSpinner monthSpinner;
		private JSpinner yearSpinner;
		private JComboBox<String> monthChooser;

		public MonthAndYearPanel() {
			Dimension panel_dimension = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
			this.setMinimumSize(panel_dimension);
			this.setPreferredSize(panel_dimension);
			this.setSize(panel_dimension);
			this.setBackground(Color.BLACK);
			createUI();
		}

		public void createUI() {
			this.setLayout(new FlowLayout(FlowLayout.CENTER));
			monthChooser = new JComboBox<String>(CalendarDialog.MONTHS);
			monthChooser.setSelectedIndex(calendar.get(Calendar.MONTH));
			monthChooser.addActionListener(e -> {
				String value = (String) monthChooser.getSelectedItem();
				monthSpinner.getModel().setValue(value);
			});

			monthSpinner = new JSpinner();
			monthSpinner.setEditor(monthChooser);
			monthSpinner.setModel(new SpinnerListModel(CalendarDialog.MONTHS));
			monthSpinner.setValue(CalendarDialog.MONTHS[calendar.get(Calendar.MONTH)]);
			monthSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					String value = (String) monthSpinner.getModel().getValue();
					monthChooser.setSelectedItem(value);
					int year = (Integer) yearSpinner.getValue();
					updateChange(year, value);
				}
			});

			yearSpinner = new JSpinner(spinnerNumberModel);
			yearSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					String value = (String) monthSpinner.getModel().getValue();
					monthChooser.setSelectedItem(value);
					int year = (Integer) yearSpinner.getValue();
					updateChange(year, value);
				}
			});
			this.add(monthSpinner);
			this.add(yearSpinner);

		}
	}

	/**
	 * Builds the calendar buttons
	 * 
	 * @param noOfDays
	 * @param dayIndex
	 */
	private void buildCalendar(int noOfDays, int dayIndex) {
		calendarPanel.removeAll();
		calendarPanel.setLayout(new GridLayout(7, 7, 2, 2));
		int countButtons = 0;
		
		for (int dayHeader = 0; dayHeader < DAYS.length; dayHeader++) {
			JButton headerButton = new JButton(DAYS[dayHeader]);
			headerButton.setForeground(Color.RED);
			headerButton.setBackground(Color.WHITE);
			headerButton.setEnabled(false);
			headerButton.setSize(DEFAULT_BUTTON_DIMENSION);
			calendarPanel.add(headerButton);
			countButtons++;
		}

		/** adding blank dates for DateMarker **/
		for (int blankDateCount = 0; blankDateCount < dayIndex; blankDateCount++) {
			JButton blankButton = new JButton("");
			blankButton.setEnabled(false);
			blankButton.setSize(DEFAULT_BUTTON_DIMENSION);
			calendarPanel.add(blankButton);
			countButtons++;
		}

		/** adding date buttons **/
		for (int dayBtnIndex = 1; dayBtnIndex <= noOfDays; dayBtnIndex++) {
			JButton dateButton = new JButton("" + dayBtnIndex);
			dateButton.addActionListener(e -> {
				String currentDate = ((JButton) e.getSource()).getText();
				finalDate = currentYear + "/" + (findMonth(currentMonth) + 1) + "/" + currentDate;
				parent.setDisplayText(finalDate);
				parent.disposeDialog();
			});
			dateButton.setSize(DEFAULT_BUTTON_DIMENSION);
			calendarPanel.add(dateButton);
			countButtons++;
		}

		// adding more blankbuttons to fill the gap
		if (countButtons < 49) {
			for (int i = 0; i < 49 - countButtons; i++) {
				JButton blankButton = new JButton("");
				blankButton.setEnabled(false);
				blankButton.setSize(DEFAULT_BUTTON_DIMENSION);
				calendarPanel.add(blankButton);
			}
		}
		calendarPanel.validate();
		calendarPanel.repaint();
	}


	/**
	 * find index for the month
	 * 
	 * @param month
	 * @return
	 */
	private static int findMonth(String month) {
		int found = -1;
		for(int index = 0; index < MONTHS.length; index++) {
			if(month.contentEquals(MONTHS[index])) {
				found = index;
				break;
			}
		}
		return found;
	}

	/**
	 * find index for the Days
	 * 
	 * @param month
	 * @return
	 */
	private static int findDay(String day) {
		int found = -1;
		for(int index = 0; index < DAYS.length; index++) {
			if(day.contentEquals(DAYS[index])) {
				found = index;
				break;
			}
		}
		return found;
	}

}
