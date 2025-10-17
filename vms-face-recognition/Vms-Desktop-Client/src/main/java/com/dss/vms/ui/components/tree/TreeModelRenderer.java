package com.dss.vms.ui.components.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.dss.vms.common.data.VideoCamera;

public class TreeModelRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof VideoCamera) {
			VideoCamera camera = (VideoCamera) node.getUserObject();
			return super.getTreeCellRendererComponent(tree, camera.getName(), sel, expanded, leaf, row, hasFocus);
		}
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}

}
