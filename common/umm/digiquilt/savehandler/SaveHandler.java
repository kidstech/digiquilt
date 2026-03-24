package umm.digiquilt.savehandler;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.server.QuiltZeroconf;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;
import umm.digiquilt.xmlsaveload.ChallengeWriter;
import umm.digiquilt.xmlsaveload.SaveBlockXML;
import umm.digiquilt.xmlsaveload.SavePatchXML;

/**A SaveHandler that saves both locally and to the current server, using
 * the given login name.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-28 21:59:12 $
 * @version $Revision: 1.1 $
 *
 */
public class SaveHandler {

    /**
     * The file on the server which contains the possible login names, one
     * per line.
     */
    public static final String NAME_FILE_LOCATION = "names.txt";
    /**
     * The file on the server containing all the challenges, in xml format.
     */
    public static final String CHALLENGE_FILE_LOCATION = "challenges.xml";
    
    /**
     * The default text the challenges file needs to have 
     */
    public static final String DEFAULT_CHALLENGE_FILE_TEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Challenges></Challenges>";
    
    /**
     * The file extension for DigiQuilt files, including the dot.
     */
    public static final String QUILT_EXT = ".xml.gz";
    
    /**
     * The file extension for DigiQuilt patch files, including the dot.
     */
    public static final String PATCH_EXT = ".patch.xml";
    
    /**
     * The file extension for DigiQuilt patch image files, including the dot.
     */
    public static final String PATCH_IMAGE_EXT = ".patch.png";

    /**
     * The name of the server that we're connecting to
     */
    private String serverName;

    /**
     * The student name to be added to quilt file names.
     */
    private String studentName;

    /**
     * QuiltZeroconf to get connection to the server
     */
    private QuiltZeroconf qz;
    
    /**
     * Listeners to notify when a sync happens.
     */
    private List<SyncListener> listeners = new ArrayList<SyncListener>();

    /**
     * The directory to save quilts to locally.
     */
    File localSaveDirectory;

    /**Create a new SaveHandler. When told to save, this will check both
     * locally and remotely for name conflicts, and if there are none, save
     * to both of them. 
     * @param qz A QuiltZeroconf to use to get connections
     * @param quiltDirectory The overall directory for DQ classes
     * @param serverName The name of the server to use.
     */
    public SaveHandler(QuiltZeroconf qz, File quiltDirectory,
            String serverName){
        this.serverName = serverName;
        this.qz = qz;
        localSaveDirectory = new File(quiltDirectory, serverName);
        // Create the local save directory if it doesn't exist already
//        if (!localSaveDirectory.exists()){
//            localSaveDirectory.mkdirs();
//        }
    }

    /**
     * @param name The name of the current student, which will be used when 
     * saving files to identify who created them.
     */
    public void setStudentName(String name){
        studentName = name;
    }

    /**
     * @return the name of the student
     */
    public String getStudentName(){
        return studentName;
    }



    /**
     * @return the directory where files will be saved.
     */
    public File getSaveDirectory(){
        return localSaveDirectory;
    }


