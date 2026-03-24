/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import umm.digiquilt.control.ChallengeFileObserver;
import umm.digiquilt.control.ClearAction;
import umm.digiquilt.control.CreateChallengeAction;
import umm.digiquilt.control.LoadBlockAction;
import umm.digiquilt.control.MainFrameAnimationController;
import umm.digiquilt.control.SaveBlockAction;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.model.works.PatchWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.blockAnimation.UserSlideshow;
import umm.digiquilt.view.challenge.ChallengePanel;
import umm.digiquilt.view.fabriccontrols.FabricPalette;
import umm.digiquilt.view.fabriccontrols.ReplaceAllColorPanel;
import umm.digiquilt.view.glasspane.HandGlassPane;
import umm.digiquilt.view.glasspane.LockingGlassPane;
import umm.digiquilt.view.grids.GridSelectionPanel;
import umm.digiquilt.view.login.ChangeUser;
import umm.digiquilt.view.patchworkarea.PatchWorkArea;

/**
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author: biatekjt $
 *         on $Date: 2009-07-28 21:59:12 $
 * @version $Revision: 1.2 $
 */

@SuppressWarnings("serial")
//Serializations of JFrame being supressed
public class DigiQuiltFrame extends JFrame {

    private SaveHandler handler;
    
    private HandGlassPane handGlassPane;
    private LockingGlassPane lockGlassPane;
    
    private PatchWorks patchWorks;
    private BlockWorks blockWorks = new BlockWorks();
    //private GridViewPanel grid;
    private ChallengePanel challengePanel;
    //private BlockViewer blockViewer;
    private BlockWorkArea blockWorkArea;
    
    /**
     * The size of a patch, in pixels. This should be propagated to each
     * GUI element that deals with pixels rather than hardcoding the idea
     * that it is always 100x100.
     */
    private static final int PATCHSIZE = 100;

    private WhatsHappeningPanel happeningBar;

    private FabricPalette fabricPalette;

    private ShapesPalette shapesPalette;

    private JPanel basketPanel;

    private BlockSizePanel gridSelect;

    private GridSelectionPanel toolbarPalette;

    private JPanel utilityPanel;
    
    private JPanel loginPanel;
    private JLabel userLabel;

    private PatchWorkArea patchWorkArea;

    private ReplaceAllColorPanel replaceAllColorUI;
    
    private LoadPatchPanel loadPatchPanel;

    private TrashPanel trash;

    private final DefaultComboBoxModel challenges = new DefaultComboBoxModel();

    /**
     * The main DigiQuilt frame, contains all the other stuff.
     * @param handler The SaveHandler to take care of saving. 
     */
    public DigiQuiltFrame(SaveHandler handler) {
        this.handler = handler;
        
        this.setTitle("DigiQuilt");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Image frameIcon = new ImageIcon(
                getClass().getResource(
                "/umm/digiquilt/view/images/client-icon.png"))
                    .getImage();
        this.setIconImage(frameIcon);

        //this.getContentPane().setBackground(new Color(253, 205, 103));
        this.setContentPane(new ImageBackground(
                "/umm/digiquilt/view/images/woodtile.png"));
        
        handGlassPane = new HandGlassPane();
        this.setGlassPane(handGlassPane); 

        
        createComponents();
        
        layoutComponents();
        
        
        // Remove window decorations, and maximize
        this.setUndecorated(true);
        //game.setResizable(false);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        if (this.getSize().equals(new Dimension(0,0))){
            // On Linux, setExtendedState doesn't work >:(
            //this.setUndecorated(false);
            this.setLocation(0,0);
            this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        }
        
