/*
 * Created by jbiatek on Jun 4, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.savehandler;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.UndoRedoStack;
import umm.digiquilt.savehandler.DQPClient;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.savehandler.SyncListener;
import umm.digiquilt.server.QuiltZeroconf;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;
import umm.digiquilt.xmlsaveload.ChallengeWriter;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-28 21:59:12 $
 * @version $Revision: 1.1 $
 *
 */

public class SaveHandlerTest {

    /**
     * The local directory for the save handler to use
     */
    File localDir;

    /**
     * The name of the "server"
     */
    String mockServerName = "Mock Server";


    /**
     * Set up temporary directory 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        localDir = File.createTempFile("savehandler", ".tmp");
        localDir.delete();
        localDir.mkdir();
    }

    /**
     * Test the getName() function.
     * @throws Exception
     */
    @Test
    public void testGetName() throws Exception{
        final String[] names = 
            new String[]{"Name 1", "Name 2", "Another name"};
        
        byte[] nameFileContents = dumpStringsToByteArray(names);
        
        // Create a mock which returns these names (as bytes)
        DQPClient mockConnection = mock(DQPClient.class);
        when(mockConnection.get("names.txt")).thenReturn(nameFileContents);
        
        QuiltZeroconf mockZeroconf = makeMockQZ(mockConnection);

        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);

