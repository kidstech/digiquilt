package umm.digiquilt.view.login;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.jmdns.ServiceEvent;

import org.fest.swing.fixture.DialogFixture;
import org.junit.After;
import org.junit.Test;

import umm.digiquilt.savehandler.DQPClient;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.server.QuiltZeroconf;
import umm.digiquilt.view.login.QuiltLogin;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-29 20:50:26 $
 * @version $Revision: 1.4 $
 *
 */
public class QuiltLoginTest {

    /**
     * Class which is only online
     */
    String online = "1 Online-only server";
    /**
     * Class which is only offline
     */
    String offline = "2 Offline-only directory";
    /**
     * This class is online, but then it is removed.
     */
    String removed = "3 A removed class";
    /**
     * A class which is online, but already has a directory. Shouldn't
     * show up twice because of it, though.
     */
    String both = "4 Both local and online";
    
    /**
     * The default class which should always show up
     */
    String defaultClass = "Offline";
    
    /**
     * The FEST fixture
     */
    DialogFixture loginFixture;
    
    /**
     * Clean up the fixture
     */
    @After
    public void tearDown(){
        loginFixture.cleanUp();
    }
    
    
    /**
     * @throws Exception
     */
    @Test
    public void testLoginPrompt() throws Exception {
        QuiltZeroconf qz = makeMockQZ();
        File tempDir = File.createTempFile("quiltlogin", ".tmp");
        tempDir.delete();
        tempDir.mkdir();
        
        // Create directories for the local ones
        File offlineDir = new File(tempDir, offline);
        offlineDir.mkdir();

        File bothDir = new File(tempDir, both);
        bothDir.mkdir();
        
        final QuiltLogin login = new QuiltLogin(qz, tempDir);
        
        
        loginFixture = new DialogFixture(login);
        assertFalse("Prompt shouldn't be visible yet", login.isVisible());
        loginFixture.show();
        loginFixture.requireModal();
        loginFixture.requireVisible();
        
        
        loginFixture.label("message")
            .requireText("Please choose your class and name to log in.");

        loginFixture.comboBox("classes").requireNotEditable();
        
        loginFixture.comboBox("names").requireDisabled();
        
        loginFixture.button("login").requireText("Log in")
                                    .requireDisabled();
        
        // Check initial contents of class box
        String[] contents = loginFixture.comboBox("classes").contents();
        String[] expected = new String[]{offline, both, defaultClass};
        assertArrayEquals("The list is incorrect (note: it should be sorted)",
                expected, contents);

        // Try removing and adding services
        ServiceEvent onlineEvent = makeMockServiceEvent(online);
        ServiceEvent bothEvent = makeMockServiceEvent(both);
        ServiceEvent removedEvent = makeMockServiceEvent(removed);

        login.serviceAdded(removedEvent);
        expected = new String[]{offline, removed, both, defaultClass};
        contents = loginFixture.comboBox("classes").contents();
        assertArrayEquals("The list is incorrect (note: it should be sorted)",
                expected, contents);
        
        
        login.serviceAdded(onlineEvent);
        expected = new String[]{online, offline, removed, both, defaultClass};
        contents = loginFixture.comboBox("classes").contents();
        assertArrayEquals("The list is incorrect (note: it should be sorted)",
                expected, contents);
        
        
        login.serviceAdded(bothEvent);
        // Shouldn't have changed...
        contents = loginFixture.comboBox("classes").contents();
        assertArrayEquals("The list shouldn't have changed for both",
                expected, contents);
        

        login.serviceRemoved(removedEvent);
        expected = new String[]{online, offline, both, defaultClass};
        contents = loginFixture.comboBox("classes").contents();
        assertArrayEquals("Removed element didn't disappear",
                expected, contents);
        
        
        // Remove "both", it should still be there because it exists locally
        login.serviceRemoved(bothEvent);
        contents = loginFixture.comboBox("classes").contents();
        assertArrayEquals("The list shouldn't have changed",
                expected, contents);

        
        
        // Now we pick a class and check the names box
        loginFixture.comboBox("classes").selectItem(both);
        loginFixture.comboBox("names").requireEnabled()
                                      .requireEditable();
        
        
        expected = new String[]{"Name 1", "Name 2", "Name 3"};
        String[] names = loginFixture.comboBox("names").contents();
        assertArrayEquals("Incorrect names given", expected, names);
        
        // Check login button state changes
        // Clear the text field
        loginFixture.comboBox("names").clearSelection();
        // The button should be off
        loginFixture.button("login").requireDisabled();
        // Enter some text
        loginFixture.comboBox("names").enterText("text");
        // The button should now be on
        loginFixture.button("login").requireEnabled();
        // Clear it again
        loginFixture.comboBox("names").enterText("\b\b\b\b");
        // The button should be off again
        loginFixture.button("login").requireDisabled();
        // Select an item from the menu
        loginFixture.comboBox("names").selectItem(0);
        loginFixture.button("login").requireEnabled();
        // Type in our own name
        loginFixture.comboBox("names").replaceText("Custom name");
        loginFixture.button("login").requireEnabled();
        loginFixture.button("login").click();
        
        // It should now be gone
        loginFixture.requireNotVisible();
        
        SaveHandler handler = login.getSaveHandler();
        
        assertEquals("Should be set to the selected class",
                both, handler.getSaveDirectory().getName());
        assertEquals("Should be set to the selected name", 
                "Custom name", handler.getStudentName());
    }
    
    /**
     * Test logging in for the first time ever (no directories created
     * beforehand)
     * @throws Exception 
     */
    @Test
    public void testCleanStart() throws Exception {

        QuiltZeroconf qz = makeMockQZ();
        
        File tempDir = File.createTempFile("quiltlogin", ".tmp");
        tempDir.delete();
        
        final QuiltLogin login = new QuiltLogin(qz, tempDir);
        loginFixture = new DialogFixture(login);
        loginFixture.show();
        
        String[] classes = loginFixture.comboBox("classes").contents();
        String[] expected = new String[]{defaultClass};
        assertArrayEquals("The default option should be there", 
                expected, classes);
        
        // Now test closing the window without making a selection
        loginFixture.button("quit").click();
        loginFixture.requireNotVisible();
        
        SaveHandler handler = login.getSaveHandler();
        
        assertNull("The handler should be null after closing", handler);
        
    }
    
    /**
     * @return a mock QuiltZeroconf that pretends the name file contains
     * "Name 1", "Name 2", and "Name 3".
     * @throws IOException
     */
    private QuiltZeroconf makeMockQZ() throws IOException {
        QuiltZeroconf qz = mock(QuiltZeroconf.class);
        DQPClient mockHandler = mock(DQPClient.class);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(byteOut);
        out.println("Name 1");
        out.println("Name 2");
        out.println("Name 3");
        out.flush();
        
        byte[] nameFile = byteOut.toByteArray();
        
        when(mockHandler.get("names.txt")).thenReturn(nameFile);
        
        when(qz.getConnection(anyString())).thenReturn(mockHandler);
        when(qz.getConnection(anyString(), anyInt())).thenReturn(mockHandler);

        
        return qz;
    }
    
    /**
     * @param name the name of the fake server
     * @return a mock ServiceEvent
     */
    private ServiceEvent makeMockServiceEvent(String name){
        ServiceEvent mockEvent = mock(ServiceEvent.class);
        when(mockEvent.getName()).thenReturn(name);
        when(mockEvent.getType()).thenReturn("_digiQuiltServ._tcp.local.");

        return mockEvent;
    }
    
}
