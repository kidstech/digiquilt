/*
 * Created by biatekjt on Oct 25, 2008
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.control;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import umm.digiquilt.savehandler.SaveHandler;

import umm.digiquilt.view.PatchViewer;
import umm.digiquilt.view.filebrowser.PatchFileChooser;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * @author Scott Steffes, last changed by $Author: steffessw $
 * on $Date: 2012-04-02 21:59:13 $
 * @version $Revision: 1.5 $
 *
 */
public class LoadPatchAction extends AbstractAction {

    /**
     * The starting directory for the file loader.
     */
    File defaultDirectory;
    
    /**
     * The file which should be selected when the chooser first pops up.
     * This could be null if one isn't specified.
     */
    private File selectedFile;

    /**
     * The SaveHandler, which does synchronization before we load.
     */
    private SaveHandler handler;
    
    /**
     * The Patchworks that the loaded patch is sent to.
     */
    private PatchViewer patchViewer;
    

    /**
     * Present to the user a file chooser to load a new DigiQuilt file. The
     * contents of this file will be reconstituted into Java objects and
     * loaded into the given BlockWorks object, overwriting whatever is
     * currently in it. The Grid that is loaded from file is displayed
     * in the given GridViewer.
     * 
     * If the user hits cancel or otherwise doesn't load a file,
     * nothing happens.
     * 
     * @param blockWorks
     * @param grid
     * @param handler 
     * @param challenges 
     */
    public LoadPatchAction(PatchViewer patchViewer, SaveHandler handler){
        this(patchViewer, handler, null);
    }
    
    
    /**
     * Present to the user a file chooser to load a new DigiQuilt file. The
     * contents of this file will be reconstituted into Java objects and
     * loaded into the given BlockWorks object, overwriting whatever is
     * currently in it. The Grid that is loaded from file is displayed
     * in the given GridViewer.
     * 
     * If the user hits cancel or otherwise doesn't load a file,
     * nothing happens.
     * 
     * @param blockWorks
     * @param grid
     * @param handler 
     * @param challenges 
     * @param selectedFile The file which will be selected by default for the 
     * user.
     */
    public LoadPatchAction(PatchViewer patchViewer, SaveHandler handler, File selectedFile){
        super("Open...");
        this.patchViewer = patchViewer;
        this.handler = handler;
        this.selectedFile = selectedFile;
        defaultDirectory = handler.getSaveDirectory();

    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event){      
        showOpenDialog();
    }
    
    public void showOpenDialog(){
        try {
            handler.synchronize();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null,
                    "An error occured trying to connect to the server.",
                    "I/O error",
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }

        PatchFileChooser loadPanel = new PatchFileChooser(defaultDirectory);
        if (selectedFile != null){
            loadPanel.setSelectedFile(selectedFile);        
        }
        
        int returnVal = loadPanel.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION){
            try {
                File selected = loadPanel.getSelectedFile();
                LoadXML loader = new LoadXML(selected.getPath());
                patchViewer.setPatch(loader.getCurrentBlock().getPatch(0));
//                blockWorks.setCurrentBlock(loader.getCurrentBlock());
//                blockWorks.setNewUndoStack(loader.getUndoRedoStack());
//                Grid loadedGrid = loader.getGrid();
//                challenges.setSelectedItem(loader.getChallenge());
//                if (loadedGrid != null){
//                    blockWorks.setGrid(loader.getGrid());
//                } else {
//                    blockWorks.setGrid(new Grid());
//                }
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null,
                        "An error occured trying to load this file.",
                        "File I/O Error",
                        JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            } catch (SAXException e1) {
                e1.printStackTrace();
            }
        }
    }

}
