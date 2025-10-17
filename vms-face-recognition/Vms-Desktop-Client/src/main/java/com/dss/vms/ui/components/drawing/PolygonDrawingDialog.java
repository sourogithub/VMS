package com.dss.vms.ui.components.drawing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import com.dss.vms.common.constants.AnalyticType;
import com.dss.vms.common.data.Region;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.data.CameraLookupTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.view.panel.ImageTile;

/**
 * @author Sibendu
 */
public class PolygonDrawingDialog extends JDialog implements ActionListener, WindowFocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_DIALOG_HEIGHT = 400;
	private static final int DEFAULT_DIALOG_WIDTH = 600;
	private static final int H_GAP = 10;
	private static final int V_GAP = 10;
	private static final int PADDING = 5;
	
	private static int currentDialogWidth;
	private static int currentDialogHeight;

	private static CameraLookupTable  channelLookupTable = CameraLookupTable.getInstance();
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();

	@SuppressWarnings("rawtypes")
	private Vector<Vector> rowData = new Vector<Vector>();
	private DefaultTableModel model;

	private PolygonDrawingPanel drawingPane;
	private JPanel toolbarPanel;
	private JScrollPane parameterPanel;
	private JTable paramTable;
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnClearRegions;
	private ImageTile currentTile = null;

	private VideoCamera currentCamera = null;

	/**
	 * @param currentTile
	 */
	public PolygonDrawingDialog(ImageTile currentTile) {
		super();
		this.setTitle("Intruder Detection");
		this.currentTile = currentTile;

		int index = ImageTileBucket.find(currentTile);
		if (index != -1) {
			int channel = channelLookupTable.getChannel(index);
			if (channel != CameraLookupTable.INVALID_CHANNEL) {
				VideoCamera camera = cameraBucket.getCamera(channel);
				this.currentCamera = camera;
				setTitle("Intruder Detection");
				initialiseRegions();
			} else {
				JPanel messagePanel = new JPanel();
				JLabel label = new JLabel("No Video Stream running on this view pane");
				label.setForeground(Color.WHITE);
				messagePanel.add(label);
				JOptionPane.showMessageDialog(currentTile, messagePanel);
			}
		}
		setResizable(false);
		setAlwaysOnTop(true);
		addWindowFocusListener(this);
		setUndecorated(false);

		createUI();

	}

	/**
	 * Create The UI Components
	 */
	private void createUI() {
		setLayout(null);

		BufferedImage bImage = currentTile.getBufferedImage();

		Dimension size = new Dimension(DEFAULT_DIALOG_WIDTH,
				DEFAULT_DIALOG_HEIGHT); /* Setting DEFAULT DIALOG DIMENSION */
		Dimension imageSize = new Dimension(bImage.getWidth(), bImage.getHeight());

		drawingPane = new PolygonDrawingPanel(imageSize, bImage, this);
		drawingPane.setSize(imageSize);
		drawingPane.setMinimumSize(imageSize);
		drawingPane.setPreferredSize(imageSize);

		JScrollPane scrollpane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollpane.getViewport().add(drawingPane);
		scrollpane.getViewport().setSize(size);
		scrollpane.getViewport().setPreferredSize(size);
		scrollpane.getViewport().setMinimumSize(size);

		scrollpane.setSize(size);
		scrollpane.setMinimumSize(size);
		scrollpane.setPreferredSize(size);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnSave = new JButton("Save");
		btnSave.setBackground(Color.green);
		btnSave.addActionListener(this);
		btnClearRegions = new JButton("Clear All");
		btnClearRegions.setBackground(Color.RED);
		btnClearRegions.addActionListener(this);

		// create Toolbar panel containing OK and Cancel options
		toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		toolbarPanel.add(btnSave);
		toolbarPanel.add(btnCancel);
		toolbarPanel.add(btnClearRegions);
		toolbarPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		toolbarPanel.setBackground(new Color(82, 91, 104));

		// create the Panel containing Parameter Table
		initTable();

		parameterPanel = new JScrollPane(paramTable);
		parameterPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		parameterPanel.setPreferredSize(new Dimension(DEFAULT_DIALOG_WIDTH, 200)); /* Parameter Table Height = 100 */
		parameterPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		parameterPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollpane.setBounds(H_GAP, V_GAP, size.width, size.height);
		parameterPanel.setBounds(H_GAP, (V_GAP + size.height + PADDING), size.width, 200);
		toolbarPanel.setBounds(H_GAP, (V_GAP + size.height + PADDING + parameterPanel.getHeight() + PADDING),
				parameterPanel.getWidth(), 40); /* setting 40 as toolbar panel height */

		add(scrollpane);
		add(parameterPanel);
		add(toolbarPanel);

		currentDialogWidth = (2 * H_GAP) + scrollpane.getWidth();
		currentDialogHeight = (2 * V_GAP) + (2 * PADDING) + scrollpane.getHeight() + parameterPanel.getHeight()
				+ toolbarPanel.getHeight() + 30; /* 30 = OFFSET */

		setSize(currentDialogWidth, currentDialogHeight);
		setPreferredSize(new Dimension(currentDialogWidth, currentDialogHeight));
		setMinimumSize(new Dimension(currentDialogWidth, currentDialogHeight));
	}

	/**
	 * Creates The JTable for parameter to set
	 */
	private void initTable() {
		model = new DefaultTableModel();
		model.addColumn("Zone");
		model.addColumn("Parameter 1");
		model.addColumn("Parameter 2");
		model.addColumn("Parameter 3");
		model.addColumn("Parameter 4");
		paramTable = new JTable(model); /* creating a table with blank Model */
		paramTable.getTableHeader().setReorderingAllowed(false); /* disabling Column reordering by the User */
		paramTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void initialiseRegions() {
		if (currentCamera != null) {
			VmsResponse response = sessionManager.getRegion(currentCamera.getId(), AnalyticType.INTRUDER);
			ArrayList<Region> regions = (ArrayList<Region>) response.getResponse();

			if (!regions.isEmpty() && drawingPane != null) {
				System.err.println(regions);
				drawingPane.setRegions(regions);
			}
		}
	}

	/**
	 * Adds Data to Table
	 */
	public void addDataRowToTable(Vector<Integer> data) {
		rowData.add(data);
		model.addRow(data);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		Object actionSource = action.getSource();
		if (actionSource.equals(btnCancel)) {
			this.setVisible(false);
			this.dispose();
		} else if (actionSource.equals(btnSave)) {
			JPanel messagePan = new JPanel();
			JLabel message = new JLabel("Saved");
			message.setForeground(Color.white);

			if (currentCamera != null) {
				System.out.println("DRAW Control Points");
				for (Region reg : drawingPane.getRegions()) {
					System.out.println(reg.getControlPoint());
				}

				VmsResponse response = sessionManager.setRegion(currentCamera.getId(), AnalyticType.INTRUDER,
						drawingPane.getRegions());
//				VmsResponse response = new VmsResponse(CommonResponseCode.SUCCESS);

				if (response.isSuccess()) {
					messagePan.add(message);
					JOptionPane.showMessageDialog(this, messagePan, "Saved", JOptionPane.INFORMATION_MESSAGE);

//					VmsResponse startAnalyticResponse  = sessionManager.startAnaytic(currentCamera.getId(), AnalyticType.INTRUDER);
//					if(startAnalyticResponse.isSuccess()) {
//						System.out.println("SUCCESSFULLY STARTED Analytics");
//					} else {
//						message.setText("Failed to Start Analytic on " + currentCamera.getName());
//						messagePan.add(message);
//						JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.INFORMATION_MESSAGE);
//					}

					this.setVisible(false);
					this.dispose();
				} else {
					message.setText("Failed to set Regions");
					messagePan.add(message);
					JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.INFORMATION_MESSAGE);
				}

			} else {
				message.setText("Failed to save Regions As no Camera Streaming Running on this Pane");
				messagePan.add(message);
				JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (action.getSource().equals(btnClearRegions)) {
			drawingPane.clearDrawing();
			resetTable();
			if (currentCamera != null) {
				sessionManager.setRegion(currentCamera.getId(), AnalyticType.INTRUDER, drawingPane.getRegions());
			} else {
				JPanel messagePan = new JPanel();
				JLabel message = new JLabel("Saved");
				message.setForeground(Color.white);
				message.setText("Failed to clear Regions As no Camera Streaming Running on this Pane");
				messagePan.add(message);
				JOptionPane.showMessageDialog(this, messagePan, "Failure", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// System.out.println("Window Focus lost now delete the intemediate polygon on
		// drawing Panel "+panelID);
		// drawingPane.resetIntemediatePolygonDrawing();
	}

	/**
	 * reset Table data
	 */
	public void resetTable() {
		DefaultTableModel model = (DefaultTableModel) paramTable.getModel();
		for (int count = (model.getRowCount() - 1); count >= 0; count--) {
			model.removeRow(count);
		}
	}

	/**
	 * @return the paramTable
	 */
	public JTable getParamTable() {
		return paramTable;
	}
}
