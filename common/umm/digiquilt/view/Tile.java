/*
 * Created by jbiatek on Feb 23, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;

/**
 * Represents the view of a single tile. It can either calculate the proper
 * location to draw the tile, or it can simply paint itself using a Graphics
 * object.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class Tile {

    /**
     * The fabric of this tile.
     */
    private Fabric fabric;
    
    /**
     * The rotation of this tile
     */
    private double theta;

    /**
     * The index of this tile.
     */
    private int index;
    
    /**
     * The size of the whole Block that this Tile belongs to
     */
    private int blockSize;


    /**
     * @param index
     * @param fabric
     * @param blockSize
     * @param patchSize
     */
    public Tile(int index, Fabric fabric, int blockSize) {
        this.index = index;
        this.fabric = fabric;
        this.blockSize = blockSize;
        
        theta = getThetaForIndex(index);
    }
    

    /**
     * @param patchSize 
     * @return the x coordinate that the shape should be placed at, if a Patch
     * is patchSize by patchSize
     */
    public int getX(int patchSize) {
        return getXForIndex(index, blockSize, patchSize);
    }

    /**
     * @param patchSize 
     * @return the y coordinate that the shape should be placed at, if a Patch
     * is patchSize by patchSize
     */
    public int getY(int patchSize) {
        return getYForIndex(index, blockSize, patchSize);
    }

    /**
     * @return the amount of rotation that should be applied to the shape
     */
    public double getRotation() {
        return theta;
    }
    /**
     * @return the color that the shape should be filled with
     */
    public Color getColor() {
        return fabric.getColor();
    }
    
    /**
     * @return the fabric for this Tile
     */
    public Fabric getFabric() {
        return fabric;
    }

    /**
     * @return the index of this Tile.
     */
    public int getIndex() {
        return index;
    }

    

    /**Given an index, returns the X coordinate for that tile, assuming
     * that we're dealing with tiles of size tileSize/2 and a block of
     * size blockSize by blockSize.
     * 
     * @param index
     * @param blockSize
     * @param patchSize 
     * @return the X coordinate
     */
    private static int getXForIndex(int index, int blockSize, int patchSize){
        int tileSize = patchSize / 2;
        int sideSize = (int) Math.sqrt(blockSize);
        
        // Get which patch number this index is on
        int patchNumber = index/Patch.MAXTILES;
        // Find the x coordinate of the top left corner of this patch
        int startX = tileSize*2 * (patchNumber % sideSize);
        // Bump x value up to where the corner actually goes, in the
        // middle of the quadrant
        startX += tileSize/2;
        // Get the tile's number relative to the patch
        int tileNumber = index - patchNumber*Patch.MAXTILES;
        if ((tileNumber >3 && tileNumber<8) || (tileNumber>11)){
            // If this tile is on the right side of this patch, move
            // it over to that side.
            startX += tileSize;
        }
        return startX;
    }

    /**Given an index, returns the Y coordinate for that tile, assuming
     * that we're dealing with tiles of size tileSize and a block of size
     * blockSize by blockSize.
     * 
     * @param index
     * @param blockSize 
     * @param patchSize 
     * @return the Y coordinate
     */
    private static int getYForIndex(int index, int blockSize, int patchSize){
        int tileSize = patchSize / 2;
        int sideSize = (int) Math.sqrt(blockSize);
        
        // Get which patch number this index is on
        int patchNumber = index/Patch.MAXTILES;
        // Find the Y coordinate of the top left corner of this pach
        int startY = tileSize*2 * (patchNumber / sideSize);
        // Bump Y value up to where the corner actually goes, in the
        // middle of the quadrant
        startY += tileSize/2;
        // Get the tile's number relative to the patch
        int tileNumber = index - patchNumber*Patch.MAXTILES;
        if ( tileNumber > 7){
            // If this tile is on the bottom of this patch, move
            // it down one more quadrant
            startY += tileSize;
        }
        return startY;
    }

    /**Given the index of a tile, returns the proper rotation for a 
     * tile at that location. 
     * @param index
     * @return the angle in radians to rotate theTile for this index
     */
    private static double getThetaForIndex(int index){
        // Positive is clockwise ( it seems backwards because in
        // Swing, the positive y axis points down)
        switch (index % 4){
        case 0:
            return 0;
        case 1:
            return -Math.PI * 1/2;
        case 2:
            return Math.PI * 1/2;
        }
        // Must be case 3 then
        return Math.PI;

    }


    /**
     * Create and return a Shape which can be used to draw this Tile. It
     * will not be rotated or translated or anything, but it will be the
     * correct size. 
     * 
     * (this is so that things like animation, which want to do averages
     * of two different tiles, can get the proper coordinates and then
     * do custom drawing themselves, with whatever rotations they want)
     * 
     * @param patchSize 
     * @return the shape
     */
    public static Shape getTileShape(int patchSize) {
        int tileSize = patchSize/2;
        Polygon theShape = new Polygon();
        theShape.addPoint(0, 0);
        theShape.addPoint(tileSize/2, -tileSize/2);
        theShape.addPoint(-tileSize/2, -tileSize/2);
        return theShape;
    }


    /**
     * Paint this tile using the given Graphics object.
     * 
     * @param g2
     * @param patchSize 
     */
    public void paintTile(Graphics2D g2, int patchSize) {
        AffineTransform t = 
            AffineTransform.getTranslateInstance(
                    getX(patchSize), getY(patchSize));
        t.rotate(getRotation());
                
        Shape tile = t.createTransformedShape(getTileShape(patchSize));
        
        g2.setColor(getColor());
        g2.fill(tile);
        
    }


    /**
     * @param block
     * @param patchSize
     * @return a list containing a Tile for each tile in the block
     */
    public static List<Tile> getTilesFromBlock(Block block) {
        List<Tile> tiles = new ArrayList<Tile>();
        int index = 0;
        for (Fabric fabric : block.allTiles()){
            tiles.add(new Tile(index, fabric, block.getSize()));
            index++;
        }
        return tiles;
    }


}
