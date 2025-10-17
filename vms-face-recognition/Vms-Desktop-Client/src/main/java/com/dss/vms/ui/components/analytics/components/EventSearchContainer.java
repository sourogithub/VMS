package com.dss.vms.ui.components.analytics.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dss.vms.analytics.data.GenericEvent;
import com.dss.vms.ui.constants.ComponentColors;
import com.dss.vms.ui.constants.Icons;
/**
 * Includes all the event media related components
 * @see EventSearchPanel for more reference
 * @see ControlPanel
 * @author dss-02
 */
public class EventSearchContainer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int INVALID_MEDIA = -1;
	
	private int currentIndex = INVALID_MEDIA;
	private EventDisplayPanel canvas;
	private DefaultListModel<GenericEvent> model = new DefaultListModel<GenericEvent>();
	private JList<GenericEvent> recordList;
	private ControlPanel controlPanel;
	
	/**
	 * Media PLayer container
	 */
	public EventSearchContainer() {
		this.setPreferredSize(new Dimension(900, 540));
		setLayout(new BorderLayout(10, 10));

		this.canvas = new EventDisplayPanel();
		canvas.setMinimumSize(new Dimension(750, 560));

		JPanel container = new JPanel(new BorderLayout(10, 10));
		container.add(canvas, BorderLayout.CENTER);
		controlPanel = new ControlPanel();
		container.add(controlPanel, BorderLayout.SOUTH);

		this.recordList = new JList<GenericEvent>();
		this.recordList.setCellRenderer(new EventSearchListRenderer());
		this.recordList.setModel(model);
		this.recordList.setLayoutOrientation(JList.VERTICAL);
		this.recordList.setBackground(Color.DARK_GRAY);
		this.recordList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.recordList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 2) {
					currentIndex = recordList.getSelectedIndex();
					GenericEvent eventSession = model.get(currentIndex);
					canvas.updateView(eventSession);
				}
			}
		});
		JScrollPane modelScrollPane = new JScrollPane(recordList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		modelScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				modelScrollPane, container);
		splitPane.setPreferredSize(new Dimension(900, 540));
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(300);
		add(splitPane, BorderLayout.CENTER);
	}

	
	/**
	 * update list with records when records are searched
	 * @param list
	 */
	public void updateList(List<GenericEvent> list) {
		if(list.size() == 0) currentIndex = INVALID_MEDIA;
		else currentIndex = 0;
		
		model.removeAllElements();
		
		for(GenericEvent record : list) {
			model.addElement(record);
		}
		recordList.setVisibleRowCount(list.size());
		recordList.repaint();
		this.repaint();
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
		
		public ControlPanel() {

			JButton previous = new JButton(Icons.media_prev_icon);
			previous.addActionListener(action -> { showPrevious(); });
			
			JButton next = new JButton(Icons.media_next_icon);
			next.addActionListener(action -> { showNext(); });
			
			JButton screenshot = new JButton(Icons.media_screenshot_icon);
			screenshot.addActionListener(e->{ saveScreenshot(); });
			
			setBackground(ComponentColors.MEDIA_CONTROLS_BG);
			setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
			add(previous);
			add(screenshot);
			add(next);
		}

		private void saveScreenshot() {
			BufferedImage snapshot = canvas.getCurrentImage();
			if (snapshot == null) {
				showErrorDialog("Error", "No Event found at this moment.");
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
		
		public void showNext() {
			if(currentIndex == INVALID_MEDIA) {
				showErrorDialog("NO Media", "No Event present on the list.");
			} else {
				currentIndex = (currentIndex < model.getSize() - 1) ? currentIndex + 1 : 0;

				GenericEvent eventSession = model.get(currentIndex);
				canvas.updateView(eventSession);
				recordList.setSelectedIndex(currentIndex);
			}
		}
		
		public void showPrevious() {
			if(currentIndex == INVALID_MEDIA) {
				showErrorDialog("NO Media", "No Event present on the list.");
			}
			else {
				currentIndex = (currentIndex > 0) ? currentIndex - 1 : model.getSize() - 1;

				GenericEvent eventSession = model.get(currentIndex);
				canvas.updateView(eventSession);
				recordList.setSelectedIndex(currentIndex);
			}
		}
	}
	
}
