package umm.digiquilt.control.patches;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.PatchViewer;
/**
 * ClickAction for PatchViewers in the current Block, handles pickups 
 * and drop off of Patches.
 *
 */
public class PatchMouseListener extends MouseAdapter {

    /**
     * The PatchViewer associated with this mouse action
     */
    private final PatchViewer patchViewer;
    
    /**
     * Access to the current Patch
     */
    private PatchWorks patchWorks;
    
    /**
     * Access to the current block.
     */
    private BlockWorks blockWorks;
    
    /**
     * The backend location for this action.
     */
    private int index;

    /**
     * Create a PatchMouseListener for the given viewer. 
     * @param viewer The viewer that this is to be attached to.
     * @param patchWorks Access to the currently held patch.
     * @param blockWorks Access to the current block.
     * @param index The patch index for this action.
     */
    public PatchMouseListener(final PatchViewer viewer, PatchWorks patchWorks,
            BlockWorks blockWorks, int index) {
        this.patchViewer = viewer;
        this.patchWorks = patchWorks;
        this.blockWorks = blockWorks;
        this.index = index;
    }

    /**
     * drops the held patch onto the patch model.
     * 
     * @param point
     *            the point where the mouse was when clicked.
     */
    private void performDrop(final Point point) {

        // Get a clone so we don't change the patch behind BlockWorks's back
        Patch mergedPatch = this.patchViewer.getPatch().getPatchClone();

        mergedPatch.mergePatch(patchWorks.getCurrentPatch(), 
                point.x * 1.0 / patchViewer.getWidth(),
                point.y * 1.0 / patchViewer.getHeight());

        if (patchWorks.getFromBWA()) {
            // Set the patch *without* creating an undo. Since the dropped patch
            // came from the block work area, and is being dropped onto the block
            // work area, an Undo would contain the state of the block while the 
            // patch was in the hand. This doesn't really make sense, clicking undo
            // should undo the move that just happened directly even though technically
            // it was two steps (pick up, put down) not one.
            blockWorks.setPatch(mergedPatch, index, false);
        } else {
            blockWorks.setPatch(mergedPatch, index);
        }
        patchWorks.releasePatch();
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        if (patchWorks.getIsHeld()) {
            performDrop(event.getPoint());
        } else if (! this.patchViewer.getPatch().equals(new Patch()) ){
            // pick up the patch.
            patchWorks.releasePatch();
            patchWorks.setSelectedPatch(this.patchViewer.getPatch(), true);
            blockWorks.setPatch(new Patch(), index);

        }
    }

}