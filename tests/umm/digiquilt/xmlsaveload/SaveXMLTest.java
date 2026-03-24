package umm.digiquilt.xmlsaveload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.PatchTest;
import umm.digiquilt.model.works.UndoRedoStack;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * Test the SaveXML class.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-04 01:49:19 $
 * @version $Revision: 1.6 $
 *
 */
public class SaveXMLTest{
    
    /**
     * Temporary test save location
     */
    File saveLocation;
    
    /**
     * Create a temporary save file
     * @throws Exception
     */
    @Before
    public void createSaveLocation() throws Exception{
        saveLocation = File.createTempFile("dq-save", ".xml.gz");
    }
    
    /**
     * Delete the save file.
     */
    @After
    public void removeSaveLocation(){
        saveLocation.delete();
    }

    /**
     * Create some DigiQuilt objects, save them with SaveXML, and then 
     * use an XML parser to go through the resulting file to see if it
     * corresponds.
     * 
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception{
        String studentName = "The best student ever";
        String blockName = "Awesome quilt!";
        long timestamp = System.currentTimeMillis();
        
        
        Patch testpatch = PatchTest.makeFancyTestPatch();
        Block testBlock = new Block(16);
        testBlock.replacePatchInBlock(new Patch(), testpatch);

        Block redBlock = new Block(9);
        redBlock.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.RED);

        Block blueBlock = new Block(9);
        blueBlock.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);

        Block orangeBlock = new Block(4);
        orangeBlock.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.ORANGE);

        Block greenBlock = new Block(4);
        greenBlock.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);

        UndoRedoStack testUndo = new UndoRedoStack();
        testUndo.addUndo(blueBlock);
        testUndo.addUndo(greenBlock);
        testUndo.addRedo(redBlock);
        testUndo.addRedo(orangeBlock);


        Grid testGrid = new Grid(0, 2, 0, 0);

        String notes = "These are the notes for the test save file.";
        Map<Fabric, Fraction> map = new HashMap<Fabric, Fraction>();
        Challenge fractionChallenge = new FractionChallenge("Test name", map);


        SaveBlockXML testSaver = new SaveBlockXML(testBlock, testGrid, testUndo, 
                studentName, blockName, timestamp, notes, fractionChallenge);

        testSaver.writeOutDocumentToFile(saveLocation);



        GZIPInputStream input = new GZIPInputStream(
                new FileInputStream(saveLocation));


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new TestErrorHandler());
        Document loadedDoc = builder.parse(input);

        NodeList documentNodes = loadedDoc.getChildNodes();
        assertEquals("There should only be one node at the root level", 
                1, documentNodes.getLength());
        // FYI: If doctypes get put back in, this will fail since a doctype
        // apparently counts as a node. You'll need to change this to 2, and
        // the next line to get item 1, not 0.
        
        Node saveNode = documentNodes.item(0);
        assertEquals("Root node should be <DigiQuiltSave>",
                "DigiQuiltSave", saveNode.getNodeName());

        NodeList saveChildren = saveNode.getChildNodes();
        assertEquals("digiquiltsave should have 8 children",
                8, saveChildren.getLength());

        Node studentNode = saveChildren.item(0);
        assertEquals("Student", studentNode.getNodeName());
        assertEquals(studentName, studentNode.getTextContent());
        
        Node blockNameNode = saveChildren.item(1);
        assertEquals("BlockName", blockNameNode.getNodeName());
        assertEquals(blockName, blockNameNode.getTextContent());
        
        Node timestampNode = saveChildren.item(2);
        assertEquals("Timestamp", timestampNode.getNodeName());
        assertEquals(Long.toString(timestamp), timestampNode.getTextContent());
        
        Node notesNode = saveChildren.item(3);
        assertEquals("Notes", notesNode.getNodeName());
        assertEquals("Notes were saved incorrectly", 
                notes, notesNode.getTextContent());
        
        
        Node challengeNode = saveChildren.item(4);
        ChallengeWriterTest.verifyChallenge(fractionChallenge, challengeNode);
        
        
        Node gridNode = saveChildren.item(5);
        assertEquals("Grid", gridNode.getNodeName());
        assertEquals("Grid had wrong number of lines", testGrid.size(),
                gridNode.getChildNodes().getLength());

        
        Node currentBlockNode = saveChildren.item(6);
        NodeList patches = currentBlockNode.getChildNodes();
        assertEquals("Current block should have 16 patches",
                16, patches.getLength());
        for (int i=0; i<patches.getLength(); i++){
            Node patchNode = patches.item(i);
            for (int j=0; j<Patch.MAXTILES; j++){
                Node fabricNode = patchNode.getChildNodes().item(j);

                assertEquals("Current block had incorrect fabric",
                        fabricNode.getTextContent(), 
                        testpatch.getFabricList()[j]);
            }
        }

        
        Node historyNode = saveChildren.item(7);
        assertEquals("History", historyNode.getNodeName());
        
        Node undoNode = historyNode.getFirstChild();
        assertEquals("Undos", undoNode.getNodeName());
        
        // This is the blue block
        Node blueNode = undoNode.getChildNodes().item(0);
        assertOneColorBlock(blueNode, "BLUE", 9);

        // This is the green block
        Node greenNode = undoNode.getChildNodes().item(1);
        assertOneColorBlock(greenNode, "GREEN", 4);
        
        
        Node redoNode = historyNode.getChildNodes().item(1);
        assertEquals("Redos", redoNode.getNodeName());

        // This is red blue block
        Node redNode = redoNode.getChildNodes().item(0);
        assertOneColorBlock(redNode, "RED", 9);
        
        // This is the orange block
        Node orangeNode = redoNode.getChildNodes().item(1);
        assertOneColorBlock(orangeNode, "ORANGE", 4);


    }

    /**
     * Go through a block node and make sure that all the tiles
     * are the given color.
     * 
     * @param blockNode
     * @param color
     * @param size 
     */
    private void assertOneColorBlock(Node blockNode, String color, int size){
        assertEquals("Block", blockNode.getNodeName());
        assertEquals(size, blockNode.getChildNodes().getLength());
        NodeList patches = blockNode.getChildNodes();
        for (int i=0; i<patches.getLength(); i++){
            Node patchNode = patches.item(i);
            assertEquals("Patch", patchNode.getNodeName());
            assertEquals("Patch had wrong number of fabrics", 
                    Patch.MAXTILES, patchNode.getChildNodes().getLength());
            
            
            for (int j=0; j<Patch.MAXTILES; j++){
                Node fabricNode = patchNode.getChildNodes().item(j);
                assertEquals("Fabric", fabricNode.getNodeName());
                assertEquals("Block had incorrect saved fabric",
                        fabricNode.getTextContent(), color);
            }
        }
    }

    /**
     * An ErrorHandler which fails the JUnit test if something goes wrong.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.6 $
     *
     */
    public static class TestErrorHandler implements ErrorHandler {

        public void error(SAXParseException exception) throws SAXException {
            fail("An error was encountered: "+exception.getMessage());
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            fail("A fatal error occured while parsing: "+exception.getMessage());
        }

        public void warning(SAXParseException exception) throws SAXException {
            System.out.println("Warning: "+exception.getMessage());
        }

    }

}
