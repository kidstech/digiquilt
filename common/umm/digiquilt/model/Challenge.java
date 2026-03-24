/*
 * Created by biatekjt on Mar 24, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.model;

/**
 * An interface for Challenges, which can see if a Block fulfills the challenge
 * or not.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public interface Challenge {

    /**
     * See if the given Block matches this Challenge.
     * 
     * @param block
     * @return true if it does, false if it doesn't.
     */
    public boolean blockMatchesChallenge(Block block);

}
