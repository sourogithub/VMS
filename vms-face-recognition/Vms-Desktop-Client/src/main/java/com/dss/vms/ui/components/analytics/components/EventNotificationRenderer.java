package com.dss.vms.ui.components.analytics.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.utility.MJPEGDecoder;
import com.dss.vms.video.data.MediaFrame;

public class EventNotificationRenderer extends DefaultListCellRenderer implements Icons {
	private static final long serialVersionUID = -6750598219676183806L;
	private static final int THUMBNAIL_WIDTH = 100; 

	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		//setting font size
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 12));
		label.setForeground(Color.white);
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		
		if(isSelected) {
			label.setBackground(Color.GRAY);
		} else {
			label.setBackground(Color.DARK_GRAY);
		}
		
		if (value != null) {
			try {
				if (value instanceof FaceRecognitionEvent) {
					FaceRecognitionEvent event = (FaceRecognitionEvent) value;
					int channelID = event.getChannelID();
					VideoCamera capturedCamera = cameraBucket.searchChannelID(channelID);

					if (event.isRecognised()) {
						Employee employeeData = event.getEmployee();
						String employeeName = employeeData.getEmployeeName();
						String employeeID = employeeData.getEmployeeId();
						BufferedImage image = null;

						String labelText =  employeeName + " [" + employeeID + "] ";
						if(capturedCamera != null) {
							labelText = labelText + " , Camera : " + capturedCamera.getName();
						} 
						label.setText(labelText);
						
						if (employeeData.getFaces() != null && employeeData.getFaces().length > 0) {
							// get the first frame
							MediaFrame frame = employeeData.getFaces()[0];
							if (frame.isDecoded()) {
								image = frame.getBufferedImage();
							} else if (frame.getMediaType() == MediaType.JPEG 
									|| frame.getMediaType() == MediaType.MJPEG) {
								byte[] frameData = frame.getRawFrame();
								image = MJPEGDecoder.decode(frameData);
							} else {
								// no image
							}

							if (image != null) {
								int imageWidth = image.getWidth();
								int imageHeight = image.getHeight();
								double aspectRatio = (double) imageWidth / imageHeight;

								int thumbnailHeight = (int) (THUMBNAIL_WIDTH / aspectRatio);
								BufferedImage resizedImage = new BufferedImage(THUMBNAIL_WIDTH, thumbnailHeight,
										BufferedImage.TYPE_4BYTE_ABGR);
								Graphics2D graphics = (Graphics2D) resizedImage.getGraphics();
								graphics.drawImage(image, 0, 0, THUMBNAIL_WIDTH, thumbnailHeight, null);
								graphics.dispose();

								label.setIcon(new ImageIcon(resizedImage));
							}
						}

					} else {
						Employee employeeData = event.getEmployee();
						
						String labelText = "Detected : " + employeeData.getEmployeeName() + " [" + employeeData.getEmployeeId() + "] ";
						if(capturedCamera != null) {
							labelText = labelText + " , Camera : " + capturedCamera.getName();
						}

						// show unknown person
						label.setText(labelText);
					}
					
					label.setText(label.getText() + " Time : " + new Date(event.getTimestamp()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return label;
	}
}