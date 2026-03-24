package umm.digiquilt.view.blockAnimation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import umm.digiquilt.animation.AnimationPanel;
import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.view.Tile;

/**
 * A block animation which transitions through a given list of Blocks.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-16 23:17:14 $
 * @version $Revision: 1.4 $
 *
 */
@SuppressWarnings("serial")
public class QuiltSlideShow extends AnimationPanel implements PropertyChangeListener{

    /**
     * The list of blocks to animate.
     */
    List<Block> blocks;

    /**
     * The index of the block that we're currently animating as the 
     * starting block. This block is transforming into currentBlock+1
     * (unless it's at the end, in which case it's transforming back into
     * the first block).
     */
    int currentBlock = -1;

    /**
     * Flag to hold all animation for a bit while a block is shown.
     */
    boolean waiting = true;
    
    /**
     * Timer to wait between animations.
     */
    Timer waitTimer = new Timer(2000, this);
    
    /**
     * The size of each Patch.
     */
    int patchSize;
    
    boolean stopped = false;

    /**
     * Create a QuiltSlideShow with the given patchSize and blocks. The
     * animation will start automatically and keep going forever.
     * 
     * @param patchSize
     * @param blocks
     */
    public QuiltSlideShow(int patchSize, List<Block> blocks){
        // Just set the block size to 4x4 rather than think about it
        this.blocks = blocks;
        this.patchSize = patchSize;
        waitTimer.setRepeats(false);
        
        Dimension mySize = new Dimension(4*patchSize, 4*patchSize);
        this.setSize(mySize);
        this.setMinimumSize(mySize);
        this.setMaximumSize(mySize);
        this.setPreferredSize(mySize);
        setOpaque(false);
        addPropertyChangeListener("animationstate", this);
        
        initializeTiles(0, 1);
        waitTimer.start();
    }

    /**
     * Set up animation to go between the blocks at the given indices.
     * 
     * @param startBlockIndex
     * @param endBlockIndex
     */
    private void initializeTiles(int startBlockIndex, int endBlockIndex){
        clear();
        Block startBlock = blocks.get(startBlockIndex);
        Block endBlock = blocks.get(endBlockIndex);
        
        if (areRelated(startBlock, endBlock)){
            mapRelatedBlocks(startBlock, endBlock);
        } else {
            mapUnrelatedBlocks(startBlock, endBlock);
        }
        
        
        
    }
    
    
    /**
     * Set up animation between two blocks which aren't fractionally
     * related to one another.
     * 
     * @param start
     * @param end
     */
    private void mapUnrelatedBlocks(Block start, Block end){
        if (start.getSize() != end.getSize()){
            mapIrregularSizedBlocks(start, end);
            return;
        }
        
        List<Tile> startTiles = Tile.getTilesFromBlock(start);
        List<Tile> endTiles = Tile.getTilesFromBlock(end);
        
        for (int i=0; i<startTiles.size(); i++){
            addSprite(new MovingTile(startTiles.get(i), endTiles.get(i), 
                    patchSize));
        }

        
    }
    
    /**
     * Map two blocks that aren't the same size.
     * 
     * @param start
     * @param end
     */
    private void mapIrregularSizedBlocks(Block start, Block end){
        List<Tile> startTiles = Tile.getTilesFromBlock(start);
        List<Tile> endTiles = Tile.getTilesFromBlock(end);
        
        int smallerListSize = Math.min(startTiles.size(), endTiles.size());
        
        for (int i=0; i<smallerListSize; i++){
            addSprite(new MovingTile(startTiles.get(0), endTiles.get(0), 
                    patchSize));
            
            startTiles.remove(0);
            endTiles.remove(0);
        }
        
        // Now take care of the ones that didn't get paired up:
        for (int i=0; i<startTiles.size(); i++){
            addSprite(new MovingTile(startTiles.get(i), 
                    makeOffscreenTile(startTiles.get(i).getFabric()),
                    patchSize));
        }
        for (int i=0; i<endTiles.size(); i++){
            addSprite(new MovingTile(
                    makeOffscreenTile(endTiles.get(i).getFabric()), 
                    endTiles.get(i), 
                    patchSize));
        }
        
    }
    
    /**
     * @param color
     * @return a tile that will come from offscreen
     */
    private Tile makeOffscreenTile(Fabric color){
        Random random = new Random();
        int index;
        if (random.nextBoolean()){
            // Start somewhere above the screen
            index = -(random.nextInt(64)+1);
        } else {
            index = random.nextInt(64)+256;
        }
        return new Tile(index, color, 16);
    }
    
    /**
     * Animate between blocks that have corresponding fractions.
     * 
     * @param start
     * @param end
     */
    private void mapRelatedBlocks(Block start, Block end){
        List<Tile> startTiles = Tile.getTilesFromBlock(start);
        List<Tile> endTiles = Tile.getTilesFromBlock(end);
        
        Collections.sort(startTiles, new SortByCoverage(start));
        Collections.sort(endTiles, new SortByCoverage(end));
        
        for (int i=0; i<startTiles.size(); i++){
            addSprite(new MovingTile(startTiles.get(i), endTiles.get(i),
                    patchSize));
        }
        
    }
    
    /**
     * @param block1
     * @param block2
     * @return true if the blocks have corresponding fractions and are
     * the same size, false otherwise
     */
    private boolean areRelated(Block block1, Block block2){
        if (block1.getSize() != block2.getSize()){
            return false;
        }
        List<Fraction> fractions1 = getFractions(block1);
        List<Fraction> fractions2 = getFractions(block2);

        return (fractions1.equals(fractions2));
    }
    
    /**
     * @param block
     * @return a list of the fractions in this block, sorted
     */
    private List<Fraction> getFractions(Block block){
        List<Fraction> fractions = new ArrayList<Fraction>();
        for (Fabric fabric : Fabric.values()){
            Fraction amount = block.getBlockCoverage(fabric);
            if (amount.getNumerator() != 0){
                fractions.add(amount);
            }
        }
        Collections.sort(fractions);
        return fractions;
    }
    

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getOldValue() == null || evt.getNewValue() == null){
            return;
        }
        
        if (evt.getNewValue().equals("stopped")){
            waiting = true;
            waitTimer.start();
        }
        
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (stopped) return;
        
        if (waiting){
            setUpNextTransition();
            waiting = false;
            animate(2000, 100);
        } else {
            super.actionPerformed(e);
        }
    }
    
    /**
     * Set up for a move to the next block in the list
     */
    private void setUpNextTransition(){
        if (stopped){
            return;
        }
        currentBlock++;
        if (currentBlock == blocks.size()){
            currentBlock = 0;
        }
        if (currentBlock == blocks.size()-1){
            initializeTiles(currentBlock, 0);
        } else {
            initializeTiles(currentBlock, currentBlock+1);
        }

    }
    
    public void stop(){
        stopped = true;
    }

}
