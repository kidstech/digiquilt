/*
 * Created by jbiatek on Sep 14, 2008
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.patchworkarea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import umm.digiquilt.animation.AnimationPanel;
import umm.digiquilt.animation.Sprite;

/**Panel that can do a rotation animation of an image around a point.
 * Used for eye candy in the Patch Work Area when a patch is rotated.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */

@SuppressWarnings("serial")
public class RotationPanel extends AnimationPanel implements PropertyChangeListener {
    /**
     * The number of steps to take when rotating.
     */
    private static final int TOTALSTEPS = 10;
    
    /**
     * The time to take while animating, in milliseconds.
     */
    private static final int TOTALTIME = 300;
    
    /**
     * The amount to rotate each image. Should probably stay at pi/2
     * radians (90 degrees).
     */
    private static final double QUARTERTURN = Math.PI *1/2;
    
    /**
     * Create a new RotationPanel. Starts out with setVisible false.
     */
    public RotationPanel(){
        this.setOpaque(false);
        this.setVisible(false);
        // This panel adds a MouseListener only to block out mouse events
        // while the animation is happening.
        this.addMouseListener(new MouseAdapter(){ /* do nothing */ });
        
        // We want to listen to ourself for property changes
        addPropertyChangeListener("animationstate", this);
        
    }

   
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue().equals("stopped")){
            clear();
            this.setVisible(false);
        }
    }
    
    /**
     * Rotate the given image clockwise around the given point. The point
     * should be relative to this panel. This will set this panel to be 
     * visible while the rotation goes.
     * 
     * @param img
     * @param point
     */
    public void rotateClockwise(Image img, Point point){
        rotateImage(img, point, QUARTERTURN);
    }
    
    
    /**
     * Rotate the given image counterclockwise around the given point.
     * The point should be relative to this panel. This will set this
     * panel to be visible while the rotation goes.
     * 
     * @param img
     * @param point
     */
    public void rotateCounterClockwise(Image img, Point point){
        rotateImage(img, point, -QUARTERTURN);
    }
    
    /**
     * Rotate the image around the given point to the specified angle.
     * 
     * @param img
     * @param point
     * @param angle
     */
    private void rotateImage(Image img, Point point, double angle){
        RotatingImage sprite = new RotatingImage(img, point, angle);
        addSprite(sprite);
        setVisible(true);
        animate(TOTALTIME, TOTALSTEPS);
    }

    /**
     * Sprite which rotates an image around a given point, and also paints
     * a grey rectangle over where the unrotated image would be. This is to
     * cover up 'the real thing' which is presumably underneath the animation.
     * 
     * @author Jason Biatek, last changed by $Author: lamberty $
     * on $Date: 2008/01/22 17:50:24 $
     * @version $Revision: 1.1 $
     *
     */
    private class RotatingImage implements Sprite {
        
        /**
         * The Image for this sprite to rotate.
         */
        private Image image;
        /**
         * The point to rotate it around
         */
        private Point center;
        /**
         * The amount of rotation to perform, in radians.
         */
        private double rotation;
        /**
         * The current percentage through the animation.
         */
        private float percent = 0;
        
        /**
         * Create a rotating image sprite. 
         * 
         * @param img
         * @param center
         * @param radians
         */
        public RotatingImage(Image img, Point center, double radians){
            image = img;
            rotation = radians;
            this.center = center;
        }
        

        /* (non-Javadoc)
         * @see umm.digiquilt.animation.Sprite#paintSprite(java.awt.Graphics)
         */
        public void paintSprite(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            // Get the coordinates to paint the image so that its center is
            // right over our rotation point
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int x = center.x - width / 2;
            int y = center.y - height / 2;
            // Paint a gray rectangle first, to cover up the real patch that's
            // below us, before we rotate.
            g2.setColor(Color.GRAY);
            g2.fillRect(x, y, width, height);
            // Now rotate the Graphics2D object around the center point.
            double theta = rotation * percent;
            g2.rotate(theta, center.x, center.y);
            g2.drawImage(image, x, y, null);
        }

        /* (non-Javadoc)
         * @see umm.digiquilt.animation.Sprite#setPercent(float)
         */
        public void setPercent(float percent) {
            this.percent = percent;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {
            // Do nothing
        }
        
    }
    
}
