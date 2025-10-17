package com.dss.vms.ui.utility;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.dss.vms.ui.constants.VmsComponentColor;

public class VmsLookAndFeelUtil {
	public static void setupLookAndFeel(String lookAndFeelType) {
		try {
			 /* Default Look and Feel is CrossPlatform*/
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (info.getName().equals(lookAndFeelType)) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Throwable e) {
			System.out.println("Error occured while setting look and feel, Error -" + e);
		} finally {
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			UIManager.put("control", VmsComponentColor.CONTROL); /* adding a few more customizations on L&A */
			UIManager.put("Button.foreground", VmsComponentColor.BUTTON_FG);
			defaults.put("MenuBar.background", VmsComponentColor.MENUBAR_BG);
			defaults.put("Button.background", VmsComponentColor.BUTTON_BG);
			defaults.put("TabbedPane.extendTabsToBase", false);
			defaults.put("nimbusBase", Color.GRAY);
			defaults.put("text", Color.white);
			defaults.put("Table.alternateRowColor", VmsComponentColor.TABLE_ALTERNATE_ROW);
			defaults.put("TextField.inactiveBackground", Color.CYAN);
			defaults.put("Button.textForeground", Color.WHITE);
			/** setting fonts **/
			UIManager.put("Label.font", new Font("Calibri", Font.BOLD, 12));
			UIManager.put("Button.font", new Font("Calibri", Font.BOLD, 12));
		}

	}
}
