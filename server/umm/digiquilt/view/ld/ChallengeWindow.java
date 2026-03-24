package umm.digiquilt.view.ld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.MutableComboBoxModel;
import javax.swing.Timer;

import org.xml.sax.SAXException;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.blockAnimation.QuiltSlideShow;
import umm.digiquilt.view.challenge.ChallengePanel;
import umm.digiquilt.xmlsaveload.ChallengeFileParser;

/**
 * A frame which shows a challenge and all the quilts in a directory
 * that fulfill that challenge. The available challenges are loaded
 * from challenges.txt in that directory.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-28 21:59:12 $
 * @version $Revision: 1.4 $
 *
 */
@SuppressWarnings("serial")
public class ChallengeWindow extends JFrame {

    /**
     * The delay before moving to a new challenge.
     */
    private static final int TIMER_DELAY = 30000;
    
    /**
     * The directory to look into to find quilts.
     */
    File directory;

    /**
     * The panel containing all the quilts that were found.
     */
    JPanel quiltPanel = new JPanel();
    
    MutableComboBoxModel challenges = new DefaultComboBoxModel();
    
    /**
     * The challenge panel.
     */
    ChallengePanel selector;

    /**
     * A separate thread to do the heavy lifting of scanning (potentially)
     * lots of quilt files. That way, the GUI doesn't lock up while quilts
     * are being loaded.
     */
    ChallengeWorkerThread worker;

    QuiltSlideShow slideShow;
    
    /**
     * Timer which fires, signaling a move to a new challenge
     */
    Timer autochangeTimer;

    /**Create a new ChallengeWindow, which will look into the given
     * directory to find the list of challenges and the quilts.
     * @param directory
     * @throws IOException 
     * @throws SAXException 
     * @throws FileNotFoundException 
     */
    public ChallengeWindow(File directory) throws FileNotFoundException, SAXException, IOException{

        this.directory = directory;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.BLACK);
        selector = new ChallengePanel(challenges);
        
        loadFromChallengeFile();
        
        this.add(selector, BorderLayout.NORTH);

        quiltPanel.setLayout(new GridBagLayout());
        quiltPanel.setBackground(Color.BLACK);
        JScrollPane scroller = new JScrollPane(quiltPanel);
        this.add(scroller, BorderLayout.CENTER);

        worker = new ChallengeWorkerThread(this, directory);
        worker.setChallenge((Challenge) challenges.getSelectedItem());

        autochangeTimer = new Timer(TIMER_DELAY, new TimerListener());
        autochangeTimer.start();
                
        selector.addActionListener(new ChallengeChangeListener());
        refresh((Challenge) challenges.getSelectedItem());
    }
    
    private void loadFromChallengeFile() throws SAXException, IOException{
        File challengeFile = new File(directory, 
                SaveHandler.CHALLENGE_FILE_LOCATION);
        ChallengeFileParser parser = new ChallengeFileParser(
                new FileInputStream(challengeFile));
        List<Challenge> loadedChallenges = parser.getChallenges();
        
        for (Challenge challenge: loadedChallenges){
            challenges.addElement(challenge);
        }
        

    }

    private class ChallengeChangeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            refresh((Challenge) challenges.getSelectedItem());
            autochangeTimer.restart();
        }

    }

    private class TimerListener implements ActionListener {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            // The timer has called us, let's change the challenge.
            // No need to worry about refreshing, the challenge panel will 
            // tell us to later, since we're changing the challenge.
//            int selected = indexOf(challenges.getSelectedItem());
//            if (selected+1 < challenges.getSize()){
//                challenges.setSelectedItem(challenges.getElementAt(selected+1));
//            } else {
//                challenges.setSelectedItem(challenges.getElementAt(0));
//            }
            
            challenges.setSelectedItem(worker.chooseAutoChallenge());
        }
        
        private int indexOf(Object o){
            if (o == null){
                return -1;
            }
            for (int i=0; i<challenges.getSize(); i++){
                if (o.equals(challenges.getElementAt(i))){
                    return i;
                }
            }
            return -1;
        }
    }
    /**
     * Re-scan directory for the newly selected challenge and display
     * quilts.
     * 
     * @param challenge
     */
    private void refresh(Challenge challenge){
        quiltPanel.removeAll();
        // Start the worker thread to search for the new challenge.
        worker.setChallenge(challenge);
        Thread t = new Thread(worker);
        t.start();
    }

    /**
     * Populate the quilt panel with the given elements. Each List must have
     * the same number of items, or an UnsupportedOperationException will be
     * thrown.
     * 
     * @param blocks
     * @param names
     * @param notes
     */
    public void populatePanel(List<Block> blocks, 
            List<String> names, List<String> notes){
        if (blocks.size() != names.size() || names.size() != notes.size()){
            throw new UnsupportedOperationException();
        }
        
        quiltPanel.removeAll();
        if (slideShow != null) slideShow.stop();
        
        // Constraints for the center thing
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        if (blocks.size() > 1){
            // Create and add the slideshow
            slideShow = new QuiltSlideShow(80, blocks);
            quiltPanel.add(slideShow, c);
        } else if (blocks.size() == 1){
            // There's only one block to show...
            BlockViewer still = new BlockViewer(blocks.get(0));
            quiltPanel.add(still, c);
        }
        
        // Now we add up to 8 individual panels for the blocks
        int numBlocks = Math.min(blocks.size(), 8);
        for (int i=0; i<numBlocks; i++){
            c = new GridBagConstraints();
            c.insets = new Insets(3,3,3,3);
            JPanel panel = makeSinglePanel(
                    blocks.get(i), names.get(i), notes.get(i));
            if (i<2){
                c.gridx = 0;
                c.gridy = i;
            } else if (i>=2 && i<6){
                c.gridx = i-2;
                c.gridy = 2;
            } else {
                c.gridx = 3;
                c.gridy = i==6 ? 1 : 0;
            }
            quiltPanel.add(panel, c);
            
        }
        
        
        this.repaint();
        this.pack();
        
    }

    /**
     * Create a panel to show one block with a name and notes in the tooltip.
     * @param block The block to show
     * @param studentName The name of the student
     * @param notes The notes for this block
     * @return a panel containing all of the above
     */
    public JPanel makeSinglePanel(Block block, String studentName, String notes){
        final JPanel disp = new JPanel();
        disp.setLayout(new BorderLayout());
        disp.setOpaque(false);

        JLabel nameLabel = new JLabel(studentName, JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        disp.add(nameLabel, BorderLayout.SOUTH);

        int patchSize = 144/block.getSideSize();
        BlockViewer blockView = new BlockViewer(block, patchSize);
        blockView.setOpaque(false);
        disp.add(blockView, BorderLayout.CENTER);
        
        disp.setToolTipText(notes);
        
        return disp;
    }


}
