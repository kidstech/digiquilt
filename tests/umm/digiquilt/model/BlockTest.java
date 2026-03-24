/*
 * Created by mitchella on Mar 21, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.Shape;

/**
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         mitchella $ on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.10 $
 */
@SuppressWarnings("boxing")
public class BlockTest {

    /**
     * Fancy, unique patch for testing.
     */
    private Patch testPatch;

    /**
     * A blank block for testing.
     */
    private Block testBlock;

    /**
     * Initialize fields for tests
     */
    @Before
    public void setUp() {
        testBlock = new Block();
        testPatch = PatchTest.makeFancyTestPatch();
    }

    /**
     * Test the constructor
     */
    @Test
    public void testConstructor() {
        assertEquals("The size of testBlock should be 16", 16, testBlock
                .getSize());
        for (int i = 0; i < testBlock.getSize(); i++) {
            assertEquals("The patch should be the default transparent patch",
                    new Patch(), testBlock.getPatch(i));
        }
        final Block testBlock2 = new Block(4);
        assertEquals("The size of testBlock2 should be 4", 4, testBlock2
                .getSize());

        boolean threwException = false;
        try {
            @SuppressWarnings("unused")
            final Block testBlock3 = new Block(8);
        } catch (UnsupportedOperationException e) {
            threwException = true;
        }
        assertTrue(
                "Trying to create a block of an invalid size should throw an exception",
                threwException);
    }

    /**
     * Test adding both valid and invalid patches
     */
    @Test
    public void testAddPatch() {
        testBlock.setPatch(testPatch, 3);
        assertEquals("The patch at index 3 should be the testPatch", testPatch,
                testBlock.getPatch(3));

        boolean threwException = false;
        try {
            testBlock.setPatch(testPatch, 90);
        } catch (IndexOutOfBoundsException e) {
            threwException = true;
        }
        assertTrue(
                "Trying to add a patch to an invalid exception should throw an exception",
                threwException);
    }

    /**
     * Test getPatch()
     */
    @Test
    public void testGetPatch() {
        for (int i = 0; i < testBlock.getSize(); i++) {
            assertEquals("The patch at index " + i
                    + " should be the default transparent patch", new Patch(),
                    testBlock.getPatch(i));
        }

        boolean threwException = false;
        try {
            testBlock.getPatch(90);
        } catch (IndexOutOfBoundsException e) {
            threwException = true;
        }
        assertTrue(
                "Trying to get a patch to an invalid exception should throw an exception",
                threwException);
    }

    /**
     * Test that equals() works properly
     */
    @Test
    public void testEquals() {
        testBlock.setPatch(testPatch, 1);
        final Block myBlock = new Block();
        myBlock.setPatch(testPatch, 1);
        final boolean answer = myBlock.equals(testBlock);
        assertTrue("These two Blocks should be equal", answer);

        final Block myBlock2 = new Block(4);
        myBlock2.setPatch(testPatch, 1);
        final boolean answer2 = myBlock2.equals(myBlock);
        assertFalse(
                "A block should not be equal to a block of a different size",
                answer2);

        final int fire = 5;
        final boolean answer3 = myBlock.equals(fire);
        assertFalse("A Block should not be equal to a non-block object",
                answer3);

        testBlock.setPatch(testPatch, 7);
        final boolean answer4 = myBlock.equals(testBlock);
        assertFalse(
                "A Block should not be equal to a block that has different patches in different areas",
                answer4);
        
        assertFalse("equals(null) should return false", myBlock.equals(null));
    }

    /**
     * Test hashCode()
     */
    @Test
    public void testHashCode() {
        for (int i = 0; i < testBlock.getSize(); i++) {
            testBlock.setPatch(testPatch, i);
        }
        final int testPatchHash = testPatch.hashCode();
        int testBlockHash = 19;
        for (int i = 0; i < testBlock.getSize(); i++) {
            testBlockHash *= testPatchHash + 3;
        }
        assertEquals("The hashcode for testBlock should be ...", testBlockHash, testBlock.hashCode());
    }

