package com.marcschweikert;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main JPanel.
 * 
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 */
public final class MainPanelGUI {

	/** Main JPanel */
	private final JPanel mainPanel = new JPanel();

	/**
	 * Specify the look and feel to use. Valid values: null (use the default), "Metal", "System", "Motif", "GTK+"
	 */
	private static final String LOOKANDFEEL = "Metal";

	/**
	 * Constructor.
	 */
	public MainPanelGUI() {
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setOpaque(true);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(ConfigurationPanel.getInstance());
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(ProgressPanel.getInstance());
		mainPanel.add(Box.createGlue());
	}

	/**
	 * Specify the theme to display.
	 */
	private static final void initLookAndFeel() {
		String lookAndFeel = null;

		if (LOOKANDFEEL != null) {
			if ("Metal".equals(LOOKANDFEEL)) {
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			} else if ("System".equals(LOOKANDFEEL)) {
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			} else if ("Motif".equals(LOOKANDFEEL)) {
				lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			} else if ("GTK+".equals(LOOKANDFEEL)) { // new in 1.4.2
				lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
			} else {
				System.err.println("Unexpected value of LOOKANDFEEL specified: " + LOOKANDFEEL);
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			}

			try {
				UIManager.setLookAndFeel(lookAndFeel);
			} catch (ClassNotFoundException e) {
				System.err.println("Couldn't find class for specified " + "look and feel:" + lookAndFeel);
				System.err.println("Did you include the L&F library " + "in the class path?");
				System.err.println("Using the default look and feel.");
			} catch (UnsupportedLookAndFeelException e) {
				System.err.println("Can't use the specified look and feel (" + lookAndFeel + ") on this platform.");
				System.err.println("Using the default look and feel.");
			} catch (Exception e) {
				System.err.println("Couldn't get specified look and feel (" + lookAndFeel + "), for some reason.");
				System.err.println("Using the default look and feel.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add widgets to the main panel and display to the user.
	 */
	public final void createAndShowGUI() {
		// Set the look and feel.
		initLookAndFeel();

		// Create and set up the window.
		final JFrame frame = new JFrame("ParallelFileDownloader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1250, 500));

		// Create and set up the content pane.
		mainPanel.setOpaque(true); // content panes must be opaque
		frame.setContentPane(mainPanel);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}
