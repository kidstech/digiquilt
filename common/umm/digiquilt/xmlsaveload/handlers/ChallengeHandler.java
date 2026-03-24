/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload.handlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.FreeformChallenge;

/**
 * Parses a single Challenge.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeHandler extends DelegatingHandler {

    /**
     * The challenge that was created
     */
    Challenge challenge;
    
    /**
     * This collects text in case it's a FreeformChallenge.
     */
    StringBuilder collectedText = new StringBuilder();
    
    /**
     * @param parent
     */
    public ChallengeHandler(DelegatingHandler parent) {
        super(parent);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals("FractionChallenge")){
            FractionChallengeHandler fractionHandler =
                new FractionChallengeHandler(this);
            fractionHandler.startHandlingEvents();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // Save this text, in case it turns out that this was a freeform 
        // challenge.
        collectedText.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("Challenge")){
            if (challenge == null){
                // Oh, a challenge was never made. It must have been a 
                // freeform challenge. Luckily, we saved the text!
                challenge = new FreeformChallenge(collectedText.toString());
                
            }
            
            
            stopHandlingEvents();
            parent.childFinished(challenge);
        }
    }

    /* (non-Javadoc)
     * @see umm.digiquilt.xmlsaveload.handlers.DelegatingHandler#childFinished(java.lang.Object)
     */
    @Override
    public void childFinished(Object o) {
        challenge = (Challenge) o;
    }
    
}
