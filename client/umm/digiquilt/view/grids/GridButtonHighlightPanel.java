package umm.digiquilt.view.grids;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * This is the class that paints the highlights behind the
 * grid button panels.
 * 
 * @author deragonmr
 *
 */
@SuppressWarnings("serial")
public class GridButtonHighlightPanel extends JPanel{

    /**
     * The button number on the left to be highlighted. Will be the only
     * button highlighted on the left side. 
     */
    private int leftSide = 0;
    /**
     * The buttons on the right to be highlighted. Unlike the left side,
     * this will highlight all the buttons from the top up to and 
     * including this one.
     */
    private int rightSide = -1;

    /**
     * The assumed size of one button.
     */
    private static final int BUTTONSIZE = 32;
    /**
     * The assumed space between buttons.
     */
    private static final int OFFSET = 5;

    /**
     * Create a new GridButtonHighlightPanel.
     */
    public GridButtonHighlightPanel(){
        setVisible( true );
        setOpaque(false);
    }

    /**
     * Set the highlight. Buttons are numbered starting with 0 at the
     * top.
     * 
     * @param left highlighted button on the left side.
     * @param right number of buttons on the right side.
     */
    public void setHighlights(int left, int right){
        leftSide = left;
        // If right is less than zero, they probably want no highlighting
        // on the right at all. Otherwise, we should highlight all the buttons
        // on the right side or up to the button on the left side, whichever
        // is larger.
        rightSide = right<0? -1 : Math.max(leftSide + 1, right);
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphic){
        Graphics2D graphic2d = (Graphics2D)graphic;

        // sets the transparency.
        graphic2d.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, .33f));

        // sets the color to the one determined above.
        graphic2d.setColor(Color.CYAN);

        // Paint highlight on the left
        graphic2d.fillRect(0 ,
                leftSide*(BUTTONSIZE + OFFSET),
                BUTTONSIZE+(2*OFFSET),
                BUTTONSIZE+(2*OFFSET) );

        //Paint highlight on the right
        if (rightSide > 0){
            graphic2d.fillRect(BUTTONSIZE+(2*OFFSET),
                    0, 
                    BUTTONSIZE+2*OFFSET,
                    rightSide*(BUTTONSIZE+OFFSET)+OFFSET);
        }
    }



}
