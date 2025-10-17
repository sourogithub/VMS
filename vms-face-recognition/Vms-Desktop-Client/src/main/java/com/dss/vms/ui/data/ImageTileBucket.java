package com.dss.vms.ui.data;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.view.panel.ImageTile;

/**
 * Contains all the {@linkplain ImageTile}  
 * currently used for live view
 * @author dss-02
 *
 */
public class ImageTileBucket {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageTileBucket.class);
	public static final int FRAME_HEIGHT = 480;
	public static final int FRAME_WIDTH = 640;
	public static final int NO_OF_TILES = 36; 

	private static BufferedImage defaultImage = null;
	private static ImageTile[] tiles = null;
	private static ImageTileBucket INSTANCE = null;
	private static BufferedImage vidPrevUnavailable = null;
	
	static {
		try {
			defaultImage = ImageIO.read(new File("res/tileBackground.png"));
		} catch (Exception e) { LOGGER.error("Failed to load background image for ImageTiles..."); }
		
		try {
			vidPrevUnavailable = ImageIO.read(new File("res/video_unavailable.jpg"));
		} catch (Exception e) { LOGGER.error("Failed to No_Video_Preview_Available for ImageTiles..."); }
	}

	public static ImageTileBucket getInstance() {
		synchronized (ImageTileBucket.class) {
			if (INSTANCE == null) {
				INSTANCE = new ImageTileBucket();
			}
		}
		return INSTANCE;
	}

	private ImageTileBucket() {
		tiles = new ImageTile[NO_OF_TILES];
		
		for (int tileIndex = 0; tileIndex < NO_OF_TILES; tileIndex++) {
			Dimension defaultTileDimension = new Dimension(LiveViewPanel.getDefaultviewpanelwidth(),
					LiveViewPanel.getDefaultviewpanelheight());
			if (defaultImage == null) {
				BufferedImage bTileImage = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				tiles[tileIndex] = new ImageTile(bTileImage, defaultTileDimension);
			} else {
				Dimension dimension = new Dimension(defaultImage.getWidth(), defaultImage.getHeight());
				tiles[tileIndex] = new ImageTile(defaultImage, dimension);
			}
		}
	}

	/**
	 * 
	 * @param index
	 * @return Image tile
	 */
	public ImageTile getTile(int index) {
		if(index < 0) return null;
		if(index >= tiles.length) return null;
		return tiles[index];
	} 

	/**
	 * @return the tileBgImage
	 */
	public static BufferedImage getDefaultBackgroundImage() {
		if (defaultImage != null) return defaultImage;
		return (new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB));
	}
	
	/**
	 * @return the tileBgImage
	 */
	public static BufferedImage getNoImage() {
		if (vidPrevUnavailable != null) return vidPrevUnavailable;
		return (new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB));
	}
	
	/**
	 * render the bufferedImage to imageTile
	 * 
	 * @param tile
	 * @param decodedImage
	 */
	public void renderTile(int tileIndex, BufferedImage decodedImage) {
		if(tileIndex < 0 || tileIndex >= tiles.length) return;
		tiles[tileIndex].setBufferedImage(decodedImage);
	}

	/**
	 * Get the index of the tile object in the Array
	 * 
	 * @param tile
	 * @return
	 */
	public static synchronized int find(ImageTile tile) {
		int found = -1;
		for (int tileIndex = 0; tileIndex < tiles.length; tileIndex++) {
			if (tiles[tileIndex].equals(tile)) {
				found = tileIndex;
				break;
			}
		}
		return found;
	}
}
