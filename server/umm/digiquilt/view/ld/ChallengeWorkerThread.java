/*
 * Created by biatekjt on Apr 11, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.view.ld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Challenge;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-17 20:08:35 $
 * @version $Revision: 1.3 $
 *
 */
public class ChallengeWorkerThread implements Runnable {

    /**
     * Reference back to the ChallengeWindow
     */
    ChallengeWindow window;

    /**
     * The directory to look in.
     */
    File directory;

    /**
     * The currently selected challenge.
     */
    Challenge challenge;
    
    /**
     * List of challenges, in order of what we've seen recently. The idea is
     * to show new quilts as they are saved first.
     */
    List<Challenge> challengeQueue = new ArrayList<Challenge>();

    /**
     * A cache of the contents of files so that they only have to be
     * scanned once.
     */
    Map<File, CachedFileInfo> cache = new HashMap<File, CachedFileInfo>();

    /**
     * Create a new ChallengeWorkerThread for the given window and
     * directory.
     * 
     * @param window
     * @param directory
     */
    public ChallengeWorkerThread(ChallengeWindow window, File directory){
        this.window = window;
        this.directory = directory;


    }

    /**
     * Set the current challenge to search for.
     * 
     * @param newChallenge
     */
    public void setChallenge(Challenge newChallenge){
        challenge = newChallenge;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        File[] ls = directory.listFiles(new FilenameFilter(){

            public boolean accept(File dir, String name) {
                return name.endsWith(SaveHandler.QUILT_EXT);
            }

        });
        // Sort by date created
        Arrays.sort(ls, new SortByDate());

        final int numFiles = ls.length;
        ProgressMonitor monitor = new ProgressMonitor(window, 
                "Getting quilts...", null, 0, numFiles);

        int fileNumber = 0;
        final ArrayList<Block> blocks = new ArrayList<Block>();
        final ArrayList<String> names = new ArrayList<String>();
        final ArrayList<String> notes = new ArrayList<String>();
        for (File file : ls){
            fileNumber++;
            CachedFileInfo info = infoFromFile(file);

            if (info != null && challenge.equals(info.getChallenge())){
                Block loadedBlock = info.getBlock();
                String studentName = info.getStudent();
                String filenotes = info.getNotes();
                
                blocks.add(loadedBlock);
                names.add(studentName);
                notes.add(filenotes);
            } 
            monitor.setProgress(fileNumber);
        }
        monitor.close();

        Collections.reverse(blocks);
        Collections.reverse(names);
        Collections.reverse(notes);
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                window.populatePanel(blocks, names, notes);
            }

        });
    }


    /**
     * Return the loaded file information for this file. If we've seen it
     * before it can be loaded from cache, but otherwise this method will
     * load it and cache the information. If the file is new, this method will
     * update the challenge queue to show the new file sometime soon.
     * 
     * If something goes wrong while loading the file, this could return
     * null.
     * 
     * @param file
     * @return a CachedFileInfo for this file.
     */
    private CachedFileInfo infoFromFile(File file){
        if (!cache.containsKey(file)){
            // This one has never been loaded, so let's load it.
            try {
                LoadXML loaded = new LoadXML(new FileInputStream(file));
                Challenge fileChallenge = loaded.getChallenge();
                Block loadedBlock = loaded.getCurrentBlock();
                String notes = loaded.getNotes();
                String student = loaded.getStudent();
                cache.put(file, new CachedFileInfo(
                        loadedBlock, fileChallenge, notes, student));
                updateChallengeQueue(fileChallenge);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cache.get(file);
    }

    /**
     * Move the given challenge to the top of the queue. If it's not
     * already in the queue, it will be added.
     * 
     * @param fileChallenge
     */
    private void updateChallengeQueue(Challenge fileChallenge){
        if (challengeQueue.contains(fileChallenge)){
            // Remove it so we can put it at the top
            challengeQueue.remove(fileChallenge);
        }
        challengeQueue.add(fileChallenge);
    }
    
    /**
     * @return the next challenge that should be shown, assuming the window
     * is auto-changing.
     */
    public Challenge chooseAutoChallenge(){
        Challenge topChallenge = 
            challengeQueue.remove(challengeQueue.size()-1);
        // Put it back, but at the beginning
        challengeQueue.add(0, topChallenge);
        return topChallenge;
    }

    /**
     * A private class to hold information about a file after it is
     * scanned.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-06-17 20:08:35 $
     * @version $Revision: 1.3 $
     *
     */
    private class CachedFileInfo{

        /**
         * The block for this file.
         */
        private final Block block;

        /**
         * The challenge for this file.
         */
        private final Challenge myChallenge;
        
        /**
         * The notes for this file
         */
        private final String myNotes;
        
        /**
         * The student who created this file
         */
        private final String myStudent;

        /**
         * Create a new CachedFileInfo.
         * 
         * @param block
         * @param fileChallenge
         * @param notes 
         * @param student
         */
        public CachedFileInfo(Block block, Challenge fileChallenge, String notes, String student){
            this.block = block;
            this.myChallenge = fileChallenge;
            this.myNotes = notes;
            this.myStudent = student;
        }


        /**
         * @return the block for this file
         */
        public Block getBlock() {
            return block;
        }

        /**
         * @return the challenge for this file
         */
        public Challenge getChallenge() {
            return myChallenge;
        }
        
        /**
         * @return the notes for this file
         */
        public String getNotes(){
            return myNotes;
        }
        
        /**
         * @return the student for this file.
         */
        public String getStudent(){
            return myStudent;
        }
    }

    /**
     * Comparator which sorts Files by date modified, oldest to newest.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-06-17 20:08:35 $
     * @version $Revision: 1.3 $
     *
     */
    private class SortByDate implements Comparator<File>{
    
        public int compare(File o1, File o2) {
            if (o1.lastModified() < o2.lastModified()){
                return -1;
            } else if (o1.lastModified() > o2.lastModified()){
                return 1;
            } else{
                return 0;
            }
        }
    
    }

}
