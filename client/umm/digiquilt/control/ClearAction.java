/*
 * Created by jbiatek on Nov 19, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.GridViewPanel;
import umm.digiquilt.view.challenge.ChallengePanel;
import umm.digiquilt.xmlsaveload.SaveBlockXML;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class ClearAction implements ActionListener {
    
    private BlockWorks blockWorks;
    private ComboBoxModel challengePanel;
    private SaveHandler handler;
    
    public ClearAction(BlockWorks blockWorks, ComboBoxModel challengePanel, 
            SaveHandler handler){
        this.blockWorks = blockWorks;
        this.challengePanel = challengePanel;
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            SaveBlockXML xml = new SaveBlockXML(
                    blockWorks.getCurrentBlockClone(),
                    blockWorks.getGrid(),
                    blockWorks.getUndoRedoStack(),
                    handler.getStudentName(),
                    "(autosaved block)",
                    System.currentTimeMillis(),
                    "Autosave",
                    (Challenge) challengePanel.getSelectedItem());
            handler.autosave(xml);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        blockWorks.clear();

    }

}
