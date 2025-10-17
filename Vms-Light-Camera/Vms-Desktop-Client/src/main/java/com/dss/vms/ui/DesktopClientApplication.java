package com.dss.vms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.interfaces.SessionManager;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.CodecEngine;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.actions.TileHandler;
import com.dss.vms.ui.actions.ViewResizeHandler;
import com.dss.vms.ui.components.CameraListPanel;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.components.StatusbarPanel;
import com.dss.vms.ui.components.toolbar.ToolbarPanel;
import com.dss.vms.ui.constants.VmsIcons;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.data.ChannelTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.ui.utility.VmsGridPainter;
import com.dss.vms.video.data.MediaFrame;

/**
 * @author Sibendu
 */
public final class DesktopClientApplication extends JFrame implements Observer, VmsIcons {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopClientApplication.class);
	
	public static final Dimension FULLSCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();

	public static final Dimension WINDOW_DIMENSION = new Dimension((int) (FULLSCREEN_DIMENSION.width),
			(int) (FULLSCREEN_DIMENSION.height - 28));
	
	private static final String APP_TITLE = "Surveillux";
	
	/** current application instance **/
	private static DesktopClientApplication uiInstance;

	/** Data **/
	private static CodecEngine codecEngine = CodecEngine.getInstance();
	private static SessionManager sessionManager = SessionManagerImpl.getInstance();
	private static ChannelTable  channelTable = ChannelTable.getInstance();
	private static VmsGridPainter gridPainter = VmsGridPainter.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	private static CameraBucket cameraBucket = CameraBucket.getInstance();
	
	private boolean tileZoomMode = false;
