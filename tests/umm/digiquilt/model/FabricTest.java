package umm.digiquilt.model;

import java.awt.Color;

import org.junit.Test;

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
				"The color for the dark pink fabric should be dark pink",
				Fabric.REDVIOLET.getColor(), new Color(205, 80, 120));
		assertEquals("The color for the red fabric should be red", Fabric.RED
				.getColor(), new Color(180, 65, 80));
		assertEquals("The color for the orange fabric should be orange",
				Fabric.ORANGE.getColor(), new Color(230, 150, 100));
		assertEquals("The color for the yellow fabric should be yellow",
				Fabric.YELLOW.getColor(), new Color(240, 215, 50));
		assertEquals("The color for the green fabric should be green",
				Fabric.GREEN.getColor(), new Color(160, 187, 130));
		assertEquals(
				"The color for the dark green fabric should be dark green",
				Fabric.DARKGREEN.getColor(), new Color(79, 140, 105));
		assertEquals("The color for the blue fabric should be blue",
				Fabric.BLUE.getColor(), new Color(60, 100, 190));
		assertEquals("The color for the light blue fabric should be light blue",
				Fabric.INDIGO.getColor(), new Color(131, 175, 208));
		assertEquals("The color for the violet fabric should be violet",
				Fabric.VIOLET.getColor(), new Color(117, 88, 154));
		assertEquals("The color for the pink fabric should be pink",
				Fabric.PINK.getColor(), new Color(220, 150, 160));
		assertEquals("The color for the white fabric should be white",
				Fabric.WHITE.getColor(), new Color(246, 236, 235));
		assertEquals("The color for the black fabric should be black",
				Fabric.BLACK.getColor(), new Color(65, 63, 68));
		assertEquals("The color for the brown fabric should be brown",
				Fabric.BROWN.getColor(), new Color(120, 90, 70));
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
                "Dark Pink", Fabric.REDVIOLET.getName());
	    assertEquals("Fabric name was incorrect", 
                "Red", Fabric.RED.getName());
	    assertEquals("Fabric name was incorrect", 
                "Orange", Fabric.ORANGE.getName());
	    assertEquals("Fabric name was incorrect", 
                "Yellow", Fabric.YELLOW.getName());
	    assertEquals("Fabric name was incorrect", 
                "Light Green", Fabric.GREEN.getName());
	    assertEquals("Fabric name was incorrect", 
                "Green", Fabric.DARKGREEN.getName());
	    assertEquals("Fabric name was incorrect", 
                "Blue", Fabric.BLUE.getName());
	    assertEquals("Fabric name was incorrect", 
                "Light Blue", Fabric.INDIGO.getName());
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
                Color.BLACK, Fabric.INDIGO.getGoodTextColor());
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
