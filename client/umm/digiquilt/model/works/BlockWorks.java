package umm.digiquilt.model.works;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.Patch;
import umm.digiquilt.view.GridDisplayer;

/**
 * Provides abstracted access to the current block, pushing Undos when necessary. 
 * Its job is to hold the current block, and notify subscribers when it is changed, 
 * and push those changes to the Undo stack.
 * 
 * @author biatekjt, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */
public class BlockWorks implements GridDisplayer{
    
    /**
     * The block currently being worked on in the BlockWorkArea.
     */
    private Block currentBlock;
    
    /**
     * The Grid currently being used.
     */
    private Grid grid;
    
    /**
     * Stack that holds and executes undos and redos.
     */
    private UndoRedoStack undoRedo;
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * Create a new BlockWorks, with an empty Block and new UndoRedoStack. 
     */
    public BlockWorks(){
        currentBlock = new Block();
        grid = new Grid(
                currentBlock.getSideSize(), currentBlock.getSideSize(), 0, 0);
        undoRedo = new UndoRedoStack();
    }
    
    /**
     * Add a PropertyChangeListener to this object. Properties are:
     * <br />
     * "grid": The current Grid. <br />
     * "currentBlock": The current Block. <br />
     * 
     * @param property
     * @param listener
     */
    public void addPropertyChangeListener(String property, 
            PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property, listener);
    }
    
    /**
     * Remove this PropertyChangeListener.
     * 
     * @param property 
     * @param listener
     */
    public void removePropertyChangeListener(String property, 
            PropertyChangeListener listener){
        pcs.removePropertyChangeListener(property, listener);
    }


    /**
     * Set the current Grid.
     * 
     * @param grid
     */
    public void setGrid(Grid grid){
        Grid oldGrid = this.grid;
        this.grid = grid;
        pcs.firePropertyChange("grid", oldGrid, this.grid);
    }
    
    /**
     * @return the Grid currently being used
     */
    public Grid getGrid(){
        return grid;
    }
    
    /**
     * Saves the current state of the block and pushes it onto the Undo stack.
     */
    private void pushUndo() {
        undoRedo.clearRedoStack();
        undoRedo.addUndo(this.getCurrentBlockClone());
    }
    
    /**
     * @return this BlockWorks' UndoRedoStack
     */
    public UndoRedoStack getUndoRedoStack(){
        return undoRedo;
    }
  
    /**
     * Get a clone of the current block.
     * 
     * @return clone of the current block
     */
    public Block getCurrentBlockClone() {
        return currentBlock.getBlockClone();
    }
    
    /**
     * Replace the current block with a new one. Note: This does not push an Undo, since
     * it's used by the Undos themselves and when a file is loaded from XML. 
     * 
     * @param newBlock
     */
    public void setCurrentBlock(Block newBlock) {
        if (newBlock.getSize() != currentBlock.getSize()){
            // We need to change the grid too
            setGrid(new Grid(
                    newBlock.getSideSize(), newBlock.getSideSize(), 0, 0));
        }
        
        Block oldBlock = currentBlock;
        currentBlock = newBlock;
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**Replace the current UndoRedoStack with a new one. The button listeners from
     * the old one will be transferred to the new one.
     * @param newStack
     */
    public void setNewUndoStack(UndoRedoStack newStack){
        newStack.stealButtons(undoRedo);
        undoRedo = newStack;
    }
    
    /**
     * Called when a block change is undone. Sets the current block to the given
     * new block and takes care of making the undo redoable.
     */
    public void performUndo(){
        Block newBlock = undoRedo.popNextUndoBlock();
        undoRedo.addRedo(this.getCurrentBlockClone());
        setCurrentBlock(newBlock);
    }
    
    /**
     * Called when a block change is redone. Will set the current block to the
     * given block, and make sure that the redone change is undoable again.
     * 
     */
    public void performRedo(){
        Block newBlock = undoRedo.popNextRedoBlock();
        undoRedo.addUndo(this.getCurrentBlockClone());
        setCurrentBlock(newBlock);
    }
    /**
     * Clear the current block.
     * 
     * @see umm.digiquilt.model.Block#clear()
     */
    public void clear(){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.clear();
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    /**
     * Get the size of the current block, in patches.
     * @return size of the block
     * 
     * @see umm.digiquilt.model.Block#getSize()
     */
    public int getSize() {
        return currentBlock.getSize();
    }
    
    /**
     * Method to get the height/width of this block, in patches
     * @return height/width of block
     * 
     * @see umm.digiquilt.model.Block#getSideSize()
     */
    public int getSideSize(){
        return currentBlock.getSideSize();
    }
    
    /**
     * Get the fraction of the current block covered with this fabric.
     * @param fabric the desired fabric
     * @return what fraction is covered by that fabric
     * 
     * @see umm.digiquilt.model.Block#getBlockCoverage(Fabric)
     */
    public Fraction getBlockCoverage(Fabric fabric){
        return currentBlock.getBlockCoverage(fabric);
    }
    /**
     * Replace one fabric with another in the current block.
     * @param replaceThis Replaces this fabric...
     * @param withThis with this one.
     * 
     * @see umm.digiquilt.model.Block#replaceFabricInBlock(Fabric, Fabric)
     */
    public void replaceFabricInBlock(Fabric replaceThis, Fabric withThis){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.replaceFabricInBlock(replaceThis, withThis);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Swap two fabrics in the current block.
     * @param one the first fabric to be swapped
     * @param two the second fabric to be swapped
     * 
     * @see umm.digiquilt.model.Block#swapFabricInBlock(Fabric, Fabric)
     */
    public void swapFabricInBlock(Fabric one, Fabric two){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.swapFabricInBlock(one, two);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Replace one patch with another in the current block.
     * @param replaceThis Replaces this patch...
     * @param withThis with this one.
     * 
     * @see umm.digiquilt.model.Block#replacePatchInBlock(Patch, Patch)
     */
    public void replacePatchInBlock(Patch replaceThis, Patch withThis){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.replacePatchInBlock(replaceThis, withThis);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Swap two patches in the current block.
     * @param swapThis Swap this patch...
     * @param andThis and this one.
     * 
     * @see umm.digiquilt.model.Block#swapPatchesInBlock(Patch, Patch)
     */
    public void swapPatchesInBlock(Patch swapThis, Patch andThis){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.swapPatchesInBlock(swapThis, andThis);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Set a patch to a certain position on the current block.
     * @param aPatch The input patch
     * @param location The position to place this patch
     * 
     * @see umm.digiquilt.model.Block#setPatch(Patch, int)
     */
    public void setPatch(Patch aPatch, int location){
        pushUndo();
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.setPatch(aPatch, location);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Set a patch to a certain position on the current block, with the option of not
     * pushing an undo. Use with caution! This is provided so that when a patch is moved
     * from the current block to the current block, the inbetween state of the block
     * can be discarded.
     * @param aPatch The input patch
     * @param location The position to place this patch
     * @param undo Push an undo onto the stack?
     * 
     * @see umm.digiquilt.model.Block#setPatch(Patch, int)
     */
    public void setPatch(Patch aPatch, int location, boolean undo){
        if (undo){
            pushUndo();
        }
        Block oldBlock = currentBlock.getBlockClone();
        currentBlock.setPatch(aPatch, location);
        pcs.firePropertyChange("currentBlock", oldBlock, getCurrentBlockClone());
    }
    
    /**
     * Returns the patch at a specific location in the current block.
     * @param location The location of the patch
     * @return the patch
     * 
     * @see umm.digiquilt.model.Block#getPatch(int)
     */
    public Patch getPatch(int location){
        return currentBlock.getPatch(location);
    }

}
