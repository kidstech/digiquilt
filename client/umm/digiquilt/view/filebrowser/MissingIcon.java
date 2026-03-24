package umm.digiquilt.view.filebrowser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;

/**
 * A dynamicallly created icon, which shows a red X in a black border. This is
 * better than loading an "X" icon from a file, because what if loading that 
 * icon fails too? This way we can get an icon, guaranteed.
 * 
 * @author biatekjt, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */
public class MissingIcon implements Icon {
    /**
     * The height of this icon.
     */
    private int height = 100;
    
    /**
     * The width of this icon.
     */
    private int width = 100;

    /**
     * Create a new MissingIcon.
     */
    public MissingIcon(){
        this(100, 100);
    }
    
    /**
     * Create a new MissingIcon.
     * 
     * @param width
     * @param height
     */
    public MissingIcon(int width, int height){
        this.height = height;
        this.width = width;
    }
    
    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);
        Stroke thickLine = new BasicStroke(width/10);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(thickLine);
        g2.drawRect(0, 0, width, height);
        
        g2.setColor(Color.RED);
        g2.drawLine(width*2/10, height*2/10, width*8/10, height*8/10);
        g2.drawLine(width*2/10, height*8/10, width*8/10, height*2/10);
        g2.dispose();
    }

}
