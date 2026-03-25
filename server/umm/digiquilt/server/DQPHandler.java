/*
 * Created by jbiatek on Jul 5, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import umm.digiquilt.io.ReadLineInputStream;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;
import umm.digiquilt.xmlsaveload.ChallengeWriter;

/**<p>A handler that listens for DigiQuilt requests, and responds to them. This 
 * uses a unique protocol, inspired by HTTP but much simpler. Each
 * incoming request is responded to with "OK" or an error message before data 
 * is sent. Possible incoming requests include:</p>
 * <li>"LIST" : Respond with "OK" followed by a list of all files being served,
 * separated by line.</li>
 * <li>"GET filename" : Respond with "OK" followed by the contents of the
 * requested file, or "FILE NOT FOUND" if the specified file does not exist</li>
 * <li>"PUT filename" : Request to save a file on the server. Responds with "OK"
 * and waits for the contents of the file, or responds "FILE ALREADY EXISTS" if
 * there is already a file by that name on the server.</li>
 * <li>"AUTOSAVE studentname": Responds with "OK" and waits for a file which 
 * will be saved privately on the server.
 * <li>"APPEND filename": Responds with "OK" and waits for data to append to
 * the given file. This will only accept the name file and the challenge file,
 * all others will be rejected with "UNEDITABLE FILE", since this command
 * assumes that text is being handled.
 * <br>
 * <br>
 * <p>If the request is not any of these, "INVALID REQUEST" is returned.
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 05:44:20 $
 * @version $Revision: 1.3 $
 *
 */

public class DQPHandler implements ConnectionHandler {
    
    /**
     * The request header that the client sent.
     */
    String request;

    /**
     * InputStream for the current connection.
     */
    InputStream in;

    /**
     * Output connection to the client for this connection.
     */
    PrintStream out;
    
    /**
     * A File indicating the directory that this server should save and
     * send files in.
     */
    File currentDirectory;
    
    /**
     * The directory to save a secret copy of student work, with
     * timestamps.
     */
    File autosaveDirectory;

    /**
     * Create a new DQPHandler which will save files in the given directory.
     * 
     * @param currentDirectory
     */
    public DQPHandler(File currentDirectory){
        this.currentDirectory = currentDirectory;
        autosaveDirectory = new File(currentDirectory, "Autosaves");
        if (!autosaveDirectory.exists()){
            autosaveDirectory.mkdir();
        }
    }
    
    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.server.ConnectionHandler#createNewHandler()
     */
    public ConnectionHandler cloneHandler() {
        return new DQPHandler(currentDirectory);
    }

