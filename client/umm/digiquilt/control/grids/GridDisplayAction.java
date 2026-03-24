package umm.digiquilt.control.grids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import umm.digiquilt.model.Grid;
import umm.digiquilt.view.GridDisplayer;

/**
 * Action to display a given Grid when performed. Can also set
 * a given button's Grid to the same.
 *
 * @author deragonmr
 * 
 */
public class GridDisplayAction implements ActionListener {

    /**
     * The grid to set when this action is performed.
     */
    private Grid gridLines;

    /**
     * This viewer will have its grid set to this action's Grid
     * when the action is performed.
     */
    private GridDisplayer gridViewer;

    /**
     * Create new GridDisplayAction, which will set the current grid to
     * the given set of Grid when an action is performed.
     * 
     * @param lines The grid to be set.
     * @param gridViewer The GridViewer that will have its Grid set.
     */
    public GridDisplayAction(Grid lines, GridDisplayer gridViewer) {
        gridLines = lines;
        this.gridViewer = gridViewer;
    }


    public void actionPerformed(ActionEvent e) {
        gridViewer.setGrid(gridLines);
    }

}