    /**
     * Add a listener to be notified when a synchronize() happens.
     * 
     * @param listener The listener.
     */
    public void addSyncListener(SyncListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove the specified listener from the list.
     * 
     * @param listener
     */
    public void removeSyncListener(SyncListener listener){
        listeners.remove(listener);
    }

    /**Attempt to save the given SaveBlockXML to the appropriate place or places.
     * @param xml
     * @param icon 
     * @return true if the save was successful, false if the given block name
     * was already taken.
     * @throws IOException if an I/O error occurs
     */
    public boolean saveBlock(SaveBlockXML xml, BufferedImage icon, String blockName) 
    throws IOException {

        synchronize();

        String fileName = studentName+" - "+blockName;
        // Check the local copy for file name conflict first
        File fileLocation = new File(localSaveDirectory, fileName+QUILT_EXT);
        if (fileLocation.exists()){
            // This filename is already taken, refuse to overwrite.
            return false;
        }
        // Write XML to local file
        xml.writeOutDocumentToFile(fileLocation);

        // Write image out to file. I guess we could be overwriting something,
        // but I don't know how it would get there, and anyway, we just 
        // successfully wrote the XML, so we may as well overwrite to make it
        // match...
        File imageLocation = new File(localSaveDirectory, fileName+".png");
        OutputStream imageOut = new FileOutputStream(imageLocation);
        ImageIO.write(icon, "png", imageOut);
        imageOut.close();

        // Sync again to get the new files onto the server

        synchronize();

        return true;
    }
    
    /**Attempt to save the given SavePatchXML to the appropriate place or places.
     * @param xml
     * @param icon 
     * @return true if the save was successful, false if the given block name
     * was already taken.
     * @throws IOException if an I/O error occurs
     */
    public boolean savePatch(SavePatchXML xml, BufferedImage icon, String blockName) 
    throws IOException {

        synchronize();

        String fileName = studentName+" - "+blockName;
        // Check the local copy for file name conflict first
        File fileLocation = new File(localSaveDirectory, fileName+PATCH_EXT);
        if (fileLocation.exists()){
            // This filename is already taken, refuse to overwrite.
            return false;
        }
        // Write XML to local file
        xml.writeOutDocumentToFile(fileLocation);

        // Write image out to file. I guess we could be overwriting something,
        // but I don't know how it would get there, and anyway, we just 
        // successfully wrote the XML, so we may as well overwrite to make it
        // match...
        File imageLocation = new File(localSaveDirectory, fileName+PATCH_IMAGE_EXT);
        OutputStream imageOut = new FileOutputStream(imageLocation);
        ImageIO.write(icon, "png", imageOut);
        imageOut.close();

        // Sync again to get the new files onto the server

        synchronize();

        return true;
    }

    /**
     * Perform an autosave of the given SaveXML. This will be saved to the 
     * server but is not public to clients.
     * 
     * @param xml
     */
    public void autosave(SaveBlockXML xml){
        try {
            DQPClient connection = qz.getConnection(serverName, 500);
            if (connection != null){
                connection.autosave(studentName, xml);
            }
            // Right now, this just gives up if the server isn't there
            // when we need it. It might be a good idea to hang on to
            // this and save it later?
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return a list of possible names for this server.
     * @throws IOException
     */
    public List<String> getNames() throws IOException{
        DQPClient connection = qz.getConnection(serverName, 1000);
        if (connection == null){
            // Dang, the server isn't there. Let's load from disk.
            List<String> names;
            names = getLocalTextFile(
                    new File(localSaveDirectory, NAME_FILE_LOCATION));
            if (names != null){
                return names;
            }
            // Okay, well we're stuck. We don't want to return
            // null, though.
            return new ArrayList<String>();
        }
        // We got a connection. Let's load directly into memory
        // (this way, the act of simply looking at a server doesn't go 
        // making a bunch of files
        return getTextFileInMemory(NAME_FILE_LOCATION, connection);
    }

    /**
     * Download any files from the server that we don't have locally, and then
     * upload anything saved while offline to the server.
     * 
     * @throws IOException
     */
    public void synchronize() throws IOException {
        
        if (!localSaveDirectory.exists()){
            localSaveDirectory.mkdirs();
        }
        
        List<String> localFiles = Arrays.asList(localSaveDirectory.list());

        DQPClient connection = qz.getConnection(serverName, 500);
        if (connection == null){
            // Couldn't establish a connection to the server, so syncing
            // isn't going to happen. Listeners still need to be notified,
            // though.
            for (SyncListener listener : listeners){
                listener.onSynchronize(this);
            }
            return;
        }

        List<String> serverFiles = connection.list();

        List<String> needToGet = new ArrayList<String>();
        List<String> needToUpload = new ArrayList<String>();

        needToGet.addAll(serverFiles);
        needToGet.removeAll(localFiles);

        needToUpload.addAll(localFiles);
        needToUpload.removeAll(serverFiles);

        for (String filename : needToGet){
            getFileFromServer(filename);
        }
        for (String filename : needToUpload){
            File file = new File(localSaveDirectory, filename);
            if (!file.isHidden())
                putFileOnServer(new File(localSaveDirectory, filename));
        }

        // The challenge file and name file should be synced separately
        if (serverFiles.contains(CHALLENGE_FILE_LOCATION)){
            syncChallengeFile();
        }
        if (serverFiles.contains(NAME_FILE_LOCATION)){
            syncTextFileByLine(NAME_FILE_LOCATION);
        }

        // Notify listeners that a sync happened:
        for (SyncListener listener : listeners){
            listener.onSynchronize(this);
        }
        
    }

    /**
     * Do challenge file specific syncing.
     * 
     * @throws IOException
     */
    private void syncChallengeFile() throws IOException {
        DQPClient connection = qz.getConnection(serverName);
        
        byte[] serverChallengeFile = connection.get(CHALLENGE_FILE_LOCATION);
        ChallengeFileParser parser;
        try {
            parser = new ChallengeFileParser(
                    new ByteArrayInputStream(serverChallengeFile));
        } catch (SAXException e) {
            // Challenge file isn't well formed...
            e.printStackTrace();
            return;
        }
        
        List<Challenge> serverChallenges = parser.getChallenges();
        
        File challengeFile = new File(localSaveDirectory, CHALLENGE_FILE_LOCATION);
        try {
            parser = new ChallengeFileParser(new FileInputStream(challengeFile));
        } catch (SAXException e){
            // Our challenge file seems to have a problem... let's just replace
            // it with the server's.
        }
        
        List<Challenge> localChallenges = parser.getChallenges();
        
        List<Challenge> needToAdd = new ArrayList<Challenge>(localChallenges);
        needToAdd.removeAll(serverChallenges);
        
        // Now we add them to the server
        connection = qz.getConnection(serverName);
        connection.addChallenges(needToAdd);
        
        // And then write them back to the file.
        serverChallenges.addAll(needToAdd);
        ChallengeWriter writer = new ChallengeWriter();
        writer.writeToStream(serverChallenges, new FileOutputStream(challengeFile));
        
        
        
    }

    /**
     * Add a challenge to the challenge file, and then synchronize to the
     * server to try to send it to others.
     * 
     * @param challengeToAdd
     * @throws IOException 
     */
    public void addChallenge(Challenge challengeToAdd) throws SAXException, IOException {
        if (!localSaveDirectory.exists()){
            localSaveDirectory.mkdir();
        }
        File challengeFile = 
            new File(localSaveDirectory, CHALLENGE_FILE_LOCATION);
        List<Challenge> challenges;
        if (challengeFile.exists()){
            // Read in the list of challenges
            ChallengeFileParser parser = new ChallengeFileParser(
                    new FileInputStream(challengeFile));

            challenges = parser.getChallenges();
        } else {
            challenges = new ArrayList<Challenge>();
        }

        challenges.add(challengeToAdd);
        
        ChallengeWriter writer = new ChallengeWriter();
        writer.writeToStream(challenges, new FileOutputStream(challengeFile));
        
        synchronize();
    }

    /**Download a file from the server and save it locally under the same name.
     * Will not download if the file already exists locally or if the server
     * doesn't have it.
     * 
     * @param fileName
     * @throws IOException
     */
    private void getFileFromServer(String fileName) throws IOException{
        getFileFromServer(fileName, false);
    }


    /**
     * Download a file from the server and save it locally under the same 
     * name. If it already exists locally, it will be overwritten if overwrite
     * is true. If a connection can't be made, nothing will happen.
     * 
     * @param fileName
     * @param overwrite
     * @throws IOException
     */
    private void getFileFromServer(String fileName, boolean overwrite) 
    throws IOException{
        File savedFile = new File(localSaveDirectory, fileName);
        if (savedFile.exists() && !overwrite){
            System.err.println(
                    "Tried to save file that already exists: "+savedFile);
            return;
        } 
        DQPClient connection = qz.getConnection(serverName);
        if (connection != null){
            FileOutputStream fos = new FileOutputStream(savedFile);
            byte[] data = connection.get(fileName);
            
            fos.write(data);
        }
    }

    /**
     * Get the specified file from the server, but instead of saving it
     * to disk it will be read and returned on the fly, and returned as a 
     * list of Strings corresponding to each line of the file.
     * 
     * @param fileName
     * @param connection The connection to use to get the file
     * @return the contents of the requested file from the server.
     * @throws IOException 
     */
    private List<String> getTextFileInMemory(
            String fileName, DQPClient connection) throws IOException {
        List<String> contents = new ArrayList<String>();

        byte[] bytes = connection.get(fileName);

        // Now read the contents back
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(bytes)));
        String currentName;
        while ((currentName = in.readLine()) != null){
            contents.add(currentName);
        }

        return contents;
    }

    /**
     * Get the contents of the given text file from disk. Each line of the
     * text file will be a string in the returned list.
     * 
     * @param textfile
     * @return the list of lines from the file. If the file doesn't exist, 
     * null will be returned.
     * @throws IOException 
     */
    private static List<String> getLocalTextFile(File textfile) throws IOException{
        if (!textfile.exists()){
            // Hmm, that's a problem. Oh well, we tried.
            return null;
        }
        List<String> ret = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(textfile));
        String currentLine;
        while ((currentLine = in.readLine()) != null){
            ret.add(currentLine);
        }
        in.close();
        return ret;
    }

