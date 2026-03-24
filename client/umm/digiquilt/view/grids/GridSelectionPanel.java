/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.grids;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import umm.digiquilt.control.grids.GridDisplayAction;
import umm.digiquilt.control.grids.GridDivisionAction;
import umm.digiquilt.model.Block;
import umm.digiquilt.model.DefaultGrids;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.DefaultGrids.GridDivision;
import umm.digiquilt.model.works.BlockWorks;

/**
 * The "Select-a-grid" panel in the top right, which provides grid controls.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         fortunan $ on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 */
@SuppressWarnings("serial")
// supressing the Serialized warning from JPanel
public class GridSelectionPanel extends JPanel implements PropertyChangeListener {

    /**
     * The highlight panel paints highlights around the GridButtons.
     * Makes the same assumptions about the button's size and position.
     */
    GridButtonHighlightPanel highlight = new GridButtonHighlightPanel();

    /**
     * The current, "pop up" panel being shown on the right side.
     */
    JPanel currentPanel = new JPanel();
    
    /**
     * Layered pane for all the panels. Doesn't have a layout manager,
     * so things have to be placed and sized manually. But that's okay
     * in this case because we want to be able to place things down
     * to the pixel.
     */
    JLayeredPane layer = new JLayeredPane();

    /**
     * The hard coded size for the layered pane. The width of 84 comes from
     * two 32x32 buttons plus spacing of 5 pixels on either side of each 
     * button.
     */
    static final Dimension LAYERSIZE = new Dimension(84, 400);
    
    /**
     * The BlockWorks that this selection panel will set the grid to.
     */
    BlockWorks blockWorks;

    /**
     * List of all the left hand panels (division panels).
     */
    private List<GridButtonPanel> leftPanels = new ArrayList<GridButtonPanel>();

    /**
     * Create a default GridSelectionPanel.
     * @param gridViewPanel The GridViewPanel that this selection panel will 
     * set the grid to.
     */
    public GridSelectionPanel(BlockWorks blockWorks) {
        this.blockWorks = blockWorks;

        this.setOpaque(false);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());

        //Size and place the highlight panel in the layered pane
        highlight.setSize(LAYERSIZE);
        layer.add(highlight, Integer.valueOf(0));

        // Put the label at the top of this panel
        JLabel label = new JLabel("Select-a-grid");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        this.add(label, c);

        // Add the layered pane to this panel
        layer.setPreferredSize(LAYERSIZE);
        layer.setMinimumSize(LAYERSIZE);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        this.add(layer, c);

        
        for (DefaultGrids blockSize : DefaultGrids.values()){
            List<GridButton> divButtons = new ArrayList<GridButton>(); 
            
            // Create the top, default button
            Grid defaultGrid = new Grid(blockSize.getBlockSize(), blockSize.getBlockSize(), 0, 0);
            GridButton defaultButton = new GridButton(defaultGrid);
            defaultButton.addActionListener(
                    new GridDisplayAction(defaultGrid, blockWorks));
            divButtons.add(defaultButton);

            // Create the "no grid" button
            Grid blank = new Grid();
            GridButton blankButton = new GridButton(blank);
            blankButton.addActionListener(new GridDisplayAction(blank, 
                    blockWorks));
            divButtons.add(blankButton);

            for (GridDivision division : blockSize.getDivisions()){
                GridButton divButton = addPopUpPanel(division.getText(), division.getGrids());
                divButtons.add(divButton);
            }

            // Create the left divisions panel and add them
            GridButtonPanel divPanel = new GridButtonPanel(divButtons);
            divPanel.setBlockSize(blockSize.getBlockSize());

            layer.add(divPanel, Integer.valueOf(1));
            leftPanels.add(divPanel);
        }
        
        // Set the initial size
        changeToSideSize(blockWorks.getSideSize());
    }

    /**
     * Set the highlight to be on this button number and this panel.
     * @param button
     * @param panel
     */
    public void setHighlight(GridButton button, JPanel panel){
        int leftIndex = 0;
        for (GridButtonPanel divPanel : leftPanels){
            if (divPanel.buttons.indexOf(button) != -1){
                leftIndex = divPanel.buttons.indexOf(button);
                break;
            }
        }
        highlight.setHighlights(leftIndex, panel.getComponentCount());

        // Activate the given pane, disable the current one
        currentPanel.setVisible(false);
        panel.setVisible(true);
        currentPanel = panel;
    }


    /**Creates a panel containing GridButtons for each of the Grids
     * passed in, and a GridButton that when clicked will bring up
     * that panel.
     * 
     * @param buttonText
     * @param grids
     * @return the grid button that will bring forward the popup panel
     */
    private GridButton addPopUpPanel(String buttonText, Grid[] grids){

        GridButton divButton = new GridButton(buttonText);
        ArrayList<GridButton> buttons = new ArrayList<GridButton>();
        // Create buttons for each Grid, and add a GridDisplayAction
        for (Grid lines : grids){
            GridButton button = new GridButton(lines);
            button.addActionListener(new GridDisplayAction(lines, divButton));
            button.addActionListener(new GridDisplayAction(lines, blockWorks));
            buttons.add(button);
        }
        GridButtonPanel panel = new GridButtonPanel(buttons);
        // Now that we have a panel, we make it so that the given
        // button will display this new panel when clicked.
        divButton.addActionListener(
                new GridDivisionAction(this, divButton, panel));
        // Place the panel, turn it off initially, and add it
        panel.setLocation(42, 0);
        panel.setVisible(false);
        layer.add(panel, Integer.valueOf(1));

        return divButton;
    }



    /**
     * Sets this selection panel's viewer to the given set of lines.
     * 
     * @param lines
     */
    public void setGrid(Grid lines) {
        blockWorks.setGrid(lines);
    }


    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Block newBlock = (Block) evt.getNewValue();
        Block oldBlock = (Block) evt.getOldValue();
        
        if (newBlock.getSideSize() != oldBlock.getSideSize()){
            changeToSideSize(newBlock.getSideSize());
        }
    }
    
    /**
     * Switch the available grids to this block size.
     * 
     * @param sideSize (2, 3, 4)
     */
    private void changeToSideSize(int sideSize){
        currentPanel.setVisible(false);
        highlight.setHighlights(0, -1);

        for (GridButtonPanel panel : leftPanels){
            if (panel.getBlockSize() == sideSize){
                panel.setVisible(true);
            } else{
                panel.setVisible(false);
            }
        }

    }



}