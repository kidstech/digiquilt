/*
 * Created by jbiatek on Jul 3, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Test;

import umm.digiquilt.server.ConnectionHandler;
import umm.digiquilt.server.MultiThreadedServer;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 00:45:14 $
 * @version $Revision: 1.3 $
 *
 */

public class MultiThreadedServerTest {

    /**
     * Make sure that the multi threaded server can hand out connections to 
     * multiple ConnectionHandlers over the network.
     * 
     * @throws Exception
     */
    @Test
    public void testMultiThreadedServer() throws Exception{
        int port = 9999;
        final MultiThreadedServer server = 
            new MultiThreadedServer(new EchoHandler(), port);
        Thread serverThread = new Thread(
                new Runnable(){

                    public void run() {
                        server.start();
                    }
                    
                });
        serverThread.start();
        
        // Give it a bit of time to get ready
        Thread.sleep(100);
        
        Socket connection1 = new Socket(InetAddress.getLocalHost(), port);
        Socket connection2 = new Socket(InetAddress.getLocalHost(), port);
        
        BufferedReader in1 = new BufferedReader(
                new InputStreamReader(connection1.getInputStream()));
        BufferedReader in2 = new BufferedReader(
                new InputStreamReader(connection2.getInputStream()));
        PrintStream out1 = new PrintStream(connection1.getOutputStream());
        PrintStream out2 = new PrintStream(connection2.getOutputStream());
        
        String test1 = "Testing the server, try #1";
        out1.println(test1);
        String result = in1.readLine();
        assertEquals("The server is not delegating properly", test1, result);
        
        String test2 = "Testing a second connection at the same time";
        out2.println(test2);
        result = in2.readLine();
        assertEquals("The server is not delegating properly", test2, result);
        
        in1.close();
        in2.close();
        out1.close();
        out2.close();
        connection1.close();
        connection2.close();
        
        serverThread.interrupt();
        
        // Give it some time to stop
        Thread.sleep(150);
        
        assertFalse("The server thread failed to stop", serverThread.isAlive());
        
    }

    
    
    /**
     * A simple connection handler which echoes back each line it gets.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-06 00:45:14 $
     * @version $Revision: 1.3 $
     *
     */
    private class EchoHandler implements ConnectionHandler{

        
        public ConnectionHandler cloneHandler() {
            return new EchoHandler();
        }
        
        public void handleConnection(InputStream in, OutputStream out){
            PrintStream out_ = new PrintStream(out);
            BufferedReader in_ = new BufferedReader(
                    new InputStreamReader(in));
            
            String input;
            try {
                while ((input = in_.readLine()) != null){
                    out_.println(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }
    }
    
}
