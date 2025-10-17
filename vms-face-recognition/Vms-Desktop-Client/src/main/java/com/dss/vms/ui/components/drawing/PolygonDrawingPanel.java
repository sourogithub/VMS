package com.dss.vms.ui.components.drawing;

import com.dss.vms.common.constants.RegionDirection;
import com.dss.vms.common.constants.RegionType;
import com.dss.vms.common.data.Region;
import com.dss.vms.ui.utility.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sibendu
 */
public class PolygonDrawingPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final int MAXIMUM_ALLOWED_POINTS = 8;
	private static final Logger LOGGER = LoggerFactory.getLogger(PolygonDrawingPanel.class);

	private static final long serialVersionUID = 1L;
	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final Color DRAWING_COLOR = Color.RED;

	private PolygonDrawingDialog parentDialog;
	private Dimension dimension;
	private BufferedImage bufferedImage;
	private double divRatio;

	private boolean polygonStart;
	private ArrayList<Region> regions;
	private ArrayList<Shape> shapes;
	private ArrayList<Point> temporaryPoints;
	private int controlPointCount = 0;
	private Vector<Integer> paramValues;

	public PolygonDrawingPanel(Dimension dimension, BufferedImage bufferedImage, PolygonDrawingDialog parent) {
		this.parentDialog = parent;
		this.dimension = dimension;
		this.bufferedImage = bufferedImage;
		this.divRatio = (double) dimension.height / (double) dimension.width;
		this.polygonStart = false;

		if (regions == null) {
			this.regions = new ArrayList<Region>();
		}
		if (shapes == null) {
			this.shapes = new ArrayList<Shape>();
		}
		/* saving intermediate points while creating Polygon */
		this.temporaryPoints = new ArrayList<Point>(); 
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);
		this.setBackground(Color.BLACK);
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setDoubleBuffered(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(DEFAULT_COLOR);
		super.paintComponent(g);

		Dimension currentDimension = this.getSize();
		g.drawImage(bufferedImage, 0, 0, currentDimension.width, currentDimension.height, null);

		// drawing Regions
		if (!regions.isEmpty()) {
			for (int index = 0; index < regions.size(); index++) {
				Region region = regions.get(index);
				RegionType regionType = region.getType();
				if (regionType == RegionType.POLYGON) {
					drawPolygon(region, g);
				}
//				g.setColor(Color.white);
//				Point startingPointOfPoly = region.getDirectionStart();
//				g.drawString("Zone " + index, (startingPointOfPoly.x + 5), (startingPointOfPoly.y + 5));		/* drawing region marker */
				g.setColor(DEFAULT_COLOR); /* reset to initial Color */
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.repaint(); /* calling PaintComponent repeatedly */
	}

	/**
	 * @param polygon
	 * @param graphics Handle of Panel Draws Polygons
	 */
	public void drawPolygon(Region polygon, Graphics g) {
		int noOfVertices = polygon.getControlPoint().size();
		g.setColor(Color.red);
		ArrayList<Point> controlPoints = (ArrayList<Point>) polygon.getControlPoint();

		for (int edge = 0; edge < (noOfVertices - 1); edge++) {
			Point startOfLine = controlPoints.get(edge);
			Point endOfLine = controlPoints.get(edge + 1);
			g.drawLine(startOfLine.x, startOfLine.y, endOfLine.x, endOfLine.y);
			// drawing vertices with filled circles
			g.setColor(Color.ORANGE);
			g.fillOval(startOfLine.x - 4, startOfLine.y - 4, 8, 8);
			g.fillOval(endOfLine.x - 4, endOfLine.y - 4, 8, 8);
			g.setColor(DRAWING_COLOR);
		}
		// now draw the remaining edge from end to start
		Point directionStart = polygon.getDirectionStart();
		Point directionEnd = polygon.getDirectionEnd();
		g.drawLine(directionStart.x, directionStart.y, directionEnd.x, directionEnd.y);
	}

	/**
	 * create Region and Add to the Regions list
	 */
	private void createAndAddRegionToList() {
		Point startPoint = temporaryPoints.get(0); /* fetch the starting point */
		Point endPoint = temporaryPoints.get(temporaryPoints.size() - 1); /* fetching the end point */

		// Adding shape to Shapes
		addShape(temporaryPoints);

		// creating region Data
		Region region = new Region();
		region.setControlPoint(temporaryPoints);
		region.setDirectionStart(startPoint);
		region.setDirectionEnd(endPoint);
		region.setType(RegionType.POLYGON);
		region.setDirection(RegionDirection.BOTH);
		region.setDimension(AnalyticRegionUtility.getPolygonDimension(temporaryPoints));

		regions.add(region);
		resetPolygonDrawing();
		polygonStart = false;
	}

	/**
	 * @param reg
	 */
	private void initDrawing(ArrayList<Region> reg) {
		LOGGER.info("Regions List initialising from the Session Master...");
		if (!reg.isEmpty()) {
			regions = new ArrayList<>(); /* resetting Region */
			shapes = new ArrayList<>(); /* resetting Shapes */
			regions.addAll(reg);
			for (Region currentRegion : reg) {
				addShape((ArrayList<Point>) currentRegion.getControlPoint());
			}

		} else {
			LOGGER.warn("Region List is Empty....");
		}
		LOGGER.info("Regions List initialised Successfully...");
	}

	@Override
	public void mouseClicked(MouseEvent clickEvent) {
		if (polygonStart == false) { /* create a new PolygonPoint ArrayLists */
			temporaryPoints = new ArrayList<Point>();
		}

		if (clickEvent.getButton() == MouseEvent.BUTTON1) {
			Point currentPoint = clickEvent.getPoint();
			temporaryPoints.add(currentPoint);
			polygonStart = true;
			controlPointCount++;

			// Draw pointer Location
			Graphics g = this.getGraphics();
			g.setColor(DRAWING_COLOR);
			g.fillOval(clickEvent.getX() - 5, clickEvent.getY() - 5, 10,
					10); /* drawing a pointer location of Radius 5 */
			g.dispose();

		} else if (clickEvent.getButton() == MouseEvent.BUTTON3) {
			if (controlPointCount > 2 && controlPointCount <= MAXIMUM_ALLOWED_POINTS) { /* created the final Polygon */
				if (polygonStart == true) {

					createAndAddRegionToList();
					controlPointCount = 0;
					this.repaint();

					// now creating table Row Data
					paramValues = new Vector<Integer>(); /* now creating table Row Data */
					paramValues.addElement(regions.size() - 1);
					paramValues.addElement(0);
					paramValues.addElement(0);
					paramValues.addElement(0);
					paramValues.addElement(0);
					parentDialog.addDataRowToTable(paramValues);

					polygonStart = false;
				}
			} else if ((clickEvent.getButton() == MouseEvent.BUTTON3)
					&& (polygonStart == false)) { /* right click on shape to delete the shape */
				int x = clickEvent.getX();
				int y = clickEvent.getY();
				for (int shapeIndex = 0; shapeIndex < shapes.size(); shapeIndex++) {
					Shape shape = shapes.get(shapeIndex);
					if (shape.contains(x, y)) {
						Graphics2D g2 = (Graphics2D) this.getGraphics();
						g2.setColor(Color.RED);
						g2.fill(shape);

						PolygonDrawingPopup popup = new PolygonDrawingPopup(this);
						popup.setShapeIndex(shapeIndex);
						popup.show(this, clickEvent.getX(), clickEvent.getY());
					}
				}
			} else {
				JPanel messagePanel = new JPanel();
				JLabel message = new JLabel(
						"Cannot create polygon using lesser than 3 vertices and More than 8 vertices");
				controlPointCount = 0;
				message.setForeground(Color.WHITE);
				messagePanel.add(message);
				JOptionPane.showMessageDialog(this, messagePanel, "Polygon Creation Failed", JOptionPane.ERROR_MESSAGE);
				resetPolygonDrawing();
				repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent enterEvent) {

	}

	@Override
	public void mouseExited(MouseEvent exitEvent) {
	}

	@Override
	public void mousePressed(MouseEvent pressEvent) {
	}

	@Override
	public void mouseReleased(MouseEvent releaseEvent) {
	}

	@Override
	public void mouseDragged(MouseEvent drag) {
	}

	@Override
	public void mouseMoved(MouseEvent mouseMove) {
		// TODO Auto-generated method stub
		Graphics g = this.getGraphics();

		if (polygonStart == true) {
			int numOfVertices = temporaryPoints.size();
			if (numOfVertices == 1) /*
									 * if no of polygon points is 1 then we cannot draw Polygons so we draw Line
									 * else draw polygon
									 */
			{
				int currentX = mouseMove.getX();
				int currentY = mouseMove.getY();
				int polygonXCoordinate = temporaryPoints.get(0).x;
				int polygonYCoordinate = temporaryPoints.get(0).y;

				g = this.getGraphics(); /* getting graphics handle */
				g.setColor(DRAWING_COLOR);
				g.drawLine(polygonXCoordinate, polygonYCoordinate, currentX, currentY);
			} else {
				int polygonXCoordinates[] = new int[temporaryPoints.size()
						+ 1]; /* got drawn polygon vertices and +1 for the current vertiex to be added */
				int polygonYCoordinates[] = new int[temporaryPoints.size() + 1];
				int currentX = mouseMove.getX();
				int currentY = mouseMove.getY();
				int sizeOfArray = polygonXCoordinates.length;
				// putting the polygon points into the Array
				for (int index = 0; index < sizeOfArray; index++) {
					if (index == (sizeOfArray - 1)) { /* putting last element as CUrrentX and CurrentY coordinates */
						polygonXCoordinates[index] = currentX;
						polygonYCoordinates[index] = currentY;
					} else {
						polygonXCoordinates[index] = temporaryPoints.get(index).x;
						polygonYCoordinates[index] = temporaryPoints.get(index).y;
					}
				}
				g = this.getGraphics(); /* getting graphics handle */
				g.setColor(DRAWING_COLOR);
				// g.drawPolygon(polygonXCoordinates, polygonYCoordinates, sizeOfArray);
				for (int index = 0; index < (sizeOfArray - 1); index++) {
					int x1 = polygonXCoordinates[index];
					int y1 = polygonYCoordinates[index];

					int x2 = polygonXCoordinates[index + 1];
					int y2 = polygonYCoordinates[index + 1];
					// draw Line
					g.drawLine(x1, y1, x2, y2);
				}
			}

			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				g.dispose();
			}
		}
	}

	/**
	 * @param controlPoints
	 */
	private void addShape(ArrayList<Point> controlPoints) {
		LOGGER.info("Creating Shape objects from the given control Points");
		int[] xPoints = new int[controlPoints.size()];
		int[] yPoints = new int[controlPoints.size()];
		for (int index = 0; index < controlPoints.size(); index++) {
			xPoints[index] = controlPoints.get(index).x;
			yPoints[index] = controlPoints.get(index).y;
		}
		Shape polygon = new Polygon(xPoints, yPoints, controlPoints.size());
		shapes.add(polygon);
	}

	/**
	 * @return bufferedImage in Drawingpanel
	 */
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	/**
	 * @param bufferedImage
	 */
	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	/**
	 * @return panelDimension
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * @return region containing polygons
	 */
	public ArrayList<Region> getRegions() {
		return regions;
	}

	/**
	 * Reset the gathered set of intermediate points
	 */
	public void resetPolygonDrawing() {
		this.polygonStart = false;
	}

	/**
	 * Clear Regions
	 */
	public void clearDrawing() {
		this.regions = new ArrayList<Region>();
		this.shapes = new ArrayList<Shape>();
		this.repaint();
	}

	/**
	 * @param dimension bufferedImage dimension to set
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the shapes
	 */
	public ArrayList<Shape> getShapes() {
		return shapes;
	}

	/**
	 * @param regions the regions to set
	 */
	public void setRegions(ArrayList<Region> regions) {
		this.initDrawing(regions);
	}

	/**
	 * @return the parentDialog
	 */
	public PolygonDrawingDialog getParentDialog() {
		return parentDialog;
	}

}
