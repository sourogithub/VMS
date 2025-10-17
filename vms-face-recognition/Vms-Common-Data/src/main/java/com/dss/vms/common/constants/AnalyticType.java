package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum AnalyticType {

	FACE((short) 1), INTRUDER((short) 2);

	private short value;

	/**
	 * @param mediaId
	 */
	AnalyticType(short value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public short value() {
		return value;
	}

	/**
	 * @param stringName
	 * @return
	 */
	public static AnalyticType find(short value) {
		for (AnalyticType type : AnalyticType.values()) {
			if (type.value == value)
				return type;
		}
		return null;
	}

}
