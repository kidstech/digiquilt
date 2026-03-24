package umm.digiquilt.view.patchworkarea;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import umm.digiquilt.control.patches.PatchWorkMouseListener;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.PatchViewer;
/**
 * Panel that holds a PatchWorkViewer, as well as buttons for rotating and
 * clearing.
 */
@SuppressWarnings("serial") //will not be serialized
public class PatchWorkPanel extends JPanel{

    /**
     * The PatchWorkViewer that this Panel holds.
     */
    private final PatchViewer patchWorkViewer;

    /**
     * A panel which shows rotation of patches with a neat animation.
     */
    private final RotationPanel rotater;
    
    /**
     * The size of a Patch, in pixels.
     */
    private int patchSize;
    /**
     * Create a new PatchWorkPanel.
     * @param patchWorks Access to the current Patch.
     * @param patchSize size of a patch, in pixels
     * @param rotater A RotationPanel to show animation of rotations.
     */
    public PatchWorkPanel(PatchWorks patchWorks, int patchSize, RotationPanel rotater) {
        this.patchSize = patchSize;
        this.rotater = rotater;
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        this.setOpaque(false);
        this.setVisible(true);
        this.setLayout(gridbag);

        ImageIcon cwIcon = new ImageIcon(
                getClass().getResource(
                "/umm/digiquilt/view/images/cw.gif"));
        ImageIcon ccwIcon = new ImageIcon(
                getClass().getResource(
                "/umm/digiquilt/view/images/ccw.gif"));
        ImageIcon smallTrash = new ImageIcon(
                getClass().getResource(
                "/umm/digiquilt/view/images/TrashCan32x32.png"));

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

        JButton rotateCCW = new JButton(ccwIcon);
        rotateCCW.setPreferredSize(new Dimension(ccwIcon.getIconWidth(), ccwIcon.getIconHeight()));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        gridbag.setConstraints(rotateCCW, constraints);
        rotateCCW.addActionListener(new RotateCCWAction());
        rotateCCW.setToolTipText("Rotate counter clockwise");
        add(rotateCCW);
        
        JButton clear = new JButton(smallTrash);
        clear.setPreferredSize(new Dimension(smallTrash.getIconWidth(),smallTrash.getIconHeight()));
        constraints.gridx = 1;
        constraints.gridy = 1;
        gridbag.setConstraints(clear, constraints);
        clear.addActionListener(new ClearAction());
        clear.setToolTipText("Clear patch");
        add(clear);

        JButton rotateCW = new JButton(cwIcon);
        rotateCW.setPreferredSize(new Dimension(cwIcon.getIconWidth(), cwIcon.getIconHeight()));
        constraints.gridx = 2;
        constraints.gridy = 1;
        gridbag.setConstraints(rotateCW, constraints);
        rotateCW.addActionListener(new RotateCWAction());
        rotateCW.setToolTipText("Rotate clockwise");
        add(rotateCW);

        
    }

    /**
     * Action that tells the patch to rotate clockwise on click.
     *
     */
    private class RotateCWAction implements ActionListener {
        public void actionPerformed(final ActionEvent event) {
            Image image = PatchViewer.generateImage(
                    patchWorkViewer.getPatch(), patchSize, false);
            Point patchLocation = patchWorkViewer.getLocation();
            Point relative = SwingUtilities.convertPoint(
                    patchWorkViewer.getParent(), patchLocation, rotater);
            Point centered = new Point(
                    relative.x+patchWorkViewer.getWidth()/2, 
                    relative.y+patchWorkViewer.getHeight()/2);
            rotater.rotateClockwise(image, centered);
            patchWorkViewer.getPatch().rotateCW();
            patchWorkViewer.refreshImage();
        }
    }

    /**
     * Action that tells the patch to rotate counterclockwise on click.
     *
     */
    private class RotateCCWAction implements ActionListener {
        public void actionPerformed(final ActionEvent event) {
            Image image = PatchViewer.generateImage(
                    patchWorkViewer.getPatch(), patchSize, false);
            Point patchLocation = patchWorkViewer.getLocation();
            Point relative = SwingUtilities.convertPoint(
                    patchWorkViewer.getParent(), patchLocation, rotater);
            Point centered = new Point(
                    relative.x+patchWorkViewer.getWidth()/2, 
                    relative.y+patchWorkViewer.getHeight()/2);
            rotater.rotateCounterClockwise(image, centered);
            patchWorkViewer.getPatch().rotateCCW();
            patchWorkViewer.refreshImage();
        }
    }

    /**
     * Action to clear the PatchWorkViewer.
     *
     */
    private class ClearAction implements ActionListener {
        public void actionPerformed(final ActionEvent event) {
                patchWorkViewer.setPatch(new Patch());
                patchWorkViewer.refreshImage();
        }
    }

    /**
     * @return Returns the patchWorkViewer.
     */
    public PatchViewer getPatchWorkViewer() {
        return patchWorkViewer;
    }
}
