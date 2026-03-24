package umm.digiquilt.view.filebrowser;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import umm.digiquilt.savehandler.SaveHandler;

public class PatchFileFilter extends FileFilter {

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        return (f.getName().endsWith(SaveHandler.PATCH_EXT) || f.isDirectory());
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "DigiQuilt patch files";
    }

}
