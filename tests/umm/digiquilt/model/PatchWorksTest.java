package umm.digiquilt.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.awt.Image;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.glasspane.HandGlassPane;

/**
 * Test PatchWorks, which keeps track of the currently held Patch
 * in the client.
 */
public class PatchWorksTest{
    
    /**
     * The test object
     */
    private PatchWorks testWorks;
    
    /**
     * A mock glass pane
     */
    private HandGlassPane mockPane;
    
    /**
     * Initialize the test objects before each test.
     */
    @Before
    public void setUp(){
        mockPane = mock(HandGlassPane.class);
        testWorks = new PatchWorks(mockPane, 100);
    }

	/**
	 * Test setting and releasing/dropping the patch.
	 */
    @Test
	public void testSetAndReleasePatch() {
	    assertFalse("isHeld should be false at first", testWorks.getIsHeld());
	    assertFalse("fromBWA should be false at first", 
	            testWorks.getFromBWA());
	    assertEquals("Current patch should be blank at first", 
	            new Patch(), testWorks.getCurrentPatch());
	    verify(mockPane, never()).setVisible(anyBoolean());
	    
	    
		testWorks.setSelectedPatch(PatchTest.makeFancyTestPatch(), true);
		assertTrue("isHeld should be true after setting a patch", 
		        testWorks.getIsHeld());
		assertEquals("The current patch should be the fancy test patch",
		        PatchTest.makeFancyTestPatch(), testWorks.getCurrentPatch());
		assertTrue("The patch should be marked as from the BWA", 
		        testWorks.getFromBWA());
		verify(mockPane).setVisible(true);
		
		ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);
		verify(mockPane).setHand(captor.capture());
		Image capturedImage = captor.getValue();
		
		assertEquals("Image should have been sent to glass pane", 
                100, capturedImage.getWidth(null));
		assertEquals("Image should have been sent to glass pane", 
		        100, capturedImage.getHeight(null));
		
		
		testWorks.releasePatch();
		assertFalse("isHeld should be false after releasing", 
		        testWorks.getIsHeld());
		assertFalse("FromBWA should be false after releasing", 
		        testWorks.getIsHeld());
        assertEquals("After releasing the current patch should be the default patch",
                new Patch(), testWorks.getCurrentPatch());
        verify(mockPane).setVisible(false);
        
        
        testWorks.setSelectedPatch(PatchTest.makeFancyTestPatch(), false);
        assertTrue("isHeld should be true after setting a patch", 
                testWorks.getIsHeld());
        assertEquals("The current patch should be the fancy test patch",
                PatchTest.makeFancyTestPatch(), testWorks.getCurrentPatch());
        assertFalse("The patch should not be marked as from the BWA", 
                testWorks.getFromBWA());
        verify(mockPane, times(2)).setVisible(true);
	}
	
    /**
     * Test the isHeld() boolean value.
     */
    @Test
	public void testGetIsHeld() {
		testWorks.setSelectedPatch(PatchTest.makeFancyTestPatch(), false);
		assertTrue("Is held should be true", testWorks.getIsHeld());
		testWorks.releasePatch();
		assertFalse("Is held should be false", testWorks.getIsHeld());
	}
	
	/**
	 * Test setting the current Patch to an empty Patch. This could cause
	 * problems, since an empty Patch would collapse into nothing and
	 * generally mess up the glasspane.
	 */
	@Test
	public void testSetSelectedPatchToNewPatch() {
        testWorks.setSelectedPatch(PatchTest.makeFancyTestPatch(), false);

		assertEquals("The currentPatch should be the FancyPatch", PatchTest.makeFancyTestPatch(), testWorks.getCurrentPatch());
		
        testWorks.setSelectedPatch(new Patch(), false);
        
        
		assertEquals("setSelectedPatch(new Patch()) should not change it",
		        PatchTest.makeFancyTestPatch(), testWorks.getCurrentPatch());
		assertTrue("isHeld should still be true", testWorks.getIsHeld());
	}

}
