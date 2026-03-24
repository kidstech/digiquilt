package umm.digiquilt.view;

import umm.digiquilt.model.Grid;

/**
 * Interface for anything that can take in and display Grid.
 * 
 * @author biatekjt
 */
public interface GridDisplayer {
    /**
     * Sets the current Grid to the given set.
     * 
     * @param newLines
     */
    void setGrid(Grid newLines);
}
