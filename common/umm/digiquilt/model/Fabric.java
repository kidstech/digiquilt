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
     * Dark pink fabric.
     */
    REDVIOLET (new Color(205, 80, 120), "Dark Pink"), 
    /**
     * Red fabric.
     */
    RED (new Color(180, 65, 80), "Red"), 
    /**
     * Orange fabric.
     */
    ORANGE (new Color(230, 150, 100), "Orange"), 
    /**
     * Yellow fabric.
     */
    YELLOW (new Color(240, 215, 50), "Yellow"), 
    /**
     * Green fabric.
     */
    GREEN (new Color(160, 187, 130), "Light Green"), 
    /**
     * Dark green fabric.
     */
    DARKGREEN (new Color(79, 140, 105), "Green"), 
    /**
     * Blue fabric.
     */
    BLUE (new Color(60, 100, 190), "Blue"), 
    /**
     * Indigo fabric. 131, 196, 242
     */
    INDIGO (new Color(131, 175, 208), "Light Blue"),
    /**
     * Violet fabric.
     */
    VIOLET (new Color(117,88, 154), "Violet"), 
    /**
     * Pink fabric.
     */
    PINK (new Color(220, 150, 160), "Pink"), 
    /**
     * White fabric.
     */
    WHITE (new Color(246, 236, 235), "White"), 
    /**
     * Black fabric.
     */
    BLACK (new Color(65, 63, 68), "Black"), 
    /**
     * Brown fabric.
     */
    BROWN (new Color(120, 90, 70), "Brown"), 
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
