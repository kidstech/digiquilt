package umm.digiquilt.view.challenge;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.RequiredModelMBean;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.challenge.ChallengePanel;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengePanelTest {
    
    /**
     * FEST frame fixture for the test panel
     */
    private FrameFixture window;

    /**
     * Test challenge panel 
     */
    private ChallengePanel panel;
    
    /**
     * The model behind the drop down box.
     */
    private MutableComboBoxModel list = new DefaultComboBoxModel();
    
    /**
     * Test listener
     */
    private ActionListener listener = mock(ActionListener.class);

    /**
     * Set up the frame fixtures, etc.
     */
    @Before
    public void setUp() {
        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                panel = new ChallengePanel(list);
                listener = mock(ActionListener.class);
                panel.addActionListener(listener);
                jframe.add(panel);
                return jframe;  
            }
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }
    
    /**
     * Clean up afterwards
     */
    @After
    public void tearDown(){
        window.cleanUp();
    }
    
    /**
     * Check that the panel is working properly at the start
     */
    @Test
    public void testInitialPanel(){
        window.comboBox("Challenge box").requireNotEditable();
        window.comboBox("Challenge box").requireVisible();
        window.comboBox("Challenge box").requireNoSelection();
        String[] current = window.comboBox("Challenge box").contents();
        assertEquals(0, current.length);
    }
    
    /**
     * Test setting a new list of challenges.
     */
    @Test
    public void testSetChalleges(){
        Challenge mock1 = mockChallenge("Completely new challenge");
        Challenge mock2 = mockChallenge("Where did they come from?");
        Challenge mock3 = mockChallenge("No one knows.");
        
        list.addElement(mock1);
        // Adding an element for the first time should fire this:
        verify(listener).actionPerformed(any(ActionEvent.class));
        list.addElement(mock2);
        list.addElement(mock3);
        
        String[] contents = window.comboBox("Challenge box").contents();
        
        for (int i=0; i<list.getSize(); i++){
            assertEquals(list.getElementAt(i).toString(), contents[i]);
        }
        
        window.comboBox("Challenge box").selectItem(mock2.toString());
        verify(listener, times(2)).actionPerformed(any(ActionEvent.class));
        
        
        // "Next" button checks:
//        assertChallengeIs("Completely new challenge");
//        window.button("Next").click();
//        assertChallengeIs("Where did they come from?");
//        window.button("Next").click();
//        assertChallengeIs("No one knows.");
//        window.button("Next").click();
//        assertChallengeIs("Completely new challenge");
        
        // Now if we set new challenges, it should try to hang on to
        // the one that's currently showing, even if it moves around
        list.addElement(mockChallenge("Another set of new challenges"));
        list.addElement(mockChallenge("Completely new challenge"));
        list.addElement(mockChallenge("Oh wait, it's not new anymore."));
        list.addElement(mockChallenge("Oh well."));
        
        contents = window.comboBox("Challenge box").contents();
        window.comboBox("Challenge box").requireSelection(mock2.toString());
        
        for (int i=0; i<list.getSize(); i++){
            assertEquals(list.getElementAt(i).toString(), contents[i]);
        }

        // More next button checks:
//        assertChallengeIs("Completely new challenge");
//        window.button("Next").click();
//        assertChallengeIs("Oh wait, it's not new anymore.");
//        window.button("Next").click();
//        assertChallengeIs("Oh well.");
//        window.button("Next").click();
//        assertChallengeIs("Another set of new challenges");
//        window.button("Next").click();
//        assertChallengeIs("Completely new challenge");

        window.comboBox("Challenge box").selectItem(mock3.toString());
        verify(listener, times(3)).actionPerformed(any(ActionEvent.class));
        
        // Setting it programatically should work too
        list.setSelectedItem(list.getElementAt(0));
        verify(listener, times(4)).actionPerformed(any(ActionEvent.class));

    }
    
    private Challenge mockChallenge(String text){
        Challenge mock = mock(Challenge.class);
        when(mock.toString()).thenReturn(text);
        
        return mock;
    }
    
    
}
