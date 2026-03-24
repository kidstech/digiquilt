package umm.digiquilt.view.blockAnimation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;

import umm.digiquilt.animation.Sprite;
import umm.digiquilt.view.Tile;

/**
 * A class to calculate where its tile should be placed, and how
 * much it should be rotated, based on where the counter is. Also
 * stores the proper color for that tile.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-03 21:26:18 $
 * @version $Revision: 1.2 $
 *
 */
public class MovingTile implements Sprite {

    /**
     * The starting tile.
     */
    private Tile start;
    
    /**
     * The ending tile.
     */
    private Tile end;
    
    /**
     * The size to make each Patch.
     */
    private int patchSize;
    
    /**
     * The current percentage through the animation.
     */
    private float percent = 0;
    
    
    /**
     * X coordinate of start tile
     */
    private int startX;
    /**
     * Y coordinate of start tile
     */
    private int startY;
    /**
     * X coordinate of end tile
     */
    private int endX;
    /**
     * Y coordinate of end tile
     */
    private int endY;
    /**
     * Rotation of start tile
     */
    private double startTheta;
    /**
     * Rotation of end tile
     */
    private double endTheta;
    
    
    /**Create a MovingTile, which transitions between the two given Tiles
     * during animation. These tiles should have the same patchSize, or
     * things won't really work. They can, however, be from differently-sized
     * Blocks.
     * @param start The starting Tile.
     * @param end The ending Tile.
     * @param patchSize The size of a Patch.
     * 
     */
    public MovingTile(Tile start, Tile end, int patchSize) {
        this.start = start;
        this.end = end;
        this.patchSize = patchSize;
        
        
        // Save these calculations so we don't have to do them each time:
        startX = start.getX(patchSize);
        startY = start.getY(patchSize);
        endX = end.getX(patchSize);
        endY = end.getY(patchSize);
        startTheta = start.getRotation();
        endTheta = end.getRotation();
        
    }
    

    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.animation.Sprite#paintSprite(java.awt.Graphics)
     */
    public void paintSprite(Graphics g) {
        if (getColor().getAlpha() != 0){
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform t = AffineTransform.getTranslateInstance(getX(), getY());
            t.rotate(getTheta());

            Shape tileShape = t.createTransformedShape(Tile.getTileShape(patchSize));

            g2.setColor(getColor());
            g2.fill(tileShape);
        }
    }

    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.animation.Sprite#setPercent(float)
     */
    public void setPercent(float percent) {
        this.percent = percent;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // Do nothing, we don't care about changes yet
    }


    /**Get the proper X coordinate for this counter time.
     * @return the X coordinate
     */
    protected int getX(){
        return getProgress(startX, endX);
    }
    
    /**Get the proper Y coordinate for this counter time.
     * @return the Y coordinate
     */
    protected int getY(){
        return getProgress(startY, endY);
    }
    
    /**Get the proper rotation for this counter time.
     * @return the rotation in radians
     */
    protected double getTheta(){
        return getProgress(startTheta, endTheta);
    }

    /**
     * Get the correct color for this tile at counter time.
     * 
     * @param percent
     * @return a weighted blend of the two colors based on counter.
     */
    protected Color getColor(){
        int startRed = start.getColor().getRed();
        int startGreen = start.getColor().getGreen();
        int startBlue = start.getColor().getBlue();
        int startAlpha = start.getColor().getAlpha();

        int endRed = end.getColor().getRed();
        int endGreen = end.getColor().getGreen();
        int endBlue = end.getColor().getBlue();
        int endAlpha = end.getColor().getAlpha();

        int currentRed = getProgress(startRed, endRed);
        int currentGreen = getProgress(startGreen, endGreen);
        int currentBlue = getProgress(startBlue, endBlue);
        int currentAlpha = getProgress(startAlpha, endAlpha);

        
        if (currentAlpha == 255) {
            return new Color(currentRed, currentGreen, currentBlue);
        }
        return new Color(currentRed, currentGreen, currentBlue, currentAlpha);

    }
    
    /**
     * @param startValue
     * @param endValue
     * @return the value between startValue and endValue that should be used,
     * based on the current percent.
     */
    private int getProgress(int startValue, int endValue){
        return (int) (startValue + (endValue - startValue)*percent);
    }
    
    /**
     * @param startValue
     * @param endValue
     * @return the value between startValue and endValue that should be used,
     * based on the current percent.
     */
    private double getProgress(double startValue, double endValue){
        return startValue + (endValue - startValue)*percent;

    }
    
}