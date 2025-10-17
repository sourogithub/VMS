package com.dss.vms.ui.components.analytics.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.analytics.data.FaceRecognitionEvent;
import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.ui.utility.MJPEGDecoder;
import com.dss.vms.video.data.MediaFrame;
import com.dss.vms.view.panel.ImageTile;
/**
 * Displays the events in a canvas and all the event details.
 * @author dss-02
 */
public class EventDisplayPanel extends JPanel {
	private static final BufferedImage backgroundImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
	
	private static final String FR_HEADERS[] = {
			"Event Time", "Camera", "Recognition Status", 
			"Employee ID", "Employee Name", "Employee Date of Birth", "Employee Gender" 
	};
	
	private ImageTile view;
	private JTable eventTable;
	
	public EventDisplayPanel() { createUI(); }

	/**
	 * create UI components and draw on panel
	 */
	private void createUI() {
		this.setLayout(new BorderLayout(5, 5));
		Dimension dimension = new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
		this.view = new ImageTile(backgroundImage, dimension);
		this.view.setMaximumSize(dimension);
		this.view.setMinimumSize(dimension);

		this.eventTable = new JTable(7, 2);

		this.add(view, BorderLayout.CENTER);
		this.add(eventTable, BorderLayout.SOUTH);
	}
	
	/**
	 * update the event details on display panel.
	 * @param event
	 */
	public void updateView(GenericEvent event) {
		AnalyticType eventType = event.getType();
		
		if(eventType == AnalyticType.FACE) {
			FaceRecognitionEvent faceEvent = (FaceRecognitionEvent) event;
			Employee employee = faceEvent.getEmployee();

			MediaFrame[] faces = employee.getFaces();
			if(faces != null && faces.length > 0) {
				renderFrame(faces[0]);
			}
			
			TableModel tableModel = this.eventTable.getModel();
			String tableData[] = {
					new Date(event.getTimestamp()).toString(), 
					"NOT_AVAILABLE",
					String.valueOf(faceEvent.isRecognised()),
					employee.getEmployeeId(),
					employee.getEmployeeName(),
					employee.getEmployeeDoB().toString(),
					employee.getEmployeeGender()
			};
			
			for(int rowIndex = 0; rowIndex < tableData.length; rowIndex++) {
				tableModel.setValueAt(FR_HEADERS[rowIndex], rowIndex, 0);
				tableModel.setValueAt(tableData[rowIndex], rowIndex, 1);
			}
			
		} else if (eventType == AnalyticType.INTRUDER) {
			
		}
	}

	/**
	 * render the frame on view panel
	 * @apiNote please use jpeg frames for rendering.
	 * @param mediaFrame
	 */
	private void renderFrame(MediaFrame mediaFrame) {
		BufferedImage image = null;
		if(mediaFrame.isDecoded()) {
			image = mediaFrame.getBufferedImage();

		} else if(mediaFrame.getMediaType() == MediaType.JPEG) {
			image = MJPEGDecoder.decode(mediaFrame.getRawFrame());
		}
		//TODO: Render other image types
		
		if(image == null) {
			image = ImageTileBucket.getNoImage();
		}
				
		this.view.setBufferedImage(image);
		Dimension dimension = new Dimension(view.getSize());
		this.view.setDimension(dimension);
	}
	
	public BufferedImage getCurrentImage() {
		BufferedImage image = view.getBufferedImage();
		if(image.equals(backgroundImage)) {
			return null;
		} 
		return image;
	}
	
}
