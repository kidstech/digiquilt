package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.view.glasspane.HandPaneHighlighted;

/**
 * A panel that displays a Patch. It is highlightable by the glasspane, but
 * this can be turned off.
 *  
 * @author biatekjt, last changed by $Author: biatekjt $
 * on $Date: 2009-06-21 23:41:03 $
 * @version $Revision: 1.2 $
 *
 */
@SuppressWarnings("serial")
public class PatchViewer extends JPanel implements HandPaneHighlighted{

    /**
     * Patch backend. 
     */
    private Patch thePatch;
    /**
     * Image of the Patch to be displayed.
     */
    private Image image;

    /**
     * The length of a side of a full sized Patch. In other words, a full
     * square will be fullSize by fullSize, whereas a rectangle could be
     * fullSize/2 by fullSize.
     */
    private int fullSize;

    /**
     * Turn on or off highlighting when hovered over by the GlassPane. Defaults
     * to true.
     */
    private boolean highlighted = true;

    /**
     * Turn on or off shape snapping when hovered over by the GlassPane. 
     * Defaults to true.
     */
    private boolean snappy = true;

    /**
     * Boolean to decide whether transparency is completely invisible
     * or shown with a semi-transparent shadow.
     */
    private boolean isShadowed = false;


    /**
     * Create a viewer with the given patch.
     * 
     * @param patch
     * @param size The size of a full sized patch. A full sized square will
     * be size by size.
     */
    public PatchViewer(Patch patch, int size){
        thePatch = patch;
        fullSize = size;
        setVisible(true);
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        refreshImage();
    }

    /**
     * @return the patch held by this Viewer
     */
    public Patch getPatch() {
        return thePatch;
    }

    /**
     * Set a new patch for this Viewer
     * 
     * @param newPatch
     */
    public void setPatch(Patch newPatch) {
        thePatch = newPatch;
        this.refreshImage();
    }

    /**
     * Regenerate the image of the patch being held, and repaint.
     */
    public final void refreshImage() {
        image = generateImage(thePatch, fullSize, false);
        this.repaint();
    }

    @Override
    public void paintComponent(final Graphics graphic) {
        super.paintComponent(graphic);
        final Graphics2D drawSpace = (Graphics2D) graphic;
        if (isShadowed){
            drawSpace.setColor(Fabric.getShadowColor());
            drawSpace.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        drawSpace.drawImage((BufferedImage) image, null, 0, 0);
    }


    /**
     * Method to create an image representation of a Patch.
     * 
     * @param patch The patch to create an image of
     * @param patchSize The desired full size of the square. If cropping is
     * enabled, the returned image may be cropped to half or a quarter of the
     * size, depending on the shape of the Patch. Otherwise, the returned 
     * image should be patchSize by patchSize.
     * @param cropped Whether or not to crop the image, shrinking the image
     * just like Patch's getSmallPatch() method does. This is meant for 
     * use with display in the Hand.
     * 
     * @return An Image that represents the Patch
     */
    public static BufferedImage generateImage(
        Patch patch, int patchSize, boolean cropped){
        int width = patchSize;
        int height = patchSize;
        Patch myPatch = patch;
        if (cropped){
            width = patchSize * myPatch.getWidth() / 2;
            height = patchSize * myPatch.getHeight() / 2;
            myPatch = myPatch.getSmallPatch();
        } 
        final BufferedImage display = new BufferedImage(width, height,
                BufferedImage.TRANSLUCENT);
        final Graphics2D canvas = display.createGraphics();

        for (int i=0; i<Patch.MAXTILES; i++){
            Tile theTile = new Tile(i, myPatch.getTile(i), 4);
            theTile.paintTile(canvas, patchSize);
        }
        return display;
    }



    /**
     * Enable or disable highlighting by the GlassPane. By default, it is
     * enabled.
     * @param flag
     */
    public void setHighlightEnabled(boolean flag){
        this.highlighted = flag;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    /**Enable or disable snappiness for this viewer in the Glasspane. By
     * default, it is enabled.
     * @param flag
     */
    public void setSnappiness(boolean flag){
        this.snappy = flag;
    }

    public boolean isSnappy() {
        return snappy;
    }

    /**
     * Switch between having a semi-transparent shadow or not. The shadow
     * is good for showing that something is empty but still making it
     * clear that the Viewer is there.
     * 
     * @param flag true for shadow, false for completely transparent
     */
    public void setShadowed(boolean flag){
        isShadowed = flag;
    }

}