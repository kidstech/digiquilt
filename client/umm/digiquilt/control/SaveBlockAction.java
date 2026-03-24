/*
 * Created by jbiatek on Nov 19, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.GridViewPanel;
import umm.digiquilt.view.challenge.ChallengePanel;
import umm.digiquilt.view.filebrowser.BlockSaveBrowser;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class SaveBlockAction extends AbstractAction {

    private BlockWorks blockWorks;
    private ComboBoxModel challengePanel;
    private SaveHandler handler;
    
    public SaveBlockAction(BlockWorks blockWorks, 
            ComboBoxModel challengePanel, SaveHandler handler){
        super("Save...");
        this.blockWorks = blockWorks;
        this.challengePanel = challengePanel;
        this.handler = handler;
    }
    
    public void actionPerformed(ActionEvent e){
        Challenge currentChallenge = 
            (Challenge) challengePanel.getSelectedItem();
        if (currentChallenge != null){
            if (!currentChallenge.blockMatchesChallenge(
                    blockWorks.getCurrentBlockClone())){
                String message = "This quilt block does not complete " +
                "the current challenge:\n\n";
                message += currentChallenge.toString();
                JOptionPane.showMessageDialog(
                        null, 
                        message, 
                        "Does not complete challenge", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        BlockSaveBrowser saveBrowser = 
            new BlockSaveBrowser(
                    blockWorks,
                    currentChallenge,
                    handler);
        saveBrowser.setVisible(true);
    }

}
