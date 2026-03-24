package umm.digiquilt.view.grids;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * A Panel that is specially formatted for GridButtons and compatible with
 * the GridButtonHighlight panel and other grid selection components.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class GridButtonPanel extends JPanel {
    
    /**
     * The size of one panel. Assumes that GridButtons are 32x32, 
     * with a 5 pixel space on each side.
     */
    private static final Dimension PANELSIZE = new Dimension(42, 400);
    
    
    /**
     * List of all buttons on this panel. Others use this to find out
     * where a button is located and highlight it if need be. 
     */
    List<GridButton> buttons = new ArrayList<GridButton>();
    
    
    /**
     * A block size for this panel. This is so that division panels
     * can have a specified block size that they are meant for. 
     */
    private int blockSize = 0;
    
    /**
     * Create a panel containing all of these buttons, all correctly
     * laid out.
     * 
     * @param buttons ArrayList of buttons to be added
     */
    public GridButtonPanel(List<GridButton> buttons){
        this.buttons = buttons;
        this.setSize(PANELSIZE);
        this.setPreferredSize(PANELSIZE);
        this.setMinimumSize(PANELSIZE);
        this.setOpaque(false);
        this.setLayout(new GridBagLayout());
        // Set up common constraints
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(5,5,0,5);

        for (int i=0; i < buttons.size(); i++){
            c.gridy = i;
            if (i == buttons.size()-1){
                // Add all extra space to last button
                c.weighty = 1;
            }
            this.add(buttons.get(i), c);
        }
    }
    
    /**
     * Set a block size for this panel. This is so that division panels
     * can have a specified block size that they are meant for. 
     * 
     * @param size
     */
    public void setBlockSize(int size){
        blockSize = size;
    }
    
    /**
     * @return the block size that this panel is meant for.
     */
    public int getBlockSize(){
        return blockSize;
    }
    
    /**
     * @return a List of all grid buttons on this panel
     */
    public List<GridButton> getButtons(){
        return buttons;
    }

}
