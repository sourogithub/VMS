package com.dss.vms.ui.constants;

/**
 * Defines the grid layout properties
 * 
 * @author dss-02
 *
 */
public class TileGridProperty {
	public static int panelHeight = 700;
	public static int panelWidth = 900;

	/* 1 x 1 GRID */
	public static int grid_1 = 1;
	public static int[] gridWidth_1 = { 900 };
	public static int[] gridHeight_1 = { 700 };
	public static int[] gridX_1 = { 000 };
	public static int[] gridY_1 = { 000 };

	/* 2 x 2 GRID */
	public static int grid_4 = 4;
	public static int[] gridWidth_4 = { 450, 450, 450, 450 };
	public static int[] gridHeight_4 = { 350, 350, 350, 350 };
	public static int[] gridX_4 = { 000, 450, 000, 450 };
	public static int[] gridY_4 = { 000, 000, 350, 350 };

	/* 3 x 3 GRID */
	public static int grid_9 = 9;
	public static int[] gridWidth_9 = { 300, 300, 300, 300, 300, 300, 300, 300, 300 };
	public static int[] gridHeight_9 = { 233, 233, 233, 233, 233, 233, 233, 233, 233 };
	public static int[] gridX_9 = { 000, 300, 600, 000, 300, 600, 000, 300, 600 };
	public static int[] gridY_9 = { 000, 000, 000, 233, 233, 233, 466, 466, 466 };

	/* 4 x 4 GRID */
	public static int grid_16 = 16;
	public static int[] gridWidth_16 = { 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225,
			225 };
	public static int[] gridHeight_16 = { 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175,
			175 };
	public static int[] gridX_16 = { 000, 225, 450, 625, 000, 225, 450, 625, 000, 225, 450, 625, 000, 225, 450, 625 };
	public static int[] gridY_16 = { 000, 000, 000, 000, 175, 175, 175, 175, 350, 350, 350, 350, 525, 525, 525, 525 };

	/* for 5 X 1 GRID */
	public static int grid_6 = 6;
	public static int[] gridWidth_6 = { 600, 300, 300, 300, 300, 300 };
	public static int[] gridHeight_6 = { 466, 233, 233, 233, 233, 233 };
	public static int[] gridX_6 = { 000, 600, 600, 600, 300, 000 };
	public static int[] gridY_6 = { 000, 000, 233, 466, 466, 466 };

	/* for 7 x 1 GRID */
	public static int grid_8 = 8;
	public static int[] gridWidth_8 = { 625, 225, 225, 225, 225, 225, 225, 225 };
	public static int[] gridHeight_8 = { 525, 175, 175, 175, 175, 175, 175, 175 };
	public static int[] gridX_8 = { 000, 625, 625, 625, 625, 450, 225, 000 };
	public static int[] gridY_8 = { 000, 000, 175, 350, 525, 525, 525, 525 };

	/* for 12 x 1 GRID */
	public static int grid_13 = 13;
	public static int[] gridWidth_13 = { 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 450 };
	public static int[] gridHeight_13 = { 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 175, 350 };
	public static int[] gridX_13 = { 000, 225, 450, 625, 625, 625, 625, 450, 225, 000, 000, 000, 225 };
	public static int[] gridY_13 = { 000, 000, 000, 000, 175, 350, 525, 525, 525, 525, 350, 175, 175 };

}
