/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload.handlers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;

/**
 * Handles parsing of the FractionChallenge tag, creating a FractionChallenge
 * object when finished.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FractionChallengeHandler extends DelegatingHandler {

    /**
     * The name that was indicated in XML.
     */
    String name;
    
    /**
     * The fabrics and fractions from XML.
     */
    Map<Fabric, Fraction> map = new HashMap<Fabric, Fraction>();
    
    /**
     * The creation date from the XML.
     */
    Date dateCreated;
    /**
     * @param parent
     */
    public FractionChallengeHandler(DelegatingHandler parent) {
        super(parent);
        
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals("Name")){
            TextHandler textHandler = new TextHandler(this, "Name");
            textHandler.startHandlingEvents();
        } else if (qName.equals("Item")){
            Fabric fabric = Fabric.valueOf(attributes.getValue("key"));
            String[] fractString = attributes.getValue("value").split("/");
            int numerator = Integer.parseInt(fractString[0]);
            int denominator = Integer.parseInt(fractString[1]);
            Fraction fraction = new Fraction(numerator, denominator);
            
            map.put(fabric, fraction);
            
            
        } else if(qName.equals("DateCreated")){
        	DateHandler dateHandler = new DateHandler(this, "DateCreated");
        	dateHandler.startHandlingEvents();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("FractionChallenge")){
            // Okay, we're done!
            FractionChallenge result = new FractionChallenge(name, map, dateCreated);
            stopHandlingEvents();
            parent.childFinished(result);
            
        }
    }

    /* (non-Javadoc)
     * @see umm.digiquilt.xmlsaveload.handlers.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        if(o instanceof Date){
        	dateCreated = (Date) o;
        }
        else{
        	name = (String) o;
        }
        
    }
    
}