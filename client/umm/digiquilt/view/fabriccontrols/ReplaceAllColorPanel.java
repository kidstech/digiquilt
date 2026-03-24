package umm.digiquilt.view.fabriccontrols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.TwoColorArrow;

/**
 * The panel that goes in the bottom left, which has the two FabricWells
 * and buttons to swap colors in the Block.
 *
 */
@SuppressWarnings("serial") //this panel will not be serialized
public class ReplaceAllColorPanel extends JPanel 
                implements FabricListener, PropertyChangeListener{
    /**
     * Default size for the buttons
     */
    final Dimension buttonSize = new Dimension(60,24);

    /**
     * The FabricWell on the left
     */
    private FabricWell wellOne;

    /**
     * The FabricWell on the right
     */
    private FabricWell wellTwo;

    /**
     * Access to the current Block.
     */
    private BlockWorks blockWorks;
    
    /**
     * The button to replace the left color with the right
     */
    private JButton leftRightButton;
    /**
     * The button to swap the two colors.
     */
    private JButton swapButton;
    /**
     * The button to replace the right color with the left
     */
    private JButton rightLeftButton;
    
    /**
     * The arrow pictured on the left to right button.
     */
    private TwoColorArrow leftRightArrowIcon;
    /**
     * The arrow pictured on the swap button.
     */
    private TwoColorArrow swapArrowIcon;
    /**
     * The arrow pictured on the right to left button.
     */
    private TwoColorArrow rightLeftArrowIcon;
    /**
     * This is the panel that will store the components that will make up the
     * color switching area
     * @param patchWorks Access to the current patch.
     * @param blockWorks Access to the current block.
     */
    public ReplaceAllColorPanel(PatchWorks patchWorks, BlockWorks blockWorks) {
        this.blockWorks = blockWorks;
        
        GridBagConstraints constraints = new GridBagConstraints();
        this.setOpaque(false);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        
        JLabel label = new JLabel("Replace Colors");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(1, 1, 5, 5);
        constraints.weighty = 100;
        this.add(label, constraints);

        wellOne = new FabricWell(patchWorks); // add the first fabricWell to
        // the panel
        wellOne.addPropertyChangeListener("fabric", this);
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.insets = new Insets(1, 1, 5, 5);
        add(wellOne, constraints);

        leftRightArrowIcon = new TwoColorArrow(buttonSize.width, buttonSize.height, false, true);
        leftRightArrowIcon.setColors(Fabric.getShadowColor(), Fabric.getShadowColor());
        
        swapArrowIcon = new TwoColorArrow(buttonSize.width, buttonSize.height, true, true);
        swapArrowIcon.setColors(Fabric.getShadowColor(), Fabric.getShadowColor());
        
        rightLeftArrowIcon = new TwoColorArrow(buttonSize.width, buttonSize.height, true, false);
        rightLeftArrowIcon.setColors(Fabric.getShadowColor(), Fabric.getShadowColor());
        
        leftRightButton = new JButton(leftRightArrowIcon);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(1, 1, 5, 5);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 1;
        leftRightButton.setPreferredSize(buttonSize);
        leftRightButton.setMinimumSize(buttonSize);
        leftRightButton.setToolTipText("Click to replace fabrics from left to right");
        add(leftRightButton, constraints);

        swapButton = new JButton(swapArrowIcon);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = new Insets(1, 1, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = 1;
        swapButton.setPreferredSize(buttonSize);
        swapButton.setMinimumSize(buttonSize);
        swapButton.setToolTipText("Click to swap left and right fabrics");
        add(swapButton, constraints);

        rightLeftButton = new JButton(rightLeftArrowIcon);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.insets = new Insets(1, 1, 5, 5);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.weighty = 1;
        rightLeftButton.setPreferredSize(buttonSize);
        rightLeftButton.setMinimumSize(buttonSize);
        rightLeftButton.setToolTipText("click to replace fabrics from right to left");
        add(rightLeftButton, constraints);

        wellTwo = new FabricWell(patchWorks);
        wellTwo.addPropertyChangeListener("fabric", this);
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.insets = new Insets(1, 1, 5, 5);
        add(wellTwo, constraints);

        ReplaceAction leftToRight = new ReplaceAction(wellOne, wellTwo);
        ReplaceAction rightToLeft = new ReplaceAction(wellTwo, wellOne);
        SwapAction swapBoth = new SwapAction(wellOne, wellTwo);

        leftRightButton.addActionListener(leftToRight);
        rightLeftButton.addActionListener(rightToLeft);
        swapButton.addActionListener(swapBoth);

    }

    /**
     * This is an ActionListener which performs ReplaceColor
     * @author BrianOhs, last changed by $Author: biatekjt $
     * on $Date: 2009-05-28 19:19:52 $
     * @version $Revision: 1.1 $
     */
    private class ReplaceAction implements ActionListener {

        /**
         * Pointer to source well
         */
        final private FabricWell replaceThis;

        /**
         * Pointer to target well
         */
        final private FabricWell withThis;

        /**
         * Create ReplaceAction.
         * @param fromWell the well that will replace the second well
         * @param toWell the well that will be replaced
         */
        public ReplaceAction(FabricWell fromWell, FabricWell toWell) {
            replaceThis = fromWell;
            withThis = toWell;
        }

        public void actionPerformed(final ActionEvent event) {
            blockWorks.replaceFabricInBlock(
                    replaceThis.returnFabric(), withThis.returnFabric());

        }
    }

    /**
     * When fired, swaps the two Fabrics being held in the current Block
     *
     */
    private class SwapAction implements ActionListener {
        /**
         * Pointer to first FabricWell
         */
        final private FabricWell swapOne;

        /**
         * Pointer to second FabricWell
         */
        final private FabricWell swapTwo;

        /**
         * Create a new SwapAction with these two wells.
         * 
         * @param one the first well
         * @param two the second well
         */
        public SwapAction(final FabricWell one, final FabricWell two) {
            swapOne = one;
            swapTwo = two;
        }

        public void actionPerformed(final ActionEvent event) {
            blockWorks.swapFabricInBlock(
                    swapOne.returnFabric(), swapTwo.returnFabric());

        }
    }

    /**
     * Move color from one well to the next, and take a new color. 
     * 
     * @param fabric The fabric to change the first well to.
     */
    private void passColors(Fabric fabric) {
        Fabric firstFabric = wellOne.returnFabric();
        wellOne.setFabric(fabric);
        wellTwo.setFabric(firstFabric);
    }

    public void newFabricSelected(Fabric newFabric) {
        passColors(newFabric);
        updateArrows();
    }
    
    /**
     * Update the colors on the TwoColorArrows.
     */
    private void updateArrows(){
        Fabric fabric1 = wellOne.returnFabric();
        Fabric fabric2 = wellTwo.returnFabric();
        Color color1;
        Color color2;
        if (!fabric1.equals(Fabric.TRANSPARENT)){
            color1 = fabric1.getColor();
        } else {
            color1 = Fabric.getShadowColor();
        }
        
        if (!fabric2.equals(Fabric.TRANSPARENT)){
            color2 = fabric2.getColor();
        } else {
            color2 = Fabric.getShadowColor();
        }
        
        leftRightArrowIcon.setColors(color1, color2);
        swapArrowIcon.setColors(color1, color2);
        rightLeftArrowIcon.setColors(color1, color2);
        leftRightButton.repaint();
        swapButton.repaint();
        rightLeftButton.repaint();
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener
     *      #propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // This should get fired whenever a FabricWell reports a change. We
        // need to update the arrows.
        updateArrows();
        
    }
}
