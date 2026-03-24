/*
 * Created by jbiatek on Jan 10, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.animation;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * A Panel which performs animations. Sprite objects are added to the
 * panel using addSprite(), and while the animation occurs they are updated
 * with the percent complete and given a chance to paint themselves.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class AnimationPanel extends JPanel implements ActionListener {
    
    /**
     * Animation state property name.
     */
    public static final String ANI_STATE_PROP = "animationstate";
    
    /**
     * The current animation state of this panel. Can be "stopped" or
     * "forward".
     */
    private String state = "stopped";
    
    /**
     * The percentage through the current animation.
     */
    private float percent;
    
    /**
     * List of Sprites that have been added.
     */
    private List<Sprite> sprites = new ArrayList<Sprite>();
    
    /**
     * The timer that tells how much of the animation has passed and pokes
     * us to refresh the image
     */
    private PercentageTimer timer;

    /**
     * Add a Sprite to this AnimationPanel. It will be updated with
     * the progress of the animation and given a Graphics object
     * to paint with while the animation happens.
     * 
     * @param sprite
     */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
        addPropertyChangeListener(ANI_STATE_PROP, sprite);
    }

    /**
     * Tell this AnimationPanel to start animating. 
     * 
     * @param duration The approximate amount of time to animate, in 
     * milliseconds
     * @param frames The number of frames to render in that time.
     */
    public void animate(int duration, int frames) {
        animateWithState(duration, frames, "forward");
    }
    
    /**
     * Run the animation with percentages going from 100% to 0%.
     * 
     * @param duration in milliseconds (approximate)
     * @param frames the number of frames to show during the animation
     */
    public void animateReverse(int duration, int frames){
        animateWithState(duration, frames, "reverse");
    }
    
    /**
     * Start animating, and fire a property change to the given state.
     * 
     * @param duration
     * @param frames
     * @param newState
     */
    private void animateWithState(int duration, int frames, String newState){
        timer = new PercentageTimer(duration, frames, this);
        String oldState = state;
        state = newState;
        firePropertyChange(ANI_STATE_PROP, oldState, newState);
        
        timer.start();

    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        percent = Float.parseFloat(e.getActionCommand());
        if (state.equals("reverse")){
            percent = 1f - percent;
        }
        
        
        for (Sprite sprite : sprites){
            sprite.setPercent(percent);
        }
        
        repaint();
        
        if (isFinished()){
            String oldState = state;
            state = "stopped";
            firePropertyChange(ANI_STATE_PROP, oldState, state);
        }
    }
    
    /**
     * @return whether or not the animation should be done.
     */
    private boolean isFinished(){
        if (percent == 1 && state.equals("forward")){
            return true;
        } else if (percent == 0 && state.equals("reverse")){
            return true;
        }
        
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (Sprite sprite : sprites){
            sprite.paintSprite(g);
        }
    }

    /**
     * Remove all Sprites from this panel.
     */
    public void clear() {
        sprites.clear();
    }
    

}
