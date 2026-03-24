package umm.digiquilt.view.filebrowser;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import umm.digiquilt.savehandler.SaveHandler;

public class PatchFileView extends FileView {
    /**
     * If we've already loaded the icon for a file once, this holds
     * it so we can use it again. Java doesn't do this for us, so every time
     * the file chooser repaints, we've got to give it the icon again. Since
     * we're loading and resizing images from disk, this is pretty costly
     * if we don't cache.
     */
    private static HashMap<File, Icon> cache = new HashMap<File, Icon>();
    
    @Override
    public String getTypeDescription(File f) {
        if (isQuiltFile(f)){
            return "DigiQuilt Patch";
        }
        return null;
    }
    
    @Override
    public String getName(File f){
        if (f.getName().endsWith(SaveHandler.PATCH_EXT)){
            //return f.getName().replaceAll(".xml$", "");
            return "";
        }
        return f.getName();
    }

    @Override
    public Icon getIcon(File f) {
        if (isQuiltFile(f)) {
            if (cache.containsKey(f)){
                return cache.get(f);
            }
            String iconpath = f.getPath().replace(SaveHandler.PATCH_EXT, SaveHandler.PATCH_IMAGE_EXT);
            File iconFile = new File(iconpath);
            
            Icon icon = null;
            BufferedImage bimg = null;
            if (iconFile.exists()) {
                try {
                    bimg = ImageIO.read(iconFile);
                    //resizes image
                    BufferedImage dimg = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = dimg.createGraphics();  
                    //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
                    g.drawImage(bimg, 0, 0, 100, 100, 0, 0, bimg.getWidth(), bimg.getHeight(), null);  
                    g.dispose();
                    icon = new ImageIcon(dimg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //icon = new ImageIcon(iconpath);
            } else {
                icon = new MissingIcon();
            }
            cache.put(f, icon);
            return icon;
        }
        return null;
    }

    /**
     * Returns true if the given file appears to be a valid DigiQuilt
     * file.
     * 
     * @param f
     * @return true if it is, false if it isn't.
     */
    private boolean isQuiltFile(File f){
        return f.getPath().endsWith(SaveHandler.PATCH_EXT);
    }
    @Override
    public String getDescription(File f){
        return "This is where the description goes. Is this a good spot for notes, or not?";
    }
}
