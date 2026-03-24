package umm.digiquilt.view.blockAnimation;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import umm.digiquilt.animation.AnimationPanel;
import umm.digiquilt.model.Block;
import umm.digiquilt.model.Grid;
import umm.digiquilt.view.Tile;

/**A panel that displays a Block, and can be animated to show the tiles
 * of the block moving into position to show how much of each color there
 * is.
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-15 20:25:34 $
 * @version $Revision: 1.2 $
 *
 */
@SuppressWarnings("serial")
public class BlockSorter extends AnimationPanel implements PropertyChangeListener{


    /**
     * The number of Patches per side, i.e. if the block being animated
     * is 4 by 4, the blockSize is 4.
     */
    int sideSize;
    
    /**
     * The total number of Patches in the Block.
     */
    int blockSize;
    
    /**
     * The size of one Patch in the block
     */
    int patchSize;

    /**
     * The original Tiles, which are sorted by color but contain their original
     * information about location.
     */
    private List<Tile> originalTiles;

    /**
     * A list containing tile indices after analyzeGrid() is done. They conform
     * to the Grid instead of to the regular block order.
     */
    private List<Integer> gridIndexOrder;

    /**Create a new AnimatedBlock showing the given Block.
     * 
     * @param block
     * @param grid 
     * @param patchSize in pixels
     */
    public BlockSorter(Block block, Grid grid, int patchSize){
        this.sideSize = block.getSideSize();
        this.blockSize = block.getSize();
        this.patchSize = patchSize;
        
        Dimension size = new Dimension(sideSize * patchSize,
                sideSize * patchSize);
        
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        
        originalTiles = Tile.getTilesFromBlock(block);
        Collections.sort(originalTiles, new SortByCoverage(block));
        
        analyzeGrid(grid, Tile.getTilesFromBlock(block));
        
        
        // Now create the initial moving tiles using the grid order that
        // analyzeGrid() figured out
        for (int i=0; i<originalTiles.size(); i++){
            Tile unsorted = originalTiles.get(i);
            Tile sorted = new Tile(
                    gridIndexOrder.get(i), unsorted.getFabric(),
                    blockSize);

            addSprite(new MovingTile(unsorted, sorted, patchSize));
        }
        
    }
    
    /**
     * Looks at the given Grid and produces an order for tiles to be laid out
     * which will conform to the sections of that Grid. For a Grid with no lines,
     * that would just be 0, 1, 2, 3, 4, .... but if a Grid has, say, lots of tall
     * vertical sections, a better order may be 0, 1, 2, 3, 8, 9, 10, 11... i.e.
     * filling up the tiles contained in the first section before moving on
     * to the next one instead of going Patch by Patch.
     * 
     * This gets a good order by creating a TileChunk containing all the tiles, and
     * then slicing each chunk with each line in the Grid. The result is a list of
     * TileChunks which correspond with sections in the Grid. These chunks are then
     * sorted and flattened into tileOrder, so the tileOrder goes section by section.
     * 
     * @param grid
     * @param initialTiles 
     */
    private void analyzeGrid(Grid grid, List<Tile> initialTiles){
        gridIndexOrder = new ArrayList<Integer>();
        List<TileChunk> chunks = new ArrayList<TileChunk>();
        chunks.add(new TileChunk(initialTiles));
        
        for (Line2D.Double line : grid){
            // Slice up any Chunks which this line cuts
            for (int i=0; i<chunks.size(); i++){
                TileChunk chunk = chunks.get(i);
                TileChunk[] pieces = chunk.slice(line);
                if (pieces[0].isEmpty() || pieces[1].isEmpty()){
                    // The cut did nothing
                    continue;
                }
                // Remove the chunk which was cut
                chunks.remove(i);
                // Replace it with the remaining pieces
                chunks.add(i, pieces[0]);
                chunks.add(i+1, pieces[1]);
                
                
            }
            
        }
        Collections.sort(chunks);
        for (TileChunk chunk: chunks){
            for (Tile tile : chunk.myTiles){
                gridIndexOrder.add(tile.getIndex());
            }
        }
    }

    



    /**
     * Start the animation going from unsorted to sorted. If the animation
     * is already going, this will do nothing.
     */
    public void sort(){
        animate(1000, 100);
    }

