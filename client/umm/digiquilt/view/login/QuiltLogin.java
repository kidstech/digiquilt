package umm.digiquilt.view.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.server.QuiltZeroconf;

/**
 * Prompts the user for a class and name. Set it to visible, and since it is 
 * modal it will pause everything until it has gotten info from the user, at
 * which point it can be retrieved. 
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-29 20:50:26 $
 * @version $Revision: 1.4 $
 *
 */
@SuppressWarnings("serial")
public class QuiltLogin extends JDialog implements ServiceListener {

    /**
     * Network access
     */
    private QuiltZeroconf qz;

    /**
     * The directory for classes to be stored in
     */
    private File quiltDirectory;

    /**
     * List of all the classes that are on disk. Even if they disappear from
     * JmDNS, they should still be available for log in.
     */
    private List<String> offlineClasses = new ArrayList<String>();

    /**
     * The combo box containing the list of classes
     */
    private SortedComboBoxModel classBoxModel = new SortedComboBoxModel();

    /**
     * Combo box for names
     */
    private JComboBox nameComboBox;

    /**
     * Log in button
     */
    private JButton loginButton;
    
    /**
     * Flag to whether the Log in button was clicked or not
     */
    private boolean userPressedButton = false;

    /**
     * Create a new QuiltLogin object. 
     * 
     * @param qz
     * @param quiltDirectory
     */
    public QuiltLogin(QuiltZeroconf qz, File quiltDirectory){
        this.qz = qz;
        this.quiltDirectory = quiltDirectory;


        // Set up the GUI:
        this.setTitle("DigiQuilt Login");

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);


        JLabel message = 
            new JLabel("Please choose your class and name to log in.");
        message.setName("message");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.add(message, c);

        JComboBox classComboBox = new JComboBox(classBoxModel);
        classComboBox.setName("classes");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.add(classComboBox, c);

        nameComboBox = new JComboBox();
        nameComboBox.setName("names");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 1;
        this.add(nameComboBox, c);


        loginButton = new JButton("Log in");
        loginButton.setName("login");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.EAST;
        this.add(loginButton, c);
        
        JButton quitButton = new JButton("Quit");
        quitButton.setName("quit");
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.EAST;
        this.add(quitButton, c);

        // Done adding items, now initializing/adding listeners:

        // Add all the classes on disk
        if (quiltDirectory.exists()){
            for (File file : quiltDirectory.listFiles()){
                if (file.isDirectory()){
                    offlineClasses.add(file.getName());
                    classBoxModel.addElement(file.getName());
                }
            }
        }
        // Add the "Offline" class if it isn't there already
        if (!offlineClasses.contains("Offline")){
            offlineClasses.add("Offline");
            classBoxModel.addElement("Offline");
        }

        // Disable certain elements
        nameComboBox.setEnabled(false);
        loginButton.setEnabled(false);
        // The name box should be editable
        nameComboBox.setEditable(true);

        // Set up listeners to conditionally enable them

        // The name box should be turned on when a class is selected. 
        classComboBox.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    enableNameBox();
                } else {
                    disableNameBox();
                }
            }

        });
        // The "Log in" button should be turned on when there's 
        // text in the name box 
        nameComboBox.getEditor().getEditorComponent()
            .addKeyListener(new KeyAdapter() {
                // This checks whenever a key is pressed
                @Override
                public void keyTyped(KeyEvent e) {
                    checkNameBox();
                }
            });
        nameComboBox.addActionListener(new ActionListener() {
            // This checks when an item is selected
            public void actionPerformed(ActionEvent e) {
                checkNameBox();
            }
        });

        // The login and quit buttons hide the panel
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                userPressedButton = true;
                QuiltLogin.this.setVisible(false);
            }
        });
        quitButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                QuiltLogin.this.setVisible(false);
            }
        
        });

        this.pack();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


    }

    /**
     * Populate and turn on the name box.
     */
    private void enableNameBox(){
        String serverName = (String) classBoxModel.getSelectedItem();
        if (serverName != null){
            // Try to get the list of names
            SaveHandler handler = 
                new SaveHandler(qz, quiltDirectory, serverName);
            List<String> names;
            try {
                names = handler.getNames();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            for (String name : names){
                nameComboBox.addItem(name);
            }
            nameComboBox.setEnabled(true);
        }
    }

    /**
     * Turn off the name box
     */
    private void disableNameBox(){
        nameComboBox.removeAllItems();
        nameComboBox.setEnabled(false);
    }

    /**
     * Check the selected name and set the login button accordingly
     * (no blanks allowed)
     */
    private void checkNameBox(){
        Object enteredName = nameComboBox.getEditor().getItem();
        if (enteredName!=null && !enteredName.equals("")){
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }


    /**
     * @return a SaveHandler configured according to the user's input, or
     * null if the dialog was closed without logging in.
     */
    public SaveHandler getSaveHandler() {
        if (!userPressedButton){
            return null;
        }
        String serverName = (String) classBoxModel.getSelectedItem();
        SaveHandler handler = 
            new SaveHandler(qz, quiltDirectory, serverName);
        String studentName = (String) nameComboBox.getSelectedItem();
        handler.setStudentName(studentName);
        return handler;
    }



    public void serviceAdded(ServiceEvent event) {
        String className = event.getName();
        // Make sure it isn't a duplicate first
        int index = classBoxModel.getIndexOf(className);
        if (index < 0){
            classBoxModel.addElement(className);
        }
    }

    public void serviceRemoved(ServiceEvent event) {
        String className = event.getName();
        if (offlineClasses.contains(className)){
            // This server has gone away, but we still have an offline
            // copy of it.
            return;
        }

        int index = classBoxModel.getIndexOf(className);
        if (index >= 0){
            classBoxModel.removeElementAt(index);
        }
    }

    public void serviceResolved(ServiceEvent event) {
        // Do nothing, we don't care about resolution
    }








    /**
     *  Custom model to make sure the items are stored in a sorted order.
     *  The default is to sort in the natural order of the item, but a
     *  Comparator can be used to customize the sort order.
     *
     *  The data is initially sorted before the model is created. Any updates
     *  to the model will cause the items to be inserted in sorted order.
     */
    private class SortedComboBoxModel extends DefaultComboBoxModel {


        @Override
        public void addElement(Object element) {
            insertElementAt(element, 0);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void insertElementAt(Object element, int index) {
            int size = getSize();

            int i;
            for (i=0; i<size; i++) {

                Comparable c = (Comparable) getElementAt( i );

                if (c.compareTo(element) > 0) {
                    break;
                }
            }

            super.insertElementAt(element, i);

        }
    }






}
