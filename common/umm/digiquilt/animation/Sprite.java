/*
 * Created by jbiatek on Jan 10, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.animation;

import java.awt.Graphics;
import java.beans.PropertyChangeListener;

/**
 * A Sprite is put into an AnimationPanel, and is given information
 * about the animation as it is in progress. PropertyChangeEvents from
 * the AnimationPanel and percent updates let the Sprite know the status
 * of the animation, and it is given a Graphics object to paint on the
 * AnimationPanel.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public interface Sprite extends PropertyChangeListener {

    /**
     * Update this Sprite with the progress of the animation, from 0 to 1.
     * 
     * @param percent
     */
    void setPercent(float percent);

    /**
     * Called from the AnimationPanel, asking the Sprite to paint itself
     * at this point in the animation.
     * 
     * @param g
     */
    void paintSprite(Graphics g);

}
