/*
 * Created on Apr 12, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package umm.digiquilt.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.works.UndoRedoStack;

/**
 * Test the UndoRedoStack
 *
 */
public class UndoRedoTest{
    
    /**
     * Test popping on undo side
     */
    @Test
    public void testUndoPop(){
        UndoRedoStack testStack = new UndoRedoStack();
        Block block1 = new Block();
        Block block2 = new Block();
        Block block3 = new Block();
        block1.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.RED);
        block2.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        block3.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        testStack.addUndo(block1);
        testStack.addUndo(block2);
        testStack.addUndo(block3);
        
        assertEquals("Did not pop the correct Block", testStack.popNextUndoBlock(), block3);
        assertEquals("Did not pop the correct Block", testStack.popNextUndoBlock(), block2);
        assertEquals("Did not pop the correct Block", testStack.popNextUndoBlock(), block1);
        assertEquals("Stack should be empty", testStack.getUndoStackSize(), 0);
    }
    
    /**
     * Test popping on the redo side
     */
    @Test
    public void testRedoPop(){
        UndoRedoStack testStack = new UndoRedoStack();
        Block block1 = new Block();
        Block block2 = new Block();
        Block block3 = new Block();
        block1.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.RED);
        block2.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        block3.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        testStack.addRedo(block1);
        testStack.addRedo(block2);
        testStack.addRedo(block3);
        
        assertEquals("Did not pop the correct Block", testStack.popNextRedoBlock(), block3);
        assertEquals("Did not pop the correct Block", testStack.popNextRedoBlock(), block2);
        assertEquals("Did not pop the correct Block", testStack.popNextRedoBlock(), block1);
        assertEquals("Stack should be empty", testStack.getRedoStackSize(), 0);
    }
    
    /**
     * Test undo stack clearing
     */
    @Test
    public void testUndoClear(){
        UndoRedoStack testStack = new UndoRedoStack();
        Block testBlock1 = new Block();
        Block testBlock2 = new Block();
        Block testBlock3 = new Block();
        testStack.addUndo(testBlock1);
        testStack.addUndo(testBlock2);
        testStack.addUndo(testBlock3);
        
        assertEquals("The Undo stack has the wrong number of elements in it",testStack.getUndoStackSize(), 3);
        
        testStack.clearUndoStack();
        
        assertEquals("The Undo stack should be empty at this point",testStack.getUndoStackSize(), 0);
    }
    
    /**
     * Test redo stack clearing
     */
    @Test
    public void testRedoClear(){
        UndoRedoStack testStack = new UndoRedoStack();
        Block testBlock1 = new Block();
        Block testBlock2 = new Block();
        Block testBlock3 = new Block();
        testStack.addRedo(testBlock1);
        testStack.addRedo(testBlock2);
        testStack.addRedo(testBlock3);
        
        assertEquals("The Redo stack has the wrong number of elements in it",testStack.getRedoStackSize(), 3);
        
        testStack.clearRedoStack();
        
        assertEquals("The Redo stack should be empty at this point",testStack.getRedoStackSize(), 0);
    }
    
    /**
     * Test that subscribed buttons are set properly
     */
    @Test
    public void testButtonSwitching(){
        AbstractButton undoButton1 = new JButton();
        AbstractButton undoButton2 = new JButton();
        AbstractButton redoButton1 = new JButton();
        AbstractButton redoButton2 = new JButton();
        undoButton1.setEnabled(true);
        undoButton2.setEnabled(true);
        redoButton1.setEnabled(true);
        redoButton2.setEnabled(true);
        UndoRedoStack testStack = new UndoRedoStack();
        testStack.addUndoButton(undoButton1);
        testStack.addUndoButton(undoButton2);
        testStack.addRedoButton(redoButton1);
        testStack.addRedoButton(redoButton2);
        assertFalse("Buttons should be set to the correct state after adding", undoButton1.isEnabled());
        assertFalse("Buttons should be set to the correct state after adding", undoButton2.isEnabled());
        assertFalse("Buttons should be set to the correct state after adding", redoButton1.isEnabled());
        assertFalse("Buttons should be set to the correct state after adding", redoButton2.isEnabled());
        testStack.addUndo(new Block());
        assertTrue("Undos should be enabled now", undoButton1.isEnabled());
        assertTrue("Undos should be enabled now", undoButton2.isEnabled());
        assertFalse("Redos should not be enabled yet", redoButton1.isEnabled());
        assertFalse("Redos should not be enabled yet", redoButton2.isEnabled());
        testStack.clearUndoStack();
        assertFalse("Undos should now be disabled", undoButton1.isEnabled());
        assertFalse("Undos should now be disabled", undoButton2.isEnabled());
        assertFalse("Redos should still be disabled", redoButton1.isEnabled());
        assertFalse("Redos should still be disabled", redoButton2.isEnabled());
        testStack.addRedo(new Block());
        assertFalse("Undos should still be disabled", undoButton1.isEnabled());
        assertFalse("Undos should still be disabled", undoButton2.isEnabled());
        assertTrue("Redos should now be enabled", redoButton1.isEnabled());
        assertTrue("Redos should now be enabled", redoButton2.isEnabled());
        testStack.clearRedoStack();
        assertFalse("Undos should still be disabled", undoButton1.isEnabled());
        assertFalse("Undos should still be disabled", undoButton2.isEnabled());
        assertFalse("Redos should not be enabled", redoButton1.isEnabled());
        assertFalse("Redos should not be enabled", redoButton2.isEnabled());
        
        
        UndoRedoStack stackTwo = new UndoRedoStack();
        stackTwo.stealButtons(testStack);
        testStack = null;
        
        assertFalse("Undos should now be disabled", undoButton1.isEnabled());
        assertFalse("Undos should now be disabled", undoButton2.isEnabled());
        assertFalse("Redos should now be disabled", redoButton1.isEnabled());
        assertFalse("Redos should now be disabled", redoButton2.isEnabled());
        stackTwo.addUndo(new Block());
        assertTrue("Undos should be enabled now", undoButton1.isEnabled());
        assertTrue("Undos should be enabled now", undoButton2.isEnabled());
        assertFalse("Redos should not be enabled yet", redoButton1.isEnabled());
        assertFalse("Redos should not be enabled yet", redoButton2.isEnabled());
        stackTwo.addRedo(new Block());
        assertTrue("Undos should still be enabled", undoButton1.isEnabled());
        assertTrue("Undos should still be enabled", undoButton2.isEnabled());
        assertTrue("Redos should now be enabled", redoButton1.isEnabled());
        assertTrue("Redos should now be enabled", redoButton2.isEnabled());
        JButton undoButton3 = new JButton();
        JButton redoButton3 = new JButton();
        stackTwo.addUndoButton(undoButton3);
        stackTwo.addRedoButton(redoButton3);
        assertTrue("Latecomer button should be in sync with the others", 
                undoButton3.isEnabled());
        assertTrue("Latecomer button should be in sync with the others", 
                redoButton3.isEnabled());
        
        stackTwo.clearUndoStack();
        assertFalse("Undos should be disabled now", undoButton1.isEnabled());
        assertFalse("Undos should be disabled now", undoButton2.isEnabled());
        assertFalse("Undos should be disabled now", undoButton3.isEnabled());
        assertTrue("Redos should still be enabled", redoButton1.isEnabled());
        assertTrue("Redos should still be enabled", redoButton2.isEnabled());
        assertTrue("Redos should still be enabled", redoButton3.isEnabled());
        stackTwo.clearRedoStack();
        assertFalse("Undos should still be disabled", undoButton1.isEnabled());
        assertFalse("Undos should still be disabled", undoButton2.isEnabled());
        assertFalse("Undos should still be disabled", undoButton3.isEnabled());
        assertFalse("Redos should not be enabled", redoButton1.isEnabled());
        assertFalse("Redos should not be enabled", redoButton2.isEnabled());
        assertFalse("Redos should not be enabled", redoButton3.isEnabled());
        
    }
    
    /**
     * Test that getting elements is indexed properly
     */
    @Test
    public void testGetElements(){
        UndoRedoStack testStack = new UndoRedoStack();
        Block block1 = new Block();
        Block block2 = new Block();
        Block block3 = new Block();
        block1.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.RED);
        block2.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        block3.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        testStack.addUndo(block1);
        testStack.addUndo(block2);
        testStack.addUndo(block3);
        assertEquals("Did not get the correct Block", testStack.getUndoElement(0), block1);
        assertEquals("Did not get the correct Block", testStack.getUndoElement(1), block2);
        assertEquals("Did not get the correct Block", testStack.getUndoElement(2), block3);
        testStack.clearUndoStack();
        testStack.addRedo(block1);
        testStack.addRedo(block2);
        testStack.addRedo(block3);
        assertEquals("Did not get the correct Block", testStack.getRedoElement(0), block1);
        assertEquals("Did not get the correct Block", testStack.getRedoElement(1), block2);
        assertEquals("Did not get the correct Block", testStack.getRedoElement(2), block3);
    }
}
