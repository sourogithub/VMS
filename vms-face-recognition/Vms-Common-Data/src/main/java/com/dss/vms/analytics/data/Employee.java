package com.dss.vms.analytics.data;

import java.io.Serializable;
import java.util.Date;

import com.dss.vms.video.data.MediaFrame;

public class Employee implements Serializable {
	public static final String NOT_AVAILABLE = "NOT_AVAILABLE";
	public static final Date AGE_NOT_AVAILABLE = new Date();
	
	private String employeeName = NOT_AVAILABLE;
	private String employeeId = NOT_AVAILABLE;
	private String employeeGender = NOT_AVAILABLE;
	private Date employeeDoB = AGE_NOT_AVAILABLE;
	private MediaFrame[] faces = null;

	public Employee() {}
	
	public Employee(String emplId) {
		this.employeeId = emplId;
	}
	
	public Employee(String emplName, String emplId, String emplGender, Date emplDob) {
		this.employeeName = emplName;
		this.employeeId = emplId;
		this.employeeGender = emplGender;
		this.employeeDoB = emplDob;
	}

	public void setFaces(MediaFrame...faces) {
		this.faces = faces;
	}

	public void setEmployeeName(String emplName) {
		this.employeeName = emplName;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public void setEmployeeGender(String employeeGender) {
		this.employeeGender = employeeGender;
	}

	public void setEmployeeDoB(Date employeeAge) {
		this.employeeDoB = employeeAge;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public String getEmployeeGender() {
		return employeeGender;
	}

	public Date getEmployeeDoB() {
		return employeeDoB;
	}

	public MediaFrame[] getFaces() {
		return faces;
	}

	@Override
	public String toString() {
		return "EmployeeData [ employeeName=" + employeeName + ", employeeId=" + employeeId
				+ ", employeeGender=" + employeeGender + ", employeeAge=" + employeeDoB + "]";
	}

}
