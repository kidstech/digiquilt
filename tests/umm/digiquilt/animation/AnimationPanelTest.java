/*
 * Created by jbiatek on Jan 10, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.animation;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.eq;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class AnimationPanelTest {

    /**
     * The frame to show the test panel in.
     */
    JFrame frame;

    /**
     * The test panel.
     */
    AnimationPanel panel;

    /**
     * 
     */
    @Before
    public void createPanel(){
        frame = new JFrame();
        panel = GuiActionRunner.execute(
                new GuiQuery<AnimationPanel>() {
                    @Override
                    protected AnimationPanel executeInEDT() throws Throwable {
                        AnimationPanel anim = new AnimationPanel();
                        frame.add(anim);
                        frame.setVisible(true);
                        anim.setVisible(true);
                        return anim;
                    }

                });

    }

    /**
     * 
     */
    @After
    public void closeFrame(){
        frame.dispose();
    }


    /**
     * @throws Exception
     */
    @Test
    public void testAnimation() throws Exception {
        Sprite mockSprite = mock(Sprite.class);
        PropertyChangeListener pch = mock(PropertyChangeListener.class);
        InOrder inOrder = inOrder(mockSprite, pch);


        panel.addSprite(mockSprite);
        panel.addPropertyChangeListener("animationstate", pch);

        // Try it 3 times in a row, to make sure it doesn't misbehave
        // the next time around
        
        for ( int count=0; count<3; count++){
            panel.animate(500, 20);

            // Verify that the correct PropertyChange was sent first
            ArgumentCaptor<PropertyChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(PropertyChangeEvent.class);
            inOrder.verify(pch).propertyChange(eventCaptor.capture());
            assertEquals(
                    eventCaptor.getValue().getPropertyName(), "animationstate");
            assertEquals(
                    eventCaptor.getValue().getNewValue(), "forward");


            Thread.sleep(1000);

            // Check that it was told the new percentage of animation, and then
            // to paint itself
            float percent = 0.0f;
            for (int i=0; i<=20; i++){
                inOrder.verify(mockSprite).setPercent(eq(percent, 0.000001f));
                percent += 1f/20;
            }

            inOrder.verify(pch).propertyChange(eventCaptor.capture());
            assertEquals(
                    eventCaptor.getValue().getPropertyName(), "animationstate");
            assertEquals(
                    eventCaptor.getValue().getNewValue(), "stopped");
        }

    }

    /**
     * @throws Exception
     */
    @Test
    public void testReverseAnimation() throws Exception {
        Sprite mockSprite = mock(Sprite.class);
        PropertyChangeListener pch = mock(PropertyChangeListener.class);
        InOrder inOrder = inOrder(mockSprite, pch);


        panel.addSprite(mockSprite);
        panel.addPropertyChangeListener("animationstate", pch);

        // Try it 3 times in a row, to make sure it doesn't misbehave
        // the next time around
        
        for ( int count=0; count<3; count++){
            panel.animateReverse(500, 20);

            // Verify that the correct PropertyChange was sent first
            ArgumentCaptor<PropertyChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(PropertyChangeEvent.class);
            inOrder.verify(pch).propertyChange(eventCaptor.capture());
            assertEquals(
                    eventCaptor.getValue().getPropertyName(), "animationstate");
            assertEquals(
                    eventCaptor.getValue().getNewValue(), "reverse");


            Thread.sleep(1000);

            // Check that it was told the new percentage of animation, and then
            // to paint itself
            float percent = 1f;
            for (int i=0; i<=20; i++){
                inOrder.verify(mockSprite).setPercent(eq(percent, 0.000001f));
                percent -= 1f/20;
            }

            inOrder.verify(pch).propertyChange(eventCaptor.capture());
            assertEquals(
                    eventCaptor.getValue().getPropertyName(), "animationstate");
            assertEquals(
                    eventCaptor.getValue().getNewValue(), "stopped");
        }

    }

    
    /**
     * @throws Exception
     */
    @Test
    public void testClear() throws Exception {
        Sprite mockSprite1 = mock(Sprite.class);
        Sprite mockSprite2 = mock(Sprite.class);

        panel.addSprite(mockSprite1);
        panel.addSprite(mockSprite2);

        panel.clear();
        panel.animate(10, 5);

        Thread.sleep(200);

        verify(mockSprite1, times(0)).setPercent(anyFloat());
        verify(mockSprite1, times(0)).paintSprite(any(Graphics.class));
        verify(mockSprite2, times(0)).setPercent(anyFloat());
        verify(mockSprite2, times(0)).paintSprite(any(Graphics.class));


    }

}
