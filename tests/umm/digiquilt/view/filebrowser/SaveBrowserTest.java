/*
 * Created by jbiatek on Aug 14, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.filebrowser;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import org.fest.swing.fixture.DialogFixture;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.GridViewPanel;
import umm.digiquilt.view.filebrowser.BlockSaveBrowser;
import umm.digiquilt.xmlsaveload.LoadXML;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class SaveBrowserTest {
    
    /**
     * Test fixture
     */
    DialogFixture testBrowser;
    
    /**
     * BlockWorks access
     */
    BlockWorks blockWorks;
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        testBrowser.cleanUp();
    }

    /**
     * Test the save dialog
     * @throws Exception 
     */
    @SuppressWarnings("boxing")
    @Test
    public void testSaving() throws Exception {
        blockWorks = new BlockWorks();
        blockWorks.swapFabricInBlock(Fabric.TRANSPARENT, Fabric.GREEN);
        Grid currentGrid = new Grid(4,4,2,2);
        blockWorks.setGrid(currentGrid);
        
        Challenge challenge = new FreeformChallenge("This is a test challenge! Woohoo!");
        SaveHandler handler = mock(SaveHandler.class);
        BlockSaveBrowser browser = 
            new BlockSaveBrowser(blockWorks, challenge, handler);

        testBrowser = new DialogFixture(browser);
        testBrowser.show();
        
        // TODO: These should probably be tested better...
        testBrowser.panel("Block").targetCastedTo(BlockViewer.class);
        testBrowser.panel("Grid").targetCastedTo(GridViewPanel.class);
        testBrowser.textBox("Challenge").requireText(challenge.toString());
        
        String[] prompts = testBrowser.comboBox("Prompts").contents();
        String[] expected = new String[]{
                "<html><h2>I like my design because: </h2></html>",
                "<html><h2>I was wondering: </h2></html>",
                "<html><h2>I didn't understand: </h2></html>",
                "<html><h2>I struggled with: </h2></html>"
        };
        assertArrayEquals(expected, prompts);
        
        // Try clicking Save without a name entered
        testBrowser.button("Save").click();
        testBrowser.optionPane().requireErrorMessage();
        testBrowser.optionPane().requireTitle("Name your quilt");
        testBrowser.optionPane().requireMessage(
                "Please type a name for this quilt");
        testBrowser.optionPane().okButton().click();
        
        // Enter a name, notes, pick a prompt, etc.
        testBrowser.textBox("Quilt name").enterText("Test quilt");
        testBrowser.comboBox("Prompts").selectItem(
                "<html><h2>I was wondering: </h2></html>");
        testBrowser.textBox("Notes").enterText("Notes, notes...");
        testBrowser.button("Save").click();
        
        // Oh, what's this? A name conflict?
        testBrowser.optionPane().requireErrorMessage();
        testBrowser.optionPane().requireTitle("Choose a new quilt name");
        testBrowser.optionPane().requireMessage(
                "A quilt with this name already exists. Please pick a different name.");
        testBrowser.optionPane().okButton().click();
        
        // We'll try again, only this time with an exception instead
        // of a name conflict.
        when(handler.saveBlock(
                any(SaveBlockXML.class), 
                any(BufferedImage.class), 
                anyString()
            )).thenThrow(
                    new IOException(
                            "Test exception: nothing actually went wrong"));
        testBrowser.button("Save").click();
        testBrowser.optionPane().requireErrorMessage();
        testBrowser.optionPane().requireTitle("File I/O Error");
        testBrowser.optionPane().requireMessage(
                "An error occured trying to save this file: "
                + "Test exception: nothing actually went wrong");
        testBrowser.optionPane().okButton().click();

        // Okay, well we'll fix it.
        testBrowser.textBox("Quilt name").deleteText();
        testBrowser.textBox("Quilt name").enterText("Name 2");
        // (this is what really "fixes" it here:)
        reset(handler);
        when(handler.saveBlock(
                any(SaveBlockXML.class), 
                any(BufferedImage.class), 
                eq("Name 2")
            )).thenReturn(true);
        
        testBrowser.button("Save").click();
        
        // The browser should indicate what its exit status was
        assertEquals("The exit status should be 'SAVED'", 
                BlockSaveBrowser.SAVED, browser.getExitStatus());
        

        ArgumentCaptor<SaveBlockXML> captor = 
            ArgumentCaptor.forClass(SaveBlockXML.class);
        
        verify(handler).saveBlock(
                captor.capture(), any(BufferedImage.class), eq("Name 2"));
        
        SaveBlockXML xml = captor.getValue();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        xml.writeDocumentToStream(byteOut);
        xml.writeOutDocumentToFile(new File("/Users/Scott/test.xml.gz"));
        byte[] writtenXML = byteOut.toByteArray();
        
        LoadXML loader = new LoadXML(new ByteArrayInputStream(writtenXML));
        assertEquals("Wrong block saved", blockWorks.getCurrentBlockClone(), 
                loader.getCurrentBlock());
        assertEquals("Incorrect challenge saved", challenge, loader.getChallenge());
        assertEquals("Incorrect notes saved", "I was wondering: Notes, notes...",
                loader.getNotes());   
    }
    
    /**
     * Test hitting "Cancel"
     */
    @Test
    public void testCancel(){
        blockWorks = new BlockWorks();
        Challenge challenge = new FreeformChallenge("Challenge: Press cancel.");
        SaveHandler handler = mock(SaveHandler.class);
        BlockSaveBrowser browser = 
            new BlockSaveBrowser(blockWorks, challenge, handler);

        testBrowser = new DialogFixture(browser);
        testBrowser.show();
        
        testBrowser.button("Cancel").click();
        testBrowser.requireNotVisible();
        
        assertEquals(BlockSaveBrowser.CANCELLED, browser.getExitStatus());

    }
    
}
