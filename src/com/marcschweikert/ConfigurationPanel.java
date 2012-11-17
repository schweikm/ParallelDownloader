package com.marcschweikert;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * JPanel that contains the user-configurable options.
 * 
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 *
 */
public final class ConfigurationPanel extends JPanel
                                      implements ActionListener {


    /////////////////////
    // PRIVATE MEMBERS //
    /////////////////////


    /**
     * Singleton instance.
     */
    private static final ConfigurationPanel instance =
        new ConfigurationPanel();

    /**
     * URL Text Field.
     */
    private final JTextField myURLTextField = new JTextField();

    /**
     * File chooser
     */
    private final FileChooserField myDestinationFileChooser =
        new FileChooserField();

    /**
     * Number of download chunks drop-down.
     */
    private final JComboBox<Integer> myChunkComboBox =
        new JComboBox<Integer>();

    /**
     * Status message.
     */
    private final JTextField myStatusTextField = new JTextField();

    /**
     * Download button.
     */
    private final JButton myDownloadButton = new JButton("Download");

    /**
     * Action command for Download button.
     */
    private static final String ACTION_DOWNLOAD = "action_download";

    /**
     * Action command for chunk box drop-down.
     */
    private static final String ACTION_NUMCHUNKS = "action_numChunks";

    /**
     * Unique serialization ID.
     */
    private static final long serialVersionUID = 1111111111111111L;


    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////


    /**
     * Returns Singleton instance.
     *
     * @return ConfigurationPanel
     */
    public static ConfigurationPanel getInstance() {
        return instance;
    }


    /**
     * Method called when any action is performed on this JPanel.
     *
     * @param e ActionEvent with event data
     */
    public void actionPerformed(final ActionEvent event) {
        // Download button
        if(event.getActionCommand().equals(ACTION_DOWNLOAD)) {
            // let's make sure we have all of the input we need
            if (myURLTextField.getText().equals("")) {
                myStatusTextField.setText("\"Source URL\" is blank!");
            } else if (myDestinationFileChooser.getText().equals("")) {
                myStatusTextField.setText("\"Destination\" is blank!");
            } else {
                // disallow changes while downloading
                myChunkComboBox.setEnabled(false);
                myDownloadButton.setEnabled(false);
                myStatusTextField.setText("Download in progress ...");

                // start the download in a new thread to free the GUI
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final String sourceURL = myURLTextField.getText();
                            final String destination =
                                myDestinationFileChooser.getText();
                            final int numChunks =
                                (int) myChunkComboBox.getSelectedItem();

                            // download the file and time it
                            final long start = System.nanoTime();

                            final ParallelDownloader downloader =
                                new ParallelDownloader();
                            downloader.download(sourceURL,
                                                destination,
                                                numChunks);

                            final long end = System.nanoTime();
                            final double totalTime = (end - start) / 1.0e9;

                            // update the status field for success
                            updateStatusMessageLater("Download complete!  "
                                + "Time (seconds): " + totalTime);
                        } catch (final Exception ex) {
                            // something went wrong - show we failed
                            updateStatusMessageLater("Download Failed!  "
                                + ex.getMessage());
                        } finally {
                            // we need to re-enable the buttons no matter what
                            javax.swing.SwingUtilities.
                                invokeLater(new Runnable() {
                                public void run() {
                                    myChunkComboBox.setEnabled(true);
                                    myDownloadButton.setEnabled(true);
                                }
                            });
                        }
                    }
                }).start();
            }
        } else if (event.getActionCommand().equals(ACTION_NUMCHUNKS)) {
            ProgressPanel.getInstance().
                setSelectedCard(myChunkComboBox.getSelectedIndex());
        } else {
            System.err.println("ConfigurationPanel:  unknown action!");
        }
    }


    /////////////////////////
    // PROTECTED INTERFACE //
    /////////////////////////


    ///////////////////////
    // PRIVATE INTERFACE //
    ///////////////////////


    /**
     * Safely update the status message on the GUI thread.
     *
     * @param message Message to display on status field
     */
    private void updateStatusMessageLater(final String message) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myStatusTextField.setText(message);
            }
        });
    }

    /**
     * Constructor.
     */
    private ConfigurationPanel() {
        this.setLayout(new GridLayout(10, 2));
        addComponentsToPanel();
    }

    /**
     * Add the components to the JPanel.
     */
    private void addComponentsToPanel() {
        //
        // URL FIELD
        //
        final JLabel urlLabel = new JLabel("Source URL");
        this.add(urlLabel);
        this.add(myURLTextField);

        //
        // FILE CHOOSER
        //
        final JLabel fileChooserLabel = new JLabel("Destination");
        this.add(fileChooserLabel);
        this.add(myDestinationFileChooser);

        //
        // NUMBER OF CORES
        //
        final JLabel numCoresLabel = new JLabel("Number of CPU Cores");
        this.add(numCoresLabel);

        final int numCores = Runtime.getRuntime().availableProcessors();
        final JTextField numCoresTextField =
            new JTextField(((Integer) numCores).toString());
        numCoresTextField.setEditable(false);
        this.add(numCoresTextField);


        //
        // NUMBER OF CHUNKS
        //
        final JLabel numChunksLabel = new JLabel("Number of Chunks");
        this.add(numChunksLabel);

        myChunkComboBox.addItem(1);
        myChunkComboBox.addItem(2);
        myChunkComboBox.addItem(4);
        myChunkComboBox.addItem(8);
        myChunkComboBox.setSelectedIndex(0);
        myChunkComboBox.setActionCommand(ACTION_NUMCHUNKS);
        myChunkComboBox.addActionListener(this);
        this.add(myChunkComboBox);


        //
        // DOWNLOAD BUTTON
        //
        myDownloadButton.setActionCommand(ACTION_DOWNLOAD);
        myDownloadButton.addActionListener(this);
        this.add(myDownloadButton);


        //
        // STATUS FIELD
        //
        myStatusTextField.setText("System Ready");
        myStatusTextField.setEditable(false);
        myStatusTextField.setHorizontalAlignment(JTextField.CENTER);
        this.add(myStatusTextField);


        // this makes the widgets arrange correctly
        final JLabel filler = new JLabel("");
        this.add(filler);
    }
}