    /* (non-Javadoc)
     * @see umm.softwaredevelopment.digiquilt.server.ConnectionHandler#handleConnection(java.io.InputStream, java.io.OutputStream)
     */
    public void handleConnection(InputStream rawIn, OutputStream rawOut) {
        in = rawIn;
        out = new PrintStream(rawOut);
        try {
            request = ReadLineInputStream.readLine(in);

            if (request.startsWith("LIST")){
                listFiles();
            } else if (request.startsWith("GET ")){
                getRequest();
            } else if (request.startsWith("PUT ")){
                putRequest();
            } else if (request.startsWith("AUTOSAVE ")){
                autosave();
            } else if (request.startsWith("APPEND ")){
                append();
            } else if (request.equals("ADD CHALLENGES")){
                addChallenges();
            } else {
                out.println("INVALID REQUEST");
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deal with a LIST request. This will respond with a list of
     * all the files being served (excludes directories and hidden files)
     */
    private void listFiles(){
        out.println("OK");
        File[] files = currentDirectory.listFiles();
        for (File file : files){
            if (file.isFile() && !file.isHidden()){
                out.println(file.getName());
            }
        }
        out.close();
    }

    /**
     * Deal with a GET request. This will respond with the specified
     * file's contents, or "FILE NOT FOUND" if the file doesn't exist.
     */
    private void getRequest(){
        String fileName = request.replaceFirst("GET ", "");
        File requestedFile = new File(currentDirectory, fileName);
        try {
            if (requestedFile.isFile()){
                out.println("OK");
                FileInputStream fis = new FileInputStream(requestedFile);
                int bytesRead;
                byte[] buffer = new byte[2048];
                while ((bytesRead = fis.read(buffer)) != -1){
                    out.write(buffer, 0, bytesRead);
                }
            } else {
                out.println("FILE NOT FOUND: "+fileName);
            }
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Handle a PUT request. This will attempt to save the file specified
     * by the client in the main directory.
     */
    private void putRequest(){
        String fileName = request.replaceFirst("PUT ", "");
        File save = new File(currentDirectory, fileName);
        this.saveFile(save);
        
        if (fileName.endsWith(SaveHandler.QUILT_EXT)){
            // We should save a copy in the autosave directory as well.
            String studentName = fileName.replaceFirst(" - .*", "");
            String quiltName = fileName.replaceAll("^.* - ","");
            File autosave = new File(autosaveDirectory, 
                    studentName + " " + getTimestamp() + " " + quiltName);
            try {
                FileReader fileIn = new FileReader(save);
                FileWriter fileOut = new FileWriter(autosave);

                int c;
                while ((c = fileIn.read()) != -1){
                    fileOut.write(c);
                }
                fileIn.close();
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Deal with an AUTOSAVE request. This will save a file to the secret
     * Autosave directory with a timestamp and the student's name.
     */
    private void autosave(){
        String studentName = request.replaceFirst("AUTOSAVE ", "");
        File autosaveFile = new File(autosaveDirectory, 
                studentName + " " + getTimestamp() + " (autosave)"+SaveHandler.QUILT_EXT);
        saveFile(autosaveFile);
    }
    
    
    /**
     * Append data to the end of the name file. 
     */
    private void append() {
        String filename = request.replaceFirst("APPEND ", "");
        
        // Check that it's either the name or challenge file
        if (!filename.equalsIgnoreCase(SaveHandler.NAME_FILE_LOCATION)){
            out.println("UNEDITABLE FILE");
            out.close();
            return;
        }
        
        // Okay, we'll try to append it now.
        try {
            FileWriter fileOut = new FileWriter(
                    new File(currentDirectory, filename), true);
            BufferedReader charIn = new BufferedReader(
                    new InputStreamReader(in));
            out.println("OK");
            int temp;
            while ((temp = charIn.read()) != -1){
                fileOut.write(temp);
            }
            fileOut.close();
            
        } catch (IOException e) {
            out.println("I/O ERROR");
        }
        out.close();
    }

    /**
     * 
     */
    private void addChallenges() {
        out.println("OK");
        
        // Read in the challenges
        File challengeFile = new File(currentDirectory, 
                SaveHandler.CHALLENGE_FILE_LOCATION);
        
        List<Challenge> client;
        List<Challenge> existing;
        try {
            ChallengeFileParser fromOutside = new ChallengeFileParser(in);
            client = fromOutside.getChallenges();
            
            if (challengeFile.exists()){
                ChallengeFileParser local = 
                    new ChallengeFileParser(new FileInputStream(challengeFile));
                existing = local.getChallenges();
            } else {
                existing = new ArrayList<Challenge>();
            }

        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        

        
        // Add the unique ones
        for (Challenge newChallenge : client){
            if (!existing.contains(newChallenge)){
                existing.add(newChallenge);
            }
            
        }
        
        // Write back to the file
        ChallengeWriter writer = new ChallengeWriter();
        try {
            writer.writeToStream(existing, new FileOutputStream(challengeFile));
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        out.close();
    }

    /**Try to save whatever is coming in the given InputStream to the
     * given File. If the file already exists, the client will be informed
     * and the connection will be closed.
     * @param filename 
     */
    private void saveFile(File filename){
        try{
            if (filename.exists()){
                out.println("FILE ALREADY EXISTS: "+filename.getName());
            } else {
                out.println("OK");
                FileOutputStream fos = new FileOutputStream(filename);
                int bytesRead;
                byte[] buffer = new byte[2048];
                while ((bytesRead = in.read(buffer)) != -1){
                    fos.write(buffer, 0, bytesRead);
                }
            }
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @return a String containing a the current date and time.
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        return sdf.format(new Date());
    }

}
