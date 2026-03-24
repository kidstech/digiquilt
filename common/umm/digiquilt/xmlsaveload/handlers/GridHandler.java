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

import umm.digiquilt.model.Grid;

/**
 * XML handler which loads a Grid from XML.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.2 $
 *
 */

public class GridHandler extends DelegatingHandler {

    /**
     * The Grid that will be returned. 
     */
    Grid returnGrid;
    
    /**
     * @param parent
     */
    public GridHandler(DelegatingHandler parent) {
        super(parent);
        returnGrid = new Grid();
        // Grids come with 4 lines by default, but those 4 lines are probably
        // also saved in XML. If we were to load and save multiple times
        // they'd be duplicated each time. Best to just throw them out now.
        returnGrid.clear();
    }
    
    

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (name.equals("Line")){
            LineHandler lh = new LineHandler(this);
            lh.startHandlingEvents();
        }
    }



    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (name.equals("Grid")){
            parent.childFinished(returnGrid);
            stopHandlingEvents();
        }
    }



    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.xmlsaveload.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        // This will be a Line2D from LineHandler
        Line2D.Double line = (Line2D.Double) o;
        returnGrid.add(line);
    }

}
