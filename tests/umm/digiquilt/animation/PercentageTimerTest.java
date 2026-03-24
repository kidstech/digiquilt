/*
 * Created by biatekjt on Oct 10, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.animation;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.Test;

import umm.digiquilt.animation.PercentageTimer;


/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("boxing")
public class PercentageTimerTest {
    
    /**
     * @throws Exception
     */
    @Test
    public void testByFives() throws Exception{
        runTest(1000, 20);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testUnevenPercentages() throws Exception {
        runTest(1000, 7);
    }
    
    /**
     * @param totalTime
     * @param intervals
     * @return the timer that was tested
     * @throws Exception
     */
    private PercentageTimer runTest(int totalTime, int intervals) 
            throws Exception {
        TimerLogger logger = new TimerLogger();
        PercentageTimer timer = new PercentageTimer(totalTime, intervals, logger);
        timer.start();
        Thread.sleep(totalTime*2);
        
        assertTrue(logger.executedOnEventThread);
        float percent = 0f;
        for (Float current : logger.percentlog){
            assertEquals(percent, current, 0.01f);
            percent += 1f/intervals;
        }
        assertEquals("Didn't reach 100%", 1 + 1f/intervals, percent, .00001);
        
        long difference = totalTime / intervals;
        long previous = logger.timestamplog.get(0);
        for (int i=1; i<logger.timestamplog.size(); i++){
            long current = logger.timestamplog.get(i);
            // Tolerate being 50 milliseconds off
            assertEquals(0, current-previous-difference, 50);
            previous = current;
        }
        return timer;
    }
    
    /**
     * @author Jason Biatek, last changed by $Author: lamberty $
     * on $Date: 2008/01/22 17:50:24 $
     * @version $Revision: 1.1 $
     *
     */
    private class TimerLogger implements ActionListener{

        /**
         * List of the percentages received
         */
        List<Float> percentlog = new ArrayList<Float>();
        /**
         * List of when the method was called
         */
        List<Long> timestamplog = new ArrayList<Long>();
        /**
         * Whether the method was always called on the right thread
         */
        boolean executedOnEventThread = true;
        
        public void actionPerformed(ActionEvent arg0) {
            percentlog.add(Float.parseFloat(arg0.getActionCommand()));
            timestamplog.add(Calendar.getInstance().getTimeInMillis());
            if (!SwingUtilities.isEventDispatchThread()){
                executedOnEventThread = false;
            }
        }
        
    }

}
