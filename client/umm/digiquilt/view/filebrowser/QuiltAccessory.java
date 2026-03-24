package umm.digiquilt.view.filebrowser;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.xml.sax.SAXException;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * A side panel for a JFileChooser that displays a preview of the selected
 * quilt and the notes/challenge for that quilt.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-05-28 19:19:52 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class QuiltAccessory extends JPanel implements PropertyChangeListener {

    /**
     * Panel to show the notes pulled from XML.
     */
    NotePanel notes = new NotePanel();
    
    /**
     * Label to show the challenge pulled from XML.
     */
    JLabel challenge = new JLabel();
    
    /**
     * Label to show the loaded image of the selected quilt.
     */
    JLabel preview = new JLabel();
    
    /**
     * Label to show the file name.
     */
    JLabel fileName = new JLabel();
    
    /**
     * Create a new QuiltAccessory panel.
     */
    public QuiltAccessory() {
        this.setPreferredSize(new Dimension(400,600));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;
        c.gridheight=1;
        this.add(fileName, c);
        
        c.gridx=0;
        c.gridy=1;
        c.gridwidth=1;
        c.gridheight=1;
        preview.setPreferredSize(new Dimension(200,200));
        this.add(preview, c);
        
        c.gridx=0;
        c.gridy=2;
        c.gridwidth=1;
        c.gridheight=1;
        //challenge.setOpaque(true);
        //challenge.setBackground(ChallengePanel.CHALLENGE_COLOR);
        challenge.setPreferredSize(new Dimension(400, 60));
        this.add(challenge, c);
        
        c.gridx=0;
        c.gridy=3;
        c.gridwidth=1;
        c.gridheight=1;
        notes.setEditable(false);
        this.add(notes, c);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)){
            File selected = (File) evt.getNewValue();
            
            
            LoadXML loader;
            try {
                loader = new LoadXML(selected.getAbsolutePath());
                String student = loader.getStudent();
                String blockName = loader.getBlockName();
                fileName.setText(student +" - " + blockName);
                
                notes.setText(loader.getNotes());
                challenge.setText("<html>"+loader.getChallenge());

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SAXException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println(selected.getPath());
            String imageLocation = selected.getPath().replaceFirst(SaveHandler.QUILT_EXT+"$", ".png");
            File image = new File(imageLocation);
            if (!image.exists()){
                // Try to find a jpeg instead of a png
                imageLocation = selected.getPath().replaceFirst(SaveHandler.QUILT_EXT+"$", ".jpg");
                image = new File(imageLocation);
            }
            if (image.exists()){
                try {
                    BufferedImage loaded = ImageIO.read(image);
                    BufferedImage scaled = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaled.createGraphics();  
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
                    g.drawImage(loaded, 0, 0, 200, 200, 0, 0, loaded.getWidth(), loaded.getHeight(), null);  
                    g.dispose();
                    
                    preview.setIcon(new ImageIcon(scaled));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            } else {
                preview.setIcon(null);
            }
        }

    }



}
