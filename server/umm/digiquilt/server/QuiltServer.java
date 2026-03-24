package umm.digiquilt.server;


import java.io.File;
import java.io.IOException;

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
        return new QuiltZeroconf(JmDNS.create());
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
