package umm.digiquilt.model.works;

import umm.digiquilt.model.Patch;
import umm.digiquilt.view.PatchViewer;
import umm.digiquilt.view.glasspane.HandGlassPane;
/**
 * 
 * Keeps track of the Patch that has been selected, if any. The selected patch
 * should show up in the hand. Also keeps track of where that patch came from.
 *
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 05:56:37 $
 * @version $Revision: 1.4 $
 */
public class PatchWorks {
    /**
     * Boolean for whether or not a patch is "held"
     */
    private boolean isHeld = false;

    /**
     * A patch to represent the currently selected patch in DigiQuilt
     */
    private Patch selectedPatch = new Patch();
    
    /**
     * Boolean for whether or not this shape came from the BlockWorkArea. Used
     * for certain cases where we don't want an Undo being created.
     */
    private boolean fromBlockWorkArea = false;
    
    /**
     * Reference to the GlassPane, so it can be turned on and off.
     */
    private HandGlassPane glassPane;
    
    /**
     * The size of a Patch, in pixels
     */
    private int patchSize;
    
    /**Create a new PatchWorks, which will handle holding the current
     * Patch and turning the GlassPane on and off.
     * @param glassPane
     * @param patchSize the size of a patch, in pixels
     */
    public PatchWorks(HandGlassPane glassPane, int patchSize) {
    	this.glassPane = glassPane;
    	this.patchSize = patchSize;
    }

    /**
     * "Releases" the currently held Patch and sets isHeld to false.
     * also, on release isHeldAShape is also set to false.
     */
    public void releasePatch() {
        isHeld = false;
        fromBlockWorkArea = false;
        selectedPatch = new Patch();
        glassPane.setVisible(false);
    }

    /**
     * @return selectedPatch the currently selected patch
     */
    public Patch getCurrentPatch() {
        return selectedPatch;
    }

    /**
     * @return the isHeld boolean (whether or not there is a patch being "held").
     */
    public boolean getIsHeld() {
        return isHeld;
    }


    /**
     * Method to set the currently selected patch
     * @param aPatch the patch to be selected/picked up
     * @param fromBWA a flag which is true if last patch picked up was from
     * the blockWorkArea.
     */
    public void setSelectedPatch(final Patch aPatch, boolean fromBWA) {
        // checks to see if the patch is transparent, to prevent
    	// picking up transparant patches.
    	if(! aPatch.equals(new Patch())){
    	 	selectedPatch = aPatch;
            isHeld = true;
            fromBlockWorkArea = fromBWA;
            
            glassPane.setHand(PatchViewer.generateImage(
                    aPatch, patchSize, true));
            glassPane.setVisible(true);
        }        	
        
    }
    
    /**
     * @return a flag which is true if last patch picked up was from blockWorkArea.
     */
    public boolean getFromBWA(){
        return fromBlockWorkArea;
    }
    
}
