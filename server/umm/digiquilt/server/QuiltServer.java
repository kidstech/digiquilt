package umm.digiquilt.server;


import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.jmdns.JmDNS;

import umm.digiquilt.server.QuiltZeroconf;

/**
 * Create and run a DigiQuilt server. This involves advertising it over JmDNS,
 * and starting the server with the appropriate handling behind it.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-20 19:04:14 $
 * @version $Revision: 1.6 $
 *
 */
public class QuiltServer implements Runnable {

    /**
     * A File indicating the directory that this server should save and
     * send files in.
     */
    private File currentDirectory;

    /**Create a new Runnable quilt server to listen for incoming
     * connections.
     * @param currentDirectory
     */
    public QuiltServer(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }


    public void run(){
        QuiltZeroconf mdns;
        try {
            mdns = getQuiltZeroconf();
        } catch (IOException e) {
            System.err.println("Could not start mDNS:");
            e.printStackTrace();
            return;
        }
        
        mdns.registerServer(currentDirectory.getName());
        
        ConnectionHandler handler = getDQPHandler(currentDirectory);
        MultiThreadedServer server = getServer(handler, 9090);
        
        server.start();
        
        // If we're here, that means the server has finished.
        mdns.unregisterAll();
    }

    /**
     * Protected method to create mDNS access, meant to be overridden for
     * test purposes.
     * 
     * @return a QuiltZeroconf instance
     * @throws IOException
     */
    protected QuiltZeroconf getQuiltZeroconf() throws IOException {
        InetAddress preferredAddress = findPreferredMdnsAddress();

        if (preferredAddress != null) {
            try {
                return new QuiltZeroconf(JmDNS.create(preferredAddress));
            } catch (IOException e) {
                // Fall back to default interface selection if explicit binding
                // fails on this machine.
                System.err.println("Could not bind mDNS to "
                        + preferredAddress + "; falling back to default.");
            }
        }

        return new QuiltZeroconf(JmDNS.create());
    }

    /**
     * Find a multicast-capable non-loopback address suitable for JmDNS.
     *
     * @return an address to bind JmDNS to, or null if no suitable address
     * could be found.
     */
    private InetAddress findPreferredMdnsAddress() {
        InetAddress fallbackAddress = null;

        try {
            Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();

            if (interfaces == null) {
                return null;
            }

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isUp()
                        || networkInterface.isLoopback()
                        || !networkInterface.supportsMulticast()) {
                    continue;
                }

                Enumeration<InetAddress> addresses =
                    networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (address.isLoopbackAddress()
                            || address.isAnyLocalAddress()
                            || address.isLinkLocalAddress()) {
                        continue;
                    }

                    // Prefer site-local IPv4 addresses because they are the
                    // most common and stable choice for local mDNS traffic.
                    if (address instanceof Inet4Address
                            && address.isSiteLocalAddress()) {
                        return address;
                    }

                    if (fallbackAddress == null) {
                        fallbackAddress = address;
                    }
                }
            }
        } catch (SocketException e) {
            return null;
        }

        return fallbackAddress;
    }


    /**
     * Protected method for creating the proper connection handler, meant
     * to be overridden for testing purposes.
     * 
     * @param dir
     * @return a connection handler for the server
     */
    protected ConnectionHandler getDQPHandler(File dir) {
        return new DQPHandler(dir);
    }


    /**Protected method for creating a multi threaded server. Meant to be
     * overridden for testing purposes.
     * 
     * @param handler
     * @param port
     * @return a multithreaded server with the given settings
     */
    protected MultiThreadedServer getServer(
            ConnectionHandler handler, int port) {
        return new MultiThreadedServer(handler, port);
    }

}
