package com.dss.vms.video;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpegCodec {
	private static final Logger LOGGER = LoggerFactory.getLogger(JpegCodec.class);
	private static byte[] noVideo = null;
	private static BufferedImage noVideoImage = null;
	private static int noVideoWidth = 0;
	private static int noVideoHeight = 0;

	static {
		ByteArrayInputStream byteInput = null;
		try {
			LOGGER.info("Trying to load no Video image...");
			noVideo = Files.readAllBytes(Paths.get("res/video_unavailable.jpg"));
			byteInput = new ByteArrayInputStream(noVideo);
			noVideoImage = ImageIO.read(byteInput);
			if (noVideoImage != null) { 
				noVideoWidth = noVideoImage.getWidth();
				noVideoHeight =  noVideoImage.getHeight();
			}
			LOGGER.info("Video image successfully loaded...");
		} catch (IOException e1) {
			LOGGER.info("Failed to load No_Video image, Error : " + e1);
		} finally {
			if(byteInput != null) try { byteInput.close(); } catch (IOException e) {}
		}
	}
	
	private static final int TILE_4x4_X[] = {0, 1, 0, 1};
	private static final int TILE_4x4_Y[] = {0, 0, 1, 1};
	
	/**
	 * join buffered images into 4x4 dimension
	 * @param imageDimension
	 * @param images
	 * @return
	 */
	public BufferedImage joinImages(Dimension imageDimension, List<BufferedImage> images) {
		BufferedImage target = new BufferedImage(imageDimension.width, imageDimension.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) target.getGraphics();
		if(images.size() <= 4 ) {
			int imageWidth = (int) imageDimension.getWidth() / 2;
			int imageHeight = (int) imageDimension.getHeight() / 2;
			
			for(int tile = 0; tile < TILE_4x4_X.length; tile++) {
				BufferedImage bufferedImage = images.size() > tile ? images.get(tile) : noVideoImage;
				int srcX = imageWidth * TILE_4x4_X[tile];
				int srcY = imageHeight * TILE_4x4_Y[tile];
				int destX = (TILE_4x4_X[tile] + 1) * imageWidth;
				int destY = (TILE_4x4_Y[tile] + 1) * imageHeight;
				
				graphics.drawImage(bufferedImage, srcX, srcY, destX, destY, 0, 0, 
						bufferedImage.getWidth(), bufferedImage.getHeight(), null);
			}
		}
		graphics.dispose();
		return target;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public BufferedImage decode(byte[] data) {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
		BufferedImage bImage = null;
		try {
			if (byteInputStream != null) {
				bImage = ImageIO.read(byteInputStream);
				byteInputStream.close();
			}
		} catch (IOException ie) { 
			LOGGER.error("Error occured while decoding jpeg data , Error: " + ie);
		}
		return bImage;
	}
	
	public BufferedImage getNoVideoImage() {
		return noVideoImage;
	}
	
	public byte[] getNoVideoRaster() {
		return noVideo;
	}
}
