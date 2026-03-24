/*
 * Created by biatekjt on Mar 27, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class BlockWorkArea extends JLayeredPane {
    
    /**
     * The BlockViewer that we made.
     */
    private InteractiveBlockViewer blockViewer;

    /**
     * Create the BlockWorkArea, which is where the current Block and Grid are shown.
     * 
     * @param blockWorks
     * @param patchWorks
     * @param patchSize
     */
    public BlockWorkArea(BlockWorks blockWorks, PatchWorks patchWorks, int patchSize){
        setOpaque(false);
        
        final GridViewPanel grid = new GridViewPanel(blockWorks.getSideSize(), patchSize);
        blockViewer = new InteractiveBlockViewer(patchWorks, blockWorks, patchSize);
        blockViewer.setOpaque(false);
        // The block viewer should be notified of changes
        blockWorks.addPropertyChangeListener("currentBlock", blockViewer);
        
        // The grid wants to know when the size of the current block changes
        blockWorks.addPropertyChangeListener("currentBlock", 
                new BlockSizeWatcher(patchSize, grid));
                
                
        blockWorks.addPropertyChangeListener("grid", new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                grid.setGrid((Grid) evt.getNewValue());
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(grid, constraints);
        add(blockViewer, constraints);
        setLayer(grid, 1);
        setLayer(blockViewer, 0);
    }
    
    /**
     * Set the BlockViewer to be visible or not visible.
     * 
     * @param visibility
     */
    public void setBlockVisible(boolean visibility){
        blockViewer.setVisible(visibility);
    }
    
    
    /**
     * A listener which listens for block changes in BlockWorks. When it sees
     * that the Block has changed size, it changes the minimum and preferred
     * sizes of a panel.
     * 
     * @author Jason Biatek, last changed by $Author: lamberty $
     * on $Date: 2008/01/22 17:50:24 $
     * @version $Revision: 1.1 $
     *
     */
    private class BlockSizeWatcher implements PropertyChangeListener {
        
        /**
         * The size of one Patch.
         */
        int patchSize;
        /**
         * The panel to change the size of.
         */
        JPanel panel;
        
        /**
         * Create a listener to react to changes in the Block. It will make 
         * sure that the given panel changes size when the Block does.
         * 
         * @param patchSize the size of one Patch.
         * @param panel The panel to change the size of.
         */
        public BlockSizeWatcher(int patchSize, JPanel panel){
            this.patchSize = patchSize;
            this.panel = panel;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {
            Block newBlock = (Block) evt.getNewValue();
            Block oldBlock = (Block) evt.getOldValue();
            
            if (newBlock.getSize() != oldBlock.getSize()){
                int sideSize = newBlock.getSideSize();
                Dimension mySize = new Dimension(sideSize*patchSize,sideSize*patchSize);
                panel.setPreferredSize(mySize);
                panel.setMinimumSize(mySize);

            }
        }
        
        
    }
    
//  
//  public void onBlockChange(Block newBlock) {
//      int sideSize = newBlock.getSideSize();
//      if (mySize.width != patchSize*sideSize){ // block size has changed
//          mySize = new Dimension(sideSize*patchSize,sideSize*patchSize);
//          this.setPreferredSize(mySize);
//          this.setMinimumSize(mySize);
//      }
//  }
//
//
//  /* (non-Javadoc)
//   * @see umm.softwaredevelopment.digiquilt.model.works.GridChangeListener#onGridChange(umm.softwaredevelopment.digiquilt.model.Grid)
//   */
//  public void onGridChange(Grid newGrid) {
//      setGrid(newGrid);
//  }
//  
    
}
