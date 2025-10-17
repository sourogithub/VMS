package com.dss.vms.ui.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.plaf.BorderUIResource.TitledBorderUIResource;

import com.dss.vms.view.panel.ImageTile;

/**
 * Camera Lookup table is mapped with {@linkplain ImageTileBucket} as
 * {current channel ID : current tile index}
 * 
 * @author dss-02
 *
 */
public class CameraLookupTable implements Serializable {
	private static final String CHANNEL_MAP_FILE = "channel_map.xml";
	
	public static final int NO_OF_TILES = ImageTileBucket.NO_OF_TILES;
	
	public static final int INVALID_CHANNEL = -1;
	
	/** contains the Lookup entry for channel **/
	private static CameraLookupTable INSTANCE  = null;
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	
	/* <PanelIndex : CAMERAID> */
	private Integer[] channels = null; 
	
	/**
	 * @return map
	 */
	public static CameraLookupTable getInstance() {
		synchronized (CameraLookupTable.class) {
			if (INSTANCE == null) {
				INSTANCE = new CameraLookupTable();
			}
		}
		return INSTANCE;
	}
	private CameraLookupTable() {
		/** loading from saved data **/
		Object object = deserializeFromXML(CHANNEL_MAP_FILE);
		if (object != null && object instanceof Integer[]) {
			channels = (Integer[]) object;
		} else {
			channels = new Integer[NO_OF_TILES];

			for (int index = 0; index < NO_OF_TILES; index++) {
				channels[index] = INVALID_CHANNEL;
			}
		}
	}

	/**
	 * Append Channel to map
	 * 
	 * @param data
	 * @param index
	 */
	public void setChannel(int data, int index) {
		channels[index] = data;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public int getChannel(int index) {
		return channels[index];
	}

	private BufferedImage cloneImages(BufferedImage image) {
		BufferedImage clone = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		Graphics2D g2d = clone.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return clone;
	}

	/**
	 * Swap The Channels
	 * 
	 * @param source
	 * @param dest
	 */
	public void swapEntry(ImageTile source, ImageTile dest) {
		synchronized (CameraLookupTable.class) {
			BufferedImage sourceImage = cloneImages(source.getBufferedImage());
			BufferedImage destImage = cloneImages(dest.getBufferedImage());

			int srcChannelIndex = ImageTileBucket.find(source);
			int destChannelIndex = ImageTileBucket.find(dest);

			if ((srcChannelIndex != INVALID_CHANNEL) && (destChannelIndex != INVALID_CHANNEL)) {
				int tempChannel = channels[srcChannelIndex];
				this.setChannel(channels[destChannelIndex], srcChannelIndex);
				this.setChannel(tempChannel, destChannelIndex);
			}
			try { Thread.sleep(20); } catch (InterruptedException e) {}
			tileBucket.getTile(srcChannelIndex).setBufferedImage(destImage);
			tileBucket.getTile(destChannelIndex).setBufferedImage(sourceImage);
		}
	}
	
	/**
	 * saved contents of channels in channel_map.xml
	 */
	public void closeMappingTable() {
		serializateToXML(channels, CHANNEL_MAP_FILE);
	}
	
	private static Object deserializeFromXML(String fileName) {
		XMLDecoder decoder = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		Object objToReturn = null;
		File inputFile = new File(fileName);

		if (inputFile.exists()) {
			try {
				fis = new FileInputStream(fileName);
				bis = new BufferedInputStream(fis);
				decoder = new XMLDecoder(bis);
				objToReturn = decoder.readObject();
				return objToReturn;
			} catch (Exception e) {}
			finally {
				if (fis != null) {
					try {fis.close();} catch (IOException e) {}
				}
				if (bis != null) {
					try {bis.close();} catch (IOException e) {}
				}
				if (decoder != null) {
					try {decoder.close();} catch (Exception e) {}
				}
			}
		}
		return null;
	}
	
	private static boolean serializateToXML(Object objectToDump, String fileName) {
		FileOutputStream fos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(fileName);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(objectToDump);
			fos.flush();
			return true;
		} catch (Exception e) {}
		finally {
			if (encoder != null) {
				try {encoder.close();} catch (Exception e) {}
			}
			if (fos != null) {
				try {fos.close();} catch (IOException e) {}
			}
		}
		return false;
	}
}
