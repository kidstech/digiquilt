/*
 * Created by jbiatek on Nov 19, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.PatchViewer;
import umm.digiquilt.view.filebrowser.PatchSaveBrowser;

/**
 * @author Scott Steffes, last changed by $Author: steffess $
 * on $Date: 2012/04/29 17:50:24 $
 * @version $Revision: 1.2 $
 *
 */

public class SavePatchAction extends AbstractAction {

    /**
     * The SaveHandler, which does synchronization before we load.
     */
    private SaveHandler handler;
    
    /**
     * The PatchViewer the patch to save comes from.
     */
    private PatchViewer patchViewer;
    
    public SavePatchAction(PatchViewer patchViewer, SaveHandler handler){
        super("Save...");
        this.patchViewer = patchViewer;
        this.handler = handler;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        PatchSaveBrowser saveBrowser = 
            new PatchSaveBrowser(patchViewer, handler);
        saveBrowser.setVisible(true);
    }

}