        List<String> testNames = testHandler.getNames();
        assertEquals("SaveHandler got the wrong number of names",
                names.length, testNames.size());
        for (int i=0; i<names.length; i++){
            assertEquals("SaveHandler reported an incorrect name",
                    names[i], testNames.get(i));
        }

    }
    
    /**
     * Test getName() when a server can't be found.
     * @throws Exception 
     */
    @Test
    public void testGetNameWithoutServer() throws Exception {
        // First try: the server is gone, and the names file isn't there
        QuiltZeroconf mockZeroconf = mock(QuiltZeroconf.class);
        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);
        
        List<String> names = testHandler.getNames();
        
        assertEquals("There shouldn't be any names", 0, names.size());
        
        // Second try: The server isn't responding still, but now there
        // is a name file that it can use.
        File classDir = new File(localDir, mockServerName);
        classDir.mkdir();
        File namesFile = new File(classDir, "names.txt");
        
        List<String> expected = new ArrayList<String>();
        expected.add("First name");
        expected.add("A second name");
        expected.add("Final name");
        FileWriter out = new FileWriter(namesFile);
        for (String name : expected){
            out.write(name + "\n");
        }
        out.close();
        
        names = testHandler.getNames();
        assertEquals("Should have returned an equal list", expected, names);
    }
    
    private static class FakeSaveXML extends SaveBlockXML {

        public static String message = "Fake XML was told to write to this.";
        
        public FakeSaveXML() throws ParserConfigurationException{
            super();
        }

        @Override
        public void writeDocumentToStream(OutputStream outStream)
                throws IOException {
            PrintStream out = new PrintStream(outStream);
            out.println(message);
        }

        @Override
        public void writeOutDocumentToFile(File filename) throws IOException {
            writeDocumentToStream(new FileOutputStream(filename));
        }
        
        
    }

    /**Test that a regular save does the proper thing both locally and
     * on the server.
     * @throws Exception
     */
    @Test
    public void testSaving() throws Exception{
        // Set up all the save stuff
        Block block = new Block(16);
        block.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLACK);
        Grid grid = new Grid(4,4,4,4);
        String notes = "Test notes";
        Challenge challenge = new FreeformChallenge("Test challenge");
        UndoRedoStack stack = new UndoRedoStack();
        SaveBlockXML saver = new FakeSaveXML();
        // We really just need any sort of image...
        BufferedImage icon = ImageIO.read(getClass().getResource(
        "/umm/digiquilt/view/images/digiIcon.gif"));
        String name = "Test save file";

        String studentName = "Student name";

        DQPClient mockConnection = mock(DQPClient.class);
        QuiltZeroconf mockZeroconf = makeMockQZ(mockConnection);

        // Show time:
        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);
        assertNull("Name should be null before setting", 
                testHandler.getStudentName());
        testHandler.setStudentName(studentName);
        assertEquals("Student name should be set",
                studentName, testHandler.getStudentName());
        
        testHandler.saveBlock(saver, icon, name);


        // Now we test the local save for correctness:
        File localClassDir = 
            new File(localDir, mockServerName).getAbsoluteFile();
        assertTrue(
                "Local class file was not created: "+localClassDir.getPath(), 
                localClassDir.exists());

        File localXML = new File(localClassDir, 
                studentName+" - "+name+".xml.gz");
        File localImage = new File(localClassDir,
                studentName+" - "+name+".png");

        assertTrue("File wasn't saved locally", localXML.exists());
        assertTrue("Icon wasn't saved locally", localImage.exists());

        assertEquals("SaveXML didn't get told to write file", 
                FakeSaveXML.message, readFromFile(localXML));



        BufferedImage loadedIcon = ImageIO.read(localImage);
        // We're just going to assume that if the picture got written
        // and if it has the same dimensions, it got done right.
        assertEquals("Image had wrong dimensions", icon.getWidth(),
                loadedIcon.getWidth());
        assertEquals("Image had wrong dimensions", icon.getHeight(),
                loadedIcon.getHeight());



        // Now we need to check that the server was sent the correct data.
        String serverXMLFile = studentName+" - "+name+".xml.gz";
        String serverIconFile = studentName+" - "+name+".png";
        
        // Need to get their correct contents too.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        saver.writeDocumentToStream(byteOut);
        byte[] xmlData = byteOut.toByteArray();

        byteOut = new ByteArrayOutputStream();
        ImageIO.write(icon, "png", byteOut);
        byte[] iconData = byteOut.toByteArray();
        
        verify(mockConnection).put(serverXMLFile, xmlData);
        verify(mockConnection).put(serverIconFile, iconData);
        
    }

    /**
     * Test that saving works even if the server isn't there.
     * @throws Exception
     */
    @Test
    public void testSavingWithoutServer() throws Exception{
        // Set up all the save stuff again
        Block block = new Block(16);
        block.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.ORANGE);
        Grid grid = new Grid(4,4,4,4);
        String notes = "Test notes";
        Challenge challenge = new FreeformChallenge("Test challenge");
        UndoRedoStack stack = new UndoRedoStack();

        SaveBlockXML saver = new FakeSaveXML();
        // We really just need any sort of image...
        BufferedImage icon = ImageIO.read(getClass().getResource(
        "/umm/digiquilt/view/images/digiIcon.gif"));
        String name = "Test save file";

        String studentName = "Student name";

        QuiltZeroconf fakeZeroconf = mock(QuiltZeroconf.class);


        // Showtime:
        SaveHandler testHandler = 
            new SaveHandler(fakeZeroconf, localDir, mockServerName);
        testHandler.setStudentName(studentName);
        testHandler.saveBlock(saver, icon, name);


        // Check the file:
        File localClassDir = 
            new File(localDir, mockServerName).getAbsoluteFile();
        assertTrue(
                "Local class file was not created: "+localClassDir.getPath(), 
                localClassDir.exists());

        File localXML = new File(localClassDir, 
                studentName+" - "+name+".xml.gz");
        File localImage = new File(localClassDir,
                studentName+" - "+name+".png");

        assertTrue("File wasn't saved locally", localXML.exists());
        assertTrue("Icon wasn't saved locally", localImage.exists());

        assertEquals("SaveXML wasn't told to save", 
                FakeSaveXML.message, readFromFile(localXML));


        BufferedImage loadedIcon = ImageIO.read(localImage);
        // We're just going to assume that if the picture got written
        // and if it has the same dimensions, it got done right.
        assertEquals("Image had wrong dimensions", icon.getWidth(),
                loadedIcon.getWidth());
        assertEquals("Image had wrong dimensions", icon.getHeight(),
                loadedIcon.getHeight());

    }
    
    /**
     * Make sure that saved files aren't overwritten.
     * @throws Exception 
     */
    @Test
    public void testSavesDontOverwrite() throws Exception {
        String xmlName = "Name - Save.xml.gz";
        String pngName = "Name - Save.png";
        
        DQPClient mockClient = mock(DQPClient.class);
        
        // Make it appear that the "server" has these files:
        List<String> serverFiles = new ArrayList<String>();
        serverFiles.add(xmlName);
        serverFiles.add(pngName);
        when(mockClient.list()).thenReturn(serverFiles);
        
        byte[] fileContents = dumpStringsToByteArray("Some stuff");
        when(mockClient.get(xmlName)).thenReturn(fileContents);
        when(mockClient.get(pngName)).thenReturn(fileContents);
        
        QuiltZeroconf mockQZ = makeMockQZ(mockClient);
        
        
        
        
        SaveHandler testHandler = 
            new SaveHandler(mockQZ, localDir, mockServerName);
        
        SaveBlockXML mockSaver = mock(SaveBlockXML.class);
        doThrow(new RuntimeException(
                "I shouldn't be writing anything, that would destroy data!"))
        		.when(mockSaver).writeOutDocumentToFile(any(File.class));
        
        doThrow(new RuntimeException(
                "I shouldn't be writing anything, that would destroy data!"))
        .when(mockSaver).writeDocumentToStream(any(OutputStream.class));
        
        BufferedImage icon = ImageIO.read(getClass().getResource(
        "/umm/digiquilt/view/images/digiIcon.gif"));
        
        testHandler.setStudentName("Name");
        boolean result = testHandler.saveBlock(mockSaver, icon, "Save");
        
        assertFalse("Handler should have reported failure", result);
        
        File classDir = new File(localDir, mockServerName);
        File xmlFile = new File(classDir, "Name - Save.xml.gz");
        File pngFile = new File(classDir, "Name - Save.png");
        
        String expected = "Some stuff";
        assertEquals("XML file had wrong contents", 
                expected, readFromFile(xmlFile));
        assertEquals("PNG file had wrong contents",
                expected, readFromFile(pngFile));
    }



    /**Test that the autosave function sends the proper command and the 
     * correct data to the server.
     * @throws Exception
     */
    @Test
    public void testAutosaving() throws Exception{
        final SaveBlockXML saver = mock(SaveBlockXML.class);

        final String testStudentName = "Student name";

        DQPClient mockConnection = mock(DQPClient.class);
        QuiltZeroconf mockZeroconf = makeMockQZ(mockConnection);

        // Run the test
        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);
        testHandler.setStudentName(testStudentName);
        testHandler.autosave(saver);
        
        
        verify(mockConnection).autosave(testStudentName, saver);
        
    }
    
    /**
     * Test autosaving when the server isn't there.
     * 
     * Note that right now, autosaving when the server isn't there ==
     * give up, throw out the autosave and pretend nothing bad happened.
     * @throws Exception
     */
    @Test
    public void testAutoSaveWithoutServer() throws Exception {
        SaveBlockXML saver = mock(SaveBlockXML.class);
        
        QuiltZeroconf fakeZeroconf = mock(QuiltZeroconf.class);
        SaveHandler testHandler =
            new SaveHandler(fakeZeroconf, localDir, mockServerName);
        
        /*
         * So, what's the proper behavior here? Autosaves are supposed
         * to be silent, so right now that's all we look for. It might
         * be good to keep these so if/when the server comes back it
         * can be sent then.
         */
        testHandler.autosave(saver);
        
    }

    /**
     * Test that synchronization happens properly.
     * @throws Exception 
     */
    @Test
    public void testSynchronize() throws Exception{
        File localClassDir = 
            new File(localDir, mockServerName).getAbsoluteFile();
        localClassDir.mkdirs();


        File local1 = new File(localClassDir, "Local file");
        String local1contents = "This file only exists locally";
        byte[] local1data = dumpStringsToByteArray(local1contents);
        writeToFile(local1, local1contents);


        File localCommon = new File(localClassDir, "Common file");
        String localCommonContents = "Contents of file on local side";
        writeToFile(localCommon, localCommonContents);


        // These are the server files, which each consist of one line for
        // simplicity's sake.

        String server1 = "Server file 1";
        String server1contents = "File that only exists on server";
        byte[] server1data = dumpStringsToByteArray(server1contents);

        String server2 = "Another server file";
        String server2contents = "Another file which should be downloaded";
        byte[] server2data = dumpStringsToByteArray(server2contents);

        String serverCommon = "Common file";
        String serverCommonContents = "Contents of file on server side";
        byte[] serverCommondData = 
            dumpStringsToByteArray(serverCommonContents);
        
        List<String> serverList = new ArrayList<String>();
        serverList.add(server1);
        serverList.add(server2);
        serverList.add(serverCommon);
        
        DQPClient mockConnection = mock(DQPClient.class);
        when(mockConnection.list()).thenReturn(serverList);
        
        when(mockConnection.get(server1)).thenReturn(server1data);
        when(mockConnection.get(server2)).thenReturn(server2data);
        when(mockConnection.get(serverCommon)).thenReturn(serverCommondData);

        QuiltZeroconf mockZeroconf = makeMockQZ(mockConnection);
        
        // We'll also test the synchronize() subscription.
        SyncListener listener = mock(SyncListener.class);

        // The test call:
        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);
        testHandler.addSyncListener(listener);
        
        testHandler.synchronize();

        
        verify(listener).onSynchronize(testHandler);
        

        // Server file 1 should have been downloaded:
        File server1test = new File(localClassDir, server1);
        assertTrue("File from server did not get synced",
                server1test.exists());
        assertEquals("File from server had incorrect contents", 
                server1contents, readFromFile(server1test));


        // Server file 2 should also have been downloaded:
        File server2test = new File(localClassDir, server2);
        assertTrue("File from server did not get synced",
                server2test.exists());
        assertEquals("File from server had incorrect contents", 
                server2contents, readFromFile(server2test));

        // Local file 1 should have been uploaded
        verify(mockConnection).put(local1.getName(), local1data);

        // Common file should NOT have been overwritten
        assertEquals("Local file was overwritten", localCommonContents,
                readFromFile(localCommon));


    }
    
    /**
     * The files names.txt and challenges.xml should be treated
     * a little bit differently...
     * @throws Exception 
     */
    @Test
    public void testSyncingNameFile() throws Exception {
        File localClassDir = 
            new File(localDir, mockServerName).getAbsoluteFile();
        localClassDir.mkdirs();
        
        
        File nameFile = new File(localClassDir, "names.txt");
        PrintStream nameOut = new PrintStream(new FileOutputStream(nameFile));
        nameOut.println("Common name");
        nameOut.println("Common name 2");
        nameOut.println("Local name");
        nameOut.println("Second local name");
        nameOut.close();
        
        // The contents of the "server" file:
        
        DQPClient mockConnection = mock(DQPClient.class);
        byte[] serverNameFile = dumpStringsToByteArray(
                "Common name", 
                "Common name 2", 
                "Server name", 
                "Second server name"
        );
        
        doReturn(serverNameFile).when(mockConnection).get("names.txt");
        
        List<String> listResponse = new ArrayList<String>();
        listResponse.add("names.txt");
        when(mockConnection.list()).thenReturn(listResponse);
        
        QuiltZeroconf fakeZeroconf = makeMockQZ(mockConnection); 
        
        // Time to sync and see what happens:
        SaveHandler handler = 
            new SaveHandler(fakeZeroconf, localDir, mockServerName);
        handler.synchronize();
        
        
        
        // Let's see what the server got told to do:
        verify(mockConnection).append("names.txt", 
                "Local name", "Second local name");

        
        // And we make sure the same is true locally.
        List<String> localNames = new ArrayList<String>();
        localNames.add("Common name");
        localNames.add("Common name 2");
        localNames.add("Server name");
        localNames.add("Second server name");
        localNames.add("Local name");
        localNames.add("Second local name");
        checkFileContains(nameFile, localNames);
        
    }
    
    /**
     * The files names.txt and challenges.xml should be treated
     * a little bit differently...
     * @throws Exception 
     */
    @Test
    public void testSyncingChallengesFile() throws Exception {
        File localClassDir = 
            new File(localDir, mockServerName).getAbsoluteFile();
        localClassDir.mkdirs();
        
        List<Challenge> localChallenges = new ArrayList<Challenge>();
        localChallenges.add(new FreeformChallenge("Common challenge"));
        localChallenges.add(new FreeformChallenge("Common challenge 2"));
        localChallenges.add(new FreeformChallenge("Local challenge"));
        localChallenges.add(new FreeformChallenge("Second local challenge"));
        File challengeFile = new File(localClassDir, "challenges.xml");
        
        
        OutputStream challengeOut = new FileOutputStream(challengeFile);
        ChallengeWriter writer = new ChallengeWriter();
        writer.writeToStream(localChallenges, challengeOut);
        
        // The contents of the "server" files:
        List<Challenge> serverChallenges = new ArrayList<Challenge>();
        serverChallenges.add(new FreeformChallenge("Common challenge"));
        serverChallenges.add(new FreeformChallenge("Common challenge 2"));
        serverChallenges.add(new FreeformChallenge("Server challenge"));
        serverChallenges.add(new FreeformChallenge("Second server challenge"));
        ByteArrayOutputStream serverFile = new ByteArrayOutputStream();
        writer.writeToStream(serverChallenges, serverFile);
        
        
        DQPClient mockConnection = mock(DQPClient.class);
        
        doReturn(serverFile.toByteArray()).when(mockConnection).get("challenges.xml");
        
        List<String> listResponse = new ArrayList<String>();
        listResponse.add("challenges.xml");
        when(mockConnection.list()).thenReturn(listResponse);
        
        QuiltZeroconf fakeZeroconf = makeMockQZ(mockConnection); 
        
        // Time to sync and see what happens:
        SaveHandler handler = 
            new SaveHandler(fakeZeroconf, localDir, mockServerName);
        handler.synchronize();
        
        
        
        // Let's see what the server got told to do:
        List<Challenge> expectedServerChallenges = new ArrayList<Challenge>();
        expectedServerChallenges.addAll(localChallenges);
        expectedServerChallenges.removeAll(serverChallenges);
        
        verify(mockConnection).addChallenges(expectedServerChallenges);

        
        // And we make sure the same is true locally.
        List<Challenge> expectedLocalChallenges = new ArrayList<Challenge>();
        expectedLocalChallenges.addAll(serverChallenges);
        expectedLocalChallenges.addAll(expectedServerChallenges);
        
        ChallengeFileParser parser = new ChallengeFileParser(
                new FileInputStream(challengeFile));
        List<Challenge> actual = parser.getChallenges();
        
        assertEquals(expectedLocalChallenges, actual);
        
        
    }
    
    /*
     * TODO: The challenge files are always assumed to be correct...
     */
    
    
    /**
     * Test syncing when the server is not there.
     * @throws Exception
     */
    @Test
    public void testSynchronizeWithoutServer() throws Exception {
        QuiltZeroconf mockZeroconf = mock(QuiltZeroconf.class);
        localDir.delete();
        SaveHandler testHandler = 
            new SaveHandler(mockZeroconf, localDir, mockServerName);
        assertFalse("Local directory shouldn't exist yet", localDir.exists());
        
        SyncListener listener = mock(SyncListener.class);
        testHandler.addSyncListener(listener);
        // Without a server to sync with, this should simply
        // return without complaining.
        testHandler.synchronize();
        
        
        verify(listener).onSynchronize(testHandler);
    }
    
    
    
    
    /**
     * Flag for the testAddChallenge method.
     */
    boolean syncGotCalled = false;
    
    /**
     * Test the addChallenge() method
     * 
     * @throws Exception
     */
    @Test
    public void testAddChallenge() throws Exception {
        File quiltClassDir = new File(localDir, mockServerName);  
        final File challengeFile = new File(quiltClassDir, "challenges.xml");
        
        final Challenge challenge1 = new FreeformChallenge( 
            "This challenge should be appended.");
        final Challenge challenge2 = new FreeformChallenge( 
            "This challenge is another one that should be appended");
        final Challenge challenge3 = new FreeformChallenge( 
            "This third challenge should be appended too.");
        final List<Challenge> expected = new ArrayList<Challenge>();
        SaveHandler testHandler = new SaveHandler(
                mock(QuiltZeroconf.class), localDir, mockServerName);
        
        
        // This listener is what checks the challenge file. This way,
        // we know that listeners are being notified *after* the challenge
        // is added, and not before.
        SyncListener challengeChecker = new SyncListener() {
            
            public void onSynchronize(SaveHandler handler) {
                syncGotCalled = true;
                try {
                    ChallengeFileParser parser = new ChallengeFileParser(
                            new FileInputStream(challengeFile));
                    assertEquals(expected, parser.getChallenges());
                } catch (Exception e){
                    fail(e.toString());
                }
            }
        };
        
        testHandler.addSyncListener(challengeChecker);
        

        expected.add(challenge1);
        testHandler.addChallenge(challenge1);
        assertTrue("Listener wasn't notified", syncGotCalled);
        syncGotCalled = false;
        
        expected.add(challenge2);
        testHandler.addChallenge(challenge2);
        assertTrue("Listener wasn't notified", syncGotCalled);
        syncGotCalled = false;

        expected.add(challenge3);
        testHandler.addChallenge(challenge3);
        assertTrue("Listener wasn't notified", syncGotCalled);
        syncGotCalled = false;
        
        // Trying to add a duplicate challenge shouldn't work:
        //testHandler.addChallenge(challenge1);
        
        
    }
    
    /**
     * Write a number of strings to a byte array, separated by line.
     * 
     * @param strings
     * @return a byte array containing the strings.
     */
    private byte[] dumpStringsToByteArray(String... strings){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(outputStream);
        for (String name : strings){
            printer.println(name);
        }
        printer.flush();
        
        return outputStream.toByteArray();
    }

    /**
     * Ensure that the given file has the same lines as the given list.
     * 
     * @param file
     * @param expected
     * @throws Exception
     */
    private void checkFileContains(File file, List<String> expected)
                                                            throws Exception{

        BufferedReader in = 
            new BufferedReader(new FileReader(file));
        List<String> actual = new ArrayList<String>();
        String line;
        while ((line = in.readLine()) != null){
            actual.add(line);
        }
        in.close();
        assertEquals("Contents of "+file.getName()+
                "were not what was expected", 
                expected, actual);
        
    }
    
    
    /**
     * Create a mock QuiltZeroconf that will return the given DQPClient
     * whenever it is asked for a connection.
     * 
     * @param mockConnection
     * @return a mock QuiltZeroconf
     * @throws Exception
     */
    private QuiltZeroconf makeMockQZ(DQPClient mockConnection) 
                                                    throws Exception{
        QuiltZeroconf mockZeroconf = mock(QuiltZeroconf.class);
        when(mockZeroconf.getConnection(eq(mockServerName), anyInt()))
            .thenReturn(mockConnection);
        when(mockZeroconf.getConnection(mockServerName))
            .thenReturn(mockConnection);
        
        return mockZeroconf;
    }
    
    
    /**
     * Quickly write a string to a file
     * 
     * @param file
     * @param text
     * @throws IOException
     */
    private void writeToFile(File file, String text) throws IOException{
        FileWriter fw = new FileWriter(file);
        fw.write(text+System.getProperty("line.separator"));
        fw.close();
    }

    /**
     * Quickly read the first line of a file
     * 
     * @param file
     * @return the first line of the file
     * @throws IOException
     */
    private String readFromFile(File file) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String ret = br.readLine();
        return ret;
    }


}
