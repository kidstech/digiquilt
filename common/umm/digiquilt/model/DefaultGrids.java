package umm.digiquilt.model;

import java.awt.geom.Line2D;

/**
 * Static methods to get the predefined Grid used by the Select A
 * Grid panel.
 * 
 * @author biatekjt, last changed by $Author: biatekjt $
 * on $Date: 2009-07-01 23:35:06 $
 * @version $Revision: 1.2 $
 *
 */
public enum DefaultGrids {

    /**
     * Grid divisions for a 2x2 Block.
     */
    TWO( 2,
            new GridDivision("2",
                    new Grid(0,2,0,0),
                    new Grid(2,0,0,0),
                    new Grid(0,0,0,2),
                    new Grid(0,0,2,0)
            ),
            new GridDivision("4",
                    new Grid(2,2,0,0),
                    new Grid(0,0,2,2),
                    new Grid(0,4,0,0),
                    new Grid(4,0,0,0)
            ),
            new GridDivision("8",
                    new Grid(2,2,2,2),
                    new Grid(2,2,0,0, 
                            new Line2D.Double[]{
                            new Line2D.Double(0.0, 0.5, 0.5, 0.0),
                            new Line2D.Double(0.5, 0.0, 1.0, 0.5),
                            new Line2D.Double(1.0, 0.5, 0.5, 1.0),
                            new Line2D.Double(0.5, 1.0, 0.0, 0.5)
                    }),
                    new Grid(2,4,0,0),
                    new Grid(4,2,0,0)
            ),
            new GridDivision("16",
                    new Grid(4,4,0,0),
                    new Grid(2,2,4,4)
            ),
            new GridDivision("32", 
                    new Grid(4,4,4,4),
                    new Grid(4,4,0,0, 
                            new Line2D.Double[]{
                            // add the short "caps" to the longer "X".
                            new Line2D.Double(0.0, 0.25, 0.25, 0.0),
                            new Line2D.Double(0.75, 0.0, 1.0, 0.25),
                            new Line2D.Double(1.0, 0.75, 0.75, 1.0),
                            new Line2D.Double(0.25, 1.0, 0.0, 0.75),
                            // add the long sides of the "X".
                            new Line2D.Double(0.25, 0.0, 1.0, 0.75),
                            new Line2D.Double(0.0, 0.25, 0.75, 1.0),
                            new Line2D.Double(0.75, 0.0, 0.0, 0.75),
                            new Line2D.Double(1.0, 0.25, 0.25,1.0)
                    }),

                    new Grid(4,4,0,0,
                            new Line2D.Double[]{
                            // add the short segments to the outer diamond
                            new Line2D.Double(0.0, 0.25, 0.25, 0.0),
                            new Line2D.Double(0.75, 0.0, 1.0, 0.25),
                            new Line2D.Double(1.0, 0.75, 0.75, 1.0),
                            new Line2D.Double(0.25, 1.0, 0.0, 0.75),
                            // add the long sides of the middle diamand
                            new Line2D.Double(0.0, 0.5, 0.5, 0.0),
                            new Line2D.Double(0.5, 0.0, 1.0, 0.5),
                            new Line2D.Double(1.0, 0.5, 0.5, 1.0),
                            new Line2D.Double(0.5, 1.0, 0.0, 0.5),
                            // add the short sides to the center diamond.
                            new Line2D.Double(0.25, 0.5, 0.5, 0.25),
                            new Line2D.Double(0.5, 0.25, 0.75, 0.5),
                            new Line2D.Double(0.75, 0.5, 0.5, 0.75),
                            new Line2D.Double(0.5, 0.75, 0.25, 0.5),
                    }),
                    new Grid(4,4,8,0),
                    new Grid(4,4,0,8)
            ),
            new GridDivision("64",
                    new Grid(4,4,8,8)
            )
    ),

    /**
     * Grid divisions for a 3x3 Block.
     */
    THREE( 3,
            new GridDivision("2",
                    new Grid(0,2,0,0),
                    new Grid(2,0,0,0),
                    new Grid(0,0,0,2),
                    new Grid(0,0,2,0)
            ),
            new GridDivision("3",
                    new Grid(3,0,0,0),
                    new Grid(0,3,0,0)
            ),
            new GridDivision("4",
                    new Grid(2,2,0,0),
                    new Grid(0,0,2,2)
            ),            
            new GridDivision("8",
                    new Grid(2,2,2,2),
                    new Grid(2,2,0,0,
                            new Line2D.Double[]{
                            new Line2D.Double(0.5, 0, 1, 0.5),
                            new Line2D.Double(1, 0.5, 0.5, 1),
                            new Line2D.Double(0.5, 1, 0, 0.5),
                            new Line2D.Double(0, 0.5, 0.5, 0)
                    })
                            
            ),
            new GridDivision("18",
                    new Grid(3,3,6,0),
                    new Grid(3,3,0,6),
                    new Grid(3,6,0,0),
                    new Grid(6,3,0,0)
            ),
            new GridDivision("36",
                    new Grid(3,3,6,6),
                    new Grid(6,6,0,0)
            ),
            // 48????
            new GridDivision("72",
                    new Grid(6,6,6,6)
            ),
            new GridDivision("144", 
                    new Grid(6,6,12,12)
            )
    ),

