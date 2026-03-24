/*
 * Created by jbiatek on Jul 1, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;

import static org.junit.Assert.*;

import org.junit.Test;

import umm.digiquilt.model.DefaultGrids;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.DefaultGrids.GridDivision;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.1 $
 *
 */

public class DefaultGridsTest {
    
    /**
     * Do some testing on DefaultGrids to make sure things will not break
     */
    @SuppressWarnings("boxing")
    @Test
    public void testDefaultGrids(){
        DefaultGrids.valueOf("TWO");
        DefaultGrids.valueOf("THREE");
        DefaultGrids.valueOf("FOUR");    
        
        for (DefaultGrids grids : DefaultGrids.values()){
            assertNotNull("Size should not be null", grids.getBlockSize());
            assertNotNull("Divisions shouldn't be null", grids.getDivisions());
            for (GridDivision div : grids.getDivisions()){
                assertNotNull("Button text shouldn't be null",div.getText());
                assertNotNull("Grid array shouldn't be null", div.getGrids());
                assertNotSame("Grid array shouldn't be empty",
                        div.getGrids().length);
                for (Grid grid : div.getGrids()){
                    assertNotNull("Grid shouldn't be null", grid);
                }
            }
        }
    }

}
