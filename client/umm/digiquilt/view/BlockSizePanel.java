package umm.digiquilt.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.view.grids.GridButton;

/**
 * The panel that lets you make 4x4, 3x3, or 2x2 blocks. 
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class BlockSizePanel extends JPanel {
    
    /**
     * Create a new BlockSizePanel which will change
     * the given BlockWorks. Also listens for when the size
     * of the block changes, and resizes the given GridViewPanel
     * accordingly.
     * 
     * @param works
     */
    public BlockSizePanel(BlockWorks works){
        final BlockWorks blockWorks = works;
        final Grid fourGrid = new Grid(4,4,0,0);
        GridButton four = new GridButton("4", fourGrid);
        four.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                blockWorks.clear();
                blockWorks.setCurrentBlock(new Block(16));
            }

        });
        
        final Grid threeGrid = new Grid(3,3,0,0);
        GridButton three = new GridButton("3", threeGrid);
        three.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                blockWorks.clear();
                blockWorks.setCurrentBlock(new Block(9));
            }

        });
        
        final Grid twoGrid = new Grid(2,2,0,0);
        GridButton two = new GridButton("2", twoGrid);
        two.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                blockWorks.clear();
                blockWorks.setCurrentBlock(new Block(4));
            }

        });
        
        this.add(four);
        this.add(three);
        this.add(two);
        this.setOpaque(false);
    }


}
