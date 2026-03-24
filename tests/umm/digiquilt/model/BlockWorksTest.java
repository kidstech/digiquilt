package umm.digiquilt.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.UndoRedoStack;

/**
 * Test the BlockWorks class, which holds and controls access to the current
 * Block as well as keeping track of undo/redo history.
 */
public class BlockWorksTest{

    /**
     * Test the constructors for BlockWorks
     */
    @Test
    public void testConstructors(){
        BlockWorks defaultWorks = new BlockWorks();
        assertEquals("Default should come with an empty Block",  new Block(), defaultWorks.getCurrentBlockClone());
        assertNotNull("Should have a grid by default", defaultWorks.getGrid());
        assertEquals("Undo stack should be empty", 0, defaultWorks.getUndoRedoStack().getUndoStackSize());
        assertEquals("Redo stack should be empty", 0, defaultWorks.getUndoRedoStack().getRedoStackSize());
    }
    
    /**
     * Test notification by BlockWorks
     */
    @Test
    public void testBlockListener() {
        BlockWorks works = new BlockWorks();
        PropertyChangeListener observer = mock(PropertyChangeListener.class);
        String propertyName = "currentBlock";
        
        works.addPropertyChangeListener(propertyName, observer);
        
        Block oldBlock;
        Block newBlock = new Block();
        newBlock.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        
        oldBlock = works.getCurrentBlockClone();
        works.setCurrentBlock(newBlock);
        verifyPropertyChange(observer, 1, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        
        oldBlock = works.getCurrentBlockClone();
        works.swapFabricInBlock(Fabric.BLUE, Fabric.INDIGO);
        verifyPropertyChange(observer, 2, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        
        oldBlock = works.getCurrentBlockClone();
        works.replaceFabricInBlock(Fabric.INDIGO, Fabric.RED);
        verifyPropertyChange(observer, 3, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        
        oldBlock = works.getCurrentBlockClone();
        works.swapPatchesInBlock(Shape.FULLSQUARE.getPatch(Fabric.RED),
                                 Shape.HALFTRIANGLE.getPatch(Fabric.GREEN));
        verifyPropertyChange(observer, 4, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        
        oldBlock = works.getCurrentBlockClone();
        works.replacePatchInBlock(Shape.HALFTRIANGLE.getPatch(Fabric.GREEN),
                                  Shape.HALFRECTANGLE.getPatch(Fabric.RED));
        verifyPropertyChange(observer, 5, propertyName, 
                oldBlock, works.getCurrentBlockClone());

        
        oldBlock = works.getCurrentBlockClone();
        works.setPatch(new Patch(), 0);
        verifyPropertyChange(observer, 6, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        
        
        // Add a second observer, and then remove it
        PropertyChangeListener observer2 = mock(PropertyChangeListener.class);
        works.addPropertyChangeListener(propertyName, observer2);
        
        oldBlock = works.getCurrentBlockClone();
        works.setPatch(new Patch(), 1, false);
        verifyPropertyChange(observer, 7, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        verifyPropertyChange(observer2, 1, propertyName, 
                oldBlock, works.getCurrentBlockClone());

        works.removePropertyChangeListener(propertyName, observer2);
        
        oldBlock = works.getCurrentBlockClone();
        works.clear();
        verifyPropertyChange(observer, 8, propertyName, 
                oldBlock, works.getCurrentBlockClone());
        verify(observer2, times(1)).propertyChange(any(PropertyChangeEvent.class));
        
        
    }
    
    /**
     * Test that a PropertyChangeEvent was sent with these values:
     * 
     * @param mock The mock listener
     * @param times Number of times to expect, for example never() or times(2)
     * (Mockito's static methods)
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public static void verifyPropertyChange(PropertyChangeListener mock, int times,
            String propertyName, Object oldValue, Object newValue){
        PropertyChangeEvent event = getPropertyChangeEvent(mock, times);
        
        assertEquals(propertyName, event.getPropertyName());
        assertEquals(oldValue, event.getOldValue());
        assertEquals(newValue, event.getNewValue());
        
    }
    
    /**
     * Verify that a PropertyChangeListener has gotten called some number of
     * times, and returns the PropertyChangeEvent that was passed to this 
     * mock.
     * 
     * @param mock
     * @param times
     * @return the PropertyChangeEvent
     */
    public static PropertyChangeEvent getPropertyChangeEvent(
            PropertyChangeListener mock, int times){
        ArgumentCaptor<PropertyChangeEvent> captor = 
            ArgumentCaptor.forClass(PropertyChangeEvent.class);
        
        verify(mock, times(times)).propertyChange(captor.capture());
        return captor.getValue();
    }
    
    /**
     * 
     */
    @Test
    public void testGridListener(){
        BlockWorks works = new BlockWorks();
        String propertyName = "grid";
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        PropertyChangeListener listener2 = mock(PropertyChangeListener.class);

        
        works.addPropertyChangeListener(propertyName, listener);
        works.addPropertyChangeListener(propertyName, listener2);

        Grid testGrid1 = new Grid(0, 0, 2, 0);
        works.setGrid(testGrid1);
        checkGridEvent(getPropertyChangeEvent(listener, 1));
        checkGridEvent(getPropertyChangeEvent(listener2, 1));
        
        Grid testGrid2 = new Grid(8, 8, 0, 0);
        works.setGrid(testGrid2);
        checkGridEvent(getPropertyChangeEvent(listener, 2));
        checkGridEvent(getPropertyChangeEvent(listener2, 2));
        
        works.removePropertyChangeListener(propertyName, listener);
        Grid testGrid3 = new Grid(4, 4, 4, 4);
        works.setGrid(testGrid3);
        checkGridEvent(getPropertyChangeEvent(listener, 2));
        checkGridEvent(getPropertyChangeEvent(listener2, 3));
        
        works.removePropertyChangeListener(propertyName, listener2);
        Grid testGrid4 = new Grid(8, 2, 0, 0);
        works.setGrid(testGrid4);
        checkGridEvent(getPropertyChangeEvent(listener, 2));
        checkGridEvent(getPropertyChangeEvent(listener2, 3));
    }
    
    /**
     * @param event
     */
    private void checkGridEvent(PropertyChangeEvent event){
        assertEquals("grid", event.getPropertyName());
    }
    

    /**
     * Test undoing and redoing
     */
    @Test
    public void testUndoRedoBehavior(){
        BlockWorks works = new BlockWorks();
        UndoRedoStack testStack = works.getUndoRedoStack();
        
        
        JButton testUndoButton = new JButton();
        JButton testRedoButton = new JButton();
        testUndoButton.setEnabled(true);
        testRedoButton.setEnabled(true);
        works.getUndoRedoStack().addUndoButton(testUndoButton);
        works.getUndoRedoStack().addRedoButton(testRedoButton);
        
        assertFalse("Buttons should have been disabled", 
                testUndoButton.isEnabled());
        assertFalse("Buttons should have been disabled", 
                testRedoButton.isEnabled());
        
        works.clear();
        assertEquals("Should have pushed undo on clear", 1, testStack.getUndoStackSize());
        assertTrue("Undo button should have been enabled", 
                testUndoButton.isEnabled());
        assertFalse("Redo button should have stayed disabled", 
                testRedoButton.isEnabled());
        
        
        works.replaceFabricInBlock(Fabric.TRANSPARENT, Fabric.PINK);
        assertEquals("Shoud have pushed undo on replace fabric", 2, testStack.getUndoStackSize());
        
        works.swapFabricInBlock(Fabric.PINK, Fabric.BROWN);
        assertEquals("Should have pushed undo on swap fabric", 3, testStack.getUndoStackSize());
        
        works.replacePatchInBlock(new Patch(), new Patch());
        assertEquals("Should have pushed undo on replace patch", 4, testStack.getUndoStackSize());
        
        works.swapPatchesInBlock(new Patch(), new Patch());
        assertEquals("Should have pushed undo on swap patch", 5, testStack.getUndoStackSize());
        
        works.setPatch(new Patch(), 0);
        assertEquals("Should have pushed undo on patch set", 6, testStack.getUndoStackSize());
        
        works.setPatch(new Patch(), 0, true);
        assertEquals("Should have pushed an undo on patch set", 7, testStack.getUndoStackSize());
        
        works.setPatch(new Patch(), 0, false);
        assertEquals("Should NOT have pushed a block", 7, testStack.getUndoStackSize());
        
        works.setCurrentBlock(new Block());
        assertEquals("Should NOT have pushed a block on block set", 7, testStack.getUndoStackSize());
        
        works.performUndo();
        works.performUndo();
        works.performUndo();
        works.performUndo();
        works.performUndo();
        
        works.performRedo();
        works.performRedo();
        
        assertEquals("Should be 4 blocks left on the undo stack", 4, testStack.getUndoStackSize());
        assertEquals("Should be 3 blocks now on the redo stack", 3,  testStack.getRedoStackSize());
        assertTrue("Undo button should still be enabled", 
                testUndoButton.isEnabled());
        assertTrue("Redo button should now be enabled",
                testRedoButton.isEnabled());
        
        
        works.clear();
        
        assertEquals("Should be 5 blocks on the undo stack", 5, testStack.getUndoStackSize());
        assertEquals("Should be no elements on the redo stack", 0, testStack.getRedoStackSize());
        assertTrue("Undo button should still be enabled", 
                testUndoButton.isEnabled());
        assertFalse("Redo button should now be disabled",
                testRedoButton.isEnabled());
        
        
        UndoRedoStack newStack = new UndoRedoStack();
        works.setNewUndoStack(newStack);
        assertFalse("Undo button should have been disabled for new stack",
                testUndoButton.isEnabled());
        assertFalse("Redo button should still be disabled",
                testRedoButton.isEnabled());
        
        newStack.addUndo(new Block());
        assertTrue("Undo button isn't being changed after setting new stack",
                testUndoButton.isEnabled());
        assertFalse("Redo button should still be disabled",
                testRedoButton.isEnabled());
        
        works.performUndo();
        assertFalse("Undo button wasn't disabled", testUndoButton.isEnabled());
        assertTrue("Redo button isn't being changed after setting new stack", 
                testRedoButton.isEnabled());
        
    }

    /**
     * Test block clearing
     */
    @Test
    public void testClear(){
        BlockWorks works = new BlockWorks();
        works.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        works.clear();
        assertEquals("Block should have been cleared", new Block(), works.getCurrentBlockClone());
    }

    /**
     * Test getting the size of the block
     */
    @Test
    public void testGetSize(){
        BlockWorks works = new BlockWorks();
        works.setCurrentBlock(new Block(9));
        assertEquals("Block size should be 9", 9, works.getSize());
        assertEquals("Side size should be 3", 3, works.getSideSize());
        works.setCurrentBlock(new Block(16));
        assertEquals("Block size should be 16", 16, works.getSize());
        assertEquals("Side size should be 4", 4, works.getSideSize());
        works.setCurrentBlock(new Block(4));
        assertEquals("Block size should be 4", 4, works.getSize());
        assertEquals("Side size should be 2", 2, works.getSideSize());
    }

    /**
     * Test fractional coverage information
     */
    @Test
    public void testBlockCoverage(){
        Patch testPatch = new Patch(new Fabric[] {
                Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, Fabric.GREEN,
                Fabric.BLUE, Fabric.BLUE, Fabric.BLUE, Fabric.BLUE,
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW,
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW});
        BlockWorks works = new BlockWorks();
        works.replacePatchInBlock(new Patch(), testPatch);
        Fraction green = works.getBlockCoverage(Fabric.GREEN);
        Fraction blue = works.getBlockCoverage(Fabric.BLUE);
        Fraction yellow = works.getBlockCoverage(Fabric.YELLOW);
        assertEquals("Green should be 1/4", new Fraction(1,4), green);
        assertEquals("Blue should be 1/4", new Fraction(1, 4), blue);
        assertEquals("Yellow should be 1/2", new Fraction(1,2), yellow);
    }

    /**
     * Test swapping of patches and colors on the block
     */
    @Test
    public void testSwap(){
        Patch testPatch = makeTestPatch();
        BlockWorks works = new BlockWorks();
        Block control = new Block();

        works.setPatch(testPatch, 0);
        control.setPatch(testPatch, 0);
        
        works.swapPatchesInBlock(new Patch(), testPatch);
        control.swapPatchesInBlock(new Patch(), testPatch);
        assertEquals("BlockWorks did not swap patches like a regular Block",
                control, works.getCurrentBlockClone() );
        
        works.swapFabricInBlock(Fabric.YELLOW, Fabric.BLUE);
        control.swapFabricInBlock(Fabric.YELLOW, Fabric.BLUE);
        assertEquals("BlockWorks did not swap fabrics like a regular Block",
                control, works.getCurrentBlockClone() );
    }

    /**
     * Test replacing fabrics in the block
     */
    @Test
    public void testReplaceFabric(){
        Patch testPatch = makeTestPatch();
        Block control = new Block();
        control.swapPatchesInBlock(new Patch(), testPatch);
        BlockWorks works = new BlockWorks();
        works.setCurrentBlock(control.getBlockClone());
        
        control.replaceFabricInBlock(Fabric.YELLOW, Fabric.BLUE);
        works.replaceFabricInBlock(Fabric.YELLOW, Fabric.BLUE);
        assertEquals("BlockWorks did not replace fabrics like a regular Block",
                control, works.getCurrentBlockClone());
    }


    /**
     * Test replacing patches in the block
     */
    @Test
    public void testReplacePatch(){
        Patch testPatch = makeTestPatch();
        Block control = new Block();
        control.swapPatchesInBlock(new Patch(), testPatch);
        control.setPatch(PatchTest.makeFancyTestPatch(), 0);
        
        BlockWorks works = new BlockWorks();
        works.setCurrentBlock(control.getBlockClone());

        control.replacePatchInBlock(testPatch, PatchTest.makeFancyTestPatch());
        works.replacePatchInBlock(testPatch, PatchTest.makeFancyTestPatch());
        assertEquals("BlockWorks did not replace patch like a regular block", 
                control, works.getCurrentBlockClone());
    }

    /**
     * Test getting and setting patches
     */
    @Test
    public void testSetPatch(){
        BlockWorks works = new BlockWorks();
        Patch testPatch = PatchTest.makeFancyTestPatch();
        works.setPatch(testPatch, 0);
        assertEquals("Patch was not set", testPatch, works.getPatch(0));
    }
    
    /**
     * @return a test patch
     */
    private Patch makeTestPatch(){
        return  new Patch(new Fabric[] {
                Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, Fabric.GREEN,
                Fabric.BLUE, Fabric.BLUE, Fabric.BLUE, Fabric.BLUE,
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW,
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW});
    }


}
