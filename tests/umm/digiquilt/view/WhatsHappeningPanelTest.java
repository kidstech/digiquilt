package umm.digiquilt.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.JFrame;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.timing.Timeout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.BlockTest;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.UndoRedoStack;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.WhatsHappeningPanel;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class WhatsHappeningPanelTest {


    /**
     * FEST frame fixture for the test panel
     */
    private FrameFixture window;

    /**
     * Test panel 
     */
    private WhatsHappeningPanel panel;
    
    /**
     * Temporary working directory.
     */
    private File tempDir;

    /**
     * How many items in the What's Happening bar to expect
     */
    private static final int NUM_ITEMS = 4;

    /**
     * The number of blocks to create during testing. This should be
     * greater than the number of items expected above, otherwise this
     * test will not be covering everything.
     */
    private static final int NUM_BLOCKS_TO_CREATE = 7;
    
    /**
     * The Blocks that are shown should be 32x32.
     */
    private static final int EXPECTED_BLOCK_SIZE = 48;
    
    private final BlockWorks mockBW = mock(BlockWorks.class);
    private final SaveHandler mockHandler = mock(SaveHandler.class);
    private final ComboBoxModel mockModel = mock(ComboBoxModel.class);
    
    /**
     * Set up the frame fixtures, etc.
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        
        tempDir = File.createTempFile("whatshappening", ".temp");
        tempDir.delete();
        tempDir.mkdir();

        when(mockHandler.getSaveDirectory()).thenReturn(tempDir);
        
        
        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                panel = new WhatsHappeningPanel(mockBW, mockHandler, mockModel, NUM_ITEMS);
                jframe.setLayout(new GridBagLayout());
                jframe.add(panel);
                return jframe;  
            }
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }

    /**
     * Clean up afterwards
     */
    @After
    public void tearDown(){
        window.cleanUp();
        //rm(tempDir);
    }
    
    /**Delete a file. If it's a directory, recursively delete it.
     * @param toRemove
     */
    private static void rm(File toRemove){
        if (toRemove.isDirectory()){
            File[] contents = toRemove.listFiles();
            for (File file : contents){
                if (file.isDirectory()){
                    rm(file);
                } else {
                    file.delete();
                }
            }
        }
        toRemove.delete();
    }

    /**
     * Save items and poke the bar, and it should respond properly
     * 
     * @throws Exception
     */
    @Test
    public void testAddingItems() throws Exception {
        int numComponents = panel.getComponents().length;
        assertEquals("Bar shouldn't have any components yet",
                0, numComponents);
        Block[] testBlocks = new Block[NUM_BLOCKS_TO_CREATE];

        for (int i=0; i<NUM_BLOCKS_TO_CREATE; i++){
            // Create a random block
            testBlocks[i] = BlockTest.createRandomBlock(
                    new int[]{4, 9, 16}[i%3]);
            SaveBlockXML xml1 = new SaveBlockXML(
                    testBlocks[i], new Grid(), new UndoRedoStack(), 
                    "Name", "Test block", 0,
                    "Notes for block "+i,
                    new FreeformChallenge("Challenge for block "+i));
            // Save it to the temp directory
            xml1.writeOutDocumentToFile(
                    new File(tempDir, "file "+i+".xml.gz"));
            
            
            // Notify the panel of changes:
            SwingUtilities.invokeAndWait(new Runnable(){

                public void run() {
                    panel.onSynchronize(mockHandler);
                    window.component().pack();
                }
                
            });
            
            
            
            
            // Now we check what it's got
            int expectedComponents = Math.min(i+1, NUM_ITEMS);
            Component[] children = panel.getComponents();
            // Should have up to NUM_ITEMS components at this point
            assertEquals(expectedComponents, children.length);
            // Okay, now we want to make sure that in this array they're
            // sorted by their position in the GUI (from left to right)
            Arrays.sort(children, new Comparator<Component>(){

                public int compare(Component o1, Component o2) {
                    return o1.getLocation().x - o2.getLocation().x;
                }
                
            });
            
            // Now we go through the array, make sure each child is a 
            // BlockViewer, that it is the block that we're expecting,
            // that it is the correct size, and that it has the correct 
            // tooltip text. 
            for (int j=0; j<children.length; j++){
                // Is this a BlockViewer at all?
                assertEquals(BlockViewer.class, children[j].getClass());
                // Yes, it is.
                BlockViewer viewer = (BlockViewer) children[j];
                
                
                
                // Find the index of the block number that this should be:
                int blockNum = Math.max(0, i-NUM_ITEMS+1) + j;
                // Now that we have this, let's check the tooltip
                // text:
                String tooltip = viewer.getToolTipText();
                String expected = "<html>Name - Test block "+blockNum+
                          "<br>Challenge: Challenge for block "+blockNum+
                          "<br>Notes: Notes for block "+blockNum;
                
                //assertEquals(expected, tooltip);
                
                
                // Now we check the displayed size:
                Dimension size = viewer.getSize();
                Dimension expectedSize = new Dimension(
                        EXPECTED_BLOCK_SIZE, EXPECTED_BLOCK_SIZE);
                assertEquals(expectedSize, size);
                
                
                
                // And let's not forget to check that the correct
                // block really is showing.
                // Need to calculate the patch size first:
                int patchSize = 
                    EXPECTED_BLOCK_SIZE / testBlocks[blockNum].getSideSize();
                // Also need to get the JPanelFixture from FEST:
                String filename = "file "+blockNum+".xml.gz";
                JPanelFixture viewerFixture =
                    window.panel(filename);
                BlockViewerTest.checkBlock(
                        testBlocks[blockNum], viewerFixture, patchSize);
                
                
                // And check that clicking it brings up a file chooser
                // with the file selected
                viewerFixture.click();
                File selected = window.fileChooser().target.getSelectedFile();
                File expectedFile = new File(tempDir, filename);
                assertEquals(expectedFile, selected);
                window.fileChooser().cancel();
                
                
                
            }
            
        }

    }


}

