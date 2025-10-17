package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum ColorSpace {

	ARGB((byte) 0);

	private byte value;

	/**
	 * @param mediaId
	 */
	ColorSpace(byte value) {
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
	public static ColorSpace find(byte value) {
		for (ColorSpace mediaType : ColorSpace.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
