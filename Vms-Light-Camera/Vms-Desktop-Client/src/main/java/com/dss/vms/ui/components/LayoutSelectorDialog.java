package com.dss.vms.ui.components;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import com.dss.vms.ui.components.toolbar.ToolbarPanel;
import com.dss.vms.ui.constants.VmsGridLayout;
import com.dss.vms.ui.utility.VmsGraphicsInfo;
import com.dss.vms.ui.utility.VmsGridPainter;

public class LayoutSelectorDialog extends JDialog implements WindowFocusListener {
	private static final int DIALOG_HEIGHT = 100;
	private static final int DIALOG_WIDTH = 200;
	private static VmsGridPainter viewController = VmsGridPainter.getInstance();

	private ToolbarPanel parent;

	public LayoutSelectorDialog(ToolbarPanel parent) {
		this.parent = parent;
		setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		if (VmsGraphicsInfo.translucencySupported()) {
			setOpacity(0.9f);
		}
		createUI();
		addWindowFocusListener(this);
		setUndecorated(true);
	}

	private void createUI() {
		JButton grid1x1 = new JButton("1x1");
		grid1x1.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout1x1);
		});
		
		JButton grid2x2 = new JButton("2x2");
		grid2x2.addActionListener(e-> {
			viewController.drawTiles(VmsGridLayout.Layout2x2);
		});
		
		JButton grid3x3 = new JButton("3x3");
		grid3x3.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout3x3);
		});
		
		JButton grid4x4 = new JButton("4x4");
		grid4x4.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout4x4);
		});
		
		JButton grid5x5 = new JButton("5x5");
		grid5x5.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout5x5);
		});
		
		JButton grid6x6 = new JButton("6x6");
		grid6x6.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout6x6);
		});
		
		JButton grid5x1 = new JButton("5x1");
		grid5x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout5x1);
		});
		
		JButton grid7x1 = new JButton("7x1");
		grid7x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout7x1);
		});
		
		JButton grid12x1 = new JButton("12x1");
		grid12x1.addActionListener(e->{
			viewController.drawTiles(VmsGridLayout.Layout12x1);
		});

		setLayout(new GridLayout(3, 3, 5, 5));
		setType(Type.UTILITY);
		add(grid1x1);
		add(grid2x2);
		add(grid3x3);
		add(grid4x4);
		add(grid5x5);
		add(grid6x6);
		add(grid5x1);
		add(grid7x1);
		add(grid12x1);
	}


	@Override
	public void windowGainedFocus(WindowEvent arg0) {}

	@Override
	public void windowLostFocus(WindowEvent wfLost) {
//		parent.closeLayoutDialog();
	}
}
