import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.dss.vms.ui.DesktopClientApplication;
import com.dss.vms.ui.utility.LookAndFeelUtil;
import com.dss.vms.ui.utility.VmsSystemTrayUtility;

/**
 * Driver Class for Splash-Screen 
 * and booting {@linkplain DesktopClientApplication}
 * @author Sibendu-PC
 */
public class App {
	private static DesktopClientApplication application;
	private static SplashScreen splashWindow;
	private static Double splashTextArea;
	private static Double splashProgressArea;
	private static Graphics2D splashGraphics;
	private static Font font;

	private static void splashInit() {
		splashWindow = SplashScreen.getSplashScreen();
		if (splashWindow != null) {
			Dimension splashDim = splashWindow.getSize();
			int height = splashDim.height;
			int width = splashDim.width;
			/* getting the bounds for text area */
			splashTextArea = new Rectangle2D.Double(15, height * 0.88, width * 0.4, 12); 
			/* getting the bounds for progress area */
			splashProgressArea = new Rectangle2D.Double(20, height * 0.92, width - 40, 12); 

			splashGraphics = splashWindow.createGraphics();
			font = new Font("Dialog", Font.PLAIN, 14);
			splashGraphics.setFont(font);

			splashGraphics.drawString("Video Management System", 20, 40);
			splashWindow.update();
		}
	}

	/**
	 * set splash screen text
	 * 
	 * @param message
	 */
	private static void splashText(String message) {
		if (splashWindow != null && splashWindow.isVisible()) {
			// draw the text
			splashGraphics.setPaint(Color.WHITE);
			splashGraphics.drawString(message, (int) (splashTextArea.getX() + 10), (int) (splashTextArea.getY() + 10));
			// update the splash Screen
			splashWindow.update();
		}
	}

	/**
	 * @param progressTrack
	 */
	private static void splashProgress(int progressTrack) {
		if (splashWindow != null && splashWindow.isVisible()) {
			splashGraphics.setPaint(Color.LIGHT_GRAY);
			splashGraphics.fill(splashProgressArea);

			// drawing an outline
			splashGraphics.setPaint(Color.DARK_GRAY);
			splashGraphics.draw(splashProgressArea);

			// Calculating the width corresponding to the correct percentage
			int x = (int) splashProgressArea.getMinX();
			int y = (int) splashProgressArea.getMinY();
			int width = (int) splashProgressArea.getWidth();
			int height = (int) splashProgressArea.getHeight();

			int doneWidth = Math.round(progressTrack * width / 100);
			doneWidth = Math.max(0, Math.min(doneWidth, width - 1)); // limit 0 to width
			// fill the done part one pixel smaller than the outline
			splashGraphics.setPaint(Color.GREEN);
			splashGraphics.fillRect(x, y + 1, doneWidth, height - 1);
			// make sure it's displayed
			splashWindow.update();
		}
	}

	/**
	 * Close the splash window
	 */
	private static void splashClose() {
		if (splashWindow != null) {
			splashWindow.close();
		}
	}

	/**
	 * init main app
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	private static void appInit() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				LookAndFeelUtil.setupLookAndFeel("Nimbus");
				application = DesktopClientApplication.getInstance();

				// for splash screen progress setting
				if (splashWindow != null) {
					for (int progressCounter = 1; progressCounter <= 4; progressCounter++) {
						int progressComplete = progressCounter * 25;
						splashText("Loading...");
						splashProgress(progressComplete);
						try { Thread.sleep(1000); } 
						catch (InterruptedException ex) { splashClose(); }
					}
				}

				/** App loaded completely now display main App 
				 * and close the splash Screen **/
				application.setVisible(true);
			}
		});
	}

	/**
	 * Entry Point
	 */
	public static void main(String[] args) {
		try {
			splashInit();
			appInit();
			VmsSystemTrayUtility.createSystemTrayIcon();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
}
