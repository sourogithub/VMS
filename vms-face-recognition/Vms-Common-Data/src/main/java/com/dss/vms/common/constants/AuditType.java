package com.dss.vms.common.constants;

/**
 * Contains Auditing types
 * 
 * @author dss-02
 *
 */
public enum AuditType {
	CAMERA_ADD(1, "Camera Added"), CAMERA_DELETE(2, "Camera Deleted"), SERVER_WAKEUP(3, "Server Wakeup"),
	SERVER_SLEEP(4, "Server shutdown"), RECORD_DELETE(5, "Record Deleted");

	private String auditDescription;
	private Integer auditID;

	/**
	 * @param auditDescription
	 * @param auditID
	 */
	private AuditType(Integer auditID, String auditDescription) {
		this.auditDescription = auditDescription;
		this.auditID = auditID;
	}

	/**
	 * @return the auditDescription
	 */
	public String getAuditDescription() {
		return auditDescription;
	}

	/**
	 * @return the auditID
	 */
	public Integer getAuditID() {
		return auditID;
	}

	/**
	 * 
	 * @param auditId
	 * @return
	 */
	public static AuditType find(Integer auditId) {
		AuditType type = null;
		for (AuditType currentType : AuditType.values()) {
			if (currentType.getAuditID() == auditId) {
				type = currentType;
			}
		}
		return type;
	}
}
