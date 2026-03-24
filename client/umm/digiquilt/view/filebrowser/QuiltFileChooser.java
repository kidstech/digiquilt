/*
 * Created by jbiatek on Jul 8, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.filebrowser;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

/**
 * File chooser that is customized for DigiQuilt.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-08 19:20:28 $
 * @version $Revision: 1.1 $
 *
 */

@SuppressWarnings("serial")
public class QuiltFileChooser extends JFileChooser {
    
    /**
     * Create a QuiltFileChooser with the given directory as the 
     * default/home directory. The parent of this directory is assumed
     * to be the main quilt directory.
     * 
     * @param defaultDirectory
     */
    public QuiltFileChooser(File defaultDirectory){
        // Set the file system view using the JFileChooser constructor
        super(defaultDirectory, new QuiltFSView(
                defaultDirectory.getParentFile(), defaultDirectory));
        
        this.setAcceptAllFileFilterUsed(false);
        this.setFileFilter(new QuiltFileFilter());
        this.setFileView(new QuiltFileView());
        QuiltAccessory accessory = new QuiltAccessory();
        this.setAccessory(accessory);
        this.addPropertyChangeListener(accessory);
        
        // This might be an undocumented hack, I'm not sure. But it should
        // make it so that things like creating new directories and renaming
        // files are disabled (globally).
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        
        this.setPreferredSize(new Dimension(900,600));

    }
    
}