//	private boolean isFullscreenMode = false;

	private CameraListPanel cameraListPanel = null;
	private StatusbarPanel statusBar = new StatusbarPanel();
	private ToolbarPanel toolbar = new ToolbarPanel();
	private LiveViewPanel cameraViewPanel = new LiveViewPanel();

	/**
	 * Get Application Instance
	 * 
	 * @return Application root frame
	 */
	public static DesktopClientApplication getInstance() {
		synchronized (DesktopClientApplication.class) {
			if (uiInstance == null) {
				uiInstance = new DesktopClientApplication();
			}
		}
		return uiInstance;
	}

	/**
	 * Desktop Client Application
	 */
	private DesktopClientApplication() {
		super(APP_TITLE);
		((SessionManagerImpl) sessionManager).addObserver(this);
		sessionManager.wakeup();
		
		/* creating UI Components */
		this.setupComponents();
		/* Drawing 2 x 2 grid by Default */
		gridPainter.drawTiles(VmsGridPainter.DEFAULT_GRID_LAYOUT);
	}

	/**
	 * This Method builds the Entire UI with the Components
	 */
	private void setupComponents() {
		setMinimumSize(WINDOW_DIMENSION);
		setPreferredSize(WINDOW_DIMENSION);
		setSize(WINDOW_DIMENSION);
		setUndecorated(false);
		setResizable(false);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImage(vms_icon.getImage());
		setLayout(new BorderLayout());

		this.cameraListPanel = new CameraListPanel(cameraViewPanel);
		this.gridPainter.registerParentViewPanel(cameraViewPanel);
		JSplitPane viewSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cameraListPanel, cameraViewPanel);
		viewSplitPane.setDividerSize(2);
		viewSplitPane.setDividerLocation(CameraListPanel.DEFAULT_TOOLBAR_WIDTH);

		// createMenuBar();
		setupEventHandlers();
		/* Adding to Main UI */
		add(viewSplitPane, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
	}

	private void setupEventHandlers() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent open) {
				super.windowOpened(open);
				LOGGER.info("Getting all cameras from Session-Manager to Update camera list");
				VmsResponse response = sessionManager.getAllCameras();
				LOGGER.info("Response Recieved from Session-Manager : " + response);
				boolean success = response.isSuccess();
				if (success) {
					Serializable data = response.getResponse();
					if (data instanceof ConcurrentHashMap) {
						cameraBucket.putCameras(
								((ConcurrentHashMap<Integer, VideoCamera>) data).values()
								.stream()
								.collect(Collectors.toList()));
						updateTreeUI();
					}
				}
			}

			@Override
			public void windowClosing(WindowEvent e) { shutdownApp(); }
		});

		// Attach Resize Event Handlers on Window 
		addComponentListener(new ViewResizeHandler(this));
		// Attach Resize Event Handlers on ViewPanel 
		cameraViewPanel.addComponentListener(new ViewResizeHandler(this));

		/** adding action listeners to all image Tiles **/
		for (int tileIndex = 0; tileIndex < ImageTileBucket.NO_OF_TILES; tileIndex++) {
			final int currTileIndex = tileIndex;
			JPopupMenu popupMenu = new JPopupMenu();
			
			JMenuItem clearView = new JMenuItem("Clear View");
			clearView.addActionListener(action -> {
				channelTable.setChannel(ChannelTable.INVALID_CHANNEL, currTileIndex);
				channelTable.saveChanges();
				//getting current tile index and setting default-image
				int tile = channelTable.getChannel(currTileIndex);
				tileBucket.renderTile(tile, ImageTileBucket.getDefaultImage());
			});
			
			JMenuItem clearAll = new JMenuItem("Clear All Views");
			clearAll.addActionListener(e -> {
				//setting all map values to INVALID_CHANNEL
				for(int channel = 0; channel < ChannelTable.NO_OF_TILES; channel++) {
					channelTable.setChannel(ChannelTable.INVALID_CHANNEL, channel);
					int tile = channelTable.getChannel(channel);
					tileBucket.renderTile(tile, ImageTileBucket.getDefaultImage());
				}
				
				channelTable.saveChanges();
			});
			popupMenu.add(clearView);
			popupMenu.add(clearAll);
			
			TileHandler mouseDragEvents = new TileHandler(this);
			mouseDragEvents.setPopupMenu(popupMenu);
			tileBucket.getTile(tileIndex).addMouseListener(mouseDragEvents);
			tileBucket.getTile(tileIndex).addMouseMotionListener(mouseDragEvents);
		}
	}

	/**
	 * shuts down the application
	 */
	public void shutdownApp() {
		uiInstance.setState(JFrame.ICONIFIED);
	}

	@Override
	public void update(Observable sender, Object data) {
		if (data instanceof MediaFrame) {
			MediaFrame frame = (MediaFrame) data;
			if (frame.getStreamType() != StreamType.MICRO) return;
			
			try {
				for (int index = 0; index < gridPainter.getCurrentGridLayout().getNoOfTiles(); index++) {
					int channel = channelTable.getChannel(index);
					if (channel == frame.getChannelID()) {
						if (frame.getBufferedImage() == null) {
							if (codecEngine.decodeFrame(frame)) {
								BufferedImage outputImage = new BufferedImage(frame.getRgbWidth(), frame.getRgbHeight(),
										BufferedImage.TYPE_INT_ARGB);
								outputImage.setRGB(0, 0, frame.getRgbWidth(), frame.getRgbHeight(), frame.getRgbFrame(),
										0, frame.getRgbWidth());
								/* drawing grabbing timestamp and camera details */
								VideoCamera camera = cameraBucket.getCamera(channel);
								if (camera != null) {
									Date grabTime = new Date(frame.getTimestamp());
									Graphics2D graphics = (Graphics2D) outputImage.getGraphics();
									graphics.setFont(graphics.getFont().deriveFont(18f));
									graphics.drawString(camera.getName(), 30, outputImage.getHeight() - 30);
									graphics.drawString(String.valueOf(grabTime), 30, 30);
									graphics.dispose();
								}
								frame.setBufferedImage(outputImage);
							}
						}
						if (frame.getBufferedImage() != null) tileBucket.renderTile(index, frame.getBufferedImage());
						else {
							LOGGER.error("Failed to decode video frame...");
							tileBucket.renderTile(index, ImageTileBucket.getNoImage());
						}
					} else if (channel == ChannelTable.INVALID_CHANNEL) {
						BufferedImage currentImage = tileBucket.getTile(index).getBufferedImage();
						BufferedImage defaultImage = ImageTileBucket.getDefaultImage();
						// clean up view tile
						if (!currentImage.equals(defaultImage)) tileBucket.renderTile(index, defaultImage);
					}
				}
			} catch (Throwable e) {
			LOGGER.error("Error occured while decoding camera stream , error - " + e);
		}
			
		}
	}

	/**
	 * @param camera to Add And Update the Tree
	 */
	public void updateTreeUI() {
		List<VideoCamera> cameras = cameraBucket.getAllCameras();
		cameraListPanel.updateTree(cameras, cameras);
	}

	/**
	 * @return the tileFullscreenModeOn
	 */
	public boolean isTileZoomMode() { return tileZoomMode; }

	/**
	 * @param tileZoomMode the tileFullscreenModeOn to set
	 */
	public void setTileZoom(boolean mode) { this.tileZoomMode = mode; }

	/**
	 * @return the currentFrame
	 */
	public static DesktopClientApplication getRootFrame() { return uiInstance; }
}
