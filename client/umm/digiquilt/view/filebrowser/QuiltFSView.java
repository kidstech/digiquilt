/*
 * Created by jbiatek on Mar 11, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.filebrowser;

import java.io.File;
import java.io.IOException;

import java.io.FileFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * QuiltFSView is a custom FileSystemView, which restricts what is visible to the
 * user. Essentially, it pretends that the only files that exist anywhere are those
 * residing in the quilts directory, and that each class folder is a root directory.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */

public class QuiltFSView extends FileSystemView {

    /**
     * The directory containing all the pretend roots of our custom file system.
     */
    private File quiltDir;
    
    /**
     * The directory to default to, also used for the "Home" button in a JFileChooser.
     */
    private File defaultDir;
    
    /**
     * Create a new QuiltFSView, which is a FileSystemView restricted to see only
     * files and folders inside of quiltDir. Each directory in quiltDir becomes a
     * root directory that can be selected, but the user cannot see anything above
     * that. (This does mean that any <i>files</i> inside of quiltDir are also 
     * not accessible.)
     * 
     * The default directory is used as the default starting directory for this
     * FileSystemView, and is also used as the home directory.
     * 
     * @param quiltDir
     * @param defaultDir
     */
    public QuiltFSView(File quiltDir, File defaultDir){
        this.quiltDir = quiltDir;
        this.defaultDir = defaultDir;
    }

    @Override
    public File getDefaultDirectory() {
        return defaultDir;
    }

    @Override
    public File getHomeDirectory() {
        return defaultDir;
    }

    @Override
    public File getParentDirectory(File dir) {
        // TODO Auto-generated method stub
        return super.getParentDirectory(dir);
    }

    @Override
    public File[] getRoots() {
        return quiltDir.listFiles(new FileFilter(){

            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
            
        });
    }
    
    @Override
    public String getSystemDisplayName(File f) {
        // TODO Auto-generated method stub
        return super.getSystemDisplayName(f);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileSystemView#createNewFolder(java.io.File)
     */
    @Override
    public File createNewFolder(File containingDir) throws IOException {
        throw new UnsupportedOperationException("QuiltFSView is read-only.");
    }

    @Override
    public boolean isFileSystemRoot(File dir) {
        return (dir.isDirectory() && isParent(quiltDir, dir));
    }

    @Override
    public boolean isRoot(File f) {
        return (f.isDirectory() && isParent(quiltDir, f));
    }

}
