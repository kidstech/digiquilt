/*
 * Created by mitchella on Mar 21, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;

/**
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         mitchella $ on $Date: 2009-06-21 23:41:03 $
 * @version $Revision: 1.11 $
 */
public class PatchTest {

    /**
     * Message for assertions
     */
    private static final String AFTER_ROTATING = 
        "After rotating, the Fabric should be ";

    /**
     * Test the patch constructor
     */
    @Test
    public void testConstructor() {
        final Patch mergePatch = new Patch();
        for (int i = 0; i < 16; i++) {
            assertEquals("The color should be TRANSPARENT", Fabric.TRANSPARENT,
                    mergePatch.getTile(i));
        }
        final Patch patchToMerge = new Patch();
        patchToMerge.setTile(3, Fabric.BLACK);
        mergePatch.mergePatch(patchToMerge, 0.0, 0.0);
        assertEquals("The third tile should be black", mergePatch.getTile(3)
                , Fabric.BLACK);
        patchToMerge.mergePatch(new Patch(), 0.0, 0.0);
        assertEquals("The third tile should be black", patchToMerge.getTile(3)
                , Fabric.BLACK);
    }

    /**
     * Test mergePatch()
     */
    @Test
    public void testMergePatch(){
        Patch oneQuadrant = new Patch();
        oneQuadrant.setTile(12, Fabric.BLACK);
        oneQuadrant.setTile(13, Fabric.BLACK);
        oneQuadrant.setTile(14, Fabric.BLACK);
        oneQuadrant.setTile(15, Fabric.BLACK);

        // Dropping this patch should have the same result, even if it is
        // rotated, as long as the drop position (25%, 25%) remains the same
        for (int rotation=0; rotation<4; rotation++){
            Patch blankPatch = new Patch();
            blankPatch.mergePatch(oneQuadrant, .25, .25);
            for (int i=0; i<4; i++){
                assertEquals("Patch did not get dropped properly on rotation "+rotation,
                        Fabric.BLACK, blankPatch.getTile(i));
            }
            for (int i=4; i<Patch.MAXTILES; i++){
                assertEquals("Patch did not get dropped properly",
                        Fabric.TRANSPARENT, blankPatch.getTile(i));
            }
            oneQuadrant.rotateCW();
        }

        // Same test, trying it on the opposite side of the Patch
        for (int rotation=0; rotation<4; rotation++){
            Patch blankPatch = new Patch();
            blankPatch.mergePatch(oneQuadrant, .75, .75);
            for (int i=0; i<12; i++){
                assertEquals("Patch did not get dropped properly",
                        Fabric.TRANSPARENT, blankPatch.getTile(i));
            }
            for (int i=12; i<16; i++){
                assertEquals("Patch did not get dropped properly",
                        Fabric.BLACK, blankPatch.getTile(i));
            }
            oneQuadrant.rotateCW();
        }


        // Test that transparency is dealt with properly, as well as testing
        // rectangular small shapes
        Patch twoQuadrants = new Patch(new Fabric[]{
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.BLACK, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.BLACK,
                Fabric.BLACK, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.BLACK
        });
        Patch yellowPatch = new Patch();
        for (int i=0; i<Patch.MAXTILES; i++){
            yellowPatch.setTile(i, Fabric.YELLOW);
        }

        yellowPatch.mergePatch(twoQuadrants, .25, .25);

        Patch expected = new Patch(new Fabric[]{
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, 
                Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW, Fabric.YELLOW
        });

        assertEquals("Semi-transparent rectangle wasn't merged correctly", 
                expected, yellowPatch);

        // Do another merge on the bottom half
        yellowPatch.mergePatch(twoQuadrants, .75, .75);

        expected = new Patch(new Fabric[]{
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK
        });
        assertEquals("Semi-transparent rectangle wasn't merged correctly", 
                expected, yellowPatch);

        twoQuadrants.rotateCW();

        yellowPatch.mergePatch(twoQuadrants, .25, .75);
        expected = new Patch(new Fabric[]{
                Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, 
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK, 
                Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, Fabric.BLACK,
                Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK 
        });

        assertEquals("Semi-transparent rectangle wasn't merged correctly", 
                expected, yellowPatch);


        yellowPatch.mergePatch(twoQuadrants, .75, .25);
        for (int i=0; i<Patch.MAXTILES; i++){
            expected.setTile(i, Fabric.BLACK);
        }

        assertEquals("Semi-transparent rectangle wasn't merged correctly", 
                expected, yellowPatch);


        // Test that a Patch with an empty first quadrant doesn't erroneously
        // get shifted if the third quadrant is full

        Patch threeQuadrants = new Patch();
        for (int i=4; i<Patch.MAXTILES; i++){
            threeQuadrants.setTile(i, Fabric.GREEN);
        }
        Patch bluePatch = new Patch();
        for (int i=0; i<Patch.MAXTILES; i++){
            bluePatch.setTile(i, Fabric.BLUE);
        }

        bluePatch.mergePatch(threeQuadrants, 0, 0);
        expected = new Patch(new Fabric[]{
                Fabric.BLUE, Fabric.BLUE, Fabric.BLUE, Fabric.BLUE, 
                Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, 
                Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, 
                Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, Fabric.GREEN, 
        });

        assertEquals("Patch with 3 quadrants full was not merged correctly", 
                expected, bluePatch);

    }

