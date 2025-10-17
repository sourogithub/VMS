package com.dss.vms.common.constants;

/**
 * Contains Auditing types
 * 
 * @author dss-02
 *
 */
public enum AuditLog {
	CAMERA_ADD(1, "Camera Added"),
	CAMERA_DELETE(2, "Camera Deleted"),
	SERVER_WAKEUP(3, "Server Wakeup"),
	SERVER_SLEEP(4, "Server shutdown"),
	RECORD_DELETE(5, "Record Deleted");

	private String message;
	private Integer id;

	/**
	 * @param description
	 * @param id
	 */
	private AuditLog(Integer id, String description) {
		this.message = description;
		this.id = id;
	}

	/**
	 * @return the auditDescription
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the auditID
	 */
	public Integer getID() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static AuditLog find(int id) {
		AuditLog type = null;
		for (AuditLog currentType : AuditLog.values()) {
			if (currentType.getID() == id) {
				type = currentType;
			}
		}
		return type;
	}
}
