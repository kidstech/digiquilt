package umm.digiquilt.view.fabriccontrols;


import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.Shape;
import umm.digiquilt.view.fabriccontrols.FractionViewer;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FractionViewerTest {

    /**
     * FEST frame fixture for the test component
     */
    private FrameFixture window;
    
    /**
     * We'll only test these fabrics, it's not necessary to check every
     * single one of them. They should all work equally well.
     */
    private final Fabric[] testFabrics = new Fabric[]{
            Fabric.RED, Fabric.BLUE, Fabric.GRAY, Fabric.GREEN
    };

    /**
     * Set up the frame fixtures, etc.
     */
    @Before
    public void setUp() {
        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                jframe.getContentPane().setLayout(new FlowLayout());
                // Create a button for every fabric
                for (Fabric fabric : testFabrics){
                    FractionViewer viewer = 
                        new FractionViewer(fabric.toString()+" label", fabric);
                    viewer.setVisible(true);
                    jframe.add(viewer);
                }
                
                
                return jframe;  
            }
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }
    
    /**
     * Clean up afterwards
     */
    @After
    public void tearDown(){
        window.cleanUp();
    }
    
    /**
     * Check all the buttons for the correct settings & behavior
     */
    @Test
    public void testButton(){
        // Check the button for each fabric
        for (Fabric fabric : testFabrics){
            
            JButtonFixture button = 
                window.button(fabric.toString() + " label" + " fraction");
            button.background().requireEqualTo(fabric.getColor());
            button.foreground().requireEqualTo(fabric.getGoodTextColor());
            Dimension preferrred = button.component().getPreferredSize();
            Dimension minimum = button.component().getMinimumSize();
            Dimension maximum = button.component().getMaximumSize();
            Dimension expected = new Dimension(37, 32);
            assertEquals(expected, preferrred);
            assertEquals(expected, minimum);
            assertEquals(expected, maximum);
            // The button is small enough that this needs to be done
            // or it is too shifted to the right
            Insets margin = button.component().getMargin();
            assertEquals(new Insets(0,0,0,0), margin);
            
            // This should be set false, it paints a weird rectangle
            // when the button has focus and it looks bad.
            assertFalse(button.component().isFocusPainted());
            
            String tooltip = button.component().getToolTipText();
            assertEquals("Fraction of the block that is currently " + 
                    fabric.toString() + " label" +
                    ". \n" + " Click to increase the denomonator.", 
                    tooltip);
            button.requireText("");
            
            
            
            // Now we check the behavior
            Block testBlock = new Block(9);
            testBlock.setPatch(Shape.QUARTERSQUARE.getPatch(fabric), 0);

            button.targetCastedTo(FractionViewer.class)
                .propertyChange(makeEvent(testBlock));
            
            // Should now have html text
            checkButton(button, 1, 36);
            
            // Try increasing & reducing
            button.click();
            checkButton(button, 2, 72);
            
            button.click();
            checkButton(button, 4, 144);
            
            // Should have wrapped around
            button.click();
            checkButton(button, 1, 36);
            
            // Now we check reduction:
            button.click();
            button.click();
            button.targetCastedTo(FractionViewer.class)
                .reduce();
            checkButton(button, 1, 36);
            
            // It should reduce on block change, even if the fraction
            // remains unchanged
            button.click();
            button.click();
            button.targetCastedTo(FractionViewer.class)
                .propertyChange(makeEvent(testBlock));
            checkButton(button, 1, 36);

            // Make sure that the full block case is handled right
            testBlock.replaceFabricInBlock(Fabric.TRANSPARENT, fabric);
            button.targetCastedTo(FractionViewer.class)
                .propertyChange(makeEvent(testBlock));
            button.requireText("1");
            
            // Inflating a 1 should do... this? I guess?
            button.click();
            checkButton(button, 2, 2);
            button.click();
            checkButton(button, 3, 3);
            
            
            // Make sure that 0 is still handled right
            testBlock.replaceFabricInBlock(fabric, Fabric.TRANSPARENT);
            button.targetCastedTo(FractionViewer.class)
                .propertyChange(makeEvent(testBlock));
            button.requireText("");
            // Clicking shouldn't change it either
            button.click();
            button.requireText("");
            
        }
    }

    private PropertyChangeEvent makeEvent(Block block){
        return new PropertyChangeEvent(this, "currentBlock", null, block);
    }
    
    /**
     * Make sure this button has the correct fraction inside, and that
     * it is displaying it properly.
     * 
     * @param button
     * @param num
     * @param denom
     */
    private void checkButton(JButtonFixture button, int num, int denom){
        button.requireText(getHTML(num, denom));
        Fraction fraction = button.targetCastedTo(FractionViewer.class)
            .getFraction();
        assertEquals(num, fraction.getNumerator());
        assertEquals(denom, fraction.getDenominator());
    }
    
    /**
     * @param num
     * @param den
     * @return the proper html text for these numbers
     */
    private String getHTML(int num, int den){
        return "<html><center><u>&nbsp;"+num+
            "&nbsp;</u><br>"+den+"</center></html>";
    }
    
    

}
