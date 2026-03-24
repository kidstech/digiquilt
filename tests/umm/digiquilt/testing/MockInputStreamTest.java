/*
 * Created by jbiatek on Oct 30, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.testing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class MockInputStreamTest {
    
    
    /**
     * @throws Exception
     */
    @Test
    public void testWritingAndReadingStrings() throws Exception {
        MockInputStream mock = new MockInputStream();
        
        mock.returnString("Appended string");
        mock.returnString(", on the same line");
        mock.returnLine("");
        mock.returnLine("A different line");
        mock.returnLine("Another new line");
        
        BufferedReader test = new BufferedReader(new InputStreamReader(mock));
        
        assertEquals("Incorrect data received", 
                "Appended string, on the same line", 
                test.readLine());
        assertEquals("Incorrect data received", 
                "A different line", 
                test.readLine());
        assertEquals("Incorrect data received", 
                "Another new line", 
                test.readLine());
        assertNull("Stream should be finished", test.readLine());
    }

}
