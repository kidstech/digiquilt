/*
 * Created by mitchella on Mar 21, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;


/**
 * A patch is one "square" of the main block. It contains 16 tiles.
 * (see Shapes.java to see how they are laid out).
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         mitchella $ on $Date: 2009-06-21 23:41:03 $
 * @version $Revision: 1.3 $
 */
public class Patch {

    /**
     * The maximum number of tiles possible in any Patch.
     */
    public static final int MAXTILES = 16;
    /**
     * Array of tiles, the actual backend implementation of a Patch.
     */
    private Fabric[] tileArray;



    /**
     * Default constructor for a patch.
     */
    public Patch() {
        tileArray = new Fabric[MAXTILES];
        for (int i = 0; i < MAXTILES; i++) {
            tileArray[i] = Fabric.TRANSPARENT;
        }
    }

    /**
     * (Re)Create a patch with certain Fabrics.
     * 
     * @param fabrics an array of fabric names
     */
    public Patch(Fabric[] fabrics){
        if (fabrics.length != MAXTILES){
            throw new IllegalArgumentException();
        }
        tileArray = new Fabric[MAXTILES];
        for (int i = 0; i < MAXTILES; i++){
            tileArray[i] = fabrics[i];
        }
    }

    /**
     * Boolean method to test equality of Patches
     * @return result True if the patches are equal.
     */
    @Override
    public boolean equals(final Object object) {
        boolean result = true;
        if (object instanceof Patch) {
            final Patch patch = (Patch) object;
            for (int i = 0; i < MAXTILES; i++) {
                if (!patch.tileArray[i].equals(this.tileArray[i])) {
                    result = false;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Creating hashcode since we are overriding equals()
     */
    @Override
    public int hashCode() {
        int hash = 17;
        for (int i = 0; i < MAXTILES; i++) {
            hash *= (tileArray[i].hashCode() + 37);
        }
        return hash;
    }


    /**Assuming this patch has only one color (other than transparent),
     * returns that one color. If it has more than one color, or if its
     * only color is transparent, returns Fabric.TRANSPARENT.
     * 
     * @return the one Fabric covering this patch, or if there's more than
     * one, Fabric.TRANSPARENT.
     * 
     */
    public Fabric getSolidColor(){
        Fabric firstFoundFabric = Fabric.TRANSPARENT;
        for (Fabric current : tileArray){
            if (current.equals(Fabric.TRANSPARENT)){
                // Skip this tile, it's empty
                continue;
            }
            if (!current.equals(firstFoundFabric)){
                // Uh oh, this tile is different from the first one we found...
                if (firstFoundFabric.equals(Fabric.TRANSPARENT)){
                    // Oh, that's because we haven't found one yet. This is the
                    // first one.
                    firstFoundFabric = current;
                } else {
                    // Nope, two colors have been found.
                    return Fabric.TRANSPARENT;
                }
            }
        }
        return firstFoundFabric;
    }

    /**
     * Create a clone of this Patch, with the tiles of this patch moved to
     * be as close to the top left corner as possible without changing the
     * actual appearance of the shape. This is so that small shapes (for 
     * example, the half-rectangle and quarter-square in Shapes.java) can
     * be picked up, put down in a different quadrant of a Patch, and picked 
     * up again, all while behaving the same way. 
     * 
     * @return a shifted copy of this Patch
     */
    public Patch getSmallPatch(){
        Patch smallPatch = new Patch();
        boolean zeroIsEmpty = quadrantIsEmpty(0);
        boolean oneIsEmpty = quadrantIsEmpty(1);
        boolean twoIsEmpty = quadrantIsEmpty(2);
        if (zeroIsEmpty && oneIsEmpty && twoIsEmpty){
            smallPatch.mergeQuadrants(this, 3, 0);
        } else if (zeroIsEmpty && oneIsEmpty){
            smallPatch.mergeQuadrants(this, 2, 0);
            smallPatch.mergeQuadrants(this, 3, 1);
        } else if (zeroIsEmpty && twoIsEmpty){
            smallPatch.mergeQuadrants(this, 1, 0);
            smallPatch.mergeQuadrants(this, 3, 2);
        } else {
            return getPatchClone();
        }
        
        return smallPatch;
    }

    /**
     * Get the width of this Patch, in quadrants. A Patch with width 0 is
     * completely empty, a Patch with width 1 is a small shape with either the
     * left or right half completely empty, and a Patch with width 2 is a 
     * full sized patch.
     * 
     * @return the width, in quadrants.
     */
    public int getWidth(){
        int width = 2;
        if (quadrantIsEmpty(0) && quadrantIsEmpty(2)){
            width--;
        }
        if (quadrantIsEmpty(1) && quadrantIsEmpty(3)){
            width--;
        }
        return width;
    }

    /**
     * Get the height of this Patch, in quadrants. A Patch with height 0 is 
     * completely empty, a Patch with height 1 is a small shape with either
     * the top or bottom half completely empty, and a Patch with height 2 is
     * a full sized patch.
     * 
     * @return the height, in quadrants.
     */
    public int getHeight(){
        int height = 2;
        if (quadrantIsEmpty(0) && quadrantIsEmpty(1)){
            height--;
        }
        if (quadrantIsEmpty(2) && quadrantIsEmpty(3)){
            height--;
        }
        return height;
    }
    /**
     * See if a quadrant is empty or not. Quadrants are numbered 0 through 3.
     * @param quadrant
     * @return true if a quadrant is empty, false otherwise
     */
    private boolean quadrantIsEmpty(int quadrant){
        int start = quadrant * 4;
        for (int i=start; i < start + 4; i++){
            if (tileArray[i] != Fabric.TRANSPARENT){
                return false;
            }
        }
        return true;
    }


    /**
     * Method for rotating a patch clockwise
     */
    public void rotateCW() {
        Fabric[] tempTileArray = new Fabric[MAXTILES];
        tempTileArray[0] = tileArray[9];
        tempTileArray[1] = tileArray[11];
        tempTileArray[2] = tileArray[8];
        tempTileArray[3] = tileArray[10];
        tempTileArray[4] = tileArray[1];
        tempTileArray[5] = tileArray[3];
        tempTileArray[6] = tileArray[0];
        tempTileArray[7] = tileArray[2];
        tempTileArray[8] = tileArray[13];
        tempTileArray[9] = tileArray[15];
        tempTileArray[10] = tileArray[12];
        tempTileArray[11] = tileArray[14];
        tempTileArray[12] = tileArray[5];
        tempTileArray[13] = tileArray[7];
        tempTileArray[14] = tileArray[4];
        tempTileArray[15] = tileArray[6];
        tileArray = tempTileArray;
    }

    /**
     * Method for rotating a patch counter-clockwise
     */
    public void rotateCCW() {
        Fabric[] tempTileArray = new Fabric[MAXTILES];
        tempTileArray[0] = tileArray[6];
        tempTileArray[1] = tileArray[4];
        tempTileArray[2] = tileArray[7];
        tempTileArray[3] = tileArray[5];
        tempTileArray[4] = tileArray[14];
        tempTileArray[5] = tileArray[12];
        tempTileArray[6] = tileArray[15];
        tempTileArray[7] = tileArray[13];
        tempTileArray[8] = tileArray[2];
        tempTileArray[9] = tileArray[0];
        tempTileArray[10] = tileArray[3];
        tempTileArray[11] = tileArray[1];
        tempTileArray[12] = tileArray[10];
        tempTileArray[13] = tileArray[8];
        tempTileArray[14] = tileArray[11];
        tempTileArray[15] = tileArray[9];
        tileArray = tempTileArray;
    }

    /**
     * Returns a string array of the fabrics that make up this Patch. Can be used
     * to store a Patch in text form, and reconstructed by passing the same
     * String[] into the constructor.
     * 
     * @return a string array of this Patch's fabrics
     */
    public String[] getFabricList(){
        String[] list = new String[16];
        for (int i=0; i < MAXTILES; i++){
            list[i] = tileArray[i].toString();
        }
        return list;
    }

    /**
     * Getter for a specific tile of the patch.
     * 
     * @param index the tile number to get
     * @return the FabricTile at that location
     */
    public Fabric getTile(final int index) {
        if (index < MAXTILES && index >= 0) {
            return tileArray[index];
        } 
        throw new IndexOutOfBoundsException();
    }

    /**
     * Method to set the tiles in a Patch to a FabricTile. This should only
     * be used internally or for testing purposes, the "right" thing to do is
     * to use the constructor. 
     * 
     * @param index numbered location of FabricTile in Patch.
     * @param tile input tile which will be set to the index location in Patch.
     */
    protected void setTile(final int index, final Fabric tile) {
        if (index < MAXTILES && index >= 0) {
            tileArray[index] = tile;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Method to place a patch on top of this one, and replace the tiles of this
     * patch with all non-transparent tiles from the new patch
     * 
     * @param patch The patch to be merged on top of this one
     * @param xPercent Horizontal location of patch drop, from 0.0 to 1.0 (left
     * and right edge, respectively)
     * @param yPercent Vertical location 0.0 (top) to 1.0 (bottom)
     * 
     */
    public void mergePatch(final Patch patch, double xPercent, double yPercent) {
        Patch topPatch = patch.getSmallPatch();
        if (topPatch.isSmallSquare()){
            int dropQuadrant = 0;
            if (xPercent >= .5) {
                dropQuadrant = 1;
            }
            if (yPercent >= .5) {
                dropQuadrant = dropQuadrant + 2;
            }
            mergeQuadrants(topPatch, 0, dropQuadrant);
            return;
        } else if ( topPatch.isWideRectangle() && yPercent >= .5){
            mergeQuadrants(topPatch, 0, 2);
            mergeQuadrants(topPatch, 1, 3);
            return;
        } else if ( topPatch.isTallRectangle() && xPercent >= .5){
            mergeQuadrants(topPatch, 0, 1);
            mergeQuadrants(topPatch, 2, 3);
            return;
        }
        mergeFullPatch(topPatch);
    }
    
    /**
     * @return true if this Patch takes up only one quadrant
     */
    private boolean isSmallSquare(){
        return getHeight() == 1 && getWidth() == 1;
    }
    
    /**
     * @return true if this Patch is one quadrant high and two quadrants wide
     */
    private boolean isWideRectangle(){
        return getHeight() == 1 && getWidth() == 2;
    }
    
    /**
     * @return true if this Patch is two quadrants high and one quadrant wide
     */
    private boolean isTallRectangle(){
        return getHeight() == 2 && getWidth() == 1; 
    }
    
    /**
     * Merge one quadrant from the given Patch on to this Patch. 
     * 
     * @param topPatch The Patch to merge
     * @param quadrantTop The quadrant on the top Patch to copy on to this one
     * @param quadrantBottom The quadrant on this Patch to copy on to.
     */
    private void mergeQuadrants(Patch topPatch, int quadrantTop, int quadrantBottom){
        for (int i=0; i<4; i++){
            int topIndex = quadrantTop*4 + i;
            int bottomIndex = quadrantBottom*4 + i;
            Fabric topFabric = topPatch.getTile(topIndex);
            if (topFabric != Fabric.TRANSPARENT) {
                setTile(bottomIndex, topFabric);
            }
            
        }
    }
    
    /**
     * Merge the given Patch onto this one.
     * 
     * @param topPatch
     */
    private void mergeFullPatch(Patch topPatch){
        for (int i=0; i<MAXTILES; i++){
            if (topPatch.getTile(i) != Fabric.TRANSPARENT){
                setTile(i, topPatch.getTile(i));
            }
        }
    }

    /**
     * Returns a clone of the patch
     * @return clone a clone of Patch
     */
    public Patch getPatchClone() {
        final Patch clone = new Patch();
        for (int i = 0; i < tileArray.length; i++) {
            clone.setTile(i, getTile(i));
        }
        return clone;
    }

    /**
     * Replace the first given fabric with the second in this Patch.
     * 
     * @param replaceThis the fabric to be replaced
     * @param withThis the fabric used to replace
     */
    public void replaceFabrics(final Fabric replaceThis, final Fabric withThis) {
        for (int i = 0; i < MAXTILES; i++) {
            if (getTile(i).equals(replaceThis)) {
                setTile(i, withThis);
            }
        }
    }

    /**
     * Swap the two given fabrics in this Patch.
     * 
     * @param one the fabric to replace two and to be replaced by two
     * @param two the fabric to replace one and to be replaced by one
     */
    public void swapFabrics(final Fabric one, final Fabric two) {
        for (int i = 0; i < MAXTILES; i++) {
            if (getTile(i).equals(one)) {
                setTile(i, two);
            } else if (getTile(i).equals(two)) {
                setTile(i, one);
            }
        }
    }

    /**
     * Method to get the Fractional coverage of a fabric for this Patch.
     * @param fabric
     * @return Fractional coverage of the fabric.
     */

    public int getPatchCoverage(Fabric fabric){
        int tempInt = 0;
        for(Fabric tile: tileArray){
            if (tile.equals(fabric)){
                tempInt++;
            }
        }
        return tempInt;
    }

}
