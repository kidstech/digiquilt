/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.fabriccontrols;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Fraction;
import umm.digiquilt.model.Shape;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.view.fabriccontrols.FabricListener;
import umm.digiquilt.view.fabriccontrols.FabricPalette;
import umm.digiquilt.view.fabriccontrols.FractionViewer;

/**
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author:
 *         fortunan $ on $Date: 2009-07-22 17:59:28 $
 * @version $Revision: 1.6 $
 */

@GUITest
public class FabricPaletteTest{

    /**
     * Backend block works
     */
    private BlockWorks blockWorks;

    /**
     * FEST frame fixture for the test palette
     */
    private FrameFixture window;

    /**
     * Test fabric palette
     */
    private FabricPalette palette;

    /**
     * Set up the frame fixtures, etc.
     */
    @Before
    public void setUp() {
        blockWorks = new BlockWorks();
        JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                JFrame jframe = new JFrame();
                palette = new FabricPalette(blockWorks);
                jframe.add(palette);
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
     * Test that listeners receive fabric change events
     */
    @Test
    public void testFabricListenerFiring() {
        TestFabricListener testListener = new TestFabricListener();
        palette.addFabricListener(testListener);
        
        window.button("Red Violet").click();
        assertEquals("Fabric change was not sent",
                Fabric.REDVIOLET, testListener.lastFabric);
        
        window.button("Red").click();
        assertEquals("Fabric change was not sent",
                Fabric.RED, testListener.lastFabric);
        
        window.button("Orange").click();
        assertEquals("Fabric change was not sent",
                Fabric.ORANGE, testListener.lastFabric);
        
        window.button("Yellow").click();
        assertEquals("Fabric change was not sent",
                Fabric.YELLOW, testListener.lastFabric);
        
        window.button("Green").click();
        assertEquals("Fabric change was not sent",
                Fabric.GREEN, testListener.lastFabric);
        
        window.button("Dark Green").click();
        assertEquals("Fabric change was not sent",
                Fabric.DARKGREEN, testListener.lastFabric);
        
        window.button("Blue").click();
        assertEquals("Fabric change was not sent",
                Fabric.BLUE, testListener.lastFabric);
        
        window.button("Indigo").click();
        assertEquals("Fabric change was not sent",
                Fabric.INDIGO, testListener.lastFabric);
        
        window.button("Violet").click();
        assertEquals("Fabric change was not sent",
                Fabric.VIOLET, testListener.lastFabric);
        
        window.button("Pink").click();
        assertEquals("Fabric change was not sent",
                Fabric.PINK, testListener.lastFabric);
        
        window.button("White").click();
        assertEquals("Fabric change was not sent",
                Fabric.WHITE, testListener.lastFabric);
        
        window.button("Black").click();
        assertEquals("Fabric change was not sent",
                Fabric.BLACK, testListener.lastFabric);
        
        window.button("Brown").click();
        assertEquals("Fabric change was not sent",
                Fabric.BROWN, testListener.lastFabric);
        
        palette.removeFabricListener(testListener);
        window.button("Red Violet").click();
        assertEquals("Fabric changed after listener was unsubscribed", 
                Fabric.BROWN, testListener.lastFabric);
        
    }
    
    /**
     * Test reduce button and fraction buttons
     */
    @Test
    public void testReduceButton(){
        window.button("Reduce").requireText("Reduce");
        // Create block with each color
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.REDVIOLET), 0);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.RED), 1);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.ORANGE), 2);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.YELLOW), 3);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.GREEN), 4);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.DARKGREEN), 5);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLUE), 6);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.INDIGO), 7);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.VIOLET), 8);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.PINK), 9);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.WHITE), 10);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 11);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BROWN), 12);

        checkFractionButton(
                window.button("Red Violet fraction"), Fabric.REDVIOLET);
        checkFractionButton(
                window.button("Red fraction"), Fabric.RED);
        checkFractionButton(
                window.button("Orange fraction"), Fabric.ORANGE);
        checkFractionButton(
                window.button("Yellow fraction"), Fabric.YELLOW);
        checkFractionButton(
                window.button("Green fraction"), Fabric.GREEN);
        checkFractionButton(
                window.button("Dark Green fraction"), Fabric.DARKGREEN);
        checkFractionButton(
                window.button("Blue fraction"), Fabric.BLUE);
        checkFractionButton(
                window.button("Indigo fraction"), Fabric.INDIGO);
        checkFractionButton(
                window.button("Violet fraction"), Fabric.VIOLET);
        checkFractionButton(
                window.button("Pink fraction"), Fabric.PINK);
        checkFractionButton(
                window.button("White fraction"), Fabric.WHITE);
        checkFractionButton(
                window.button("Black fraction"), Fabric.BLACK);
        checkFractionButton(
                window.button("Brown fraction"), Fabric.BROWN);
    }
    
    /**
     * Helper method for the reduce button test
     * 
     * @param button
     * @param fabric
     */
    private void checkFractionButton(JButtonFixture button, Fabric fabric){
        FractionViewer view = button.targetCastedTo(FractionViewer.class);
        Fraction fraction = view.getFraction();
        // Should be 1/16
        assertEquals(1, fraction.getNumerator());
        assertEquals(16, fraction.getDenominator());
        
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(fabric), 15);
        fraction = view.getFraction();
        // Should now be 1/8 if the button is set up right
        assertEquals(1, fraction.getNumerator());
        assertEquals(8, fraction.getDenominator());
        
        button.click();
        // Should now be inflated to 2/16
        fraction = view.getFraction();
        assertEquals(2, fraction.getNumerator());
        assertEquals(16, fraction.getDenominator());
        
        window.button("Reduce").click();
        // Should have reduced back to 1/8
        fraction = view.getFraction();
        assertEquals(1, fraction.getNumerator());
        assertEquals(8, fraction.getDenominator());
    }
    
    /**
     * Check getting the current fractions
     */
    @Test
    public void checkGetFractions(){
        // Create block with each color
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.REDVIOLET), 0);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.RED), 1);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.ORANGE), 2);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.YELLOW), 3);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.GREEN), 4);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.DARKGREEN), 5);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLUE), 6);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.INDIGO), 7);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.VIOLET), 8);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.PINK), 9);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.WHITE), 10);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 11);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BROWN), 12);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.YELLOW), 13);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.INDIGO), 14);
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.WHITE), 15);

        
        // Click on some fractions to inflate them
        window.button("Green fraction").click();
        window.button("Green fraction").click();
        
        window.button("Black fraction").click();
        
        window.button("White fraction").click();
        
        Map<Fabric, Fraction> expected = new HashMap<Fabric, Fraction>();
        expected.put(Fabric.REDVIOLET, new Fraction(1, 16));
        expected.put(Fabric.RED, new Fraction(1, 16));
        expected.put(Fabric.ORANGE, new Fraction(1, 16));
        expected.put(Fabric.YELLOW, new Fraction(1, 8));
        expected.put(Fabric.GREEN, new Fraction(3, 48));
        expected.put(Fabric.DARKGREEN, new Fraction(1, 16));
        expected.put(Fabric.BLUE, new Fraction(1, 16));
        expected.put(Fabric.INDIGO, new Fraction(1, 8));
        expected.put(Fabric.VIOLET, new Fraction(1, 16));
        expected.put(Fabric.PINK, new Fraction(1, 16));
        expected.put(Fabric.WHITE, new Fraction(2, 16));
        expected.put(Fabric.BLACK, new Fraction(2, 32));
        expected.put(Fabric.BROWN, new Fraction(1, 16));
        
        Map<Fabric, Fraction> fractions = palette.getFractions();
        
        assertEquals("The fraction list is wrong", expected, fractions);
        
        // Make sure that the fractions are still inflated
        for (Fabric fabric : expected.keySet()){
            Fraction rightFraction = expected.get(fabric);
            Fraction testFraction = fractions.get(fabric);
            assertEquals("Incorrect but equivalent fraction", 
                    rightFraction.getNumerator(), testFraction.getNumerator());
            assertEquals("Incorrect but equivalent fraction", 
                    rightFraction.getDenominator(), 
                    testFraction.getDenominator());
        }
        
        
        
        // Now we check when a fabric has 0 coverage (it shouldn't be
        // returned to us)
        blockWorks.clear();
        blockWorks.setPatch(Shape.FULLSQUARE.getPatch(Fabric.BLACK), 0);
        
        window.button("Black fraction").click();
        window.button("Black fraction").click();
        window.button("Black fraction").click();
        
        
        expected.clear();
        expected.put(Fabric.BLACK, new Fraction(4, 64));
        
        fractions = palette.getFractions();
        
        assertEquals(expected, fractions);
        Fraction black = fractions.get(Fabric.BLACK);
        assertEquals(4, black.getNumerator());
        assertEquals(64, black.getDenominator());
        
        
        // Now we check for a fabric with 100% coverage (this displays
        // as just "1" in the GUI
        blockWorks.replaceFabricInBlock(Fabric.TRANSPARENT, Fabric.BLACK);
        expected.put(Fabric.BLACK, new Fraction(1,1));
                
        fractions = palette.getFractions();
        assertEquals(expected, fractions);
        black = fractions.get(Fabric.BLACK);
        assertEquals(1, black.getNumerator());
        assertEquals(1, black.getDenominator());
        
        // If we click it though, it'll inflate to 2/2, then 3/3, etc.
        window.button("Black fraction").click();
        expected.put(Fabric.BLACK, new Fraction(2,2));
        
        fractions = palette.getFractions();
        assertEquals(expected, fractions);
        black = fractions.get(Fabric.BLACK);
        assertEquals(2, black.getNumerator());
        assertEquals(2, black.getDenominator());

        
    }
    
    /**
     * Test fabric listener
     */
    private class TestFabricListener implements FabricListener {
        
        /** The last fabric that we've received */
        Fabric lastFabric = Fabric.TRANSPARENT;
        
        /* (non-Javadoc)
         * @see umm.softwaredevelopment.digiquilt.view.fabriccontrols.FabricListener#newFabricSelected(umm.softwaredevelopment.digiquilt.model.Fabric)
         */
        public void newFabricSelected(Fabric newFabric) {
            lastFabric = newFabric;
        }
        
    }
    

}
