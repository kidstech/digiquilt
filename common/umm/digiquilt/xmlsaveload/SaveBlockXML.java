package umm.digiquilt.xmlsaveload;

import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.UndoRedoStack;
/**
 * @author DanielSelifonov, last changed by $Author: biatekjt $
 * on $Date: 2009-07-13 19:42:56 $
 * @version $Revision: 1.6 $
 */
public class SaveBlockXML 
{
    /**
     * Factory which produces DocumentBuilders
     */
    final private DocumentBuilderFactory factory;

    /**
     * The Document that is created using input from the constructors.
     */
    final private Document savedXML;



    /**
     * A private constructor which just creates the initial save file. This shouldn't
     * be used by anything outside of here because it makes an empty save. The public
     * constructors can use this to get started before adding the actual data to XML.
     * 
     * @throws ParserConfigurationException
     */
    protected SaveBlockXML() throws ParserConfigurationException
    {
	factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	savedXML = builder.newDocument();
    }

    /**
     * Convert the given parameters into a DigiQuilt save file. Once created, the
     * resulting Document can be streamed into a file or any other OutputStream. This
     * constructor is meant for a full save.
     * 
     * @param currentBlock The current block.
     * @param currentGrid The current grid.
     * @param undoRedo The current undo/redo stack
     * @param studentName The name of the student
     * @param blockName The name of the block
     * @param timestamp The timestamp (probably just current time)
     * @param notes The notes to be saved in this file.
     * @param challenge The currently selected challenge.
     * @throws ParserConfigurationException
     */
    public SaveBlockXML(Block currentBlock, Grid currentGrid, 
	    UndoRedoStack undoRedo, String studentName, String blockName, 
	    long timestamp, String notes, Challenge challenge)
    throws ParserConfigurationException{
	// Create the initial document
	this();
	Element saveRoot = savedXML.createElement("DigiQuiltSave");
	savedXML.appendChild(saveRoot);
	// Add text elements
	saveText(savedXML, "Student", studentName);
	saveText(savedXML, "BlockName", blockName);
	saveText(savedXML, "Timestamp", Long.toString(timestamp));
	saveText(savedXML, "Notes", notes);

	if(challenge != null){
	    // Save the challenge
	    ChallengeWriter writer = new ChallengeWriter();
	    writer.saveChallenge(challenge, savedXML, savedXML.getFirstChild());
	}

	// Save the Grid
	saveGrid(savedXML, currentGrid);

	// Take care of the current block
	Element blockElement = saveBlock(savedXML, currentBlock);
	saveRoot.appendChild(blockElement);

	//save the history:
	Element historyNode = savedXML.createElement("History");
	Element undos = savedXML.createElement("Undos");
	Element redos = savedXML.createElement("Redos");

	saveRoot.appendChild(historyNode);
	historyNode.appendChild(undos);
	historyNode.appendChild(redos);

	for (int i = 0; i < undoRedo.getUndoStackSize(); i++) {
	    Block undo = undoRedo.getUndoElement(i);
	    undos.appendChild(saveBlock(savedXML, undo));
	}

	//save the redo stack
	for (int i = 0; i < undoRedo.getRedoStackSize(); i++)
	{
	    Block redo = undoRedo.getRedoElement(i);
	    redos.appendChild(saveBlock(savedXML, redo));
	}
    }

    /**
     * Save a string of text in XML. It will be appended to doc.getFirstChild()
     * and should look like &lt;tagName&gt;contents&lt;/tagName&gt;
     * 
     * @param doc The Document to save to.
     * @param tagName The name of the tag, e.g. "Notes" or "Challenge"
     * @param contents The contents of the tag.
     */
    private void saveText(Document doc, String tagName, String contents){
	Element textElement = doc.createElement(tagName);
	textElement.setTextContent(contents);
	doc.getFirstChild().appendChild(textElement);
    }


    /**
     * Create an Element containing the given Block.
     * 
     * @param doc Place we are building the XML tree from the block.
     * @param saveBlock The block we wish to save.
     * @param temporalState Whether this is the current block, an undo block, 
     * or a redo block. Represented by "current", "undo #", and "redo #" where
     * # is the number of the block in the stack.
     * @return The resulting Element, ready to be attached.
     */
    private Element saveBlock(Document doc, Block saveBlock)
    {
	Element blockRoot = doc.createElement("Block");
	blockRoot.setAttribute("size", ""+saveBlock.getSize());

	for (Patch savePatch : saveBlock)
	{
	    String[] tileArray = savePatch.getFabricList();
	    Element patchRoot = doc.createElement("Patch");
	    blockRoot.appendChild(patchRoot);

	    for (int j = 0; j < Patch.MAXTILES; j++)
	    {
		Element fabricRoot = doc.createElement("Fabric");
		fabricRoot.setTextContent(tileArray[j]);
		patchRoot.appendChild(fabricRoot);
	    }
	}

	return blockRoot;
    }

    /**Save a Grid object into XML.
     * @param doc
     * @param grid
     */
    private void saveGrid(Document doc, Grid grid){
	Element gridRoot = doc.createElement("Grid");
	doc.getFirstChild().appendChild(gridRoot);

	for (Line2D.Double line : grid){
	    Element lineRoot = doc.createElement("Line");

	    Element x1 = doc.createElement("x1");
	    x1.setTextContent(line.getX1()+"");
	    lineRoot.appendChild(x1);

	    Element y1 = doc.createElement("y1");
	    y1.setTextContent(line.getY1()+"");
	    lineRoot.appendChild(y1);

	    Element x2 = doc.createElement("x2");
	    x2.setTextContent(line.getX2()+"");
	    lineRoot.appendChild(x2);

	    Element y2 = doc.createElement("y2");
	    y2.setTextContent(line.getY2()+"");
	    lineRoot.appendChild(y2);

	    gridRoot.appendChild(lineRoot);
	}
    }

    /**
     * Save the Document object to a file.
     * 
     * @param filename 
     * @throws IOException 
     */
    public void writeOutDocumentToFile(File filename) throws IOException
    {
	// create buffered file output stream
	FileOutputStream fileOut = new FileOutputStream(filename);
	writeDocumentToStream(fileOut);
    }

    /**
     * Save the Document object to an OutputStream.
     * 
     * @param outStream 
     * @throws IOException 
     */
    public void writeDocumentToStream(OutputStream outStream) throws IOException
    {
	// GZIP compression
	GZIPOutputStream compressedStream = new GZIPOutputStream(outStream);

	// Use a Transformer for output
	TransformerFactory tFactory =
	    TransformerFactory.newInstance();
	try {
	    Transformer transformer = tFactory.newTransformer();
	    DOMSource source = new DOMSource(savedXML);
	    StreamResult result = new StreamResult(compressedStream);

	    transformer.transform(source, result);
	} catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}

	compressedStream.close();
    }
}
