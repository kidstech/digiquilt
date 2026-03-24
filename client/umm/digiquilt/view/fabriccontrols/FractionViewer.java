package umm.digiquilt.view.fabriccontrols;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;

/**
 * The button for viewing how much of the current block is covered by
 * this button's color.
 * 
 * @author deragonmr
 *
 */
@SuppressWarnings({"serial"})
public class FractionViewer extends JButton implements PropertyChangeListener {

    /**
     * Default size of a FractionViewer.
     */
    private static final Dimension BUTTONSIZE = new Dimension(37, 32);

    /**
     * The fabric being watched by this FractionViewer 
     */
    private final Fabric fabricCovered;

    /**
     * The Fraction that this FractionViewer is showing.
     */
    private Fraction fraction = new Fraction(0,1);
    

    /**
     * Create a FractionViewer for the given Fabric.
     * 
     * @param label Name of the Fabric to be displayed in the tooltip
     * @param fabric The fabric to be covered
     */
    public FractionViewer(String label, Fabric fabric) {

        fabricCovered = fabric;
        
        setPreferredSize(BUTTONSIZE);
        setMinimumSize(BUTTONSIZE);
        setMaximumSize(BUTTONSIZE);
        setMargin(new Insets(0,0,0,0));
        setFocusPainted(false);
        
        setBackground(fabric.getColor());
        setForeground(fabric.getGoodTextColor());
        setName(label + " fraction");
        
        setToolTipText("Fraction of the block that is currently " + label
                + ". \n" + " Click to increase the denomonator.");
        
        // When clicked, this viewer should increment
        addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                increment();
            }
        });
    }



    
    /**
     * Analyze the current fraction and set the button text accordingly
     */
    private void refreshText(){
        if (fraction.getNumerator() == 0){
            // Display nothing
            this.setText("");
        } else if (fraction.getDenominator() == 1
                && fraction.getNumerator() == 1){
            // Special case, we'll just display "1" to be clearer
            this.setText("1");
        } else {
            // Use HTML to show the fraction
            this.setText("<html><center><u>&nbsp;"+fraction.getNumerator()
                    + "&nbsp;</u><br>"
                    + fraction.getDenominator() + "</center></html>");
        }
        
    }

    /**
     * Reduce this FractionViewer to its simplest form.
     */
    public void reduce() {
        fraction = fraction.reduced();
        refreshText();
    }
    
    /**
     * Increment the denominator to show a different equivalent fraction.
     */
    public void increment(){
        fraction = fraction.inflated();
        refreshText();
    }

    /**
     * @return the fraction being displayed by this viewer.
     */
    public Fraction getFraction() {
        return fraction;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Block newBlock = (Block) evt.getNewValue();
        fraction = newBlock.getBlockCoverage(fabricCovered);
        reduce();
    }

}
