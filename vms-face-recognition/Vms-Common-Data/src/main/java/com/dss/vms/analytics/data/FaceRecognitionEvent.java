package com.dss.vms.analytics.data;

import com.dss.vms.common.constants.AnalyticType;

public class FaceRecognitionEvent extends GenericEvent {

	private Employee employee;
	private boolean recognised = false;
	
	public FaceRecognitionEvent(long timestamp, boolean recognised) {
		super(timestamp, AnalyticType.FACE);
		this.recognised = recognised;
	}
	
	public boolean isRecognised() {
		return recognised;
	}

	public void setEmployee(Employee personData) {
		if(personData.getEmployeeId().equals(Employee.NOT_AVAILABLE)) {
			this.recognised = false;
		} else {
			recognised = true;
		}
		
		this.employee = personData;
	}

	public Employee getEmployee() {
		return employee;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8321061581131525815L;

	@Override
	public String toString() {
		return "FaceRecognitionEvent [personData=" + employee + ", recognised=" + recognised + "]";
	}
}