        // Everything should now be set up! Let's try to sync up.
        try {
            handler.synchronize();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "An error occured trying to connect to the server.",
                    "I/O error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
    }
    
    
    private void createComponents(){
        patchWorks = new PatchWorks(handGlassPane, PATCHSIZE);
        
        
        happeningBar = new WhatsHappeningPanel(
                blockWorks, handler, challenges, 20);
        handler.addSyncListener(happeningBar);
        // Initialize it
        happeningBar.onSynchronize(handler);

        challengePanel = new ChallengePanel(challenges);
        // Subscribe the challenge panel so that it can refresh itself
        ChallengeFileObserver observer = new ChallengeFileObserver(challenges);
        handler.addSyncListener(observer);

        fabricPalette = new FabricPalette(blockWorks);

        shapesPalette = new ShapesPalette(patchWorks, PATCHSIZE);
        fabricPalette.addFabricListener(shapesPalette);
        
        basketPanel = new ImageBackground("/umm/digiquilt/view/images/Baskettile.png");
        basketPanel.setLayout(new BorderLayout());
        basketPanel.add(fabricPalette, BorderLayout.WEST);
        basketPanel.add(shapesPalette, BorderLayout.EAST);
        
        // Create the Block Work Area -- this is a BlockViewer and
        // a GridViewer, stacked on top of each other in a JLayeredPane
        blockWorkArea = new BlockWorkArea(blockWorks, patchWorks, PATCHSIZE);

        
        // Create a BlockSizePanel which can change the current block to a
        // 4x4, 3x3, or 2x2 block.
        gridSelect = new BlockSizePanel(blockWorks);

        
        // Create the grid selection panel, this will manipulate the GridViewer
        // created above when a button is clicked.
        toolbarPalette = new GridSelectionPanel(blockWorks);
        // Listens for block size changes to show different grids depending on whether 
        // we're working on a 2x2, 3x3, or 4x4 block
        blockWorks.addPropertyChangeListener("currentBlock", toolbarPalette);

        
        
        
        // Create the Patch Work Area. 
        patchWorkArea = new PatchWorkArea(patchWorks, blockWorks, PATCHSIZE);

        
        replaceAllColorUI = new ReplaceAllColorPanel(patchWorks, blockWorks);
        // The replace color panel needs to hear when new colors are 
        // selected
        fabricPalette.addFabricListener(replaceAllColorUI);
        
        loadPatchPanel = new LoadPatchPanel(patchWorks, PATCHSIZE, handler);
        
        trash = new TrashPanel(patchWorks);

        
        lockGlassPane = new LockingGlassPane();
        lockGlassPane.setVisible(false);
        lockGlassPane.unlockComponent(toolbarPalette);
        lockGlassPane.unlockComponent(blockWorkArea);
        lockGlassPane.unlockComponent(fabricPalette);
        lockGlassPane.unlockComponent(challengePanel);

        

        utilityPanel = makeUtilityPanel();
        
        loginPanel = makeLoginPanel();
        
        // Create the menus
        setUpMenu(handler);
        
    }
    
    private void layoutComponents(){
        GridBagConstraints constraints;
        this.setLayout(new GridBagLayout());
        
        // What's Happening bar
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        //constraints.weightx = 1;
        this.add(happeningBar, constraints);
        
        // Challenge panel
        constraints = new GridBagConstraints();
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        this.add(challengePanel, constraints);
        
        

        //Add the basket panel
        constraints = new GridBagConstraints();
        constraints.gridwidth = 2;
        constraints.gridheight = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        this.add(basketPanel, constraints);
        
        // Add the whole block work area
        constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.gridx = 2;
        constraints.gridy = 2;
        this.add(blockWorkArea, constraints);

        // Add the grid selector
        constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 3;
        constraints.gridy = 2;
        this.add(gridSelect, constraints);
        
        // Add the toolbar palette
        constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.gridx = 3;
        constraints.gridy = 3;
        this.add(toolbarPalette, constraints);
        
        

        // Add the utility panel that goes underneath the Block Work Area
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 2;
        constraints.gridy = 4;
        this.add(utilityPanel, constraints);

        // Now add patchWorkLayers
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.gridx = 2;
        constraints.gridy = 5;
        this.add(patchWorkArea, constraints);

        // Add the replace all color panel.
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 5;
        this.add(replaceAllColorUI, constraints);
        
     // Add the replace all color panel.
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 5;
        this.add(loadPatchPanel, constraints);

        // Put the trash can in the bottom right corner
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 3;
        constraints.gridy = 5;
        constraints.fill = GridBagConstraints.BOTH;
        this.add(trash, constraints);
        
        // Add the login panel next to the trash
        constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 6;
        this.add(loginPanel, constraints);
    }

    private JPanel makeLoginPanel(){
	JPanel loginPnl = new JPanel();
	loginPnl.setLayout(new FlowLayout());
        loginPnl.setOpaque(false);
        loginPnl.add(makeChangeUserButton());
        userLabel = new JLabel(handler.getStudentName());
        loginPnl.add(userLabel);
        
        
        return loginPnl;
    }
    
    private JPanel makeUtilityPanel(){
        
        JPanel utilityPanel = new JPanel();
        utilityPanel.setLayout(new FlowLayout());
        utilityPanel.setPreferredSize(new Dimension(400, 60));
        utilityPanel.add(makeUpdateButton(handler));
        utilityPanel.add(makeSaveButton(handler, challengePanel));
        utilityPanel.add(makeChallengeButton(fabricPalette, handler));
        utilityPanel.add(makeOpenButton(handler));
        utilityPanel.add(makeUndoButton());
        utilityPanel.add(makeRedoButton());
        utilityPanel.add(makeClearButton(handler));
        utilityPanel.add(makeSortButton());
        utilityPanel.setOpaque(false);

        return utilityPanel;
    }
    
    private JButton makeUpdateButton(final SaveHandler handler){
        JButton update = new JButton("Update");
        update.addActionListener(new ActionListener() {
            @Override
	    public void actionPerformed(ActionEvent ae) {
        	    try {
			handler.synchronize();
		    } catch (IOException e) {
			JOptionPane.showMessageDialog(utilityPanel, "Update Failed");
		    }
        	 }
        });
        return update;
    }
    
