/*
 * Created by jbiatek on Sep 12, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;


import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.BlockTest;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.PatchViewer;



/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class BlockViewerTest {


    /**
     * FEST fixture. Automatically cleaned up after every test, so make
     * sure you use it.
     */
    FrameFixture window;


    /**
     * Test a 2x2 random block.
     * 
     * @throws Exception
     */
    @Test
    public void testSizeFour() throws Exception{
        testRandomBlock(4);
    }

    /**
     * Test a 3x3 random block.
     * 
     * @throws Exception
     */
    @Test
    public void testSizeNine() throws Exception{
        testRandomBlock(9);
    }
    
    /**
     * Test a 4x4 random block.
     * 
     * @throws Exception
     */
    @Test
    public void testSizeSixteen() throws Exception {
        testRandomBlock(16);
    }

    /**
     * Create randomly sized blocks and test them.
     * @param size 
     * 
     * @throws Exception
     */
    public void testRandomBlock(int size) throws Exception{
        Block testBlock = BlockTest.createRandomBlock(size);
        Random random = new Random();
        int patchSize = random.nextInt(143) + 8; // 8 <= patchSize <= 150
        final BlockViewer testViewer = new BlockViewer(testBlock, patchSize);
        testViewer.setName("Test viewer");
        testViewer.setOpaque(false);
        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                jframe.setLayout(new GridBagLayout());
                jframe.add(testViewer);
                jframe.validate();
                return jframe;  
            }
        });

        window = new FrameFixture(frame);
        window.show(); // shows the frame to test

        checkBlock(testBlock, window.panel("Test viewer"), patchSize);


        int sideSize = (int) (patchSize * Math.sqrt(size));
        BufferedImage img = new BufferedImage(
                sideSize, sideSize, BufferedImage.TRANSLUCENT);
        Graphics g = img.getGraphics();
        testViewer.paintComponents(g);
        checkBlockImage(testBlock, img, patchSize);

    }


    /**
     * Clean up FEST
     */
    @After
    public void cleanUp(){
        window.cleanUp();
    }

    /**
     * Check the given BlockViewer fixture against the given Block, using
     * the given patch size. This only makes sure that the BlockViewer has
     * properly set up the PatchViewers, so if something is wrong with how
     * they draw their Patches then it won't be caught. That's the 
     * PatchViewerTest's job.
     * 
     * @param correctBlock
     * @param viewer
     * @param patchSize
     */
    public static void checkBlock(Block correctBlock, JPanelFixture viewer, 
            int patchSize) {
        // We'll go patch by patch and check each viewer
        for (int i=0; i< correctBlock.getSize(); i++){
            PatchViewer patchV = viewer.panel("Patch"+i)
            .targetCastedTo(PatchViewer.class);
            // Check dimensions
            assertEquals(new Dimension(patchSize, patchSize),
                    patchV.getSize());
            // Check location
            Point position = patchV.getLocation();
            int sideSize = correctBlock.getSideSize();
            int x = (i % sideSize) * patchSize;
            int y = (i / sideSize) * patchSize;

            Point expected = new Point(x, y);
            assertEquals(expected, position);

            // Check the patch itself
            Patch shownPatch = patchV.getPatch();
            assertEquals(correctBlock.getPatch(i), shownPatch);
        }
    }

    /**
     * Verify that an image matches the given Block.
     * 
     * @param expected
     * @param view
     * @param patchSize
     */
    public static void checkBlockImage(
            Block expected, BufferedImage view, int patchSize){
        int expectedSize = expected.getSideSize() * patchSize;
        assertEquals(expectedSize, view.getWidth());
        assertEquals(expectedSize, view.getHeight());


        for (int i=0; i<expected.getSize(); i++){
            int x = getX(i, patchSize, expected.getSideSize());
            int y = getY(i, patchSize, expected.getSideSize());

            BufferedImage patchImg = 
                view.getSubimage(x, y, patchSize, patchSize);

            PatchViewerTest.checkImage(
                    expected.getPatch(i),
                    patchImg, 
                    patchSize);
        }
    }

    /**
     * Get the x coordinate for the location of a Patch.
     * 
     * @param patchNum
     * @param patchSize
     * @param blockSize
     * @return x coordinate
     */
    private static int getX(int patchNum, int patchSize, int blockSize){
        return (patchNum % blockSize) * patchSize;
    }

    /**
     * Get the Y coordinate for the location of a Patch.
     * 
     * @param patchNum
     * @param patchSize
     * @param blockSize
     * @return y coordinate
     */
    private static int getY(int patchNum, int patchSize, int blockSize){
        return (patchNum / blockSize) * patchSize;
    }

}
