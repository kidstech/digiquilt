package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import umm.digiquilt.control.LoadPatchAction;
import umm.digiquilt.control.SavePatchAction;
import umm.digiquilt.control.patches.PatchWorkMouseListener;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.PatchViewer;
/**
 * Panel For loading patches into the Digiquilt Client
 */
@SuppressWarnings("serial") //will not be serialized
public class LoadPatchPanel extends JPanel{

    /**
     * The PatchWorkViewer that this Panel holds.
     */
    private final PatchViewer patchWorkViewer;
    
    /**
     * Create a new PatchWorkPanel.
     * @param patchWorks Access to the current Patch.
     * @param patchSize size of a patch, in pixels
     * @param rotater A RotationPanel to show animation of rotations.
     */
    public LoadPatchPanel(PatchWorks patchWorks, int patchSize, SaveHandler handler) {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        this.setOpaque(false);
        this.setVisible(true);
        this.setLayout(gridbag);


        patchWorkViewer = new PatchViewer(new Patch(), patchSize);
        patchWorkViewer.setPreferredSize(new Dimension(patchSize, patchSize));
        patchWorkViewer.addMouseListener(
                new PatchWorkMouseListener(patchWorkViewer, patchWorks));
        patchWorkViewer.setShadowed(true);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.CENTER;
        gridbag.setConstraints(patchWorkViewer, constraints);
        constraints.insets = new Insets(0, 0, 2, 2);
        add(patchWorkViewer);
        
        JButton openButton = new JButton("Open Patch");
        constraints.gridx = 1;
        constraints.gridy = 1;
        gridbag.setConstraints(openButton, constraints);
        openButton.addActionListener(new LoadPatchAction(patchWorkViewer, handler));
        openButton.setToolTipText("Open Patch");
        add(openButton);
        
        JButton saveButton = new JButton("Save Patch");
        constraints.gridx = 1;
        constraints.gridy = 2;
        gridbag.setConstraints(saveButton, constraints);
        saveButton.addActionListener(new SavePatchAction(patchWorkViewer, handler));
        saveButton.setToolTipText("Save Patch");
        add(saveButton);

        
    }

    /**
     * @return Returns the patchWorkViewer.
     */
    public PatchViewer getPatchWorkViewer() {
        return patchWorkViewer;
    }
}

