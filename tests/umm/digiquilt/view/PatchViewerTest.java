/*
 * Created by jbiatek on Jun 20, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import org.junit.Test;

import umm.digiquilt.model.Fabric;
import umm.digiquilt.model.Patch;
import umm.digiquilt.model.PatchTest;
import umm.digiquilt.view.PatchViewer;


/**
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-21 23:41:03 $
 * @version $Revision: 1.1 $
 *
 */

public class PatchViewerTest {
    
    /**
     * 
     */
    Patch fancyPatch = PatchTest.makeFancyTestPatch();

    /**
     * Test creating an image of a Patch.
     * 
     * @throws Exception 
     */
    @Test
    public void testImageGeneration() throws Exception {
        BufferedImage uncropped = 
            PatchViewer.generateImage(fancyPatch, 50, false);
        assertEquals("The image should be 50x50", 
                50, uncropped.getWidth(null));
        assertEquals("The image should be 50x50",
                50, uncropped.getHeight(null));
        checkImage(fancyPatch, uncropped, 50);
    }
    
    /**
     * 
     */
    @Test
    public void testImageGeneration2(){
        BufferedImage uncropped = 
            PatchViewer.generateImage(fancyPatch, 144, false);
        assertEquals("The image should be 144x144", 
                144, uncropped.getWidth(null));
        assertEquals("The image should be 144x144",
                144, uncropped.getHeight(null));
        checkImage(fancyPatch, uncropped, 144);
    }
    
    /**
     * 
     */
    @Test
    public void testCroppedImageGeneration1(){
        // Now to test the cropped image, this is a bit more interesting...
        Patch smallPatch = new Patch(
                new Fabric[] {
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.GREEN, Fabric.RED, Fabric.BLUE, Fabric.BLACK
                }
        );
        BufferedImage cropped = 
            PatchViewer.generateImage(smallPatch, 200, true);
        assertEquals("The image should be 100x100", 
                100, cropped.getWidth(null));
        assertEquals("The image should be 100x100",
                100, cropped.getHeight(null));
        checkImage(smallPatch, cropped, 200);

    }
    
    /**
     * 
     */
    @Test
    public void testCroppedImageGeneration2(){
        Patch smallPatch = new Patch(
                new Fabric[] {
                        Fabric.RED, Fabric.ORANGE, Fabric.BLUE, Fabric.WHITE,
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT 
                }
        );
        BufferedImage cropped = 
            PatchViewer.generateImage(smallPatch, 200, true);
        assertEquals("The image should be 100x100", 
                100, cropped.getWidth(null));
        assertEquals("The image should be 100x100",
                100, cropped.getHeight(null));
        checkImage(smallPatch, cropped, 200);

    }
    
    /**
     * 
     */
    @Test
    public void testCroppedImageGeneration3(){
        Patch smallPatch = new Patch(
                new Fabric[] {
                        Fabric.RED, Fabric.PINK, Fabric.YELLOW, Fabric.ORANGE,
                        Fabric.BLUE, Fabric.BLACK, Fabric.GREEN, Fabric.INDIGO,
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, 
                        Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT, Fabric.TRANSPARENT 
                }
        );
        BufferedImage cropped = 
            PatchViewer.generateImage(smallPatch, 200, true);
        assertEquals("The image should be 200x100", 
                200, cropped.getWidth(null));
        assertEquals("The image should be 200x100",
                100, cropped.getHeight(null));
        checkImage(smallPatch, cropped, 200);


        smallPatch.rotateCW();
        cropped = PatchViewer.generateImage(smallPatch, 100, true);
        assertEquals("The image should be 50x100",
                50, cropped.getWidth(null));
        assertEquals("The image should be 50x100",
                100, cropped.getHeight(null));
        checkImage(smallPatch, cropped, 100);



    }

