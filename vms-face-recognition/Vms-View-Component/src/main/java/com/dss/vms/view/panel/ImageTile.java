package com.dss.vms.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author dss
 */
public class ImageTile extends JPanel {
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");

	private static LocalDateTime currentLocalTime;
	private BufferedImage bufferedImage;
	private Dimension dimension;
	private double divRatio;

	/**
	 * @param bufferedImage
	 * @param dimension
	 */
	public ImageTile(BufferedImage bufferedImage, Dimension dimension) {
		super();
		this.bufferedImage = bufferedImage;
		this.dimension = dimension;
		this.divRatio = (double) dimension.height / (double) dimension.width;
		this.setPreferredSize(dimension);
		this.setMaximumSize(dimension);
		this.setMinimumSize(dimension);
		this.setBackground(Color.black);
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
		this.setDoubleBuffered(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		super.paintComponent(g);
		if (bufferedImage != null) {
			int imageWidth = bufferedImage.getWidth();
			int imageHeight = bufferedImage.getHeight();
			double imageAspectRatio = (double) bufferedImage.getHeight() / (double) bufferedImage.getWidth();

			if ((imageWidth >= dimension.width) || (imageHeight >= dimension.height)) {
				if (divRatio == imageAspectRatio) {
					g.drawImage(bufferedImage, 0, 0, dimension.width, dimension.height, this);
				} else {
					Dimension displayDimension = new Dimension();
					displayDimension.height = dimension.height;
					displayDimension.width = (int) ((1 / imageAspectRatio) * displayDimension.height);
					if (displayDimension.width > dimension.width) {
						displayDimension.width = dimension.width;
						displayDimension.height = (int) (imageAspectRatio * displayDimension.width);
					}
					int displayX = (dimension.width - displayDimension.width) >> 1;
					int displayY = (dimension.height - displayDimension.height) >> 1;
					g.setColor(Color.black);
					g.fillRect(0, 0, dimension.width, dimension.height);
					g.drawImage(bufferedImage, displayX, displayY, displayDimension.width, displayDimension.height,
							this);

				}
			} else {
				int displayX = (dimension.width - imageWidth) / 2;
				int displayY = (dimension.height - imageHeight) / 2;
				g.setColor(Color.black);
				g.fillRect(0, 0, dimension.width, dimension.height);
				g.drawImage(bufferedImage, displayX, displayY, imageWidth, imageHeight, this);

			}

		} else {
			g.setColor(Color.black);
			g.fillRect(0, 0, dimension.width, dimension.height);
		}

		// Drawing current Time
		// drawTime(g);
	}

	/**
	 * Draws time on the view panel
	 * 
	 * @param g
	 */
	private void drawTime(Graphics g) {
		g.setColor(Color.GREEN);
		currentLocalTime = LocalDateTime.now();
		g.drawString(DATE_TIME_FORMAT.format(currentLocalTime), 20, 20);
//		g.drawRect(0, 0, this.getWidth(), 30);
		repaint(new Rectangle(0, 0, this.getWidth(), 30));
	}

	/**
	 * @return the bufferedImage
	 */
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	/**
	 * @param bufferedImage the bufferedImage to set
	 */
	public synchronized void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		repaint();
	}

	/**
	 * @return the dimension
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageTile [bufferedImage=");
		builder.append(bufferedImage);
		builder.append(", dimension=");
		builder.append(dimension);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param dimension the dimension to set
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
}
