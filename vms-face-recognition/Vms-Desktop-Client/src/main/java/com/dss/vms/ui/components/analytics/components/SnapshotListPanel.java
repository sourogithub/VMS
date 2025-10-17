package com.dss.vms.ui.components.analytics.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.data.ImageTileBucket;

public class SnapshotListPanel extends JPanel {
	private static final String DEFAULT_IMAGES[] = new String[] {
					"res/turn_left.png", "res/turn_right.png", 
					"res/face_front.png", "res/face_down.png", "res/face_up.png",
					"res/face_bottom_right.png", "res/face_bottom_left.png",
					"res/face_top_right.png", "res/face_top_left.png" };
	
	public static final int MAX_IMAGE_COUNT = 16;
	public static final int MIN_IMAGE_COUNT = DEFAULT_IMAGES.length;

	private DefaultListModel<BufferedImage> imageList = new DefaultListModel<>();
	private int imageCount = 0;

	public SnapshotListPanel() {
		this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		this.setBackground(Color.darkGray);
		this.setMinimumSize(new Dimension(180, 400));
		this.setLayout(new BorderLayout(5, 5));

		JList<BufferedImage> imageInput = new JList<>();
		imageInput.setModel(imageList);
		imageInput.setCellRenderer(new ImageListRenderer());
		imageInput.setBackground(ComponentColors.TREE_BG);

		JScrollPane scrollpane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.getViewport().add(imageInput);
		scrollpane.getViewport().setBackground(ComponentColors.TREE_BG);

		this.add(scrollpane, BorderLayout.CENTER);
		/* loading direction images */
		this.loadDirectionThumbnails();
	}

	/** Facial Thumbnails for Direction indication **/
	private void loadDirectionThumbnails() {
		for (String filePath : DEFAULT_IMAGES) {
			try {
				BufferedImage image = ImageIO.read(new File(filePath));
				imageList.addElement(image);
			} catch (IOException e) {}
		}
	}

	public boolean addImage(BufferedImage image) {
		if (imageCount >= MAX_IMAGE_COUNT)
			return false;
		if (imageCount < MIN_IMAGE_COUNT) {
			imageList.remove(imageCount);
		}
		imageList.add(imageCount, image);
		imageCount++;
		return true;
	}

	public void clearAllImages() {
		imageList.removeAllElements();
		imageList.clear();
		imageCount = 0;
		loadDirectionThumbnails();
	}

	public List<BufferedImage> getAllImages() {
		List<BufferedImage> list = new ArrayList<>();

		for (int imageCount = 0; imageCount < imageList.size(); imageCount++) {
			list.add(imageList.get(imageCount));
		}
		return list;
	}
}
