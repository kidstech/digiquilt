package umm.digiquilt.control.grids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import umm.digiquilt.view.grids.GridButton;
import umm.digiquilt.view.grids.GridButtonPanel;
import umm.digiquilt.view.grids.GridSelectionPanel;

/**
 * Action to display a division panel when a button is clicked. Also
 * sets the grid if the button has one.
 * 
 * @author deragonmr
 *
 */
public class GridDivisionAction implements ActionListener {

    /**
     * The button that this listener gets the grid from, if it has one.
     */
    private GridButton button;
    
    /**
     * The panel that this button should bring to the front when fired.
     */
    private JPanel popUp;
    
    /**
     * Pointer back to the GridSelectionPanel. The task of setting the
     * highlight and actually putting the correct button panel up is
     * delegated to this, since it can keep track of what every single
     * panel is doing and whatnot.
     */
    private GridSelectionPanel selectionPanel;

    /**Create a new GridDivisionAction. When this action is performed,
     * a number of things will happen. First, the current grid will be
     * set to the grid contained in the button supplied (if any). Then, 
     * this action will tell the GridSelectionPanel to highlight that 
     * button and also to bring the supplied panel to the front.
     * 
     * @param selectionPanel
     * @param divisionButton
     * @param popUpPanel
     */
    public GridDivisionAction(GridSelectionPanel selectionPanel,
            GridButton divisionButton, 
            GridButtonPanel popUpPanel){
        button = divisionButton;
        popUp = popUpPanel;
        this.selectionPanel = selectionPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if ( button.hasGrid() ){
            selectionPanel.setGrid(button.getGrid());
        }
        selectionPanel.setHighlight(button, popUp);
    }
}
