package com.dss.vms.ui.utility;

import java.awt.Dimension;

import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.constants.GridLayout;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author dss-02 this class is responsible for drawing ImageTiles on The
 *         ViewPanel
 */
public class GridPainter {

	public static final GridLayout DEFAULT_GRID_LAYOUT = GridLayout.Layout1x1;
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	
	private LiveViewPanel view = null;
	private GridLayout currentGridLayout = DEFAULT_GRID_LAYOUT;
	private ImageTile currentSelectedTile = null;

	private static GridPainter INSTANCE = null;
	/**
	 * @return ViewController instance
	 */
	public static GridPainter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GridPainter();
		}
		return INSTANCE;
	}

	/**
	 * @param viewPanel
	 */
	public void registerContainerPanel(LiveViewPanel viewPanel) {
		this.view = viewPanel;
	}

	/**
	 * @return view
	 */
	public LiveViewPanel getRegisteredContainerPanel() {
		return this.view;
	}

	/**
	 * Draw a 1x1 layout on a panel
	 * 
	 * @param sourceTile
	 * @param fullscreenMode
	 */
	public void drawTileZoom(ImageTile sourceTile, boolean fullscreenMode) { 
		view.removeAll();
		if (!fullscreenMode) {
			drawImageTilesOnViewPanel(this.getCurrentGridLayout());
		} else { 
			currentSelectedTile = sourceTile;
			view.setLayout(null);
			Dimension displayDimension = view.getSize();
			sourceTile.setBounds(0, 0, displayDimension.width, displayDimension.height);
			sourceTile.setDimension(displayDimension);
			view.add(sourceTile);
			view.validate();
			view.repaint();
		}
	}

	/**
	 * This method is responsible for redesigning the ViewPanel for any
	 * ButtonEvents,WindowResize Events
	 * 
	 * @param view
	 * @param tiles
	 * @param imageViewLayout
	 * @param numOfImageTilesToDisplay
	 */
	public boolean drawImageTilesOnViewPanel(GridLayout layout) {
		if (this.isViewPanelRegistered()) {
			int numOfImageTilesToDisplay = layout.getNoOfTiles();
			currentGridLayout = layout;
			view.removeAll();
			view.setLayout(null);
			Dimension currentDisplayDimension = view.getSize();
			int currentDisplayHeight = currentDisplayDimension.height;
			int currentDisplayWidth = currentDisplayDimension.width;

			if (numOfImageTilesToDisplay == 1 || numOfImageTilesToDisplay == 4 || numOfImageTilesToDisplay == 9
					|| numOfImageTilesToDisplay == 16 || numOfImageTilesToDisplay == 25
					|| numOfImageTilesToDisplay == 36) {
				int currentX = 0;
				int currentY = 0;
				int divideRatio = (int) Math.sqrt(numOfImageTilesToDisplay);
				int imageTileWidth = Math.round(currentDisplayWidth / divideRatio);
				int imageTileHeight = Math.round(currentDisplayHeight / divideRatio);
				int numOfTimesToDrawPanelsInARow = 1;

				for (int index = 0; index < numOfImageTilesToDisplay; index++) {
					tileBucket.getTile(index).setDimension(new Dimension(imageTileWidth, imageTileHeight));
					tileBucket.getTile(index).setSize(new Dimension(imageTileWidth, imageTileHeight));
					tileBucket.getTile(index).setBounds(currentX, currentY, imageTileWidth, imageTileHeight);
					view.add(tileBucket.getTile(index));

					if (numOfTimesToDrawPanelsInARow < divideRatio) {
						currentX += imageTileWidth;
					} else if (numOfTimesToDrawPanelsInARow == divideRatio) {
						numOfTimesToDrawPanelsInARow = 0; 
						currentX = 0;
						currentY += imageTileHeight;
					}
					numOfTimesToDrawPanelsInARow++;
				}
			} else if (numOfImageTilesToDisplay == 6) { /* for 5x1 layout */
				int oneThirdHeight = Math.round(currentDisplayHeight / 3);
				int oneThirdWidth = Math.round(currentDisplayWidth / 3);
				int twoThirdWidth = Math.round((currentDisplayWidth * 2) / 3);
				int twoThirdHeight = Math.round((currentDisplayHeight * 2) / 3);

				ImageTile panel0 = tileBucket.getTile(0);
				ImageTile panel1 = tileBucket.getTile(1);
				ImageTile panel2 = tileBucket.getTile(2);
				ImageTile panel3 = tileBucket.getTile(3);
				ImageTile panel4 = tileBucket.getTile(4);
				ImageTile panel5 = tileBucket.getTile(5);

				// Setting Panel Bounds
				panel0.setBounds(0, 0, twoThirdWidth, twoThirdHeight);
				panel1.setBounds(twoThirdWidth, 0, oneThirdWidth, oneThirdHeight);
				panel2.setBounds(twoThirdWidth, oneThirdHeight, oneThirdWidth, oneThirdHeight);
				panel3.setBounds(twoThirdWidth, twoThirdHeight, oneThirdWidth, oneThirdHeight);
				panel4.setBounds(oneThirdWidth, twoThirdHeight, oneThirdWidth, oneThirdHeight);
				panel5.setBounds(0, twoThirdHeight, oneThirdWidth, oneThirdHeight);

				// Setting Dimension Param in Panels
				panel0.setDimension(new Dimension(twoThirdWidth, twoThirdHeight));
				panel1.setDimension(new Dimension(oneThirdWidth, oneThirdHeight));
				panel2.setDimension(new Dimension(oneThirdWidth, oneThirdHeight));
				panel3.setDimension(new Dimension(oneThirdWidth, oneThirdHeight));
				panel4.setDimension(new Dimension(oneThirdWidth, oneThirdHeight));
				panel5.setDimension(new Dimension(oneThirdWidth, oneThirdHeight));

				// Setting Size of Panels
				panel0.setSize(new Dimension(twoThirdWidth, twoThirdHeight));
				panel1.setSize(new Dimension(oneThirdWidth, oneThirdHeight));
				panel2.setSize(new Dimension(oneThirdWidth, oneThirdHeight));
				panel3.setSize(new Dimension(oneThirdWidth, oneThirdHeight));
				panel4.setSize(new Dimension(oneThirdWidth, oneThirdHeight));
				panel5.setSize(new Dimension(oneThirdWidth, oneThirdHeight));

				// Adding to Viewing Panel
				view.add(panel0);
				view.add(panel1);
				view.add(panel2);
				view.add(panel3);
				view.add(panel4);
				view.add(panel5);

			} else if (numOfImageTilesToDisplay == 8) { /* for 7x1 layout */
				int oneFourthWidth = Math.round(currentDisplayWidth / 4);
				int oneFourthHeight = Math.round(currentDisplayHeight / 4);
				int twoFourthWidth = Math.round((currentDisplayWidth * 2) / 4);
				int twoFourthHeight = Math.round((currentDisplayHeight * 2) / 4);
				int threeFourthWidth = Math.round((currentDisplayWidth * 3) / 4);
				int threeFourthHeight = Math.round((currentDisplayHeight * 3) / 4);

				ImageTile panel0 = tileBucket.getTile(0);
				ImageTile panel1 = tileBucket.getTile(1);
				ImageTile panel2 = tileBucket.getTile(2);
				ImageTile panel3 = tileBucket.getTile(3);
				ImageTile panel4 = tileBucket.getTile(4);
				ImageTile panel5 = tileBucket.getTile(5);
				ImageTile panel6 = tileBucket.getTile(6);
				ImageTile panel7 = tileBucket.getTile(7);

				panel0.setBounds(0, 0, threeFourthWidth, threeFourthHeight);
				panel0.setDimension(new Dimension(threeFourthWidth, threeFourthHeight));
				panel0.setSize(new Dimension(threeFourthWidth, threeFourthHeight));

				panel1.setBounds(threeFourthWidth, 0, oneFourthWidth, oneFourthHeight);
				panel1.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel1.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel2.setBounds(threeFourthWidth, oneFourthHeight, oneFourthWidth, oneFourthHeight);
				panel2.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel2.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel3.setBounds(threeFourthWidth, twoFourthHeight, oneFourthWidth, oneFourthHeight);
				panel3.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel3.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel4.setBounds(threeFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel4.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel4.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel5.setBounds(twoFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel5.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel5.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel6.setBounds(oneFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel6.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel6.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				panel7.setBounds(0, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel7.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel7.setSize(new Dimension(oneFourthWidth, oneFourthHeight));

				view.add(panel0);
				view.add(panel1);
				view.add(panel2);
				view.add(panel3);
				view.add(panel4);
				view.add(panel5);
				view.add(panel6);
				view.add(panel7);

			} else if (numOfImageTilesToDisplay == 13) { /* for 12x1 layout */
				int oneFourthWidth = Math.round(currentDisplayWidth / 4);
				int oneFourthHeight = Math.round(currentDisplayHeight / 4);
				int twoFourthWidth = Math.round((currentDisplayWidth * 2) / 4);
				int twoFourthHeight = Math.round((currentDisplayHeight * 2) / 4);
				int threeFourthWidth = Math.round((currentDisplayWidth * 3) / 4);
				int threeFourthHeight = Math.round((currentDisplayHeight * 3) / 4);

				ImageTile panel0 = tileBucket.getTile(0);
				ImageTile panel1 = tileBucket.getTile(1);
				ImageTile panel2 = tileBucket.getTile(2);
				ImageTile panel3 = tileBucket.getTile(3);
				ImageTile panel4 = tileBucket.getTile(4);
				ImageTile panel5 = tileBucket.getTile(5);
				ImageTile panel6 = tileBucket.getTile(6);
				ImageTile panel7 = tileBucket.getTile(7);
				ImageTile panel8 = tileBucket.getTile(8);
				ImageTile panel9 = tileBucket.getTile(9);
				ImageTile panel10 = tileBucket.getTile(10);
				ImageTile panel11 = tileBucket.getTile(11);
				ImageTile panel12 = tileBucket.getTile(12);

				// Setting Panel Bounds
				panel0.setBounds(0, 0, oneFourthWidth, oneFourthHeight);
				panel1.setBounds(oneFourthWidth, 0, oneFourthWidth, oneFourthHeight);
				panel2.setBounds(twoFourthWidth, 0, oneFourthWidth, oneFourthHeight);
				panel3.setBounds(threeFourthWidth, 0, oneFourthWidth, oneFourthHeight);
				panel4.setBounds(threeFourthWidth, oneFourthHeight, oneFourthWidth, oneFourthHeight);
				panel5.setBounds(threeFourthWidth, twoFourthHeight, oneFourthWidth, oneFourthHeight);
				panel6.setBounds(threeFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel7.setBounds(twoFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel8.setBounds(oneFourthWidth, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel9.setBounds(0, threeFourthHeight, oneFourthWidth, oneFourthHeight);
				panel10.setBounds(0, twoFourthHeight, oneFourthWidth, oneFourthHeight);
				panel11.setBounds(0, oneFourthHeight, oneFourthWidth, oneFourthHeight);
				panel12.setBounds(oneFourthWidth, oneFourthHeight, twoFourthWidth, twoFourthHeight);

				// Setting Panel Dimension
				panel0.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel1.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel2.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel3.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel4.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel5.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel6.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel7.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel8.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel9.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel10.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel11.setDimension(new Dimension(oneFourthWidth, oneFourthHeight));
				panel12.setDimension(new Dimension(twoFourthWidth, twoFourthHeight));

				// setting panel Size
				panel0.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel1.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel2.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel3.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel4.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel5.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel6.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel7.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel8.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel9.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel10.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel11.setSize(new Dimension(oneFourthWidth, oneFourthHeight));
				panel12.setSize(new Dimension(twoFourthWidth, twoFourthHeight));

				view.add(panel0);
				view.add(panel1);
				view.add(panel2);
				view.add(panel3);
				view.add(panel4);
				view.add(panel5);
				view.add(panel6);
				view.add(panel7);
				view.add(panel8);
				view.add(panel9);
				view.add(panel10);
				view.add(panel11);
				view.add(panel12);
			}

			view.validate();
			view.repaint();
			return true;
		}
		return false;
	}

	/**
	 * @return the currentGridLayout
	 */
	public GridLayout getCurrentGridLayout() {
		return currentGridLayout;
	}

	/**
	 * @return the currentSelectedTile
	 */
	public ImageTile getCurrentSelectedTile() {
		return currentSelectedTile;
	}

	/**
	 * @param currentSelectedTile the currentSelectedTile to set
	 */
	public void setCurrentSelectedTile(ImageTile currentSelectedTile) {
		this.currentSelectedTile = currentSelectedTile;
	}

	/**
	 * if viewPanel is registered or not
	 * 
	 * @return
	 */
	public boolean isViewPanelRegistered() {
		return view == null ? false : true;
	}
}
