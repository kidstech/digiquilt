package umm.digiquilt.view.fabriccontrols;

import umm.digiquilt.model.Fabric;

/**Interface for getting notified when a new fabric is chosen, and what
 * that fabric is.
 * 
 * @author biatekjt, created Aug 31, 2008
 *
 */
public interface FabricListener {
    /**Called when a new fabric is selected.
     * @param newFabric the fabric that was selected.
     */
    public void newFabricSelected(Fabric newFabric);
}
