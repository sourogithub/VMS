package com.dss.vms.ui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.components.tree.TreePopupMenu;
import com.dss.vms.ui.data.CameraLookupTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author Sibendu
 */
public class TreeActions extends MouseAdapter {
	private static CameraLookupTable  camLookupTable = CameraLookupTable.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	
	private DefaultMutableTreeNode currentNode = null;
	private VideoCamera camera = null;
	private LiveViewPanel viewPanel = null;
	public boolean dragOn = false;

	private int cameralistSize = -1;
	private int cameraIndex = 0;

	/**
	 * @param viewPanel
	 */
	public TreeActions(LiveViewPanel viewPanel) {
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
				/**
				 * if currentNode contains child nodes then select the available 
				 * ImageTiles and allocate them
				 **/
				if (!currentNode.isLeaf()) {
					int childCount = currentNode.getChildCount();
					int availableTileCount = viewPanel.getComponentCount();
					int size = (childCount > availableTileCount) ? availableTileCount : childCount;

					VideoCamera cameras[] = new VideoCamera[size];
					cameralistSize = size;
					recurseAndFindLeaf(currentNode, cameras);
					cameraIndex = 0; // reset camera Index
					cameralistSize = -1; // reset cameraList limit

					for (int count = 0; count < size; count++) {
						VideoCamera cameraToGet = cameras[count];
						int tileIndex = count;
						camLookupTable.setChannel(cameraToGet.getId(), tileIndex);
					}
				}
				/** if the current node contains no child node then allocate it onto the selected  ImageTile **/
				else {
					if (currentNode.getUserObject() instanceof VideoCamera) {
						camera = (VideoCamera) currentNode.getUserObject();
						if (currentComponent instanceof ImageTile) {
							ImageTile currentTile = (ImageTile) currentComponent;
							int sourceTileIndex = -1;

							for (int index = 0; index < ImageTileBucket.NO_OF_TILES; index++) {
								if (tileBucket.getTile(index).equals(currentTile)) {
									sourceTileIndex = index;
									break;
								}
							}
							camLookupTable.setChannel(camera.getId(), sourceTileIndex);
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
		if (cameraIndex < cameralistSize) {
			if (node.isLeaf()) {
				if (node.getUserObject() instanceof VideoCamera) {
					cameraSet[cameraIndex++] = (VideoCamera) node.getUserObject();
				}
			} else {
				for (int index = 0; index < node.getChildCount(); index++) {
					recurseAndFindLeaf((DefaultMutableTreeNode) node.getChildAt(index), cameraSet);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent press) {
		if (press.getButton() == MouseEvent.BUTTON1) {
			if (dragOn == false) {
				dragOn = true;
				JTree currentTree = (JTree) press.getSource();
				TreePath path = currentTree.getClosestPathForLocation(press.getX(), press.getY());
				currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			}
			viewPanel.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else if (press.getButton() == MouseEvent.BUTTON3) { 
			JTree selectedTree = (JTree) press.getSource();
			int row = selectedTree.getClosestRowForLocation(press.getX(), press.getY());
			selectedTree.setSelectionRow(row);
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedTree
					.getClosestPathForLocation(press.getX(), press.getY()).getLastPathComponent();

			if ((selectedNode.isLeaf()) && (selectedNode.getUserObject() instanceof VideoCamera)) {
				VideoCamera camera = (VideoCamera) selectedNode.getUserObject();
				TreePopupMenu popup = new TreePopupMenu(camera);
				popup.show((Component) press.getSource(), press.getX(), press.getY());
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
