/*
 * Created by jbiatek on Jun 9, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.xmlsaveload.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.UndoRedoStack;

/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */

public class DigiQuiltSAXHandler extends DelegatingHandler {

    /**
     * The current block loaded from XML. 
     */
    private Block currentBlock;
    
    /**
     * The current grid loaded from XML.
     */
    private Grid currentGrid;
    
    /**
     * The notes loaded from XML.
     */
    private String notes;
    
    /**
     * The challenge loaded from XML.
     */
    private Challenge challenge;
    
    /**
     * The history information loaded from XML.
     */
    private UndoRedoStack stack;
    

    /**
     * The name of the student from XML.
     */
    private String student;

    /**
     * The name of the quilt block from XML.
     */
    private String blockName;

    /**
     * The timestamp from XML.
     */
    private long timestamp;
    
    
    /**
     * The tag we left off on, for example "Notes" or "grid". We delegate
     * the actual loading of these to their respective handlers, and then when
     * they send us an object with childFinished() we can use this to know 
     * what to expect.
     */
    String currentDelegation;

    /**
     * @param xmlReader
     */
    public DigiQuiltSAXHandler(XMLReader xmlReader) {
        super(xmlReader);
        stack = new UndoRedoStack();
    }

    /**
     * @return the block marked "current" in the save file.
     */
    public Block getCurrentBlock(){
        return currentBlock;
    }
    
    /**
     * @return the grid from the save file.
     */
    public Grid getCurrentGrid(){
        return currentGrid;
    }
    
    /**
     * @return the notes saved in the file.
     */
    public String getNotes(){
        return notes;
    }
    
    /**
     * @return the challenge saved in the file.
     */
    public Challenge getChallenge(){
        return challenge;
    }
    
    /**
     * @return the UndoRedoStack as loaded from the file.
     */
    public UndoRedoStack getUndoRedoStack(){
        return stack;
    }
    
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        currentDelegation = name;
        if (name.equals("Block")){
            BlockHandler blockH = new BlockHandler(this, attributes);
            blockH.startHandlingEvents();
        } else if (name.equals("Grid")){
            GridHandler gridH = new GridHandler(this);
            gridH.startHandlingEvents();
        } else if (name.equals("Challenge")){
            ChallengeHandler challengeH = new ChallengeHandler(this);
            challengeH.startHandlingEvents();
        } else if (name.equals("History")){
            HistoryHandler historyH = new HistoryHandler(this);
            historyH.startHandlingEvents();
        } else if (name.equals("Notes")
                || name.equals("Student")
                || name.equals("BlockName")
                || name.equals("Timestamp")){
            // Simple text contents
            TextHandler textH = new TextHandler(this, name);
            textH.startHandlingEvents();
        }
    }

    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.xmlsaveload.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        if (currentDelegation.equals("Block")){
            // This was listed as the "current" block
            currentBlock = (Block) o;
        } else if (currentDelegation.equals("History")){
            stack = (UndoRedoStack) o;
        } else if (currentDelegation.equals("Grid")){
            currentGrid = (Grid) o;
        } else if (currentDelegation.equals("Challenge")){
            challenge = (Challenge) o;
        } else if (currentDelegation.equals("Notes")){
            notes = (String) o;
        } else if (currentDelegation.equals("Student")){
            student = (String) o;
        } else if (currentDelegation.equals("BlockName")){
            blockName = (String) o;
        } else if (currentDelegation.equals("Timestamp")){
            timestamp = Long.parseLong((String) o);
        }
    }

    /**
     * @return the student name.
     */
    public String getStudent() {
        return student;
    }

    /**
     * @return the block name
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * @return the timestamp from inside the file
     */
    public long getTimestamp() {
        return timestamp;
    }

}
