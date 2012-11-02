package com.marcschweikert;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ConfigurationPanel extends JPanel implements ActionListener {


    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////


    public static ConfigurationPanel getInstance() {
        return instance;
    }


    public void actionPerformed(ActionEvent e) {
        // Download button
        if(e.getActionCommand().equals(ACTION_DOWNLOAD)) {
            // let's make sure we have all of the input we need
            if(myURLTextField.getText().equals("")) {
                myStatusTextField.setText("\"Source URL\" is blank!");
            }
            else if(myDestinationFileChooser.getText().equals("")) {
            //else if(myDestinationTextField.getText().equals("")) {
                myStatusTextField.setText("\"Destination\" is blank!");
            }
            else {
                // disallow changes while downloading
                myChunkComboBox.setEnabled(false);
                myDownloadButton.setEnabled(false);
                myStatusTextField.setText("Download in progress ...");

                // start the download in a new thread to free the GUI
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final String sourceURL = myURLTextField.getText();
                            final String destination = myDestinationFileChooser.getText();
                            final int numChunks = (int)myChunkComboBox.getSelectedItem();

                            // download the file and time it
                            final long start = System.nanoTime();

                            final ParallelDownloader p = new ParallelDownloader();
                            p.download(sourceURL, destination, numChunks);

                            final long end = System.nanoTime();
                            final double totalTime = (end - start) / 1.0e9;

                            // update the status field for success
                            updateStatusMessageLater("Download complete!  Time (seconds): " + totalTime);
                        } catch (final Exception ex) {
                            // something went wrong - show we failed
                            updateStatusMessageLater("Download Failed!  " + ex.getMessage());
                        }
                        finally {
                            // we need to re-enable the buttons no matter what
                            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    myChunkComboBox.setEnabled(true);
                                    myDownloadButton.setEnabled(true);
                                }
                            });
                        }
                    }
                }).start();
            }
        }
        // number of chunks combo box
        else if(e.getActionCommand().equals(ACTION_NUMCHUNKS)) {
            ProgressPanel.getInstance().setSelectedCard(myChunkComboBox.getSelectedIndex());
        }
        else {
            System.err.println("ConfigurationPanel:  I don't know what action just happened!");
        }
    }


    /////////////////////////
    // PROTECTED INTERFACE //
    /////////////////////////


    ///////////////////////
    // PRIVATE INTERFACE //
    ///////////////////////


    private void updateStatusMessageLater(final String message) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myStatusTextField.setText(message);
            }
        });
    }

    private ConfigurationPanel() {
        this.setLayout(new GridLayout(10, 2));
        addComponentsToPanel();
    }

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
        final JTextField numCoresTextField = new JTextField(((Integer)numCores).toString());
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


    /////////////////////
    // PRIVATE MEMBERS //
    /////////////////////


    // Singleton instance
    private static final ConfigurationPanel instance = new ConfigurationPanel();

    // need these components for the action listener
    private final JTextField myURLTextField = new JTextField();
   // private final JTextField myDestinationTextField = new JTextField();
    private final FileChooserField myDestinationFileChooser = new FileChooserField();
    private final JComboBox<Integer> myChunkComboBox = new JComboBox<Integer>();
    private final JTextField myStatusTextField = new JTextField();
    private final JButton myDownloadButton = new JButton("Download");

    // action commands
    private static final String ACTION_DOWNLOAD = "action_download";
    private static final String ACTION_NUMCHUNKS = "action_numChunks";

    // not sure what this is, but it causes a warning
    private static final long serialVersionUID = 1L;
}
