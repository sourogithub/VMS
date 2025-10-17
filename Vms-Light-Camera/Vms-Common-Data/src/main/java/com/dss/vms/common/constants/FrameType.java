package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum FrameType {

	H_FRAME((byte) 0), I_FRAME((byte) 1), P_FRAME((byte) 2), B_FRAME((byte) 3);

	private byte value;

	/**
	 * @param mediaId
	 */
	FrameType(byte value) {
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
	public static FrameType find(byte value) {
		for (FrameType mediaType : FrameType.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
