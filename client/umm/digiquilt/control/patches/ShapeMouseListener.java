package umm.digiquilt.control.patches;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.PatchViewer;

    /**
     * Action for when the GenericPatchViewer is clicked on. Dictates picking up of
     * shape, or if the shape being held is the same as this one, dropping it
     * "back" onto this viewer (really it just disappears).
     * 
     */
    public class ShapeMouseListener extends MouseAdapter {
        /**
         * The Viewer for this click action
         */
        PatchViewer viewer;
        
        /**
         * Access to the current Patch
         */
        PatchWorks patchWorks;
        
        /**
         * Create a new ShapeMouseListener for this viewer
         * @param viewer
         * @param patchWorks 
         */
        public ShapeMouseListener(PatchViewer viewer, PatchWorks patchWorks) {
            this.viewer = viewer;
            this.patchWorks = patchWorks;
        }

        @Override
        public void mousePressed(final MouseEvent event) {
            if (!(patchWorks.getIsHeld())) {
                // Pick up the Shape
                patchWorks.releasePatch();
                patchWorks.setSelectedPatch(viewer.getPatch(), false);
            } else if (patchWorks.getCurrentPatch().equals(viewer.getPatch())) {
                // "Drop" the shape back into the viewer
                patchWorks.releasePatch();
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent event) {
            Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            viewer.setCursor( hand );
        }
    }