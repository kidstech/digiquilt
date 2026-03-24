/*
 * Created by jbiatek on Jul 5, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.io.ReadLineInputStream;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.server.DQPHandler;
import umm.digiquilt.testing.MockInputStream;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;
import umm.digiquilt.xmlsaveload.ChallengeWriter;

/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-06 00:45:14 $
 * @version $Revision: 1.2 $
 *
 */

public class DQPHandlerTest {

    /**
     * The temporary directory for testing
     */
    File testServerDirectory;

    /**
     * The DQPHandler that we're testing.
     */
    DQPHandler testHandler;
    
    /**
     * The test handler's input stream.
     */
    MockInputStream handlerIn;

    /**
     * Output from the test handler.
     */
    ByteArrayOutputStream handlerOut;
    
    /**
     * A reader to read what the server printed to handlerOut.
     */
    InputStream serverResponse;

    
    /**
     * Create the test object and test directory.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        
        handlerIn = new MockInputStream();
        handlerOut = new ByteArrayOutputStream();
        
        testServerDirectory = File.createTempFile("dqpTest", ".tmp");
        testServerDirectory.delete();
        testServerDirectory.mkdirs();
        testServerDirectory.deleteOnExit();
        
        testHandler = new DQPHandler(testServerDirectory);
        
    }

    /**
     * This method runs after every single test and deletes the test
     * directory that we created.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        rm(testServerDirectory);
    }



    /**
     * The server should create an Autosave directory for itself.
     */
    @Test
    public void testIsAutosaveDirectoryCreated(){
        File autosave = new File(testServerDirectory, "Autosaves");
        assertTrue("The server failed to create "+autosave.getPath(), 
                autosave.exists());

    }

    /**
     * Give the server a bogus request
     * @throws Exception
     */
    @Test
    public void testInvalidCommand() throws Exception{
        handlerIn.returnLine("WHAARGARBL");
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);