    /**
     * Test block cloning.
     */
    @Test
    public void testGetBlockClone() {
        final Block newBlock = testBlock.getBlockClone();
        final boolean isEqual = newBlock.equals(testBlock);
        assertTrue("testBlock should be equal to its clone", isEqual);
        assertFalse("Block clone should not reference the same object", 
                newBlock == testBlock);
    }

    /**
     * Test swapping of patches
     */
    @Test
    public void testPatchSwapAndReplace() {
        final Patch sansPatch = new Patch();
        final Patch triangle = Shape.HALFTRIANGLE.getPatch(Fabric.RED);
        final Patch triangle2 = Shape.HALFTRIANGLE.getPatch(Fabric.BLACK);
        sansPatch.setTile(1, Fabric.RED);
        sansPatch.setTile(12, Fabric.BLACK);
        testBlock.setPatch(testPatch, 1);
        testBlock.setPatch(sansPatch, 7);
        testBlock.swapPatchesInBlock(testPatch, sansPatch);
        assertTrue("This patch should be equal to fancyPatch", testBlock
                .getPatch(7).equals(testPatch));
        assertTrue("This patch should be equal to sansPatch", testBlock
                .getPatch(1).equals(sansPatch));
        testBlock.replacePatchInBlock(sansPatch, testPatch);
        assertTrue("This patch should be equal to fancyPatch", testBlock
                .getPatch(7).equals(testPatch));
        assertTrue("This patch should be equal to fancyPatch", testBlock
                .getPatch(1).equals(testPatch));
        testBlock.replacePatchInBlock(testPatch, sansPatch);
        assertTrue("This patch should be equal to sansPatch", testBlock
                .getPatch(7).equals(sansPatch));
        assertTrue("This patch should be equal to sansPatch", testBlock
                .getPatch(1).equals(sansPatch));
        testBlock = new Block();
        testBlock.setPatch(triangle, 5);
        testBlock.setPatch(triangle2, 6);
        testBlock.swapPatchesInBlock(triangle, triangle2);
        assertTrue("The patch at location 6 should be triangle", testBlock.getPatch(6).equals(triangle));
        assertTrue("The patch at location 5 should be triangle2", testBlock.getPatch(5).equals(triangle2));
        testBlock = new Block();
        testBlock.setPatch(triangle, 5);
        testBlock.setPatch(triangle2, 6);
        testBlock.replacePatchInBlock(triangle, triangle2);
        assertTrue("The patch at location 6 should be triangle2", testBlock.getPatch(6).equals(triangle2));
        assertTrue("The patch at location 5 should be triangle2", testBlock.getPatch(5).equals(triangle2));
    }

