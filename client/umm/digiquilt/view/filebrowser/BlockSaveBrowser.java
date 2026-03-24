package umm.digiquilt.view.filebrowser;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import umm.digiquilt.model.Challenge;
import umm.digiquilt.model.FreeformChallenge;
import umm.digiquilt.model.Grid;
import umm.digiquilt.model.works.BlockWorks;
import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.GridViewPanel;
import umm.digiquilt.xmlsaveload.SaveBlockXML;
/**
 * The save dialog box. It extends the regular load dialog box by
 * changing a few things around.
 *
 */
@SuppressWarnings("serial")
public class BlockSaveBrowser extends JDialog{

    /**
     * Indicates that the user clicked the "Save" dialog.
     */
    public static final int SAVED = 1;

    /**
     * Indicates that the user cancelled, or that the dialog hasn't been
     * shown yet.
     */
    public static final int CANCELLED = 0;
    
    /**
     * Access to the current block, and its undo and redo stack.
     */
    BlockWorks blockWorks;

    /**
     * The grid to be saved.
     */
    Grid grid;

    /**
     * This handles the actual act of saving the file. It knows if it should
     * save to a server, or just locally, and does all the checking for
     * file name conflicts and such for us.
     */
    SaveHandler handler;

    /**
     * A note panel, to allow the user to input notes.
     */
    NotePanel notes;

    /**
     * Text field to take in the name of the quilt, which will be used
     * to decide on the file name.
     */
    JTextField fileNameBox;

    /**
     * A layered pane, holding a view of the block with the grid on top of it.
     * Provides a preview for the user, and is used to take the "snapshot"
     * which will be used as a thumbnail when the file is loaded back.
     */
    JLayeredPane blockLayeredPane;

    /**
     * The challenge that the user was working on at the
     * time of the save.
     */
    Challenge currentChallenge;
    
    /**
     * Indicates which button the user clicked to get rid of the
     * dialog
     */
    private int exitStatus = CANCELLED;
    
    /**
     * Prompts for the drop down menu. The HTML is so that they're
     * big and noticable. promptForNotes() removes this HTML when it returns
     * their notes.
     */
    private static final String[] PROMPTS = new String[] {
            "<html><h2>I like my design because: </h2></html>",
            "<html><h2>I was wondering: </h2></html>",
            "<html><h2>I didn't understand: </h2></html>",
            "<html><h2>I struggled with: </h2></html>"
    };
    
    /**
     * The drop down menu of prompts
     */
    private JComboBox promptList;

    /**
     * Constructor for a SaveBrowser
     * @param blockWorks The current Block to be saved
     * @param currentChallenge The currently displayed challenge
     * @param handler The SaveHandler that should be used to do the actual saving.
     */
    public BlockSaveBrowser(BlockWorks blockWorks, Challenge currentChallenge, SaveHandler handler) {
        this.blockWorks = blockWorks;
        this.grid = blockWorks.getGrid();
        this.handler = handler;
        if (currentChallenge != null){
        this.currentChallenge = currentChallenge;
        } else {
            this.currentChallenge = new FreeformChallenge("(no challenge)");
        }
        
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(680, 720));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setModal(true);

        GridBagConstraints constraints = new GridBagConstraints();

        BlockViewer blockView = new BlockViewer(blockWorks.getCurrentBlockClone());
        blockView.setName("Block");
        int sideSize = blockWorks.getSideSize();
        GridViewPanel gridView = new GridViewPanel(sideSize, 100);
        gridView.setName("Grid");
        blockLayeredPane = new JLayeredPane();
        blockLayeredPane.add(blockView, Integer.valueOf(0));
        blockLayeredPane.add(gridView, Integer.valueOf(1));
        GridBagLayout layerLayout = new GridBagLayout();
        blockLayeredPane.setLayout(layerLayout);
        constraints.gridx = 0;
        constraints.gridy = 0;
        layerLayout.setConstraints(blockView, constraints);
        layerLayout.setConstraints(gridView, constraints);
        
        
        gridView.setGrid(grid);
        constraints = new GridBagConstraints();
        constraints.gridheight=1;
        constraints.gridwidth = 2;
        constraints.gridx=0;
        constraints.gridy=0;
        this.add(blockLayeredPane, constraints);

