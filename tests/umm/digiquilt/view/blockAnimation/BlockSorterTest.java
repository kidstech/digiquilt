/*
 * Created by jbiatek on Nov 12, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.blockAnimation;


import static org.junit.Assert.*;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.Patch;
import umm.digiquilt.view.blockAnimation.BlockSortListener;
import umm.digiquilt.view.blockAnimation.BlockSorter;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class BlockSorterTest {

    /**
     * The FEST fixture. This will get cleaned up after each test,
     * so make sure to use it.
     */
    FrameFixture window;

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        window.cleanUp();
    }

    @Test
    public void testTwoByTwoHalves(){

        Block block = createRandomSortBlock(4, 2);
        Grid grid = new Grid(0, 0, 0, 2);
        int patchSize = 100;
        
        final BlockSorter testSorter = new BlockSorter(block, grid, patchSize);

        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                jframe.setLayout(new GridBagLayout());
                jframe.add(testSorter);
                jframe.validate();
                return jframe;  
            }
        });

        window = new FrameFixture(frame);
        window.show();
    }
    
    
    private class BlockTester implements BlockSortListener {

        boolean testedSorted = false;
        
        boolean testedUnsorted = false;
        
        /* (non-Javadoc)
         * @see umm.softwaredevelopment.digiquilt.view.blockAnimation.BlockSortListener#animationComplete(boolean)
         */
        public void animationComplete(boolean nowSorted) {
            // TODO Auto-generated method stub
            
        }
        
    }



    /**
     * Create a block of the specified size, with equal amounts of random
     * colors.
     * 
     * @param blockSize
     * @param colors The number of colors to use.
     * @return the new block.
     */
    @SuppressWarnings("boxing")
    private static Block createRandomSortBlock(int blockSize, int colors){
        Block newBlock = new Block(blockSize);
        // Pick n random colors
        Random r = new Random();
        Map<Fabric, Integer> fabrics = new HashMap<Fabric, Integer>();
        while (fabrics.keySet().size() < colors){
            int i = r.nextInt(Fabric.values().length);
            fabrics.put(Fabric.values()[i], blockSize*16 / colors);
        }

        List<Fabric> fabricList = new ArrayList<Fabric>(fabrics.keySet());
        for (int i=0; i< blockSize; i++){
            Fabric[] patch = new Fabric[Patch.MAXTILES];
            for (int j=0; j<Patch.MAXTILES; j++){

                int randomNum = r.nextInt(fabricList.size());
                Fabric randomFabric = fabricList.get(randomNum);
                patch[j] = randomFabric;

                int remaining = fabrics.get(randomFabric);
                // We just used one more than this
                remaining--;
                if (remaining == 0){
                    fabricList.remove(randomFabric);
                }
                fabrics.put(randomFabric, remaining);

            }
            newBlock.setPatch(new Patch(patch), i);
        }
        
        Fraction desiredFraction = new Fraction(1, colors);
        for (Fabric fabric : fabrics.keySet()){
            assertEquals("This method fails its purpose", 
                    desiredFraction, newBlock.getBlockCoverage(fabric));
        }
        
        return newBlock;

    }

}
