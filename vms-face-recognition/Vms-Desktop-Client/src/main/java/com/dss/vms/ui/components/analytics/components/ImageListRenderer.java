package com.dss.vms.ui.components.analytics.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.ui.constants.Icons;
/**
 * 	
 * @author dss-02
 *
 */
public class ImageListRenderer extends DefaultListCellRenderer implements Icons {
	/** default thumbnail dimensions are 300 x 300 , but maintaining aspect ratio **/
	private static final int THUMBNAIL_WIDTH = 150; 
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null) {
			if (value instanceof BufferedImage) {
				try {
					BufferedImage origImage = (BufferedImage) value;
					double aspectRatio = (double) origImage.getWidth() / origImage.getHeight();
					int thumbnailWidth = THUMBNAIL_WIDTH;
					int thumbnailHeight = (int) (thumbnailWidth / aspectRatio);
					/* copy the buffered image to thumbnail dimension buffered Image */
					BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight,
							BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D graphics = (Graphics2D) thumbnail.getGraphics();
					graphics.drawImage(origImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
					graphics.dispose();
					
					/* Setting thumbnail to Label */
					label = new JLabel(new ImageIcon(thumbnail));
					/* if this label is selected , set highlight */
					if (isSelected) {
						label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED));
					} else {
						label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return label;
	}
}