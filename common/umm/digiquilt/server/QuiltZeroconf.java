/*
 * Created by jbiatek on Jul 5, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import java.io.IOException;
import java.net.Socket;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.net.SocketFactory;

import umm.digiquilt.savehandler.DQPClient;

/**
 * A class to provide a single place to perform zeroconf related tasks for
 * DigiQuilt specifically.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-23 22:15:14 $
 * @version $Revision: 1.4 $
 *
 */

public class QuiltZeroconf {

    /**
     * mDNS type for a DigiQuilt server.
     */
    static final String SERVER_TYPE = "_digiQuiltServ._tcp.local.";

    /**
     * The port that a server should run on.
     */
    static final int PORT = 9090;

    /**
     * An instance of JmDNS to boss around.
     */
    private JmDNS jmdns;
    
    /**
     * Factory to create socket connections.
     */
    private SocketFactory socketFactory = SocketFactory.getDefault();

    /**
     * Create a new QuiltZeroconf
     * @param jmdns The JmDNS to use
     * 
     * @throws IOException
     */
    public QuiltZeroconf(JmDNS jmdns) throws IOException {
        this.jmdns = jmdns;
    }
    
    /**
     * Create a new QuiltZeroconf object, which will use the given
     * SocketFactory to make connections.
     * 
     * @param jmdns
     * @param factory
     */
    public QuiltZeroconf(JmDNS jmdns, SocketFactory factory){
        this.jmdns = jmdns;
        this.socketFactory = factory;
    }

    /**
     * Advertise a DigiQuilt server on at this location with the given name
     * over mDNS.
     * 
     * @param serverName The name for the server 
     * 
     * @return true if the service was successfully registered
     */
    public boolean registerServer(String serverName) {
        ServiceInfo newInfo = ServiceInfo.create(
                SERVER_TYPE, 
                serverName,
                PORT,
        "version = 0.0");
        try {
            jmdns.registerService(newInfo);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Unregister all services from mDNS.
     */
    public void unregisterAll() {
        jmdns.unregisterAllServices();
    }

    /**
     * Add a DigiQuilt service listener.
     * 
     * @param listener
     */
    public void addServiceListener(ServiceListener listener) {
        jmdns.addServiceListener(SERVER_TYPE, listener);
    }


    /**
     * Remove a DigiQuilt service listener.
     * 
     * @param listener
     */
    public void removeServiceListener(ServiceListener listener) {
        jmdns.removeServiceListener(SERVER_TYPE, listener);
    }

    /**
     * Get a connection to the given server. This method could potentially
     * block for a while as the server is found, so don't call it from the
     * event thread.
     * 
     * @param serverName
     * @return a DQPClient connection to the server, or null if the server
     * could not be found.
     * @throws IOException
     */
    public DQPClient getConnection(String serverName) throws IOException {
        return getConnection(serverName, -1);
    }

    /**
     * Try to get a connection to the given server.  
     * 
     * @param serverName The name of the server to try to connect to.
     * @param timeout The time to spend trying to find the server before giving
     * up, in milliseconds.
     * @return A DQPClient connection to the server, or null if the server
     * could not be found before the timeout.
     * @throws IOException
     */
    public DQPClient getConnection(String serverName, int timeout) 
                                                        throws IOException{
        ServiceInfo info;
        if (timeout < 0){
            info = jmdns.getServiceInfo(SERVER_TYPE, serverName);
        } else {
            info = jmdns.getServiceInfo(SERVER_TYPE, serverName, timeout);
        }
        if (info != null) {
            Socket sock = socketFactory.createSocket(info.getAddress(), info.getPort());
            return new DQPClient(
                    sock.getInputStream(), sock.getOutputStream());
        }
        return null;
    }

}
