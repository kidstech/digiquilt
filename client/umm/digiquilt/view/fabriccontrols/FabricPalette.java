/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.fabriccontrols;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.works.BlockWorks;

/**
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         deragonmr $ on $Date: 2009-07-04 01:49:19 $
 * @version $Revision: 1.3 $
 */
@SuppressWarnings("serial")
//supressing the Serialized warning from JPanel
public final class FabricPalette extends JPanel {

    /**
     * Default size for Fabric Buttons
     */
    private static final Dimension BUTTONSIZE = new Dimension(112, 32);

    /**
     * Constraints for components laid out in the palette
     */
    final private GridBagConstraints constraints;

    /**
     * Pointer to the "Reduce" button, so the button creator can add listeners
     * to it.
     */
    private JButton reduceButton;

    /**
     * List of all fabric change subscribers.
     */
    private List<FabricListener> listeners = new ArrayList<FabricListener>();
    
    /**
     * Access to the block, for the FractionViewers
     */
    private BlockWorks blockWorks;
    
    /**
     * Map of all the fraction viewers
     */
    private Map<Fabric, FractionViewer> fractionButtons = 
        new HashMap<Fabric, FractionViewer>();

    /**
     * Create a new FabricPalette
     * @param blockWorks The block to get fraction information from.
     */
    public FabricPalette(BlockWorks blockWorks) {
        
        this.blockWorks = blockWorks;
        
        constraints = new GridBagConstraints();

        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        //this.setBackground(new Color(203, 204, 102));

        this.setVisible(true);

        reduceButton = new JButton();
        reduceButton.setSize(new Dimension(75,20));
        reduceButton.setText("Reduce");
        reduceButton.setName("Reduce");
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.add(reduceButton, constraints);

        JLabel paletteLabel = new JLabel("Fabrics");
        //paletteLabel.setSize(new Dimension(75, 32));
        constraints.gridwidth = 1;// GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.gridx = 2;
        constraints.gridy = 0;
        this.add(paletteLabel, constraints);

        buttonCreator("Red Violet", Fabric.REDVIOLET, 1);
        buttonCreator("Red", Fabric.RED, 2);
        buttonCreator("Orange", Fabric.ORANGE, 3);
        buttonCreator("Yellow", Fabric.YELLOW, 4);
        buttonCreator("Green", Fabric.GREEN, 5);
        buttonCreator("Dark Green", Fabric.DARKGREEN, 6);
        buttonCreator("Blue", Fabric.BLUE, 7);
        buttonCreator("Indigo", Fabric.INDIGO, 8);
        buttonCreator("Violet", Fabric.VIOLET, 9);
        buttonCreator("Pink", Fabric.PINK, 10);
        buttonCreator("White", Fabric.WHITE, 11);
        buttonCreator("Black", Fabric.BLACK, 12);
        buttonCreator("Brown", Fabric.BROWN, 13);
    }

    /**Add a FabricListener to be notified when a new fabric is selected.
     * @param listener
     */
    public void addFabricListener(FabricListener listener) {
        listeners.add(listener);
    }

    /**Unsubscribe a FabricListener from fabric changes.
     * @param listener
     */
    public void removeFabricListener(FabricListener listener) {
        listeners.remove(listener);
    }

    /**Notify all listeners of a fabric change.
     * @param fabric
     */
    protected void fireFabricListeners(Fabric fabric){
        for (FabricListener listener : listeners){
            listener.newFabricSelected(fabric);
        }
    }

    /**
     * method to create FabricButtons, add them to the panel and attach the
     * appropriate action listioner.
     * 
     * @param buttonName
     *            the name of the button.
     * @param fabric
     *            the color of the button.
     * @param buttonPlacement The row to place the two new buttons.
     */
    private void buttonCreator(final String buttonName,
            final Fabric fabric, int buttonPlacement) {
        final JButton fabricButton = new JButton(buttonName);
        final FractionViewer fractionViewer = 
            new FractionViewer(buttonName,fabric);
        blockWorks.addPropertyChangeListener("currentBlock", fractionViewer);

        // Create FractionViewer
        constraints.gridwidth =  1; // GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridheight = 1;
        constraints.gridy = buttonPlacement;

        this.add(fractionViewer, constraints);
        reduceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fractionViewer.reduce();
            }
        });
        // Add the viewer to our own records as well
        fractionButtons.put(fabric, fractionViewer);
        

        // Create fabric button
        constraints.gridwidth = 2; //GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = buttonPlacement;

        fabricButton.setToolTipText("Change shape color to " + buttonName);
        fabricButton.setName(buttonName);
        fabricButton.setBackground(fabric.getColor());
        fabricButton.setForeground(fabric.getGoodTextColor());
        fabricButton.setPreferredSize(BUTTONSIZE);

        this.add(fabricButton, constraints);

        fabricButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                fireFabricListeners(fabric);
            }
        });
    }


    /**
     * @return a mapping of the currently set fractions as they appear
     * to the user. Fabrics which have no coverage aren't included. If
     * a fabric has 100% coverage, the fraction should be 1/1
     */
    public Map<Fabric, Fraction> getFractions() {
        Map<Fabric, Fraction> ret = new HashMap<Fabric, Fraction>();
        // Go through the fraction viewers and put their values
        // into the map
        for (Fabric fabric : fractionButtons.keySet()){
            Fraction fraction = fractionButtons.get(fabric).getFraction();
            if (fraction.getNumerator() != 0){
                ret.put(fabric, fraction);
            }
            
        }
        
        
        return ret;
    }
}
