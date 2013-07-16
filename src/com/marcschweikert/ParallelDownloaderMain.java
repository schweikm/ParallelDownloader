package com.marcschweikert;

/**
 * Main class.
 * 
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 */
public final class ParallelDownloaderMain {

	/**
	 * Main.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static final void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final MainPanelGUI gui = new MainPanelGUI();
				gui.createAndShowGUI();
			}
		});
	}
}