    /**
     * Sync a file with the server by line. That is, any line of this
     * file that we have and the server doesn't will be appended to the
     * server. Make sure to only use this for the name file and the challenge
     * file.
     * 
     * @param filename
     * @throws IOException 
     */
    private void syncTextFileByLine(String filename) throws IOException {
        // Try to open a connection first.
        DQPClient connection = qz.getConnection(serverName);
        if (connection == null){
            // Couldn't connect to sync, give up.
            return;
        }
        List<String> serverFile = getTextFileInMemory(filename, connection);
        List<String> localFile = getLocalTextFile(
                new File(localSaveDirectory, filename));

        List<String> localOnly = new ArrayList<String>();
        localOnly.addAll(localFile);
        localOnly.removeAll(serverFile);

        // Create the updated list and write it out to disk
        serverFile.addAll(localOnly);
        PrintStream out = new PrintStream(
                new FileOutputStream(new File(localSaveDirectory, filename)));
        for (String line : serverFile){
            out.println(line);
        }
        out.close();

        // Now we try to append our unique lines to the server
        connection = qz.getConnection(serverName);
        String[] localArray = localOnly.toArray(new String[localOnly.size()]);
        connection.append(filename, localArray);

    }

    /**Attempt to write a file to the server. 
     * 
     * @param file
     * @return true if successful
     * @throws IOException
     */
    private boolean putFileOnServer(File file) throws IOException{
        if (!file.isFile()){
            return false;
        }
        String fileName = file.getName();        

        DQPClient connection = qz.getConnection(serverName);
        if (connection == null) {
            return false;
        }

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[2048];
        int bytesRead;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        while ((bytesRead = fis.read(buffer)) != -1){
            byteOut.write(buffer, 0, bytesRead);
        }
        
        connection.put(fileName, byteOut.toByteArray());
        
        
        return true;
    }

    public QuiltZeroconf getQz() {
	return qz;
    }

    public File getLocalSaveDirectory() {
	return localSaveDirectory;
    }


}