    /**
     * Reverse the direction of the animation and start the animation.
     */
    public void restore(){
        // We need to animate back to where the original tiles belong:
        // (the current moving tiles may be from another grid, not the original
        // block... )
        clear();
        
        for (int i=0; i<originalTiles.size(); i++){
            Tile sortedTile = new Tile(gridIndexOrder.get(i), 
                    originalTiles.get(i).getFabric(), blockSize);
            
            addSprite(new MovingTile(originalTiles.get(i), sortedTile, patchSize));
            
        }
        
        
        
        animateReverse(1000, 100);
    }


    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("grid")){
            Grid newGrid = (Grid) evt.getNewValue();
            reSortToNewGrid(newGrid);
        }
    }

    /**
     * Take a new Grid, and animate the existing tiles to sort to it instead
     * of whatever was there before.
     * 
     * @param newGrid
     */
    public void reSortToNewGrid(Grid newGrid) {
        List<Integer> oldIndexOrder = gridIndexOrder;
        analyzeGrid(newGrid, originalTiles);
        
        clear();
        
        // Make new moving tiles that go from where tiles were to where they should be now
        for (int i=0; i<oldIndexOrder.size(); i++){
            Tile oldTile = new Tile(oldIndexOrder.get(i), 
                    originalTiles.get(i).getFabric(), blockSize);
            Tile newTile = new Tile(gridIndexOrder.get(i), 
                    originalTiles.get(i).getFabric(), blockSize);
            
            addSprite(new MovingTile(oldTile, newTile, patchSize));
        }
        
        // And start animating!
        sort();
        
    }


    /**
     * Represents a set of Tiles. These tiles can be sliced by a given
     * GridLine, resulting in the two smaller chunks.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-06-15 20:25:34 $
     * @version $Revision: 1.2 $
     *
     */
    private class TileChunk implements Comparable<TileChunk>{

        /**
         * List of tiles that this Chunk contains.
         */
        List<Tile> myTiles;

        /**
         * Create a new TileChunk which contains the given tiles.
         * @param tiles
         */
        public TileChunk(List<Tile> tiles){
            myTiles = tiles;
        }
        
        /**
         * @return true if this Chunk contains no tiles at all.
         */
        public boolean isEmpty(){
            return myTiles.isEmpty();
        }

        /**
         * Slices this chunk into (possibly) two along the given GridLine.
         * This will return the two pieces. Note that if this line doesn't
         * actually slice this Chunk, the returned chunks will consist of
         * a clone of this chunk and an empty chunk. Also note that this
         * method does not actually change this TileChunk, it just creates
         * two new ones which could replace it.
         * 
         * @param line
         * @return an array containing the two pieces.
         */
        public TileChunk[] slice(Line2D.Double line){
            // Convert line percentages into pixel coordinates
            int x1 = (int) (line.getX1()*patchSize*sideSize);
            int y1 = (int) (line.getY1()*patchSize*sideSize);
            int x2 = (int) (line.getX2()*patchSize*sideSize);
            int y2 = (int) (line.getY2()*patchSize*sideSize);
            
            List<Tile> above = new ArrayList<Tile>();
            List<Tile> below = new ArrayList<Tile>();
            for (Tile tile : myTiles){
                int x = tile.getX(patchSize);
                int y = tile.getY(patchSize);
                // X and Y are currently the coordinates of the middle
                // of a patch quadrant, we need to 'bump' one of them to
                // the middle of this tile depending on which one it is
                // (similar to what getThetaForIndex does)
                switch (tile.getIndex() % 4){
                case 0:
                    y -= patchSize/8;
                    break;
                case 1:
                    x -= patchSize/8;
                    break;
                case 2:
                    x += patchSize/8;
                    break;
                case 3:
                    y += patchSize/8;
                    break;
                }
                
                if (x2 != x1){
                    int lineY = (y2-y1)/(x2-x1)*(x-x1) + y1; // Two point form...

                    if (lineY > y){
                        above.add(tile);
                    } else {
                        below.add(tile);
                    }
                } else {
                    // This is a vertical line, so to avoid dividing by zero
                    // we have to deal with it differently
                    if (x < x1){
                        above.add(tile);
                    } else {
                        below.add(tile);
                    }
                }
            }
            return new TileChunk[]{new TileChunk(above), new TileChunk(below)};

        }

        public int compareTo(TileChunk o) {
            return myTiles.get(0).getIndex() - o.myTiles.get(0).getIndex();
        }
        


    }
}
