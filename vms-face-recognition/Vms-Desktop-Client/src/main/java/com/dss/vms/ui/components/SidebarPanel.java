package com.dss.vms.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.ui.actions.TreeActions;
import com.dss.vms.ui.components.tree.TreeModelRenderer;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;

/**
 * @author Sibendu
 */
public class SidebarPanel extends JPanel implements Icons{
	private static final long serialVersionUID = -1424972264860164735L;
	public static final int DEFAULT_TOOLBAR_HEIGHT = 300;
	public static final int DEFAULT_TOOLBAR_WIDTH = 270;
	
	public static final int GRID_BUTTON_ROWS = 1;
	public static final int GRID_BUTTON_COLS = 2;
//	private static GridPainter viewController = GridPainter.getInstance();

	private JTree cameraTree;
	private JTabbedPane tabbedPane;
	private TreeActions treeActions;
	private EventNotificationPanel notification;

	public SidebarPanel() {
		this.setMinimumSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.setPreferredSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.setSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, DEFAULT_TOOLBAR_HEIGHT));
		this.setupComponents();
	}

	/**
	 * initializes the Components and adds into respective Frames
	 */
	private void setupComponents() {
		cameraTree = new JTree(new DefaultTreeModel(null));
		cameraTree.setRootVisible(false);
		cameraTree.setBackground(ComponentColors.TREE_BG);

		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(SwingConstants.TOP);
		tabbedPane.add("Cameras", cameraTree);

		JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setBorder(BorderFactory.createLineBorder(ComponentColors.TOOLBAR_PANEL_BG, 5));
		JViewport viewport = scrollpane.getViewport();
		viewport.setLayout(new BorderLayout());
		viewport.add(tabbedPane, BorderLayout.CENTER);
		viewport.setBackground(ComponentColors.TOOLBAR_PANEL_BG);

		JPanel wrapPanel = new JPanel(new BorderLayout(5, 5));
		wrapPanel.add(scrollpane, BorderLayout.CENTER);
		wrapPanel.setMinimumSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, 200));
		
		this.notification = new EventNotificationPanel();
		notification.setMinimumSize(new Dimension(DEFAULT_TOOLBAR_WIDTH, 200));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wrapPanel, notification);
		split.setDividerSize(1);
		
		this.setBackground(ComponentColors.TOOLBAR_PANEL_BG);
		this.setLayout(new BorderLayout(5, 5));
		this.add(split, BorderLayout.CENTER);
	}
	
	/**
	 * events generated
	 * @param events
	 */
	public void eventGenerated(GenericEvent ...events) {
		this.notification.eventGenerated(events);
	}
	
	/**
	 * Camera Tree Action MouseListener
	 * @param action
	 */
	public void setTreeMouseListener(TreeActions action) {
		this.treeActions = action;
	}
	
	/**
	 * @param cameraStatus
	 * @param cameraLocation
	 */
	public void updateCameraTree(List<VideoCamera> cameraStatus, List<VideoCamera> cameraLocation) {
		tabbedPane.removeAll();
		DefaultMutableTreeNode cameraModelNodes = new DefaultMutableTreeNode("Default");

		for (VideoCamera cam : cameraStatus) {
			DefaultMutableTreeNode cameraNode = new DefaultMutableTreeNode(cam);
			cameraModelNodes.add(cameraNode);
		}
		DefaultTreeModel defaultTreeModel = new DefaultTreeModel(cameraModelNodes);
		cameraTree = new JTree(defaultTreeModel);
		cameraTree.setCellRenderer(new TreeModelRenderer());
		cameraTree.setBackground(ComponentColors.TREE_BG);
		if (treeActions != null) {
			cameraTree.addMouseListener(treeActions);
			cameraTree.addMouseMotionListener(treeActions);
		}
		tabbedPane.add("Cameras", cameraTree);
		/** updating icons on cell render **/
		setTreeRenderer(cameraTree); 
		/** reload the tree model to avoid text shrinking with ... */
		defaultTreeModel.reload();
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
}
