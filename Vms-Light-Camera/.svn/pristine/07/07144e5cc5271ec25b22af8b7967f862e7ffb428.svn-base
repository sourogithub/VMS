package com.dss.vms.common.constants;

/**
 * @author jdeveloper
 */
public enum MediaType {

	JPEG((byte) 0), MJPEG((byte) 1), MPEG4((byte) 2), H264((byte) 3), H265((byte) 4);

	private byte value;

	/**
	 * @param mediaId
	 */
	MediaType(byte value) {
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
	public static MediaType find(byte value) {
		for (MediaType mediaType : MediaType.values()) {
			if (mediaType.value == value)
				return mediaType;
		}
		return null;
	}

}
