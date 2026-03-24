package umm.digiquilt.view.grids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JButton;

import umm.digiquilt.model.Grid;
import umm.digiquilt.view.GridDisplayer;

/**
 * 
 * Button that displays an image of a Grid, as well as an optional
 * text label.
 * 
 * @author deragonmr
 *
 */
@SuppressWarnings("serial")
public class GridButton extends JButton implements GridDisplayer{

    /**
     * The text displayed by this button. Defaults to an empty string.
     */
    private String label = "";
    /**
     * The grid being held by this button, which is painted on the 
     * button's face.
     */
    private Grid lines = new Grid();
    
    /**
     * Boolean to keep track of whether or not this GridButton has had
     * a grid set yet, or if it's using the default grid. Is there a 
     * better way to do this?
     */
    private boolean hasGrid = false;
    /**
     * The default size of this button.
     */
    private Dimension size = new Dimension(32, 32);

    /**
     * Create a new GridButton with a default grid.
     */
    public GridButton(){
        this("");
    }
    
    /**Create a new GridButton with the specified label.
     * @param label
     */
    public GridButton(String label){
        this.label = label;
        this.setPreferredSize(size);
    }
    
    /**
     * Create a GridButton with the specified grid
     * @param gridlines
     */
    public GridButton(Grid gridlines){
        this("", gridlines);
    }
    
    /**Create a GridButton with the specified grid and label
     * @param label
     * @param gridlines
     */
    public GridButton(String label, Grid gridlines){
        this.lines = gridlines;
        this.label = label;
        hasGrid = true;
        this.setPreferredSize(size);
        this.setMinimumSize(size);
    }
    
    /**
     * Set the grid that this button holds and shows.
     * @param gridLines
     */
    public void setGrid(Grid gridLines){
        lines = gridLines;
        hasGrid = true;
        repaint();
    }

    /**
     * @return The grid being held by this button.
     */
    public Grid getGrid(){
        return lines;
    }
    
    /**
     * @return Boolean signifying whether this button has had a grid
     * set to it, either using the constructor or using setGrid().
     */
    public boolean hasGrid(){
        return hasGrid;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getSize().width,getSize().height);

        // draw the Grid
        g2d.setColor(Color.GRAY);

        for(Line2D.Double line:lines){

            int x1 = (int)(getSize().width * line.getX1() );
            int y1 = (int)(getSize().height * line.getY1() );
            int x2 = (int)(getSize().width * line.getX2() );
            int y2 = (int)(getSize().height * line.getY2() );			

            g2d.drawLine(x1,y1,x2,y2);

        }

        g2d.setColor(Color.BLACK);
        
        // assumes characters are about 7 pixels wide...
        int numOffset = (getSize().width - (7 * label.length())) /2;
        // and 9 pixels tall...
        g2d.drawString(label, numOffset,(getSize().height + 9)/2);

    }

}
