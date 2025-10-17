package com.dss.vms.ui.components.analytics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.analytics.data.Employee;
import com.dss.vms.common.constants.AnalyticCommand;
import com.dss.vms.common.constants.FrameType;
import com.dss.vms.common.constants.MediaType;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.exceptation.VmsCommonException;
import com.dss.vms.common.interfaces.ImageProbe;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.CodecEngine;
import com.dss.vms.probe.ImageProbeImpl;
import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.components.analytics.components.CameraAddAction;
import com.dss.vms.ui.components.analytics.components.ExistingCameraDialog;
import com.dss.vms.ui.components.analytics.components.FormInputPanel;
import com.dss.vms.ui.components.analytics.components.MobileCameraDialog;
import com.dss.vms.ui.components.analytics.components.SnapshotListPanel;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.ui.utility.MJPEGDecoder;
import com.dss.vms.video.data.MediaFrame;
import com.dss.vms.video.reorder.media.AbstractStreamGrabber;
import com.dss.vms.video.reorder.media.impls.HttpMjpegGrabber;
import com.dss.vms.view.panel.ImageTile;
/**
 * 
 * @author dss-02
 *
 */
public class AnalyticsTrainingDialog extends JDialog implements Observer {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsTrainingDialog.class);
	private static final int INVALID_CHANNEL = -1;
	private static final int VIEWPANEL_HEIGHT = 480;
	private static final int VIEWPANEL_WIDTH = 640;
	private static final Dimension DIALOG_DIMENSION = new Dimension(900, 675);

	private static CodecEngine codecEngine = CodecEngine.getInstance();
	
	private static BufferedImage defaultImage = ImageTileBucket.getDefaultBackgroundImage();

	private FormInputPanel formPanel;
	private SnapshotListPanel imageListPanel;
	private ToolbarPanel toolbar;
	private ImageTile viewTile;
	private int channelID = INVALID_CHANNEL;
	private boolean mediaPaused = false;
	
	/** current observable and grabber **/
	private Observable currentObservable = null;
	private AbstractStreamGrabber currentGrabber = null;
	
	private static AnalyticsTrainingDialog INSTANCE = null;
	
	public static AnalyticsTrainingDialog getWindowInstance() {
		synchronized (AnalyticsTrainingDialog.class) {
			if(INSTANCE == null) {
				INSTANCE = new AnalyticsTrainingDialog();
			}
		}
		return INSTANCE;
	}
	
	private AnalyticsTrainingDialog() {
		this.setTitle("Data Training");
		setupComponents();
		setupEventHandlers();
	}

	private void setupComponents() {
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setMinimumSize(DIALOG_DIMENSION);
		
		this.viewTile = new ImageTile(defaultImage, new Dimension(VIEWPANEL_WIDTH, VIEWPANEL_HEIGHT));
		this.imageListPanel = new SnapshotListPanel();
		
		JButton existingCamera = new JButton("Existing Camera");
		existingCamera.addActionListener(action -> {
			Point location = existingCamera.getLocationOnScreen();
			ExistingCameraDialog dialog = new ExistingCameraDialog();
			dialog.setCameraSelectionAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						JComboBox<Object> comboBox = (JComboBox<Object>) e.getSource();
						Object item = comboBox.getSelectedItem();
						if (item instanceof VideoCamera) {
							VideoCamera camera = (VideoCamera) item;
							dialog.setVisible(false);
							dialog.dispose();
							/* stop current grabber operation */
							stopCurrentGrabber();
							/* setting current grab operation */
							channelID = camera.getId();
							currentObservable = DesktopClientApplication.getRootFrame().getMediaListener();
							currentObservable.addObserver(AnalyticsTrainingDialog.this);
							currentGrabber = null;
						}
					} catch (Exception ex) {
						showDialog("Error", "Error occured while selecting Camera, Please try again.");
						LOGGER.error("Error occured while selecting camera : Error - " + e);
					}
				}
			});
			dialog.setLocation(location.x, location.y + existingCamera.getHeight());
			dialog.show();
		});

		JButton mobileCamera = new JButton("Mobile Camera");
		mobileCamera.addActionListener(action -> {
			/** stopping previous action **/
			Point location = mobileCamera.getLocationOnScreen();
			MobileCameraDialog dialog = new MobileCameraDialog();
			dialog.setLocation(location.x, location.y + mobileCamera.getHeight());
			dialog.setCameraAddAction(new CameraAddAction() {
				@Override
				public void cameraAddSuccess(VideoCamera camera) {
					try {
						/* stop previous grabbers */
						stopCurrentGrabber();

						currentGrabber = new HttpMjpegGrabber(camera.getModel(), camera.getAnalyticStreamURL(),
								camera.getStreamingUsername(), camera.getStreamingPassword(),
								camera.getRecordingStream(), camera.getId());
						channelID = camera.getId();
						currentObservable = currentGrabber;
						currentObservable.addObserver(AnalyticsTrainingDialog.this);
						currentGrabber.startGrabber();
						dialog.setVisible(false);
						dialog.dispose();
					} catch (VmsCommonException e) {
						showDialog("Error", "Failed to get stream from camera. Please check the connection and try again.");
					}
				}
				
				@Override
				public void cameraAddFailed() {
					showDialog("Error", "Invalid IP Address.");
				}
			});
			dialog.show();
		});
		
		JButton offlineCamera = new JButton("Offline");
		offlineCamera.addActionListener(action -> {
			/** stopping all previous actions. **/
//			stopPreviousGrabber();
			
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
					"Image files", ImageIO.getReaderFileSuffixes());
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(imageFilter);
			fileChooser.setDialogTitle("Browse images");
			fileChooser.setForeground(ComponentColors.LABEL_COLOR);
			fileChooser.setMultiSelectionEnabled(true);
			int option = fileChooser.showOpenDialog(AnalyticsTrainingDialog.this);
			if(option == JFileChooser.APPROVE_OPTION) {
				try {
					File[] files = fileChooser.getSelectedFiles();
					for (File file : files) {
						try {
							BufferedImage image = ImageIO.read(file);
							boolean success = imageListPanel.addImage(image);
							if (!success) {
								showDialog("Error",
										"Please input atleast " + SnapshotListPanel.MIN_IMAGE_COUNT + " images");
							}
						} catch (Exception e) {
							showDialog("Error", "failed to read file : " + file.getName());
						}
					}
				} catch (Throwable e) {
					showDialog("Error", "Error occured while selecting files. Please try again.");
				}
			}
		});
		
		JButton webCamera = new JButton("Web Camera");
		webCamera.addActionListener(action -> {
			showDialog("Webcam Not Supported", "Webcam not supported at this moment. Contact admin");
		});
	
		JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tabs.add(existingCamera);
		tabs.add(mobileCamera);
		tabs.add(offlineCamera);
		tabs.add(webCamera);
		
		this.toolbar = new ToolbarPanel();
		JPanel viewContainer = new JPanel();
		viewContainer.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
		viewContainer.setLayout(new BorderLayout(5, 5));
		viewContainer.add(viewTile, BorderLayout.CENTER);
		viewContainer.add(toolbar, BorderLayout.SOUTH);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewContainer, imageListPanel);
		split.setDividerSize(2);
		split.setDividerLocation(DIALOG_DIMENSION.width - 180);
		
		this.formPanel = new FormInputPanel();

		this.setLayout(new BorderLayout(5, 0));
		this.add(tabs, BorderLayout.NORTH);
		this.add(formPanel, BorderLayout.SOUTH);
		this.add(split, BorderLayout.CENTER);
	}

	private void setupEventHandlers() {
		
		/* reset all fields */
		formPanel.setResetAction(action -> {
			formPanel.resetAllFields();
			imageListPanel.clearAllImages();
		});
		
		/* validate saved images and display result */
		formPanel.setSubmitAction(action -> {
			try {
				/** validate fields and submit records to native library **/
				List<BufferedImage> faces = imageListPanel.getAllImages();
				if (faces.size() < SnapshotListPanel.MIN_IMAGE_COUNT) {
					showDialog("Error", "Please add atleast " + SnapshotListPanel.MIN_IMAGE_COUNT + " images");
				} else {
					// TODO : submit data to native library and check for success return
					/* show notification based on return code */
					try {
						MediaFrame frames[] = new MediaFrame[faces.size()];
						
						for(int index = 0; index < faces.size(); index++) {
							BufferedImage image = faces.get(index);
							byte[] rawFrame = null;
							ByteArrayOutputStream baos = null;
							try {
								baos = new ByteArrayOutputStream();
								ImageIO.write(image, "jpg", baos);
								rawFrame = baos.toByteArray();
							} catch (Exception e) {
								LOGGER.error("Failed to read raw frame to jpeg, Error - " + e);
								//TODO : add default image
								e.printStackTrace();
							} finally {
								if(baos != null) { baos.close(); }
							}
							
							MediaFrame frame = new MediaFrame(FrameType.I_FRAME, -1, MediaType.JPEG, rawFrame,
									System.currentTimeMillis(), StreamType.ANALYTIC, 0, 0);
							frame.setBufferedImage(image);
							frame.setDecoded(true);
							
							frames[index] = frame;
						}
						
						String emplName = formPanel.getEmployeeName();
						Date emplDob = formPanel.getEmployeeDoB();
						String emplgender = formPanel.getEmployeeGender();
						String emplId = formPanel.getEmployeeId();

						Employee employee = new Employee(emplName, emplId, emplgender, emplDob);
						employee.setFaces(frames);
						
						ImageProbe imageProbe = ImageProbeImpl.getInstance();
						VmsResponse response = imageProbe.executeCommand(AnalyticCommand.FR_TRAIN_DATASET, employee);
						if (response.isSuccess()) {
							showDialog("Success", "Data submitted successfully.");
							/* close the window */
							this.setVisible(false);
							this.dispose();
						} else {
							showDialog("Error", "Validation Failed. Please enter valid input.");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.debug("Error Occured while validating User Form , Error - " + e);
						showDialog("Invalid Data", "Invalid Data Entered.");
					}
				}
			} catch (Throwable e) {
				showDialog("Error", "Error occured while submitting data, Please try again.");
			}
		});
		
		/** adding window listener to cleanup resources **/
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				/* requesting focus will close all popup dialogs */
				AnalyticsTrainingDialog.this.requestFocus();
				stopCurrentGrabber();
			}
		});
	}
	
	public void stopCurrentGrabber() {
		if(currentGrabber != null) {
			LOGGER.info("Stopping current Grabber : " + currentGrabber);
			VmsResponse response = currentGrabber.stopGrabber();
			LOGGER.info("Grabber Stop Response " + response);
		}
		
		if(currentObservable != null) {
			LOGGER.info("Closing incomming Media Stream.");
			currentObservable.deleteObserver(this);
		}
		
		LOGGER.info("Resetting Channel ID.");
		channelID = INVALID_CHANNEL;
		viewTile.setBufferedImage(defaultImage);
		viewTile.setDimension(viewTile.getSize());
	}
	
	@Override
	public void update(Observable sender, Object data) {
		if (data instanceof MediaFrame) {
			if (!mediaPaused) {
				MediaFrame frame = (MediaFrame) data;
				if (frame.getChannelID() == channelID) {
					if (frame.isDecoded()) {
						this.viewTile.setBufferedImage(frame.getBufferedImage());
						this.viewTile.setDimension(this.viewTile.getDimension());
						return;
					}
					
					/* frame is not decoded so decode the frame and set image */
					BufferedImage outputImage = null;
					if (frame.getMediaType() == MediaType.MJPEG) {
						outputImage = MJPEGDecoder.decode(frame.getRawFrame());
					} else if (frame.getMediaType() == MediaType.H264) {
						if (codecEngine.decodeFrame(frame)) {
							outputImage = new BufferedImage(frame.getRgbWidth(), frame.getRgbHeight(),
									BufferedImage.TYPE_INT_ARGB);
							outputImage.setRGB(0, 0, frame.getRgbWidth(), frame.getRgbHeight(), frame.getRgbFrame(),
									0, frame.getRgbWidth());
						}
					}

					if (outputImage != null) {
						viewTile.setDimension(viewTile.getSize());
						viewTile.setBufferedImage(outputImage);
					}
				} 
			}
		}
	}
	
	/**
	 * Media Toolbar panel
	 * @author SIBENDU
	 */
	class ToolbarPanel extends JPanel {
		public ToolbarPanel() {
			JButton snapshot = new JButton(Icons.media_screenshot_icon);
			snapshot.setToolTipText("Take snapshot");
			snapshot.setBackground(Color.RED);
			snapshot.addActionListener(action -> {
				BufferedImage image = viewTile.getBufferedImage();
				if (!image.equals(ImageTileBucket.getDefaultBackgroundImage())) {
					boolean success = imageListPanel.addImage(image);
					if (!success) {
						showDialog("Error", "Cannot add more than 16 images");
					}
				} else {
					showDialog("Error", "No video feed running at this moment. Please select a camera");
				}
			});
			JButton pause = new JButton(Icons.media_pause_icon);
			pause.addActionListener(action -> { mediaPaused = true;	});
			JButton play = new JButton(Icons.media_play_icon);
			play.addActionListener(action -> { mediaPaused = false;	});
			
			this.setLayout(new FlowLayout(FlowLayout.CENTER));
			add(play);
			add(pause);
			add(snapshot);
		}
	}
	
	/**
	 * Show Dialog 
	 * @param header
	 * @param message
	 */
	private void showDialog(String header, String message) {
		JLabel label = new JLabel(message);
		label.setForeground(ComponentColors.LABEL_COLOR);
		JOptionPane.showMessageDialog(this, new JPanel().add(label), header, JOptionPane.PLAIN_MESSAGE);
	}
}
