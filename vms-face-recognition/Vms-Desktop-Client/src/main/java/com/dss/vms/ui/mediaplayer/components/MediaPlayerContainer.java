package com.dss.vms.ui.mediaplayer.components;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dss.vms.common.data.RecordSession;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;
import com.dss.vms.ui.mediaplayer.actions.JListRecordRenderer;
import com.dss.vms.ui.mediaplayer.actions.MediaTimerAction;
import com.dss.vms.ui.mediaplayer.actions.MediaTimerCallback;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
/**
 * Media Player container includes the canvas, 
 * the media player library {@link https://github.com/caprica/vlcj}
 * along with customized controls {@link ControlPanel}
 * 
 * @author dss-02
 *
 */
public class MediaPlayerContainer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MediaPlayerContainer.class);
	private static final int INVALID_MEDIA = -1;
	private static final String[] PLAYBACK_SPEEDS = new String[] {"1x" , "2x", "4x", "8x"};
	
	private EmbeddedMediaPlayer mediaPlayer;
	private int currentMediaIndex = INVALID_MEDIA;
	private Canvas canvas;
	private DefaultListModel<RecordSession> model = new DefaultListModel<RecordSession>();
	private JList<RecordSession> recordList;
	private ControlPanel controlPanel;
	
	/**
	 * Media PLayer container
	 */
	public MediaPlayerContainer() {
		this.setPreferredSize(new Dimension(900, 540));
		setLayout(new BorderLayout(10, 10));

		this.canvas = new Canvas();
		canvas.setMinimumSize(new Dimension(750, 560));
		createMediaPlayer();

		JPanel playerWrapper = new JPanel(new BorderLayout(10, 10));
		playerWrapper.add(canvas, BorderLayout.CENTER);
		controlPanel = new ControlPanel();
		playerWrapper.add(controlPanel, BorderLayout.SOUTH);

		this.recordList = new JList<RecordSession>();
		this.recordList.setModel(model);
		this.recordList.setLayoutOrientation(JList.VERTICAL);
		this.recordList.setCellRenderer(new JListRecordRenderer());
		this.recordList.setBackground(Color.DARK_GRAY);
		this.recordList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.recordList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 2) {
					currentMediaIndex = recordList.getSelectedIndex();
					controlPanel.play.doClick();
				}
			}
		});
		JScrollPane modelScrollPane = new JScrollPane(recordList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		modelScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				modelScrollPane, playerWrapper);
		splitPane.setPreferredSize(new Dimension(900, 540));
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(300);
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * Create the media player with canvas surface 
	 */
	private void createMediaPlayer() {
		MediaPlayerFactory factory = new MediaPlayerFactory();
		this.mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
		this.mediaPlayer.videoSurface().set(factory.videoSurfaces().newVideoSurface(canvas));
		this.mediaPlayer.overlay().enable(true);
	}
	
	/**
	 * play current media
	 * @param mediaUrl
	 * @return
	 */
	private boolean playMedia(String mediaUrl) {
		boolean success = false;
		try {
			if(mediaPlayer.status().state() == State.PAUSED) {
				mediaPlayer.controls().play();
				return true;
			}
			success = mediaPlayer.media().play(mediaUrl);
		} catch (Exception ex) {
			String url = model.get(currentMediaIndex).getMediaUrl();
			LOGGER.error("Failed to play media [" + url + "] ,Error - " + ex);
		}
		return success;
	}
	
	/**
	 * pause Media player
	 */
	private void pauseMedia() {
		try {
			mediaPlayer.controls().pause();
		} catch (Exception ex) {
			LOGGER.error("Error occured while pausing media, Error - " + ex);
		}
	}
	
	/**
	 * stop current media play
	 */
	private void stopMedia() {
		try {
			mediaPlayer.controls().stop();
		} catch (Exception ex) {
			LOGGER.error("Error occured while stopping media, Error - " + ex);
		}
	}
	
	/**
	 * update list with records when records are searched
	 * @param list
	 */
	public void updateList(List<RecordSession> list) {
		//TODO : ScrollPane is overlapping the records 
		/* stopping current media , if any */
		stopMedia();
		if(list.size() == 0) currentMediaIndex = INVALID_MEDIA;
		else currentMediaIndex = 0;
		
		model.clear();
		model.removeAllElements();
		
		for(RecordSession record : list) {
			model.addElement(record);
		}
		recordList.setVisibleRowCount(list.size());
		recordList.revalidate();
		recordList.repaint();
		this.revalidate();
		this.repaint();
		
		/** starting media play automatically after search **/
		controlPanel.play.doClick();
	}
	
	/**
	 * play next media from the list
	 * @return
	 */
	private boolean playNext() {
		if(currentMediaIndex == INVALID_MEDIA && currentMediaIndex >= model.getSize()) return false;
		currentMediaIndex = (currentMediaIndex + 1 >= model.size()) ? model.getSize() - 1 : currentMediaIndex + 1;
		String mediaUrl = model.get(currentMediaIndex).getMediaUrl();
		return playMedia(mediaUrl);
	}
	
	/**
	 * play previous media from the list
	 * @return
	 */
	private boolean playPrevious() {
		if(currentMediaIndex == INVALID_MEDIA) return false;
		currentMediaIndex = (currentMediaIndex - 1 < 0) ? 0 : currentMediaIndex - 1;
		String mediaUrl = model.get(currentMediaIndex).getMediaUrl();
		return playMedia(mediaUrl);
	}
	
	/**
	 * get screenshot of current media play
	 * @return
	 */
	private BufferedImage getScreenshot() {
		if(mediaPlayer.media().info() == null) return null;
		return mediaPlayer.snapshots().get();
	}
	
	/**
	 * set media play speed
	 * @param speed
	 * @return
	 */
	private boolean setPlaybackSpeed(float speed) {
		return mediaPlayer.controls().setRate(speed);
	}
	
	/**
	 * stop media player
	 */
	public void shutdown() {
		try { controlPanel.timerAction.stopAction(); } catch (Exception e) {}
		try { mediaPlayer.controls().stop(); } catch (Exception e) {}
		try { mediaPlayer.release(); } catch (Exception e) {}
	}
	
	/**
	 * 
	 * @param header
	 * @param message
	 */
	private void showErrorDialog(String header, String message) {
		JLabel label = new JLabel(message);
		label.setForeground(ComponentColors.LABEL_COLOR);
		JOptionPane.showMessageDialog(this, new JPanel().add(label), header, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * 
	 * @author SIBENDU
	 *
	 */
	private class ControlPanel extends JPanel {
		private MediaTimerAction timerAction;
		private JButton play;
		
		public ControlPanel() {
			play = new JButton(Icons.media_play_icon);
			play.addActionListener(e -> {
				if (currentMediaIndex != INVALID_MEDIA) {
					String mediaUrl = model.get(currentMediaIndex).getMediaUrl();
					boolean success = playMedia(mediaUrl);

					if (success) timerAction.start();
				}
			});
			
			JButton pause = new JButton(Icons.media_pause_icon);
			pause.addActionListener(e -> { pauseMedia(); });
			
			JButton replay = new JButton(Icons.media_replay_icon);
			replay.addActionListener(e -> {
				try {
					stopMedia();
					mediaPlayer.controls().setTime(00);
					String mediaUrl = model.get(currentMediaIndex).getMediaUrl();
					boolean success = playMedia(mediaUrl);

					if(success) timerAction.start();
				} catch (Exception ex) {
					LOGGER.error("Failed to	replay media, Error - " + ex);
				}
			});
			
//			JButton stop = new JButton(UIIcons.media_stop_icon);
//			stop.addActionListener(e -> {
//				mediaPaused = false;
//				stopMedia();
//			});

			JButton previous = new JButton(Icons.media_prev_icon);
			previous.addActionListener(e -> {
				boolean success = playPrevious();
				if(success) timerAction.start();
			});
			
			JButton next = new JButton(Icons.media_next_icon);
			next.addActionListener(e -> {
				boolean sucess = playNext();
				if(sucess) timerAction.start();
			});
			
			JSlider slider = new JSlider(00, 100);
			slider.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					int slideValue = slider.getValue();
					float position = slideValue / 100f;
					mediaPlayer.controls().setPosition(position);
				}
			});
			
			slider.setValue(00);
			timerAction = new MediaTimerAction(mediaPlayer, new MediaTimerCallback() {
				
				@Override
				public void playStopped() {}
				
				@Override
				public void mediaPlayed(float percentage) {
					slider.setValue((int) percentage);
				}
				
				@Override
				public void mediaPlayStarted() {
					/** initial procedures **/
					recordList.setSelectedIndex(currentMediaIndex);
					slider.setValue(00);
				}
				
				@Override
				public void mediaPlayComplete() {
					if (currentMediaIndex < model.getSize()) {
						LOGGER.info("Media play complete , Playing next item..");
						next.doClick();
					}
				}
			});

			JButton screenshot = new JButton(Icons.media_screenshot_icon);
			screenshot.addActionListener(e->{ saveScreenshot(); });
			
			JButton download = new JButton(Icons.media_download_icon);
			download.addActionListener(e->{ downloadFile(); });
			
			JComboBox<String> playSpeed = new JComboBox<>(PLAYBACK_SPEEDS);
			playSpeed.addActionListener(e->{
				try {
					String speed = (String) playSpeed.getSelectedItem();
					speed = speed.replace("x", "").trim();
					Float value = Float.parseFloat(speed);
					setPlaybackSpeed(value);
				} catch (NumberFormatException ne) {
					LOGGER.error("Failed to parse playback speed, Resetting value..  Error - " + ne);
					setPlaybackSpeed(1.0f);
				}
			});
					
			setBackground(ComponentColors.MEDIA_CONTROLS_BG);
			setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
			add(previous);
			add(play);
			add(pause);
//			add(stop);
			add(replay);
			add(slider);
			add(playSpeed);
			add(screenshot);
			add(download);
			add(next);
		}
		
		private void downloadFile() {
			if(currentMediaIndex == INVALID_MEDIA) showErrorDialog("Error", "No media playing at this moment.");
			else {
				String mrl = model.get(currentMediaIndex).getMediaUrl();
				File mediaFile = new File(mrl);
				if (mediaFile.exists()) {
					String mediaExtension = mrl.substring(mrl.lastIndexOf("."));
					FileNameExtensionFilter videoFilter = new FileNameExtensionFilter("DSS Video Files", mediaExtension);
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Download video");
					fileChooser.setFileFilter(videoFilter);
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setSelectedFile(new File(mrl.substring(0, mrl.indexOf(mediaExtension))));
					fileChooser.setForeground(ComponentColors.LABEL_COLOR);
					int option = fileChooser.showSaveDialog(this);
					if(option == JFileChooser.APPROVE_OPTION) {
						try {
							/** starting download as background thread **/
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									File savedFile = new File(fileChooser.getSelectedFile() + mediaExtension);
									FileOutputStream download = null;
									FileInputStream upload = null;
									try {
										download = new FileOutputStream(savedFile);
										upload = new FileInputStream(mediaFile);
										
										while(upload.available() > 0) {
											int writeBytes = upload.available() >= 65536 ? 65536 : upload.available();
											byte[] data = new byte[writeBytes];
											upload.read(data);
											download.write(data);
										}
									} catch (Exception e) {
										LOGGER.error("Error occured while writing data to file, Error - " + e);
										showErrorDialog("Error", "Error occured while saving file, cancelling operation.");
									} finally {
										try { download.close(); } catch (Exception e) {}
										try { upload.close(); } catch (Exception e) {}
									}
								}
							});
						} catch (Throwable e) {
							showErrorDialog("Error", "Error occured while saving file, cancelling operation.");
						}
					}
				} else { showErrorDialog("Error", "Media doesn't exist."); }
			}
		}

		private void saveScreenshot() {
			BufferedImage snapshot = getScreenshot();
			if (snapshot == null) {
				showErrorDialog("Error", "No media playing at this moment.");
			} else {
				FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
						"Image files", ImageIO.getReaderFileSuffixes());
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(imageFilter);
				fileChooser.setSelectedFile(new File("snapshot-" + new Date()));
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setDialogTitle("Save screenshot");
				fileChooser.setForeground(ComponentColors.LABEL_COLOR);
				int option = fileChooser.showSaveDialog(this);
				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						File file = new File(fileChooser.getSelectedFile() + ".jpg");
						ImageIO.write(snapshot, "jpg", file);
					} catch (Exception e) { showErrorDialog("Error", "Failed to save snapshot."); }
				}
			}
		}
	}
}
