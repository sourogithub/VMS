package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum AuthProtocol {

	BASIC((byte) 0), BASE_64((byte) 1), MD5((byte) 2);

	private byte value;

	/**
	 * @param mediaId
	 */
	AuthProtocol(byte value) {
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
	public static AuthProtocol find(byte value) {
		for (AuthProtocol mediaType : AuthProtocol.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
