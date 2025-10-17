package com.dss.vms.common.data;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.dss.vms.common.constants.CommonConstant;
import com.dss.vms.common.constants.RegionDirection;
import com.dss.vms.common.constants.RegionType;

/**
 * @author jdeveloper
 */
public class Region implements Serializable {
	private static final long serialVersionUID = 1L;
	private Dimension dimension;
	private RegionType type;
	private List<Point> controlPoint;
	private Point directionStart;
	private Point directionEnd;
	private RegionDirection direction;
	private int[] thresholds = new int[CommonConstant.MAX_PARAMETER_PER_REGION];

	/**
	 * @return the dimension
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * @param dimension the dimension to set
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the type
	 */
	public RegionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RegionType type) {
		this.type = type;
	}

	/**
	 * @return the controlPoint
	 */
	public List<Point> getControlPoint() {
		return controlPoint;
	}

	/**
	 * @param controlPoint the controlPoint to set
	 */
	public void setControlPoint(List<Point> controlPoint) {
		this.controlPoint = controlPoint;
	}

	/**
	 * @return the directionStart
	 */
	public Point getDirectionStart() {
		return directionStart;
	}

	/**
	 * @param directionStart the directionStart to set
	 */
	public void setDirectionStart(Point directionStart) {
		this.directionStart = directionStart;
	}

	/**
	 * @return the directionEnd
	 */
	public Point getDirectionEnd() {
		return directionEnd;
	}

	/**
	 * @param directionEnd the directionEnd to set
	 */
	public void setDirectionEnd(Point directionEnd) {
		this.directionEnd = directionEnd;
	}

	/**
	 * @return the direction
	 */
	public RegionDirection getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(RegionDirection direction) {
		this.direction = direction;
	}

	/**
	 * @return the thresholds
	 */
	public int[] getThresholds() {
		return thresholds;
	}

	/**
	 * @param thresholds the thresholds to set
	 */
	public void setThresholds(int[] thresholds) {
		this.thresholds = thresholds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Region [dimension=");
		builder.append(dimension);
		builder.append(", type=");
		builder.append(type);
		builder.append(", controlPoint=");
		builder.append(controlPoint);
		builder.append(", directionStart=");
		builder.append(directionStart);
		builder.append(", directionEnd=");
		builder.append(directionEnd);
		builder.append(", direction=");
		builder.append(direction);
		builder.append(", thresholds=");
		builder.append(Arrays.toString(thresholds));
		builder.append("]");
		return builder.toString();
	}
}
