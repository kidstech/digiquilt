/*
 * Created by jbiatek on Feb 23, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.BlockTest;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.view.Tile;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class TileTest {

    /**
     * 
     */
    @Test
    public void runTileCoordinateTests(){
        testTileCoordinates(4);
        testTileCoordinates(9);
        testTileCoordinates(16);
    }

    /**
     * @param blockSize 
     * 
     */
    public void testTileCoordinates(int blockSize){
        int patchSize = 50;

        int sideSize = (int) Math.sqrt(blockSize);
        int tileSize = patchSize / 2;
        int x = patchSize / 4;
        int y = patchSize / 4;
        double theta = 0;

        for (int i=0; i<blockSize*Patch.MAXTILES; /* gets incremented later*/){
            Fabric fabric = BlockTest.getRandomFabric();


            Tile testTile = new Tile(i, fabric, blockSize);

            assertEquals(x, testTile.getX(patchSize));
            assertEquals(y, testTile.getY(patchSize));
            assertEquals(theta, testTile.getRotation(), .0001);
            assertEquals(fabric.getColor(), testTile.getColor());
            assertEquals(fabric, testTile.getFabric());
            assertEquals(i, testTile.getIndex());

            theta = getNextTheta(theta);

            i++;
            // Let's see if we need to move x and y:
            if (i % (sideSize * Patch.MAXTILES) == 0){
                // It's moved to a whole new row:
                x = patchSize / 4;
                y += tileSize;
            } else if (i % Patch.MAXTILES == 0){
                // Just moving over to a new Patch:
                x += tileSize;
                y -= tileSize;
            } else if (i % (Patch.MAXTILES / 2) == 0){
                // Moving to bottom half of the same Patch:
                x -= tileSize;
                y += tileSize;
            } else if (i%4 == 0){
                // Just need to move to the right one quadrant:
                x += tileSize;
            }

        }


    }
    
    /**
     * 
     */
    @Test
    public void testGetTilesFromBlock(){
        checkGetTilesFromBlock(4);
        checkGetTilesFromBlock(9);
        checkGetTilesFromBlock(16);
    }
    
    
    
    /**
     * @param blockSize
     */
    private void checkGetTilesFromBlock(int blockSize){
        Block testBlock = BlockTest.createRandomBlock(blockSize);
        List<Tile> tiles = Tile.getTilesFromBlock(testBlock);
        
        int index = 0;
        for (Fabric fabric : testBlock.allTiles()){
            Tile tile = tiles.get(index);
            
            assertEquals(index, tile.getIndex());
            assertEquals(tile.getFabric(), fabric);
            
            index++;
        }
        
    }

    /**Given the theta value for an index i, returns the expected theta for
     * index i+1.
     * @param currentTheta
     * @return the next theta value
     */
    private static double getNextTheta(double currentTheta){
        if (currentTheta == 0){ 
            return -Math.PI/2;
        } else if (currentTheta == -Math.PI / 2){
            return Math.PI/2;
        } else if (currentTheta == Math.PI / 2){
            return Math.PI;
        } else if (currentTheta == Math.PI){
            return 0;
        }

        return Double.NaN;
    }


    /**
     * 
     */
    @Test
    public void testGetTileShape(){
        int patchSize = 50;
        
        int tileSize = patchSize / 2;
        Polygon expectedShape = new Polygon();
        expectedShape.addPoint(0, 0);
        expectedShape.addPoint(tileSize/2, -tileSize/2);
        expectedShape.addPoint(-tileSize/2, -tileSize/2);

        Shape theShape = Tile.getTileShape(patchSize);
        assertShapesAreEqual(expectedShape, theShape);
    }
    
    /**
     * Check that the given shapes are the same, since they don't seem
     * to have a useful equals() method... sigh.
     * 
     * @param shape1
     * @param shape2
     */
    private static void assertShapesAreEqual(Shape shape1, Shape shape2){
        Rectangle bounds1 = shape1.getBounds();
        Rectangle bounds2 = shape2.getBounds();
        
        int xStart = Math.min(bounds1.x, bounds2.x);
        int xEnd = Math.max(bounds1.x+bounds1.width, bounds2.x+bounds2.width);
        
        int yStart = Math.min(bounds1.y, bounds2.y);
        int yEnd = Math.max(bounds1.y+bounds1.width, bounds2.y+bounds2.width);

        for (int x=xStart; x<xEnd; x++){
            for (int y=yStart; y<yEnd; y++){
                if (shape1.contains(x, y) != shape2.contains(x, y)){
                    fail("The shapes differed at "+x+", "+y);
                }
            }
        }
    }

    /**
     * The paintTile() method should do the basic painting of a tile
     * for us. (The Tile class gives us all the information and tools we
     * need, this is just a convenience that takes care of the trivial case.)
     */
    @Test
    public void testTileDrawing(){
        int patchSize = 100;
        int blockSize = 16;
        
        
        for (int i=0; i<blockSize*Patch.MAXTILES; i++){
            Fabric fabric = BlockTest.getRandomFabric();
            Tile theTile = new Tile(i, fabric, blockSize);
            
            // Create the expected shape. This is what we'd do if we were
            // drawing it ourselves.
            int x = theTile.getX(patchSize);
            int y = theTile.getY(patchSize);
            double theta = theTile.getRotation();
            Shape tileShape = Tile.getTileShape(patchSize);
            AffineTransform t = AffineTransform.getTranslateInstance(x, y);
            t.rotate(theta);
            Shape expectedShape = t.createTransformedShape(tileShape);
            
            // Now we'll see what the Tile comes up with when we give it
            // a Graphics object and ask it to paint:
            Graphics2D mockGraphics = mock(Graphics2D.class);
            
            
            theTile.paintTile(mockGraphics, patchSize);
            
            
            // Check and see what happened:
            InOrder order = inOrder(mockGraphics);
            ArgumentCaptor<Shape> captor = 
                ArgumentCaptor.forClass(Shape.class);
            
            order.verify(mockGraphics).setColor(fabric.getColor());
            order.verify(mockGraphics).fill(captor.capture());
            
            assertShapesAreEqual(expectedShape, captor.getValue());
            
            
        }
        
    }

}
