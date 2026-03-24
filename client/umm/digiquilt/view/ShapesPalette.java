/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import umm.digiquilt.control.patches.ShapeMouseListener;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.Shape;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.PatchViewer;
import umm.digiquilt.view.fabriccontrols.FabricListener;

/**
 * A panel containing the basic shapes that can be used to make a quilt. The shapes
 * are whatever color is currently selected.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         fortunan $ on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 */
@SuppressWarnings("serial")
// supressing the Serialized warning from JPanel
public class ShapesPalette extends JPanel implements FabricListener{
    
    /**
     * The fabric to start out with.
     */
    private static final Fabric DEFAULTFABRIC = Fabric.REDVIOLET;
    
    /**
     * PatchViewer for the full sized square shape
     */
    private final PatchViewer fullSquareView;

    /**
     * PatchViewer for the large triangle shape
     */
    private final PatchViewer halfTriangleView;
    
    /**
     * PatchViewer for the rectangle shape
     */
    private final PatchViewer halfRectView;
    
    /**
     * PatchViewer for the small triangle shape
     */
    private final PatchViewer quarterTriangleView;
    
    /**
     * PatchViewer for the small square shape
     */
    private final PatchViewer quarterSquareView;
    
    /**
     * The size that a patch should be, in pixels.
     */
    private int patchSize;
    
    /**
     * Button to toggle the smaller shapes on and off
     */
    
    private final JButton toggleButton;
    /**
     * Makes a shapesPalette
     * 
     * @param patchWorks 
     * @param patchSize input size for Patches
     */
    public ShapesPalette(PatchWorks patchWorks, int patchSize) {
        this.patchSize = patchSize;
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.insets = new Insets(3, 3, 3, 3);
        
        
        //Color BACKGROUND = new Color(203, 204, 102);
        
        //this.setBackground(BACKGROUND);
        this.setVisible(true);
        this.setOpaque(false);
        JLabel label = new JLabel("Shapes Selection");
        this.add(label, constraints);
        
        toggleButton = new JButton("Show All");
        toggleButton.addActionListener(new ToggleShapesAction());
        this.add(toggleButton, constraints);
        
        Dimension fullPatchSize = new Dimension(patchSize, patchSize);
        
        Dimension halfPatchSize = new Dimension(patchSize,patchSize/2);
        
        Dimension quarterPatchSize = new Dimension(patchSize/2, patchSize/2);
        
        //Adds shapes
        fullSquareView = createShapeViewer(patchWorks);
        fullSquareView.setPreferredSize(fullPatchSize);
        fullSquareView.setPatch(Shape.FULLSQUARE.getPatch(DEFAULTFABRIC));
        this.add(fullSquareView, constraints);

        halfTriangleView = createShapeViewer(patchWorks);
        halfTriangleView.setPreferredSize(fullPatchSize);
        halfTriangleView.setPatch(Shape.HALFTRIANGLE.getPatch(DEFAULTFABRIC));
        this.add(halfTriangleView, constraints);
        
        halfRectView = createShapeViewer(patchWorks);
        halfRectView.setPreferredSize(halfPatchSize);
        halfRectView.setPatch(Shape.HALFRECTANGLE.getPatch(DEFAULTFABRIC));
        halfRectView.setVisible(false);
        this.add(halfRectView, constraints);
        
        quarterTriangleView = createShapeViewer(patchWorks);
        quarterTriangleView.setPreferredSize(halfPatchSize);
        quarterTriangleView.setPatch(Shape.QUARTERTRIANGLE.getPatch(DEFAULTFABRIC));
        quarterTriangleView.setVisible(false);
        this.add(quarterTriangleView, constraints);
        
        quarterSquareView = createShapeViewer(patchWorks);
        quarterSquareView.setPreferredSize(quarterPatchSize);
        quarterSquareView.setPatch(Shape.QUARTERSQUARE.getPatch(DEFAULTFABRIC));
        quarterSquareView.setVisible(false);
        this.add(quarterSquareView, constraints);
        
        // Add an invisible panel at the end which will take up any extra
        // space.
        JPanel end = new JPanel();
        end.setPreferredSize(new Dimension(0,0));
        constraints.weighty = 1;
        this.add(end, constraints);
       
    }
    
    /**Creates PatchViewers with the correct parameters for viewing shapes.
     * Also attaches a ShapeMouseListener using the given PatchWorks.
     * 
     * @param patchWorks
     * @return an empty PatchViewer configured for shape viewing.
     */
    private PatchViewer createShapeViewer(PatchWorks patchWorks){
        PatchViewer newPatch = new PatchViewer(new Patch(), patchSize);
        newPatch.addMouseListener(new ShapeMouseListener(newPatch, patchWorks));
        newPatch.setOpaque(false);
        newPatch.setHighlightEnabled(false);
        newPatch.setSnappiness(false);
        return newPatch;
    }
    
    /**
     * Toggles the "extra" shapes on and off, and changes
     * the text of the toggle button.
     * 
     * @author jbiatek
     *
     */
    private class ToggleShapesAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (halfRectView.isVisible()){
                halfRectView.setVisible(false);
                quarterTriangleView.setVisible(false);
                quarterSquareView.setVisible(false);
                toggleButton.setText("Show All");
            } else {
                halfRectView.setVisible(true);
                quarterTriangleView.setVisible(true);
                quarterSquareView.setVisible(true);
                toggleButton.setText("Basic");                
            }            
        }
    }

    public void newFabricSelected(Fabric newFabric) {
        fullSquareView.setPatch(Shape.FULLSQUARE.getPatch(newFabric));
        halfTriangleView.setPatch(Shape.HALFTRIANGLE.getPatch(newFabric));
        halfRectView.setPatch(Shape.HALFRECTANGLE.getPatch(newFabric));
        quarterTriangleView.setPatch(Shape.QUARTERTRIANGLE.getPatch(newFabric));
        quarterSquareView.setPatch(Shape.QUARTERSQUARE.getPatch(newFabric));
    }
}