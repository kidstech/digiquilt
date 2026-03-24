package umm.digiquilt.view.blockAnimation;

/**
 * An interface for components who wish to be notified when an 
 * AnimatedBlockViewer finishes its animation.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:53 $
 * @version $Revision: 1.1 $
 *
 */
public interface BlockSortListener {

    /**
     * Called when the AnimatedBlockViewer finishes its animation.
     * 
     * @param nowSorted true if the viewer is now displaying a 
     * sorted block, false if it is showing the original block.
     */
    public void animationComplete(boolean nowSorted);
    
}
