/*
 * Created by jbiatek on Jul 3, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * A server which can handle multiple clients at the same time. The actual
 * communication is delegated to a ConnectionHandler.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 05:44:20 $
 * @version $Revision: 1.4 $
 *
 */

public class MultiThreadedServer {
    
    /**
     * The object that will take over handling the connection once it's made
     */
    private ConnectionHandler handler;
    
    /**
     * The port to listen on.
     */
    private int port;
    
    /**
     * Create a new MultiThreadedServer. This server will listen on the given
     * port, and any incoming connections will be handed off to the given 
     * ConnectionHandler to be dealt with on a new Thread. This means that 
     * multiple connections can be handled at the same time.
     * 
     * @param handler
     * @param port
     */
    public MultiThreadedServer(ConnectionHandler handler, int port){
        this.handler = handler;
        this.port = port;
    }

    
    /**
     * Start the server and begin listening for connections. If the socket can
     * be initialized without any problems, this method will block until the
     * thread is interrupted, at which point the socket will be released and
     * the method will return. However, any existing connections to clients
     * will continue to run on their individual daemon threads. 
     */
    public void start(){
        // Initialize the server socket if possible
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(100);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // We have the socket, let's listen for connections
        while (true){
            if (Thread.interrupted()){
                // We've been interrupted, let's stop
                break;
            }
            try {
                Socket connection = serverSocket.accept();
                final InputStream in = connection.getInputStream();
                final OutputStream out = connection.getOutputStream();
                // Hand this off to a handler on another thread
                final ConnectionHandler newHandler = handler.cloneHandler();
                Thread newThread = new Thread(
                        new Runnable(){

                            public void run() {
                                newHandler.handleConnection(in, out);
                            }
                            
                        });
                newThread.setDaemon(true);
                newThread.start();
                
            } catch (SocketTimeoutException soe){
                // This is okay, let's just go back to the top to see if we've
                // been interrupted or not.
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        
        // We are done here, try to shut things down if possible
        
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Well, we tried.
            e.printStackTrace();
        }
    }
}
