/**
 * DigiQuilt XML Loading Class [In progress]
 */
package umm.digiquilt.xmlsaveload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.UndoRedoStack;
import umm.digiquilt.xmlsaveload.handlers.DigiQuiltSAXHandler;

/**
 * @author DanielSelifonov, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.2 $
 */
public class LoadXML
{

    /**
     * The SAX ContentHandler which knows how to load from XML.
     */
    DigiQuiltSAXHandler dqHandler;
    
    private String filename;
    
    /**
     * Loads all of the save data from the specified file name into this LoadXML.
     * 
     * @param filename The file to be loaded.
     * @throws SAXException 
     * @throws IOException 
     */
    public LoadXML(String filename) throws IOException, SAXException {
	this.filename = filename;
        FileInputStream fileIn = new FileInputStream(filename);
        loadFromStream(fileIn);
    }

    /**
     * Load all of the saved XML data from the specified InputStream.
     * 
     * @param in
     * @throws IOException
     * @throws SAXException
     */
    public LoadXML(InputStream in) throws IOException, SAXException {
        loadFromStream(in);
    }

    /**
     * Loads and parses a GZIPped input stream of XML into this instance of LoadXML.
     * The results can be accessed using the get methods.
     * 
     * @param in
     * @throws IOException
     * @throws SAXException
     */
    private final void loadFromStream(InputStream in) throws SAXException, IOException{
        XMLReader xmlRead = XMLReaderFactory.createXMLReader();
        dqHandler = new DigiQuiltSAXHandler(xmlRead);
        xmlRead.setContentHandler(dqHandler);
        xmlRead.setErrorHandler(dqHandler);
        
        if(filename == null || filename.endsWith(".gz")){
            xmlRead.parse(new InputSource(new GZIPInputStream(in)));
        }
        else{
            xmlRead.parse(new InputSource(in));
        }
    }

    /**
     * @return The block that was marked as the current block from the XML file. If
     * there wasn't one, or nothing has been loaded yet, it will be null.
     */
    public Block getCurrentBlock(){
        return dqHandler.getCurrentBlock();
    }

    /**
     * @return An UndoRedoStack containing all the undos and redos loaded from XML, in
     * the proper order and in the correct stacks. If no XML has been loaded, this will
     * still be null.
     */
    public UndoRedoStack getUndoRedoStack(){
        return dqHandler.getUndoRedoStack();
    }

    /**
     * @return The grid that was loaded from XML, if any. If there wasn't one,
     * this may be null.
     */
    public Grid getGrid(){
        return dqHandler.getCurrentGrid();
    }

    /**
     * @return the notes loaded from this XML, if any. If there wasn't one, 
     * this may be null.
     */
    public String getNotes(){
        return dqHandler.getNotes();
    }

    /**
     * @return the challenge loaded from XML, if any. IF there wasn't on, this
     * may be null.
     */
    public Challenge getChallenge(){
        return dqHandler.getChallenge();
    }

    /**
     * @return the name of the student that created this file.
     */
    public String getStudent() {
        return dqHandler.getStudent();
    }

    /**
     * @return the name of the quilt block.
     */
    public String getBlockName() {
        return dqHandler.getBlockName();
    }

    /**
     * @return the timestamp from the file.
     */
    public long getTimestamp() {
        return dqHandler.getTimestamp();
    }
}
