/*
 * Created by jbiatek on May 14, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * An InputStream which deals with the problem of reading both character
 * data and binary data at the same time. The readLine() method will
 * treat data from an underlying InputStream as characters up to
 * the first newline character it encounters. The other InputStream
 * methods are still there to deal with binary data.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class ReadLineInputStream {
    /**
     * Read the input stream as character data up to the first 
     * newline character. The resulting line (without newlines)
     * will be returned
     * 
     * @return the next line of text from this stream, or null if the
     * stream has ended and there is no more data
     * @throws IOException if an IO problem occurs
     */
    public static String readLine(InputStream in) throws IOException {
        List<Byte> line = new ArrayList<Byte>();
        int b;
        while ((b = in.read()) != -1){
            line.add(Byte.valueOf((byte) b));
            if (b == '\n'){
                break;
            }
        }
        if (line.size() == 0){
            return null;
        }
        
        
        byte[] array = new byte[line.size()];
        for (int i=0; i<line.size(); i++){
            array[i] = line.get(i).byteValue();
        }
        String str = new String(array);
        return str.toString().replaceAll("[\r\n]", "");
    }

}
