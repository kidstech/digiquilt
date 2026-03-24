package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import umm.digiquilt.model.Block;

/**
 * The layer in the BlockWorkArea that shows the actual Block.
 * 
 * @author deragonmr
 *
 */
@SuppressWarnings("serial")
public class BlockViewer extends JPanel{

    /**
     * The size of one side of a patch, in pixels.
     */
    private int patchSize;

    /**
     * Gridbag component constraints.
     */
    GridBagConstraints constraints = new GridBagConstraints();

    /**
     * Array of all the PatchViewers in this BlockViewer.
     */
    PatchViewer[] patchViewers;
    
    /**
     * The dimensions of the displayed block. In other words, the block should be
     * sideSize by sideSize (in terms of patches).
     */
    protected int sideSize;
    

    /**
     * Create a new BlockViewer.
     * @param block The block to display.
     */
    public BlockViewer(Block block){
        this(block, 100);
    }
    
    /**
     * Create a new BlockViewer of the given block, with each Patch
     * being patchSize by patchSize.
     * @param block The block to display
     * @param patchSize The dimension of each patch (in pixels).
     */
    public BlockViewer(Block block, int patchSize){
        this.patchSize = patchSize;
      //this.setBackground(new Color(253, 205, 103) );
        this.setLayout(new GridBagLayout());

        sideSize=block.getSideSize();
        
        // Create the patch viewers
        createPatchViewers(block);

    }


    /**Creates patch viewers for the given block. This removes any components
     * that have been added, and sets the preferred size based on the size
     * of the given block.
     * 
     * @param block
     */
    protected final void createPatchViewers(Block block){
        this.removeAll();
        patchViewers = new PatchViewer[block.getSize()];
        int location = 0;
        for (int i = 0; i < sideSize; i++) {
            for (int j = 0; j < sideSize; j++){
                final PatchViewer patchViewer =
                    new PatchViewer(block.getPatch(location), patchSize);
                patchViewer.setPreferredSize(new Dimension(patchSize, patchSize));
                patchViewer.setShadowed(true);
                patchViewer.setName("Patch"+location);
                patchViewers[location] = patchViewer;
                constraints.gridx=j;
                constraints.gridy=i;
                this.add(patchViewer, constraints);
                location++;
            }
        }
        Dimension size = new Dimension(sideSize*patchSize, sideSize*patchSize);
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        validate();
    }


}