        JPanel fileNamePanel = new JPanel();
        fileNameBox = new JTextField(30);
        fileNameBox.setName("Quilt name");
        fileNamePanel.add(new JLabel("Quilt Name:"));
        fileNamePanel.add(fileNameBox);
        constraints.gridx=0;
        constraints.gridy=1;
        this.add(fileNamePanel, constraints);

        JTextArea challenge = new JTextArea(this.currentChallenge.toString());
        challenge.setName("Challenge");
        challenge.setEditable(false);
        challenge.setLineWrap(true);
        challenge.setWrapStyleWord(true);
        challenge.setPreferredSize(new Dimension(600, 30));
        challenge.setOpaque(false);
        constraints.gridx=0;
        constraints.gridy=2;
        this.add(challenge, constraints);
        
        constraints.gridx=0;
        constraints.gridy=3;
        promptList = new JComboBox(PROMPTS);
        promptList.setName("Prompts");
        this.add(promptList,constraints);
        
        notes = new NotePanel();
        constraints.gridx=0;
        constraints.gridy=4;
        this.add(notes, constraints);

        constraints.gridheight=1;
        constraints.gridwidth =1;
        constraints.gridx=0;
        constraints.gridy=5;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        JButton saveButton = new JButton("Save");
        saveButton.setName("Save");
        saveButton.addActionListener(new SaveListener(this));
        this.add(saveButton, constraints);

        constraints.gridx = 1;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
	    public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        this.add(cancelButton, constraints);

    }

    /**
     * @return the status of this browser when it was closed. If the browser
     * hasn't actually been opened, it will return CANCELLED.
     * 
     * @see BlockSaveBrowser.SAVED
     * @see BlockSaveBrowser.CANCELLED
     */
    public int getExitStatus(){
        return exitStatus;
    }
    

    /**
     * An ActionListener to save the quilt when it is fired. Checks that
     * the file name doesn't already exist, and that the file name isn't
     * blank.
     */
    private class SaveListener implements ActionListener{

        /**
         * Pointer back to the parent for popup dialogs.
         */
        private BlockSaveBrowser browser;

        /**Construct a SaveListener with the given browser as the parent.
         * 
         * @param browser
         */
        public SaveListener(BlockSaveBrowser browser){
            this.browser = browser;
        }

        @Override
	public void actionPerformed(ActionEvent e) {
            String filename = fileNameBox.getText();
            if (filename.length() == 0){
                JOptionPane.showMessageDialog(browser,
                        "Please type a name for this quilt",
                        "Name your quilt",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Okay, they gave a name.
            try {
                // Get the prompt they chose, and take out the HTML.
                String prompt = (String) promptList.getSelectedItem();
                prompt = prompt.replaceAll("(<[/]?html>|<[/]?h\\d>)", "");
                
                SaveBlockXML saver = new SaveBlockXML(
                        blockWorks.getCurrentBlockClone(),
                        grid,
                        blockWorks.getUndoRedoStack(),
                        handler.getStudentName(),
                        filename,
                        System.currentTimeMillis(),
                        prompt+notes.getNotes(),
                        currentChallenge
                );
                BufferedImage icon = makeImageOfComponent(blockLayeredPane);
                boolean result = handler.saveBlock(saver, icon, filename);
                if (result == false){
                    // The save failed due to a name conflict
                    // TODO: Do a better job here, maybe offer an alternative?
                    JOptionPane.showMessageDialog(browser,
                            "A quilt with this name already exists. Please pick a different name.",
                            "Choose a new quilt name",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } 
                // If all went well, then we close the browser.
                exitStatus = SAVED;
                dispose();
            } catch (IOException e1) {
                // Something bad happened =(
                JOptionPane.showMessageDialog(browser,
                        "An error occured trying to save this file: "
                          + e1.getMessage(),
                        "File I/O Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (ParserConfigurationException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        }

    }

    /**
     * Takes in a regular component and returns an image of that component.
     * @param myComponent
     * @return an image of the specified component.
     */
    public static BufferedImage makeImageOfComponent(Component myComponent) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage = 
            new BufferedImage(size.width, size.height,
                    BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        myComponent.paint(g2);
        return myImage;
    }  
}
