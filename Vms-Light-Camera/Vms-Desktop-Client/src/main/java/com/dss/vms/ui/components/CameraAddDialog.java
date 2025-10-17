package com.dss.vms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.toolbar.ToolbarPanel;
import com.dss.vms.ui.constants.SupportedCameraModel;
import com.dss.vms.ui.constants.VmsComponentColor;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.utility.CameraFormValidator;

/**
 * Add Camera Form {@link ToolbarPanel}
 * @author dss-02
 */
public class CameraAddDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CameraAddDialog.class);
	public static final int DIALOG_WIDTH = 600;
	public static final int DIALOG_HEIGHT = 400;
	private static final int GRID_ROWS = 11;
	private static final int GRID_COLS = 2;
	private static final int PROTOCOL_COLUMN_SIZE = 3;
	private static final int IP_COL_SIZE = 8;
	private static final int PORT_COL_SIZE = 3;
	private static final int PATH_COL_SIZE = 10;
	
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();

	private boolean ptz = false;
	private boolean isadvanceMode = true;
	private DesktopClientApplication parent;

	private JComboBox<Object> jCbModel;
	private JTextField tfName;
	private JTextField tfIp;
	private JTextField tfAnalyticStreamIp;
	private JTextField tfAnalyticStreamProtocol;
	private JTextField tfAnalyticStreamPort;
	private JTextField tfAnalyticStreamPath;
	private JTextField tfMicroStreamIp;
	private JTextField tfMicroStreamProtocol;
	private JTextField tfMicroStreamPort;
	private JTextField tfMicroStreamPath;
	private JTextField tfMiniStreamIp;
	private JTextField tfMiniStreamProtocol;
	private JTextField tfMiniStreamPort;
	private JTextField tfMiniStreamPath;
	private JTextField tfMacroStreamIp;
	private JTextField tfMacroStreamProtocol;
	private JTextField tfMacroStreamPort;
	private JTextField tfMacroStreamPath;
	private JTextField tfStreamingUsername;
	private JPasswordField tfStreamingPassword;
	private JTextField tfControlUsername;
	private JPasswordField tfControlPassword;
	private JCheckBox cbPtz;
	private JCheckBox cbAdvance;
	private JButton btnOK;
	private JButton btnCancel;

	public CameraAddDialog(DesktopClientApplication parent) {
		addModality();
		this.parent = parent;
		setTitle("Fill Camera Information");
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		setLocationRelativeTo(parent);
		setResizable(false);
		getContentPane().setBackground(new Color(89, 81, 104));
		setUndecorated(false);
		setAlwaysOnTop(true);
		createUI();
	}

	private void addModality() {
		this.setModal(true);
		this.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
	}

	/**
	 * Creates the UI
	 */
	private void createUI() {
		this.getContentPane().setLayout(new BorderLayout());
		JPanel form = new JPanel();
		form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(5, 5, 0, 5, Color.GRAY),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		form.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, 1, 1));

		/** Camera Name **/
		JLabel lblName = new JLabel("Camera Name:");
		lblName.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfName = new JTextField();

		/** ANALYTIC STREAM CONTROLS **/
		JLabel lblAnlStreamURL = new JLabel("Analytic Stream Address: ");
		lblAnlStreamURL.setForeground(VmsComponentColor.LABEL_COLOR);
		JPanel analyticStreamPanel = new JPanel();
		this.tfAnalyticStreamProtocol = new JTextField(PROTOCOL_COLUMN_SIZE);
		this.tfAnalyticStreamIp = new JTextField(IP_COL_SIZE);
		this.tfAnalyticStreamPort = new JTextField(PORT_COL_SIZE);
		this.tfAnalyticStreamPath = new JTextField(PATH_COL_SIZE);
		analyticStreamPanel.add(tfAnalyticStreamProtocol);
		JLabel anProto = new JLabel("://");
		anProto.setForeground(VmsComponentColor.LABEL_COLOR);
		analyticStreamPanel.add(anProto);
		analyticStreamPanel.add(tfAnalyticStreamIp);
		JLabel anPort = new JLabel(":");
		anPort.setForeground(VmsComponentColor.LABEL_COLOR);
		analyticStreamPanel.add(anPort);
		analyticStreamPanel.add(tfAnalyticStreamPort);
		JLabel anPath = new JLabel("/");
		anPath.setForeground(VmsComponentColor.LABEL_COLOR);
		analyticStreamPanel.add(anPath);
		analyticStreamPanel.add(tfAnalyticStreamPath);

		/** MICRO STREAM CONTROLS **/
		JLabel lblMicroStreamURL = new JLabel("Micro Stream Address: ");
		lblMicroStreamURL.setForeground(VmsComponentColor.LABEL_COLOR);
		JPanel microStreamPanel = new JPanel();
		this.tfMicroStreamProtocol = new JTextField(PROTOCOL_COLUMN_SIZE);
		this.tfMicroStreamIp = new JTextField(IP_COL_SIZE);
		this.tfMicroStreamPort = new JTextField(PORT_COL_SIZE);
		this.tfMicroStreamPath = new JTextField(PATH_COL_SIZE);
		microStreamPanel.add(tfMicroStreamProtocol);
		JLabel microProto = new JLabel("://");
		microProto.setForeground(VmsComponentColor.LABEL_COLOR);
		microStreamPanel.add(microProto);
		microStreamPanel.add(tfMicroStreamIp);
		JLabel microPort = new JLabel(":");
		microPort.setForeground(VmsComponentColor.LABEL_COLOR);
		microStreamPanel.add(microPort);
		microStreamPanel.add(tfMicroStreamPort);
		JLabel microPath = new JLabel("/");
		microPath.setForeground(VmsComponentColor.LABEL_COLOR);
		microStreamPanel.add(microPath);
		microStreamPanel.add(tfMicroStreamPath);

		/** MINI STREAM CONTROLS **/
		JLabel lblMiniStreamURL = new JLabel("Mini Stream Address: ");
		lblMiniStreamURL.setForeground(VmsComponentColor.LABEL_COLOR);
		JPanel miniStreamPanel = new JPanel();
		this.tfMiniStreamProtocol = new JTextField(PROTOCOL_COLUMN_SIZE);
		this.tfMiniStreamIp = new JTextField(IP_COL_SIZE);
		this.tfMiniStreamPort = new JTextField(PORT_COL_SIZE);
		this.tfMiniStreamPath = new JTextField(PATH_COL_SIZE);
		miniStreamPanel.add(tfMiniStreamProtocol);
		JLabel miniProto = new JLabel("://");
		miniProto.setForeground(VmsComponentColor.LABEL_COLOR);
		miniStreamPanel.add(miniProto);
		miniStreamPanel.add(tfMiniStreamIp);
		JLabel miniPort = new JLabel(":");
		miniPort.setForeground(VmsComponentColor.LABEL_COLOR);
		miniStreamPanel.add(miniPort);
		miniStreamPanel.add(tfMiniStreamPort);
		JLabel miniPath = new JLabel("/");
		miniPath.setForeground(VmsComponentColor.LABEL_COLOR);
		miniStreamPanel.add(miniPath);
		miniStreamPanel.add(tfMiniStreamPath);

		/** MACRO STREAM CONTROLS **/
		JLabel lblMacroStreamURL = new JLabel("Macro Stream Address: ");
		lblMacroStreamURL.setForeground(VmsComponentColor.LABEL_COLOR);
		JPanel macroStreamPanel = new JPanel();
		this.tfMacroStreamProtocol = new JTextField(PROTOCOL_COLUMN_SIZE);
		this.tfMacroStreamIp = new JTextField(IP_COL_SIZE);
		this.tfMacroStreamPort = new JTextField(PORT_COL_SIZE);
		this.tfMacroStreamPath = new JTextField(PATH_COL_SIZE);
		macroStreamPanel.add(tfMacroStreamProtocol);
		JLabel macroProto = new JLabel("://");
		macroProto.setForeground(VmsComponentColor.LABEL_COLOR);
		macroStreamPanel.add(macroProto);
		macroStreamPanel.add(tfMacroStreamIp);
		JLabel macroPort = new JLabel(":");
		macroPort.setForeground(VmsComponentColor.LABEL_COLOR);
		macroStreamPanel.add(macroPort);
		macroStreamPanel.add(tfMacroStreamPort);
		JLabel macroPath = new JLabel("/");
		macroPath.setForeground(VmsComponentColor.LABEL_COLOR);
		macroStreamPanel.add(macroPath);
		macroStreamPanel.add(tfMacroStreamPath);

		// Ptz Checkbox
		this.cbPtz = new JCheckBox("PTZ Camera", false);
		this.cbPtz.setForeground(VmsComponentColor.LABEL_COLOR);
		this.cbPtz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				if ((cbPtz.isSelected()) || (!cbPtz.isSelected())) {
					ptz = !ptz;
				}
			}
		});

		// Advance Checkbox
		this.cbAdvance = new JCheckBox("Advance");
		this.cbAdvance.setForeground(Color.white);
		this.cbAdvance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((cbAdvance.isSelected()) || (!cbAdvance.isSelected())) {
					isadvanceMode = !isadvanceMode;
					setEnableAction(isadvanceMode);
					CameraAddDialog.this.repaint();
				}
			}
		});

		// Streaming Username
		JLabel lblStrmUsername = new JLabel("Streaming Username");
		lblStrmUsername.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfStreamingUsername = new JTextField();

		// Streaming Password
		JLabel lblStrmPassword = new JLabel("Streaming Password");
		lblStrmPassword.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfStreamingPassword = new JPasswordField();

		// Control Username
		JLabel lblCtrlUsername = new JLabel("Control Username");
		lblCtrlUsername.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfControlUsername = new JTextField();

		// Control Password
		JLabel lblCtrlPassword = new JLabel("Control Password");
		lblCtrlPassword.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfControlPassword = new JPasswordField();

		/** IP ADDRESS KEY PRESS ACTIONS **/
		JLabel lblIp = new JLabel("IP Address");
		lblIp.setForeground(VmsComponentColor.LABEL_COLOR);
		this.tfIp = new JTextField();
		this.tfIp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEv) {
				String text = tfIp.getText();
				tfAnalyticStreamIp.setText(text);
				tfMicroStreamIp.setText(text);
				tfMacroStreamIp.setText(text);
				tfMiniStreamIp.setText(text);
			}
		});

		/** COMBO BOX CONTROLS **/
		JLabel lblModel = new JLabel("Model Name:");
		lblModel.setForeground(VmsComponentColor.LABEL_COLOR);
		this.jCbModel = new JComboBox<>(SupportedCameraModel.values());
		this.jCbModel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jCbModel.addActionListener(new ModelSelectionAction());
		this.jCbModel.setSelectedItem(SupportedCameraModel.CUSTOM);
		this.setEnableAction(true); /* Default Enabled */
		this.cbPtz.setEnabled(true);
		this.cbAdvance.setSelected(isadvanceMode);

		/** BUTTON CONTROLS **/
		this.btnOK = new JButton("OK");
		this.btnOK.setSize(btnOK.getPreferredSize());
		this.btnOK.addActionListener(new SubmitAction());
		/* setting ok button as default button for Enter action */
		this.getRootPane().setDefaultButton(btnOK);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setSize(btnCancel.getPreferredSize());
		btnCancel.addActionListener(e-> { closeDialog(); });
		
		/** Add Components to Form **/
		drawComponents(form, new Component[] {
				/** Donot change the order **/
				lblModel, jCbModel, lblName, tfName,  lblIp, tfIp, lblStrmUsername,
				tfStreamingUsername, lblStrmPassword, tfStreamingPassword, 
				/** lblCtrlUsername, tfControlUsername, lblCtrlPassword, tfControlPassword, **/ 
				cbAdvance, cbPtz, lblAnlStreamURL, analyticStreamPanel,
				lblMicroStreamURL, microStreamPanel, lblMiniStreamURL, miniStreamPanel, lblMacroStreamURL,
				macroStreamPanel });

		JPanel control = new JPanel(new FlowLayout());
		control.add(btnOK);
		control.add(btnCancel);
		control.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.GRAY),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		add(form, BorderLayout.CENTER);
		add(control, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * add components to Display panel
	 * 
	 * @param form
	 * @param components
	 */
	private void drawComponents(JPanel form, Component[] components) {
		for (Component component : components) {
			form.add(component);
		}
	}

	/**
	 * Set Mini, Macro, Analytic TextFields Enables/Disabled
	 * 
	 * @param active
	 */
	private void setEnableAction(boolean active) {
		tfIp.setEnabled(!active);
		cbPtz.setEnabled(active);
		tfAnalyticStreamIp.setEnabled(active);
		tfAnalyticStreamProtocol.setEnabled(active);
		tfAnalyticStreamPort.setEnabled(active);
		tfAnalyticStreamPath.setEnabled(active);

		tfMicroStreamProtocol.setEnabled(active);
		tfMicroStreamIp.setEnabled(active);
		tfMicroStreamPort.setEnabled(active);
		tfMicroStreamPath.setEnabled(active);

		tfMacroStreamProtocol.setEnabled(active);
		tfMacroStreamIp.setEnabled(active);
		tfMacroStreamPort.setEnabled(active);
		tfMacroStreamPath.setEnabled(active);

		tfMiniStreamProtocol.setEnabled(active);
		tfMiniStreamIp.setEnabled(active);
		tfMiniStreamPort.setEnabled(active);
		tfMiniStreamPath.setEnabled(active);
	}
	
	/**
	 * submit action on OK click
	 * @author dss-02
	 *
	 */
	private class SubmitAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				boolean validationSuccess = true;
				String cameraName = getCameraName();
				String ip = getIPAddress();
				String analyticStream = getAnalyticStreamURL();
				String macroStream = getMacroStreamURL();
				String microStream = getMicroStreamURL();
				String miniStream = getMiniStreamURL();

				if (!CameraFormValidator.validateCameraName(cameraName)) {
					showErrorMessage("Enter valid camera name", "Validation Failed");
					validationSuccess = false;
				}

				if (!CameraFormValidator.validateIPAddress(ip) || !CameraFormValidator.validateStreamAddresses(
						new String[] { analyticStream, macroStream, miniStream, microStream })) {
					showErrorMessage("Enter valid Address", "Validation Failed");
					validationSuccess = false;
				}

				if (validationSuccess) {
					LOGGER.info("camera Validations Passed...[ipAddress = " + ip + ", MacroStreamAddress = "
							+ macroStream + ", MicroStreamAddress = " + microStream + " miniStreamAddress = "
							+ miniStream + " ]");

					/**
					 * now adding stream through StreamMaster IF statements to be added to validate
					 **/
					VideoCamera camera = new VideoCamera(ip, cameraName, ptz, getModelName(), analyticStream,
							microStream, miniStream, macroStream, StreamType.ANALYTIC);
					camera.setStreamingUsername(getStreamingUsername());
					camera.setStreamingPassword(getStreamingPassword());

					VmsResponse response = sessionManager.addCamera(camera); /* adding camera to SessionManager */
					if (response.isSuccess()) {
						cameraBucket.putCamera(camera);
						parent.updateTreeUI();
						closeDialog();
					} else {
						showErrorMessage("Failed to add Camera. Network Error, Please try again.", "Error");
					}

				}
			} catch (Throwable ex) {
				ex.printStackTrace();
				showErrorMessage("Failed to add Camera. Fatal Error Encountered", "Error");
			}
		}
	}
	
	/**
	 * camera model selection action
	 * @author dss-02
	 *
	 */
	private class ModelSelectionAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent action) {
			if (jCbModel.getSelectedItem().equals("Custom")) {
				setEnableAction(true);
				isadvanceMode = true;
				cbAdvance.setSelected(isadvanceMode);
			} else {
				isadvanceMode = false;
				cbAdvance.setSelected(isadvanceMode);
				setEnableAction(false);
				SupportedCameraModel model = (SupportedCameraModel) jCbModel.getSelectedItem();

				cbPtz.setSelected(false);
				ptz = model.isPtz();

				// Setting Text value to all Fields
				tfAnalyticStreamProtocol.setText(model.getAnalyticProto().toString().toLowerCase());
				tfMicroStreamProtocol.setText(model.getMicroProto().toString().toLowerCase());
				tfMacroStreamProtocol.setText(model.getMacroProto().toString().toLowerCase());
				tfMiniStreamProtocol.setText(model.getMiniProto().toString().toLowerCase());

				tfAnalyticStreamPort.setText(String.valueOf(model.getAnalyticPort()));
				tfMicroStreamPort.setText(String.valueOf(model.getMicroPort()));
				tfMacroStreamPort.setText(String.valueOf(model.getMacroPort()));
				tfMiniStreamPort.setText(String.valueOf(model.getMiniPort()));

				tfAnalyticStreamPath.setText(model.getAnalyticUrl());
				tfMicroStreamPath.setText(model.getMicroUrl());
				tfMacroStreamPath.setText(model.getMacroUrl());
				tfMiniStreamPath.setText(model.getMiniUrl());
			}
		}
	}
	
	private void closeDialog() {
		this.setVisible(false);
		this.dispose();
	}

	private void showErrorMessage(String message, String heading) {
		JPanel messagePanel = new JPanel();
		JLabel label = new JLabel(message);
		label.setForeground(VmsComponentColor.LABEL_COLOR);
		messagePanel.add(label);
		JOptionPane.showMessageDialog(this, messagePanel, heading, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * @return Camera Model Name
	 */
	private String getModelName() {
		return jCbModel.getSelectedItem().toString();
	}

	/**
	 * @return IP address
	 */
	private String getIPAddress() {
		return new String(this.tfAnalyticStreamProtocol.getText() + "://" + this.tfAnalyticStreamIp.getText() + ":"
				+ this.tfAnalyticStreamPort.getText() + "/" + this.tfAnalyticStreamPath.getText());
	}

	/**
	 * @return AnalyticStreamURL
	 */
	private String getAnalyticStreamURL() {
		return new String(this.tfAnalyticStreamProtocol.getText() + "://" + this.tfAnalyticStreamIp.getText() + ":"
				+ this.tfAnalyticStreamPort.getText() + "/" + this.tfAnalyticStreamPath.getText());
	}

	/**
	 * @return MicroStreamURL
	 */
	private String getMicroStreamURL() {
		return new String(this.tfMicroStreamProtocol.getText() + "://" + this.tfMicroStreamIp.getText() + ":"
				+ this.tfMicroStreamPort.getText() + "/" + this.tfMicroStreamPath.getText());
	}

	/**
	 * @return MiniStreamURL
	 */
	private String getMiniStreamURL() {
		return new String(this.tfMiniStreamProtocol.getText() + "://" + this.tfMiniStreamIp.getText() + ":"
				+ this.tfMiniStreamPort.getText() + "/" + this.tfMiniStreamPath.getText());
	}

	/**
	 * @return MiniStreamURL
	 */
	private String getMacroStreamURL() {
		return new String(this.tfMacroStreamProtocol.getText() + "://" + this.tfMacroStreamIp.getText() + ":"
				+ this.tfMacroStreamPort.getText() + "/" + this.tfMacroStreamPath.getText());
	}

	/**
	 * @return StreamingUsername
	 */
	private String getStreamingUsername() {
		return this.tfStreamingUsername.getText();
	}

	/**
	 * @return Streaming Password
	 */
	private String getStreamingPassword() {
		return this.tfStreamingPassword.getText();
	}

	/**
	 * @return Password
	 */
	private String getControlUsername() {
		return this.tfControlUsername.getText();
	}

	/**
	 * @return Control Password
	 */
	private String getControlPassword() {
		return this.tfControlPassword.getText();
	}

	/**
	 * @return the tfName
	 */
	private String getCameraName() {
		return tfName.getText();
	}

}
