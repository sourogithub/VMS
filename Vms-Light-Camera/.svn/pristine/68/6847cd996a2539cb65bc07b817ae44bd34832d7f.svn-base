package com.dss.vms.ui.components;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.actions.TreeEventActions;
import com.dss.vms.ui.components.tree.TreeModelRenderer;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.constants.VmsGridLayout;
import com.dss.vms.ui.constants.VmsIcons;
import com.dss.vms.ui.utility.VmsGridPainter;

/**
 * @author Sibendu
 */
public class CameraListPanel extends JPanel implements VmsIcons{
	private static final long serialVersionUID = -1424972264860164735L;
	public static final int DEFAULT_TOOLBAR_HEIGHT = 600;
	public static final int DEFAULT_TOOLBAR_WIDTH = 270;

	private static VmsGridPainter viewController = VmsGridPainter.getInstance();

	private JTree cameraListTree;
	//	private ViewPanel cameraViewPanel;
	private JTabbedPane cameraInfoPanel;
	private LiveViewPanel viewPanel;

	/**
	 * @param panel
	 */
	public CameraListPanel(LiveViewPanel panel) {
		this.viewPanel = panel;
		this.setMinimumSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.setPreferredSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.setSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.createUI();
	}

	/**
	 * initializes the Components and adds into respective Frames
	 */
	private void createUI() {
		setLayout(new BorderLayout());
		cameraInfoPanel = new JTabbedPane();
		cameraInfoPanel.setTabPlacement(SwingConstants.TOP);

		cameraListTree = new JTree(new DefaultTreeModel(null));
		cameraListTree.setRootVisible(false);
		cameraListTree.setBackground(VmsComponentColor.TREE_BG);

//		cameraLocationTree = new JTree(new DefaultTreeModel(null));
//		cameraLocationTree.setRootVisible(false);
//		cameraLocationTree.setBackground(ComponentColor.TREE_BG);

		cameraInfoPanel.add("Camera Model", cameraListTree);
//		tabbedPaneCameraDetails.add("Camera Location", cameraLocationTree);	

		JScrollPane cameraTreeScrollPane = new JScrollPane();
		cameraTreeScrollPane.getViewport().setLayout(new BorderLayout());
		cameraTreeScrollPane.getViewport().add(cameraInfoPanel, BorderLayout.CENTER);
		cameraTreeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		cameraTreeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cameraTreeScrollPane.setBorder(BorderFactory.createLineBorder(VmsComponentColor.TOOLBAR_PANEL_BG, 5));

		// creating Buttons
		JButton btnLayout1x1 = new JButton("1x1");
		btnLayout1x1.addActionListener(e -> {
			viewController.drawTiles(VmsGridLayout.Layout1x1);
		});
		JButton btnLayout2x2 = new JButton("2x2");
		btnLayout2x2.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout2x2);
		});
		JButton btnLayout3x3 = new JButton("3x3");
		btnLayout3x3.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout3x3);			
		});
		JButton btnLayout4x4 = new JButton("4x4");
		btnLayout4x4.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout4x4);
		});
		JButton btnLayout5x5 = new JButton("5x5");
		btnLayout5x5.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout5x5);
		});
		JButton btnLayout6x6 = new JButton("6x6");
		btnLayout6x6.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout6x6);
		});
		JButton btnLayout5x1 = new JButton("5x1");
		btnLayout5x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout5x1);
		});
		JButton btnLayout7x1 = new JButton("7x1");
		btnLayout7x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout7x1);
		});
		JButton btnLayout12x1 = new JButton("12x1");
		btnLayout12x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout12x1);
		});

		// creating Layout Selection Panel to put Buttons
		JPanel layoutSelectorPanel = new JPanel();
		layoutSelectorPanel.setLayout(new GridLayout(3, 3, 3, 3));
		layoutSelectorPanel.setBorder(BorderFactory.createTitledBorder(""));
		layoutSelectorPanel.setBackground(VmsComponentColor.LAYOUT_PANEL_BG);
		
		layoutSelectorPanel.add(btnLayout1x1);
		layoutSelectorPanel.add(btnLayout2x2);
		layoutSelectorPanel.add(btnLayout3x3);
		layoutSelectorPanel.add(btnLayout4x4);