    private JButton makeSaveButton(SaveHandler handler, ChallengePanel challengePanel) {
        JButton save = new JButton("Save");
        save.addActionListener(new SaveBlockAction(
                blockWorks, challenges, handler));
        
        return save;
    }
    
    private JButton makeChallengeButton(FabricPalette palette, SaveHandler handler){
        JButton makeChallenge = new JButton("Save and Challenge Others");
        makeChallenge.addActionListener(
                new CreateChallengeAction(blockWorks, palette, handler));
        
        return makeChallenge;

    }
    
    private JButton makeOpenButton(SaveHandler handler){
        JButton load = new JButton("Open");
        load.addActionListener(
                new LoadBlockAction(blockWorks, handler, challenges));

        return load;
    }
    
    private JButton makeUndoButton(){
        JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e){
                blockWorks.performUndo();
            }
        });
        blockWorks.getUndoRedoStack().addUndoButton(undo);
        
        
        return undo;

    }
    
    private JButton makeRedoButton() {
        JButton redo = new JButton("Redo");
        redo.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e){
                blockWorks.performRedo();
            }
        });
        blockWorks.getUndoRedoStack().addRedoButton(redo);
        
        return redo;
    }
    
    private JButton makeClearButton(SaveHandler handler){
        JButton clear = new JButton("Clear");
        // Add a Clear action that will also perform an autosave to the server
        clear.addActionListener(new ClearAction(
                blockWorks, challenges, handler));
        
        return clear;
    }
    
    private JButton makeSortButton(){
        JButton sort = new JButton("Sort");
        MainFrameAnimationController controller = 
            new MainFrameAnimationController(
                    this, blockWorkArea, blockWorks,
                    lockGlassPane, handGlassPane, sort, PATCHSIZE);
        return sort;
    }
    
    private JButton makeAnimateButton(final SaveHandler handler){
        JButton animate = new JButton("Create animation");
        animate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                UserSlideshow show = new UserSlideshow(handler.getSaveDirectory());
                show.setVisible(true);
            }
            
        });
        
        return animate;

    }
    
    private JButton makeChangeUserButton(){
	JButton changeUser = new JButton("Change User");
        // Add a Clear action that will also perform an autosave to the server
        changeUser.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
        	ChangeUser change = new ChangeUser(handler, userLabel); //Pauses until change user has been dismissed
                change.setVisible(true);
            }
            
        });
        
        return changeUser;
    }
    
    private void setUpMenu(SaveHandler handler){
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu dqMenu = new JMenu("DigiQuilt");
        JMenuItem about = new JMenuItem(new AbstractAction("About...") {
            
            @Override
	    public void actionPerformed(ActionEvent e) {
               JFrame aboutBox = createAboutBox();
               aboutBox.setVisible(true);
            }
        });
        JMenuItem quit = new JMenuItem(new AbstractAction("Quit DigiQuilt") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });
        dqMenu.add(about);
        dqMenu.add(quit);
        
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenu = new JMenuItem(
                new LoadBlockAction(blockWorks, handler, challenges));
        JMenuItem saveMenu = new JMenuItem(
                new SaveBlockAction(blockWorks, challenges, handler));
        fileMenu.add(openMenu);
        fileMenu.add(saveMenu);
        
        menuBar.add(dqMenu);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
        

    }
    /**
     * The about box for the digiquilt project
     */
    private JFrame createAboutBox(){
        JFrame aboutFrame = new JFrame("About Digiquilt");
        JButton closeButton = new JButton("Close");
        JLabel creatorLabel = new JLabel("Digiquilt was created by Kristin Lamberty");
        JLabel jmdnsLabel = new JLabel("<html><ul><li>This software uses parts of the JmDNS libraries</li>" +
        		"<li> you can find them at " +
        		"<a href='http://sourceforge.net/projects/jmdns/'>http://sourceforge.net/projects/jmdns/</a></li></ul>");
        aboutFrame.getContentPane().add(creatorLabel, BorderLayout.NORTH);
        aboutFrame.getContentPane().add(jmdnsLabel, BorderLayout.EAST);
        aboutFrame.getContentPane().add(closeButton);
        //closeButton.setSize(75, 50);
        aboutFrame.pack();
        return aboutFrame;
    }

    /**
     * A panel which paints a tiled image.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-28 21:59:12 $
     * @version $Revision: 1.2 $
     *
     */
    private class ImageBackground extends JPanel {
        
        /**
         * The image to be tiled.
         */
        final ImageIcon image;
        
        /**
         * Create a new ImageBackground panel using the given
         * image.
         * 
         * @param imagePath
         */
        public ImageBackground(String imagePath) {
            image = new ImageIcon(getClass().getResource(imagePath));
        }
        
        @Override
        public void paintComponent(Graphics graphic){
            Graphics2D g2 = (Graphics2D) graphic;
            int x = 0;
            while (x<this.getSize().width){
                int y = 0;
                while (y<this.getSize().height){
                    g2.drawImage(image.getImage(), x, y, this);
                    y+= image.getIconHeight();
                }
                x += image.getIconWidth();
            }
        }
    }


}
