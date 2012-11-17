package com.marcschweikert;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.ArrayList;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JPanel;
import javax.swing.JProgressBar;


/**
 * JPanel that shows the individual progress bars.
 *
 * @author Chris Bubernak, Marc Schweikert
 * @version 1.0
 *
 */
public final class ProgressPanel extends JPanel {


    /////////////////////
    // PRIVATE MEMBERS //
    /////////////////////


    /**
     * Singleton instance.
     */
    private static final ProgressPanel instance = new ProgressPanel();

    /**
     * Unique serialization ID.
     */
    private static final long serialVersionUID = 6666666666666666L;

    /**
     * JPanel for the CardLayout.
     */
    private final JPanel myCardPanel = new JPanel(new CardLayout());

    /**
     * Description for 1 download chunk.
     */
    private static final String CARD_CHUNK1 = "Download with 1 chunk";

    /**
     * Description for 2 download chunks.
     */
    private static final String CARD_CHUNK2 = "Download with 2 chunks";

    /**
     * Description for 4 download chunks.
     */
    private static final String CARD_CHUNK4 = "Download with 4 chunks";

    /**
     * Description for 8 download chunks.
     */
    private static final String CARD_CHUNK8 = "Download with 8 chunks";

    /**
     * List for 1 download chunk.
     */
    private final ArrayList<JProgressBar> myCard1Bars = new ArrayList<JProgressBar>();

    /**
     * List for 2 download chunks.
     */
    private final ArrayList<JProgressBar> myCard2Bars = new ArrayList<JProgressBar>();

    /**
     * List for 4 download chunks.
     */
    private final ArrayList<JProgressBar> myCard3Bars = new ArrayList<JProgressBar>();

    /**
     * List for 8 download chunks.
     */
    private final ArrayList<JProgressBar> myCard4Bars = new ArrayList<JProgressBar>();

    /**
     * List of all JProgressBar Lists.
     */
    private final ArrayList<ArrayList<JProgressBar>> myBarList = 
      new ArrayList<ArrayList<JProgressBar>>();

    /**
     * Index of currently selected card.
     */
    private int myCardIndex;

    /**
     * Lock for thread-safe access.
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();


    //////////////////////
    // PUBLIC INTERFACE //
    //////////////////////


    /**
     * @return Singleton instance.
     */
    public static ProgressPanel getInstance() {
        return instance;
    }

    //:MAINTENANCE
    // I realize that the ConfigurationPanel and the ProgressPanel
    // are too tightly coupled but it can be fixed later

    /**
     * Shows the card with the given index.
     *
     * @param selectedIndex index of CardLayout to display
     */
    public void setSelectedCard(final int selectedIndex) {
        final CardLayout layout = (CardLayout) (myCardPanel.getLayout());
        myCardIndex = selectedIndex;

        if (0 == selectedIndex) {
            layout.first(myCardPanel);
        }

        if (1 == selectedIndex) {
            layout.first(myCardPanel);
            layout.next(myCardPanel);
        }

        if (2 == selectedIndex) {
            layout.last(myCardPanel);
            layout.previous(myCardPanel);
        }

        if (3 == selectedIndex) {
            layout.last(myCardPanel);
        }
    }

    /**
     * Update the progress bars on the ProgressPanel.
     *
     * @param index  chunk index
     * @param amount  amount between 0 and 100 (percent)
     */
    public void updateProgress(final int index, final int amount) {
        final int lowerBound = 0;
        final int upperBound = (int) (Math.pow(2, myCardIndex));

        if (index < lowerBound || index >= upperBound) {
            System.err.println("Invalid index specified!"
                             + "  index:  " + index
                             + "  lower bound:  " + lowerBound
                             + "  upper bound:  " + upperBound);
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


    /**
     * Constructor.
     */
    private ProgressPanel() {
        myCardIndex = 0;
        addComponentToPanel();
    }

    /**
     * Add the widgets to the JPanel.
     */
    private void addComponentToPanel() {
        createProgressBar(myCard1Bars, 1, CARD_CHUNK1);
        createProgressBar(myCard2Bars, 2, CARD_CHUNK2);
        createProgressBar(myCard3Bars, 4, CARD_CHUNK4);
        createProgressBar(myCard4Bars, 8, CARD_CHUNK8);

        // and add the container to the panel
        this.add(myCardPanel, BorderLayout.NORTH);
    }

    /**
     * Create a JProgressBar instance.
     *
     * @param barList  List to store the JProgressBars in
     * @param numBars  Number of progress bars to create
     * @param description  String to display
     */
    private void createProgressBar(final ArrayList<JProgressBar> barList,
                                   final int numBars,
                                   final String description) {

        // create the container layout
        final JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(4, 4));

        // add the number of progress bars
        for (int i = 0; i < numBars; i++) {
            final JProgressBar bar = new JProgressBar(0, 100);
            bar.setPreferredSize(new Dimension(600, 40));
            bar.setForeground(Color.GREEN);
            barList.add(bar);
            cardPanel.add(bar);
        }

        myCardPanel.add(cardPanel, description);
        myBarList.add(barList);
    }
}
