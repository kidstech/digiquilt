package umm.digiquilt.xmlsaveload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.UndoRedoStack;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * Test the LoadXML class.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.6 $
 *
 */
public class LoadXMLTest {

    /**
     * The resource location for the test xml files.
     */
    private static final String XMLLOCATION = 
        "/umm/digiquilt/xmlsaveload/";

    /**
     * All the .xml files that should be compressed so they can be
     * tested.
     */
    private static final String[] TESTCASES = new String[]{
        "ordinary.xml",
        "empty.xml",
        "malformed.xml",
        "bad-freepatch.xml",
        "bad-tooshortblock.xml",
        "bad-toolongblock.xml"
    };

    /**
     * A temporary directory for test files.
     */
    private static File TESTDIR;

    /**
     * Compress all the files in TESTCASES and put them into a temporary
     * test location.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void compressXML() throws Exception{
        TESTDIR = File.createTempFile("dq-xml", ".temp");
        TESTDIR.delete();
        TESTDIR.mkdir();

        for (String file : TESTCASES){
            InputStream in = LoadXMLTest.class.getResourceAsStream(
                    XMLLOCATION+file);
            File gzipFile = new File(TESTDIR, file+".gz");
            GZIPOutputStream gzout = new GZIPOutputStream(
                    new FileOutputStream(gzipFile));

            int read;
            while ((read = in.read()) != -1){
                gzout.write(read);
            }

            in.close();
            gzout.close();
        }
    }

    /**
     * Clean up test directory after tests are done.
     */
    @AfterClass
    public static void deleteTestDirectory(){
        File[] contents = TESTDIR.listFiles();
        for (File file : contents){
            // This assumes there aren't any directories
            file.delete();
        }
        TESTDIR.delete();
    }

    /**
     * Test loading of a valid quilt save file. The only tricky parts are
     * that the values of the grid are listed in a few different ways
     * and the undo/redo stack elements are not listed in the proper order
     * (the correct order is the one given by their attributes)
     * 
     * @throws Exception
     */
    @Test
    public void testOrdinaryLoad() throws Exception{
        // Create the stream
        InputStream is = new FileInputStream(
                new File(TESTDIR, "ordinary.xml.gz"));

        LoadXML streamLoader = new LoadXML(is);
        LoadXML stringLoader = new LoadXML(
                new File(TESTDIR, "ordinary.xml.gz").getPath());

        assertEquals("Loading from stream vs filename had different notes",
                streamLoader.getNotes(), stringLoader.getNotes());
        assertEquals("Loading from stream vs filename had different challenges",
                streamLoader.getChallenge(), stringLoader.getChallenge());
        assertEquals("Loading from stream vs filename had different blocks",
                streamLoader.getCurrentBlock(), stringLoader.getCurrentBlock());


        UndoRedoStack stack1 = streamLoader.getUndoRedoStack();
        UndoRedoStack stack2 = stringLoader.getUndoRedoStack();
        assertEquals("Loading from stream vs filename: Undo 0 was different",
                stack1.getUndoElement(0), stack2.getUndoElement(0));
        assertEquals("Loading from stream vs filename: Undo 1 was different",
                stack1.getUndoElement(1), stack2.getUndoElement(1));
        assertEquals("Loading from stream vs filename: Redo 0 was different",
                stack1.getRedoElement(0), stack2.getRedoElement(0));
        assertEquals("Loading from stream vs filename: Redo 1 was different",
                stack1.getRedoElement(1), stack2.getRedoElement(1));

        String studentName = streamLoader.getStudent();
        String blockName = streamLoader.getBlockName();
        long timestamp = streamLoader.getTimestamp();
        String loadedNotes = streamLoader.getNotes();

        
        assertEquals("Student name", studentName);
        assertEquals("Block name", blockName);
        assertEquals(1273444072000l, timestamp);
        assertEquals("These are the notes for this save file.", loadedNotes);


        FractionChallenge challenge = (FractionChallenge) streamLoader.getChallenge();
        assertEquals("Test name", challenge.getName());
        assertEquals(4, challenge.getFractionMap().size());
        
        
        assertEquals(2, challenge.getFractionMap().get(Fabric.WHITE).getNumerator());
        assertEquals(8, challenge.getFractionMap().get(Fabric.WHITE).getDenominator());
        
        assertEquals(1, challenge.getFractionMap().get(Fabric.BLACK).getNumerator());
        assertEquals(4, challenge.getFractionMap().get(Fabric.BLACK).getDenominator());
        
        assertEquals(1, challenge.getFractionMap().get(Fabric.BROWN).getNumerator());
        assertEquals(8, challenge.getFractionMap().get(Fabric.BROWN).getDenominator());
        
        assertEquals(6, challenge.getFractionMap().get(Fabric.TRANSPARENT).getNumerator());
        assertEquals(16, challenge.getFractionMap().get(Fabric.TRANSPARENT).getDenominator());


        Grid loadedGrid = streamLoader.getGrid();
        // Right now, the order of lines matters: sorting to grid in the
        // client could potentially get messed up otherwise.
        assertEquals(loadedGrid.get(0).x1, 0d, .001d);
        assertEquals(loadedGrid.get(0).y1, 0d, .001d);
        assertEquals(loadedGrid.get(0).x2, 1d, .001d);
        assertEquals(loadedGrid.get(0).y2, 0d, .001d);

        assertEquals(loadedGrid.get(1).x1, 0d, .001d);
        assertEquals(loadedGrid.get(1).y1, 0d, .001d);
        assertEquals(loadedGrid.get(1).x2, 0d, .001d);
        assertEquals(loadedGrid.get(1).y2, 1d, .001d);

        assertEquals(loadedGrid.get(2).x1, 0d, .001d);
        assertEquals(loadedGrid.get(2).y1, 1d, .001d);
        assertEquals(loadedGrid.get(2).x2, 1d, .001d);
        assertEquals(loadedGrid.get(2).y2, 1d, .001d);

        assertEquals(loadedGrid.get(3).x1, 1d, .001d);
        assertEquals(loadedGrid.get(3).y1, 0d, .001d);
        assertEquals(loadedGrid.get(3).x2, 1d, .001d);
        assertEquals(loadedGrid.get(3).y2, 1d, .001d);

        assertEquals(loadedGrid.get(4).x1, .5d, .001d);
        assertEquals(loadedGrid.get(4).y1, 0d, .001d);
        assertEquals(loadedGrid.get(4).x2, .5d, .001d);
        assertEquals(loadedGrid.get(4).y2, 1d, .001d);


        // Testing the loaded blocks and their order

        Patch currentPatch = new Patch(new Fabric[]{
                Fabric.INDIGO, Fabric.WHITE, Fabric.INDIGO, Fabric.WHITE,
                Fabric.INDIGO, Fabric.WHITE, Fabric.INDIGO, Fabric.WHITE,
                Fabric.INDIGO, Fabric.WHITE, Fabric.INDIGO, Fabric.WHITE,
                Fabric.INDIGO, Fabric.WHITE, Fabric.INDIGO, Fabric.WHITE});
        Block currentBlock = new Block(4);
        currentBlock.swapPatchesInBlock(new Patch(), currentPatch);

        assertEquals(currentBlock, streamLoader.getCurrentBlock());

        UndoRedoStack loadedStack = streamLoader.getUndoRedoStack();

        Block undo0 = new Block(4);
        undo0.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.RED);
        assertEquals(undo0, loadedStack.getUndoElement(0));