    /**
     * Test that when patches are swapped in the block, that rotations
     * of the same Patch are dealt with properly.
     * 
     */
    @Test
    public void testRotatedPatchSwapReplace(){
        testBlock = new Block(9);
        Patch triangle0 = Shape.HALFTRIANGLE.getPatch(Fabric.BLUE);
        Patch triangle1 = triangle0.getPatchClone();
        triangle1.rotateCW();
        Patch triangle2 = triangle1.getPatchClone();
        triangle2.rotateCW();
        Patch triangle3 = triangle2.getPatchClone();
        triangle3.rotateCW();
        Patch square0 = Shape.QUARTERSQUARE.getPatch(Fabric.GREEN);
        Patch square1 = square0.getPatchClone();
        square1.rotateCW();
        Patch square2 = square1.getPatchClone();
        square2.rotateCW();
        Patch square3 = square2.getPatchClone();
        square3.rotateCW();
        
        
        testBlock.setPatch(triangle0.getPatchClone(), 0);
        testBlock.setPatch(triangle1.getPatchClone(), 1);
        testBlock.setPatch(triangle2.getPatchClone(), 2);
        testBlock.setPatch(triangle3.getPatchClone(), 3);
        testBlock.setPatch(square0.getPatchClone(), 4);
        testBlock.setPatch(square1.getPatchClone(), 5);
        testBlock.setPatch(square2.getPatchClone(), 6);
        testBlock.setPatch(square3.getPatchClone(), 7);
        
        testBlock.swapPatchesInBlock(triangle0, square0);
        
        assertEquals("Patches weren't swapped",
                square0, testBlock.getPatch(0));
        assertEquals("Rotated patch wasn't swapped", 
                square1, testBlock.getPatch(1));
        assertEquals("Rotated patch wasn't swapped", 
                square2, testBlock.getPatch(2));
        assertEquals("Rotated patch wasn't swapped", 
                square3, testBlock.getPatch(3));
        assertEquals("Rotated patch wasn't swapped", 
                triangle0, testBlock.getPatch(4));
        assertEquals("Rotated patch wasn't swapped", 
                triangle1, testBlock.getPatch(5));
        assertEquals("Rotated patch wasn't swapped", 
                triangle2, testBlock.getPatch(6));
        assertEquals("Rotated patch wasn't swapped", 
                triangle3, testBlock.getPatch(7));
        assertEquals("Blank patch got changed during swap", 
                new Patch(), testBlock.getPatch(8));
        
        // Now we check that replacing works properly too
        
        
        testBlock.replacePatchInBlock(triangle0, square0);
        assertEquals("Patch was changed during replace",
                square0, testBlock.getPatch(0));
        assertEquals("Patch was changed during replace", 
                square1, testBlock.getPatch(1));
        assertEquals("Patch was changed during replace", 
                square2, testBlock.getPatch(2));
        assertEquals("Patch was changed during replace", 
                square3, testBlock.getPatch(3));
        assertEquals("Rotated patch wasn't replaced", 
                square0, testBlock.getPatch(4));
        assertEquals("Rotated patch wasn't replaced", 
                square1, testBlock.getPatch(5));
        assertEquals("Rotated patch wasn't replaced", 
                square2, testBlock.getPatch(6));
        assertEquals("Rotated patch wasn't replaced", 
                square3, testBlock.getPatch(7));
        assertEquals("Blank patch got changed during swap", 
                new Patch(), testBlock.getPatch(8));
        
    }
    
    /**
     * This test makes sure that when a Patch in the Block with rotational
     * symmetry is swapped or replaced, the replacing/swapping Patch is
     * not rotated.
     * 
     *  (The Block's Patch swap/replace methods do a cool thing, which
     * is taking into account Patches that are the same if rotated. However,
     * this used to cause weird things to happen if a Patch is the same <i>
     * each time</i> it is rotated, for example a solid square. The problem
     * was that it checked for 0, 90, 180, and 270 degrees in that order.
     * But with a solid square, the Patch would match each time and the end
     * result was that the new Patch would be -90 degrees off from what it
     * should have been, because the 270 degree check was last.)
     */
    @Test
    public void testSymmetricalSwapReplace(){
        Patch symmetricPatch = new Patch();
        symmetricPatch.setTile(3, Fabric.BROWN);
        symmetricPatch.setTile(5, Fabric.BROWN);
        symmetricPatch.setTile(10, Fabric.BROWN);
        symmetricPatch.setTile(12, Fabric.BROWN);
        
        Patch clone = symmetricPatch.getPatchClone();
        for (int i=0; i<4; i++){
            clone.rotateCW();
            for (int j=0; j<16; j++){
                assertEquals(symmetricPatch.getTile(j), clone.getTile(j));
            }
        }
        
        Patch triangle = Shape.HALFTRIANGLE.getPatch(Fabric.GREEN);
        
        for (int i=0; i<8; i++){
            testBlock.setPatch(symmetricPatch.getPatchClone(), i);
        }
        for (int i=8; i<16; i++){
            testBlock.setPatch(triangle.getPatchClone(), i);
        }
        
        testBlock.swapPatchesInBlock(symmetricPatch, triangle);
        
        for (int i=0; i<8; i++){
            assertEquals("This Patch should be a triangle", 
                    triangle, testBlock.getPatch(i));
        }
        for (int i=8; i<16; i++){
            assertEquals("This patch should be the symmetrical shape",
                    symmetricPatch, testBlock.getPatch(i));
        }
        
        
        testBlock.replacePatchInBlock(symmetricPatch, triangle);
        
        for (int i=0; i<16; i++){
            assertEquals("This patch should be a triangle",
                    triangle, testBlock.getPatch(i));
        }
    }
    
    
    /**
     * Test replacement of fabrics.
     */
    @Test
    public void testReplaceFabricInBlock() {
        final Block testBlockRed = new Block();
        final Block newBlock = new Block();
        final Patch redPatch = new Patch();
        for (int i = 0; i < 16; i++) {
            redPatch.setTile(i, Fabric.RED);
        }
        for (int i = 0; i < testBlockRed.getSize(); i++) {
            testBlockRed.setPatch(redPatch, i);
        }
        testBlockRed.replaceFabricInBlock(Fabric.RED, Fabric.TRANSPARENT);
        assertEquals("testBlockRed should become equal to a new block",
                newBlock, testBlockRed);
    }

