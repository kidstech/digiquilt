/*
 * Created by jbiatek on Jul 3, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for classes which can communicate over a network or other one-on
 * one stream connection.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 00:45:14 $
 * @version $Revision: 1.2 $
 *
 */

public interface ConnectionHandler {

    /**
     * This should essentially do the same thing as calling the constructor
     * for this object, returning a new instance. 
     * 
     * @return a new instance of this ConnectionHandler;
     */
    public ConnectionHandler cloneHandler();
    
    /**
     * Take over talking to a peer. The protocol/behavior is up to the 
     * implementing class.
     * 
     * @param in Input coming from the peer.
     * @param out Output to the other peer.
     */
    public void handleConnection(InputStream in, OutputStream out);

}
