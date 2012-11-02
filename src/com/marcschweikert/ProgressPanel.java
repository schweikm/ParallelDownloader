package com.marcschweikert;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.lang.Math;

import java.util.ArrayList;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class ProgressPanel extends JPanel {


    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////


    public static ProgressPanel getInstance() {
        return instance;
    }

    //:MAINTENANCE
    // I realize that the ConfigurationPanel and the ProgressPanel
    // are too tightly coupled but it can be fixed later
    public void setSelectedCard(final int selectedIndex) {
        final CardLayout cl = (CardLayout)(myCardPanel.getLayout());
        myCardIndex = selectedIndex;

        if(0 == selectedIndex) {
            cl.first(myCardPanel);
        }

        if(1 == selectedIndex) {
            cl.first(myCardPanel);
            cl.next(myCardPanel);
        }

        if(2 == selectedIndex) {
            cl.last(myCardPanel);
            cl.previous(myCardPanel);
        }

        if(3 == selectedIndex) {
            cl.last(myCardPanel);
        }
    }

    public void updateProgress(final int index, final int amount) {
        final int lowerBound = 0;
        final int upperBound = (int)(Math.pow(2, myCardIndex));

        if((index < lowerBound) || (index >= upperBound)) {
            System.err.println("Invalid index specified!" +
                               "  index:  " + index +
                               "  lower bound:  " + lowerBound +
                               "  upper bound:  " + upperBound);
            return;
        }

        // get the progress bar to operate on
        rwLock.readLock().lock();
        final JProgressBar bar = myBarList.get(myCardIndex).get(index);
        rwLock.readLock().unlock();

        // update the progress
        rwLock.writeLock().lock();
        bar.setValue(amount);
        rwLock.writeLock().unlock();
    }


    /////////////////////////
    // PROTECTED INTERFACE //
    /////////////////////////


    ///////////////////////
    // PRIVATE INTERFACE //
    ///////////////////////


    private ProgressPanel() {
        myCardIndex = 0;
        addComponentToPanel();
    }

    private void addComponentToPanel() {
        createProgressBar(myCard1Bars, 1, CARD_CHUNK1);
        createProgressBar(myCard2Bars, 2, CARD_CHUNK2);
        createProgressBar(myCard3Bars, 4, CARD_CHUNK4);
        createProgressBar(myCard4Bars, 8, CARD_CHUNK8);

        // and add the container to the panel
        this.add(myCardPanel, BorderLayout.NORTH);
    }

    private void createProgressBar(final ArrayList<JProgressBar> barList,
                                   final int numBars,
                                   final String description) {

        // create the container layout
        final JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(4, 4));

        // add the number of progress bars
        for(int i = 0; i < numBars; i++) {
            final JProgressBar bar = new JProgressBar(0, 100);
            bar.setPreferredSize(new Dimension(600, 40));
            bar.setForeground(Color.GREEN);
            barList.add(bar);
            cardPanel.add(bar);
        }

        myCardPanel.add(cardPanel, description);
        myBarList.add(barList);
    }


    /////////////////////
    // PRIVATE MEMBERS //
    /////////////////////


    // Singleton pattern
    private static final ProgressPanel instance = new ProgressPanel();

    // turn off Java warning
    private static final long serialVersionUID = 0L;

    // card container
    private final JPanel myCardPanel = new JPanel(new CardLayout());

    // card descriptions
    private static final String CARD_CHUNK1 = "Download with 1 chunk";
    private static final String CARD_CHUNK2 = "Download with 2 chunks";
    private static final String CARD_CHUNK4 = "Download with 4 chunks";
    private static final String CARD_CHUNK8 = "Download with 8 chunks";

    // card components
    private final ArrayList<JProgressBar> myCard1Bars = new ArrayList<JProgressBar>();
    private final ArrayList<JProgressBar> myCard2Bars = new ArrayList<JProgressBar>();
    private final ArrayList<JProgressBar> myCard3Bars = new ArrayList<JProgressBar>();
    private final ArrayList<JProgressBar> myCard4Bars = new ArrayList<JProgressBar>();

    private final ArrayList<ArrayList<JProgressBar>> myBarList = 
      new ArrayList<ArrayList<JProgressBar>>();

    private int myCardIndex;
    
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
}