    /**
     * Test swapping of fabrics.
     */
    @Test
    public void testSwapFabricInBlock() {
        final Block testBlockRedEven = new Block();
        final Block testBlockRedOdd = new Block();
        final Patch redPatch = new Patch();
        final Patch bluePatch = new Patch();
        for (int i = 0; i < 16; i++) {
            redPatch.setTile(i,Fabric.RED);
            bluePatch.setTile(i, Fabric.BLUE);
        }
        for (int i = 0; i < testBlock.getSize(); i++) {
            if (i % 2 == 0) {
                testBlockRedEven.setPatch(redPatch.getPatchClone(), i);
                testBlockRedOdd.setPatch(bluePatch.getPatchClone(), i);

            } else {
                testBlockRedEven.setPatch(bluePatch.getPatchClone(), i);
                testBlockRedOdd.setPatch(redPatch.getPatchClone(), i);
            }
        }
        testBlockRedEven.swapFabricInBlock(Fabric.RED, Fabric.BLUE);
        assertEquals(
                "testBlocks should be equal after swaping red and blue in testBlockRedEven",
                testBlockRedOdd, testBlockRedEven);
    }

    /**
     * Test that blocks can be cleared.
     */
    @Test
    public void testClearBlock() {
        final Block redTestBlock = new Block();
        final Patch redPatch = new Patch();
        final Block newBlock = new Block();
        for (int i = 0; i < 16; i++) {
            redPatch.setTile(i, Fabric.RED);
        }
        for (int i = 0; i < redTestBlock.getSize(); i++) {
            redTestBlock.setPatch(redPatch, i);
        }
        redTestBlock.clear();
        assertEquals("cleared block should be the same as a new block",
                redTestBlock, newBlock);

    }

