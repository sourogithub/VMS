package com.dss.vms.common.data;

import java.io.Serializable;

import com.dss.vms.common.constants.AuditLog;

public final class AuditSession implements Serializable {
	private AuditLog auditType;
	private String description;
	private long timestamp;

	/**
	 * @param auditType
	 * @param description
	 * @param time
	 */
	public AuditSession(AuditLog auditType, String description, Long time) {
		this.auditType = auditType;
		this.description = description;
		this.timestamp = time;
	}

	public AuditSession() {}

	/**
	 * @return the auditType
	 */
	public AuditLog getAuditType() {
		return auditType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the time
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param auditType the auditType to set
	 */
	public void setAuditType(AuditLog auditType) {
		this.auditType = auditType;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param timestamp the time to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
