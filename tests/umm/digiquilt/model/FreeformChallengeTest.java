/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.model;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FreeformChallengeTest {

    /**
     * 
     */
    @Test
    public void testFreeformChallenge(){
        String text = "Make a random mess of stuff!";
        FreeformChallenge challenge = new FreeformChallenge(text);
        
        assertEquals(text, challenge.toString());
        // Even these random quilts should match it:
        for (int i=0; i<10; i++){
            Block block = BlockTest.createRandomBlock(16);
            assertTrue(challenge.blockMatchesChallenge(block));
        }
        
        
        
    }
    
    /**
     * 
     */
    @Test
    public void testEqualTo(){
        String text = "Sample free form challenge";
        assertEquals(new FreeformChallenge(text), new FreeformChallenge(text));
        
        assertFalse(new FreeformChallenge(text).equals(
                new FreeformChallenge("different text")));
    }
    
}
