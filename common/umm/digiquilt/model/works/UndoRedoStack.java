package umm.digiquilt.model.works;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.AbstractButton;

import umm.digiquilt.model.Block;

/**
 * Holds UndoElements and takes care of undo and redo operations, as well as turning
 * on and off any relevent buttons depending on if there are any undos or redos
 * possible.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-15 20:59:50 $
 * @version $Revision: 1.2 $
 *
 */
public class UndoRedoStack {

    /**
     * Stack holding all the elements in the undo queue.
     */
    protected Stack<Block> undoStack = new Stack<Block>();

    /**
     * Stack holding all the elements in the redo queue.
     */
    protected Stack<Block> redoStack = new Stack<Block>();

    /**
     * List of AbstractButtons, which are enabled or disabled depending on if
     * an undo is possible.
     */
    protected List<AbstractButton> undoListeners =
        new ArrayList<AbstractButton>();

    /**
     * List of AbstractButtons, which are enabled or disabled depending on if
     * a redo is possible.
     */
    protected List<AbstractButton> redoListeners =
        new ArrayList<AbstractButton>();

    /**
     * Sets all listening buttons to the appropriate state.
     */
    protected void recheckButtons(){
        boolean hasUndo = !undoStack.isEmpty();
        for (AbstractButton button : undoListeners){
            button.setEnabled(hasUndo);
        }
        boolean hasRedo = !redoStack.isEmpty();
        for (AbstractButton button : redoListeners){
            button.setEnabled(hasRedo);
        }
    }

    /**
     * @return the next block in the undo stack.
     */
    public Block popNextUndoBlock(){
        Block element = undoStack.pop();
        recheckButtons();
        return element;
    }

    /**
     * @return the next block in the redo stack.
     */
    public Block popNextRedoBlock(){
        Block element = redoStack.pop();
        recheckButtons();
        return element;
    }

    /**
     * Remove everything currently in the undo stack.
     */
    public void clearUndoStack(){
        undoStack.clear();
        recheckButtons();
    }

    /**
     * Remove everything currently in the redo stack.
     */
    public void clearRedoStack(){
        redoStack.clear();
        recheckButtons();
    }

    /** Push an Block on to the undo stack.
     * @param undo
     */
    public void addUndo(Block undo){
        undoStack.push(undo);
        recheckButtons();
    }

    /** Push an Block on to the redo stack.
     * @param redo
     */
    public void addRedo(Block redo){
        redoStack.push(redo);
        recheckButtons();
    }

    /**Subscribe an AbstractButton to be turned on and off depending on if 
     * an Undo is possible.
     * @param button
     */
    public void addUndoButton(AbstractButton button){
        undoListeners.add(button);
        if (undoStack.isEmpty()){
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    /**Subscribe an AbstractButton to be turned on and off depending on if
     * a Redo is possible.
     * @param button
     */
    public void addRedoButton(AbstractButton button){
        redoListeners.add(button);
        if (redoStack.isEmpty()){
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }   
    }

    /**
     * Replaces this Stack's listeners with the given stack's.
     * @param victim
     */
    public void stealButtons(UndoRedoStack victim){
        this.redoListeners = victim.redoListeners;
        this.undoListeners = victim.undoListeners;
        recheckButtons();
    }

    /** Get the nth element in the Undo stack. Useful for saving the stack without
     * destroying it.
     * 
     * @param index
     * @return the Block at index in the undo stack.
     */
    public Block getUndoElement(int index){
        return undoStack.get(index);
    }

    /** Get the nth element in the Redo stack. Useful for saving the stack without
     * destroying it.
     * 
     * @param index
     * @return the Block at index in the redo stack.
     */
    public Block getRedoElement(int index){
        return redoStack.get(index);
    }

    /**
     * @return the number of elements in the undo stack.
     */
    public int getUndoStackSize(){
        return undoStack.size();
    }

    /**
     * @return the number of elements in the redo stack.
     */
    public int getRedoStackSize(){
        return redoStack.size();
    }
}