        assertEquals("Server responded to invalid command with "+ response,
                "INVALID REQUEST", response);
    }

    
    /**
     * See what the server wrote to handlerOut, and load it into serverResponse
     * for the tests to read.
     */
    private void loadServerResponse(){
        serverResponse = new ByteArrayInputStream(handlerOut.toByteArray());
    }

    /**
     * Make sure that LIST returns the proper contents.
     * 
     * @throws Exception
     */
    @Test
    public void testListCommand() throws Exception{
        File testFile1 = new File(testServerDirectory, "file1.txt");
        testFile1.createNewFile();
        File testFile2 = new File(testServerDirectory, "File number two");
        testFile2.createNewFile();
        File testFile3 = new File(testServerDirectory, "Three + Four?");
        testFile3.createNewFile();

        /* This hidden file should be excluded, along with any directories,
         * but creating a hidden file isn't really possible in Java. On a
         * Unix-esque system, a dotfile should be registered as hidden so
         * that's pretty easy. On Windows, it tries to run attrib.exe
         * to set the file as hidden. If you're on something else, fix
         * the test to make a hidden file on your system! Or if that system
         * doesn't have hidden files at all for whatever reason, you could
         * just delete the hidden file since the test doesn't really apply.
         */
        File hiddenFile = new File(testServerDirectory, ".hidden");
        hiddenFile.createNewFile();
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
            // Dunno if this really works, I'm not on Windows...
        	String command = "attrib +H "+hiddenFile.getAbsolutePath();
        	System.out.println("Executing: "+command);
            Process attrib = Runtime.getRuntime().exec(command);
            attrib.waitFor();
        }

        if (!hiddenFile.isHidden()){
            fail("Could not create a File that was hidden, fix the test.");
            // Or you could just do this to skip it entirely:
            //hiddenFile.delete();
        }
        //This should be left out of the server's list
        File directory = new File(testServerDirectory, "Extra directory");
        directory.mkdir();

        
        handlerIn.returnLine("LIST");
        
        
        // The actual test call
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Server responded to LIST request with "+response,
                "OK", response);
        String nextFile;
        List<String> files = new ArrayList<String>();
        while ((nextFile = ReadLineInputStream.readLine(serverResponse)) != null){
            files.add(nextFile);
        }
        assertEquals("Wrong number of files returned", 3, files.size());
        assertTrue("First test file was not listed", 
                files.contains(testFile1.getName()));
        assertTrue("Second test file was not listed",
                files.contains(testFile2.getName()));
        assertTrue("Third test file was not listed",
                files.contains(testFile3.getName()));
    }

    /**
     * Try to GET a file containing text.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCommandOnText() throws Exception{
        String lineOne = "Line number one";
        String lineTwo = "Line 2";
        String lineThree = "a third line, for good measure";
        File testFile = new File(testServerDirectory, "test file.txt");
        PrintStream file = new PrintStream(new FileOutputStream(testFile));
        file.println(lineOne);
        file.println(lineTwo);
        file.println(lineThree);
        file.close();

        handlerIn.returnLine("GET "+testFile.getName());
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        
        String line = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Server responded to GET with "+line, "OK", line);
        line = ReadLineInputStream.readLine(serverResponse);
        assertEquals(lineOne, line);
        line = ReadLineInputStream.readLine(serverResponse);
        assertEquals(lineTwo, line);
        line = ReadLineInputStream.readLine(serverResponse);
        assertEquals(lineThree, line);
    }

    /**
     * Try to GET a file that isn't there.
     * @throws Exception
     */
    @Test
    public void testGetCommandOnInvalidFile() throws Exception {
        handlerIn.returnLine("GET invalidfile");
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("An incorrect GET responded with "+response+" instead",
                "FILE NOT FOUND: invalidfile", response);
    }

    
    /**
     * Try to GET a file with binary data.
     * @throws Exception
     */
    @Test
    public void testGetCommandOnBinaryData() throws Exception {
        File testFile = new File(testServerDirectory, "testfile");
        testFile.createNewFile();
        Random rng = new Random();
        // 4kb of random data
        int size = 1024*4;
        byte[] contents = new byte[size];
        rng.nextBytes(contents);
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write(contents);

        handlerIn.returnLine("GET "+testFile.getName());
        
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Response to GET request was "+response, "OK", response);

        int counter = 0;
        int read;
        byte[] buffer = new byte[100];
        while ((read = serverResponse.read(buffer)) != -1){
            for (int i=0; i<read; i++){
                assertEquals("Binary data does not match at byte "+counter,
                        contents[counter], buffer[i]);
                counter++;
            }
        }
        assertEquals("Premature end of file", size, counter);

    }

    /**
     * PUT a file on the server.
     * @throws Exception
     */
    @Test
    public void testPutCommand() throws Exception{
        File testFile = new File(testServerDirectory, "Put command testfile");

        handlerIn.returnLine("PUT "+testFile.getName());
        // Create some random data to throw in there
        int size = 1024*4;
        byte[] contents = new byte[size];
        Random rng = new Random();
        rng.nextBytes(contents);
        handlerIn.returnBytes(contents);
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Server responded with "+response, "OK", response);


        assertTrue("PUT file was not created", testFile.exists());
        FileInputStream fin = new FileInputStream(testFile);
        int counter = 0;
        int read;
        byte[] buffer = new byte[100];
        while ((read = fin.read(buffer)) != -1){
            for (int i=0; i<read; i++){
                assertEquals("Binary data does not match at byte "+counter,
                        contents[counter], buffer[i]);
                counter++;
            }
        }
        assertEquals("Premature end of file", size, counter);
    }

    /**
     * Tries to PUT a file that's already there.
     * @throws Exception
     */
    @Test
    public void testFileAlreadyExists() throws Exception{
        String filename = "This file exists already";
        boolean ok = new File(testServerDirectory, filename).createNewFile();
        assertTrue("Could not create test file", ok);
        
        handlerIn.returnLine("PUT "+filename);
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("PUT shouldn't allow overwriting",
                "FILE ALREADY EXISTS: "+filename, response);
    }

    /**
     * When an XML file is PUT on to the server, a copy should be saved
     * to the autosave directory.
     * 
     * @throws Exception
     */
    @Test
    public void testPutMakesAnAutosaveToo() throws Exception{
        String putfilename = "Name - autosave test file.xml.gz";
        handlerIn.returnLine("PUT "+putfilename);
        String contents = "<xml>Some stuff</xml>";
        handlerIn.returnLine(contents);
        handlerIn.close();
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Server responded with "+response, "OK", response);


        File regularSave = new File(testServerDirectory, putfilename);
        assertTrue("PUT file wasn't created", regularSave.exists());

        File autoDir = new File(testServerDirectory, "Autosaves");
        boolean foundFile = false;
        String[] autosaveList = autoDir.list();
        for (String filename : autosaveList){
            if (filename.startsWith("Name") 
                    && filename.endsWith("autosave test file.xml.gz")){
                BufferedReader br = new BufferedReader(
                        new FileReader(new File(autoDir,filename)));
                String readContents = br.readLine();

                assertEquals("Contents of file were different",
                        contents, readContents);

                foundFile = true;
            }
        }
        assertTrue("Autosaved file was not created on PUT", foundFile);
    }

    /**AUTOSAVE should put a file into the autosave directory.
     * @throws Exception
     */
    @Test
    public void testAutosaveCommand() throws Exception{
        String name = "Student Name";
        handlerIn.returnLine("AUTOSAVE "+name);
        String contents = "<xml> Something </xml>";
        handlerIn.returnLine(contents);
        handlerIn.close();
        
        
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Server responded with "+response, "OK", response);

        File autoDir = new File(testServerDirectory, "Autosaves");
        boolean foundFile = false;
        String[] autosaveList = autoDir.list();
        for (String filename : autosaveList){
            if (filename.startsWith(name) 
                    && filename.endsWith("(autosave).xml.gz")){

                BufferedReader br = new BufferedReader(
                        new FileReader(new File(autoDir,filename)));
                String readContents = br.readLine();

                assertEquals("Contents of file were different",
                        contents, readContents);

                foundFile = true;
            }
        }
        assertTrue("Autosaved file was not created", foundFile);
    }
    
    /**
     * Test the APPEND command on the challenge file
     * @throws Exception 
     */
    @Test
    public void testAppendChallenge() throws Exception {
        // Set up an existing challenge file:
        
        File challengeFile = new File(testServerDirectory, 
                SaveHandler.CHALLENGE_FILE_LOCATION);
        OutputStream challengeOut = new FileOutputStream(challengeFile);
        List<Challenge> existing = new ArrayList<Challenge>();
        existing.add(new FreeformChallenge("Existing challenge #1"));
        existing.add(new FreeformChallenge("Existing challenge #2"));
        
        Map<Fabric, Fraction> map1 = new HashMap<Fabric, Fraction>();
        map1.put(Fabric.RED, new Fraction(4, 4));
        FractionChallenge fract1 = new FractionChallenge("Existing challenge #3", map1);
        existing.add(fract1);
        
        ChallengeWriter writer = new ChallengeWriter();
        writer.writeToStream(existing, challengeOut);
        
        
        // Create the challenges that will be added. Some will be duplicates, 
        // in which case they shouldn't be added.
        List<Challenge> added = new ArrayList<Challenge>();
        added.add(new FreeformChallenge("Brand new challenge"));
        added.add(new FreeformChallenge("Existing challenge #1"));
        
        Map<Fabric, Fraction> map2 = new HashMap<Fabric, Fraction>();
        map2.put(Fabric.BLUE, new Fraction(1, 1));
        FractionChallenge fract2 = new FractionChallenge("New challenge", map2);
        added.add(fract2);
        
        Map<Fabric, Fraction> map3 = new HashMap<Fabric, Fraction>();
        map3.put(Fabric.RED, new Fraction(1, 1));
        FractionChallenge fract3 = new FractionChallenge("Duplicate", map3);
        added.add(fract3);
        
        handlerIn.returnLine("ADD CHALLENGES");
        handlerIn.returnBytes(dumpChallengesToBuffer(added));
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Handler reported: "+response, "OK", response);
        
        // Now to check what's in the challenge file now
        List<Challenge> expected = new ArrayList<Challenge>(existing);
        expected.add(new FreeformChallenge("Brand new challenge"));
        expected.add(fract2);
        
        ChallengeFileParser  parser = new ChallengeFileParser(
                new FileInputStream(challengeFile));
        assertEquals(expected, parser.getChallenges());
        
    }
    
    /**
     * Test appending a name
     * 
     * @throws Exception
     */
    @Test
    public void testAppendName() throws Exception {
        File nameFile = new File(testServerDirectory, 
                SaveHandler.NAME_FILE_LOCATION);
        PrintStream nameFileOut = 
            new PrintStream(new FileOutputStream(nameFile));
        nameFileOut.println("Name 1");
        nameFileOut.println("Name 2");
        nameFileOut.println("Name 3");
        nameFileOut.close();
        
        handlerIn.returnLine("APPEND "+SaveHandler.NAME_FILE_LOCATION);
        handlerIn.returnLine("Appended name 1");
        handlerIn.returnLine("Appended name 2");
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Handler reported: "+response, "OK", response);
        
        // Now to check what's in the name file now
        BufferedReader nameFileIn = new BufferedReader(
                new FileReader(nameFile));
        assertEquals("The name file didn't match", 
                "Name 1", nameFileIn.readLine());
        assertEquals("The name file didn't match", 
                "Name 2", nameFileIn.readLine());
        assertEquals("The name file didn't match", 
                "Name 3", nameFileIn.readLine());
        assertEquals("The name file didn't match", 
                "Appended name 1", nameFileIn.readLine());
        assertEquals("The name file didn't match", 
                "Appended name 2", nameFileIn.readLine());
        assertNull("File didn't end when expected", nameFileIn.readLine());
    }
    
    /**
     * It shouldn't be possible to append to anything other than
     * the name and challenge files.
     * 
     * @throws Exception
     */
    @Test
    public void testAppendUneditableFile() throws Exception {
        handlerIn.returnLine("APPEND challenges.xml");

        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Should have refused to append to other file", 
                "UNEDITABLE FILE", response);
    }
    
    /**
     * Test appending even if there's not a challenge file at all
     * @throws Exception 
     */
    @Test
    public void testAddToEmptyChallengeFile() throws Exception {
        File challengeFile = new File(testServerDirectory, 
                SaveHandler.CHALLENGE_FILE_LOCATION);

        String name = "Fraction Challenge!";
        Map<Fabric, Fraction> map = new HashMap<Fabric, Fraction>();
        map.put(Fabric.BLUE, new Fraction(2, 2));
        Challenge challenge1 = new FractionChallenge(name, map);
        String freeform = "Freeform challenge, yo";
        Challenge challenge2 = new FreeformChallenge(freeform);
        
        List<Challenge> created = new ArrayList<Challenge>();
        created.add(challenge1);
        created.add(challenge2);
        
        handlerIn.returnLine("ADD CHALLENGES");
        handlerIn.returnBytes(dumpChallengesToBuffer(created));
        
        testHandler.handleConnection(handlerIn, handlerOut);
        loadServerResponse();
        
        String response = ReadLineInputStream.readLine(serverResponse);
        assertEquals("Handler reported: "+response, "OK", response);
        
        // Now to check what's in the challenge file now
        ChallengeFileParser parser = new ChallengeFileParser(
                new FileInputStream(challengeFile));
        List<Challenge> loaded = parser.getChallenges();
        
        assertEquals(created, loaded);
        
    }
    
    /**
     * Use ChallengeWriter to put some challenges in a byte array
     * 
     * @param challenges
     * @return
     */
    private byte[] dumpChallengesToBuffer(List<Challenge> challenges) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChallengeWriter writer = new ChallengeWriter();
        
        writer.writeToStream(challenges, out);
        
        return out.toByteArray();
    }


    /**Delete a file. If it's a directory, recursively delete it.
     * @param toRemove
     */
    private static void rm(File toRemove){
        if (toRemove.isDirectory()){
            File[] contents = toRemove.listFiles();
            for (File file : contents){
                if (file.isDirectory()){
                    rm(file);
                } else {
                    file.delete();
                }
            }
        }
        toRemove.delete();
    }

}