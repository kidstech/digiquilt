/*
 * Created by jbiatek on Apr 26, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import javax.swing.Icon;

/**
 * A Icon drawn dynamically which points left and/or right, with a 
 * gradient of colors between the two sides.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */

public class TwoColorArrow implements Icon {
    
    /**
     * Width of the icon.
     */
    int width;
    /**
     * Height of the icon
     */
    int height;
    /**
     * The paint to use to fill the arrow shape.
     */
    GradientPaint myPaint;
    /**
     * The shape to be filled
     */
    Polygon arrowShape;
    
    /**
     * Create a new TwoColorArrow, with the specified height and pointing
     * left and/or right as specified. The colors of the left and right sides
     * default to black.
     * 
     * @param width
     * @param height
     * @param pointLeft
     * @param pointRight
     */
    public TwoColorArrow(int width, int height,
            boolean pointLeft, boolean pointRight){
        this.width = width;
        this.height = height;
        
        setColors(Color.black, Color.black);
        
        arrowShape = new Polygon();
        arrowShape.addPoint(height/2, height/4);
        arrowShape.addPoint(width-height/2, height/4);
        if (pointRight){
            // Draw the right arrow head
            arrowShape.addPoint(width-height/2, 0);
            arrowShape.addPoint(width, height/2);
            arrowShape.addPoint(width-height/2, height);
        } else {
            // Just cap it off with a box
            arrowShape.addPoint(width, height/4);
            arrowShape.addPoint(width, 3*height/4);
        }
        arrowShape.addPoint(width-height/2, 3*height/4);
        arrowShape.addPoint(height/2, 3*height/4);
        if (pointLeft){
            // Draw the left arrow head
            arrowShape.addPoint(height/2, height);
            arrowShape.addPoint(0, height/2);
            arrowShape.addPoint(height/2, 0);
        } else {
            // Just cap it off with a box
            arrowShape.addPoint(0, 3*height/4);
            arrowShape.addPoint(0, height/4);
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight() {
        return height;
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth() {
        return width;
    }
    
    /**
     * Set two new colors to be used for the left and right sides
     * of the arrow.
     * 
     * @param left 
     * @param right
     */
    public final void setColors(Color left, Color right){
        myPaint = new GradientPaint(new Point(height/2, height/2), left,
                new Point(width-height/2, height/2), right);
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);
        g2.setPaint(myPaint);
        g2.fill(arrowShape);
        g2.setColor(Color.BLACK);
        g2.draw(arrowShape);
    }

}
