package umm.digiquilt.xmlsaveload;

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

import umm.digiquilt.model.Patch;

/**
 * @author DanielSelifonov, last changed by $Author: biatekjt $
 * on $Date: 2009-07-13 19:42:56 $
 * @version $Revision: 1.6 $
 */
public class SavePatchXML
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
    protected SavePatchXML() throws ParserConfigurationException
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
    public SavePatchXML(Patch currentPatch, String studentName, String blockName, 
	    long timestamp)
    throws ParserConfigurationException{
	// Create the initial document
	this();
	Element saveRoot = savedXML.createElement("DigiQuiltSave");
	savedXML.appendChild(saveRoot);
	// Add text elements
	saveText(savedXML, "Student", studentName);
	saveText(savedXML, "BlockName", blockName);
	saveText(savedXML, "Timestamp", Long.toString(timestamp));
	saveText(savedXML, "Notes", "default");
	saveText(savedXML, "Challenge", "(no challenge)");
	
	// Take care of the current block
	Element patchElement = savePatch(savedXML, currentPatch);
	saveRoot.appendChild(patchElement);
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
    private Element savePatch(Document doc, Patch savePatch)
    {
	Element blockRoot = doc.createElement("Block");
	blockRoot.setAttribute("size", ""+1);
	String[] tileArray = savePatch.getFabricList();
	Element patchRoot = doc.createElement("Patch");
	blockRoot.appendChild(patchRoot);

	for (int j = 0; j < Patch.MAXTILES; j++)
	{
	    Element fabricRoot = doc.createElement("Fabric");
	    fabricRoot.setTextContent(tileArray[j]);
	    patchRoot.appendChild(fabricRoot);
	}

	return blockRoot;
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

	// Use a Transformer for output
	TransformerFactory tFactory =
	    TransformerFactory.newInstance();
	try {
	    Transformer transformer = tFactory.newTransformer();
	    DOMSource source = new DOMSource(savedXML);
	    StreamResult result = new StreamResult(outStream);

	    transformer.transform(source, result);
	} catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}

	outStream.close();
    }
}
