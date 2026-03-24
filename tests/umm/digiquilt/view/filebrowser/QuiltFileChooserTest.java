/*
 * Created by jbiatek on Jul 8, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.filebrowser;

import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import org.junit.Test;

import umm.digiquilt.view.filebrowser.QuiltAccessory;
import umm.digiquilt.view.filebrowser.QuiltFSView;
import umm.digiquilt.view.filebrowser.QuiltFileChooser;
import umm.digiquilt.view.filebrowser.QuiltFileFilter;
import umm.digiquilt.view.filebrowser.QuiltFileView;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-08 19:20:28 $
 * @version $Revision: 1.1 $
 *
 */

public class QuiltFileChooserTest {

    /**
     * Test that the custom file chooser is set up with all the correct
     * settings.
     * @throws Exception 
     */
    @Test
    public void testFileChooserSetup() throws Exception { 
        File fakeDir = File.createTempFile("filechooser", ".tmp");
        fakeDir.delete();
        fakeDir.mkdir();
        File root1 = new File(fakeDir, "First child");
        root1.mkdir();
        File root2 = new File(fakeDir, "Second child");
        root2.mkdir();

        QuiltFileChooser testChooser = new QuiltFileChooser(root2);
        
        assertEquals("The property FileChooser.readOnly should be set to true",
            Boolean.TRUE, UIManager.get("FileChooser.readOnly"));

        
        FileSystemView fsv = testChooser.getFileSystemView();
        assertTrue("File system view must be a QuiltFSV",
                fsv instanceof QuiltFSView);
        QuiltFSView qfsv = (QuiltFSView) fsv;
        
        assertEquals("File system view has the wrong default directory",
                root2, qfsv.getHomeDirectory());
        assertEquals("File system view has the wrong number of roots",
                2, qfsv.getRoots().length);
        
        assertTrue("File chooser needs to have a QuiltFileFilter",
                testChooser.getFileFilter() instanceof QuiltFileFilter);
        assertTrue("File chooser needs to have a QuiltFileView",
                testChooser.getFileView() instanceof QuiltFileView);
        assertFalse("File chooser shouldn't allow the All Files filter",
                testChooser.isAcceptAllFileFilterUsed());
        assertTrue("File chooser should have a QuiltAccessory",
                testChooser.getAccessory() instanceof QuiltAccessory);
        // Let's see if it's a PropertyChangeListener
        QuiltAccessory accessory = (QuiltAccessory) testChooser.getAccessory();
        PropertyChangeListener[] listeners = 
            testChooser.getPropertyChangeListeners();
        boolean foundAccessory = false;
        for (PropertyChangeListener listener : listeners){
            if (listener == accessory){
                foundAccessory = true;
            }
        }
        assertTrue("The accessory is not a PropertyChangeListener", 
                foundAccessory);
        
        assertEquals("The preferred size should be 900x600",
                900, testChooser.getPreferredSize().width);
        assertEquals("The preferred size should be 900x600",
                600, testChooser.getPreferredSize().height);
        
    }
    
}
