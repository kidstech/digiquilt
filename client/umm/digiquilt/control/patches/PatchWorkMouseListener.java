package umm.digiquilt.control.patches;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.PatchViewer;
/**
 * ClickAction for the PatchWorkViewers.
 */
public class PatchWorkMouseListener extends MouseAdapter {

    /**
     * The PatchWorkViewer for this ClickAction.
     */
    private final PatchViewer patchWorkViewer;

    /**
     * Access to the current Patch
     */
    private PatchWorks patchWorks;
    
    /**
     * Create a new PatchWorkMouseListener
     * @param viewer
     * @param patchWorks 
     */
    public PatchWorkMouseListener(PatchViewer viewer, PatchWorks patchWorks) {
        this.patchWorkViewer = viewer;
        this.patchWorks = patchWorks;
    }
    

    /**
     * Drop a patch onto the patch viewer.
     * 
     * @param point where it was dropped (used for small patches)
     */
    private void performDrop(final Point point) {

        this.patchWorkViewer.getPatch().mergePatch(
                patchWorks.getCurrentPatch(),
                point.x * 1.0 / patchWorkViewer.getWidth(),
                point.y * 1.0 / patchWorkViewer.getHeight()
                );
        patchWorks.releasePatch();
        this.patchWorkViewer.refreshImage();
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        if (patchWorks.getIsHeld()) {
            performDrop(event.getPoint());
        } else {
            patchWorks.releasePatch();
            patchWorks.setSelectedPatch(this.patchWorkViewer.getPatch(), false);
        }       
    }
    
    @Override
    public void mouseEntered(final MouseEvent evetn) {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        if (!this.patchWorkViewer.getPatch().equals(new Patch())) {
            this.patchWorkViewer.setCursor(hand);
        } else {
            this.patchWorkViewer.setCursor(Cursor.getDefaultCursor());
        }
    }
}