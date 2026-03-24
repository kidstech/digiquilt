/*
 * Created by biatekjt on Mar 29, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.model;

/**
 * A challenge which matches any Block. This is good for subjective things 
 * that a computer can't check for.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FreeformChallenge implements Challenge {

    /**
     * The text of this Challenge.
     */
    String text;
    
    /**
     * Create a FreeformChallenge. 
     * 
     * @param text the text of the challenge.
     */
    public FreeformChallenge(String text){
        this.text = text;
    }
    
    @Override
    public String toString(){
        return text;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof FreeformChallenge){
            return text.equals(((FreeformChallenge) o).text);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see umm.digiquilt.model.Challenge#blockMatchesChallenge(umm.digiquilt.model.Block)
     */
    public boolean blockMatchesChallenge(Block block) {
        return true;
    }

}
