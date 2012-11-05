package com.marcschweikert;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * JPanel to choose a file destination
 * 
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 *
 */
public class FileChooserField extends JPanel {

    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////

    /**
     * Constructor
     */
    public FileChooserField() {
        super(new BorderLayout());

        destinationTextField = new JTextField();
        destinationTextField.addMouseListener(new MouseAdapter() {
            // on mouse click pull up the file chooser
            public void mouseClicked(MouseEvent e) {
                int returnVal = fileChooser.showOpenDialog(FileChooserField.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    destinationTextField.setText(file.getPath());
                }

                destinationTextField.setCaretPosition(destinationTextField.getDocument().getLength());
            }
        });

        //add the text field to the pane
        add(destinationTextField);

        //create the file chooser
        fileChooser = new JFileChooser();
 
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); 
    }

    /**
     * @return File destination
     */
    public String getText () {
    	return destinationTextField.getText();
    }

    /////////////////////////
    // PROTECTED INTERFACE //
    /////////////////////////

    ///////////////////////
    // PRIVATE INTERFACE //
    ///////////////////////

    /////////////////////
    // PRIVATE MEMBERS //
    /////////////////////

    /**
     * Unique serialization ID
     */
    private static final long serialVersionUID = 3333333333333333L;

    /**
     * Text field for file destination
     */
    private JTextField destinationTextField;

    /**
     * JFileChooser instance
     */
    private JFileChooser fileChooser;
}
