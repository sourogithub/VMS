package com.dss.vms.ui.utility;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

/**
 * @author dss-02
 */
public class VmsRegionUtility {
	public static final int INVALID_MIN_VALUE = 9999;
	public static final int INVALID_MAX_VALUE = -9999;

	public static Dimension getPolygonDimension(ArrayList<Point> controlPoints) {
		int maxX = INVALID_MAX_VALUE;
		int maxY = INVALID_MAX_VALUE;
		int minX = INVALID_MIN_VALUE;
		int minY = INVALID_MIN_VALUE;

		for (Point currentPoint : controlPoints) {
			int currentX = currentPoint.x;
			int currentY = currentPoint.y;

			if (currentX > maxX) {
				maxX = currentX;
			}
			if (currentY > maxY) {
				maxY = currentY;
			}

			if (currentX < minX) {
				minX = currentX;
			}
			if (currentY < minY) {
				maxY = currentY;
			}
		}
		return new Dimension((Math.abs(maxX - minX)), Math.abs(maxY - minY));
	}
}