    /**
     * Sample pixels from the given image, and make sure they match up
     * with the given Patch. Note that this assumes the image dimensions
     * are what they're supposed to be.
     * 
     * @param patch
     * @param theImage
     * @param patchSize
     * @throws Exception 
     */
    public static void checkImage(
            Patch patch, BufferedImage theImage, int patchSize) {
        int width = theImage.getWidth();
        int height = theImage.getHeight();
        Patch thePatch = patch;
        
        boolean checkQ2 = (width == patchSize);
        boolean checkQ3 = (height == patchSize);
        boolean checkQ4 = (checkQ2 && checkQ3);
        if (!checkQ4){
            // This image is cropped, let's crop the Patch accordingly
            thePatch = patch.getSmallPatch();
        }
        
        
        int[] pixels = new int[width*height];

        PixelGrabber grabber = new PixelGrabber(theImage, 
                0, 0, theImage.getWidth(), theImage.getHeight(), pixels, 0, width);

        boolean result;
        try {
            result = grabber.grabPixels();
        } catch (InterruptedException e) {
            fail("Got interrupted");
            return;
        }

        if (!result){
            fail("Couldn't analyze the picture");
        }

        
        // Start checking pixels in the middle of where each tile should be
        // Pixel (x, y) is stored at y * width + x: 
        int tile0 = pixels[patchSize/8 * width + patchSize/4];
        checkColor(tile0, thePatch.getTile(0));
        int tile1 = pixels[patchSize/4 * width + patchSize/8];
        checkColor(tile1, thePatch.getTile(1));
        int tile2 = pixels[patchSize/4 * width + 3*patchSize/8];
        checkColor(tile2, thePatch.getTile(2));
        int tile3 = pixels[3*patchSize/8 * width + patchSize/4];
        checkColor(tile3, thePatch.getTile(3));

        if (checkQ2){
            int tile4 = pixels[patchSize/8 * width + 3*patchSize/4];
            checkColor(tile4, thePatch.getTile(4));        
            int tile5 = pixels[patchSize/4 * width + 5*patchSize/8];
            checkColor(tile5, thePatch.getTile(5));
            int tile6 = pixels[patchSize/4 * width + 7*patchSize/8];
            checkColor(tile6, thePatch.getTile(6));
            int tile7 = pixels[3*patchSize/8 * width + 3*patchSize/4];
            checkColor(tile7, thePatch.getTile(7));
        }

        if (checkQ3){
            int tile8 = pixels[5*patchSize/8 * width + patchSize/4];
            checkColor(tile8, thePatch.getTile(8));
            int tile9 = pixels[3*patchSize/4 * width + patchSize/8];
            checkColor(tile9, thePatch.getTile(9));
            int tile10 = pixels[3*patchSize/4 * width + 3*patchSize/8];
            checkColor(tile10, thePatch.getTile(10));
            int tile11 = pixels[7*patchSize/8 * width + patchSize/4];
            checkColor(tile11, thePatch.getTile(11));
        }

        if (checkQ4){
            int tile12 = pixels[5*patchSize/8 * width + 3*patchSize/4];
            checkColor(tile12, thePatch.getTile(12));
            int tile13 = pixels[3*patchSize/4 * width + 5*patchSize/8];
            checkColor(tile13, thePatch.getTile(13));
            int tile14 = pixels[3*patchSize/4 * width + 7*patchSize/8];
            checkColor(tile14, thePatch.getTile(14));
            int tile15 = pixels[7*patchSize/8 * width + 3*patchSize/4];
            checkColor(tile15, thePatch.getTile(15));
        }
    }

    /**
     * Check the pixel against the fabric (makes sure that transparent
     * tiles are handled right)
     * 
     * @param pixel
     * @param tile
     */
    private static void checkColor(int pixel, Fabric tile){
        if (tile == Fabric.TRANSPARENT){
            Color color = new Color(pixel);
            int alpha = color.getAlpha();
            // The rest shouldn't matter, we only care that it's transparent
            assertEquals("Transparent pixel should have alpha 1", 255, alpha);
        } else {
            assertEquals(tile.getColor().getRGB(), pixel);
        }
    }


}
