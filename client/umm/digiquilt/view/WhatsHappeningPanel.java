/*
 * Created by jbiatek on Sep 10, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import umm.digiquilt.control.LoadBlockAction;
import umm.digiquilt.model.Block;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.savehandler.SyncListener;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.filebrowser.MissingIcon;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

@SuppressWarnings("serial")
public class WhatsHappeningPanel extends JPanel implements SyncListener {


    /**
     * The size to make the BlockViewers
     */
    private static final int BLOCK_SIZE = 48;
    /**
     * A list of all the BlockViewers that we're showing, in order
     * from left to right
     */
    private List<Component> showing = new ArrayList<Component>();
    /**
     * The GridBagConstraints, pre-set up for adding viewers
     */
    private GridBagConstraints constraints = new GridBagConstraints();
    /**
     * The number of recent quilts to show.
     */
    private int numToShow;
    
    private BlockWorks blockWorks;
    
    
    private SaveHandler handler;
    
    private ComboBoxModel challenges;

    /**
     * Create a new What's Happening Panel. 
     * @param saveHandler 
     * @param grid 
     * @param blockWorks 
     * @param challenges 
     * 
     * @param numToShow The number of recent quilts to show
     */
    public WhatsHappeningPanel(BlockWorks blockWorks,  
            SaveHandler saveHandler, ComboBoxModel challenges, 
            int numToShow){
        this.blockWorks = blockWorks;
        this.handler = saveHandler;
        this.challenges = challenges;
        this.numToShow = numToShow;
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        constraints.gridy = 0;
        constraints.insets = new Insets(3,1,3,1);
    }

    public void onSynchronize(SaveHandler handler) {
        File classDir = handler.getSaveDirectory();
        File[] xmls = classDir.listFiles(new FilenameFilter(){

            public boolean accept(File dir, String name) {
                return name.endsWith(SaveHandler.QUILT_EXT);
            }

        });

        Arrays.sort(xmls, new SortByDate());

        List<File> needToGet = new ArrayList<File>();
        List<String> showingNames = new ArrayList<String>();
        for (Component viewer : showing){
            showingNames.add(viewer.getName());
        }

        int startingPoint = Math.max(0, xmls.length-numToShow);
        for (int i=startingPoint; i<xmls.length; i++){
            if (i<0){
                continue;
            }
            if (!showingNames.contains(xmls[i].getName())){
                needToGet.add(xmls[i]);
            }
        }

        // Remove old ones to make space
        if (needToGet.size() + showing.size() > numToShow){
            for (int i=0; i<needToGet.size(); i++){
                if (showing.size() > 0){
                    this.remove(showing.get(0));
                    showing.remove(0);
                }
            }
        }
        // Create the new ones
        for (File xmlFile : needToGet){
            Component viewer = makeViewer(xmlFile);
            this.add(viewer, constraints);
            showing.add(viewer);
        }
        revalidate();
    }

    /**
     * Load a Block and informatino from XML, and return the BlockViewer.
     * 
     * @param xmlFile
     * @return the BlockViewer
     */
    private Component makeViewer(File xmlFile){
        try {
            LoadXML loader = new LoadXML(new FileInputStream(xmlFile));
            Block loadedBlock = loader.getCurrentBlock();
            int patchSize = BLOCK_SIZE / loadedBlock.getSideSize();
            BlockViewer viewer = new BlockViewer(loadedBlock, patchSize);
            String tooltip = "<html>";
            tooltip += loader.getStudent()+" - "+loader.getBlockName();
            tooltip += "<br>Challenge: "+loader.getChallenge();
            tooltip += "<br>Notes: "+loader.getNotes();
            viewer.setToolTipText(tooltip);
            viewer.setName(xmlFile.getName());
            
            final LoadBlockAction loadAction = new LoadBlockAction(
                    blockWorks, handler, challenges, xmlFile);
            
            viewer.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    loadAction.showOpenDialog();
                }
                
            });
            
            return viewer;

        } catch (Exception e) {
            e.printStackTrace();
        } 

        return new JLabel(new MissingIcon(BLOCK_SIZE, BLOCK_SIZE));
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
