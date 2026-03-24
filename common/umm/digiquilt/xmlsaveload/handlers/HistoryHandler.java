/*
 * Created by jbiatek on May 9, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.xmlsaveload.handlers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.works.UndoRedoStack;

/**
 * Handle the History tag.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class HistoryHandler extends DelegatingHandler {

    /**
     * The stack that we are restoring.
     */
    UndoRedoStack stack = new UndoRedoStack();
    
    /**
     * The tag we delegated to.
     */
    String tag;
    
    /**
     * Create a new HistoryHandler to take care of a History tag.
     * 
     * @param parent
     */
    public HistoryHandler(DelegatingHandler parent) {
        super(parent);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        tag = qName;
        if (tag.equals("Redos") || tag.equals("Undos")){
            BlockListHandler lister = new BlockListHandler(this, tag);
            lister.startHandlingEvents();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("History")){
            stopHandlingEvents();
            parent.childFinished(stack);
        }
    }

    /* (non-Javadoc)
     * @see umm.digiquilt.xmlsaveload.handlers.DelegatingHandler#childFinished(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void childFinished(Object o) {
        List<Block> list = (List<Block>) o;
        if (tag.equals("Redos")){
            for (Block block : list){
                stack.addRedo(block);
            }
        } else if (tag.equals("Undos")){
            for (Block block : list){
                stack.addUndo(block);
            }
        }

    }
    
    /**
     * Grab a bunch of Blocks, and collect them in a list.
     * 
     * @author Jason Biatek, last changed by $Author: lamberty $
     * on $Date: 2008/01/22 17:50:24 $
     * @version $Revision: 1.1 $
     *
     */
    private class BlockListHandler extends DelegatingHandler {

        /**
         * The tag to stop at
         */
        private String endTag;
        
        /**
         * The list of collected Blocks.
         */
        private List<Block> list = new ArrayList<Block>();

        /**
         * Create a new BlockListHandler
         * 
         * @param parent
         * @param endTag The end tag to stop on.
         */
        public BlockListHandler(DelegatingHandler parent, String endTag) {
            super(parent);
            this.endTag = endTag;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if (qName.equals("Block")){
                BlockHandler blockH = new BlockHandler(this, attributes);
                blockH.startHandlingEvents();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (qName.equals(endTag)){
                stopHandlingEvents();
                parent.childFinished(list);
            }
        }
        
        
        /* (non-Javadoc)
         * @see umm.digiquilt.xmlsaveload.handlers.DelegatingHandler#childFinished(java.lang.Object)
         */
        @Override
        public void childFinished(Object o) {
            list.add((Block) o);
        }
        
    }

}
