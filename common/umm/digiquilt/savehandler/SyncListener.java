package umm.digiquilt.savehandler;

/**
 * Interface for objects who wish to be notified when a synchronize()
 * happens on a SaveHandler.
 * 
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public interface SyncListener {
    
    /**
     * This method will be called whenever a synchronize() happens
     * by the given SaveHandler. A reference to the handler in question
     * will be passed to subscribers.
     * 
     * @param handler The handler which was just synchronized.
     */
    public void onSynchronize(SaveHandler handler);

}
