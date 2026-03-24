/*
 * Created by mitchella on Mar 21, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;

import java.awt.Color;

/**
 * An enum representing all the possible fabric colors.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         mitchella $ on $Date: 2009-06-15 20:25:34 $
 * @version $Revision: 1.2 $
 */
public enum Fabric {

    /**
     * Gray fabric.
     */
    GRAY (Color.GRAY, "Gray"), 
    /**
     * Red violet fabric.
     */
    REDVIOLET (new Color(205, 0, 103), "Red Violet"), 
    /**
     * Red fabric.
     */
    RED (new Color(234, 43, 30), "Red"), 
    /**
     * Orange fabric.
     */
    ORANGE (new Color(255, 169, 33), "Orange"), 
    /**
     * Yellow fabric.
     */
    YELLOW (Color.YELLOW, "Yellow"), 
    /**
     * Green fabric.
     */
    GREEN (new Color(77, 209, 0), "Green"), 
    /**
     * Dark green fabric.
     */
    DARKGREEN (new Color(52, 102, 51), "Dark Green"), 
    /**
     * Blue fabric.
     */
    BLUE (new Color(0, 57, 228), "Blue"), 
    /**
     * Indigo fabric.
     */
    INDIGO (new Color(102, 0, 205), "Indigo"),
    /**
     * Violet fabric.
     */
    VIOLET (new Color(153, 0, 153), "Violet"), 
    /**
     * Pink fabric.
     */
    PINK (new Color(255, 124, 161), "Pink"), 
    /**
     * White fabric.
     */
    WHITE (Color.WHITE, "White"), 
    /**
     * Black fabric.
     */
    BLACK (Color.BLACK, "Black"), 
    /**
     * Brown fabric.
     */
    BROWN (new Color(153, 52, 0), "Brown"), 
    /**
     * Transparent fabric.
     */
    TRANSPARENT (new Color(128, 128, 128, 0), "Transparent");

    /**
     * The color of this Fabric.
     */
    private final Color myColor;
    
    /**
     * The name of this fabric
     */
    private final String myName;
    
    /**Create a fabric with the given color and name
     * 
     * @param color
     * @param name
     */
    Fabric(Color color, String name){
        myColor = color;
        myName = name;
    }
    
    /**
     * Returns the java.awt.Color that is associated with each Fabric
     * 
     * @return returnColor The returned color
     */
    public Color getColor() {
        return myColor;
    }
    
    /**
     * @return the name of this Fabric
     */
    public String getName(){
        return myName;
    }

    /**
     * Gives the best color for text on this fabric.
     * 
     * @param fabric the background fabric
     * @return black or white, whichever is better
     */
    public Color getGoodTextColor() {
        int gray = (myColor.getRed() * 299/1000
                   +myColor.getGreen() * 587/1000
                   +myColor.getBlue() * 114/1000);
        
        return gray < 127 ? Color.WHITE : Color.BLACK;
    }
    
    /**
     * @return a semi-transparent shadow color, which can be used when
     * you want to show transparency without being completely transparent
     */
    public static Color getShadowColor(){
        return new Color(128,128,128,128);
    }
}
