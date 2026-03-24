package umm.digiquilt.xmlsaveload.handlers;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A ContentHandler which allows multiple ContentHandlers to also get
 * SAX events. Normally only one Handler is allowed to receive these.<br>
 * <br>
 * A subclass can receive XML events from an XML reader by overriding the
 * methods in DefaultHandler and dealing with them. Alternatively, they can
 * create a new DelegatingHandler and pass control off to it temporarily. The
 * child may be specially built to deal with a certain tag in XML. After the
 * child is done, it can pass a resulting Object to its parent using the
 * childFinished() method and return control to the parent by executing
 * stopHandlingEvents(). 
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */
public abstract class DelegatingHandler extends DefaultHandler{
    
    /**
     * The parent of this DelegatingHandler. This will be null if we are the
     * top-level Handler.
     */
    DelegatingHandler parent;
    
    /**
     * The XMLReader for the current XML document. 
     */
    XMLReader xmlReader;
    
    /**
     * Create a top-level DelegatingHandler to receive events from
     * the given XMLReader.
     * 
     * @param xmlReader
     */
    public DelegatingHandler(XMLReader xmlReader){
        this.xmlReader = xmlReader;
    }
    
    /**
     * Create a child DelegatingHandler. The given handler will be its
     * parent.
     * 
     * @param parent
     */
    public DelegatingHandler(DelegatingHandler parent){
        this.parent = parent;
        this.xmlReader = parent.getXMLReader();
    }
    
    /**
     * @return the XMLReader for this handler. 
     */
    public XMLReader getXMLReader(){
        return xmlReader;
    }

    /**
     * Tell this DelegatingHandler to take over receiving events. Control
     * should be returned to the parent when this handler is done, probably
     * accompanied with an object being passed up as well.
     */
    public void startHandlingEvents(){
        xmlReader.setContentHandler(this);
    }
    
    /**
     * Relinquish control to the parent. The parent handler will resume
     * receiving events from the XMLReader.
     */
    public void stopHandlingEvents(){
        xmlReader.setContentHandler(parent);
    }
    
    /**
     * Children of MultiEventHandlers can use this method to pass Objects
     * back up to their parents. The parent is responsible for remembering
     * what it delegated to so that it knows what to expect.
     * 
     * @param o the Object that the child wishes to pass to the parent.
     */
    public abstract void childFinished(Object o);

}
