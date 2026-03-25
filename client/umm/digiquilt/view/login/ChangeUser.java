package umm.digiquilt.view.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import umm.digiquilt.savehandler.SaveHandler;

/**
 * Prompts the user for a class and name. Set it to visible, and since it is 
 * modal it will pause everything until it has gotten info from the user, at
 * which point it can be retrieved. 
 * 
 * @author Jason Biatek, last changed by $Author: Scott Steffes $
 * on $Date: 2012-03-02 20:50:26 $
 * @version $Revision: 1.5 $
 *
 */
@SuppressWarnings("serial")
public class ChangeUser extends JDialog{


    private SaveHandler handler; 

    /**
     * Combo box for names
     */
    private JComboBox nameComboBox;

    /**
     * Log in button
     */
    private JButton loginButton;
    
    private JLabel loggedInUserLabel;

    /**
     * Create a new QuiltLogin object. 
     * 
     * @param qz
     * @param quiltDirectory
     */
    public ChangeUser(SaveHandler handler, JLabel loggedInLabel){
	if(handler == null){
	    System.out.println("Handler is null up here");
	}
	this.handler = handler;
	this.loggedInUserLabel = loggedInLabel;

	// Set up the GUI:
	this.setTitle("Change User");

	this.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.insets = new Insets(5,5,5,5);


	JLabel message = 
	    new JLabel("Please choose your name to log in.");
	message.setName("message");
	c.gridx = 0;
	c.gridy = 0;
	c.gridwidth = 2;
	c.gridheight = 1;
	this.add(message, c);

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

	JButton quitButton = new JButton("Close");
	quitButton.setName("close");
	c.gridx = 1;
	c.gridy = 3;
	c.gridwidth = 1;
	c.gridheight = 1;
	c.anchor = GridBagConstraints.EAST;
	this.add(quitButton, c);

	// Done adding items, now initializing/adding listeners:


	// Disable certain elements
	nameComboBox.setEnabled(false);
	loginButton.setEnabled(false);
	// The name box should be editable
	nameComboBox.setEditable(true);
	this.enableNameBox();
	


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
	    @Override
	    public void actionPerformed(ActionEvent e) {
		checkNameBox();
	    }
	});

	// The login and quit buttons hide the panel
	loginButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		changeStudentName();
		ChangeUser.this.setVisible(false);
	    }
	});
	quitButton.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		ChangeUser.this.setVisible(false);
	    }

	});

	this.pack();
	this.setLocationRelativeTo(null);
	this.setModal(true);
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void changeStudentName(){
	handler.setStudentName(((String)nameComboBox.getEditor().getItem()));
	loggedInUserLabel.setText(handler.getStudentName());
    }

    /**
     * Populate and turn on the name box.
     */
    private void enableNameBox(){
	List<String> names;
	try {
	    if(handler == null){
		System.out.println("Handler is null");
		names = new ArrayList<String>();
	    }
	    else{
		names = handler.getNames();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return;
	}
	for (String name : names){
	    nameComboBox.addItem(name);
	}
	nameComboBox.setEnabled(true);
	
	if(!names.isEmpty()){
	    loginButton.setEnabled(true);
	}
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
}
