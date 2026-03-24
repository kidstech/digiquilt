/*
 * Created by biatekjt on Mar 24, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.model;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FractionChallengeTest {

    /**
     * 
     */
    @Test
    public void testCreateChallenge(){
        String testName = "Test name";
        Map<Fabric, Fraction> testMap = new HashMap<Fabric, Fraction>();
        testMap.put(Fabric.BLACK, new Fraction(1, 2));
        testMap.put(Fabric.ORANGE, new Fraction(2, 4));
        FractionChallenge testChallenge = new FractionChallenge(testName, testMap);
       
        assertEquals(testChallenge.getDateCreated(), new Date());
        assertEquals(testName, testChallenge.getName());
        assertEquals(testMap, testChallenge.getFractionMap());
    }
    
    
    /**
     * 
     */
    @Test
    public void testBlockMatchesChallenge(){
        String testName = "Test name";
        Map<Fabric, Fraction> testMap = new HashMap<Fabric, Fraction>();
        testMap.put(Fabric.BLACK, new Fraction(1, 4));
        testMap.put(Fabric.ORANGE, new Fraction(2, 8));
        testMap.put(Fabric.GREEN, new Fraction(2, 4));
        
        Challenge testChallenge = new FractionChallenge(testName, testMap);
        
        Block testBlock1 = new Block(4);
        testBlock1.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 0);
        testBlock1.setPatch(Shape.FULLSQUARE.getPatch(Fabric.ORANGE), 1);
        testBlock1.setPatch(Shape.FULLSQUARE.getPatch(Fabric.GREEN), 2);
        testBlock1.setPatch(Shape.FULLSQUARE.getPatch(Fabric.GREEN), 3);
        
        assertTrue(testChallenge.blockMatchesChallenge(testBlock1));
        
        testBlock1.setPatch(Shape.FULLSQUARE.getPatch(Fabric.RED), 0);
        
        assertFalse(testChallenge.blockMatchesChallenge(testBlock1));
        
    }
    
    /**
     * 
     */
    @Test
    public void testToString(){
        String testName = "Test name";
        Map<Fabric, Fraction> testMap = new HashMap<Fabric, Fraction>();
        testMap.put(Fabric.BLACK, new Fraction(1, 8));

        Challenge testChallenge = new FractionChallenge(testName, testMap);
        assertEquals("(Test name) Create a quilt that is 1/8 Black.", testChallenge.toString());
        
        testMap.put(Fabric.YELLOW, new Fraction(2, 4));
        testChallenge = new FractionChallenge(testName, testMap);
        assertEquals("(Test name) Create a quilt that is 1/8 Black and 2/4 Yellow.", testChallenge.toString());
        
        testMap.put(Fabric.GREEN, new Fraction(1, 8));
        testChallenge = new FractionChallenge(testName, testMap);
        assertEquals("(Test name) Create a quilt that is 1/8 Black, 1/8 Green, and 2/4 Yellow.", testChallenge.toString());
        
        testMap.put(Fabric.BROWN, new Fraction(2, 16));
        testChallenge = new FractionChallenge(testName, testMap);
        assertEquals("(Test name) Create a quilt that is 1/8 Black, 2/16 Brown, 1/8 Green, and 2/4 Yellow.", testChallenge.toString());
        
        testMap.put(Fabric.INDIGO, new Fraction(4, 32));
        testChallenge = new FractionChallenge(testName, testMap);
        assertEquals("(Test name) Create a quilt that is 1/8 Black, 2/16 Brown, 1/8 Green, 4/32 Indigo, and 2/4 Yellow.", testChallenge.toString());
        
    }
    
    
    @Test
    public void testEqualTo(){
        Map<Fabric, Fraction> testMap1 = new HashMap<Fabric, Fraction>();
        testMap1.put(Fabric.BLACK, new Fraction(1, 8));

        Map<Fabric, Fraction> testMap2 = new HashMap<Fabric, Fraction>();
        testMap2.put(Fabric.BLACK, new Fraction(2, 16));
        
        FractionChallenge testChallenge1 = new FractionChallenge("A name", testMap1);
        FractionChallenge testChallenge2 = new FractionChallenge("A different name", testMap2);
        
        assertEquals(testChallenge1, testChallenge2);
        
        Map<Fabric, Fraction> testMap3 = new HashMap<Fabric, Fraction>();
        testMap3.put(Fabric.BLUE, new Fraction(2, 16));
        FractionChallenge testChallenge3 = new FractionChallenge("A name", testMap3);
        
        assertFalse(testChallenge1.equals(testChallenge3));
        assertFalse(testChallenge2.equals(testChallenge3));
        assertFalse(testChallenge3.equals(testChallenge1));
        assertFalse(testChallenge3.equals(testChallenge2));
        assertFalse(testChallenge1.equals("A string"));


    }
    
    
}
