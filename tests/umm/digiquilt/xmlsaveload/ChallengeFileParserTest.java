/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.xmlsaveload;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.FreeformChallenge;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeFileParserTest {
    
    /**
     * Location of the XML file(s).
     */
    private static String xmlLocation = "/umm/digiquilt/xmlsaveload/";
    
    
    /**
     * @throws Exception 
     */
    @Test
    public void testChallengeFileLoading() throws Exception{
        ChallengeFileParser parser = new ChallengeFileParser(
                this.getClass().getResourceAsStream(xmlLocation+"challenges.xml"));
        
        List<Challenge> challenges = parser.getChallenges();
        
        assertEquals(4, challenges.size());
        
        FractionChallenge c0 = (FractionChallenge) challenges.get(0);
        
        assertEquals("Name #1", c0.getName());
        assertEquals(4, c0.getFractionMap().size());
        
        
        assertEquals(2, c0.getFractionMap().get(Fabric.WHITE).getNumerator());
        assertEquals(8, c0.getFractionMap().get(Fabric.WHITE).getDenominator());
        
        assertEquals(1, c0.getFractionMap().get(Fabric.BLACK).getNumerator());
        assertEquals(4, c0.getFractionMap().get(Fabric.BLACK).getDenominator());
        
        assertEquals(1, c0.getFractionMap().get(Fabric.BROWN).getNumerator());
        assertEquals(8, c0.getFractionMap().get(Fabric.BROWN).getDenominator());
        
        assertEquals(6, c0.getFractionMap().get(Fabric.TRANSPARENT).getNumerator());
        assertEquals(16, c0.getFractionMap().get(Fabric.TRANSPARENT).getDenominator());
        
        
        
        FractionChallenge c1 = (FractionChallenge) challenges.get(1);
        
        assertEquals("Name Two", c1.getName());
        assertEquals(3, c1.getFractionMap().size());
        
        
        assertEquals(3, c1.getFractionMap().get(Fabric.INDIGO).getNumerator());
        assertEquals(6, c1.getFractionMap().get(Fabric.INDIGO).getDenominator());
        
        assertEquals(1, c1.getFractionMap().get(Fabric.REDVIOLET).getNumerator());
        assertEquals(2, c1.getFractionMap().get(Fabric.REDVIOLET).getDenominator());
        
        assertEquals(1, c1.getFractionMap().get(Fabric.PINK).getNumerator());
        assertEquals(2, c1.getFractionMap().get(Fabric.PINK).getDenominator());
        
        
        
        
        FractionChallenge c2 = (FractionChallenge) challenges.get(2);
        
        assertEquals("Third Name", c2.getName());
        assertEquals(8, c2.getFractionMap().size());
        
        
        assertEquals(1, c2.getFractionMap().get(Fabric.REDVIOLET).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.REDVIOLET).getDenominator());
        
        assertEquals(1, c2.getFractionMap().get(Fabric.RED).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.RED).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.ORANGE).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.ORANGE).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.YELLOW).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.YELLOW).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.GREEN).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.GREEN).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.DARKGREEN).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.DARKGREEN).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.BLUE).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.BLUE).getDenominator());

        assertEquals(1, c2.getFractionMap().get(Fabric.VIOLET).getNumerator());
        assertEquals(8, c2.getFractionMap().get(Fabric.VIOLET).getDenominator());

        FreeformChallenge c3 = (FreeformChallenge) challenges.get(3);
        assertEquals("Make whatever you want to!", c3.toString());
        
    }



}
