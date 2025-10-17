package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum RegionDirection {

	BOTH((byte) 0), FORWARD((byte) 1), REVERSE((byte) 2);

	private byte value;

	/**
	 * @param mediaId
	 */
	RegionDirection(byte value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public byte value() {
		return value;
	}

	/**
	 * @param stringName
	 * @return
	 */
	public static RegionDirection find(byte value) {
		for (RegionDirection mediaType : RegionDirection.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
