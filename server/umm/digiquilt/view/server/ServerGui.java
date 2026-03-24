package umm.digiquilt.view.server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.xml.sax.SAXException;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.server.QuiltServer;
import umm.digiquilt.view.ld.ChallengeWindow;

/**
 * A GUI to create, edit, and launch a DigiQuiltServer.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-28 21:59:12 $
 * @version $Revision: 1.2 $
 *
 */
@SuppressWarnings("serial")
public class ServerGui extends JFrame {

    /**
     * The currently opened directory.
     */
    private File currentDirectory;

    /**
     * The thread the server is/will be running on.
     */
    private Thread serverThread;

    /**
     * The "Create New Class" button.
     */
    private JButton newClass = new JButton("Create New Class");

    /**
     * The "Open class folder" button.
     */
    private JButton openClass = new JButton("Open Class");

    /**
     * Displays the path of the currently open directory.
     */
    private JTextField dirDisplay = new JTextField(30);

    /**
     * The "Edit Challenges" button.
     */
    private JButton editChallenges = new JButton("Edit challenges");

    /**
     * The "Edit names" button.
     */
    private JButton editNames = new JButton("Edit names");

    /**
     * The "Start server" button.
     */
    private JButton startServer = new JButton("Start server");

    /**
     * The "Stop server" button.
     */
    private JButton stopServer = new JButton("Stop server");

    /**
     * The "Show challenge window" button.
     */
    private JButton challengeWindow = new JButton("Show challenge window");

    /**
     * The file chooser to use to prompt the user.
     */
    private JFileChooser fc;

    public static final String SERVER_DIRECTORY = System.getProperty("user.dir") + "/serverfolders/";

