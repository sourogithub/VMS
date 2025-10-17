package com.dss.vms.ui.constants;

public enum VmsGridLayout {
	Layout1x1(1),
	Layout2x2(4),
	Layout3x3(9),
	Layout4x4(16),
	Layout5x5(25),
	Layout6x6(36),
	Layout5x1(6),
	Layout7x1(8),
	Layout12x1(13);

	private int noOfTiles;
	/**
	 * @param noOfTiles
	 */
	private VmsGridLayout(int noOfTiles) {
		this.noOfTiles = noOfTiles;
	}

	/**
	 * @return the noOfTiles
	 */
	public int getNoOfTiles() {
		return noOfTiles;
	}
}
