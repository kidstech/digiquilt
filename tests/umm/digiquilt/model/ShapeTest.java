package umm.digiquilt.model;

import static junit.framework.Assert.*;

import org.junit.Test;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.Shape;

/**
 * Test the Shape enum.
 */
public class ShapeTest{

    /**
     * Test half-triangle shape
     */
    @Test
    public void testGetHalfTriangle() {
        Patch halfTriangle = new Patch();
        halfTriangle.setTile(6, Fabric.REDVIOLET);
        halfTriangle.setTile(7, Fabric.REDVIOLET);
        for (int i = 10; i < 16; i++) {
            halfTriangle.setTile(i, Fabric.REDVIOLET);
        }
        assertEquals("Shape.HALFTRIANGLE should equal halfTriangle", 
                halfTriangle, Shape.HALFTRIANGLE.getPatch(Fabric.REDVIOLET) );

        halfTriangle.swapFabrics(Fabric.REDVIOLET, Fabric.GREEN);
        assertEquals("Green HALFTRIANGLE should equal halfTriangle", 
                halfTriangle, Shape.HALFTRIANGLE.getPatch(Fabric.GREEN) );
    }

    /**
     * Test full square shape
     */
    @Test
    public void testGetFullSquare() {
        Patch fullSquare = new Patch();
        for (int i = 0; i < 16; i++) {
            fullSquare.setTile(i, Fabric.REDVIOLET);
        }
        assertEquals("Shape.FULLSQUARE should equal fullSquare", 
                fullSquare, Shape.FULLSQUARE.getPatch(Fabric.REDVIOLET));
    }

    /**
     * Test half rectangle shape
     */
    @Test
    public void testGetHalfRectangle(){
        Patch halfRect = new Patch();
        for (int i=0; i<8; i++){
            halfRect.setTile(i, Fabric.BLUE);
        }
        assertEquals("Shape.HALFRECTANGLE should equal halfRect", 
                halfRect, Shape.HALFRECTANGLE.getPatch(Fabric.BLUE));
    }
    
    /**
     * Test quarter triangle shape
     */
    @Test
    public void testGetQuarterTriangle(){
        Patch quarterTriangle = new Patch();
        quarterTriangle.setTile(2, Fabric.BROWN);
        quarterTriangle.setTile(3, Fabric.BROWN);
        quarterTriangle.setTile(5, Fabric.BROWN);
        quarterTriangle.setTile(7, Fabric.BROWN);
        
        assertEquals("Shape.QUARTERTRIANGLE should equal quarterTriangle",
                quarterTriangle, Shape.QUARTERTRIANGLE.getPatch(Fabric.BROWN));
    }
    
    /**
     * Test quarter square shape
     */
    @Test
    public void testGetQuarterSquare(){
        Patch qSquare = new Patch();
        for (int i=0; i<4; i++){
            qSquare.setTile(i, Fabric.ORANGE);
        }
        
        assertEquals("Shape.QUARTERSQUARE should equal qSquare", 
                qSquare, Shape.QUARTERSQUARE.getPatch(Fabric.ORANGE));
    }
    
    /**
     * This runs some of the hidden enum code so that Shape.java gets
     * full test coverage.
     */
    @Test
    public void enumFullCoverage(){
        Shape.valueOf("FULLSQUARE");
    }
    
}
