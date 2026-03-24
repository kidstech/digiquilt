package umm.digiquilt.savehandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import umm.digiquilt.io.ReadLineInputStream;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.xmlsaveload.ChallengeWriter;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * Handles negotiation with a DigiQuilt server. A connection can be
 * given to this object, which will help facilitate talking to the server.
 * Note that a DQPClient object can only be used <i>once</i>, after it
 * has finished talking to the server it will close all connections and
 * become unusable. You will need to create another connection and give it
 * to a brand new DQPClient in order to do something else.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-20 22:34:34 $
 * @version $Revision: 1.1 $
 *
 */
public class DQPClient {

    /**
     * The input stream from the server.
     */
    private InputStream inStream;

    /**
     * The output stream to the server.
     */
    private OutputStream outStream;

    /**
     * Flag indicating if we've closed the streams.
     */
    private boolean isClosed = false;

    /**
     * The message to throw back if someone tries to use this object
     * after it has been closed.
     */
    private static final String CLOSED_MESSAGE = 
        "This connection has been closed.";

    /**
     * Create a new DQPClient, which will handle the given connection
     * to a server. Once a command is done, this DQPClient and these
     * connections will be closed and unusable.
     * 
     * @param in
     * @param out
     */
    public DQPClient(InputStream in, OutputStream out) {
        inStream = in;
        outStream = out;
    }

    /**
     * Perform a LIST command on the server and close this connection.
     * 
     * @return a list containing each line of the server's response.
     * @throws IOException if an I/O error occurs, or the server reports
     * an error.
     */
    public List<String> list() throws IOException {
        checkClosed();
        PrintStream out = new PrintStream(outStream);
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(inStream));


        out.println("LIST");
        checkServerResponse(in.readLine());

        List<String> listResult = new ArrayList<String>();
        String line;
        while ((line = in.readLine()) != null){
            listResult.add(line);
        }
        out.close();
        in.close();
        return listResult;
    }

    /**
     * Request the contents of a file from the server. The contents of the
     * file will be written to the given OutputStream.
     * 
     * @param filename The file on the server to GET.
     * @return The contents of the file from the server.
     * @throws IOException if an I/O error occurs, or if the server
     * reports an error.
     */
    public byte[] get(String filename) throws IOException {
        checkClosed();
        PrintStream out = new PrintStream(outStream);

        out.println("GET "+filename);
        checkServerResponse(ReadLineInputStream.readLine(inStream));

        byte[] buffer = new byte[2048];
        int bytesRead;
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        
        while ((bytesRead = inStream.read(buffer)) != -1){
            byteArray.write(buffer, 0, bytesRead);
        }
        
        inStream.close();
        outStream.close();
        
        return byteArray.toByteArray();
    }

    /**
     * Attempt to PUT a file onto the server using the given filename and
     * data. 
     * 
     * @param filename
     * @param data the contents of the file
     * @throws IOException if an I/O error occurs or if the server reports
     * an error. 
     */
    public void put(String filename, byte[] data) throws IOException {
        checkClosed();
        PrintStream out = new PrintStream(outStream);
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(inStream));

        out.println("PUT "+filename);
        checkServerResponse(in.readLine());

        out.write(data);

        out.close();
        in.close();
    }

    /**
     * Attempt to AUTOSAVE to the server with the given student name and
     * XML.
     * 
     * @param studentName
     * @param xml
     * @throws IOException if an I/O error occurs or if the server reports
     * an error.
     */
    public void autosave(String studentName, SaveBlockXML xml) throws IOException {
        checkClosed();
        PrintStream out = new PrintStream(outStream);
        BufferedReader in = 
            new BufferedReader(new InputStreamReader(inStream));
        
        out.println("AUTOSAVE "+studentName);
        checkServerResponse(in.readLine());
        
        xml.writeDocumentToStream(out);
        
        in.close();
        out.close();
    }
    
    /**
     * Append to a file on the server. This will be denied if you specify
     * a file other than the name file.
     * 
     * @param filename The file to append to (must be names.txt)
     * @param contents The line(s) of text to write to the file
     * @throws IOException 
     */
    public void append(String filename, String... contents)throws IOException{
        checkClosed();
        PrintStream out = new PrintStream(outStream);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inStream));
        
        out.println("APPEND "+filename);
        checkServerResponse(in.readLine());
        
        // Okay, we're good to go. Let's send in the strings we got.
        for (String line : contents){
            out.println(line);
        }
        out.close();
        in.close();
    }

    /**
     * @param challenges
     */
    public void addChallenges(List<Challenge> challenges) throws IOException{
        checkClosed();
        
        PrintStream out = new PrintStream(outStream);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inStream));
        
        out.println("ADD CHALLENGES");
        checkServerResponse(in.readLine());
        
        ChallengeWriter xml = new ChallengeWriter();
        xml.writeToStream(challenges, out);
        
    }

    /**
     * @return true if this DQPClient has been used, false if it is still
     * open.
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Check to see if this is closed. If it is, throw an exception. If
     * it isn't, set the flag to true.
     */
    private void checkClosed(){
        if (isClosed()){
            throw new IllegalStateException(CLOSED_MESSAGE);
        }
        isClosed = true;
    }

    /**
     * Read the given line, and if it's not "OK" throw an exception
     * specifying whatever the server is unhappy about. 
     * 
     * @param response
     * @throws IOException
     */
    private void checkServerResponse(String response) throws IOException {
        if (!response.equals("OK")){
            throw new IOException("Server reported error: "+response);
        }
    }


}
