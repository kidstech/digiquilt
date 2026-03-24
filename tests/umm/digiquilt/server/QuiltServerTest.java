package umm.digiquilt.server;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.InOrder;

import umm.digiquilt.server.ConnectionHandler;
import umm.digiquilt.server.DQPHandler;
import umm.digiquilt.server.MultiThreadedServer;
import umm.digiquilt.server.QuiltServer;
import umm.digiquilt.server.QuiltZeroconf;

/**
 * Test the server and make sure that it's using the server, handler, and
 * zeroconf correctly.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-20 19:04:14 $
 * @version $Revision: 1.2 $
 *
 */
public class QuiltServerTest {

    /**
     * The port that it should request
     */
    int expectedPort = 9090;

    /**
     * Whether or not QuiltServer called the mock multithreaded server's
     * start() method.
     */
    boolean serverGotStarted = false;

    /**
     * Give QuiltServer a whole lot of mocks, and make sure that it puts
     * them together the way they're supposed to go.
     * 
     * @throws Exception
     */
    @Test
    public void testRunningTheServer() throws Exception{
        final File testFile = new File("fake file");
        
        final QuiltZeroconf mockZeroconf = mock(QuiltZeroconf.class);
        final MultiThreadedServer mockServer = 
                            mock(MultiThreadedServer.class);
        final ConnectionHandler mockHandler = mock(ConnectionHandler.class);

        QuiltServer testServer = new QuiltServer(testFile){

            @Override
            protected QuiltZeroconf getQuiltZeroconf() throws IOException{
                return mockZeroconf;
            }

            @Override
            protected ConnectionHandler getDQPHandler(File dir){
                assertEquals("QuiltServer asked for the wrong directory",
                        testFile, dir);
                return mockHandler;
            }

            @Override
            protected MultiThreadedServer 
                        getServer(ConnectionHandler handler, int port){
                boolean isStub = handler == mockHandler;
                assertTrue(
                        "Mock server wasn't given the stub handler", isStub);
                assertEquals("Incorrect port given", expectedPort, port);
                return mockServer;
            }

        };

        InOrder order = inOrder(mockZeroconf, mockServer);

        // No need for a separate thread, the mocks will make sure it
        // finishes quickly
        testServer.run();

        
        order.verify(mockZeroconf).registerServer(testFile.getName());
        order.verify(mockServer).start();
        order.verify(mockZeroconf).unregisterAll();
    }
    
    /**
     * Test the methods mocked out above.
     * 
     * @throws Exception
     */
    @Test
    public void testFactoryMethods() throws Exception {
        final File testFile = new File("fake file");
        QuiltServer server = new QuiltServer(testFile);
        QuiltZeroconf qz = server.getQuiltZeroconf();
        assertEquals("QuiltZeroconf should be the real thing", 
                QuiltZeroconf.class, qz.getClass());
        
        ConnectionHandler handler = server.getDQPHandler(testFile);
        assertEquals("Handler should be a real DQPHandler", 
                DQPHandler.class, handler.getClass());
        
        MultiThreadedServer mts = server.getServer(handler, 100);
        assertEquals("Server should be a real MTS", 
                MultiThreadedServer.class, mts.getClass());
        
    }

}
