package umm.digiquilt.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import umm.digiquilt.control.patches.PatchMouseListener;
import umm.digiquilt.model.Block;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;

/**
 * A BlockViewer which interacts with PatchWorks and BlockWorks, meaning
 * that Patches can be picked up and dropped onto this viewer and BlockWorks
 * will be changed.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class InteractiveBlockViewer extends BlockViewer
implements PropertyChangeListener {

    /**
     * Access to the current Patch, if it is given. This will be null
     * if a PatchWorks was not passed in to the constructor.
     */
    private PatchWorks patchWorks;


    /**Make an interactive BlockViewer. That is, clicking on Patches will
     * result in them being put in the hand (via the given PatchWorks) and
     * any interaction with this viewer will result in changes to the given
     * BlockWorks.
     * @param patchWorks
     * @param blockWorks
     * @param patchSize Size of a patch, in pixels
     */
    public InteractiveBlockViewer(PatchWorks patchWorks,
            BlockWorks blockWorks, int patchSize){
        super(blockWorks.getCurrentBlockClone(), patchSize);
        this.patchWorks = patchWorks;
        //It's really the listeners that get attached to each patch viewer
        //that do the work, to be honest.
        attachListeners(blockWorks);
    }



    /**
     * Attach PatchClickActions to the PatchViewers in this viewer. This
     * only works if we have PatchWorks and BlockWorks to create the
     * PatchClickActions.
     * @param blockWorks The current Block.
     */
    private void attachListeners(BlockWorks blockWorks){
        int location = 0;
        for (PatchViewer patchViewer : patchViewers){
            patchViewer.addMouseListener(new PatchMouseListener(
                    patchViewer, patchWorks, blockWorks, location));
            location++;
        }
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Block newBlock = (Block) evt.getNewValue();
        Block oldBlock = (Block) evt.getOldValue();
        BlockWorks blockWorks = (BlockWorks) evt.getSource();
        
        // BlockWorks has changed!
        if (newBlock.getSize() != oldBlock.getSize()){
            // The size itself has changed, so let's just get a fresh start.
            sideSize = newBlock.getSideSize();
            createPatchViewers(newBlock);
            attachListeners(blockWorks);
        } else {
            // The size hasn't changed. We can just check each of our
            // patch viewers and update them if necessary.
            for (int i=0; i<patchViewers.length; i++){
                Patch newPatch = newBlock.getPatch(i);
                if (!newPatch.equals(patchViewers[i].getPatch())){
                    patchViewers[i].setPatch(newPatch);
                } else {
                }
            }
        }
    }
}
