package umm.digiquilt.view.blockAnimation;

import java.util.Comparator;

import umm.digiquilt.model.Block;
import umm.digiquilt.view.Tile;

/**
 * A class to sort fabrics based on coverage of a given Block.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-15 20:25:34 $
 * @version $Revision: 1.2 $
 *
 */
public class SortByCoverage implements Comparator<Tile>{

    /**
     * The Block to get coverage information from.
     */
    Block block;

    /**
     * @param block the Block to get coverage information from.
     */
    public SortByCoverage(Block block){
        this.block = block;
    }

    public int compare(Tile o1, Tile o2) {
        int covCompare = 
            block.getBlockCoverage(o1.getFabric()).getNumerator() 
            - block.getBlockCoverage(o2.getFabric()).getNumerator();
        
        if (covCompare != 0){
            return covCompare;
        }
        // If it's a tie, we want to differentiate between different fabrics
        // because otherwise two fabrics with the same fraction will be
        // intermingled with each other.
        return o1.getFabric().ordinal() - o2.getFabric().ordinal();
    }

}