/*
 * Created by jbiatek on Apr 11, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.glasspane;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A glasspane which locks out mouse access to anything not on its
 * whitelist. 
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class LockingGlassPane extends JPanel implements AWTEventListener {

    /**
     * The list of components that shouldn't be blocked.
     */
    List<Component> unlocked = new ArrayList<Component>();

    /**
     * Create a new LockingGlassPane.
     */
    public LockingGlassPane(){
        setOpaque(false);
        // Add self as AWTEventListener, so we can get events without blocking
        // everything above us, and so we can consume() them if we want to.
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.MOUSE_MOTION_EVENT_MASK | 
                AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void paintComponent(Graphics graphics){
        Graphics2D g2 = (Graphics2D) graphics;
        Area paintOver = new Area(new Rectangle(this.getSize()));
        for (Component comp : unlocked){
            Point location = SwingUtilities.convertPoint(comp.getParent(), 
                    comp.getLocation(), this);
            Rectangle hole = new Rectangle(location, comp.getSize());
            paintOver.subtract(new Area(hole));
        }

        g2.setColor(new Color(0,0,0,127));
        g2.fill(paintOver);

    }

    /**
     * Add a component to the whitelist. Even when this pane is on,
     * this component will receive mouse events as normal.
     * 
     * @param c
     */
    public void unlockComponent(Component c){
        if (!unlocked.contains(c)){
            unlocked.add(c);
        }
    }


    public void eventDispatched(AWTEvent event) {
        /*
         * Deals with the mouse events that will be received, since the glasspane
         * is an AWTEventListener (see the constructor above). It gets the location
         * of the mouse, and also updates hoveringOver.
         */
        if (this.isVisible()){
            // Only block events if we're visible
            if (event instanceof MouseEvent){
                MouseEvent mouse = (MouseEvent) event;
                Component hoveringOver = (Component) event.getSource();

                boolean outOfBounds = true;
                for (Component free : unlocked){
                    if (free.contains(SwingUtilities.convertPoint(
                            hoveringOver, mouse.getPoint(), free))){
                        outOfBounds = false;
                    }
                }

                if (outOfBounds){
                    mouse.consume();
                }
            }
        }
    }

}
