package umm.digiquilt.view.filebrowser;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class PatchFileChooser extends JFileChooser {
    
    /**
     * Create a PatchFileChooser with the given directory as the 
     * default/home directory. The parent of this directory is assumed
     * to be the main quilt directory.
     * 
     * @param defaultDirectory
     */
    public PatchFileChooser(File defaultDirectory){
        // Set the file system view using the JFileChooser constructor
	// QuiltFSView doesn't do anything Quilt specific so it will work for patches
        super(defaultDirectory, new QuiltFSView(
                defaultDirectory.getParentFile(), defaultDirectory));
        
        this.setAcceptAllFileFilterUsed(false);
        this.setFileFilter(new PatchFileFilter());
        this.setFileView(new PatchFileView());
        PatchAccessory accessory = new PatchAccessory();
        this.setAccessory(accessory);
        this.addPropertyChangeListener(accessory);
        
        // This might be an undocumented hack, I'm not sure. But it should
        // make it so that things like creating new directories and renaming
        // files are disabled (globally).
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        
        this.setPreferredSize(new Dimension(900,600));

    }
}
