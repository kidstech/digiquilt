package umm.digiquilt.view;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.view.glasspane.HandPaneHighlighted;
/**
 * The little trash can in the bottom right corner. Clears the patch in the hand
 * when clicked.
 *
 */
@SuppressWarnings("serial") //trash panel will not be serialized
public class TrashPanel extends JLabel implements HandPaneHighlighted{
    
    /**
     * Access to the current Patch.
     */
    PatchWorks patchWorks;
    
    /**
     * Create a Trash panel.
     * @param patchWorks Access to the current patch.
     */
    public TrashPanel(PatchWorks patchWorks) {
        this.setVisible(true);
        this.patchWorks = patchWorks;
        this.setPreferredSize(new Dimension(50, 80));
        ImageIcon trashIcon = new ImageIcon(
                getClass().getResource(
                "/umm/digiquilt/view/images/TrashCan48x48.png"));
        this.setIcon(trashIcon);
        this.setText("Trash");
        this.addMouseListener(new clearListener());
    }

    /**
     * Mouse listener for clicks. When fired, releases the current Patch from
     * PatchWorks.
     * 
     * @author biatekjt, last changed by $Author: biatekjt $
     * on $Date: 2009-05-28 19:19:53 $
     * @version $Revision: 1.1 $
     *
     */
    private class clearListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            patchWorks.releasePatch();
        }
    }

    public boolean isSnappy() {
        return false;
    }

    public boolean isHighlighted() {
        return true;
    }
}
