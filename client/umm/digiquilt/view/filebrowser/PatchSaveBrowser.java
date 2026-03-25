package umm.digiquilt.view.filebrowser;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.view.BlockViewer;
import umm.digiquilt.view.PatchViewer;
import umm.digiquilt.xmlsaveload.SavePatchXML;
/**
 * The save dialog box. It extends the regular load dialog box by
 * changing a few things around.
 *
 */
@SuppressWarnings("serial")
public class PatchSaveBrowser extends JDialog{

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
     * This handles the actual act of saving the file. It knows if it should
     * save to a server, or just locally, and does all the checking for
     * file name conflicts and such for us.
     */
    SaveHandler handler;


    /**
     * The PatchViewer the patch to save comes from.
     */
    private PatchViewer patchViewer;
    
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
     * Indicates which button the user clicked to get rid of the
     * dialog
     */
    private int exitStatus = CANCELLED;

    /**
     * Constructor for a SaveBrowser
     * @param blockWorks The current Block to be saved
     * @param currentChallenge The currently displayed challenge
     * @param handler The SaveHandler that should be used to do the actual saving.
     */
    public PatchSaveBrowser(PatchViewer patchViewer, SaveHandler handler) {
        this.patchViewer = patchViewer;
        this.handler = handler;
        
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(680, 720));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setModal(true);

        GridBagConstraints constraints = new GridBagConstraints();
        

        JPanel fileNamePanel = new JPanel();
        fileNameBox = new JTextField(30);
        fileNameBox.setName("Patch name");
        fileNamePanel.add(new JLabel("Patch Name:"));
        fileNamePanel.add(fileNameBox);
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.gridwidth=2;
        this.add(fileNamePanel, constraints);
        
        constraints.gridy=2;
        this.add(new PatchViewer(patchViewer.getPatch(), 450), constraints);
        
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
        private PatchSaveBrowser browser;

        /**Construct a SaveListener with the given browser as the parent.
         * 
         * @param browser
         */
        public SaveListener(PatchSaveBrowser browser){
            this.browser = browser;
        }

        @Override
	public void actionPerformed(ActionEvent e) {
            String filename = fileNameBox.getText();
            if (filename.length() == 0){
                JOptionPane.showMessageDialog(browser,
                        "Please type a name for this patch",
                        "Name your patch",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Okay, they gave a name.
            try {
                SavePatchXML saver = new SavePatchXML(patchViewer.getPatch(),
                				      handler.getStudentName(), 
                				      filename,
                				      System.currentTimeMillis());
                BufferedImage icon = BlockSaveBrowser.makeImageOfComponent(patchViewer);
                boolean result = handler.savePatch(saver, icon, filename);
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
                e2.printStackTrace();
            }
        }

    } 
}
