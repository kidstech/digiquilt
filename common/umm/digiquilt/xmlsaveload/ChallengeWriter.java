/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload;

import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeWriter {

    /**
     * Write a list of challenges out to the given stream.
     * 
     * @param list
     * @param out
     * @throws ParserConfigurationException 
     */
    public void writeToStream(List<Challenge> list, OutputStream out) {
        Document output;
        try {
            output = makeNewXMLDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        
        output.appendChild(output.createElement("Challenges"));
        
        for (Challenge challenge : list){
            saveChallenge(challenge, output, output.getFirstChild());
        }
        
        writeDocumentToStream(output, out);
    }

    
    /**
     * @return a new, empty XML Document
     * @throws ParserConfigurationException
     */
    private Document makeNewXMLDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        
        return document;
    }
    
    /**
     * Write the given Document to the given stream.
     * 
     * @param doc
     * @param out
     */
    private void writeDocumentToStream(Document doc, OutputStream out){
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(out));
        } catch (TransformerException e){
            e.printStackTrace();
        }
        
    }

    /**
     * Save a Challenge to XML. This will append the given Challenge to the node
     * that is passed in.
     * 
     * @param challenge The challenge to save
     * @param document The overall document being written to
     * @param parentNode The node that this challenge should be appended to
     */
    public void saveChallenge(Challenge challenge, Document document, 
            Node parentNode) {
        Element challengeElement = document.createElement("Challenge");
        
        
        if (challenge instanceof FractionChallenge){
            saveChallenge((FractionChallenge) challenge, challengeElement);
        } else {
            saveChallenge(challenge.toString(), challengeElement);
        }
        
        parentNode.appendChild(challengeElement);
    }
    
    /**
     * Save a FractionChallenge to XML.
     * 
     * @param challenge
     * @param parentNode
     */
    private void saveChallenge(FractionChallenge challenge, Node parentNode){
        Document owner = parentNode.getOwnerDocument();
        
        Element topNode = owner.createElement("FractionChallenge");
        Element nameNode = owner.createElement("Name");
        Element dateNode = owner.createElement("DateCreated");
        Element fractionsNode = owner.createElement("Fractions");
        
        nameNode.setTextContent(challenge.getName());
        dateNode.setTextContent(challenge.getXMLDate());
        for (Fabric fabric : challenge.getFractionMap().keySet()){
            Fraction fraction = challenge.getFractionMap().get(fabric);
            Element itemNode = owner.createElement("Item");
            itemNode.setAttribute("key", fabric.toString());
            itemNode.setAttribute("value", fraction.toString());
            
            fractionsNode.appendChild(itemNode);
            
        }
        
        topNode.appendChild(nameNode);
        topNode.appendChild(dateNode);
        topNode.appendChild(fractionsNode);

        parentNode.appendChild(topNode);
    }

    /**
     * Save a challenge as a simple String.
     * 
     * @param challengeText
     * @param parentNode
     */
    private void saveChallenge(String challengeText, Node parentNode) {
        parentNode.setTextContent(challengeText);
    }

}
