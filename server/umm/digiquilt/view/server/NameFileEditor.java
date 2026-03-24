package umm.digiquilt.view.server;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Allows the user to easily edit the name file.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-04 01:49:19 $
 * @version $Revision: 1.1 $
 *
 */
@SuppressWarnings("serial")
public class NameFileEditor extends JDialog{

    /**
     * The file being edited
     */
    private final File nameFile;  

    /**
     * The area where the user can edit the file.
     */
    JList textListView;

    /**
     * Model for the list of lines.
     */
    DefaultListModel textList = new DefaultListModel();

    /**
     * The "Add" button to add a line
     */
    JButton add;
    
    /**
     * The "Edit" button to edit a line
     */
    JButton edit;
    /**
     * The "Remove" button to delete a line
     */
    JButton remove;
    /**
     * The "Move Up" button to move a line up
     */
    JButton moveUp;
    /**
     * The "Move Down" button to move a line down
     */
    JButton moveDown;
    /**
     * The "Sort Alphabetically" to sort all lines.
     */
    JButton sort;


    /**
     * Create and show a new NameFileEditor with the given Frame as a 
     * parent.
     * 
     * @param owner
     * @param nameFile
     * @throws IOException
     */
    public NameFileEditor(Frame owner, File nameFile) throws IOException{
        super(owner, "Editing "+nameFile.getName(), false);
        this.nameFile = nameFile;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        BufferedReader reader = new BufferedReader(
                new FileReader(nameFile));

        String line;
        while ((line = reader.readLine()) != null) {
            textList.addElement(line);
        }

        textListView = new JList(textList);
        textListView.addListSelectionListener(new SelectionButtonChanger());
        textListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 6;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(new JScrollPane(textListView), c);


        add = new JButton("Add");
        add.addActionListener(new AddLineAction());
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.insets = new Insets(2,2,2,2);
        this.add(add, c);

        edit = new JButton("Edit");
        edit.addActionListener(new EditLineAction());
        edit.setEnabled(false);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(2,2,2,2);
        this.add(edit, c);

        remove = new JButton("Remove");
        remove.addActionListener(new RemoveLineAction());
        remove.setEnabled(false);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        c.insets = new Insets(2,2,2,2);
        this.add(remove, c);

        moveUp = new JButton("Move up");
        moveUp.addActionListener(new MoveUpAction());
        moveUp.setEnabled(false);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 4;
        c.insets = new Insets(2,2,2,2);
        this.add(moveUp, c);

        moveDown = new JButton("Move down");
        moveDown.addActionListener(new MoveDownAction());
        moveDown.setEnabled(false);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 5;
        c.insets = new Insets(2,2,2,2);
        this.add(moveDown, c);

        sort = new JButton("Sort alphabetically");
        sort.addActionListener(new SortAction());
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 6;
        c.insets = new Insets(2,2,2,2);
        c.anchor = GridBagConstraints.NORTH;
        this.add(sort, c);

        JButton save = new JButton("Save");
        save.addActionListener(new SaveAction());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(save, c);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                NameFileEditor.this.dispose();
            }

        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 7;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        this.add(cancel, c);

        this.pack();
        this.setVisible(true);
    }

    /**
     * Action listener which turns on and off certain buttons based on
     * whether or not something is selected in the view. (It doesn't make
     * sense to be able to edit, etc. if nothing is selected).
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class SelectionButtonChanger implements ListSelectionListener{

        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            boolean isSomethingSelected = textListView.getSelectedIndex() != -1;

            // These buttons should only be on if something is selected.
            edit.setEnabled(isSomethingSelected);
            remove.setEnabled(isSomethingSelected);
            moveUp.setEnabled(isSomethingSelected);
            moveDown.setEnabled(isSomethingSelected);
        }

    }

    /**
     * Action to add a new line
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class AddLineAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(
                    NameFileEditor.this, 
                    "Text to be added as a new line:", 
                    "Add new line", 
                    JOptionPane.QUESTION_MESSAGE);
            if (input != null){
                int selected = textListView.getSelectedIndex();
                // If something is selected but not last,
                // add the new line right afterwards
                if (selected >= 0 && selected < textList.getSize()-1){
                    textList.add(selected+1, input);
                    textListView.setSelectedIndex(selected+1);
                } else {
                    // Otherwise just stick it at the end
                    textList.addElement(input);
                    textListView.setSelectedIndex(textList.size()-1);
                }
            }
        }

    }

    /**
     * Action to edit a line.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class EditLineAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            int selected = textListView.getSelectedIndex();
            String oldLine = (String) textList.get(selected);
            String edited = JOptionPane.showInputDialog(
                    NameFileEditor.this,
                    "Change this line to:",
                    oldLine);
            if (edited != null){
                textList.set(selected, edited);
            }
        }

    }

    /**
     * Action to delete a line.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class RemoveLineAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            int selected = textListView.getSelectedIndex();
            textList.remove(selected);
            textListView.setSelectedIndex(selected);
        }

    }

    /**
     * Action to move a line up in the list.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class MoveUpAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            int originalIndex = textListView.getSelectedIndex();
            if (originalIndex > 0){
                Object item = textList.get(originalIndex);
                textList.remove(originalIndex);
                textList.add(originalIndex-1, item);
                textListView.setSelectedIndex(originalIndex-1);
            }
        }

    }

    /**
     * Action to move a line down in the list.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class MoveDownAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            int originalIndex = textListView.getSelectedIndex();
            if (originalIndex < textList.size()-1){
                Object item = textList.get(originalIndex);
                textList.remove(originalIndex);
                textList.add(originalIndex+1, item);
                textListView.setSelectedIndex(originalIndex+1);
            }
        }

    }

    /**
     * Action to sort the items in the list.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class SortAction implements ActionListener{

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object[] lines = textList.toArray();
            Arrays.sort(lines);
            textList.removeAllElements();
            for (Object line : lines){
                textList.addElement(line);
            }
        }

    }

    /**
     * Write the contents of the editing area to file, after
     * checking it for blank lines.
     * 
     * @author Jason Biatek, last changed by $Author: biatekjt $
     * on $Date: 2009-07-04 01:49:19 $
     * @version $Revision: 1.1 $
     *
     */
    private class SaveAction implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            try {
                FileOutputStream out = new FileOutputStream(nameFile);
                PrintStream output = new PrintStream(out);
                for (int i=0; i<textList.size(); i++){
                    String line = (String) textList.get(i);
                    output.println(line);
                }
                output.close();
                NameFileEditor.this.dispose();
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(
                        NameFileEditor.this, 
                        "Error writing to name file", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }

        }

    }
}
