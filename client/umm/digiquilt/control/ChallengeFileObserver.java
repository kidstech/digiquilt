/*
 * Created by biatekjt on Apr 28, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.MutableComboBoxModel;

import org.xml.sax.SAXException;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.savehandler.SyncListener;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class ChallengeFileObserver implements SyncListener {
    
    /**
     * The ComboBoxModel of all the challenges.
     */
    MutableComboBoxModel challenges;

    /**
     * @param challengeList
     */
    public ChallengeFileObserver(MutableComboBoxModel challengeList) {
        this.challenges = challengeList;
    }

    /* (non-Javadoc)
     * @see umm.digiquilt.savehandler.SyncListener#onSynchronize(umm.digiquilt.savehandler.SaveHandler)
     */
    public void onSynchronize(SaveHandler handler) {
        File challengeFile = new File(handler.getSaveDirectory(), 
                SaveHandler.CHALLENGE_FILE_LOCATION);
        ChallengeFileParser parser;
        try {
            parser = new ChallengeFileParser(new FileInputStream(challengeFile));
        } catch (SAXException e) {
            // We just give up...
            return;
        } catch (IOException e) {
            // Just give up here too.
            return;
        }
        
        List<Challenge> challengeList = parser.getChallenges();
        Object selected = challenges.getSelectedItem();
        while (challenges.getSize() > 0){
            challenges.removeElementAt(0);
        }
        for (Challenge challenge : challengeList){
            challenges.addElement(challenge);
        }
        challenges.setSelectedItem(selected);
        
    }

}
