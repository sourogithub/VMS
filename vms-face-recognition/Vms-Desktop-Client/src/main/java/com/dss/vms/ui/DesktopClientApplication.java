package com.dss.vms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.common.constants.StreamType;
import com.dss.vms.common.data.VideoCamera;
import com.dss.vms.common.response.VmsResponse;
import com.dss.vms.master.CodecEngine;
import com.dss.vms.master.SessionManagerImpl;
import com.dss.vms.ui.actions.ImageTileAction;
import com.dss.vms.ui.actions.TileResizeAction;
import com.dss.vms.ui.actions.TreeActions;
import com.dss.vms.ui.components.LiveViewPanel;
import com.dss.vms.ui.components.SidebarPanel;
import com.dss.vms.ui.components.StatusbarPanel;
import com.dss.vms.ui.components.ToolbarPanel;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.data.CameraBucket;
import com.dss.vms.ui.data.CameraLookupTable;
import com.dss.vms.ui.data.ImageTileBucket;
import com.dss.vms.ui.utility.GridPainter;
import com.dss.vms.video.data.MediaFrame;

/**
 * Main Application instance
 * @author Sibendu
 */
public final class DesktopClientApplication extends JFrame implements Icons {
	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopClientApplication.class);
	private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
	public static final Dimension WINDOW_DIMENSION = new Dimension((int) (TOOLKIT.getScreenSize().width * 0.9),
			(int) (TOOLKIT.getScreenSize().height * 0.9));
	public static final String APP_TITLE = "Surveillux";
	
	/** current application instance **/
	private static DesktopClientApplication WINDOW_INSTANCE = null;
	private static CodecEngine codecEngine = CodecEngine.getInstance();
	private static SessionManagerImpl sessionManager = (SessionManagerImpl) SessionManagerImpl.getInstance();
	private static CameraLookupTable channelTable = CameraLookupTable.getInstance();
	private static GridPainter gridPainter = GridPainter.getInstance();
	private static ImageTileBucket tileBucket = ImageTileBucket.getInstance();
	private static CameraBucket cameraBucket = CameraBucket.getInstance();

	private boolean tileZoomMode = false;