        Block undo1 = new Block(9);
        undo1.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        assertEquals(undo1, loadedStack.getUndoElement(1));
        
        Block undo2 = new Block(16);
        undo2.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.REDVIOLET);
        assertEquals("Block without explicit size was not equal",
                undo2, loadedStack.getUndoElement(2));

        Block redo1 = new Block(4);
        redo1.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        assertEquals(redo1, loadedStack.getRedoElement(1));

        Block redo0 = new Block(16);
        redo0.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.ORANGE);
        assertEquals(redo0, loadedStack.getRedoElement(0));

    }

    /**
     * Try to load a save file consisting of only an opening and closing
     * &lt;digiquiltsave&gt; tag. I'm not sure if it should complain or not,
     * though...
     * 
     * @throws Exception
     */
    @Test
    public void testEmptySaveFile() throws Exception{
        File emptySave = new File(TESTDIR, "empty.xml.gz");
        new LoadXML(new FileInputStream(emptySave));
    }

    /**
     * Attempts to load a file which has bad XML syntax: it starts
     * with a closing tag. This should be caught by the parser.
     * 
     * @throws Exception
     */
    @Test
    public void testMalformedFile() throws Exception{
        File malformed = new File(TESTDIR, "malformed.xml.gz");
        InputStream in = new FileInputStream(malformed);
        try {
            new LoadXML(in);
            fail("This file should not have loaded.");
        } catch (Exception e){
            // Do nothing, this is what should have happened
        }
    }
  
    /**Tests a file which is valid XML but doesn't make sense for 
     * Digiquilt: It contains a Patch that is not inside of a Block.
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void testFreeFloatingPatch() throws Exception{
        File malformed = new File(TESTDIR, "bad-freepatch.xml.gz");
        InputStream in = new FileInputStream(malformed);
        try {
            new LoadXML(in);
            fail("This file should not have loaded.");
        } catch (Exception e){
            // Do nothing, this is what should have happened
        }
    }
    
    /**Tests a file which is valid XML and has a block of size four which
     * only contains one Patch.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testTooShortBlock() throws Exception{
        File malformed = new File(TESTDIR, "bad-tooshortblock.xml.gz");
        InputStream in = new FileInputStream(malformed);
        try {
            new LoadXML(in);
            fail("This file should not have loaded.");
        } catch (Exception e){
            // Do nothing, this is what should have happened
        }
    }
    
    /**Tests a file which is valid XML and has a block of size four which
     * contains too many patches.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testTooLongBlock() throws Exception{
        File malformed = new File(TESTDIR, "bad-tooshortblock.xml.gz");
        InputStream in = new FileInputStream(malformed);
        try {
            new LoadXML(in);
            fail("This file should not have loaded.");
        } catch (Exception e){
            // Do nothing, this is what should have happened
        }
    }

}
