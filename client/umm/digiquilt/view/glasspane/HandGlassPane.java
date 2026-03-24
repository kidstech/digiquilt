package umm.digiquilt.view.glasspane;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import umm.digiquilt.view.glasspane.HandPaneHighlighted;

/**
 * The GlassPane sits on top of the main frame, and is transparent. When it is 
 * turned on, it will paint the patch currently in the hand, as well as highlights
 * to show where it will land when dropped.
 * 
 * @author deragonmr
 * 
 */
@SuppressWarnings("serial")
public class HandGlassPane extends JPanel implements AWTEventListener {

    /**
     * paints the Highlights with an unused patch color
     * to prevent confusion.
     */
    private static final Color HIGHLIGHT = Color.CYAN;

    /** 
     * the Alpha level (transpancey of highliter)
     * 1 is full displayed, and 0 is completely transparent.
     */
    private static final float ALPHA = .33f;

    /**
     * The Size of the halo to display around a patch (in pixels)
     */
    private static final int HALOSIZE = 15;

    /**
     * The image of what shape/patch the hand is holding.
     */
    Image hand;

    /**
     * mousePoint is the x/y coordinates of the mouse on the
     * frame.
     */
    Point mousePoint;
    /**
     * Used to center hand to the cursor
     */
    int handX;
    /**
     * Used to center hand to the cursor
     */
    int handY;
    
    /**
     * This switch lets us not snap immediately when a Patch is picked up.
     * Kids were having a problem where they picked up a Patch and it immediately
     * snapped back into place, thinking that they hadn't picked it up. 
     * 
     * When the glass pane is first turned on, it will set this to true and not
     * snap even if the component that we're hovering over requests it. After
     * the mouse moves to a new component, it will put this back to false and
     * normal behavior will resume.
     */
    boolean avoidSnapping = false;
    
    /**
     * This gets set to true when we first hover over a component, so we
     * know when we're hovering over a new one later.
     */
    boolean seenComponent = false;

    /**
     * The mouse listener that the mouse was last hovering over. Gotten from
     * eventDispatched(). It should be reset with resetHighlight() when the 
     * pane gets turned back on, or else an image will appear from the last 
     * time the pane was on until the mouse moves again.
     */
    private Object hoveringOver;

