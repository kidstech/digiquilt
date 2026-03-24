/*
 * Created by biatekjt on Apr 28, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.control;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;

import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.xmlsaveload.ChallengeWriter;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeFileObserverTest {
    
    /**
     * The temporary test directory
     */
    File testDirectory;
    
    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        testDirectory = File.createTempFile("challengeobserver", ".tmp");
        testDirectory.delete();
        testDirectory.mkdir();
        
    }
    /**
     * Test that it checks and updates the combo box when 
     * @throws Exception 
     */
    @Test
    public void testChallengeRefresh() throws Exception {
        MutableComboBoxModel challengeList = new DefaultComboBoxModel();
        
        Challenge test1 = new FreeformChallenge("challenge 1");
        Challenge test2 = new FreeformChallenge("challenge 2");
        dumpChallenges(test1, test2);
        
        ChallengeFileObserver testObserver = 
            new ChallengeFileObserver(challengeList);
        
        assertEquals(0, challengeList.getSize());
        
        testObserver.onSynchronize(makeMockHandler());
        assertEquals(2, challengeList.getSize());
        assertEquals(test1, challengeList.getElementAt(0));
        assertEquals(test2, challengeList.getElementAt(1));
        
        challengeList.setSelectedItem(test2);
        
        Challenge test3 = new FreeformChallenge("Third challenge");
        dumpChallenges(test3, test2, test1);
        
        testObserver.onSynchronize(makeMockHandler());
        assertEquals(3, challengeList.getSize());
        assertEquals(test3, challengeList.getElementAt(0));
        assertEquals(test2, challengeList.getElementAt(1));
        assertEquals(test1, challengeList.getElementAt(2));
        
        assertEquals(test2, challengeList.getSelectedItem());
    }
    
    /**
     * @throws IOException
     */
    @Test
    public void testExceptions() throws IOException{
        MutableComboBoxModel challengeList = new DefaultComboBoxModel();
        
        ChallengeFileObserver testObserver = 
            new ChallengeFileObserver(challengeList);
        
        // There isn't a challenge file at all:
        testObserver.onSynchronize(makeMockHandler());
        assertEquals(0, challengeList.getSize());
        
        // Now write some gibberish instead of a file:
        PrintStream out = new PrintStream(
                new File(testDirectory, SaveHandler.CHALLENGE_FILE_LOCATION));
        out.println("This isn't XML at all");
        out.println("Nope. This should choke and die.");
        testObserver.onSynchronize(makeMockHandler());
        assertEquals(0, challengeList.getSize());

    }
    
    /**
     * @param challenges
     * @throws IOException
     */
    private void dumpChallenges(Challenge... challenges) throws IOException{
        ChallengeWriter writer = new ChallengeWriter();
        
        List<Challenge> list = Arrays.asList(challenges);
        writer.writeToStream(list, new FileOutputStream(
                new File(testDirectory, "challenges.xml")));
    }
    
    /**
     * @return a mock SaveHandler
     */
    private SaveHandler makeMockHandler(){
        SaveHandler mock = mock(SaveHandler.class);
        when(mock.getSaveDirectory()).thenReturn(testDirectory);
        
        return mock;
    }
    
}
