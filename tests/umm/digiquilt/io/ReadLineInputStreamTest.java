/*
 * Created by jbiatek on May 14, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.io;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.junit.Test;

import umm.digiquilt.testing.MockInputStream;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class ReadLineInputStreamTest {

    /**
     * @throws IOException
     */
    @Test
    public void testReadLineInputStream() throws IOException{
        MockInputStream mock = new MockInputStream();
        String line1 = "Here is a line";
        String line2 = "Here is a strange character: $";
        byte[] data = new byte[2048];
        Random r = new Random();
        r.nextBytes(data);
        
        mock.returnLine(line1);
        mock.returnLine(line2);
        mock.returnBytes(data);
        
        
        assertEquals(line1, ReadLineInputStream.readLine(mock));
        assertEquals(line2, ReadLineInputStream.readLine(mock));
        for (int i=0; i<data.length; i++){
            assertEquals(data[i], (byte) mock.read());
        }
        
        // End of stream should have been reached
        assertEquals(-1, mock.read());
        assertNull(ReadLineInputStream.readLine(mock));
        
    }    
}
