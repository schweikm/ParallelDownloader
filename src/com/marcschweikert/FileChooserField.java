package com.marcschweikert;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class FileChooserField extends JPanel {
 
    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////
    
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

	private static final long serialVersionUID = 1L;
	
    private JTextField destinationTextField;
    
    private JFileChooser fileChooser;
}
