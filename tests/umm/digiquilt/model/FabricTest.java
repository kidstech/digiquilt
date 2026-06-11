package umm.digiquilt.model;

import java.awt.Color;

import org.junit.Test;

import umm.digiquilt.model.Fabric;

import static junit.framework.Assert.*;

/**
 * Test the Fabric class
 *
 */
public class FabricTest{

	/**
	 * Test that the colors are correct
	 */
    @Test
	public void testGetColor() {
		assertEquals("The color for the gray fabric should be gray",
				Fabric.GRAY.getColor(), Color.GRAY);
		assertEquals(
				"The color for the red violet fabric should be red violet",
				Fabric.REDVIOLET.getColor(), new Color(205, 0, 103));
		assertEquals("The color for the red fabric should be red", Fabric.RED
				.getColor(), new Color(234, 43, 30));
		assertEquals("The color for the orange fabric should be orange",
				Fabric.ORANGE.getColor(), new Color(255, 169, 33));
		assertEquals("The color for the yellow fabric should be yellow",
				Fabric.YELLOW.getColor(), Color.YELLOW);
		assertEquals("The color for the green fabric should be green",
				Fabric.GREEN.getColor(), new Color(77, 209, 0));
		assertEquals(
				"The color for the dark green fabric should be dark green",
				Fabric.DARKGREEN.getColor(), new Color(52, 102, 51));
		assertEquals("The color for the blue fabric should be blue",
				Fabric.BLUE.getColor(), new Color(0, 57, 228));
		assertEquals("The color for the indigo fabric should be indigo",
				Fabric.INDIGO.getColor(), new Color(102, 0, 205));
		assertEquals("The color for the violet fabric should be violet",
				Fabric.VIOLET.getColor(), new Color(153, 0, 153));
		assertEquals("The color for the pink fabric should be pink",
				Fabric.PINK.getColor(), new Color(255, 124, 161));
		assertEquals("The color for the white fabric should be white",
				Fabric.WHITE.getColor(), Color.WHITE);
		assertEquals("The color for the black fabric should be black",
				Fabric.BLACK.getColor(), Color.BLACK);
		assertEquals("The color for the brown fabric should be brown",
				Fabric.BROWN.getColor(), new Color(153, 52, 0));
		assertEquals("The color for the transparent fabric should be gray with a 0 alpha",
				Fabric.TRANSPARENT.getColor(), new Color(128, 128, 128, 0));
	}
	
	/**
	 * Test getName()
	 */
	@Test
	public void testToString(){
	    assertEquals("Fabric name was incorrect", 
	            "Gray", Fabric.GRAY.getName());
	    assertEquals("Fabric name was incorrect", 
                "Red Violet", Fabric.REDVIOLET.getName());
	    assertEquals("Fabric name was incorrect", 
                "Red", Fabric.RED.getName());
	    assertEquals("Fabric name was incorrect", 
                "Orange", Fabric.ORANGE.getName());
	    assertEquals("Fabric name was incorrect", 
                "Yellow", Fabric.YELLOW.getName());
	    assertEquals("Fabric name was incorrect", 
                "Green", Fabric.GREEN.getName());
	    assertEquals("Fabric name was incorrect", 
                "Dark Green", Fabric.DARKGREEN.getName());
	    assertEquals("Fabric name was incorrect", 
                "Blue", Fabric.BLUE.getName());
	    assertEquals("Fabric name was incorrect", 
                "Indigo", Fabric.INDIGO.getName());
	    assertEquals("Fabric name was incorrect", 
                "Violet", Fabric.VIOLET.getName());
	    assertEquals("Fabric name was incorrect", 
                "Pink", Fabric.PINK.getName());
	    assertEquals("Fabric name was incorrect", 
                "White", Fabric.WHITE.getName());
	    assertEquals("Fabric name was incorrect", 
                "Black", Fabric.BLACK.getName());
	    assertEquals("Fabric name was incorrect", 
                "Brown", Fabric.BROWN.getName());
	    assertEquals("Fabric name was incorrect", 
                "Transparent", Fabric.TRANSPARENT.getName());
	}
	

	/**
	 * Test getGoodTextColor()
	 */
	@Test
	public void testGetGoodTextColor(){
	    assertEquals("getGoodTextColor() returned wrong color", 
	            Color.BLACK, Fabric.GRAY.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.REDVIOLET.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.RED.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.ORANGE.getGoodTextColor()); 
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.YELLOW.getGoodTextColor()); 
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.GREEN.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.DARKGREEN.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.BLUE.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.INDIGO.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.VIOLET.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.PINK.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.WHITE.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.BLACK.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.WHITE, Fabric.BROWN.getGoodTextColor());
	    assertEquals("getGoodTextColor() returned wrong color", 
                Color.BLACK, Fabric.TRANSPARENT.getGoodTextColor());
	}
	
	/**
	 * Test the semi-transparent shadow color
	 */
	@Test
	public void testShadowColor(){
	    assertEquals("getShadowColor() returned the wrong color", 
	            new Color(128, 128, 128, 128), Fabric.getShadowColor());
	}
	
    /**
     * Test that different fabrics are not equal
     */
    @Test
    public void testEquals() {
        assertFalse("transparent and red should not be equal", 
                Fabric.TRANSPARENT.equals(Fabric.RED));
    }
    
}