    /**
     * Grid divisions for a 4x4 Block.
     */
    FOUR( 4,
            new GridDivision("2",
                    new Grid(0,2,0,0),
                    new Grid(2,0,0,0),
                    new Grid(0,0,0,2),
                    new Grid(0,0,2,0)
            ),
            new GridDivision("4",
                    new Grid(2,2,0,0),
                    new Grid(0,0,2,2),
                    new Grid(0,4,0,0),
                    new Grid(4,0,0,0)
            ),
            new GridDivision("8",
                    new Grid(2,2,2,2),
                    new Grid(2,2,0,0, 
                            new Line2D.Double[]{
                            new Line2D.Double(0.0, 0.5, 0.5, 0.0),
                            new Line2D.Double(0.5, 0.0, 1.0, 0.5),
                            new Line2D.Double(1.0, 0.5, 0.5, 1.0),
                            new Line2D.Double(0.5, 1.0, 0.0, 0.5)
                    }),
                    new Grid(2,4,0,0),
                    new Grid(4,2,0,0),
                    new Grid(0,8,0,0),
                    new Grid(8,0,0,0)
            ),
            new GridDivision("16",
                    new Grid(4,4,0,0),
                    new Grid(2,2,4,4),
                    new Grid(2,8,0,0),
                    new Grid(8,2,0,0)
            ),
            new GridDivision("32", 
                    new Grid(4,4,4,4),
                    new Grid(4,4,0,0, 
                            new Line2D.Double[]{
                            // add the short "caps" to the longer "X".
                            new Line2D.Double(0.0, 0.25, 0.25, 0.0),
                            new Line2D.Double(0.75, 0.0, 1.0, 0.25),
                            new Line2D.Double(1.0, 0.75, 0.75, 1.0),
                            new Line2D.Double(0.25, 1.0, 0.0, 0.75),
                            // add the long sides of the "X".
                            new Line2D.Double(0.25, 0.0, 1.0, 0.75),
                            new Line2D.Double(0.0, 0.25, 0.75, 1.0),
                            new Line2D.Double(0.75, 0.0, 0.0, 0.75),
                            new Line2D.Double(1.0, 0.25, 0.25,1.0)
                    }),

                    new Grid(4,4,0,0,
                            new Line2D.Double[]{
                            // add the short segments to the outer diamond
                            new Line2D.Double(0.0, 0.25, 0.25, 0.0),
                            new Line2D.Double(0.75, 0.0, 1.0, 0.25),
                            new Line2D.Double(1.0, 0.75, 0.75, 1.0),
                            new Line2D.Double(0.25, 1.0, 0.0, 0.75),
                            // add the long sides of the middle diamand
                            new Line2D.Double(0.0, 0.5, 0.5, 0.0),
                            new Line2D.Double(0.5, 0.0, 1.0, 0.5),
                            new Line2D.Double(1.0, 0.5, 0.5, 1.0),
                            new Line2D.Double(0.5, 1.0, 0.0, 0.5),
                            // add the short sides to the center diamond.
                            new Line2D.Double(0.25, 0.5, 0.5, 0.25),
                            new Line2D.Double(0.5, 0.25, 0.75, 0.5),
                            new Line2D.Double(0.75, 0.5, 0.5, 0.75),
                            new Line2D.Double(0.5, 0.75, 0.25, 0.5),
                    }),
                    new Grid(4,4,8,0),
                    new Grid(4,4,0,8),
                    new Grid(4,8,0,0),
                    new Grid(8,4,0,0)
            ),
            new GridDivision("64", 
                    new Grid(8,8,0,0),
                    new Grid(4,4,8,8)
            ),
            new GridDivision("128",
                    new Grid(8,8,8,8)                    
            ),
            new GridDivision("256", 
                    new Grid(8,8,16,16)
            )
    );

    /**
     * Array of GridDivisions for this block size.
     */
    private GridDivision[] divisions;

    /**
     * Indicates which block size this DefaultGrid is meant for.
     */
    private int blockSize;

    /**
     * Construct a DefaultGrids item for a certain block size and containing
     * a number of divisions.
     * 
     * @param blockSize
     * @param divisions
     */
    DefaultGrids(int blockSize, GridDivision... divisions ){
        this.divisions = divisions;
        this.blockSize = blockSize;
    }

    /**
     * @return the divisions specified for this block size.
     */
    public GridDivision[] getDivisions(){
        return divisions;
    }

    /**
     * @return an int indicating what block size this DefaultGrids is meant for.
     */
    public int getBlockSize(){
        return blockSize;
    }

    /**
     * A wrapper class to encapsulate a set of Grids under a certain label. This
     * gets used to create the grid selection panel. It contains text for the pop
     * up button, and the grids that will be available on the right hand side after 
     * that pop up button is clicked.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-01 23:35:06 $
     * @version $Revision: 1.2 $
     *
     */
    public static class GridDivision {
        /**
         * The text for this grid division.
         */
        String text;
        /**
         * The grids contained in this division.
         */
        Grid[] grids;

        /**
         * Create a new GridDivision, with the given text and Grids
         * contained in it.
         * 
         * @param text
         * @param grids
         */
        public GridDivision(String text, Grid... grids){
            this.text = text;
            this.grids = grids;
        }

        /**
         * @return the text for this GridDivision
         */
        public String getText(){
            return text;
        }

        /**
         * @return the grids in this GridDivision
         */
        public Grid[] getGrids(){
            return grids;
        }
    }
}
