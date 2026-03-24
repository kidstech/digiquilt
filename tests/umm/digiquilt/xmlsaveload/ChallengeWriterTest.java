/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.FreeformChallenge;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeWriterTest {
    
    /**
     * @throws Exception
     */
    @Test
    public void testWritingList() throws Exception{
        List<Challenge> list = new ArrayList<Challenge>();
        
        String name1 = "First name";
        Map<Fabric, Fraction> map1 = new HashMap<Fabric, Fraction>();
        map1.put(Fabric.ORANGE, new Fraction(1, 3));
        map1.put(Fabric.BLACK, new Fraction(2, 6));
        map1.put(Fabric.WHITE, new Fraction(4, 12));
        list.add(new FractionChallenge(name1, map1));
        
        String name2 = "Second name";
        Map<Fabric, Fraction> map2 = new HashMap<Fabric, Fraction>();
        map2.put(Fabric.BLUE, new Fraction(1, 16));
        map2.put(Fabric.BROWN, new Fraction(1, 16));
        map2.put(Fabric.DARKGREEN, new Fraction(1, 16));
        map2.put(Fabric.GREEN, new Fraction(1, 16));
        map2.put(Fabric.INDIGO, new Fraction(1, 16));
        map2.put(Fabric.PINK, new Fraction(1, 16));
        map2.put(Fabric.RED, new Fraction(1, 16));
        map2.put(Fabric.REDVIOLET, new Fraction(1,16));
        map2.put(Fabric.VIOLET, new Fraction(1, 16));
        map2.put(Fabric.WHITE, new Fraction(1, 16));
        map2.put(Fabric.YELLOW, new Fraction(1, 16));
        map2.put(Fabric.TRANSPARENT, new Fraction(1, 16));
        list.add(new FractionChallenge(name2, map2));
        
        String text3 = "Make something that is really really messy.";
        list.add(new FreeformChallenge(text3));
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChallengeWriter writer = new ChallengeWriter();
        
        writer.writeToStream(list, out);
        
        // Read back in and make sure that the XML is correct
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SaveXMLTest.TestErrorHandler());
        Document result = builder.parse(in);
        
        NodeList rootNodes = result.getChildNodes();
        assertEquals(1, rootNodes.getLength());
        
        Node rootNode = rootNodes.item(0);
        
        assertEquals("Challenges", rootNode.getNodeName());
        assertEquals(list.size(), rootNode.getChildNodes().getLength());
        NodeList challenges = rootNode.getChildNodes();
        
        
        for (int i=0; i<list.size(); i++){
            verifyChallenge(list.get(i), challenges.item(i));
        }
        
    }
    
/*    @Test
    public void testWritingSingleFractionChallenge() throws Exception{
        String name1 = "First and only name";
        Map<Fabric, Fraction> map1 = new HashMap<Fabric, Fraction>();
        map1.put(Fabric.ORANGE, new Fraction(1, 3));
        map1.put(Fabric.BLACK, new Fraction(2, 6));
        map1.put(Fabric.WHITE, new Fraction(4, 12));
        Challenge challenge = new FractionChallenge(name1, map1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChallengeWriter writer = new ChallengeWriter();
        
        writer.writeToStream(challenge, out);

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SaveXMLTest.TestErrorHandler());
        Document result = builder.parse(in);
        
        NodeList rootNodes = result.getChildNodes();
        assertEquals(1, rootNodes.getLength());
        
        Node rootNode = rootNodes.item(0);
        
        verifyChallenge(challenge, rootNode);

    }

    @Test
    public void testWritingSingleFreeformChallenge() throws Exception {
        String text = "Make something that makes you laugh";
        Challenge challenge = new FreeformChallenge(text);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChallengeWriter writer = new ChallengeWriter();
        
        writer.writeToStream(challenge, out);

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SaveXMLTest.TestErrorHandler());
        Document result = builder.parse(in);
        
        NodeList rootNodes = result.getChildNodes();
        assertEquals(1, rootNodes.getLength());
        
        Node rootNode = rootNodes.item(0);
        
        verifyChallenge(challenge, rootNode);

        
    }
*/
    /**
     * @param challenge
     * @param item
     */
    public static void verifyChallenge(Challenge challenge, Node item){
        assertEquals("Challenge", item.getNodeName());
        
        if (challenge instanceof FractionChallenge){
            verifyChallenge((FractionChallenge) challenge, item);
            return;
        } else if (challenge instanceof FreeformChallenge){
            verifyChallenge((FreeformChallenge) challenge, item);
            return;
        }
        
        fail("Don't know what kind of Challenge object this is, so I can't check it");
    }
    
    /**
     * Check a Challenge tag which should contain a FractionChallenge.
     * 
     * @param challenge
     * @param item
     */
    private static void verifyChallenge(FractionChallenge challenge, Node item) {
        assertEquals("Challenge", item.getNodeName());
        
        assertEquals(1, item.getChildNodes().getLength());
        Node fractNode = item.getFirstChild();
        // Only child of the <Challenge> tag should be <FractionChallenge>:
        assertEquals("FractionChallenge", fractNode.getNodeName());
        
        // It should have 3 things: Name, Fractions, and DateCreated
        assertEquals(3, fractNode.getChildNodes().getLength());
        
        // Check the Name tag
        Node nameNode = fractNode.getChildNodes().item(0);
        assertEquals("Name", nameNode.getNodeName());
        assertEquals(challenge.getName(), nameNode.getTextContent());
        
        // Check the DateCreated tag
        Node dateNode = fractNode.getChildNodes().item(1);
        assertEquals("DateCreated", dateNode.getNodeName());
        assertEquals(challenge.getXMLDate(), dateNode.getTextContent());

        
        // Create our own map to keep track of fractions
        Map<Fabric, Fraction> myMap = 
            new HashMap<Fabric, Fraction>(challenge.getFractionMap());
        
        // Check the Fractions tag
        Node fractionsNode = fractNode.getChildNodes().item(2);
        assertEquals("Fractions", fractionsNode.getNodeName());
        // Make sure there are the right number of items
        assertEquals(myMap.size(), fractionsNode.getChildNodes().getLength());
        
        // Check each item
        NodeList items = fractionsNode.getChildNodes();
        for (int i=0; i<items.getLength(); i++){
            Node itemNode = items.item(i);
            // Needs to be called Item
            assertEquals("Item", itemNode.getNodeName());
            
            String key = itemNode.getAttributes()
                    .getNamedItem("key").getTextContent();
            String value = itemNode.getAttributes()
                    .getNamedItem("value").getTextContent();
            
            // Make sure it's a valid Fabric
            Fabric fabric = Fabric.valueOf(key);
            Fraction fraction = myMap.get(fabric);
            
            // Check that the fractions are exactly equal
            assertEquals(fraction.toString(), value);
            // Remove it from our list now that it's done
            myMap.remove(fabric);
            
        }
        
        // Should have found and removed all of the mappings.
        assertTrue(myMap.isEmpty());
        
    }

    /**
     * Check a FreeformChallenge
     * 
     * @param challenge
     * @param item
     */
    private static void verifyChallenge(FreeformChallenge challenge, Node item) {
        assertEquals("Challenge", item.getNodeName());
        assertEquals(challenge.toString(), item.getTextContent());
    }
    

}
