package umm.digiquilt.view.glasspane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

/**
 * Interface for components that should be highlighted by the GlassPane. If the
 * mouse hovers over a component inplementing this interface, the GlassPane will
 * draw a highlight over it, based on the coordinates returned by these two methods.
 * 
 * @author jbiatek
 *
 */
public interface HandPaneHighlighted {

    /**
     * @return the size of this component.
     * @see java.awt.Component#getSize()
     */
    Dimension getSize();

    /**
     * Necessary for the GlassPane to be able to convert getLocation() to be
     * relative to it. 
     * @return this component's parent.
     * @see java.awt.Component#getLocation()
     */
    Component getParent();

    /**
     * Get the point of the top left corner of this component, relative to 
     * its parent, same as Component's getLocation.
     * @return location relative to parent.
     * @see java.awt.Component#getLocation()
     */
    Point getLocation();
    
    /**
     * Return whether or not patches should snap when they hover over this
     * component. Patch viewers generally should, anything else probably
     * shouldn't. 
     * 
     * @return true for snapping, false otherwise
     */
    boolean isSnappy();
    
    /**Return whether or not this highlightable object should actually be
     * highlighted. A highlightable component can decide not to be
     * highlighted by returning false
     * @return true for a highlight, false otherwise
     */
    boolean isHighlighted();
}
