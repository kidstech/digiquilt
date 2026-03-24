package umm.digiquilt.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import umm.digiquilt.model.Grid;

/**
 * Layer showing the lines of the grid.
 * 
 * @author deragonmr
 *
 */
@SuppressWarnings("serial")
public class GridViewPanel extends JPanel implements GridDisplayer {
    /**
     * The grid currently being shown.
     */
    private Grid gridLines;
    
    /**
     * Our current preferred size.
     */
    private Dimension mySize;

    /**
     * Create a new GridViewPanel for this number of PatchViewers.
     * 
     * @param sideSize the number of patches to a side
     * @param patchSize The size of a patch, in pixels
     */
    public GridViewPanel(int sideSize, int patchSize){
        mySize = new Dimension(sideSize*patchSize,sideSize*patchSize);
        gridLines = new Grid(sideSize, sideSize, 0, 0);
        this.setPreferredSize(mySize);
        this.setMinimumSize(mySize);
        this.setOpaque(false);
        this.setVisible(true);
    }


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        Dimension size = this.getSize();
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.WHITE);

        for(Line2D.Double line : this.gridLines ){
            double x1 = (size.width * line.getX1() );
            double y1 = (size.height * line.getY1() );
            double x2 = (size.width * line.getX2() );
            double y2 = (size.height * line.getY2());

            g2.drawLine((int)x1 ,(int)y1 ,(int)x2 ,(int)y2);
        }

        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.DARK_GRAY);

        for(Line2D.Double line : this.gridLines ){
            double x1 = (size.width * line.getX1() );
            double y1 = (size.height * line.getY1() );
            double x2 = (size.width * line.getX2() );
            double y2 = (size.height * line.getY2());

            g2.drawLine((int)x1 ,(int)y1 ,(int)x2 ,(int)y2);
        }
    }

    /**Set new Grid for this viewer. Will cause a repaint().
     * 
     * @param newLines
     */
    public void setGrid(Grid newLines) {
        gridLines = newLines;
        repaint();
    }
   

}
