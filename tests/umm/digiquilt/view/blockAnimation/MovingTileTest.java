/*
 * Created by jbiatek on Feb 24, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.blockAnimation;

import static org.junit.Assert.*;

import org.junit.Test;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.view.Tile;
import umm.digiquilt.view.blockAnimation.MovingTile;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class MovingTileTest {
    
    /**
     * 
     */
    @Test
    public void testMovingTile(){
        int patchSize = 100;
        int blockSize = 16;
        
        Tile startTile = new Tile(0, Fabric.RED, blockSize);
        Tile endTile = new Tile(191, Fabric.RED, blockSize);
        
        MovingTile movingTile = new MovingTile(startTile, endTile, patchSize);
        
        int startX = 25;
        int startY = 25;
        int endX = 375;
        int endY = 275;
        double startTheta = 0;
        double endTheta = Math.PI;
        
        int steps = 50;
        int xSteps = (endX - startX) / steps;
        int ySteps = (endY - startY) / steps;
        double thetaSteps = (endTheta - startTheta) / steps;
        
        for (int i=0; i<steps; i++){
            float percent = (i * 1f) / steps;
            movingTile.setPercent(percent);
            
            assertEquals(i+"", startTheta + i*thetaSteps, 
                    movingTile.getTheta(), .00001);
            assertEquals(i+"", startX + i * xSteps, movingTile.getX());
            assertEquals(i+"", startY + i * ySteps, movingTile.getY());
            
        }
        
    }

}