//		layoutSelectorPanel.add(btnLayout5x5);
//		layoutSelectorPanel.add(btnLayout6x6);
		layoutSelectorPanel.add(btnLayout5x1);
		layoutSelectorPanel.add(btnLayout7x1);
//		layoutSelectorPanel.add(btnLayout12x1);

		cameraTreeScrollPane.getViewport().setBackground(VmsComponentColor.TOOLBAR_PANEL_BG);
		add(cameraTreeScrollPane, BorderLayout.CENTER);
		add(layoutSelectorPanel, BorderLayout.SOUTH);
		setBackground(VmsComponentColor.TOOLBAR_PANEL_BG);
	}

	/**
	 * @return tabbedPaneCameraTree reference
	 */
	public JTabbedPane getTabbedPaneCameraTree() {
		return cameraInfoPanel;
	}

	/**
	 * @return cameraStatusTree reference
	 */
	public JTree getCameraStatusTree() {
		return cameraListTree;
	}

	/**
	 * @param cameraStatusTree
	 */
	public void setCameraStatusTree(JTree cameraStatusTree) {
		this.cameraListTree = cameraStatusTree;
	}

	/**
	 * @param openNodePath
	 * @param closedNodePath
	 * @param LeafNodeImageIconPath
	 */
	private void setTreeRenderer(JTree tree) {
		DefaultTreeCellRenderer treeRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		treeRenderer.setOpenIcon(treeOpenNodeIcon);
		treeRenderer.setClosedIcon(treeClosedNodeIcon);
		treeRenderer.setLeafIcon(treeLeafNodeIcon);
	}

	/**
	 * @param cameraStatus
	 * @param cameraLocation
	 */
	public void updateTree(List<VideoCamera> cameraStatus, List<VideoCamera> cameraLocation) {
		cameraInfoPanel.removeAll();
		DefaultMutableTreeNode cameraModelNodes = new DefaultMutableTreeNode("Default");
//		cameralocationNodes = new DefaultMutableTreeNode("Location");

		for (VideoCamera cam : cameraStatus) {
			DefaultMutableTreeNode cameraNode = new DefaultMutableTreeNode(cam);
			cameraModelNodes.add(cameraNode);
		}
		DefaultTreeModel defaultTreeModel = new DefaultTreeModel(cameraModelNodes);
//		cameraLocationTree = new JTree(defaultTreeModel);
//		cameraLocationTree.setToolTipText("Camera location");
//		cameraLocationTree.setBackground(ComponentColor.TREE_BG);
//		TreeEventActions dragEvent = new TreeEventActions(viewPanel);
//		cameraLocationTree.addMouseListener(dragEvent);
//		cameraLocationTree.addMouseMotionListener(dragEvent);
//		cameraLocationTree.setCellRenderer(new TreeModelRenderer());
		cameraListTree = new JTree(defaultTreeModel);
		cameraListTree.setCellRenderer(new TreeModelRenderer());
		cameraListTree.setBackground(VmsComponentColor.TREE_BG);
		cameraListTree.addMouseListener(new TreeEventActions(viewPanel));
		cameraListTree.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent drag) {
				viewPanel.getRootPane().setCursor(new Cursor(Cursor.HAND_CURSOR)); /* setting cursor as Drag Cursor */
			}
		});

		cameraInfoPanel.add("Cameras", cameraListTree);
//		tabbedPaneCameraDetails.add("Camera Location", cameraLocationTree);
		/** updating icons on cell render **/
		setTreeRenderer(cameraListTree); 
//		setTreeRenderer(cameraLocationTree);
		/** reload the tree model to avoid text shrinking with ... */
		defaultTreeModel.reload();
	}

}
