package com.dss.vms.ui.utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author dss-02
 */
public class MJPEGDecoder {

	/**
	 * @param data
	 * @return BufferedImage from byteData
	 */
	public static BufferedImage decode(byte[] data) {
		ByteArrayInputStream byteOutputStream = new ByteArrayInputStream(data);
		BufferedImage bImage = null;
		try {
			if (byteOutputStream != null) {
				bImage = ImageIO.read(byteOutputStream);
				byteOutputStream.close();
			}
		} catch (IOException ie) {}
		return bImage;
	}

}
