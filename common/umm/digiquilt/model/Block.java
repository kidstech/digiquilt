/*
 * Created by mitchella on Mar 21, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The block is the main work area for the program. This is the model representation,
 * which stores the actual Patches and can swap and replace colors and patches,
 * as well as calculating the fractions for colors.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         fortunan $ on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.3 $
 */

public class Block implements Iterable<Patch>{

    /**
     * An array to carry the patches.
     */
    private Patch[] patchArray;

    /**
     * The size of this Block, in Patches
     */
    private int size = 0;

    /**
     * The height or width of this Block, in Patches
     */
    private int sideSize = 0;

    /**
     * Default constructor. The default size should be SIXTEEN. Sets all patches
     * to the default (gray) patch.
     */
    public Block() {
        this(16);
    }

    /**
     * Constructor that takes an int to determine the size of the Block (how
     * many patches will be in the block).
     * 
     * @param size
     *            The size to construct the Block with.
     */
    public Block(final int size) {
        // Check that size is a valid size. If not, throw exception;
        if ((size == 16) || (size == 9) || (size == 4) || (size == 1)) {
            patchArray = new Patch[size];
            sideSize = (int) Math.sqrt(size);
            for (int i = 0; i < patchArray.length; i++) {
                patchArray[i] = new Patch();
            }
            this.size = size;
        } else {
            System.out.println("Block Size: " + size);
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Method to get the size of the Block (in patches)
     * 
     * @return size The size of the Block.
     */
    public int getSize() {
        return size;
    }

    /**
     * Method to get the height/width of this block, in patches
     * @return height/width of block
     */
    public int getSideSize() {
        return sideSize;
    }

    /**
     * Method to put a patch on a specified location on the block, while
     * checking that the specified location is valid.
     * 
     * @param aPatch
     *            The patch to be placed.
     * @param location
     *            the desired location to place the patch.
     */
    public void setPatch(final Patch aPatch, final int location) {
        if (location >= 0 && location < patchArray.length) {
            patchArray[location] = aPatch;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Method to get a Patch at a specific location
     * 
     * @param location
     *            The location of the desired Patch.
     * @return patchArray[location] The desired Patch.
     */
    public Patch getPatch(final int location) {
        if (location >= 0 && location < patchArray.length) {
            return patchArray[location];
        }
        throw new IndexOutOfBoundsException();

    }


    /**
     * Equals method for Blocks.
     * 
     * @return true if the given block is equal to this one.
     */ 
    @Override
    public boolean equals(final Object element) {
        if (!(element instanceof Block)){
            return false;
        }
        Block otherBlock = (Block) element;
        if (!(otherBlock.getSize() == getSize())) {
            return false;
        }
        for (int i = 0; i < getSize(); i++) {
            if (!(otherBlock.getPatch(i).equals(patchArray[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Since we are overriding equals, we should implement our own hashcode.
     * 
     * @return int containing the hash
     */
    @Override
    public int hashCode() {
        int hash = 19;
        for (int i = 0; i < patchArray.length; i++) {
            hash *= patchArray[i].hashCode() + 3;
        }
        return hash;
    }



    /**
     * Replaces fabrics in the block and then redraws the blockwork panel
     * 
     * @param replaceThis
     *            the fabric to be replaced
     * @param withThis
     *            the fabric to replace "replaceThis" with
     */
    public void replaceFabricInBlock(final Fabric replaceThis,
            final Fabric withThis) {
        for (int i = 0; i < size; i++) {
            patchArray[i].replaceFabrics(replaceThis, withThis);
        }
    }

    /**
     * This is the method to change one fabric in the backend to another fabric
     * in the backend swapping both
     * 
     * @param one
     *            the fabric to replace two
     * @param two
     *            the fabric to replace one
     */
    public void swapFabricInBlock(final Fabric one, final Fabric two) {
        for (int i = 0; i < size; i++) {
            patchArray[i].swapFabrics(one, two);
        }
    }

    /**
     * This method returns a block identical to the block which the method was
     * called upon, but it is a completely independent object.
     * 
     * @return a clone of the block which it was called upon.
     */
    public Block getBlockClone() {
        final Block newBlock = new Block(size);
        for (int i = 0; i < size; i++) {
            newBlock.setPatch(patchArray[i].getPatchClone(), i);
        }
        return newBlock;
    }

    /**
     * Goes through and replaces all instances of replaceThis with withThis.
     * This method is the backend for the patch swap functionality of the Patch
     * Work Area triangle
     * 
     * @param replaceThis
     *            replace all instances of this patch in the block work area
     * @param withThis
     *            this is the patch that replaces instances of the replaceThis
     */
    public void replacePatchInBlock(Patch replaceThis, Patch withThis) {
        // We first need to get all 4 rotations of each of these
        Patch[] replaceThese = getPatchRotations(replaceThis);
        Patch[] withThese = getPatchRotations(withThis);

        // Now perform the actual replacement
        for (int i=0; i<patchArray.length; i++){
            Patch currentPatch = patchArray[i];
            for (int j=0; j<4; j++){
                Patch replaceMe = replaceThese[j];
                if (replaceMe.equals(currentPatch)){
                    patchArray[i] = withThese[j].getPatchClone();
                }
            }

        }
    }

    /**
     * replaces all instances of one patch with the other and vice versa
     * 
     * @param swapThis
     *            one of the patches that is being swapped
     * @param andThis
     *            the second patch that is being swapped
     */
    public void swapPatchesInBlock(Patch swapThis, Patch andThis) {
        // Get rotations of the given patches
        Patch[] swapThese = getPatchRotations(swapThis);
        Patch[] andThese = getPatchRotations(andThis);

        // Go through the Block and swap them
        for (int i=0; i<patchArray.length; i++){
            Patch currentPatch = patchArray[i];
            for (int j=0; j<4; j++){
                if (swapThese[j].equals(currentPatch)){
                    patchArray[i] = andThese[j].getPatchClone();
                } else if (andThese[j].equals(currentPatch)){
                    patchArray[i] = swapThese[j].getPatchClone();
                }
            }
        }
    }

    /**
     * Get all 4 rotations of a Patch in an array. The given Patch
     * will be the last one (see the symmetrical swap replace test in 
     * BlockTest for an explanation)
     * 
     * @param patch
     * @return an array of all 4 rotations of this Patch
     */
    static private Patch[] getPatchRotations(Patch patch){
        Patch[] rotations = new Patch[4];
        rotations[0] = patch.getPatchClone();
        // Rotate it so that the original is the last one
        rotations[0].rotateCW();
        for (int i=1; i<4; i++){
            rotations[i] = rotations[i-1].getPatchClone();
            rotations[i].rotateCW();
        }

        return rotations;
    }

    /**
     * Method to clear the block and return it to new block status
     * 
     */
    public void clear() {
        for (int i = 0; i < this.getSize(); i++) {
            setPatch(new Patch(), i);
        }
    }

    /**
     * Method to get the Fraction that represents the coverage of the desired Fabric.
     * @param fabric the fabric to get coverage of
     * 
     * @return the fraction that this fabric covers
     */

    public Fraction getBlockCoverage(Fabric fabric){
        int sumOfPatches = 0;
        for(Patch patches: patchArray){
            sumOfPatches =sumOfPatches + patches.getPatchCoverage(fabric);
        }
        return new Fraction(sumOfPatches, size*Patch.MAXTILES);
    }

    /**
     * @return an iteration over all of the tiles of this Block
     */
    public Iterable<Fabric> allTiles() {
        final Iterator<Fabric> iterator =  new Iterator<Fabric>(){

            int currentPatch = 0;
            int currentTile = 0;
            
            public boolean hasNext() {
                return (currentPatch < getSize());
            }

            public Fabric next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                
                Fabric theFabric = 
                    getPatch(currentPatch).getTile(currentTile);
                currentTile++;
                if (currentTile >= Patch.MAXTILES){
                    currentTile = 0;
                    currentPatch++;
                }
                return theFabric;
            }

            public void remove() {
                throw new UnsupportedOperationException(
                        "Cannot remove tiles from blocks with this iterator"
                );
            }
            
        };
        
        return new Iterable<Fabric>(){

            public Iterator<Fabric> iterator() {
                return iterator;
            }
            
        };
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Patch> iterator() {
        return new Iterator<Patch>() {

            int current = 0;
            
            public boolean hasNext() {
                return current < getSize();
            }

            public Patch next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                
                Patch thePatch = getPatch(current);
                current++;
                return thePatch;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }


}
