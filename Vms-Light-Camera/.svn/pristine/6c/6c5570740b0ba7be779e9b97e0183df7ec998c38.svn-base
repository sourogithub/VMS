package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum RegionType {

	LINE((byte) 0), POLYGON((byte) 1);

	private byte value;

	/**
	 * @param mediaId
	 */
	RegionType(byte value) {
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
	public static RegionType find(byte value) {
		for (RegionType mediaType : RegionType.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