    /**
     * Create a new ServerGui window.
     */
    public ServerGui(){

	newClass.addActionListener(new CreateClassFolder());

	openClass.addActionListener(new OpenClassFolder());

	dirDisplay.setEditable(false);

	editChallenges.setEnabled(false);
	//editChallenges.addActionListener(new EditChallengeFile());

	editNames.setEnabled(false);
	editNames.addActionListener(new EditNameFile());

	startServer.setEnabled(false);
	startServer.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		startServer();
	    }

	});

	stopServer.setEnabled(false);
	stopServer.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		stopServer();
	    }

	});

	challengeWindow.setEnabled(false);
	challengeWindow.addActionListener(new LaunchChallengeWindow());

	this.setTitle("DigiQuilt Server");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setSize(new Dimension(350,175));

	this.setLayout(new FlowLayout());
	this.add(newClass);
	this.add(openClass);
	this.add(dirDisplay);
	this.add(editChallenges);
	this.add(editNames);
	this.add(startServer);
	this.add(stopServer);
	this.add(challengeWindow);

	File serverDirectory = new File(SERVER_DIRECTORY);
	if(!serverDirectory.exists()){
	    serverDirectory.mkdir();
	}
	FileSystemView systemView = new SingleRootFileSystemView(serverDirectory);
	fc = new JFileChooser(systemView);
    }

    /**
     * Set a new directory as the current one.
     * @param directory
     */
    private void selectDirectory(File directory){
	currentDirectory = directory;
	dirDisplay.setText(currentDirectory.getName());
	//editChallenges.setEnabled(true);
	editNames.setEnabled(true);
	startServer.setEnabled(true);
	challengeWindow.setEnabled(true);
    }

    /**
     * Create and start a new server thread, and enables or disables buttons
     * as appropriate. 
     */
    private void startServer(){

	QuiltServer server = new QuiltServer(currentDirectory);
	serverThread = new Thread(server);
	serverThread.setDaemon(true);

	serverThread.start();

	newClass.setEnabled(false);
	openClass.setEnabled(false);
	dirDisplay.setEnabled(false);
	startServer.setEnabled(false);
	stopServer.setEnabled(true);
    }

    /**
     * Interrupts the server thread and enables/disables the buttons as
     * appropriate.
     */
    private void stopServer(){

	serverThread.interrupt();

	newClass.setEnabled(true);
	openClass.setEnabled(true);
	dirDisplay.setEnabled(true);
	startServer.setEnabled(true);

	stopServer.setEnabled(false);
    }

    /**
     * Action which prompts for a name, and then creates a new
     * class folder there.
     * 
     * @author Scott Steffes, last changed by $Author: steffessw $
     * on $Date: 2012-04-21 21:59:12 $
     * @version $Revision: 1.2 $
     *
     */
    private class CreateClassFolder implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
	    //            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //            int selected = fc.showDialog(ServerGui.this, "Create Server Directory");
	    //            if (selected == JFileChooser.APPROVE_OPTION){
	    //                File chosen = fc.getSelectedFile();

	    File serverDirectory = new File(SERVER_DIRECTORY);
	    if(!serverDirectory.exists()){
		serverDirectory.mkdir();
	    }

	    String newServerName = JOptionPane.showInputDialog(ServerGui.this, "Enter New Class Name");

	    if(newServerName != null){
		File chosen = new File(serverDirectory, newServerName);

		if (chosen.exists()){
		    JOptionPane.showMessageDialog(ServerGui.this, 
			    "The specified directory already exists!", 
			    "Directory already exists", 
			    JOptionPane.ERROR_MESSAGE);
		}
		else{
		    chosen.mkdir();
		    try {
			File challenges = new File(chosen, SaveHandler.CHALLENGE_FILE_LOCATION);
			challenges.createNewFile();

			FileWriter fstream = new FileWriter(challenges);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(SaveHandler.DEFAULT_CHALLENGE_FILE_TEXT);
			out.close();

			File names = new File(chosen, SaveHandler.NAME_FILE_LOCATION);
			names.createNewFile();
			selectDirectory(chosen);
		    } catch (IOException ie){
			JOptionPane.showMessageDialog(ServerGui.this,
				"Error creating files in this directory",
				"File creation error",
				JOptionPane.ERROR_MESSAGE);
			ie.printStackTrace();
		    }
		}
	    }
	}
    }


    /**
     * Action which prompts the user for a directory and runs 
     * selectDirectory() on it. 
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-28 21:59:12 $
     * @version $Revision: 1.2 $
     *
     */
    private class OpenClassFolder implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {

	    File dir = new File(SERVER_DIRECTORY);
	    String[] serverList = dir.list(new QuiltDirectoryFilenameFilter());
	    String chosenFolder = (String) JOptionPane.showInputDialog(ServerGui.this, 
		    "Select a Server", "Select", NORMAL, null, serverList, serverList[0]);

	    if(chosenFolder != null){
		File chosen = new File(dir, chosenFolder);
		if(chosen.exists()){
		    selectDirectory(chosen);
		}
	    }

	    //	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //	    int selected = fc.showOpenDialog(ServerGui.this);
	    //	    if (selected == JFileChooser.APPROVE_OPTION){
	    //		File chosen = fc.getSelectedFile();
	    //		selectDirectory(chosen);
	    //	    }
	}

    }

    /**
     * Action which prompts the user to edit the name text file.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-28 21:59:12 $
     * @version $Revision: 1.2 $
     *
     */
    private class EditNameFile implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
	    File nameFile = new File(
		    currentDirectory, SaveHandler.NAME_FILE_LOCATION);
	    try {
		@SuppressWarnings("unused")
		NameFileEditor editor = 
		    new NameFileEditor(ServerGui.this, nameFile);
	    } catch (IOException e1) {
		JOptionPane.showMessageDialog(
			ServerGui.this, 
			"Error opening names.txt", 
			"Error", 
			JOptionPane.ERROR_MESSAGE);
	    }

	}

    }

    //    /**
    //     * Action which prompts the user to edit the name text file.
    //     * 
    //     * @author Jason Biatek, last changed by $Author: biatekjt $
    //     * on $Date: 2009-07-28 21:59:12 $
    //     * @version $Revision: 1.2 $
    //     *
    //     */
    //    private class EditChallengeFile implements ActionListener{
    //
    //        public void actionPerformed(ActionEvent e) {
    //            File challengeFile = new File(
    //                    currentDirectory, SaveHandler.CHALLENGE_FILE_LOCATION);
    //            try {
    //                @SuppressWarnings("unused")
    //                ChallengeFileEditor editor = 
    //                    new ChallengeFileEditor(ServerGui.this, challengeFile);
    //            } catch (IOException e1) {
    //                JOptionPane.showMessageDialog(
    //                        ServerGui.this, 
    //                        "Error opening challenges.txt", 
    //                        "Error", 
    //                        JOptionPane.ERROR_MESSAGE);
    //            }
    //
    //        }
    //
    //    }

    /**
     * Action which launches challenge window.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-28 21:59:12 $
     * @version $Revision: 1.2 $
     *
     */
    private class LaunchChallengeWindow implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
	    ChallengeWindow cw;
	    try {
		cw = new ChallengeWindow(currentDirectory);
		cw.setVisible(true);
	    } catch (FileNotFoundException e1) {
		JOptionPane.showMessageDialog(null, 
			"Challenge file not found", 
			"Error", 
			JOptionPane.ERROR_MESSAGE);
	    } catch (SAXException e1) {
		JOptionPane.showMessageDialog(null, 
			"Challenge file could not be read - XML Error", 
			"Error", 
			JOptionPane.ERROR_MESSAGE);
	    } catch (IOException e1) {
		JOptionPane.showMessageDialog(null, 
			"Error reading challenge file: " + e1.getMessage(), 
			"Error", 
			JOptionPane.ERROR_MESSAGE);
	    }
	}

    }

}
