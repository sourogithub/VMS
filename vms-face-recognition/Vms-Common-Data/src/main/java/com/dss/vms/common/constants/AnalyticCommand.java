package com.dss.vms.common.constants;

public enum AnalyticCommand {
	FR_TRAIN_DATASET((short) 00),
	FR_DELETE_DATASET((short) 01),
	FR_MODIFY_DATASET((short) 02);

	private short value;

	/**
	 * @param mediaId
	 */
	AnalyticCommand(short value) {
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
	public static AnalyticCommand find(short value) {
		for (AnalyticCommand type : AnalyticCommand.values()) {
			if (type.value == value)
				return type;
		}
		return null;
	}

}
