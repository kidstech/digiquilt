/*
 * Created by biatekjt on Oct 25, 2008
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.view.filebrowser;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import umm.digiquilt.savehandler.SaveHandler;

/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */
public class QuiltFileFilter extends FileFilter {

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        return (f.getName().endsWith(SaveHandler.QUILT_EXT) || f.isDirectory());
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "DigiQuilt files";
    }

}
