package com.dss.vms.ui.components.drawing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

public class PolygonDrawingPopup extends JPopupMenu implements ActionListener {
	private JMenuItem deleteRegion;
	private int shapeIndex;
	private PolygonDrawingPanel drawPane;

	/**
	 * Constructor
	 */
	public PolygonDrawingPopup(PolygonDrawingPanel drawPane) {
		deleteRegion = new JMenuItem("Delete region");
		deleteRegion.addActionListener(this);
		this.add(deleteRegion);
		this.drawPane = drawPane;
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getSource().equals(deleteRegion)) {
			// get Regions and delete that particular region by setting
			drawPane.getRegions().remove(shapeIndex);
			drawPane.getShapes().remove(shapeIndex);
			drawPane.repaint();

			// remove the table entry
			DefaultTableModel dModel = (DefaultTableModel) drawPane.getParentDialog().getParamTable().getModel();
			dModel.removeRow(shapeIndex);
		}
	}

	/**
	 * @param shape the shape to set
	 */
	public void setShapeIndex(int index) {
		this.shapeIndex = index;
	}

}
