package umm.digiquilt.savehandler;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.savehandler.DQPClient;
import umm.digiquilt.testing.MockInputStream;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * Test for DQPClient, which handles talking to DQP servers.
 */
public class DQPClientTest {

    /**
     * The test client
     */
    DQPClient testClient;

    /**
     * Mock InputStream. This can be preloaded with information, and
     * the DQPClient will read it later.
     */
    MockInputStream mockInput = new MockInputStream();

    /**
     * OutputStream for the client. Anything they say to the "server"
     * will actually be recorded here to a byte array.
     */
    ByteArrayOutputStream clientOutput = new ByteArrayOutputStream();

    /**
     * Some random data to be used for tests
     */
    byte[] randomData;

    /**
     * The error message that the error reporting thread will give.
     */
    static final String SERVER_ERROR_STRING = "Test error message";

    /**
     * Set up all the inter-thread streams, etc.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        testClient = new DQPClient(mockInput, clientOutput);

        assertFalse("Client object shouldn't be closed yet"
                , testClient.isClosed());

        // Create 10kb of random data to use for the test
        randomData = new byte[10240];
        Random random = new Random();
        random.nextBytes(randomData);
    }

    /**
     * Make sure that the testClient says it is closed, and make sure that
     * methods throw exceptions if you try to call them anyways.
     * @throws Exception 
     */
    @After
    public void checkClientIsClosedAfterwards() throws Exception{
        assertTrue("Client should be closed after giving command", 
                testClient.isClosed());

        // Make sure list() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception {
                testClient.list();
            }
        });

        // Make sure get() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception {
                testClient.get("File");
            }
        });

        // Make sure put() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception {
                testClient.put("Fake file", randomData);
            }
        });

        // Make sure autosave() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception {
                testClient.autosave("Some stuff", mock(SaveBlockXML.class));
            }
        });

        // Make sure append() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception{
                testClient.append("File name", "Stuff");
            }

        });
        
        // Make sure addChallenges() throws an exception
        makeSureClosedExceptionThrown(new Command(){
            public void runCommand() throws Exception{
                testClient.addChallenges(new ArrayList<Challenge>());
            }

        });
    }

    /**
     * Interface for running something that throws an exception
     */
    private interface Command {
        /**
         * Call the given command.
         * @throws Exception
         */
        public void runCommand() throws Exception;
    }

    /**
     * Make sure that the given command throws an exception since
     * the client is closed.
     * 
     * @param c
     */
    private void makeSureClosedExceptionThrown(Command c) {
        try {
            c.runCommand();
        } catch (IllegalStateException e) {
            assertEquals("Incorrect exception message", 
                    "This connection has been closed.",
                    e.getMessage());
            return;
        } catch (Exception e){
            fail("Incorrect type of exception thrown: "+e.getMessage());
        }

        fail("Exception wasn't thrown");
    }

    /**
     * Test the LIST command.
     * @throws Exception
     */
    @Test 
    public void testListCommand() throws Exception {
        final String[] fileList = 
        {"File 1.txt", "A second file.xml", "File 3"};
        mockInput.returnLine("OK");
        for (String file : fileList){
            mockInput.returnLine(file);
        }

        List<String> files = testClient.list();
        for (int i=0; i< Math.max(files.size(), fileList.length); i++){
            assertEquals("List of files was not returned properly", 
                    fileList[i], files.get(i));
        }

        // Read the output to make sure it contains the right request
        String request = getCommandSentToServer();
        assertEquals("LIST", request);

    }

    /**
     * @return the first line of text from clientOutput
     * @throws IOException
     */
    private String getCommandSentToServer() throws IOException{
        // Need to read the byte array to see what it was
        byte[] sentData = clientOutput.toByteArray();
        ByteArrayInputStream input = new ByteArrayInputStream(sentData);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input));
        return reader.readLine();

    }



    /**Test GETting a file from the server.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCommand() throws Exception {
        final String fakeFilename = "Fake file.bin";
        mockInput.returnLine("OK");
        mockInput.returnBytes(randomData);

        byte[] receivedBytes = testClient.get(fakeFilename);

        // Check that the client passed on the data to the stream we gave it
        assertEquals("Incorrect length", 
                randomData.length, receivedBytes.length);


        for (int i=0; i < randomData.length; i++){
            assertEquals("The returned stream didn't have the same contents "
                    + "as what was sent", randomData[i], receivedBytes[i]);
        }

        // Check that it asked for that data properly
        String request = getCommandSentToServer();
        assertEquals("GET "+fakeFilename, request);

    }

    /**
     * Test the PUT command on some random data.
     * @throws Exception
     */
    @Test
    public void testPutCommand() throws Exception {
        final String filename = "Test file name.bin";

        // The input just tells the client to go ahead
        mockInput.returnLine("OK");

        testClient.put(filename, randomData);

        // Time to read what the client said:
        ByteArrayInputStream fromClient = 
            new ByteArrayInputStream(clientOutput.toByteArray());


        // Once again, we need binary and character data...sigh.
        StringBuilder requestLine = new StringBuilder();
        while (!requestLine.toString().endsWith("\n")){
            requestLine.append((char) fromClient.read());
        }
        String request = requestLine.toString().replaceAll("[\r\n]", "");
        assertEquals("PUT request was incorrect", "PUT "+filename, request);


        int i = 0;
        int readByte;
        while ((readByte = fromClient.read()) != -1){
            assertEquals("The returned stream didn't have the same contents "
                    + "as what was sent", randomData[i], (byte) readByte);
            i++;
        }
        assertEquals("Stream ended too early", randomData.length, i);

    }

    /**
     * Test the AUTOSAVE command on some random data.
     * @throws Exception
     */
    @Test
    public void testAutosaveCommand() throws Exception {
        final String studentname = "Jimmy the testing kid";
        // Just have the "server" say OK to whatever
        mockInput.returnLine("OK");

        // Create a mock SaveXML
        final SaveBlockXML mockXml = mock(SaveBlockXML.class);

        testClient.autosave(studentname, mockXml);


        // Time to read what the client said:
        String request = getCommandSentToServer();
        assertEquals("AUTOSAVE request was incorrect", 
                "AUTOSAVE "+studentname, request);

        // We know that it wraps it in a PrintStream... it could
        // possibly pass in the wrong one, I guess, but that seems
        // unlikely.
        verify(mockXml).writeDocumentToStream(any(PrintStream.class));
    }

    /**
     * Test appending to a file
     * @throws Exception 
     */
    @Test
    public void testAppend() throws Exception{


        mockInput.returnLine("OK");

        testClient.append("filename.txt", "Contents");

        ByteArrayInputStream fromClient = 
            new ByteArrayInputStream(clientOutput.toByteArray());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(fromClient));
        String request = in.readLine();
        assertEquals("APPEND filename.txt", request);

        String challenge = in.readLine();
        assertEquals("Contents", challenge);
        assertNull(in.readLine());
    }


    /**
     * Test appending multiple lines
     * @throws Exception 
     */
    @Test
    public void testAppendMultiple() throws Exception{
        mockInput.returnLine("OK");
        
        testClient.append("filename.txt", 
                          "Appended challenge",
                          "Challenge 2");


        ByteArrayInputStream fromClient =
            new ByteArrayInputStream(clientOutput.toByteArray());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(fromClient));
        String request = in.readLine();
        assertEquals("APPEND filename.txt", request);
        String challenge = in.readLine();
        assertEquals("Appended challenge", challenge);
        challenge = in.readLine();
        assertEquals("Challenge 2", challenge);
        assertNull(in.readLine());
    }
    
    /**
     * @throws Exception 
     * 
     */
    @Test
    public void testAddChallenges() throws Exception {
        mockInput.returnLine("OK");
        
        Challenge challenge1 = new FreeformChallenge("Test challenge");
        Map<Fabric, Fraction> map = new HashMap<Fabric, Fraction>();
        Challenge challenge2 = new FractionChallenge("Test", map);
        
        List<Challenge> challenges = new ArrayList<Challenge>();
        challenges.add(challenge1);
        challenges.add(challenge2);
        
        testClient.addChallenges(challenges);
        
        BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(clientOutput.toByteArray())));
        
        assertEquals("ADD CHALLENGES", in.readLine());
        
        // Put the rest of the response *back* into an inputstream so
        // that the parser can parse it and be none the wiser... sigh...
        MockInputStream xml = new MockInputStream();
        String input;
        while ((input = in.readLine()) != null){
            xml.returnLine(input);
        }
        
        
        ChallengeFileParser parser = new ChallengeFileParser(xml);
        
        assertEquals(challenges, parser.getChallenges());
        
    }

    /**
     * Make sure the given command throws an exception
     * detailing the error the "server" reported.
     * 
     * @param c
     */
    private void testCommandReportsServerError(Command c){
        mockInput.returnLine(SERVER_ERROR_STRING);

        try {
            c.runCommand();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            assertEquals("Incorrect exception message", 
                    "Server reported error: Test error message",
                    e.getMessage());
            return;
        } catch (Exception e){
            fail("Incorrect exception was thrown: "+e.getMessage());
        }
        fail("An exception wasn't thrown");

    }

    /**
     * Makes sure that if an error is reported by the server during a list(),
     * that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testListReportsServerError() throws Exception {
        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.list();
            }
        });
    }

    /**
     * Makes sure that if an error is reported by the server during a get(),
     * that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testGetReportsServerError() throws Exception {
        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.get("Filename");
            }

        });
    }

    /**
     * Makes sure that if an error is reported by the server during a put(),
     * that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testPutReportsServerError() throws Exception {

        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.put("File", randomData);
            } 
        });
    }

    /**
     * Makes sure that if an error is reported by the server during an 
     * autosave(), that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testAutosaveReportsServerError() throws Exception {

        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.autosave("Name", mock(SaveBlockXML.class));
            }
        });
    }



    /**
     * Makes sure that if an error is reported by the server during an 
     * autosave(), that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testAppendReportsServerError() throws Exception {

        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.append("filename.txt", "Some stuff");
            }
        });
    }
    
    /**
     * Makes sure that if an error is reported by the server during an 
     * addChallenges(), that error is passed up via an IOException.
     * 
     * @throws Exception
     */
    @Test
    public void testAddChallengesReportsServerError() throws Exception {

        testCommandReportsServerError(new Command(){

            public void runCommand() throws Exception {
                testClient.addChallenges(new ArrayList<Challenge>());
            }
        });

    }
}
