/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.patchworkarea;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.TwoColorArrow;

/**
 * Workspace area at the bottom of the screen. Provides controls to rotate and
 * replace Patches.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         fortunan $ on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("serial")
//supressing the Serialized warning from JPanel
public class PatchWorkArea extends JLayeredPane {

    /**
     * Tooltip text for Right to Left replace buttons
     */
    private static final String REPLACE_R_TO_L = 
        "Replace the right patch with the left patch";
    /**
     * Tooltip text for Left to Right replace buttons
     */
    private static final String REPLACE_L_TO_R = 
        "Replace the left patch with the right patch";
    
    /**
     * Tooltip text for swap buttons
     */
    private static final String SWAP = "Swap these two patches";
    
    /**
     * The number of panels to create. If this is less than 1, bad
     * things will probably happen.
     */
    private static final int PANELS = 3;
    

    /**
     * The size of the arrow buttons.
     */
    private static final Dimension BUTTONSIZE = new Dimension(60,24);
    
    /**
     * The current Block
     */
    BlockWorks blockWorks;

    /**
     * Create a PatchWorkArea, complete with controls
     * @param patchWorks Access to the current Patch
     * @param blockWorks Access to the current Block
     * @param patchSize size of a patch, in pixels
     * @param rotater A RotationPanel, to show the rotation of patches
     */
    public PatchWorkArea(PatchWorks patchWorks, BlockWorks blockWorks,
            int patchSize) {

        this.blockWorks = blockWorks;
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(1, 1, 5, 5);

        JPanel workArea = new JPanel();
        RotationPanel rotater = new RotationPanel();
        
        workArea.setOpaque(false);
        workArea.setVisible(true);

        workArea.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Patch Work-Area");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.NORTH;
        workArea.add(label, constraints);
        
        TwoColorArrow leftArrow = new TwoColorArrow(
                BUTTONSIZE.width,
                BUTTONSIZE.height,
                true,
                false
                );
        
        TwoColorArrow bothArrow = new TwoColorArrow(
                BUTTONSIZE.width,
                BUTTONSIZE.height,
                true,
                true
                );
        
        TwoColorArrow rightArrow = new TwoColorArrow(
                BUTTONSIZE.width,
                BUTTONSIZE.height,
                false,
                true
                );
        
        PatchWorkPanel panel0 = 
            new PatchWorkPanel(patchWorks, patchSize, rotater);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 3;
        workArea.add(panel0, constraints);
        
        PatchWorkPanel prevPanel = panel0;
        for (int i=1; i<PANELS; i++){
            PatchWorkPanel nextPanel = 
                new PatchWorkPanel(patchWorks, patchSize, rotater);
            constraints.gridx = i*2;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.gridheight = 3;
            workArea.add(nextPanel, constraints);
            
            // Attach buttons between this one and the last one
            
            JButton replaceRtoL = new JButton(leftArrow);
            constraints.gridx = i*2-1;
            constraints.gridy = 3;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            replaceRtoL.setPreferredSize(BUTTONSIZE);
            replaceRtoL.addActionListener(
                    new ReplaceAction(nextPanel, prevPanel));
            replaceRtoL.setToolTipText(REPLACE_R_TO_L);
            workArea.add(replaceRtoL, constraints);

            JButton swapLandR = new JButton(bothArrow);
            constraints.gridx = i*2-1;
            constraints.gridy = 2;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            swapLandR.setPreferredSize(BUTTONSIZE);
            swapLandR.addActionListener(new SwapAction(prevPanel, nextPanel));
            swapLandR.setToolTipText(SWAP);
            workArea.add(swapLandR, constraints);

            JButton replaceLtoR = new JButton(rightArrow);
            constraints.gridx = i*2-1;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            replaceLtoR.setPreferredSize(BUTTONSIZE);
            replaceLtoR.addActionListener(
                    new ReplaceAction(prevPanel, nextPanel));
            replaceLtoR.setToolTipText(REPLACE_L_TO_R);
            workArea.add(replaceLtoR, constraints);
            
            prevPanel = nextPanel;
        }
        
        // Now we finally layer the work panel and the animation panel 
        
        this.setLayout(new GridBagLayout());
        
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.add(workArea, constraints);
        
        constraints.fill = GridBagConstraints.BOTH;
        this.add(rotater, constraints);
        
        this.setLayer(workArea, 0);
        this.setLayer(rotater, 1);
        
    }

    /**
     * Action to replace one Patch with another in the Block.
     *
     */
    private class ReplaceAction implements ActionListener {
        /**
         * The first PatchWorkPanel
         */
        PatchWorkPanel replaceThis;
        /**
         * The second PatchWorkPanel
         */
        PatchWorkPanel withThis;

        /**
         * Create a new replaceAction for these two PatchWorkPanels
         * 
         * @param replaceThis 
         * @param withThis
         */
        public ReplaceAction(PatchWorkPanel replaceThis, 
                PatchWorkPanel withThis) {
            this.replaceThis = replaceThis;
            this.withThis = withThis;
        }

        public void actionPerformed(final ActionEvent event) {
            Patch patchA = replaceThis.getPatchWorkViewer().getPatch();
            Patch patchB = withThis.getPatchWorkViewer().getPatch();
            blockWorks.replacePatchInBlock(patchA, patchB);
        }
    }

    /**
     * Action to swap two Patches in the Block
     *
     */
    private class SwapAction implements ActionListener {
        /**
         * One of the panels
         */
        PatchWorkPanel swapThis;
        /**
         * The other panel
         */
        PatchWorkPanel andThis;

        /**
         * Create a new action to swap the two patches in these panels
         * 
         * @param swapThis
         * @param andThis
         */
        public SwapAction(PatchWorkPanel swapThis, PatchWorkPanel andThis) {
            this.swapThis = swapThis;
            this.andThis = andThis;
        }

        public void actionPerformed(final ActionEvent event) {
            Patch patchA = swapThis.getPatchWorkViewer().getPatch();
            Patch patchB = andThis.getPatchWorkViewer().getPatch();
            blockWorks.swapPatchesInBlock(patchA, patchB);
        }
    }
}