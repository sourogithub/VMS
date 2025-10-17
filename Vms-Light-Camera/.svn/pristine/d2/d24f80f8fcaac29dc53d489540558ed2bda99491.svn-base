package com.dss.vms.ui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.components.tree.TreePopupMenu;
import com.dss.vms.ui.data.ChannelTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author Sibendu
 */
public class TreeEventActions extends MouseAdapter {
	private static ChannelTable  camLookupTable = ChannelTable.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	
	private JTree currentTree;
	private DefaultMutableTreeNode currentNode;
	private VideoCamera selectedCam;
	private ImageTile currentTile;
	private LiveViewPanel viewPanel;
	public boolean dragOn;

	private int cameraLimit = -1;
	private int cameraIndex = 0;
	private VideoCamera cameraSet[];

	/**
	 * @param viewPanel
	 */
	public TreeEventActions(LiveViewPanel viewPanel) {
		this.viewPanel = viewPanel;
		this.dragOn = false;
	}

	@Override
	public void mouseReleased(MouseEvent release) {
		if (dragOn == true) {
			final Point locOnScreen = release.getLocationOnScreen();
			final Point viewPanelLoc = viewPanel.getLocationOnScreen();
			final Point currentCoord = new Point((locOnScreen.x - viewPanelLoc.x), (locOnScreen.y - viewPanelLoc.y));
			Component currentComponent = viewPanel.getComponentAt(currentCoord);

			if ((currentComponent != null) && (currentNode != null)) {
				/** if currentNode contains child nodes then select the available ImageTiles and  allocate them **/
				if (!currentNode.isLeaf()) {
					int childCount = currentNode.getChildCount();
					int availableTileCount = viewPanel.getComponentCount();
					int size = (childCount > availableTileCount) ? availableTileCount : childCount;

					cameraSet = new VideoCamera[size];
					cameraLimit = size; // setting size of List to cameraLimit
					recurseAndFindLeaf(currentNode, cameraSet);
					cameraIndex = 0; // reset camera Index
					cameraLimit = -1; // reset cameraList limit

					IntStream.range(0, size)
					.forEach(count -> {
						VideoCamera cameraToGet = cameraSet[count];
						int tileIndex = count;
						camLookupTable.setChannel(cameraToGet.getId(), tileIndex);
						camLookupTable.saveChanges();
					});
				}
				/** if the current node contains no child node then allocate it onto the selected  ImageTile **/
				else {
					if (currentNode.getUserObject() instanceof VideoCamera) {
						selectedCam = (VideoCamera) currentNode.getUserObject();
						if (currentComponent instanceof ImageTile) {
							currentTile = (ImageTile) currentComponent;
							int sourceTileIndex = -1;
							// searching for ImageTile
							for (int index = 0; index < ImageTileBucket.NO_OF_TILES; index++) {
								if (tileBucket.getTile(index).equals(currentTile)) {
									sourceTileIndex = index;
									break;
								}
							}
							camLookupTable.setChannel(selectedCam.getId(), sourceTileIndex);
						}
					}
				}
			}
			dragOn = false;
		}
		/* reset Cursor */
		viewPanel.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
	}

	/**
	 * Finding Leaf nodes in a Tree upto a certain Value N
	 */
	public void recurseAndFindLeaf(DefaultMutableTreeNode node, VideoCamera[] cameraSet) {
		if (cameraIndex < cameraLimit) {
			if (node.isLeaf()) {
				if (node.getUserObject() instanceof VideoCamera) {
					cameraSet[cameraIndex++] = (VideoCamera) node.getUserObject();
				}
			} else {
				IntStream.range(0, node.getChildCount())
				.forEach(index -> recurseAndFindLeaf((DefaultMutableTreeNode) node.getChildAt(index), cameraSet));
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent press) {
		if (press.getButton() == MouseEvent.BUTTON1) {
			if (dragOn == false) {
				dragOn = true;
				currentTree = (JTree) press.getSource();
				TreePath path = currentTree.getClosestPathForLocation(press.getX(), press.getY());
				currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			}
			viewPanel.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); /* reset Cursor */
		} else if (press.getButton() == MouseEvent.BUTTON3) { /* right Click */
			JTree selectedTree = (JTree) press.getSource();
			int row = selectedTree.getClosestRowForLocation(press.getX(), press.getY());
			selectedTree.setSelectionRow(row);
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedTree
					.getClosestPathForLocation(press.getX(), press.getY()).getLastPathComponent();

			if ((selNode.isLeaf()) && (selNode.getUserObject() instanceof VideoCamera)) {
				VideoCamera selCam = (VideoCamera) selNode.getUserObject();

				TreePopupMenu popMenu = new TreePopupMenu();
				popMenu.setSelectedCam(selCam);
				popMenu.show((Component) press.getSource(), press.getX(), press.getY());
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent drag) {
		viewPanel.getRootPane().repaint();
		viewPanel.getRootPane().setCursor(new Cursor(Cursor.HAND_CURSOR)); 
		viewPanel.getRootPane().repaint();
	}

}
