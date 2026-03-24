/*
 * Created by biatekjt on Oct 10, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class PercentageTimer extends Timer {
    
    /**
     * The number of frames to show
     */
    private int numSteps;
    /**
     * The number of frames that have been shown
     */
    private int stepsTaken;
    /**
     * The direction to move in. 1 is forward (0% to 100%), and -1 is 
     * backward (100% to 0%)
     */
    private int direction = 1;
    
    /**
     * Create a PercentageTimer that will notify its listeners <i>numSteps</i>
     * times over the course of approximately <i>totalTime</i> milliseconds.
     * Each listener will receive an ActionEvent with the completed percentage
     * stored in getActionCommand() in floating point notation.<br>
     * <br>
     *
     * 
     * @param totalTime in milliseconds
     * @param numSteps
     * @param listener
     */
    public PercentageTimer(int totalTime, int numSteps, 
            ActionListener listener) {
        super(totalTime/numSteps, listener);
        setInitialDelay(0);
        stepsTaken = 0;
        this.numSteps = numSteps;
    }

    @Override
    protected void fireActionPerformed(ActionEvent e) {
        String percentDone = Float.toString((float) stepsTaken / numSteps);
        ActionEvent newEvent = new ActionEvent(
                e.getSource(),
                e.getID(), 
                percentDone,
                e.getWhen(),
                e.getModifiers()
                );
        super.fireActionPerformed(newEvent);
        
        stepsTaken+= direction;
        if (stepsTaken > numSteps || stepsTaken < 0){
            stop();
        }
        
    }

    @Override
    public void start() {
        if (isRunning()){
            // Don't reset anything if we're already running
            super.start();
            return;
        }
        stepsTaken = 0;
        super.start();
    }

    
}
