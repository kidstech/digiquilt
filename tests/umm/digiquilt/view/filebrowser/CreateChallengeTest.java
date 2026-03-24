package umm.digiquilt.view.filebrowser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.control.CreateChallengeAction;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.Shape;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.fabriccontrols.FabricPalette;
import umm.digiquilt.xmlsaveload.SaveBlockXML;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class CreateChallengeTest {
    
    /**
     * The robot to clean up after the test is done
     */
    Robot robot;
    
    /**
     * A temporary directory to work in
     */
    File tempDir;
    
    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        tempDir = File.createTempFile("challenge", ".tmp");
        tempDir.delete();
        tempDir.mkdir();
    }
    
    /**
     * Clean up FEST
     */
    @After
    public void cleanUp(){
        robot.cleanUp();
        tempDir.delete();
    }
    
    /**
     * @throws Exception 
     * 
     */
    @SuppressWarnings("boxing")
    @Test
    public void testCreateAChallenge() throws Exception {
        BlockWorks blockWorks = new BlockWorks();
        FabricPalette palette = new FabricPalette(blockWorks);
        SaveHandler mockHandler = mock(SaveHandler.class);
        when(mockHandler.getStudentName()).thenReturn("Student name");
        when(mockHandler.saveBlock(
                any(SaveBlockXML.class), 
                any(BufferedImage.class), 
                eq("Test quilt")))
            .thenReturn(true);
        
        final CreateChallengeAction testAction = new CreateChallengeAction(
                blockWorks, 
                palette,
                mockHandler);
        // Set up things for the test:
        // Should be 1/16 orange, 1/8 black, 13/16 blue
        blockWorks.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.BLUE);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 0);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 1);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.ORANGE), 2);
        
        // Show the palette so we can click it a bit
        JFrame paletteFrame = new JFrame();
        paletteFrame.add(palette);
        
        FrameFixture paletteFixture = new FrameFixture(paletteFrame);
        robot = paletteFixture.robot;
        paletteFixture.show();
        paletteFixture.button("Black fraction").click();
        paletteFixture.button("Black fraction").click();
        paletteFixture.button("Orange fraction").click();
        
        // Call the test action
        SwingUtilities.invokeLater(new Runnable(){
            
            public void run() {
                testAction.actionPerformed(null);
            }
        });
        
        DialogFixture browser = 
            new DialogFixture(robot, "Create challenge Save browser");
        // Order in a Map is not defined, so it could be one of a few options
        String displayedChallenge = browser.textBox("Challenge").text();
        Map<Fabric, Fraction> expectedMap = new HashMap<Fabric, Fraction>();
        expectedMap.put(Fabric.ORANGE, new Fraction(2, 32));
        expectedMap.put(Fabric.BLUE, new Fraction(13, 16));
        expectedMap.put(Fabric.BLACK, new Fraction(3, 24));
        Challenge expectedChallenge = 
            new FractionChallenge("Student name", expectedMap);
        
        assertEquals(expectedChallenge.toString(), displayedChallenge);

        browser.textBox("Quilt name").enterText("Test quilt");
        browser.button("Save").click();
        
        verify(mockHandler).addChallenge(expectedChallenge);
        
    }
    
    /**
     * Pressing cancel should result in no challenge being added
     * @throws Exception 
     */
    @Test
    public void testCancel() throws Exception {
        BlockWorks blockWorks = new BlockWorks();
        FabricPalette palette = new FabricPalette(blockWorks);
        SaveHandler handler = mock(SaveHandler.class);
        final CreateChallengeAction testAction = new CreateChallengeAction(
                blockWorks, 
                palette,
                handler);
        
        // Call the test action
        SwingUtilities.invokeLater(new Runnable(){
            
            public void run() {
                testAction.actionPerformed(null);
            }
        });
        
        DialogFixture browser = 
            new DialogFixture("Create challenge Save browser");
        robot = browser.robot;
        browser.button("Cancel").click();
        verify(handler, never()).addChallenge(any(Challenge.class));
        
    }
}
