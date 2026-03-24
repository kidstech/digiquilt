/*
 * Created by jbiatek on Mar 15, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.control;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLayeredPane;

import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.BlockWorkArea;
import umm.digiquilt.view.DigiQuiltFrame;
import umm.digiquilt.view.GridViewPanel;
import umm.digiquilt.view.blockAnimation.BlockSorter;
import umm.digiquilt.view.glasspane.HandGlassPane;
import umm.digiquilt.view.glasspane.LockingGlassPane;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class MainFrameAnimationController implements ActionListener,
            PropertyChangeListener{

    DigiQuiltFrame frame;
    BlockWorkArea blockWorkArea;
    int patchSize;
    BlockWorks blockWorks;
    LockingGlassPane lockGlassPane;
    HandGlassPane handGlassPane;
    AbstractButton button;
    
    BlockSorter animatedViewer;

    /**
     * @param frame
     * @param blockViewer
     * @param blockWorkArea
     * @param blockWorks
     * @param grid
     * @param lockGlassPane
     * @param handGlassPane
     * @param patchSize
     */
    public MainFrameAnimationController(
            DigiQuiltFrame frame,
            BlockWorkArea blockWorkArea,
            BlockWorks blockWorks,
            LockingGlassPane lockGlassPane,
            HandGlassPane handGlassPane,
            AbstractButton button,
            int patchSize){
        this.frame = frame;
        this.blockWorkArea = blockWorkArea;
        this.blockWorks = blockWorks;
        this.lockGlassPane = lockGlassPane;
        this.handGlassPane = handGlassPane;
        this.button = button;
        this.patchSize = patchSize;
        
        this.button.addActionListener(this);
    }
    
    /**
     * Keeps track of whether or not the locked pane is on or not.
     */
    boolean locked = false;

    public void actionPerformed(ActionEvent e) {
        if (locked){
            animatedViewer.restore();
            button.setText("Sort");
            button.setEnabled(false);
        } else {
            animatedViewer = new BlockSorter(
                    blockWorks.getCurrentBlockClone(), 
                    blockWorks.getGrid(), 
                    patchSize);
            animatedViewer.addPropertyChangeListener(this);
            blockWorks.addPropertyChangeListener("grid", animatedViewer);
            
            // Disable the "real" block viewer
            blockWorkArea.setBlockVisible(false);
            // Add the animated viewer in its place
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            blockWorkArea.add(animatedViewer, constraints);
            blockWorkArea.setLayer(animatedViewer, 0);
            animatedViewer.setVisible(true);
            animatedViewer.setOpaque(false);
            
            button.setText("Restore");
            lockGlassPane.unlockComponent(button);
            
            frame.setGlassPane(lockGlassPane);
            lockGlassPane.setVisible(true);
            locked = true;
            
            frame.validate();
            
            animatedViewer.sort();
        }
        
    }
    

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == null || evt.getOldValue() == null){
            return;
        }
        
        if (evt.getNewValue().equals("stopped") 
                && evt.getOldValue().equals("reverse")){
            // Restore to the default, unlocked state
            blockWorkArea.remove(animatedViewer);
            blockWorkArea.setBlockVisible(true);
            
            button.setEnabled(true);

            lockGlassPane.setVisible(false);
            frame.setGlassPane(handGlassPane);
            
            frame.validate();
            locked = false;
        }
    }

    
}
