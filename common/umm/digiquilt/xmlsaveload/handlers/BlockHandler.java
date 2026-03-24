package umm.digiquilt.xmlsaveload.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Patch;

/**
 * XML handler which can load one Block from XML. Will pass this block
 * up to its parent.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */
public class BlockHandler extends DelegatingHandler {
    
    /**
     * The block as restored from XML.
     */
    Block extractedBlock;
    /**
     * The patch number that we're on.
     */
    private int patchNumber = 0;

    /**
     * Create a BlockHandler which process a Block tag.
     * 
     * @param parent The parent, who will receive the block after this handler
     * is done.
     * @param attributes The attributes of the tag
     */
    public BlockHandler(DelegatingHandler parent, Attributes attributes){
        super(parent);
        // Grab the size of this block
        String sizeAttr = attributes.getValue("size");
        int size;
        if (sizeAttr != null){
            size = Integer.parseInt(sizeAttr);
        } else {
            size = 16;
        }

        extractedBlock = new Block(size);
    }
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes atts) throws SAXException {
        if (name.equals("Patch")){
            PatchHandler ph = new PatchHandler(this);
            ph.startHandlingEvents();
        }

    }

    @Override
    public void endElement(String uri, String localName, String name)
    throws SAXException {
        if (name.equals("Block")){
            patchNumber = 0;
            stopHandlingEvents();
            parent.childFinished(extractedBlock);
        }
    }

    @Override
    public void childFinished(Object o) {
        // Blocks only contain patches, so we can assume that's what this is
        Patch patch = (Patch) o;
        extractedBlock.setPatch(patch, patchNumber);
        patchNumber++;
    }


}