    /**
     * Test fraction coverage of the block
     */
    @Test
    public void testGetBlockCoverage(){
        Fraction transparency = testBlock.getBlockCoverage(Fabric.TRANSPARENT);
        Fraction expected = new Fraction(1, 1);
        assertEquals("New block should report 100% transparency",expected,
                transparency);
        assertEquals("The actual fraction shouldn't be reduced", 
                256, transparency.getNumerator());
        assertEquals("The actual fraction shouldn't be reduced", 
                256, transparency.getDenominator());

        for (int i=0; i<testBlock.getSize(); i++){
            testBlock.setPatch(testPatch, i);
        }
        transparency = testBlock.getBlockCoverage(Fabric.TRANSPARENT);
        expected = new Fraction(1, 8);
        assertEquals("Block with test patch should be 1/8 transparent", 
                expected, transparency);
        Fraction yellow = testBlock.getBlockCoverage(Fabric.YELLOW);
        expected = new Fraction(1, 4);
        assertEquals("Yellow coverage should be 1/4", expected, yellow);
        assertEquals("The fraction shouldn't be reduced", 
                256, yellow.getDenominator());
        
        
        testBlock = new Block(9);
        testPatch = Shape.FULLSQUARE.getPatch(Fabric.RED);
        testBlock.setPatch(testPatch, 0);
        testBlock.setPatch(testPatch, 1);
        testBlock.setPatch(testPatch, 2);
        Fraction red = testBlock.getBlockCoverage(Fabric.RED);
        expected = new Fraction(1, 3);
        assertEquals("Fractions should work for thirds too", expected, red);
        assertEquals("The fraction shouldn't be reduced", 
                144, red.getDenominator());
        
    }
    
    /**
     * Test getSideSize()
     */
    @Test
    public void testGetSideSize(){
        testBlock = new Block(16);
        int sideSize = testBlock.getSideSize();
        assertEquals("Side size should be the square root of block size", 
                4, sideSize);
        
        testBlock = new Block(9);
        sideSize = testBlock.getSideSize();
        assertEquals("Side size should be the square root of block size", 
                3, sideSize);
        
        testBlock = new Block(4);
        sideSize = testBlock.getSideSize();
        assertEquals("Side size should be the square root of block size", 
                2, sideSize);
    }
    
    /**
     * 
     */
    @Test
    public void testIterator(){
        testBlock = createRandomBlock(16);
        
        Iterator<Patch> patches = testBlock.iterator();
        // Shouldn't be able to remove patches, either
        try {
            patches.remove();
            fail();
        } catch (UnsupportedOperationException e) {
            // This is correct
        }
        
        for (int i=0; i<16; i++){
            assertTrue(patches.hasNext());
            Patch expected = testBlock.getPatch(i);
            Patch actual = patches.next();
            assertTrue(expected == actual);
        }
        assertFalse(patches.hasNext());
        try {
            patches.next();
            fail();
        } catch (NoSuchElementException e){
            // Expected this, carry on
        }

        
    }
    
    /**
     * 
     */
    public void testTileIteration(){
        testBlock = createRandomBlock(16);
        
        Iterator<Fabric> allTiles = testBlock.allTiles().iterator();
        // Shouldn't be able to remove tiles w/ iterator:
        try {
            allTiles.remove();
            fail();
        } catch (UnsupportedOperationException e) {
            // This is correct
        }
        
        
        
        for (int i=0; i<16; i++){
            for (int j=0; j<Patch.MAXTILES; j++){
                assertTrue(allTiles.hasNext());
                Fabric expected = testBlock.getPatch(i).getTile(j);
                Fabric actual = allTiles.next();
                assertEquals(expected, actual);
            }
            
        }
        assertFalse(allTiles.hasNext());
        try {
            allTiles.next();
            fail();
        } catch (NoSuchElementException e){
            // Expected this, carry on
        }
        
        

    }

    /**
     * Create a completely random block.
     * @param size The size of the block (16, 9, 4)
     * @return the new random block
     */
    public static Block createRandomBlock(int size) {
        Block newBlock = new Block(size);
        for (int i=0; i<size; i++){
            Fabric[] tiles = new Fabric[Patch.MAXTILES];
    
            for (int j=0; j<Patch.MAXTILES; j++){
                tiles[j] = getRandomFabric();
            }
            newBlock.setPatch(new Patch(tiles), i);
    
        }
        return newBlock;
    }
    

    /**
     * @return a random fabric from the Fabric enum.
     */
    public static Fabric getRandomFabric(){
        Random random = new Random();
        return Fabric.values()[random.nextInt(Fabric.values().length)];
    }
    
}
