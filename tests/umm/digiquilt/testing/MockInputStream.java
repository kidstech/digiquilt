/*
 * Created by jbiatek on Oct 30, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.testing;

import java.io.ByteArrayInputStream;

/**
 * An InputStream that only returns data that was loaded into
 * it first. You can create this stream, put what you want into
 * it, and then hand it off to some other object that expects an
 * InputStream to read.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class MockInputStream extends ByteArrayInputStream {

    /**
     * Create a new, empty MockInputStream.
     */
    public MockInputStream() {
        super(new byte[0]);
    }
    
    /**
     * Add a string to the end of the input stream.
     * 
     * @param str
     */
    public void returnString(String str){
        returnBytes(str.getBytes());
        
    }
    
    /**
     * Add a string to the end of the input stream followed
     * by the system's default line separator.
     * 
     * @param str
     */
    public void returnLine(String str){
        String newLine = System.getProperty("line.separator");
        returnBytes((str+newLine).getBytes());
    }
    
    /**
     * Add the given bytes to the end of the input stream.
     * 
     * @param data
     */
    public void returnBytes(byte[] data){
        byte[] newArray = new byte[buf.length + data.length];
        
        System.arraycopy(buf, 0, newArray, 0, buf.length);
        System.arraycopy(data, 0, newArray, buf.length, data.length);
        
        buf = newArray;
        count = buf.length;
    }

}
