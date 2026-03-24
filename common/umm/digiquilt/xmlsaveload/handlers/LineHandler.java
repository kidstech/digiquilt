/*
 * Created by jbiatek on Jun 9, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.xmlsaveload.handlers;

import java.awt.geom.Line2D;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * XML handler to load a single grid line from XML.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */

public class LineHandler extends DelegatingHandler {

    /**
     * Keep track of if we're in an x1, y1, x2, or y2 tag.
     */
    private String currentTagName;
    
    /**
     * The x1 value loaded from XML.
     */
    private double x1;
    
    /**
     * The y1 value loaded from XML.
     */
    private double y1;
    
    /**
     * The x2 value loaded from XML.
     */
    private double x2;
    
    /**
     * The y2 value loaded from XML.
     */
    private double y2;
    
    /**
     * @param parent
     */
    public LineHandler(DelegatingHandler parent) {
        super(parent);
    }
    


    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (name.equals("x1") || name.equals("y1")
                || name.equals("x2") || name.equals("y2")){
            currentTagName = name;
            TextHandler handler = new TextHandler(this, name);
            handler.startHandlingEvents();
        }
    }



    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (name.equals("Line")){
            Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
            parent.childFinished(line);
            stopHandlingEvents();
        }
    }



    @Override
    public void childFinished(Object o) {
        // This will be a string of a double...
        double incoming = Double.parseDouble((String) o);
        if (currentTagName.equals("x1")){
            x1 = incoming;
        } else if (currentTagName.equals("y1")){
            y1 = incoming;
        } else if (currentTagName.equals("x2")){
            x2 = incoming;
        } else if (currentTagName.equals("y2")){
            y2 = incoming;
        }
    }

}