    /**
     * The Glasspane constructor
     */
    public HandGlassPane() {
        setOpaque(false);
        // Add self as AWTEventListener, so we can get events without blocking
        // everything above us, and so we can consume() them if we want to.
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.MOUSE_MOTION_EVENT_MASK | 
                AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void paintComponent(Graphics graphic) {
        Graphics2D graphic2 = (Graphics2D) graphic;
        /* If the object being hovered over implements GlassPaneHighlighted,
         * then we want to draw a halo around it first.
         */
        if (hoveringOver instanceof HandPaneHighlighted){
            HandPaneHighlighted current = (HandPaneHighlighted) hoveringOver;
            // Get dimensions and location from the target
            Dimension size = current.getSize();
            Point location = SwingUtilities.convertPoint(
                    current.getParent(), 
                    current.getLocation(), 
                    this);

            // Paint the halo using info from current if it's enabled
            if (current.isHighlighted()){
                paintHalo(location, size, graphic2);
            }

            // Time to paint the hand
            if (current.isSnappy() && current.isHighlighted() && !avoidSnapping) {
                paintSnappedHand(location, graphic2);
            }
            else {
                paintUnsnappedHand(graphic2);
            }
        }
        // Default to just painting the hand, since whatever we're hovering over
        // doesn't want any highlighting.
        else {
            paintUnsnappedHand(graphic2);
        }
    }
    /**
     * Helper function to paint the halo over GlassPaneHighlighted objects
     * 
     * @param location - from getHighlightLocation()
     * @param size - from getHighlightSize()
     * @param graphic2
     */
    private void paintHalo(Point location, Dimension size, Graphics2D graphic2){
        // sets the transparency.
        graphic2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, ALPHA));
        // Sets the color to the one determined above
        graphic2.setColor(HIGHLIGHT);
        // Paint the big halo
        graphic2.fillRect(
                location.x - HALOSIZE,
                location.y - HALOSIZE,
                size.width + (2 * HALOSIZE),
                size.height+ (2 * HALOSIZE));
        // Paint the smaller, darker area
        graphic2.setColor(Color.black);
        graphic2.fillRect(location.x, location.y, size.width, size.height);
    }

    /**
     * Paint the patch currently in the hand, snapping it to the proper location.
     * This helps see exactly where it will land when dropped. 
     * @param location
     * @param graphic2
     */
    private void paintSnappedHand(Point location, Graphics2D graphic2){
        // Get mouse point relative to the component we're snapping to, instead
        // of relative to the whole frame
        int relativeX = mousePoint.x - location.x;
        int relativeY = mousePoint.y - location.y;
        int handHeight = hand.getHeight(this);
        int handWidth = hand.getWidth(this);
        // No, this isn't redundant...division with ints, remember?
        int bumpX = (relativeX / handWidth) * handWidth;
        int bumpY = (relativeY / handHeight) * handHeight;
        graphic2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, 1.0f));
        graphic2.drawImage(hand, location.x + bumpX, location.y + bumpY,
                null);
    }
    /**
     * Paint the patch currently in the hand, centered on the cursor instead
     * of snapping. This also paints a shadow of the patch.
     * 
     * @param graphic2
     */
    private void paintUnsnappedHand(Graphics2D graphic2){
//      Sets the Transparency level to display the "shadowed"
        // version of what is held.
        graphic2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, .33f));
        graphic2.drawImage(hand,mousePoint.x-handX+5,mousePoint.y-handY+5,null);

        // Reset the Transpency level and paints the image of whats held
        // in the hand, the handX and handY center the mouse in the image.
        graphic2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, 1.0f));
        graphic2.drawImage(hand, mousePoint.x - handX, mousePoint.y - handY,
                null);
    }

    public void eventDispatched(AWTEvent event) {
        /*
         * Deals with the mouse events that will be received, since the glasspane
         * is an AWTEventListener (see the constructor above). It gets the location
         * of the mouse, and also updates hoveringOver.
         */
        if (this.isVisible()){
            // If the pane isn't visible there's no point in doing a repaint
            if (event instanceof MouseEvent){
                MouseEvent mouse = (MouseEvent) event;
                
                if (seenComponent && hoveringOver != event.getSource()){
                    avoidSnapping = false;
                }
                
                hoveringOver = event.getSource();
                seenComponent = true;
                
                
                if (!(hoveringOver instanceof HandPaneHighlighted)){
                    // Intercept all events for non-Highlighted components
                    // if the Glasspane is on.
                    mouse.consume();
                }
                
                // The event's coordinates are relative to the component 
                // receiving the click. We want it relative to us.
                MouseEvent converted = SwingUtilities.convertMouseEvent(
                        mouse.getComponent(), mouse, this); 
                Point newPoint = converted.getPoint(); 
                // Update the fields and repaint.
                setMousePoint(newPoint);
            }
        }
    }

    /**
     * Sets the hand, the visual representation of what is currently being held.
     
     * @param handImage The image to be displayed in the hand.
     */
    public void setHand(Image handImage){
        hand = handImage;
        /*
         * handX and handY are the offsets that centers the mouse in the 
         * middle of the image.
         */
        handX = (hand.getWidth(this) / 2);
        handY = (hand.getHeight(this) / 2);
    }

    /**
     * Set the current mouse point to the given Point, and then do a repaint.
     * 
     * @param point The point where the mouse is at.
     */
    private void setMousePoint(Point point){
        if (point != null){
            mousePoint = point;
            repaint();
        }
    }
    /**
     * Resets the highlight to null. This should be used when the GlassPane is turned 
     * on again, otherwise the highlight from before will still be there until the 
     * mouse moves.
     */
    public void resetHighlight(){
        hoveringOver = new Object();
    }

    @Override
    public void setVisible(boolean aFlag){
        super.setVisible(aFlag);
        if (aFlag) {
            // Pane has been turned on
            setMousePoint(this.getRootPane().getMousePosition());
            resetHighlight();
            avoidSnapping = true;
            seenComponent = false;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            // Pane has been turned off
            setCursor(Cursor.getDefaultCursor());
        }
    }


}