    /**
     * Test clockwise rotation
     */
    @Test
    public void testRotateCW() {
        final Patch patch = PatchTest.makeFancyTestPatch();
        patch.rotateCW();
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(0));
        assertEquals(AFTER_ROTATING + "black", Fabric.BLACK, patch.getTile(1));
        assertEquals(AFTER_ROTATING + "black", Fabric.BLACK, patch.getTile(2));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(3));
        assertEquals(AFTER_ROTATING + "blue", Fabric.BLUE, patch.getTile(4));
        assertEquals(AFTER_ROTATING + "red", Fabric.RED, patch.getTile(5));
        assertEquals(AFTER_ROTATING + "blue", Fabric.BLUE, patch.getTile(6));
        assertEquals(AFTER_ROTATING + "red", Fabric.RED, patch.getTile(7));
        assertEquals(AFTER_ROTATING + "orange", Fabric.ORANGE, patch.getTile(8));
        assertEquals(AFTER_ROTATING + "transparent", Fabric.TRANSPARENT, patch.getTile(9));
        assertEquals(AFTER_ROTATING + "transparent", Fabric.TRANSPARENT, patch.getTile(10));
        assertEquals(AFTER_ROTATING + "orange", Fabric.ORANGE, patch.getTile(11));
        assertEquals(AFTER_ROTATING + "green", Fabric.GREEN, patch.getTile(12));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(13));
        assertEquals(AFTER_ROTATING + "green", Fabric.GREEN, patch.getTile(14));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(15));
    }

    /**
     * Test counter clockwise rotation
     */
    @Test
    public void testRotateCCW() {
        final Patch patch = PatchTest.makeFancyTestPatch();
        patch.rotateCCW();
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(0));
        assertEquals(AFTER_ROTATING + "green", Fabric.GREEN, patch.getTile(1));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(2));
        assertEquals(AFTER_ROTATING + "green", Fabric.GREEN, patch.getTile(3));
        assertEquals(AFTER_ROTATING + "orange", Fabric.ORANGE, patch.getTile(4));
        assertEquals(AFTER_ROTATING + "transparent", Fabric.TRANSPARENT, patch.getTile(5));
        assertEquals(AFTER_ROTATING + "transparent", Fabric.TRANSPARENT, patch.getTile(6));
        assertEquals(AFTER_ROTATING + "orange", Fabric.ORANGE, patch.getTile(7));
        assertEquals(AFTER_ROTATING + "red", Fabric.RED, patch.getTile(8));
        assertEquals(AFTER_ROTATING + "blue", Fabric.BLUE, patch.getTile(9));
        assertEquals(AFTER_ROTATING + "red", Fabric.RED, patch.getTile(10));
        assertEquals(AFTER_ROTATING + "blue", Fabric.BLUE, patch.getTile(11));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(12));
        assertEquals(AFTER_ROTATING + "black", Fabric.BLACK, patch.getTile(13));
        assertEquals(AFTER_ROTATING + "black", Fabric.BLACK, patch.getTile(14));
        assertEquals(AFTER_ROTATING + "yellow", Fabric.YELLOW, patch.getTile(15));
    }

    //  public void testGenerateImage() {
    //  // Create two patches that should be equal
    //  Patch patch2 = new Patch();
    //  Patch patch1 = patch2.getPatchClone();
    //  // Change the same part of each patch
    //  patch1.setTile(4, FabricTile.BLACK);
    //  patch2.setTile(4, FabricTile.BLACK);
    //  // Generate the images for these patches and verify they are equal
    //  Image patch1Img = patch1.generateImage();
    //  //    	Image patch2Img = patch1Img;
    //  Image patch2Img = patch1.generateImage();
    //  boolean equal = patch1Img.equals(patch2Img);
    //  //    	assertEquals("The images for patch1 and patch2 should be equal", patch1Img, patch2Img);
    //  assertTrue("The images for patch1 and patch2 should be equal", equal);
    //  }


    /**
     * Test setTile()
     */
    @Test
    public void testSetTile() {
        final Patch patch = new Patch();
        for (int i = 0; i < 16; i++) {
            patch.setTile(i, Fabric.RED);
        }
        for (int i = 0; i < 16; i++) {
            assertEquals("The FabricTile at index " + i + " should be redTile",
                    Fabric.RED, patch.getTile(i));
        }

        boolean threwException = false;
        try {
            patch.setTile(90, Fabric.TRANSPARENT);
        } catch (IndexOutOfBoundsException e) {
            threwException = true;
        }
        assertTrue(
                "Trying to set a tile at an invalid index should throw an exception",
                threwException);
    }

    /**
     * Test getTile()
     */
    @Test
    public void testGetTile() {
        final Patch patch = new Patch();
        for (int i = 0; i < 16; i++) {
            assertEquals("The FabricTile at index " + i
                    + " should be the transparent FabricTile",
                    Fabric.TRANSPARENT, patch.getTile(i)
            );
        }

        boolean threwException = false;
        try {
            patch.getTile(90);
        } catch (IndexOutOfBoundsException e) {
            threwException = true;
        }
        assertTrue(
                "Trying to add a tile to an invalid index of patch should throw an exception",
                threwException);
    }

    /**
     * Test the patch's equals() method
     */
    @Test
    public void testEquality() {
        final Patch equalsTester = new Patch();
        assertEquals("equalsTester should equal a new Patch", equalsTester,
                new Patch());
        assertEquals("Equal patches should have the same hashcode", 
                equalsTester.hashCode(), new Patch().hashCode());
        equalsTester.setTile(5, Fabric.BLACK);
        assertFalse("equalsTester should not equal a new Patch", (equalsTester
                .equals(new Patch())));
        assertFalse("\"Zimbabwe\" should not equal equalsTester", (equalsTester
                .equals("Zimbabwe")));
        assertFalse("Null should not equal a patch", 
                equalsTester.equals(null));
        assertFalse("equalsTester shouldn't equal fancy patch", equalsTester
                .equals(makeFancyTestPatch()));
    }


    /**
     * Test replacing fabrics
     */
    @Test
    public void testReplaceFabrics() {
        Patch testPatch1 = new Patch(new Fabric[]{
                Fabric.BLACK, Fabric.YELLOW, Fabric.BLACK, Fabric.YELLOW, 
                Fabric.BLACK, Fabric.YELLOW, Fabric.BLACK, Fabric.YELLOW, 
                Fabric.BLACK, Fabric.RED, Fabric.BLACK, Fabric.RED, 
                Fabric.BLACK, Fabric.RED, Fabric.BLACK, Fabric.RED
        });
        Patch expected1 = new Patch(new Fabric[]{
                Fabric.BLACK, Fabric.YELLOW, Fabric.BLACK, Fabric.YELLOW,
                Fabric.BLACK, Fabric.YELLOW, Fabric.BLACK, Fabric.YELLOW,
                Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, 
                Fabric.BLACK, Fabric.BLACK, Fabric.BLACK, Fabric.BLACK
        });

        Patch allBlack = new Patch();
        for (int i=0; i<Patch.MAXTILES; i++){
            allBlack.setTile(i, Fabric.BLACK);
        }


        testPatch1.replaceFabrics(Fabric.RED, Fabric.BLACK);
        assertEquals("Red wasn't replaced with black", expected1, testPatch1);

        testPatch1.replaceFabrics(Fabric.YELLOW, Fabric.BLACK);
        assertEquals("Yellwo wasn't replaced with black", allBlack, testPatch1);


    }

    /**
     * Test swapping of fabrics
     */
    @Test
    public void testSwapFabrics() {
        Patch swapTester = new Patch();
        // Create a half green half red patch
        for (int i = 0; i < 8; i++) {
            swapTester.setTile(i, Fabric.RED);
        }
        for (int i = 8; i < 16; i++) {
            swapTester.setTile(i, Fabric.GREEN);
        }
        // Instead of making a patch that is set to be red and green in the opposite order, we can just rotate the first patch 180 degrees
        final Patch swapped = swapTester.getPatchClone();
        swapped.rotateCW(); swapped.rotateCW();
        swapTester.swapFabrics(Fabric.RED, Fabric.GREEN);
        assertEquals("Swapping the red and green tiles of a half and half patch should make it equal to a patch half and half the other way", swapTester, swapped);
        final Patch greenPatch = new Patch();
        for (int i = 0; i < 16; i++) {
            greenPatch.setTile(i, Fabric.GREEN);
        }
        // Swap again to get back to original state
        swapTester.swapFabrics(Fabric.RED, Fabric.GREEN);

        Patch halfBlueGreen = new Patch();
        for (int i = 0; i < 8; i++) {
            halfBlueGreen.setTile(i, Fabric.BLUE);
        }
        for (int i = 8; i < 16; i++) {
            halfBlueGreen.setTile(i, Fabric.GREEN);
        }
        swapTester.swapFabrics(Fabric.BLUE, Fabric.RED);
        assertEquals("Swapping red with blue (a non-present color) should result in a half blue half green patch", swapTester, halfBlueGreen);
    }

    /**
     * Test the getSolidColor() method
     */
    @Test
    public void testGetSolidColor(){
        Patch testPatch = new Patch();
        assertEquals("Solid color should be transparent", 
                testPatch.getSolidColor(), Fabric.TRANSPARENT);

        testPatch.setTile(3, Fabric.RED);
        assertEquals("Solid color should now be red", 
                testPatch.getSolidColor(), Fabric.RED);

        testPatch.setTile(5, Fabric.BLUE);
        assertEquals("Solid color should be transparent again", 
                testPatch.getSolidColor(), Fabric.TRANSPARENT);

        testPatch.setTile(3, Fabric.BLUE);
        assertEquals("Solid color should now be blue", 
                testPatch.getSolidColor(), Fabric.BLUE);
    }

    /**
     * Test getFabricList()
     */
    @Test
    public void testGetFabricList(){
        String[] blankList = new String[]{
                "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", 
                "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", 
                "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", 
                "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT"
        };
        String[] fancyList = new String[] {"BLUE", "BLUE", "RED", "RED",
                "GREEN", "GREEN", "YELLOW", "YELLOW",
                "BLACK", "YELLOW", "YELLOW", "BLACK",
                "TRANSPARENT", "ORANGE", "ORANGE", "TRANSPARENT"};
        String[] rainbowList = new String[]{
                "REDVIOLET", "RED", "ORANGE", "YELLOW", 
                "GREEN", "DARKGREEN", "BLUE", "INDIGO", 
                "VIOLET", "PINK", "WHITE", "BLACK",
                "BROWN", "INDIGO", "BLACK", "TRANSPARENT"};

        Patch blankPatch = new Patch();
        String[] blankTest = blankPatch.getFabricList();
        assertEquals("Fabric list had wrong length", 
                blankList.length, blankTest.length);
        for (int i=0; i< Patch.MAXTILES; i++){
            assertEquals("Fabric list was incorrect", 
                    blankList[i], blankTest[i]);
        }

        Patch fancyPatch = makeFancyTestPatch();
        String[] fancyTest = fancyPatch.getFabricList();
        assertEquals("Fabric list had wrong length", 
                fancyList.length, fancyTest.length);
        for (int i=0; i< Patch.MAXTILES; i++){
            assertEquals("Fabric list was incorrect", 
                    fancyList[i], fancyTest[i]);
        }

        Patch rainbowPatch = new Patch();
        rainbowPatch.setTile(0, Fabric.REDVIOLET);
        rainbowPatch.setTile(1, Fabric.RED);
        rainbowPatch.setTile(2, Fabric.ORANGE);
        rainbowPatch.setTile(3, Fabric.YELLOW);
        rainbowPatch.setTile(4, Fabric.GREEN);
        rainbowPatch.setTile(5, Fabric.DARKGREEN);
        rainbowPatch.setTile(6, Fabric.BLUE);
        rainbowPatch.setTile(7, Fabric.INDIGO);
        rainbowPatch.setTile(8, Fabric.VIOLET);
        rainbowPatch.setTile(9, Fabric.PINK);
        rainbowPatch.setTile(10, Fabric.WHITE);
        rainbowPatch.setTile(11, Fabric.BLACK);
        rainbowPatch.setTile(12, Fabric.BROWN);
        rainbowPatch.setTile(13, Fabric.INDIGO);
        rainbowPatch.setTile(14, Fabric.BLACK);
        rainbowPatch.setTile(15, Fabric.TRANSPARENT);

        String[] rainbowTest = rainbowPatch.getFabricList();
        assertEquals("Fabric list had wrong length", 
                rainbowList.length, rainbowTest.length);
        for (int i=0; i< Patch.MAXTILES; i++){
            assertEquals("Fabric list was incorrect", 
                    rainbowList[i], rainbowTest[i]);
        }

    }

    /**
     * Test getPatchCoverage()
     */
    @Test
    public void testPatchCoverage(){
        Patch halfRed = new Patch(
                new Fabric[]{Fabric.RED, Fabric.RED, Fabric.RED, Fabric.RED,
                        Fabric.RED, Fabric.RED, Fabric.RED, Fabric.RED,
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT,
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT
                }
        );

        assertEquals("Patch should have 8 red tiles", 
                8, halfRed.getPatchCoverage(Fabric.RED));
        assertEquals("Patch should have 8 transparent tiles",
                8, halfRed.getPatchCoverage(Fabric.TRANSPARENT));

        Patch fancyPatch = makeFancyTestPatch();
        assertEquals("Fancy patch should have 2 blue tiles",
                2, fancyPatch.getPatchCoverage(Fabric.BLUE));
        assertEquals("Fancy patch should have 2 red tiles",
                2, fancyPatch.getPatchCoverage(Fabric.RED));
        assertEquals("Fancy patch should have 2 green tiles",
                2, fancyPatch.getPatchCoverage(Fabric.GREEN));
        assertEquals("Fancy patch should have 4 yellow tiles",
                4, fancyPatch.getPatchCoverage(Fabric.YELLOW));
        assertEquals("Fancy patch should have 2 black tiles",
                2, fancyPatch.getPatchCoverage(Fabric.BLACK));
        assertEquals("Fancy patch should have 2 transparent tiles",
                2, fancyPatch.getPatchCoverage(Fabric.TRANSPARENT));
        assertEquals("Fancy patch should have 2 orange tiles",
                2, fancyPatch.getPatchCoverage(Fabric.ORANGE));

    }

    /**
     * Test getWidth() and getHeight()
     */
    @Test
    public void testGetWidthAndHeight(){
        Patch testPatch = new Patch();
        assertEquals("Width of new patch should be 0", 0, testPatch.getWidth());
        assertEquals("Height of new patch should be 0", 0, testPatch.getHeight());


        testPatch.setTile(12, Fabric.RED);
        assertEquals("Width should be 1", 1, testPatch.getWidth());
        assertEquals("Height should be 1", 1, testPatch.getHeight());

        testPatch.setTile(8, Fabric.BLUE);
        assertEquals("Width should be 2", 2, testPatch.getWidth());
        assertEquals("Height should be 1", 1, testPatch.getHeight());

        testPatch.setTile(0, Fabric.GREEN);
        assertEquals("Width should be 2", 2, testPatch.getWidth());
        assertEquals("Height should be 2", 2, testPatch.getHeight());

        testPatch.setTile(12, Fabric.TRANSPARENT);
        assertEquals("Width should be 1", 1, testPatch.getWidth());
        assertEquals("Height should be 2", 2, testPatch.getHeight());

        testPatch.setTile(8, Fabric.TRANSPARENT);
        assertEquals("Width should be 1", 1, testPatch.getWidth());
        assertEquals("Height should be 1", 1, testPatch.getHeight());


    }

    /**
     * Test the getSmallPatch() method.
     */
    @Test
    public void testGetSmallPatch(){
        Patch quarterPatch = new Patch();
        quarterPatch.setTile(12, Fabric.BROWN);
        quarterPatch.setTile(13, Fabric.BROWN);
        quarterPatch.setTile(14, Fabric.BROWN);
        quarterPatch.setTile(15, Fabric.BROWN);
        for (int rotation=0; rotation<4; rotation++){
            Patch smallPatch = quarterPatch.getSmallPatch();
            for (int i=0; i<4; i++){
                assertEquals("Small patch didn't get shrunken down",
                        Fabric.BROWN, smallPatch.getTile(i));
            }
            for (int i=4; i<Patch.MAXTILES; i++){
                assertEquals("Small patch wasn't created properly", 
                        Fabric.TRANSPARENT, smallPatch.getTile(i));
            }
            quarterPatch.rotateCW();
        }
        
        Patch tallRectangle = new Patch(new Fabric[]{
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.RED, Fabric.RED, Fabric.YELLOW, Fabric.RED, 
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT,
                Fabric.ORANGE, Fabric.RED, Fabric.RED, Fabric.RED
        });
        Patch expected = new Patch(new Fabric[]{
                Fabric.RED, Fabric.RED, Fabric.YELLOW, Fabric.RED, 
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT,
                Fabric.ORANGE, Fabric.RED, Fabric.RED, Fabric.RED,
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT
        });
        assertEquals("Tall rectangle was incorrect", expected, 
                tallRectangle.getSmallPatch());
        assertEquals("This patch should not have changed", expected,
                expected.getSmallPatch());
        
        
        Patch wideRectangle = new Patch(new Fabric[]{
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT,
                Fabric.RED, Fabric.GREEN, Fabric.RED, Fabric.RED, 
                Fabric.RED, Fabric.RED, Fabric.BLUE, Fabric.RED
        });
        expected = new Patch(new Fabric[]{
                Fabric.RED, Fabric.GREEN, Fabric.RED, Fabric.RED, 
                Fabric.RED, Fabric.RED, Fabric.BLUE, Fabric.RED,
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT
        });
        assertEquals("Wide rectangle was incorrect", 
                expected, wideRectangle.getSmallPatch());
        
        
        Patch trickyPatch = new Patch(new Fabric[]{
                Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                Fabric.RED, Fabric.ORANGE, Fabric.YELLOW, Fabric.GREEN, 
                Fabric.BLUE, Fabric.INDIGO, Fabric.VIOLET, Fabric.BLACK, 
                Fabric.BROWN, Fabric.WHITE, Fabric.BLACK, Fabric.REDVIOLET
        });
        expected = trickyPatch.getPatchClone();
        assertEquals("Tricky patch should *not* have changed", 
                expected, trickyPatch.getSmallPatch());        
    }


    /**
     * Construct the fancy test patch. This arguably should be in PatchTest
     * since it's a _test_ object, but since it's also referred to in
     * DigiQuiltFrame we're stuck putting it in the production code for now.
     * Moving it back into the test code later would be a good idea, though,
     * when we no longer need it in DigiQuiltFrame.
     * 
     * @return the fancy test Patch
     */
    public static Patch makeFancyTestPatch() {
        final Patch result = new Patch(
                new Fabric[] {Fabric.BLUE, Fabric.BLUE, Fabric.RED, Fabric.RED,
                        Fabric.GREEN, Fabric.GREEN, Fabric.YELLOW, Fabric.YELLOW,
                        Fabric.BLACK, Fabric.YELLOW, Fabric.YELLOW, Fabric.BLACK,
                        Fabric.TRANSPARENT, Fabric.ORANGE, Fabric.ORANGE, Fabric.TRANSPARENT});
        return result;
    }

}
