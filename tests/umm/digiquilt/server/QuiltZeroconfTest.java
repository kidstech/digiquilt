/*
 * Created by jbiatek on Jul 5, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.net.SocketFactory;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import umm.digiquilt.savehandler.DQPClient;
import umm.digiquilt.server.QuiltZeroconf;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-20 22:28:00 $
 * @version $Revision: 1.6 $
 *
 */

public class QuiltZeroconfTest {

    /**
     * The Bonjour type for a digiquilt server
     */
    static String serverType = "_digiQuiltServ._tcp.local.";
    /**
     * The port they run on
     */
    static int serverPort = 9090;

    /**
     * Test advertising of a server on mDNS, as well as turning it off.
     * 
     * @throws Exception
     */
    @Test
    public void testServerRegistration() throws Exception {
        String serverName = "Test server";
        JmDNS mockJmdns = mock(JmDNS.class);
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmdns);
        boolean success = testZero.registerServer(serverName);
        assertTrue("QuiltZeroconf reported failure to register", success);

        // Let's check their work:
        ArgumentCaptor<ServiceInfo> argument = 
            ArgumentCaptor.forClass(ServiceInfo.class);
        verify(mockJmdns).registerService(argument.capture());
        
        ServiceInfo result = argument.getValue();
        assertTrue("Server info had the wrong name", 
                result.getName().startsWith("Test server"));
        assertEquals("Server info had the wrong type",
                serverType, result.getType());
        assertEquals("Server info had the wrong port",
                serverPort, result.getPort());


        testZero.unregisterAll();

        verify(mockJmdns).unregisterAllServices();

    }
    
    /**
     * Test that if JmDNS throws an exception, that registering a server
     * is reported as a failure.
     * @throws Exception
     */
    @Test
    public void testRegistrationFailure() throws Exception {
        String serverName = "Test server 2";
        JmDNS mockJmdns = mock(JmDNS.class);
        doThrow(new IOException())
            .when(mockJmdns).registerService(any(ServiceInfo.class));
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmdns);
        boolean success = testZero.registerServer(serverName);
        assertFalse("QZ reported success even though JmDNS failed", success);

    }

    /**
     * Test adding and removing of listeners.
     * 
     * @throws Exception
     */
    @Test
    public void testAddedAsListener() throws Exception {
        JmDNS mockJmdns = mock(JmDNS.class);
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmdns);

        ServiceListener mockService1 = mock(ServiceListener.class);

        ServiceListener mockService2 = mock(ServiceListener.class);

        testZero.addServiceListener(mockService1);
        verify(mockJmdns).addServiceListener(serverType, mockService1);
        
        testZero.addServiceListener(mockService2);
        verify(mockJmdns).addServiceListener(serverType, mockService2);

        testZero.removeServiceListener(mockService1);
        verify(mockJmdns).removeServiceListener(serverType, mockService1);

        testZero.removeServiceListener(mockService2);
        verify(mockJmdns).removeServiceListener(serverType, mockService2);

    }

    /**
     * Test getting a connection when JmDNS fails to find anything.
     * 
     * @throws Exception
     */
    @Test
    public void testGetConnectionFails() throws Exception {
        JmDNS mockJmdns = mock(JmDNS.class);
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmdns);
        
        DQPClient client = testZero.getConnection("Test server");
        // The mock object will return null, and so should the test QZ.
        assertNull(client);
        // Make sure it tried, though.
        verify(mockJmdns).getServiceInfo(serverType, "Test server");
        
    }
    
    /**
     * Test getting a connection when JmDNS fails to find anything when we
     * specify a timeout.
     * 
     * @throws Exception
     */
    @Test
    public void testGetConnectionFailsWithTimeout() throws Exception {
        JmDNS mockJmdns = mock(JmDNS.class);
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmdns);
        
        DQPClient client = testZero.getConnection("Test server", 9797);
        // The mock object will return null, and so should the test QZ.
        assertNull(client);
        // Make sure it tried, though.
        verify(mockJmdns).getServiceInfo(serverType, "Test server", 9797);
        
    }
    
    /**
     * @throws Exception
     */
    @SuppressWarnings("boxing")
    @Test
    public void testGetConnectionSuccess() throws Exception {
        ServiceInfo testInfo = mock(ServiceInfo.class);
        when(testInfo.getType()).thenReturn(serverType);
        when(testInfo.getName()).thenReturn("Fake server");
        when(testInfo.getPort()).thenReturn(9000);
        when(testInfo.getTextString()).thenReturn("fake=yes");
        // This should be an invalid IP address...
        InetAddress fakeAddress = InetAddress.getByName("240.0.0.0");
        when(testInfo.getAddress()).thenReturn(fakeAddress);
        
        // Set up the test JmDNS
        JmDNS mockJmDNS = mock(JmDNS.class);
        when(mockJmDNS.getServiceInfo(serverType, "Fake server"))
                .thenReturn(testInfo);
        
        // Set up the mock socket and factory
        Socket mockSocket = mock(Socket.class);
        InputStream mockIn = mock(InputStream.class);
        OutputStream mockOut = mock(OutputStream.class);
        
        when(mockSocket.getInputStream()).thenReturn(mockIn);
        when(mockSocket.getOutputStream()).thenReturn(mockOut);
        
        SocketFactory mockFactory = mock(SocketFactory.class);
        when(mockFactory.createSocket(fakeAddress, 9000)).thenReturn(mockSocket);

        // The actual test object
        QuiltZeroconf testZero = new QuiltZeroconf(mockJmDNS, mockFactory);


        // Test getting a connection, finally!
        DQPClient connection = testZero.getConnection("Fake server");
        verify(mockJmDNS).getServiceInfo(serverType, "Fake server");
        verify(mockFactory).createSocket(fakeAddress, 9000);
        assertNotNull(connection);
        
        
        // Check again with a specified timeout
        when(mockJmDNS.getServiceInfo(serverType, "Fake server", 1234))
                .thenReturn(testInfo);
        
        connection = testZero.getConnection("Fake server", 1234);
        verify(mockJmDNS).getServiceInfo(serverType, "Fake server", 1234);
        verify(mockFactory, times(2)).createSocket(fakeAddress, 9000);
        assertNotNull(connection);
        
        // Maybe check to make sure that the DQPClient actually got the two
        // mock streams sometime in the future.
        

    }
    
}
