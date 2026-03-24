/*
 * Created by jbiatek on Jun 9, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;

/**
 * XML Handler to load a single Patch from XML.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */
public class PatchHandler extends DelegatingHandler {

    /**
     * String array of the fabrics as we load them.
     */
    private Fabric[] fabrics;
    
    /**
     * The fabric number that we are currently on.
     */
    private int fabricNumber = 0;
    
    /**
     * Create a PatchHandler to load a single Patch from XML.
     * 
     * @param parent The handler which will be notified with the resulting
     * Patch.
     */
    public PatchHandler(DelegatingHandler parent) {
        super(parent);
        fabrics = new Fabric[Patch.MAXTILES];
    }
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (name.equals("Fabric")){
            TextHandler fabricHandler = new TextHandler(this, "Fabric");
            fabricHandler.startHandlingEvents();
        }
    }


    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (name.equals("Patch")){
            Patch returnPatch = new Patch(fabrics);
            stopHandlingEvents();
            parent.childFinished(returnPatch);
        }
    }



    @Override
    public void childFinished(Object o) {
        // Children will be coming to us with fabric strings
        String fabric = (String) o;
        fabrics[fabricNumber] = Fabric.valueOf(fabric);
        fabricNumber++;
    }

}
