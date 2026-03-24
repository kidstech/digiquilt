package umm.digiquilt.view.fabriccontrols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.glasspane.HandPaneHighlighted;

/**
 * One of the fabric wells on the bottom left of the screen. Takes a color
 * from a shape, or clears when clicked.
 *  
 * @author woldta
 */
@SuppressWarnings("serial")
//will not be serialized
public class FabricWell extends JComponent implements HandPaneHighlighted{
    /**
     * The Fabric stored by the color well
     */
    private Fabric stored = Fabric.TRANSPARENT;

    /**
     * The hard-coded size of a FabricWell.
     */
    private static final Dimension SIZE = new Dimension(30, 150);

    /**
     * Access to the current Patch
     */
    private PatchWorks patchWorks;

    /**
     * Creates a new FabricWell component setting the size and adding the mouse listener
     * @param patchWorks Access to the current Patch.
     * @param racp The ReplaceAllColorPanel to interact with.
     */
    public FabricWell(PatchWorks patchWorks) {
        this.patchWorks = patchWorks;
        //the size of the FabricWell component
        setPreferredSize(SIZE);
        setMinimumSize(SIZE);
        //setSize(componentSize);
        this.setToolTipText("Drop a shape here to select its fabric");
        this.addMouseListener(new FabricWellMouseListener());
    }

    /**
     * overrides the default paint component in a way that will draw an empty black
     * circle if fillColor hasn't yet been set and will draw a filled circle of the fillColor
     * if it has been set.
     */
    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponents(graphics);
        if (stored.equals(Fabric.TRANSPARENT)) {
            graphics.setColor(Fabric.getShadowColor());
            graphics.fillRoundRect(1,1,28,148,15,15);

        } else {
            graphics.setColor(stored.getColor());
            graphics.fillRoundRect(1, 1, 28, 148,15,15);
        }
        graphics.setColor(Color.BLACK);
        graphics.drawRoundRect(1, 1, 28, 148,15,15);
    }

    /**
     * Sets the stored fabric for the component and the fillColor based
     * on the fabric stores...then it calls repaint to draw the updated circle
     * @param fabric The fabric to be held and displayed
     */
    public void setFabric(final Fabric fabric) {
        Fabric oldFabric = stored;
        stored = fabric;
        if(stored == Fabric.TRANSPARENT){
            setToolTipText("Drop a shape here to select its fabric");
        } else {
            setToolTipText("Click here to remove this color");
        }
        repaint();
        this.firePropertyChange("fabric", oldFabric, stored);
    }

    /**
     * @return the fabric currently being stored by this well
     */
    public Fabric returnFabric() {
        return stored;
    }

    public boolean isSnappy() {
        return false;
    }

    public boolean isHighlighted() {
        Patch heldPatch = patchWorks.getCurrentPatch();
        return !heldPatch.getSolidColor().equals(Fabric.TRANSPARENT);
    }

    /**
     * The mouse listener for the FabricWell. It checks the currently held 
     * patch, and if it's a solid color the FabricWell will adopt that color.
     * If the FabricWell is just being clicked without a Patch in hand, then
     * the color will just be set to TRANSPARENT.
     */
    private class FabricWellMouseListener extends MouseAdapter{


        @Override
        public void mouseClicked(final MouseEvent event) {
            if (patchWorks.getIsHeld()) {
                Patch heldPatch = patchWorks.getCurrentPatch();
                if (!heldPatch.getSolidColor().equals(Fabric.TRANSPARENT)){
                    setFabric(patchWorks.getCurrentPatch().getSolidColor());
                    patchWorks.releasePatch();
                }
            } else {
                setFabric(Fabric.TRANSPARENT);
            }
            repaint();
        }
    }

}