//	private boolean isFullscreenMode = false;

	private MediaListener mediaListener = new MediaListener();
	private SidebarPanel sidebar = null;
	private StatusbarPanel statusBar = new StatusbarPanel();
	private ToolbarPanel toolbar = new ToolbarPanel();
	private LiveViewPanel liveView = new LiveViewPanel();

	/**
	 * Get Application Instance
	 * 
	 * @return Application root frame
	 */
	public static DesktopClientApplication getInstance() {
		synchronized (DesktopClientApplication.class) {
			if (WINDOW_INSTANCE == null) {
				WINDOW_INSTANCE = new DesktopClientApplication();
			}
		}
		return WINDOW_INSTANCE;
	}

	/**
	 * Desktop Client Application
	 */
	private DesktopClientApplication() {
		super(APP_TITLE);

		//adding session-manager
		sessionManager.addObserver(mediaListener);
		sessionManager.wakeup();
		
		/** creating UI Components **/
		this.setupComponents();
		this.setupEventHandlers();
		/** Drawing 2 x 2 grid by Default **/
		gridPainter.drawImageTilesOnViewPanel(GridPainter.DEFAULT_GRID_LAYOUT);
	}

	/**
	 * This Method builds the Entire UI with the Components
	 */
	private void setupComponents() {
		this.setMinimumSize(WINDOW_DIMENSION);
		this.setPreferredSize(WINDOW_DIMENSION);
		this.setSize(WINDOW_DIMENSION);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setIconImage(vms_icon.getImage());
		this.setLayout(new BorderLayout());

		sidebar = new SidebarPanel();
		sidebar.setTreeMouseListener(new TreeActions(liveView));
		gridPainter.registerContainerPanel(liveView);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, liveView);
		splitPane.setDividerSize(2);
		splitPane.setDividerLocation(SidebarPanel.DEFAULT_TOOLBAR_WIDTH + 100);

		/** Adding components to Main UI **/
		this.add(splitPane, BorderLayout.CENTER);
		this.add(toolbar, BorderLayout.NORTH);
		this.add(statusBar, BorderLayout.SOUTH);
	}

	private void setupEventHandlers() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent open) {
				super.windowOpened(open);

				/** loading all cameras from Session-Manager **/
				LOGGER.info("Getting all cameras from Session-Manager to Update camera list");
				VmsResponse response = sessionManager.getAllCameras();
				boolean success = response.isSuccess();
				if (success) {
					if (response.getResponse() instanceof ConcurrentHashMap) {
						ConcurrentHashMap<Integer, VideoCamera> cameraMap = 
								(ConcurrentHashMap<Integer, VideoCamera>) response.getResponse();
						List<VideoCamera> cameras = new ArrayList<>(cameraMap.values());
						cameraBucket.putCamera(cameras);
						updateTreeUI();
					}
				}
				LOGGER.info("Response Recieved from Session-Manager : " + response);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				shutdownApp();
			}
		});
		
		/* Attach Resize Event Handlers on Window */
		this.addComponentListener(new TileResizeAction(this));
		/* Attach Resize Event Handlers on ViewPanel */
		liveView.addComponentListener(new TileResizeAction(this));

		/** adding action listeners to all image Tiles **/
		for (int tileIndex = 0; tileIndex < ImageTileBucket.NO_OF_TILES; tileIndex++) {
			final int currentIndex = tileIndex;
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem clearView = new JMenuItem("Clear View");
			clearView.addActionListener(e -> {
				channelTable.setChannel(CameraLookupTable.INVALID_CHANNEL, currentIndex);
				tileBucket.renderTile(currentIndex, ImageTileBucket.getDefaultBackgroundImage());
			});
			popupMenu.add(clearView);

			ImageTileAction mouseDragEvents = new ImageTileAction(this);
			mouseDragEvents.setPopupMenu(popupMenu);
			tileBucket.getTile(tileIndex).addMouseListener(mouseDragEvents);
			tileBucket.getTile(tileIndex).addMouseMotionListener(mouseDragEvents);
		}
	}

	/**
	 * shuts down the application
	 */
	public void shutdownApp() {
		channelTable.closeMappingTable();
		sessionManager.sleep();
		
		setVisible(false);
		dispose();
		//system exit for exiting the entire instance
		System.exit(0);
	}

	public class MediaListener extends Observable implements Observer {
		@Override
		public void update(Observable sender, Object data) {
			if (data instanceof MediaFrame) {
				MediaFrame frame = (MediaFrame) data;
				
				if (frame.getStreamType() != StreamType.MICRO) return;
				
				for (int index = 0; index < gridPainter.getCurrentGridLayout().getNoOfTiles(); index++) {
					if (channelTable.getChannel(index) == frame.getChannelID()) {
						if (frame.getBufferedImage() == null) {
							if (codecEngine.decodeFrame(frame)) {
								BufferedImage outputImage = new BufferedImage(frame.getRgbWidth(), frame.getRgbHeight(),
										BufferedImage.TYPE_INT_ARGB);
								outputImage.setRGB(0, 0, frame.getRgbWidth(), frame.getRgbHeight(), frame.getRgbFrame(),
										0, frame.getRgbWidth());
								/* drawing grabbing timestamp and camera details */
								VideoCamera camera = cameraBucket.getCamera(frame.getChannelID());
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

						if (frame.getBufferedImage() != null) {
							tileBucket.renderTile(index, frame.getBufferedImage());
						} else {
							LOGGER.error("Failed to decode video frame..."); 
							tileBucket.renderTile(index, ImageTileBucket.getNoImage());
						}
					} 
				}
				/** notify the data frames to other observers **/
				notifyData(data);
				
			} else if ( data instanceof GenericEvent) {
				sidebar.eventGenerated((GenericEvent) data);
			}
		}

		private void notifyData(Object data) {
			this.setChanged();
			this.notifyObservers(data);
		}

	}

	public MediaListener getMediaListener() {
		return this.mediaListener;
	}

	/**
	 * Sets the Window Fullscreen mode
	 * 
	 * @param dim
	 */
	public void setWindowFullscreen() {
		this.setLocation(0, 0);
		this.setSize(TOOLKIT.getScreenSize());
		this.repaint();
	}

	/**
	 * Reverts the window fullscreen mode
	 */
	public void revertWindowFullScreen() {
		this.setSize(WINDOW_DIMENSION);
		sidebar.setVisible(true);
		toolbar.setVisible(true);
		statusBar.setVisible(true);
		this.repaint();
	}

	/**
	 * @param camera to Add And Update the Tree
	 */
	public void updateTreeUI() {
		ArrayList<VideoCamera> list = (ArrayList<VideoCamera>) cameraBucket.getAllCameras();
		sidebar.updateCameraTree(list, list);
	}

	/**
	 * @return the tileFullscreenModeOn
	 */
	public boolean isTileZoomMode() { return tileZoomMode;	}

	/**
	 * @param tileZoomMode the tileFullscreenModeOn to set
	 */
	public void setTileZoom(boolean mode) { this.tileZoomMode = mode; }

	/**
	 * @return the currentFrame
	 */
	public static DesktopClientApplication getRootFrame() { return WINDOW_INSTANCE; }
}
