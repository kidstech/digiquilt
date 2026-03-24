/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload.handlers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import umm.digiquilt.model.Challenge;

/**
 * Parses a list of challenges.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeFileHandler extends DelegatingHandler {


    /**
     * List of all the challenges that were created.
     */
    List<Challenge> challenges = new ArrayList<Challenge>();
    

    /**
     * @param xmlReader
     */
    public ChallengeFileHandler(XMLReader xmlReader) {
        super(xmlReader);
    }
    
    /**
     * @return the list of challenges that were made.
     */
    public List<Challenge> getChallenges() {
        return challenges;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals("Challenge")){
            ChallengeHandler challenge =
                new ChallengeHandler(this);
            challenge.startHandlingEvents();
        }
        
        
    }

    /* (non-Javadoc)
     * @see umm.digiquilt.xmlsaveload.handlers.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        // Add the challenge
        challenges.add((Challenge) o);
    }
    

}
