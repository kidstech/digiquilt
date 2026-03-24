package umm.digiquilt.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.FractionChallenge;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.fabriccontrols.FabricPalette;
import umm.digiquilt.view.filebrowser.BlockSaveBrowser;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class CreateChallengeAction implements ActionListener {
    
    /**
     * Access to the current block
     */
    private BlockWorks blockWorks;
    /**
     * Access to the current fractions.
     */
    private FabricPalette palette;
    /**
     * The SaveHandler to use to save.
     */
    private SaveHandler handler;
    
    
    /**
     * Create an Action which will handle allowing the user to create a
     * challenge and share it with others.
     * 
     * 
     * @param blockWorks
     * @param currentGrid
     * @param palette
     * @param handler
     */
    public CreateChallengeAction(BlockWorks blockWorks, 
                FabricPalette palette,
                SaveHandler handler){
        this.blockWorks = blockWorks;
        this.palette = palette;
        this.handler = handler;
    }
    
    public void actionPerformed(ActionEvent e) {
        // Need to create the new challenge from the fractions
        // being shown
        Map<Fabric, Fraction> fractions = palette.getFractions();
        Challenge newChallenge = 
            new FractionChallenge(handler.getStudentName(), fractions);
        
        
        // Okay, we have the new challenge, time to ask the user to save.
        BlockSaveBrowser browser = new BlockSaveBrowser(
                blockWorks, newChallenge, handler);
        browser.setName("Create challenge Save browser");
        browser.setVisible(true);
        
        // If they chose to save, we add the new challenge to the server.
        if (browser.getExitStatus() == BlockSaveBrowser.SAVED){
            try {
                handler.addChallenge(newChallenge);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(
                        null, 
                        "An error occured trying to add the challenge.", 
                        "I/O Error", 
                        JOptionPane.ERROR_MESSAGE);
                
                e1.printStackTrace();
            } catch (SAXException se) {
                // TODO Auto-generated catch block
                se.printStackTrace();
            }
        }
        
        
    }

}
