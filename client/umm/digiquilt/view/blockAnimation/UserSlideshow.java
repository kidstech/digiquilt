/*
 * Created by jbiatek on Oct 21, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view.blockAnimation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;

import umm.digiquilt.model.Block;
import umm.digiquilt.model.Grid;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.blockAnimation.QuiltSlideShow;
import umm.digiquilt.view.filebrowser.QuiltFileChooser;
import umm.digiquilt.xmlsaveload.LoadXML;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */

public class UserSlideshow extends JDialog {
    
    JPanel top = new JPanel();
    
    JPanel middle = new JPanel();
    
    JPanel bottom = new JPanel();
    
    QuiltSlideShow slideShow;
    
    List<Block> blocks = new ArrayList<Block>();
    
    File defaultDirectory;
    
    public UserSlideshow(File defaultDirectory){
        this.defaultDirectory = defaultDirectory;
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        
        JButton addButton = new JButton("Add...");
        top.add(addButton);
        addButton.addActionListener(new AddAction());
        
    }
    
    private void refreshSlideshow(){
        if (slideShow != null){
            middle.remove(slideShow);
        }
        slideShow = new QuiltSlideShow(100, blocks);
        middle.add(slideShow);
        slideShow.setVisible(true);
        middle.validate();

        
        validate();
        repaint();
    }
    
    private void addBlock(Block block){
        blocks.add(block);
        bottom.add(new BlockViewer(block, 20));
        bottom.revalidate();
        
        refreshSlideshow();
    }
    
    private class AddAction implements ActionListener {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            QuiltFileChooser loadPanel = new QuiltFileChooser(defaultDirectory);
            int returnVal = loadPanel.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                try {
                    File selected = loadPanel.getSelectedFile();
                    LoadXML loader = new LoadXML(selected.getPath());
                    
                    addBlock(loader.getCurrentBlock());
                    
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null,
                            "An error occured trying to load this file.",
                            "File I/O Error",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                } catch (SAXException e1) {
                    e1.printStackTrace();
                }
            }

        }
        
    }

}
